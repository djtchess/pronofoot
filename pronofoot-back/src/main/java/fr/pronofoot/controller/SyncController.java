package fr.pronofoot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.pronofoot.service.MatchService;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private MatchService matchService;

}
