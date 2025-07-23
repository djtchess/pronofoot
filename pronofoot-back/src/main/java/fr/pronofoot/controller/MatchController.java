package fr.pronofoot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.dto.MatchDto;
import fr.pronofoot.service.MatchService;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {
    @Autowired
    private MatchService matchService;

    @PostMapping("/import")
    public ResponseEntity<String> importerMatchs(@RequestParam String code, @RequestParam String saison) {
        matchService.synchronizeFromApi(code, saison);
        return ResponseEntity.ok("Import des matchs terminé pour " + code + " " + saison);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MatchDto>> getAllMatches(@RequestParam String code,
                                                        @RequestParam String saison) {
        return ResponseEntity.ok(matchService.findAllMatches(code, saison));
    }

    @GetMapping("/byJournee")
    public ResponseEntity<List<MatchDto>> getMatchesByJournee(@RequestParam String code,
                                                              @RequestParam String saison,
                                                              @RequestParam int journee) {
        return ResponseEntity.ok(matchService.findMatchesByJournee(code, saison, journee));
    }

    /** Maj des scores d’une journée précise */
    @PostMapping("/championnat/{code}/saison/{annee}/journees/{journee}/updateScores")
    public ResponseEntity<Void> updateScoresJournee(@PathVariable String code,  @PathVariable String annee,
                                                    @PathVariable int journee) {
        matchService.updateScoresForJournee(code, annee, journee);
        return ResponseEntity.noContent().build();          // 204
    }

    /** Maj de tous les matches datés d’hier ou avant (scores manquants) */
    @PostMapping("/championnat/{code}/saison/{annee}/updateScoresBeforeToday")
    public ResponseEntity<Void> updateScoresBeforeToday(@PathVariable String code, @PathVariable String annee) {
        matchService.updateScoresBeforeToday(code, annee);
        return ResponseEntity.noContent().build();
    }

}

