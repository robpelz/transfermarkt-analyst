package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.TheSportsDbLeague;
import com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.TheSportsDbTeam;
import com.transfermarkt.transfermarkt_analyst.service.TheSportsDbClient;
import com.transfermarkt.transfermarkt_analyst.service.TeamLogoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/live/teams")
@CrossOrigin(origins = "http://localhost:5173")
public class TeamLiveController {

    private static final Logger log = LoggerFactory.getLogger(TeamLiveController.class);
    private final TheSportsDbClient theSportsDbClient;
    private final TeamLogoMapper teamLogoMapper;
    private final JdbcTemplate jdbcTemplate;

    public TeamLiveController(TheSportsDbClient theSportsDbClient,
                              TeamLogoMapper teamLogoMapper,
                              JdbcTemplate jdbcTemplate) {
        this.theSportsDbClient = theSportsDbClient;
        this.teamLogoMapper = teamLogoMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private void addTeam(List<Map<String, Object>> teams, String id, String name, String logoUrl) {
        Map<String, Object> team = new HashMap<>();
        team.put("id", id);
        team.put("name", name);
        team.put("logo", logoUrl);
        teams.add(team);
    }

    @GetMapping("/top-leagues")
    public ResponseEntity<List<Map<String, String>>> getTopLeagues() {
        List<Map<String, String>> leagues = List.of(
                Map.of("id", "1", "name", "Premier League", "country", "England"),
                Map.of("id", "2", "name", "Bundesliga", "country", "Germany"),
                Map.of("id", "3", "name", "Serie A", "country", "Italy"),
                Map.of("id", "4", "name", "La Liga", "country", "Spain")
        );
        return ResponseEntity.ok(leagues);
    }

    @GetMapping("/teams/by-league")
    public ResponseEntity<List<Map<String, Object>>> getTeamsByLeagueFromDB(@RequestParam String league) {
        List<Map<String, Object>> teams = new ArrayList<>();

        switch (league) {
            case "Premier League":
                addTeam(teams, "11", "Arsenal", "https://tmssl.akamaized.net//images/wappen/head/11.png");
                addTeam(teams, "281", "Manchester City", "https://tmssl.akamaized.net//images/wappen/head/281.png");
                addTeam(teams, "31", "Liverpool", "https://tmssl.akamaized.net//images/wappen/head/31.png");
                addTeam(teams, "535", "Chelsea", "https://tmssl.akamaized.net//images/wappen/head/535.png");
                addTeam(teams, "985", "Manchester United", "https://tmssl.akamaized.net//images/wappen/head/985.png");
                addTeam(teams, "148", "Tottenham", "https://tmssl.akamaized.net//images/wappen/head/148.png");
                addTeam(teams, "762", "Newcastle", "https://tmssl.akamaized.net//images/wappen/head/762.png");
                addTeam(teams, "379", "West Ham", "https://tmssl.akamaized.net//images/wappen/head/379.png");
                addTeam(teams, "989", "Bournemouth", "https://tmssl.akamaized.net//images/wappen/head/989.png");
                addTeam(teams, "405", "Aston Villa", "https://tmssl.akamaized.net//images/wappen/head/405.png");
                addTeam(teams, "289", "Sunderland", "https://tmssl.akamaized.net//images/wappen/head/289.png");
                addTeam(teams, "931", "Fulham", "https://tmssl.akamaized.net//images/wappen/head/931.png");
                addTeam(teams, "1148", "Brentford", "https://tmssl.akamaized.net//images/wappen/head/1148.png");
                addTeam(teams, "873", "Crystal Palace", "https://tmssl.akamaized.net//images/wappen/head/873.png");
                addTeam(teams, "1132", "Burnley", "https://tmssl.akamaized.net//images/wappen/head/1132.png");
                addTeam(teams, "543", "Wolverhampton", "https://tmssl.akamaized.net//images/wappen/head/543.png");
                addTeam(teams, "1237", "Brighton", "https://tmssl.akamaized.net//images/wappen/head/1237.png");
                addTeam(teams, "399", "Leeds", "https://tmssl.akamaized.net//images/wappen/head/399.png");
                addTeam(teams, "703", "Nottingham", "https://tmssl.akamaized.net//images/wappen/head/703.png");
                addTeam(teams, "29", "Everton", "https://tmssl.akamaized.net//images/wappen/head/29.png");
                break;

            case "Bundesliga":
                addTeam(teams, "27", "Bayern Munich", "https://tmssl.akamaized.net//images/wappen/head/27.png");
                addTeam(teams, "16", "Borussia Dortmund", "https://tmssl.akamaized.net//images/wappen/head/16.png");
                addTeam(teams, "15", "Bayer Leverkusen", "https://tmssl.akamaized.net//images/wappen/head/15.png");
                addTeam(teams, "23826", "RB Leipzig", "https://tmssl.akamaized.net//images/wappen/head/23826.png");
                addTeam(teams, "24", "Eintracht Frankfurt", "https://tmssl.akamaized.net//images/wappen/head/24.png");
                addTeam(teams, "17", "Stuttgart", "https://tmssl.akamaized.net//images/wappen/head/17.png");
                addTeam(teams, "1493", "Hoffenheim", "https://tmssl.akamaized.net//images/wappen/head/1493.png");
                addTeam(teams, "60", "Freiburg", "https://tmssl.akamaized.net//images/wappen/head/60.png");
                addTeam(teams, "639", "Mainz", "https://tmssl.akamaized.net//images/wappen/head/639.png");
                addTeam(teams, "2958", "Union Berlin", "https://tmssl.akamaized.net//images/wappen/head/2958.png");
                addTeam(teams, "1679", "FC Augsburg", "https://tmssl.akamaized.net//images/wappen/head/1679.png");
                addTeam(teams, "41", "Hamburg", "https://tmssl.akamaized.net//images/wappen/head/41.png");
                addTeam(teams, "18", "Borussia Mönchengladbach", "https://tmssl.akamaized.net//images/wappen/head/18.png");
                addTeam(teams, "35", "Werder Bremen", "https://tmssl.akamaized.net//images/wappen/head/35.png");
                addTeam(teams, "5", "FC Köln", "https://tmssl.akamaized.net//images/wappen/head/5.png");
                addTeam(teams, "54", "St Pauli", "https://tmssl.akamaized.net//images/wappen/head/54.png");
                addTeam(teams, "82", "Wolfsburg", "https://tmssl.akamaized.net//images/wappen/head/82.png");
                addTeam(teams, "11827", "FC Heidenheim", "https://tmssl.akamaized.net//images/wappen/head/11827.png");
                break;

            case "Serie A":
                addTeam(teams, "5", "AC Milan", "https://tmssl.akamaized.net//images/wappen/head/5.png");
                addTeam(teams, "46", "Inter Milan", "https://tmssl.akamaized.net//images/wappen/head/46.png");
                addTeam(teams, "506", "Juventus", "https://tmssl.akamaized.net//images/wappen/head/506.png");
                addTeam(teams, "12", "Roma", "https://tmssl.akamaized.net//images/wappen/head/12.png");
                addTeam(teams, "619", "Napoli", "https://tmssl.akamaized.net//images/wappen/head/619.png");
                addTeam(teams, "43", "Atalanta", "https://tmssl.akamaized.net//images/wappen/head/43.png");
                addTeam(teams, "7", "Bologna", "https://tmssl.akamaized.net//images/wappen/head/7.png");
                addTeam(teams, "497", "Lazio", "https://tmssl.akamaized.net//images/wappen/head/497.png");
                addTeam(teams, "701", "Como", "https://tmssl.akamaized.net//images/wappen/head/701.png");
                addTeam(teams, "26371", "Sassuolo", "https://tmssl.akamaized.net//images/wappen/head/26371.png");
                addTeam(teams, "428", "Udinese", "https://tmssl.akamaized.net//images/wappen/head/428.png");
                addTeam(teams, "416", "Torino", "https://tmssl.akamaized.net//images/wappen/head/416.png");
                addTeam(teams, "130", "Parma", "https://tmssl.akamaized.net//images/wappen/head/130.png");
                addTeam(teams, "252", "Genoa", "https://tmssl.akamaized.net//images/wappen/head/252.png");
                addTeam(teams, "452", "Fiorentina", "https://tmssl.akamaized.net//images/wappen/head/452.png");
                addTeam(teams, "472", "Cagliari", "https://tmssl.akamaized.net//images/wappen/head/472.png");
                addTeam(teams, "1082", "Cremonese", "https://tmssl.akamaized.net//images/wappen/head/1082.png");
                addTeam(teams, "749", "Lecce", "https://tmssl.akamaized.net//images/wappen/head/749.png");
                addTeam(teams, "439", "Hellas Verona", "https://tmssl.akamaized.net//images/wappen/head/439.png");
                addTeam(teams, "423", "Pisa", "https://tmssl.akamaized.net//images/wappen/head/423.png");
                break;

            case "La Liga":
                addTeam(teams, "28", "Real Madrid", "https://tmssl.akamaized.net//images/wappen/head/28.png");
                addTeam(teams, "131", "Barcelona", "https://tmssl.akamaized.net//images/wappen/head/131.png");
                addTeam(teams, "13", "Atlético Madrid", "https://tmssl.akamaized.net//images/wappen/head/13.png");
                addTeam(teams, "368", "Sevilla", "https://tmssl.akamaized.net//images/wappen/head/368.png");
                addTeam(teams, "30", "Real Sociedad", "https://tmssl.akamaized.net//images/wappen/head/30.png");
                addTeam(teams, "621", "Athletic Bilbao", "https://tmssl.akamaized.net//images/wappen/head/621.png");
                addTeam(teams, "331", "Osasuna", "https://tmssl.akamaized.net//images/wappen/head/331.png");
                addTeam(teams, "1050", "Villarreal", "https://tmssl.akamaized.net//images/wappen/head/1050.png");
                addTeam(teams, "1049", "Valencia", "https://tmssl.akamaized.net//images/wappen/head/1049.png");
                addTeam(teams, "2869", "Betis", "https://tmssl.akamaized.net//images/wappen/head/2869.png");
                addTeam(teams, "1244", "Leganés", "https://tmssl.akamaized.net//images/wappen/head/1244.png");
                addTeam(teams, "3700", "Rayo Vallecano", "https://tmssl.akamaized.net//images/wappen/head/3700.png");
                addTeam(teams, "648", "Tenerife", "https://tmssl.akamaized.net//images/wappen/head/648.png");
                addTeam(teams, "2502", "Castellón", "https://tmssl.akamaized.net//images/wappen/head/2502.png");
                addTeam(teams, "1532", "Albacete", "https://tmssl.akamaized.net//images/wappen/head/1532.png");
                addTeam(teams, "12567", "Eldense", "https://tmssl.akamaized.net//images/wappen/head/12567.png");
                addTeam(teams, "11000", "Lugo", "https://tmssl.akamaized.net//images/wappen/head/11000.png");
                addTeam(teams, "13222", "Mirandés", "https://tmssl.akamaized.net//images/wappen/head/13222.png");
                addTeam(teams, "2296", "Numancia", "https://tmssl.akamaized.net//images/wappen/head/2296.png");
                addTeam(teams, "1543", "Eibar", "https://tmssl.akamaized.net//images/wappen/head/1543.png");
                break;

            default:
                break;
        }
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/by-league")
    public ResponseEntity<List<Map<String, Object>>> getTeamsByLeagueShort(@RequestParam String league) {
        return getTeamsByLeagueFromDB(league);
    }

    @GetMapping("/leagues")
    public ResponseEntity<List<TheSportsDbLeague>> getTopLeaguesOld() {
        return ResponseEntity.ok(theSportsDbClient.getTopLeagues());
    }

    @GetMapping("/league/{leagueName}")
    public ResponseEntity<List<TheSportsDbTeam>> getTeamsByLeague(@PathVariable String leagueName) {
        List<TheSportsDbTeam> teams = theSportsDbClient.getTeamsByLeague(leagueName);
        for (TheSportsDbTeam team : teams) {
            String logoUrl = teamLogoMapper.getLogoUrl(team.getStrTeam());
            team.setLocalLogoUrl(logoUrl);
        }
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/by-id/{clubId}")
    public ResponseEntity<Map<String, Object>> getTeamById(@PathVariable String clubId) {
        log.info("🏟️ Lade Verein mit ID: {}", clubId);
        String sql = "SELECT club_id as id, club_name as name, logo_url as logo, country_name as country FROM TEAMS_DETAILS WHERE club_id = ?";
        try {
            Map<String, Object> team = jdbcTemplate.queryForMap(sql, clubId);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            log.error("Verein mit ID {} nicht gefunden", clubId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{clubId}/players")
    public ResponseEntity<List<Map<String, Object>>> getClubPlayers(@PathVariable String clubId) {
        log.info("📋 Lade Kader für Verein mit ID: {}", clubId);

        String sql = """
        SELECT 
            p.player_id as id, 
            p.player_name as name, 
            p.position, 
            p.date_of_birth,
            p.citizenship as nationality,
            (SELECT value FROM player_market_values mv
             WHERE mv.player_id = p.player_id ORDER BY mv.date_unix DESC LIMIT 1) as market_value
        FROM player_profiles p 
        WHERE p.current_club_id = ? 
        ORDER BY p.player_name 
        LIMIT 50
    """;

        try {
            List<Map<String, Object>> players = jdbcTemplate.queryForList(sql, clubId);

            for (Map<String, Object> player : players) {
                Object birthObj = player.get("date_of_birth");
                if (birthObj != null && !birthObj.toString().isEmpty()) {
                    try {
                        java.time.LocalDate birth = java.time.LocalDate.parse(birthObj.toString());
                        int age = java.time.Period.between(birth, java.time.LocalDate.now()).getYears();
                        player.put("age", age);
                    } catch (Exception e) { player.put("age", 0); }
                } else { player.put("age", 0); }
                player.remove("date_of_birth");

                Object mvObj = player.get("market_value");
                if (mvObj != null && !mvObj.toString().isEmpty()) {
                    player.put("market_value", mvObj.toString().replaceAll("\\.0$", "") + " €");
                } else { player.put("market_value", "?"); }
            }

            log.info("✅ {} Spieler für Verein {} gefunden", players.size(), clubId);
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            log.error("Fehler beim Laden des Kaders für {}: {}", clubId, e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/player/{playerId}/market-value")
    public ResponseEntity<Map<String, String>> getPlayerMarketValue(@PathVariable String playerId) {
        String sql = "SELECT wert FROM PLAYER_MARKET_VALUES WHERE player_id = ? ORDER BY date_unix DESC LIMIT 1";
        try {
            String wert = jdbcTemplate.queryForObject(sql, String.class, playerId);
            return ResponseEntity.ok(Map.of("market_value", wert != null ? wert.replaceAll("\\.0$", "") + " €" : "?"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("market_value", "?"));
        }
    }
}