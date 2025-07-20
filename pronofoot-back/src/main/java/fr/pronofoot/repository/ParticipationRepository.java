package fr.pronofoot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.ChampionnatSaison;
import fr.pronofoot.entity.Equipe;
import fr.pronofoot.entity.Participation;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByEquipeAndChampionnatSaison(Equipe equipe, ChampionnatSaison championnatSaison);
    List<Participation> findByChampionnatSaison(ChampionnatSaison championnatSaison);
    List<Participation> findByChampionnatSaisonId( Long championnatSaisonId);
}
