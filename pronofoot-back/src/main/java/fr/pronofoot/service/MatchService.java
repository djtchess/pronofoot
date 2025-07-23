package fr.pronofoot.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pronofoot.dto.MatchDto;
import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Match;
import fr.pronofoot.mapper.MatchMapper;
import fr.pronofoot.repository.ChampionnatSaisonRepository;
import fr.pronofoot.repository.EquipeRepository;
import fr.pronofoot.repository.MatchRepository;

/**
 * Service local qui :
 * <ul>
 *   <li>importe les matches depuis l’API football-data (via {@link FootballApiService})</li>
 *   <li>met à jour / insère (“upsert”) les lignes en base</li>
 *   <li>rafraîchit les scores a posteriori (journée précise ou
 *       tous les matches antérieurs à aujourd’hui)</li>
 * </ul>
 */
@Service
public class MatchService {

    /* ---------- Dépendances -------------------------------------- */
    @Autowired private EquipeRepository            equipeRepository;
    @Autowired private MatchRepository             matchRepository;
    @Autowired private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired private MatchMapper                 matchMapper;
    @Autowired private FootballApiService          footballApiService;

    /* ==============================================================
       1.  Import brut d’une liste de MatchDto
    ============================================================== */
    @Transactional
    public void saveMatches(List<MatchDto> dtos) {

        for (MatchDto dto : dtos) {

            /* ------------- conversion date ----------------------- */
            Instant instant       = Instant.parse(dto.getDate());        // 2025-03-29T21:30:00Z
            LocalDateTime dateUtc = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
            LocalDateTime dateFr  = dateUtc.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("Europe/Paris"))
                    .toLocalDateTime();

            /* ------------- FK Championnat-Saison ----------------- */
            ChampionnatSaison championnatSaison = championnatSaisonRepository
                    .findByChampionnat_CodeAndSaison_Annee(
                            dto.getChampionnatCode(), dto.getSaisonAnnee())
                    .orElseThrow(() -> new RuntimeException(
                            "Championnat/Saison introuvable : "
                                    + dto.getChampionnatCode() + " - " + dto.getSaisonAnnee()));

            /* ------------- FK Équipes ---------------------------- */
            Equipe equipeDom = equipeRepository.findByNom(dto.getEquipeDomicile())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée : " + dto.getEquipeDomicile()));

            Equipe equipeExt = equipeRepository.findByNom(dto.getEquipeExterieur())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée : " + dto.getEquipeExterieur()));

            /* ------------- upsert sur clé fonctionnelle ---------- */
            Optional<Match> opt = matchRepository
                    .findByChampionnatSaisonAndEquipeDomicileAndEquipeExterieurAndNumJournee(
                            championnatSaison, equipeDom, equipeExt, dto.getNumJournee());

            Match match = opt.orElseGet(Match::new);

            match.setRemoteId(dto.getRemoteId());                // <-- identifiant API stable
            match.setChampionnatSaison(championnatSaison);
            match.setEquipeDomicile(equipeDom);
            match.setEquipeExterieur(equipeExt);
            match.setDate(dateFr);
            match.setNumJournee(dto.getNumJournee());
            match.setScoreDomicile(null);
            match.setScoreExterieur(null);

            matchRepository.save(match);                         // insert ou update
        }
    }

    /* ==============================================================
       2.  Import depuis l’API (appel unique côté public)
    ============================================================== */
    public void synchronizeFromApi(String championnatCode, String saisonAnnee) {
        List<MatchDto> dtos = footballApiService.getMatches(championnatCode, saisonAnnee);
        saveMatches(dtos);
    }

    /* ==============================================================
       3.  Requêtes lecture
    ============================================================== */
    @Transactional(readOnly = true)
    public List<MatchDto> findAllMatches(String code, String saison) {
        return matchRepository
                .findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeOrderByDateAsc(code, saison)
                .stream()
                .map(matchMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchDto> findMatchesByJournee(String code, String saison, int journee) {
        return matchRepository
                .findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeAndNumJourneeOrderByDateAsc(
                        code, saison, journee)
                .stream()
                .map(matchMapper::toDto)
                .toList();
    }

    /* ==============================================================
       4.  Mise à jour des scores
    ============================================================== */

    /** Rafraîchit tous les scores d’une journée. */
    @Transactional
    public void updateScoresForJournee(String codeChampionnat, String annee, int journee) {

        /* 1/ matches en base pour la journée */
        List<Match> dbMatches = matchRepository
                .findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeAndNumJourneeOrderByDateAsc(
                        codeChampionnat, annee, journee);

        if (dbMatches.isEmpty()) return;

        /* 2/ matches tiers (API) pour la même journée */
        List<MatchDto> apiMatches = footballApiService
                .getMatchesByJournee(codeChampionnat, annee, journee);

        /* map : remoteId -> MatchDto */
        Map<Long, MatchDto> mapById = apiMatches.stream()
                .filter(dto -> dto.getRemoteId() != null)
                .collect(Collectors.toMap(MatchDto::getRemoteId, dto -> dto));

        /* 3/ synchronisation */
        dbMatches.forEach(db -> {
            MatchDto dto = (db.getRemoteId() != null)
                    ? mapById.get(db.getRemoteId())
                    : findByTeams(apiMatches, db);

            if (dto != null && dto.getScoreDomicile() != null && dto.getScoreExterieur() != null) {
                db.setScoreDomicile(dto.getScoreDomicile());
                db.setScoreExterieur(dto.getScoreExterieur());
            }
        });
        /* flush automatique en fin de transaction */
    }

    /** Rafraîchit les scores de tous les matches joués avant aujourd’hui. */
    @Transactional
    public void updateScoresBeforeToday(String codeChampionnat, String annee) {

        LocalDate today = LocalDate.now();

        List<Match> toUpdate = matchRepository
                .findWithoutScoreBeforeDate(codeChampionnat, annee, today);

        /* tri pour garder un ordre déterministe (facultatif) */
        toUpdate.sort(Comparator.comparing(Match::getDate));

        toUpdate.forEach(m -> {
            if (m.getRemoteId() == null) return;          // rien à faire

            footballApiService.getMatchById(m.getRemoteId())
                    .filter(dto -> dto.getScoreDomicile() != null && dto.getScoreExterieur() != null)
                    .ifPresent(dto -> {
                        m.setScoreDomicile(dto.getScoreDomicile());
                        m.setScoreExterieur(dto.getScoreExterieur());

                        /* l’API peut retourner l’heure exacte -> maj date */
                        Instant inst     = Instant.parse(dto.getDate());
                        LocalDateTime dt = LocalDateTime.ofInstant(inst, ZoneId.of("Europe/Paris"));
                        m.setDate(dt);
                    });
        });
    }

    /* ==============================================================
       5.  Helpers privés
    ============================================================== */
    /** Fallback si remoteId absent : recherche sur noms d’équipes. */
    private MatchDto findByTeams(List<MatchDto> apiList, Match db) {
        return apiList.stream()
                .filter(dto -> dto.getEquipeDomicile().equalsIgnoreCase(db.getEquipeDomicile().getNom())
                        && dto.getEquipeExterieur().equalsIgnoreCase(db.getEquipeExterieur().getNom()))
                .findFirst()
                .orElse(null);
    }
}
