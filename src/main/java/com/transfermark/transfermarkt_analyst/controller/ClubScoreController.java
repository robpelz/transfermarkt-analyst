package com.transfermark.transfermarkt_analyst.controller;

import com.transfermark.transfermarkt_analyst.dto.ClubScore;
import com.transfermark.transfermarkt_analyst.service.ClubScoreService;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/club-score")
public class ClubScoreController {

    private final ClubScoreService clubScoreService;  // ← Jetzt das Interface!

    public ClubScoreController(ClubScoreService clubScoreService) {
        this.clubScoreService = clubScoreService;
    }

    @GetMapping("/{clubName}")
    public ClubScore getClubScore(@PathVariable String clubName) {
        return clubScoreService.calculateClubScore(clubName);
    }

    @GetMapping("/clubs/list")
    public List<String> getAvailableClubs() {
        return Arrays.asList(
                "Napoli", "Bayern München", "Borussia Dortmund",
                "Real Madrid", "Barcelona", "Chelsea",
                "Manchester City", "Paris Saint-Germain"
        );
    }

    @GetMapping("/{clubName}/has-data")
    public boolean hasEnoughData(@PathVariable String clubName) {
        return clubScoreService.hasEnoughData(clubName);
    }
}