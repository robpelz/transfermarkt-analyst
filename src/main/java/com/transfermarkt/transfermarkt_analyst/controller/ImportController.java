package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.service.FootballDatasetImportService;
import com.transfermarkt.transfermarkt_analyst.service.MarketValueImportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final FootballDatasetImportService footballDatasetImportService;
    private final MarketValueImportService marketValueImportService;
    private final JdbcTemplate jdbcTemplate;  // ← neu

    public ImportController(FootballDatasetImportService footballDatasetImportService,
                            MarketValueImportService marketValueImportService,
                            JdbcTemplate jdbcTemplate) {  // ← neu
        this.footballDatasetImportService = footballDatasetImportService;
        this.marketValueImportService = marketValueImportService;
        this.jdbcTemplate = jdbcTemplate;  // ← neu
    }

    @PostMapping("/players")
    public String importPlayers() {
        footballDatasetImportService.importPlayerProfiles();
        return "✅ Spieler-Profile Import gestartet!";
    }

    @PostMapping("/market-values")
    public String importMarketValues() {
        marketValueImportService.importMarketValues();
        return "✅ Marktwerte Import gestartet!";
    }

    @PostMapping("/create-scouting-table")
    public String createScoutingTable() {
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS scouting (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    player_id VARCHAR(50) NOT NULL,
                    player_name VARCHAR(200),
                    rating INT CHECK (rating BETWEEN 1 AND 5),
                    note VARCHAR(500),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY uk_player (player_id)
                )
            """);
            return "✅ Scouting table created successfully!";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}