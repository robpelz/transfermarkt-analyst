package com.transfermark.transfermarkt_analyst.controller;

import com.transfermark.transfermarkt_analyst.dto.ClubStatistics;
import com.transfermark.transfermarkt_analyst.service.ClubStatisticsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/club-statistics")
public class ClubStatisticsController {

    private final ClubStatisticsService statisticsService;

    public ClubStatisticsController(ClubStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{clubName}")
    public ClubStatistics getClubStatistics(@PathVariable String clubName) {
        return statisticsService.getClubStatistics(clubName);
    }

    @GetMapping("/top/{limit}")
    public List<ClubStatistics> getTopClubs(@PathVariable int limit) {
        return statisticsService.getTopClubs(limit);
    }
}