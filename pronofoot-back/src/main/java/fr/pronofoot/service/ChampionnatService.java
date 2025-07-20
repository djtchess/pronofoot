// ────────────────────────────────────────────────────────────────
// src/main/java/fr/pronofoot/service/ChampionnatService.java
// Refactor : factorisation + injection par constructeur Lombok
// ────────────────────────────────────────────────────────────────
package fr.pronofoot.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pronofoot.dto.ChampionnatDto;
import fr.pronofoot.dto.CompetitionItem;
import fr.pronofoot.dto.SaisonDto;
import fr.pronofoot.entity.Championnat;
import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Participation;
import fr.pronofoot.entity.Saison;
import fr.pronofoot.repository.ChampionnatRepository;
import fr.pronofoot.repository.ChampionnatSaisonRepository;
import fr.pronofoot.repository.EquipeRepository;
import fr.pronofoot.repository.ParticipationRepository;
import fr.pronofoot.repository.SaisonRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Service central pour la gestion des championnats, saisons, équipes et participations.
 * <p>
 * 1. Synchronisation avec l'API externe (compétitions, équipes)<br/>
 * 2. Opérations CRUD / lecture enrichie côté base<br/>
 * 3. Factorisation des duplications précédentes : création ou récupération d'entités via<br/>
 *    des helpers internes dédiés.
 * </p>
 */
@Service
@Transactional
public class ChampionnatService {

    // ────────────────────────────────────────────────────────────────
    // Dépendances (injection par constructeur → @RequiredArgsConstructor)
    // ────────────────────────────────────────────────────────────────

    @Autowired private ChampionnatRepository championnatRepository;
    @Autowired private SaisonRepository saisonRepository;
    @Autowired private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private ParticipationRepository participationRepository;
    @Autowired private FootballApiService footballApiService;

    // ────────────────────────────────────────────────────────────────
    // 1. Synchronisation des compétitions (championnats)
    // ────────────────────────────────────────────────────────────────
    public void synchronizeCompetitions() {
        footballApiService.getCompetitions().stream()
                .filter(c -> c.getCode() != null)
                .forEach(this::createOrUpdateChampionnat);
    }

    // ────────────────────────────────────────────────────────────────
    // 2. Sauvegarde des équipes pour une saison donnée OU la saison courante
    // ────────────────────────────────────────────────────────────────

    /**
     * Enregistre (ou met à jour) les équipes pour <b>n'importe quelle</b> saison d'un championnat.
     * Les entités manquantes (championnat, saison, lien ChampionnatSaison) sont créées au besoin.
     *
     * @param codeChampionnat code alpha (ex. "PD" = La Liga)
     * @param saisonDto           année de début de la saison (ex. 2024 pour 2024‑2025)
     */
    public void saveTeamsForSeason(String codeChampionnat, SaisonDto saisonDto) {
        Championnat championnat = getOrCreateChampionnat(codeChampionnat);
        Saison saison           = getOrCreateSaison(saisonDto);
        ChampionnatSaison cs    = getOrCreateChampionnatSaison(championnat, saison);

        persistTeams(championnat.getCode(), saison.getAnnee(), cs);
    }

//    /**
//     * Variante utilisant la saison courante enregistrée sur le championnat.
//     *
//     * @throws IllegalStateException si {@code currentSeason} est absent.
//     */
//    public void saveTeamsForCurrentSeason(String codeChampionnat) {
//        Championnat championnat = championnatRepository.findByCode(codeChampionnat)
//                .orElseThrow(() -> new IllegalArgumentException("Championnat inexistant : " + codeChampionnat));
//
//        Saison saisonCourante = Optional.ofNullable(championnat.getCurrentSeason())
//                .orElseThrow(() -> new IllegalStateException("Aucune currentSeason définie pour " + codeChampionnat));
//
//        saveTeamsForSeason(codeChampionnat, saisonCourante.getAnnee());
//    }

    // ────────────────────────────────────────────────────────────────
    // 3. Accès read‑only (DTO → REST layer)
    // ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ChampionnatDto> getAllChampionnats() {
        return championnatRepository.findAll().stream()
                .map(c -> new ChampionnatDto(c.getCode(), c.getNom(), null, null, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChampionnatDto getChampionnat(String code) {
        return championnatRepository.findByCode(code)
                .map(c -> new ChampionnatDto(c.getCode(), c.getNom(), null, null, null))
                .orElseThrow(() -> new EntityNotFoundException("Championnat non trouvé : " + code));
    }

    /**
     * Synchronise un championnat + sa saison courante depuis l'API puis le renvoie (DTO).
     */
    @Transactional
    public ChampionnatDto synchronizeAndReturnChampionnat(String code) {
        ChampionnatDto apiDto = footballApiService.getChampionnatDetails(code);
        saveTeamsForSeason(code, apiDto.getCurrentSeason());
        return apiDto;
    }

    @Transactional(readOnly = true)
    public ChampionnatDto getChampionnatFromDb(String code) {
        return championnatRepository.findByCode(code)
                .map(this::toChampionnatDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<SaisonDto> getSaisonsPourChampionnat(String codeChampionnat) {
        return championnatRepository.findByCode(codeChampionnat)
                .stream()
                .flatMap(ch -> championnatSaisonRepository.findByChampionnatId(ch.getId()).stream())
                .map(cs -> new SaisonDto(cs.getSaison().getId(), null, null, cs.getSaison().getAnnee()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Equipe> getEquipesParChampionnatEtSaison(String codeChampionnat, String idSaison) {
        Optional<Championnat> chOpt = championnatRepository.findByCode(codeChampionnat);
        Optional<Saison>      sOpt  = saisonRepository.findById(Long.valueOf(idSaison));
        if (chOpt.isEmpty() || sOpt.isEmpty()) return List.of();

        return championnatSaisonRepository.findByChampionnatIdAndSaisonId(chOpt.get().getId(), sOpt.get().getId())
                .stream()
                .flatMap(cs -> participationRepository.findByChampionnatSaison(cs).stream())
                .map(Participation::getEquipe)
                .sorted(Comparator.comparing(Equipe::getNom))
                .toList();
    }

    // ────────────────────────────────────────────────────────────────
    // 4. Helpers internes (factorisés)
    // ────────────────────────────────────────────────────────────────
    private Championnat createOrUpdateChampionnat(CompetitionItem item) {
        Championnat championnat = championnatRepository.findByCode(item.getCode())
                .orElseGet(() ->  new Championnat(item.getCode(), item.getName()));
        championnat.setNom(item.getName());
        return championnatRepository.save(championnat);
    }

    private Championnat getOrCreateChampionnat(String codeChampionnat) {
        return championnatRepository.findByCode(codeChampionnat)
                .orElseGet(() -> championnatRepository.save(new Championnat(codeChampionnat, codeChampionnat)));
    }

    private Saison getOrCreateSaison(SaisonDto dto) {
        return saisonRepository.findById(dto.getId())
                .orElseGet(() -> {
                    Saison s = new Saison();
                    s.setId(dto.getId());
                    s.setAnnee(dto.getYear());
                    s.setStartDate(dto.getStartDate());
                    s.setEndDate(dto.getEndDate());
                    return saisonRepository.save(s);
                });
    }

    private ChampionnatSaison getOrCreateChampionnatSaison(Championnat championnat, Saison saison) {
        return championnatSaisonRepository
                .findByChampionnatIdAndSaisonId(championnat.getId(), saison.getId())
                .orElseGet(() -> championnatSaisonRepository.save(new ChampionnatSaison(championnat, saison)));
    }

    private void persistTeams(String codeChampionnat, String annee, ChampionnatSaison championnatSaison) {
        Set<String> teamNames = footballApiService.getEquipes(codeChampionnat, annee);

        for (String nomEquipe : teamNames) {
            Equipe equipe = equipeRepository.findByNom(nomEquipe)
                    .orElseGet(() -> equipeRepository.save(new Equipe(nomEquipe)));

            if (!participationRepository.existsByEquipeAndChampionnatSaison(equipe, championnatSaison)) {
                Participation p = new Participation();
                p.setEquipe(equipe);
                p.setChampionnatSaison(championnatSaison);
                participationRepository.save(p);
            }
        }
    }


    /* ==================================================================== */
    /* Mapping                                                             */
    /* ==================================================================== */

    /**
     * Transforme une entité {@link Championnat} en {@link ChampionnatDto} en
     * veillant à toujours renseigner {@link ChampionnatDto#getCurrentSeason()}.
     */
    private ChampionnatDto toChampionnatDto(Championnat championnat) {
        ChampionnatDto dto = new ChampionnatDto();
        dto.setCode(championnat.getCode());
        dto.setNom(championnat.getNom());
        dto.setPays(null); // null si champ non présent

        // 1) Saison courante indiquée explicitement sur l’entité
        Saison saison = championnat.getCurrentSeason();

        // 2) À défaut, prendre la plus récente dans la liste des relations
        if (saison == null && championnat.getSaisons() != null && !championnat.getSaisons().isEmpty()) {
            saison = championnat.getSaisons().stream()
                    .map(ChampionnatSaison::getSaison)
                    .max(Comparator.comparing(Saison::getAnnee))
                    .orElse(null);
        }

        if (saison != null) {
            dto.setCurrentSeason(new SaisonDto(
                    saison.getId(),
                    saison.getStartDate(),
                    saison.getEndDate(),
                    saison.getAnnee()));
        }

        return dto;
    }

}
