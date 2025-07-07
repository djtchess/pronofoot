package fr.pronofoot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.Championnat;

@Repository
public interface ChampionnatRepository extends JpaRepository<Championnat, Long> {
    Optional<Championnat> findByCode(String code);
    boolean existsByCode(String code);
}
