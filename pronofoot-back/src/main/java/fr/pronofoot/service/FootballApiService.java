package fr.pronofoot.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import fr.pronofoot.dto.ChampionnatDto;
import fr.pronofoot.dto.CompetitionApiResponse;
import fr.pronofoot.dto.CompetitionItem;
import fr.pronofoot.dto.MatchDto;
import fr.pronofoot.dto.SaisonDto;
import fr.pronofoot.dto.TeamItem;
import fr.pronofoot.dto.TeamsApiResponse;
import reactor.core.publisher.Mono;

@Service
public class FootballApiService {

    private final WebClient webClient;

    @Autowired
    public FootballApiService(WebClient footballApiClient) {
        this.webClient = footballApiClient;
    }

    public List<MatchDto> getMatches(String championnatCode, String saison) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/competitions/{code}/matches")
                        .queryParam("season", saison)
                        .build(championnatCode))
                .retrieve()
                .onStatus(
                        res -> res.isError(),
                        res -> res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("API error: " + res.statusCode() + " - " + body)))
                )
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    List<MatchDto> result = new ArrayList<>();
                    JsonNode matches = json.get("matches");
                    for (JsonNode m : matches) {
                        MatchDto dto = new MatchDto();
                        dto.setDate(m.get("utcDate").asText());
                        dto.setEquipeDomicile(m.get("homeTeam").get("name").asText());
                        dto.setEquipeExterieur(m.get("awayTeam").get("name").asText());
                        dto.setScoreDomicile(m.get("score").get("fullTime").get("home").isNull() ? null : m.get("score").get("fullTime").get("home").asInt());
                        dto.setScoreExterieur(m.get("score").get("fullTime").get("away").isNull() ? null : m.get("score").get("fullTime").get("away").asInt());
                        dto.setNumJournee(m.hasNonNull("matchday") ? m.get("matchday").asLong() : null);
                        dto.setChampionnatCode(championnatCode);
                        dto.setSaisonAnnee(saison);
                        result.add(dto);
                    }
                    return result;
                })
                .block();
    }



    public List<CompetitionItem> getCompetitions() {
        return webClient.get()
                .uri("/competitions")
                .retrieve()
                .bodyToMono(CompetitionApiResponse.class)
                .map(CompetitionApiResponse::getCompetitions)
                .block();
    }

    public ChampionnatDto getChampionnatDetails(String codeChampionnat) {
        JsonNode node = webClient.get()
                .uri("/competitions/{code}", codeChampionnat)
                .retrieve()
                .onStatus(
                        res -> res.isError(),
                        res -> res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("API error: " + res.statusCode() + " - " + body)))
                )
                .bodyToMono(JsonNode.class)
                .block();

        if (node == null) {
            throw new RuntimeException("Aucune donnée trouvée pour le championnat " + codeChampionnat);
        }

        ChampionnatDto dto = new ChampionnatDto();
        dto.setCode(node.path("code").asText());
        dto.setNom(node.path("name").asText());

        // Récupération de la saison courante
        JsonNode currentSeason = node.path("currentSeason");
        if (!currentSeason.isMissingNode()) {
            SaisonDto saison = new SaisonDto();
            saison.setId(currentSeason.get("id").asLong());
            saison.setYear(currentSeason.path("startDate").asText().substring(0, 4)); // ou currentSeason.path("year").asText() si présent
            saison.setStartDate(LocalDate.parse(currentSeason.path("startDate").asText()));
            saison.setEndDate(LocalDate.parse(currentSeason.path("endDate").asText()));
            dto.setCurrentSeason(saison);
        }

        // Récupération de toutes les saisons (optionnel)
        JsonNode seasonsNode = node.path("seasons");
        if (seasonsNode.isArray()) {
            List<SaisonDto> saisons = new ArrayList<>();
            for (JsonNode s : seasonsNode) {
                SaisonDto saison = new SaisonDto();
                saison.setId(s.get("id").asLong());
                saison.setYear(s.path("startDate").asText().substring(0, 4));
                saison.setStartDate(LocalDate.parse(s.path("startDate").asText()));
                saison.setEndDate(LocalDate.parse(s.path("endDate").asText()));
                saisons.add(saison);
            }
            dto.setSaisons(saisons);
        }

        return dto;
    }



    public ChampionnatDto getChampionnatAvecEquipes(String code) {
        return webClient.get()
                .uri("/competitions/{code}/teams", code)
                .retrieve()
                .onStatus(
                        response -> response.isError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        "Erreur API football-data : " + clientResponse.statusCode() + " - " + errorBody
                                )))
                )
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    ChampionnatDto dto = new ChampionnatDto();
                    dto.setCode(code);
                    dto.setNom(json.path("competition").path("name").asText());

                    Set<String> equipes = new HashSet<>();
                    json.path("teams").forEach(team -> {
                        equipes.add(team.path("name").asText());
                    });
                    dto.setEquipes(equipes);
                    return dto;
                })
                .block();
    }

    public Set<String> getEquipes(String codeChampionnat, String anneeSaison) {
        try {
            String uri = String.format("/competitions/%s/teams?season=%s", codeChampionnat, anneeSaison);

            TeamsApiResponse response = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(TeamsApiResponse.class)
                    .block();

            if (response == null || response.getTeams() == null) return Set.of();

            return response.getTeams().stream()
                    .map(TeamItem::getName)
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des équipes : " + e.getMessage());
            return Set.of();
        }
    }


}

