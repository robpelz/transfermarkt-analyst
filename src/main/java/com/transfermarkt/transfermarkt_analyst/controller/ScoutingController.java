package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import com.transfermarkt.transfermarkt_analyst.service.ScoutingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scouting")
@CrossOrigin(origins = "http://localhost:5173")
public class ScoutingController {

    private final ScoutingService scoutingService;

    public ScoutingController(ScoutingService scoutingService) {
        this.scoutingService = scoutingService;
    }

    @PostMapping("/{playerId}")
    public ResponseEntity<Scouting> addScouting(@PathVariable String playerId,
                                                @RequestParam String playerName,
                                                @RequestParam(required = false) Integer rating,
                                                @RequestParam(required = false) String note) {
        return ResponseEntity.ok(scoutingService.addScouting(playerId, playerName, rating, note));
    }

    @GetMapping
    public ResponseEntity<List<Scouting>> getAllScouting() {
        return ResponseEntity.ok(scoutingService.getAllScouting());
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Scouting> getScoutingByPlayerId(@PathVariable String playerId) {
        Scouting scouting = scoutingService.getScoutingByPlayerId(playerId);
        return scouting != null ? ResponseEntity.ok(scouting) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<Scouting> updateScouting(@PathVariable String playerId,
                                                   @RequestParam(required = false) Integer rating,
                                                   @RequestParam(required = false) String note) {
        return ResponseEntity.ok(scoutingService.updateScouting(playerId, rating, note));
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deleteScouting(@PathVariable String playerId) {
        scoutingService.deleteScouting(playerId);
        return ResponseEntity.noContent().build();
    }
}
