package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.service.PlayerImageService;
import com.transfermarkt.transfermarkt_analyst.service.SoFifaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player-images")
@CrossOrigin(origins = "http://localhost:5173")
public class PlayerImageController {

    private final SoFifaClient soFifaClient;
    private final PlayerImageService playerImageService;

    public PlayerImageController(SoFifaClient soFifaClient, PlayerImageService playerImageService) {
        this.soFifaClient = soFifaClient;
        this.playerImageService = playerImageService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPlayerWithImage(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int index) {

        // 1. CRSet für Spieler-Daten
        var players = soFifaClient.searchPlayers(query);

        if (players.isEmpty() || index >= players.size()) {
            return ResponseEntity.notFound().build();
        }

        // 2. Ausgewählten Spieler mit Bild anreichern
        var player = players.get(index);
        var enriched = playerImageService.enrichWithImage(player);

        return ResponseEntity.ok(enriched);
    }
}