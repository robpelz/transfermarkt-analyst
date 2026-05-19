package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.service.SoFifaClient;
import com.transfermarkt.transfermarkt_analyst.service.TransferScoreService;
import com.transfermarkt.transfermarkt_analyst.repository.PlayerMarketDataRepository;
import com.transfermarkt.transfermarkt_analyst.model.PlayerMarketData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/live/players")
@CrossOrigin(origins = "http://localhost:5173")
public class PlayerLiveController {

    private final SoFifaClient soFifaClient;
    private final TransferScoreService transferScoreService;
    private final PlayerMarketDataRepository marketDataRepository;

    public PlayerLiveController(SoFifaClient soFifaClient, TransferScoreService transferScoreService,
                                PlayerMarketDataRepository marketDataRepository) {
        this.soFifaClient = soFifaClient;
        this.transferScoreService = transferScoreService;
        this.marketDataRepository = marketDataRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SoFifaPlayer>> searchPlayers(@RequestParam String query) {
        return ResponseEntity.ok(soFifaClient.searchPlayers(query));
    }

    @GetMapping("/score")
    public ResponseEntity<TransferScore> getPlayerScore(
            @RequestParam String name,
            @RequestParam(defaultValue = "Napoli") String club) {

        List<SoFifaPlayer> players = soFifaClient.searchPlayers(name);
        if (players.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TransferScore score = transferScoreService.calculateScore(players.get(0), club);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/test-score")
    public String testScore() {
        Optional<PlayerMarketData> data = marketDataRepository.findTopByPlayerNameOrderBySeasonDesc("Harry Kane");
        if (data.isPresent()) {
            return "Gefunden: " + data.get().getMarketValue();
        }
        return "Nicht gefunden";
    }
}