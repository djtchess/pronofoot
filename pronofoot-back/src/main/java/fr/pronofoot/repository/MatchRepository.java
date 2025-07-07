package fr.pronofoot.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByChampionnatSaisonId(Long championnatSaisonId);

    boolean existsByDateAndEquipeDomicileAndEquipeExterieur(LocalDate date, Equipe equipeDomicile, Equipe equipeExterieur);
}

