package fr.pronofoot.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

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

@Service
public class MatchService {

    @Autowired private  EquipeRepository equipeRepository;
    @Autowired private MatchRepository matchRepository;
    @Autowired private ChampionnatSaisonRepository championnatSaisonRepository;
    @Autowired private MatchMapper matchMapper;
    @Autowired private FootballApiService footballApiService;



    public void saveMatches(List<MatchDto> dtos) {
        for (MatchDto dto : dtos) {
            Instant instant = Instant.parse(dto.getDate());          // "2025-03-29T21:30:00Z"
            LocalDateTime date = LocalDateTime.ofInstant(
                    instant,
                    ZoneId.of("Europe/Paris")                            // ou ZoneOffset.UTC
            );

            ChampionnatSaison championnatSaison = championnatSaisonRepository
                    .findByChampionnat_CodeAndSaison_Annee(dto.getChampionnatCode(), dto.getSaisonAnnee())
                    .orElseThrow(() -> new RuntimeException("Championnat/Saison non trouvé : "
                            + dto.getChampionnatCode() + " - " + dto.getSaisonAnnee()));

            Equipe equipeDomicile = equipeRepository.findByNom(dto.getEquipeDomicile())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée : " + dto.getEquipeDomicile()));

            Equipe equipeExterieur = equipeRepository.findByNom(dto.getEquipeExterieur())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée : " + dto.getEquipeExterieur()));

            Optional<Match> opt = matchRepository.findByChampionnatSaisonAndEquipeDomicileAndEquipeExterieurAndNumJournee(
                    championnatSaison, equipeDomicile, equipeExterieur, dto.getNumJournee());

            Match match = opt.orElseGet(Match::new);
            match.setChampionnatSaison(championnatSaison);
            match.setEquipeDomicile(equipeDomicile);
            match.setEquipeExterieur(equipeExterieur);
            match.setScoreDomicile(null);
            match.setScoreExterieur(null);
            match.setNumJournee(dto.getNumJournee());
            match.setDate(date);
            matchRepository.save(match);

        }
    }

    public void synchronizeFromApi(String championnatCode, String saisonAnnee) {
        List<MatchDto> dtos = footballApiService.getMatches(championnatCode, saisonAnnee);
        saveMatches(dtos);
    }

    @Transactional(readOnly = true)
    public List<MatchDto> findAllMatches(String code, String saison) {
        List<MatchDto> matchDtos = matchRepository
                .findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeOrderByDateAsc(code, saison)
                .stream()
                .map(matchMapper::toDto)
                .toList();
        return matchDtos;
    }

    @Transactional(readOnly = true)
    public List<MatchDto> findMatchesByJournee(String code, String saison, int journee) {
        List<MatchDto> matchDtos = matchRepository
                .findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeAndNumJourneeOrderByDateAsc(
                        code, saison, journee)
                .stream()
                .map(matchMapper::toDto)
                .toList();
        return matchDtos;
    }
}
