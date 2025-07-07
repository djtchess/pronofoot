package fr.pronofoot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.pronofoot.dto.MatchDto;
import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Match;
import fr.pronofoot.mapper.MatchMapper;
import fr.pronofoot.repository.ChampionnatSaisonRepository;
import fr.pronofoot.repository.EquipeRepository;
import fr.pronofoot.repository.MatchRepository;

@Service
public class MatchService {

    @Autowired private  EquipeRepository equipeRepository;
    @Autowired private MatchRepository matchRepository;
    @Autowired private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired private MatchMapper matchMapper;
    @Autowired private FootballApiService footballApiService;

    public void saveMatches(List<MatchDto> dtos) {
        for (MatchDto dto : dtos) {
            LocalDate date = LocalDate.parse(dto.getDate());

            ChampionnatSaison championnatSaison = championnatSaisonRepository
                    .findByChampionnat_CodeAndSaison_Annee(dto.getChampionnatCode(), dto.getSaisonAnnee())
                    .orElseThrow(() -> new RuntimeException("Championnat/Saison non trouvé : "
                            + dto.getChampionnatCode() + " - " + dto.getSaisonAnnee()));

            Equipe equipeDomicile = equipeRepository.findByNom(dto.getEquipeDomicile())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée : " + dto.getEquipeDomicile()));

            Equipe equipeExterieur = equipeRepository.findByNom(dto.getEquipeExterieur())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée : " + dto.getEquipeExterieur()));

            boolean exists = matchRepository.existsByDateAndEquipeDomicileAndEquipeExterieur(
                    date, equipeDomicile, equipeExterieur);

            if (!exists) {
                Match match = matchMapper.toEntity(dto);
                match.setEquipeDomicile(equipeDomicile);
                match.setEquipeExterieur(equipeExterieur);
                match.setChampionnatSaison(championnatSaison);
                match.setDate(date);

                matchRepository.save(match);
            }
        }
    }

    public void synchronizeFromApi(String championnatCode, String saisonAnnee) {
        List<MatchDto> dtos = footballApiService.getMatches(championnatCode, saisonAnnee);
        saveMatches(dtos);
    }
}
