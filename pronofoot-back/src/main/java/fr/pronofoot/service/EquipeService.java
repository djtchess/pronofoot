package fr.pronofoot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.repository.ChampionnatSaisonRepository;
import fr.pronofoot.repository.ParticipationRepository;

@Service
public class EquipeService {

    @Autowired
    private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired
    private ParticipationRepository participationRepository;


    public List<String> getEquipesParChampionnatEtSaison(String code, String annee) {
        ChampionnatSaison cs = championnatSaisonRepository
                .findByChampionnat_CodeAndSaison_Annee(code, annee)
                .orElseThrow(() -> new RuntimeException("Championnat non trouvé"));

        return participationRepository.findByChampionnatSaison(cs).stream()
                .map(p -> p.getEquipe().getNom())
                .collect(Collectors.toList());
    }

}
