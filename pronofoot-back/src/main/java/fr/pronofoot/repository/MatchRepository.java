package fr.pronofoot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByChampionnatSaisonId(Long championnatSaisonId);

    boolean existsByDateAndEquipeDomicileAndEquipeExterieur(LocalDateTime date, Equipe equipeDomicile, Equipe equipeExterieur);


    /* pour toute la saison */
    List<Match> findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeOrderByDateAsc(String code, String annee);

    /* pour une journée donnée */
    List<Match> findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeAndNumJourneeOrderByDateAsc(String code, String annee, int numJournee);
}

