package fr.pronofoot.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired private ChampionnatRepository        championnatRepository;
    @Autowired private  SaisonRepository             saisonRepository;
    @Autowired private  ChampionnatSaisonRepository  championnatSaisonRepository;
    @Autowired private  MatchRepository              matchRepository;
    @Autowired private  ParticipationRepository      participationRepository;

    /**
     * Calcule le classement d’un championnat pour une saison précise.
     *
     * @param code      code alpha du championnat (ex. : PL, FL1)
     * @param seasonId  identifiant de la saison
     */
    public List<TeamStandingDto> computeClassement(String code, Long seasonId) {

        /* 1. Vérifications et récupération des entités ------------------ */
        Championnat championnat = championnatRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Championnat inconnu: " + code));

        Saison saison = saisonRepository.findById(seasonId)
                .orElseThrow(() -> new IllegalArgumentException("Saison inconnue: " + seasonId));

        ChampionnatSaison cs = championnatSaisonRepository
                .findByChampionnatIdAndSaisonId(championnat.getId(), saison.getId())
                .orElseThrow(() -> new IllegalStateException("Ce championnat ne possède pas cette saison"));

        /* 2. Matches joués ---------------------------------------------- */
        List<Match> matches = matchRepository.findByChampionnatSaisonId(cs.getId());

        /* 3. Table équipe -> stats -------------------------------------- */
        Map<Equipe, TeamStats> table = new HashMap<>();

        for (Match m : matches) {
            // Ignore les rencontres non jouées
            if (m.getScoreDomicile() == null || m.getScoreExterieur() == null) continue;

            table.computeIfAbsent(m.getEquipeDomicile(),  e -> new TeamStats()).updateHome(m);
            table.computeIfAbsent(m.getEquipeExterieur(), e -> new TeamStats()).updateAway(m);
        }

        /* 4. Ajoute les équipes sans match ------------------------------ */
        participationRepository.findByChampionnatSaisonId(cs.getId()).stream()
                .map(Participation::getEquipe)
                .forEach(eq -> table.computeIfAbsent(eq, e -> new TeamStats()));

        /* 5. Conversion + tri ------------------------------------------- */
        return table.entrySet().stream()
                .map(e -> e.getValue().toDto(e.getKey()))
                .sorted(
                        Comparator.comparingInt(TeamStandingDto::points).reversed()
                                .thenComparingInt(TeamStandingDto::goalDifference).reversed()
                                .thenComparingInt(TeamStandingDto::butsPour).reversed()
                                .thenComparing(TeamStandingDto::equipe)
                )
                .collect(Collectors.toList());
    }

    /* ------------------------------------------------------------------ */
    /* Helper interne pour cumuler les statistiques                       */
    /* ------------------------------------------------------------------ */
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
            int points     = won * 3 + draw;
            int goalDiff   = gf - ga;
            return new TeamStandingDto(
                    equipe.getNom(), played, won, draw, lost,
                    gf, ga, goalDiff, points
            );
        }
    }
}
