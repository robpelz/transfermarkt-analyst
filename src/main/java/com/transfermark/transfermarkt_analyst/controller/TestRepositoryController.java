package com.transfermark.transfermarkt_analyst.controller;

import com.transfermark.transfermarkt_analyst.model.Player;
import com.transfermark.transfermarkt_analyst.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/test/players")
public class TestRepositoryController {

    @Autowired
    private PlayerRepository playerRepository;

    // Einen Test-Spieler anlegen
    @PostMapping("/create-test")
    public String createTestPlayer() {
        Player player = new Player();
        player.setName("Victor Osimhen");
        player.setPosition("ST");
        player.setCurrentClub("Napoli");
        player.setMarketValue(120.0);
        player.setPlaysInSerieA(true);

        playerRepository.save(player);
        return "Test-Spieler wurde angelegt!";
    }

    // Alle Spieler anzeigen
    @GetMapping("/all")
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Napoli-Spieler filtern
    @GetMapping("/napoli")
    public List<Player> getNapoliPlayers() {
        return playerRepository.findByCurrentClub("Napoli");
    }
}