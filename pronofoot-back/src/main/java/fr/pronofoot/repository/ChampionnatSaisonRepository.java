package fr.pronofoot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.pronofoot.entity.ChampionnatSaison;

@Repository
public interface ChampionnatSaisonRepository extends JpaRepository<ChampionnatSaison, Long> {
    Optional<ChampionnatSaison> findByChampionnatIdAndSaisonId(Long championnatId, Long saisonId);
    Optional<ChampionnatSaison> findByChampionnat_CodeAndSaison_Annee(String codeChampionnat, String anneeSaison);
    List<ChampionnatSaison> findByChampionnatId(Long championnatId);
}

