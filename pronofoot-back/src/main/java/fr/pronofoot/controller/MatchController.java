package fr.pronofoot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.service.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
    @Autowired
    private MatchService matchService;

    @PostMapping("/import")
    public ResponseEntity<String> importerMatchs(@RequestParam String code, @RequestParam String saison) {
        matchService.synchronizeFromApi(code, saison);
        return ResponseEntity.ok("Import des matchs terminé pour " + code + " " + saison);
    }

}

