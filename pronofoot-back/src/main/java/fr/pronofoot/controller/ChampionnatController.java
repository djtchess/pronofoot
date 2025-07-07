package fr.pronofoot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.dto.ChampionnatDto;
import fr.pronofoot.dto.ChampionnatSaisonDto;
import fr.pronofoot.service.ChampionnatService;

@RestController
@RequestMapping("/api/championnats")
public class ChampionnatController {

    @Autowired private ChampionnatService championnatService;

    // 🔄 Synchronise tous les championnats depuis l'API
    @PostMapping("/sync")
    public ResponseEntity<Void> synchronizeChampionnats() {
        championnatService.loadCompetitionsFromApi();
        return ResponseEntity.ok().build();
    }

    // ➕ Importe un championnat pour une saison avec ses équipes
    @PostMapping("/import")
    public ResponseEntity<String> importChampionnatSaison(@RequestBody ChampionnatSaisonDto dto) {
        championnatService.saveChampionnatSaisonAvecEquipes(dto);
        return ResponseEntity.ok("Championnat et équipes importés pour la saison " + dto.getAnneeSaison());
    }

    // 📋 Liste tous les championnats disponibles
    @GetMapping
    public ResponseEntity<List<ChampionnatDto>> getAllChampionnats() {
        return ResponseEntity.ok(championnatService.getAllChampionnats());
    }

    // 📅 Récupère les saisons d’un championnat donné
    @GetMapping("/{code}/saisons")
    public ResponseEntity<List<String>> getSaisonsPourChampionnat(@PathVariable String code) {
        return ResponseEntity.ok(championnatService.getSaisonsPourChampionnat(code));
    }

    // 🧑‍🤝‍🧑 Liste les équipes pour un championnat et une saison
    @GetMapping("/{code}/saisons/{annee}/equipes")
    public ResponseEntity<List<String>> getEquipesPourChampionnatEtSaison(@PathVariable String code, @PathVariable String annee) {
        return ResponseEntity.ok(championnatService.getEquipesParChampionnatEtSaison(code, annee));
    }
}
