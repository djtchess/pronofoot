package fr.pronofoot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.dto.record.TeamStandingDto;
import fr.pronofoot.service.ClassementService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/championnats")
public class ClassementController {

    private final ClassementService classementService;

    public ClassementController(ClassementService classementService) {
        this.classementService = classementService;
    }

    @GetMapping("/{code}/saisons/{seasonId}/classement")
    public ResponseEntity<List<TeamStandingDto>> getClassement(
            @PathVariable String code,
            @PathVariable Long seasonId) {
        List<TeamStandingDto> classement = classementService.computeClassement(code, seasonId);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/{code}/saisons/{seasonId}/classement/domicile")
    public ResponseEntity<List<TeamStandingDto>> getClassementDomicile(
            @PathVariable String code,
            @PathVariable Long seasonId) {
        List<TeamStandingDto> classement = classementService.computeClassementDomicile(code, seasonId);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/{code}/saisons/{seasonId}/classement/exterieur")
    public ResponseEntity<List<TeamStandingDto>> getClassementExterieur(
            @PathVariable String code,
            @PathVariable Long seasonId) {
        List<TeamStandingDto> classement = classementService.computeClassementExterieur(code, seasonId);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/{code}/saisons/{seasonId}/classement/derniers/{nbMatchs}")
    public ResponseEntity<List<TeamStandingDto>> getClassementDerniersMatchs(
            @PathVariable String code,
            @PathVariable Long seasonId,
            @PathVariable int nbMatchs) {
        List<TeamStandingDto> classement = classementService.computeClassementDernieresJournees(code, seasonId, nbMatchs);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/{code}/saisons/{seasonId}/classement/derniers/{nbMatchs}/domicile")
    public ResponseEntity<List<TeamStandingDto>> getClassementDerniersMatchsDomicile(
            @PathVariable String code,
            @PathVariable Long seasonId,
            @PathVariable int nbMatchs) {
        List<TeamStandingDto> classement = classementService.computeClassementDernieresJourneesDomicile(code, seasonId, nbMatchs);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/{code}/saisons/{seasonId}/classement/derniers/{nbMatchs}/exterieur")
    public ResponseEntity<List<TeamStandingDto>> getClassementDerniersMatchsExterieur(
            @PathVariable String code,
            @PathVariable Long seasonId,
            @PathVariable int nbMatchs) {
        List<TeamStandingDto> classement = classementService.computeClassementDernieresJourneesExterieur(code, seasonId, nbMatchs);
        return ResponseEntity.ok(classement);
    }
}

