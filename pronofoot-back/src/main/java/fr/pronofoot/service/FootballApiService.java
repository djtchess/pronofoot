package fr.pronofoot.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(FootballApiService.class);

    private final WebClient webClient;

    @Autowired
    public FootballApiService(WebClient footballApiClient) {
        this.webClient = footballApiClient;
    }

    /* ================================================================
       1.  LISTE DES MATCHES (toute la saison)
    ================================================================ */
    public List<MatchDto> getMatches(String codeChampionnat, String saison) {
        String path = "/competitions/{code}/matches?season={season}";
        return fetchMatches(path, codeChampionnat, saison);
    }

    /* ================================================================
       2.  MATCHES D’UNE JOURNÉE
    ================================================================ */
    public List<MatchDto> getMatchesByJournee(String codeChampionnat,
                                              String saison,
                                              int journee) {

        String path = "/competitions/{code}/matches?season={season}&matchday={md}";
        return fetchMatches(path, codeChampionnat, saison, journee);
    }

    /* ================================================================
       3.  MATCH UNIQUE  (par identifiant API)
    ================================================================ */
    public Optional<MatchDto> getMatchById(long remoteId) {
        JsonNode m = webClient.get()
                .uri("/matches/{id}", remoteId)
                .retrieve()
                .onStatus(
                        res -> res.isError(),
                        res -> res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("API error: " + res.statusCode() + " - " + body)))
                )
                .bodyToMono(JsonNode.class)
                .block();

        return m != null ? Optional.of(parseMatchNode(m, null, null)) : Optional.empty();
    }

    /* ================================================================
       4.  COMPÉTITIONS / CHAMPIONNATS
    ================================================================ */
    public List<CompetitionItem> getCompetitions() {
        return webClient.get()
                .uri("/competitions")
                .retrieve()
                .bodyToMono(CompetitionApiResponse.class)
                .map(CompetitionApiResponse::getCompetitions)
                .block();
    }

    /* ---------------------------------------------------------------
       4.1  Détails + saisons + currentSeason
    --------------------------------------------------------------- */
    public ChampionnatDto getChampionnatDetails(String codeChampionnat) {
        JsonNode node = webClient.get()
                .uri("/competitions/{code}", codeChampionnat)
                .retrieve()
                .onStatus(
                        res -> res.isError(),
                        res -> res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("API error: " + res.statusCode() + " - " + body)))
                )
                .bodyToMono(JsonNode.class)
                .block();

        if (node == null) throw new RuntimeException("Aucune donnée pour " + codeChampionnat);

        ChampionnatDto dto = new ChampionnatDto();
        dto.setCode(codeChampionnat);
        dto.setNom(node.path("name").asText());

        /* currentSeason ------------------------------------------- */
        JsonNode current = node.path("currentSeason");
        if (!current.isMissingNode()) dto.setCurrentSeason(parseSeasonNode(current));

        /* seasons ------------------------------------------------- */
        List<SaisonDto> saisons = new ArrayList<>();
        node.path("seasons").forEach(s -> saisons.add(parseSeasonNode(s)));
        dto.setSaisons(saisons);

        return dto;
    }

    /* ---------------------------------------------------------------
       4.2  Équipes d’un championnat (option saison)
    --------------------------------------------------------------- */
    public Set<String> getEquipes(String codeChampionnat, String anneeSaison) {

        String uri = "/competitions/%s/teams".formatted(codeChampionnat);
        if (anneeSaison != null) uri += "?season=" + anneeSaison;

        TeamsApiResponse resp = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TeamsApiResponse.class)
                .block();

        return (resp == null || resp.getTeams() == null)
                ? Set.of()
                : resp.getTeams().stream()
                .map(TeamItem::getName)
                .collect(Collectors.toSet());
    }

    /* ================================================================
       5.  MÉTHODES PRIVÉES UTILITAIRES
    ================================================================ */
    /** Récupère une liste brute de matches et la mappe */
    private List<MatchDto> fetchMatches(String template, Object... uriVars) {

        JsonNode root = webClient.get()
                .uri(template, uriVars)
                .retrieve()
                .onStatus(
                        res -> res.isError(),
                        res -> res.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("API error: " + res.statusCode() + " - " + body)))
                )
                .bodyToMono(JsonNode.class)
                .block();

        if (root == null) return List.of();

        List<MatchDto> list = new ArrayList<>();
        root.path("matches").forEach(m ->
                list.add(parseMatchNode(m,
                        (String) uriVars[0],                // codeChampionnat
                        uriVars.length > 1 ? String.valueOf(uriVars[1]) : null)) // saison
        );
        return list;
    }

    /** Transforme un nœud JSON “match” en DTO métier */
    private MatchDto parseMatchNode(JsonNode m,
                                    String codeChampionnat,
                                    String saison) {

        MatchDto dto = new MatchDto();

        dto.setChampionnatCode(codeChampionnat);
        dto.setSaisonAnnee(saison);
        dto.setRemoteId(m.path("id").asLong());
        dto.setDate(m.path("utcDate").asText());
        dto.setEquipeDomicile(m.path("homeTeam").path("name").asText());
        dto.setEquipeExterieur(m.path("awayTeam").path("name").asText());

        JsonNode score = m.path("score").path("fullTime");
        dto.setScoreDomicile(score.path("home").isNull() ? null : score.path("home").asInt());
        dto.setScoreExterieur(score.path("away").isNull() ? null : score.path("away").asInt());

        dto.setNumJournee(m.hasNonNull("matchday") ? m.path("matchday").asLong() : null);

        return dto;
    }

    /** Season minimal mapping */
    private SaisonDto parseSeasonNode(JsonNode n) {
        SaisonDto s = new SaisonDto();
        s.setId(n.path("id").asLong());
        s.setYear(n.path("startDate").asText().substring(0, 4));
        s.setStartDate(LocalDate.parse(n.path("startDate").asText()));
        s.setEndDate(LocalDate.parse(n.path("endDate").asText()));
        return s;
    }
}
