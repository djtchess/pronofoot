package fr.pronofoot.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.pronofoot.dto.ChampionnatDto;
import fr.pronofoot.dto.ChampionnatSaisonDto;
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

@Service
public class ChampionnatService {

    @Autowired private  ChampionnatRepository championnatRepository;
    @Autowired private SaisonRepository saisonRepository;
    @Autowired private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private ParticipationRepository participationRepository;
    @Autowired private FootballApiService footballApiService;

    public void loadCompetitionsFromApi() {
        List<CompetitionItem> competitions = footballApiService.getCompetitions();

        for (CompetitionItem item : competitions) {
            if (item.getCode() == null) continue;

            championnatRepository.findByCode(item.getCode())
                    .orElseGet(() -> {
                        Championnat c = new Championnat();
                        c.setCode(item.getCode());
                        c.setNom(item.getName());
                        return championnatRepository.save(c);
                    });
        }
    }

    public void saveChampionnatSaisonAvecEquipes(ChampionnatSaisonDto dto) {
        Championnat championnat = championnatRepository.findByCode(dto.getCodeChampionnat())
                .orElseGet(() -> championnatRepository.save(new Championnat(dto.getCodeChampionnat(), dto.getNomChampionnat())));

        Saison saison = saisonRepository.findByAnnee(dto.getAnneeSaison())
                .orElseGet(() -> saisonRepository.save(new Saison(dto.getAnneeSaison())));

        ChampionnatSaison championnatSaison = championnatSaisonRepository
                .findByChampionnatIdAndSaisonId(championnat.getId(), saison.getId())
                .orElseGet(() -> championnatSaisonRepository.save(new ChampionnatSaison(championnat, saison)));

        // Si dto.getEquipes() est null ou vide, on récupère les équipes via l'API
        Set<String> nomsEquipes = dto.getEquipes();
        if (nomsEquipes == null || nomsEquipes.isEmpty()) {
            nomsEquipes = footballApiService.getEquipes(dto.getCodeChampionnat(), dto.getAnneeSaison());
        }

        for (String nomEquipe : nomsEquipes) {
            Equipe equipe = equipeRepository.findByNom(nomEquipe)
                    .orElseGet(() -> equipeRepository.save(new Equipe(nomEquipe)));

            if (!participationRepository.existsByEquipeAndChampionnatSaison(equipe, championnatSaison)) {
                Participation participation = new Participation();
                participation.setEquipe(equipe);
                participation.setChampionnatSaison(championnatSaison);
                participationRepository.save(participation);
            }
        }
    }


    public List<ChampionnatDto> getAllChampionnats() {
        return championnatRepository.findAll().stream()
                .map(c -> new ChampionnatDto(c.getCode(), c.getNom(), null, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un championnat par son code et le transforme en DTO.
     * @throws EntityNotFoundException si le code n'existe pas.
     */
    public ChampionnatDto getChampionnat(String code) {
        return championnatRepository.findByCode(code)
                .map(c -> new ChampionnatDto(c.getCode(), c.getNom(), null, null, null))
                .orElseThrow(() ->
                        new EntityNotFoundException("Championnat non trouvé : " + code));
    }

    public ChampionnatDto synchronizeAndReturnChampionnat(String code) {
        ChampionnatDto apiDto = footballApiService.getCompetitionInfo(code);
        Championnat championnat = championnatRepository.findByCode(code)
                .orElseGet(() -> new Championnat());

        championnat.setCode(apiDto.getCode());
        championnat.setNom(apiDto.getNom());
        championnatRepository.save(championnat);

        return apiDto;
    }

    public ChampionnatDto getChampionnatFromDb(String code) {
        return championnatRepository.findByCode(code)
                .map(championnat -> {
                    ChampionnatDto dto = new ChampionnatDto();
                    dto.setCode(championnat.getCode());
                    dto.setNom(championnat.getNom());

                    // Extraction du pays (si tu le stockes dans une future version de l'entité)
                    dto.setPays(null); // à adapter si le champ est en base

                    if (championnat.getSaisons() != null && !championnat.getSaisons().isEmpty()) {
                        // Prendre la saison la plus récente (max année)
                        ChampionnatSaison derniereSaison = championnat.getSaisons().stream()
                                .max((a, b) -> a.getSaison().getAnnee().compareTo(b.getSaison().getAnnee()))
                                .orElse(null);

                        if (derniereSaison != null) {
                            SaisonDto saisonDto = new SaisonDto();
                            saisonDto.setId(derniereSaison.getSaison().getId());
                            saisonDto.setYear(Integer.valueOf(derniereSaison.getSaison().getAnnee()));
                            // Les dates ne sont pas dans ta BDD actuelle, donc null
                            saisonDto.setStartDate(null);
                            saisonDto.setEndDate(null);
                            dto.setCurrentSeason(saisonDto);
                        }
                    }
                    return dto;
                })
                .orElse(null);
    }

    public List<String> getSaisonsPourChampionnat(String codeChampionnat) {
        Optional<Championnat> championnatOpt = championnatRepository.findByCode(codeChampionnat);
        if (championnatOpt.isEmpty()) return List.of();

        return championnatSaisonRepository.findByChampionnatId(championnatOpt.get().getId()).stream()
                .map(cs -> cs.getSaison().getAnnee())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Equipe> getEquipesParChampionnatEtSaison(String codeChampionnat, String anneeSaison) {
        Optional<Championnat> championnatOpt = championnatRepository.findByCode(codeChampionnat);
        Optional<Saison>       saisonOpt       = saisonRepository.findByAnnee(anneeSaison);
        if (championnatOpt.isEmpty() || saisonOpt.isEmpty()) return List.of();

        Optional<ChampionnatSaison> csOpt = championnatSaisonRepository
                .findByChampionnatIdAndSaisonId(championnatOpt.get().getId(), saisonOpt.get().getId());

        if (csOpt.isEmpty()) return List.of();

        return participationRepository.findByChampionnatSaison(csOpt.get()).stream()
                .map(Participation::getEquipe)
                .sorted(Comparator.comparing(Equipe::getNom))
                .collect(Collectors.toList());
    }

}
