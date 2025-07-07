package fr.pronofoot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.Saison;

@Repository
public interface SaisonRepository extends JpaRepository<Saison, Long> {
    Optional<Saison> findByAnnee(String annee);
}
