package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.service.DatabasePlayerService;
import com.transfermarkt.transfermarkt_analyst.service.ScoreService;
import com.transfermarkt.transfermarkt_analyst.service.SoFifaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sofifa")
@CrossOrigin(origins = "http://localhost:5173")
public class SoFifaController {

    private static final Logger log = LoggerFactory.getLogger(SoFifaController.class);
    private final SoFifaClient soFifaClient;
    private final DatabasePlayerService databasePlayerService;
    private final JdbcTemplate jdbcTemplate;
    private final ScoreService scoreService;

    public SoFifaController(SoFifaClient soFifaClient,
                            DatabasePlayerService databasePlayerService,
                            JdbcTemplate jdbcTemplate,
                            ScoreService scoreService) {
        this.soFifaClient = soFifaClient;
        this.databasePlayerService = databasePlayerService;
        this.jdbcTemplate = jdbcTemplate;
        this.scoreService = scoreService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SoFifaPlayer>> search(@RequestParam String query) {
        log.info("📡 Suche nach: {}", query);
        List<SoFifaPlayer> players = soFifaClient.searchPlayers(query);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        boolean available = soFifaClient.isCrsetAvailable();
        return ResponseEntity.ok(available ? "✅ CRSet verbunden" : "❌ CRSet nicht erreichbar");
    }

    private String formatMarketValue(Long valueInEuro) {
        if (valueInEuro == null) return "?";
        double valueInMio = valueInEuro / 1_000_000.0;
        if (valueInMio >= 1000) {
            return String.format("%.0f Mrd €", valueInMio / 1000);
        } else {
            return String.format("%.0f Mio €", valueInMio);
        }
    }

    @GetMapping("/full-search")
    public ResponseEntity<List<Map<String, Object>>> fullSearch(@RequestParam String query) {
        log.info("🔍 Full-Suche nach: {}", query);

        String sql = "SELECT player_id, player_name, position, date_of_birth, citizenship, current_club_name FROM PLAYER_PROFILES WHERE LOWER(player_name) LIKE LOWER(?) LIMIT 20";

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, "%" + query + "%");

            for (Map<String, Object> row : results) {
                String playerId = String.valueOf(row.get("player_id"));

                String marketValueSql = "SELECT value FROM player_market_value WHERE player_id = ? ORDER BY date_unix DESC LIMIT 1";
                try {
                    String marketValue = jdbcTemplate.queryForObject(marketValueSql, String.class, playerId);
                    if (marketValue != null) {
                        Long valueLong = Long.parseLong(marketValue.replaceAll("\\.0$", ""));
                        row.put("market_value", formatMarketValue(valueLong));
                    } else {
                        row.put("market_value", "?");
                    }
                } catch (Exception e) {
                    row.put("market_value", "?");
                }

                Object birthObj = row.get("date_of_birth");
                if (birthObj != null && !birthObj.toString().isEmpty()) {
                    try {
                        LocalDate birth = LocalDate.parse(birthObj.toString());
                        int age = Period.between(birth, LocalDate.now()).getYears();
                        row.put("age", age);
                    } catch (Exception e) {
                        row.put("age", 0);
                    }
                } else {
                    row.put("age", 0);
                }
                row.remove("date_of_birth");
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Fehler in fullSearch: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<Map<String, Object>> getPlayerByIdSimple(@PathVariable String id) {
        SoFifaPlayer player = databasePlayerService.getPlayerById(id);
        if (player == null) return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();
        response.put("id", player.getId());
        response.put("name", player.getName());
        response.put("age", player.getAge());
        response.put("value", player.getValue());
        response.put("club", player.getClub());
        response.put("nationality", player.getNationality());
        response.put("position", player.getPositions().get(0));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/player/{id}/market-value")
    public ResponseEntity<Map<String, String>> getPlayerMarketValue(@PathVariable String id) {
        String sql = "SELECT value FROM player_market_value WHERE player_id = ? ORDER BY date_unix DESC LIMIT 1";
        try {
            String value = jdbcTemplate.queryForObject(sql, String.class, id);
            if (value != null) {
                Long valueLong = Long.parseLong(value.replaceAll("\\.0$", ""));
                double valueInMio = valueLong / 1_000_000.0;
                String formatted = valueInMio >= 1000 ? String.format("%.0f Mrd €", valueInMio / 1000) : String.format("%.0f Mio €", valueInMio);
                return ResponseEntity.ok(Map.of("market_value", formatted));
            }
        } catch (Exception e) {}
        return ResponseEntity.ok(Map.of("market_value", "?"));
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> comparePlayers(@RequestParam String player1Id, @RequestParam String player2Id) {
        SoFifaPlayer player1 = databasePlayerService.getPlayerById(player1Id);
        SoFifaPlayer player2 = databasePlayerService.getPlayerById(player2Id);
        if (player1 == null || player2 == null) return ResponseEntity.notFound().build();

        Map<String, Object> result = new HashMap<>();
        result.put("player1", Map.of(
                "id", player1.getId(), "name", player1.getName(), "age", player1.getAge(),
                "value", player1.getValue(), "club", player1.getClub(),
                "nationality", player1.getNationality(), "position", player1.getPrimaryPosition()
        ));
        result.put("player2", Map.of(
                "id", player2.getId(), "name", player2.getName(), "age", player2.getAge(),
                "value", player2.getValue(), "club", player2.getClub(),
                "nationality", player2.getNationality(), "position", player2.getPrimaryPosition()
        ));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/player/{id}/score")
    public ResponseEntity<Map<String, Object>> getPlayerScore(@PathVariable String id, @RequestParam(required = false) String club) {
        SoFifaPlayer player = databasePlayerService.getPlayerById(id);
        if (player == null) return ResponseEntity.notFound().build();

        int ageScore = calculateAgeScore(player.getAge());
        int priceScore = calculatePriceScore(player.getValue());
        int positionScore = calculatePositionScore(player.getPrimaryPosition());
        int totalScore = (ageScore + priceScore + positionScore + 70 + 75) / 5;

        Map<String, Object> score = new HashMap<>();
        score.put("totalScore", totalScore);
        score.put("ageScore", ageScore);
        score.put("priceScore", priceScore);
        score.put("positionScore", positionScore);
        score.put("experienceScore", 70);
        score.put("competitionScore", 75);
        score.put("recommendation", getRecommendation(totalScore));
        return ResponseEntity.ok(score);
    }

    private int calculateAgeScore(int age) {
        if (age <= 0) return 50;
        if (age < 23) return 95;
        if (age < 26) return 85;
        if (age < 29) return 70;
        if (age < 32) return 50;
        return 30;
    }

    private int calculatePriceScore(String valueStr) {
        if (valueStr == null || valueStr.equals("?")) return 50;
        try {
            double value = Double.parseDouble(valueStr.replace(" €", "").replace(" Mio.", "").trim());
            if (value >= 100) return 90;
            if (value >= 50) return 75;
            if (value >= 20) return 60;
            if (value >= 10) return 45;
            return 30;
        } catch (Exception e) {
            return 50;
        }
    }

    private int calculatePositionScore(String position) {
        if (position == null) return 50;
        String posLower = position.toLowerCase();
        if (posLower.contains("attack")) return 85;
        if (posLower.contains("midfield")) return 80;
        if (posLower.contains("defender")) return 75;
        return 60;
    }

    private String getRecommendation(int score) {
        if (score >= 85) return "🔥 Top-Transfer – sofort zuschlagen!";
        if (score >= 70) return "✅ Gutes Investment – empfehlenswert";
        if (score >= 55) return "⚠️ Solide, aber kein Schnäppchen";
        return "❌ Zu riskant – lieber nicht";
    }

    @GetMapping("/player/name/{name}")
    public ResponseEntity<SoFifaPlayer> getPlayerByName(@PathVariable String name) {
        List<SoFifaPlayer> players = databasePlayerService.searchPlayers(name);
        if (players == null || players.isEmpty()) return ResponseEntity.notFound().build();
        SoFifaPlayer player = players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(players.get(0));
        return ResponseEntity.ok(player);
    }

    @GetMapping("/player-image/{name}")
    public ResponseEntity<Map<String, String>> getPlayerImage(@PathVariable String name) {
        Map<String, String> response = new HashMap<>();
        try {
            String cleanName = name.replaceAll(" \\(\\d+\\)", "");
            String url = "https://www.thesportsdb.com/api/v1/json/1/searchplayers.php?p=" + cleanName;
            var result = new org.springframework.web.client.RestTemplate().getForObject(url, Map.class);
            if (result != null && result.containsKey("player")) {
                var players = (java.util.List<Map<String, Object>>) result.get("player");
                if (players != null && !players.isEmpty()) {
                    String imageUrl = (String) players.get(0).get("strCutout");
                    if (imageUrl == null) imageUrl = (String) players.get(0).get("strThumb");
                    response.put("imageUrl", imageUrl);
                    return ResponseEntity.ok(response);
                }
            }
        } catch (Exception e) {
            log.error("Bild-Fehler: {}", e.getMessage());
        }
        response.put("imageUrl", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clubId}/players")
    public ResponseEntity<List<Map<String, Object>>> getClubPlayers(@PathVariable String clubId) {
        String sql = "SELECT p.player_id as id, p.player_name as name, p.position, p.date_of_birth, p.citizenship as nationality, (SELECT value FROM player_market_value mv WHERE CAST(mv.player_id AS TEXT) = p.player_id ORDER BY mv.date_unix DESC LIMIT 1) as market_value FROM player_profiles p WHERE p.current_club_id = ? ORDER BY p.player_name LIMIT 50";
        try {
            List<Map<String, Object>> players = jdbcTemplate.queryForList(sql, clubId);
            for (Map<String, Object> player : players) {
                Object birthObj = player.get("date_of_birth");
                if (birthObj != null && !birthObj.toString().isEmpty()) {
                    try {
                        LocalDate birth = LocalDate.parse(birthObj.toString());
                        int age = Period.between(birth, LocalDate.now()).getYears();
                        player.put("age", age);
                    } catch (Exception e) { player.put("age", 0); }
                } else { player.put("age", 0); }
                player.remove("date_of_birth");

                Object mvObj = player.get("market_value");
                if (mvObj != null && !mvObj.toString().isEmpty()) {
                    try {
                        Long valueLong = Long.parseLong(mvObj.toString().replaceAll("\\.0$", ""));
                        player.put("market_value", formatMarketValue(valueLong));
                    } catch (Exception e) {
                        player.put("market_value", "?");
                    }
                } else {
                    player.put("market_value", "?");
                }
            }
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/teams/by-id/{clubId}")
    public ResponseEntity<Map<String, Object>> getTeamById(@PathVariable String clubId) {
        String sql = "SELECT club_id as id, club_name as name, logo_url as logo, country_name as country FROM TEAMS_DETAILS WHERE club_id = ?";
        try {
            Map<String, Object> team = jdbcTemplate.queryForMap(sql, clubId);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}