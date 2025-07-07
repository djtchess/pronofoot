package fr.pronofoot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.Equipe;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByNom(String nom);
}