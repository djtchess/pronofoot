package fr.pronofoot.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.pronofoot.dto.ChampionnatDto;
import fr.pronofoot.dto.ChampionnatSaisonDto;
import fr.pronofoot.dto.CompetitionItem;
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
                .map(c -> new ChampionnatDto(c.getCode(), c.getNom()))
                .collect(Collectors.toList());
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

    public List<String> getEquipesParChampionnatEtSaison(String codeChampionnat, String anneeSaison) {
        Optional<Championnat> championnatOpt = championnatRepository.findByCode(codeChampionnat);
        Optional<Saison> saisonOpt = saisonRepository.findByAnnee(anneeSaison);
        if (championnatOpt.isEmpty() || saisonOpt.isEmpty()) return List.of();

        Optional<ChampionnatSaison> csOpt = championnatSaisonRepository
                .findByChampionnatIdAndSaisonId(championnatOpt.get().getId(), saisonOpt.get().getId());

        if (csOpt.isEmpty()) return List.of();

        return participationRepository.findByChampionnatSaison(csOpt.get()).stream()
                .map(p -> p.getEquipe().getNom())
                .sorted()
                .collect(Collectors.toList());
    }
}
