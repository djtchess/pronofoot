package fr.pronofoot.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.pronofoot.dto.record.TeamStandingDto;
import fr.pronofoot.entity.Championnat;
import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Match;
import fr.pronofoot.entity.Participation;
import fr.pronofoot.entity.Saison;
import fr.pronofoot.repository.ChampionnatRepository;
import fr.pronofoot.repository.ChampionnatSaisonRepository;
import fr.pronofoot.repository.MatchRepository;
import fr.pronofoot.repository.ParticipationRepository;
import fr.pronofoot.repository.SaisonRepository;

@Service
public class ClassementService {

    @Autowired private ChampionnatRepository championnatRepository;
    @Autowired private SaisonRepository saisonRepository;
    @Autowired private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired private MatchRepository matchRepository;
    @Autowired private ParticipationRepository participationRepository;

    public List<TeamStandingDto> computeClassement(String code, Long seasonId) {
        return computeClassement(code, seasonId, m -> true);
    }

    public List<TeamStandingDto> computeClassementDomicile(String code, Long seasonId) {
        return computeClassement(code, seasonId, m -> true, Match::getEquipeDomicile);
    }

    public List<TeamStandingDto> computeClassementExterieur(String code, Long seasonId) {
        return computeClassement(code, seasonId, m -> true, Match::getEquipeExterieur);
    }

    public List<TeamStandingDto> computeClassementDernieresJournees(String code, Long seasonId, int nbJournees) {
        ChampionnatSaison cs = getChampionnatSaison(code, seasonId);

        List<Match> allMatches = matchRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .filter(m -> m.getScoreDomicile() != null && m.getScoreExterieur() != null)
                .toList();

        Set<Long> lastJournees = allMatches.stream()
                .map(Match::getNumJournee)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(nbJournees)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Match> filtered = allMatches.stream()
                .filter(m -> lastJournees.contains(m.getNumJournee()))
                .toList();

        return computeTable(cs.getId(), filtered); // cette méthode traite home + away
    }

    public List<TeamStandingDto> computeClassementDernieresJourneesDomicile(String code, Long seasonId, int nbJournees) {
        return computeClassementParEquipeDerniersMatchsDomicile(code, seasonId, nbJournees);
    }

    public List<TeamStandingDto> computeClassementDernieresJourneesExterieur(String code, Long seasonId, int nbJournees) {
        return computeClassementParEquipeDerniersMatchsExterieur(code, seasonId, nbJournees);
    }

    private List<TeamStandingDto> computeClassement(String code, Long seasonId, Predicate<Match> filter) {
        ChampionnatSaison cs = getChampionnatSaison(code, seasonId);

        List<Match> matches = matchRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .filter(m -> m.getScoreDomicile() != null && m.getScoreExterieur() != null)
                .filter(filter)
                .toList();

        return computeTable(cs.getId(), matches);
    }

    private List<TeamStandingDto> computeClassement(String code, Long seasonId, Predicate<Match> filter, Function<Match, Equipe> teamExtractor) {
        ChampionnatSaison cs = getChampionnatSaison(code, seasonId);

        List<Match> matches = matchRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .filter(m -> m.getScoreDomicile() != null && m.getScoreExterieur() != null)
                .filter(filter)
                .toList();

        return computeTable(cs.getId(), matches, teamExtractor);
    }


    public List<TeamStandingDto> computeClassementParEquipeDerniersMatchsDomicile(String code, Long seasonId, int nbMatchs) {
        ChampionnatSaison cs = getChampionnatSaison(code, seasonId);

        List<Match> allMatches = matchRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .filter(m -> m.getScoreDomicile() != null && m.getScoreExterieur() != null)
                .sorted(Comparator.comparing(Match::getNumJournee).reversed()) // trier pour prendre les derniers
                .toList();

        Map<Equipe, TeamStats> statsMap = new HashMap<>();

        // Récupère toutes les équipes du championnat
        List<Equipe> equipes = participationRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .map(Participation::getEquipe)
                .toList();

        for (Equipe equipe : equipes) {
            List<Match> derniersMatchs = allMatches.stream()
                    .filter(m -> m.getEquipeDomicile().equals(equipe))
                    .limit(nbMatchs)
                    .toList();

            TeamStats stats = new TeamStats();
            for (Match match : derniersMatchs) {
                stats.updateHome(match);
            }
            statsMap.put(equipe, stats);
        }

        return statsMap.entrySet().stream()
                .map(e -> e.getValue().toDto(e.getKey()))
                .sorted(Comparator
                        .comparingInt((TeamStandingDto s) -> -s.points())
                        .thenComparingInt(s -> -s.goalDifference())
                        .thenComparingInt(s -> -s.butsPour())
                        .thenComparing(TeamStandingDto::equipe))
                .toList();
    }



    public List<TeamStandingDto> computeClassementParEquipeDerniersMatchsExterieur(String code, Long seasonId, int nbMatchs) {
        ChampionnatSaison cs = getChampionnatSaison(code, seasonId);

        List<Match> allMatches = matchRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .filter(m -> m.getScoreDomicile() != null && m.getScoreExterieur() != null)
                .sorted(Comparator.comparing(Match::getNumJournee).reversed()) // trier pour prendre les derniers
                .toList();

        Map<Equipe, TeamStats> statsMap = new HashMap<>();

        // Récupère toutes les équipes du championnat
        List<Equipe> equipes = participationRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .map(Participation::getEquipe)
                .toList();

        for (Equipe equipe : equipes) {
            List<Match> derniersMatchs = allMatches.stream()
                    .filter(m -> m.getEquipeExterieur().equals(equipe))
                    .limit(nbMatchs)
                    .toList();

            TeamStats stats = new TeamStats();
            for (Match match : derniersMatchs) {
                stats.updateAway(match);
            }
            statsMap.put(equipe, stats);
        }

        return statsMap.entrySet().stream()
                .map(e -> e.getValue().toDto(e.getKey()))
                .sorted(Comparator
                        .comparingInt((TeamStandingDto s) -> -s.points())
                        .thenComparingInt(s -> -s.goalDifference())
                        .thenComparingInt(s -> -s.butsPour())
                        .thenComparing(TeamStandingDto::equipe))
                .toList();
    }


    private List<TeamStandingDto> computeTable(Long championnatSaisonId, List<Match> matches) {
        Map<Equipe, TeamStats> table = new HashMap<>();

        for (Match m : matches) {
            table.computeIfAbsent(m.getEquipeDomicile(), e -> new TeamStats()).updateHome(m);
            table.computeIfAbsent(m.getEquipeExterieur(), e -> new TeamStats()).updateAway(m);
        }

        participationRepository.findByChampionnatSaisonId(championnatSaisonId).stream()
                .map(Participation::getEquipe)
                .forEach(eq -> table.computeIfAbsent(eq, e -> new TeamStats()));

        return toSortedStanding(table);
    }

    private List<TeamStandingDto> computeTable(Long championnatSaisonId, List<Match> matches, Function<Match, Equipe> teamExtractor) {
        Map<Equipe, TeamStats> table = new HashMap<>();

        for (Match m : matches) {
            Equipe eq = teamExtractor.apply(m);
            if (eq.equals(m.getEquipeDomicile()))
                table.computeIfAbsent(eq, e -> new TeamStats()).updateHome(m);
            else
                table.computeIfAbsent(eq, e -> new TeamStats()).updateAway(m);
        }

        participationRepository.findByChampionnatSaisonId(championnatSaisonId).stream()
                .map(Participation::getEquipe)
                .forEach(eq -> table.computeIfAbsent(eq, e -> new TeamStats()));

        return toSortedStanding(table);
    }

    private ChampionnatSaison getChampionnatSaison(String code, Long seasonId) {
        Championnat championnat = championnatRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Championnat inconnu: " + code));

        Saison saison = saisonRepository.findById(seasonId)
                .orElseThrow(() -> new IllegalArgumentException("Saison inconnue: " + seasonId));

        return championnatSaisonRepository
                .findByChampionnatIdAndSaisonId(championnat.getId(), saison.getId())
                .orElseThrow(() -> new IllegalStateException("Ce championnat ne possède pas cette saison"));
    }

    private List<TeamStandingDto> toSortedStanding(Map<Equipe, TeamStats> table) {
        return table.entrySet().stream()
                .map(e -> e.getValue().toDto(e.getKey()))
                .sorted(Comparator
                        .comparingInt((TeamStandingDto s) -> -s.points())
                        .thenComparingInt(s -> -s.goalDifference())
                        .thenComparingInt(s -> -s.butsPour())
                        .thenComparing(TeamStandingDto::equipe))
                .toList();
    }

    private static class TeamStats {
        int played, won, draw, lost, gf, ga;

        void updateHome(Match m) {
            played++;
            gf += m.getScoreDomicile();
            ga += m.getScoreExterieur();
            if (m.getScoreDomicile() > m.getScoreExterieur())       won++;
            else if (m.getScoreDomicile() < m.getScoreExterieur()) lost++;
            else                                                   draw++;
        }

        void updateAway(Match m) {
            played++;
            gf += m.getScoreExterieur();
            ga += m.getScoreDomicile();
            if (m.getScoreExterieur() > m.getScoreDomicile())       won++;
            else if (m.getScoreExterieur() < m.getScoreDomicile()) lost++;
            else                                                   draw++;
        }

        TeamStandingDto toDto(Equipe equipe) {
            int points = won * 3 + draw;
            int goalDiff = gf - ga;
            return new TeamStandingDto(
                    equipe.getNom(), played, won, draw, lost,
                    gf, ga, goalDiff, points
            );
        }
    }
}
