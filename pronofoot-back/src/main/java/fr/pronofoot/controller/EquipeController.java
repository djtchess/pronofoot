package fr.pronofoot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.service.EquipeService;

@RestController
@RequestMapping("/api/equipes")
@CrossOrigin(origins = "*")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @GetMapping("/{code}/{annee}")
    public ResponseEntity<List<String>> getEquipesPourChampionnatEtSaison(@PathVariable String code, @PathVariable String annee) {
        return ResponseEntity.ok(equipeService.getEquipesParChampionnatEtSaison(code, annee));
    }


}
