package fr.pronofoot.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByChampionnatSaisonIdAndScoreDomicileNotNullAndScoreExterieurNotNull(Long championnatSaisonId);

    List<Match> findByChampionnatSaisonId(Long championnatSaisonId);

    Optional<Match> findByChampionnatSaisonAndEquipeDomicileAndEquipeExterieurAndNumJournee(ChampionnatSaison championnatSaison, Equipe equipeDomicile, Equipe equipeExterieur, Long numJournee);


    /* pour toute la saison */
    List<Match> findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeOrderByDateAsc(String code, String annee);

    /* pour une journée donnée */
    List<Match> findByChampionnatSaison_Championnat_CodeAndChampionnatSaison_Saison_AnneeAndNumJourneeOrderByDateAsc(String code, String annee, int numJournee);


    @Query("""
         from Match m
         where m.championnatSaison.championnat.code = :code
            and m.championnatSaison.saison.annee = :annee
           and (m.scoreDomicile is null or m.scoreExterieur is null)
           and m.date < :limit
         """)
    List<Match> findWithoutScoreBeforeDate(@Param("code") String code,
                                           @Param("annee") String annee,
                                           @Param("limit") LocalDate limit);


}

