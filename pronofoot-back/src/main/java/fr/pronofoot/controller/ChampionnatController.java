package fr.pronofoot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.dto.ChampionnatDto;
import fr.pronofoot.dto.SaisonDto;
import fr.pronofoot.dto.record.EquipeDto;
import fr.pronofoot.service.ChampionnatService;
import fr.pronofoot.service.FootballApiService;

@RestController
@RequestMapping("/api/championnats")
@CrossOrigin(origins = "*")
public class ChampionnatController {

    @Autowired private ChampionnatService championnatService;

    @Autowired private FootballApiService footballApiService;

    // 🔄 Synchronise tous les championnats depuis l'API
//    @PostMapping("/sync")
//    public ResponseEntity<Void> synchronizeChampionnats() {
//        championnatService.loadCompetitionsFromApi();
//        return ResponseEntity.ok().build();
//    }

//    // ➕ Importe un championnat pour une saison avec ses équipes
//    @PostMapping("/import")
//    public ResponseEntity<String> importChampionnatSaison(@RequestBody ChampionnatDto dto) {
//        championnatService.saveChampionnatSaisonAvecEquipes(dto);
//        return ResponseEntity.ok("Championnat et équipes importés pour la saison " + dto.getCurrentSeason().getYear());
//    }

    // 📋 Liste tous les championnats disponibles
    @GetMapping
    public ResponseEntity<List<ChampionnatDto>> getAllChampionnats() {
        return ResponseEntity.ok(championnatService.getAllChampionnats());
    }

    // 📅 Récupère les saisons d’un championnat donné
    @GetMapping("/{code}/saisons")
    public ResponseEntity<List<SaisonDto>> getSaisonsPourChampionnat(@PathVariable String code) {
        return ResponseEntity.ok(championnatService.getSaisonsPourChampionnat(code));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ChampionnatDto> getChampionnat(@PathVariable String code, @RequestParam(defaultValue = "false") boolean sync) {
        ChampionnatDto dto = sync
                ? championnatService.synchronizeAndReturnChampionnat(code)
                : championnatService.getChampionnatFromDb(code);

        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }

    // 🧑‍🤝‍🧑 Liste les équipes pour un championnat et une saison
    @GetMapping("/{code}/saisons/{id}/equipes")
    public ResponseEntity<List<EquipeDto>> getEquipesPourChampionnatEtSaison(
            @PathVariable String code,
            @PathVariable String id) {

        List<EquipeDto> equipes = championnatService.getEquipesParChampionnatEtSaison(code, id).stream()
                .map(e -> new EquipeDto(e.getId(), e.getNom()))
                .toList();

        return ResponseEntity.ok(equipes);
    }


}
