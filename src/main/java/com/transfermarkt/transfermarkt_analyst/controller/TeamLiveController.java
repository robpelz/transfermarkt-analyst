package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.service.TheSportsDbClient;
import com.transfermarkt.transfermarkt_analyst.service.TeamLogoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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

    private String formatMarketValue(Long valueInEuro) {
        if (valueInEuro == null) return "?";
        double valueInMio = valueInEuro / 1_000_000.0;
        if (valueInMio >= 1000) {
            return String.format("%.0f Mrd €", valueInMio / 1000);
        } else {
            return String.format("%.0f Mio €", valueInMio);
        }
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
                addTeam(teams, "631", "Chelsea", "https://tmssl.akamaized.net//images/wappen/head/631.png");
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
                addTeam(teams, "79", "VfB Stuttgart", "https://tmssl.akamaized.net//images/wappen/head/79.png");
                addTeam(teams, "533", "TSG Hoffenheim", "https://tmssl.akamaized.net//images/wappen/head/533.png");
                addTeam(teams, "60", "SC Freiburg", "https://tmssl.akamaized.net//images/wappen/head/60.png");
                addTeam(teams, "39", "1. FSV Mainz 05", "https://tmssl.akamaized.net//images/wappen/head/39.png");
                addTeam(teams, "89", "1. FC Union Berlin", "https://tmssl.akamaized.net//images/wappen/head/89.png");
                addTeam(teams, "167", "FC Augsburg", "https://tmssl.akamaized.net//images/wappen/head/167.png");
                addTeam(teams, "41", "Hamburger SV", "https://tmssl.akamaized.net//images/wappen/head/41.png");
                addTeam(teams, "18", "Borussia Mönchengladbach", "https://tmssl.akamaized.net//images/wappen/head/18.png");
                addTeam(teams, "86", "Werder Bremen", "https://tmssl.akamaized.net//images/wappen/head/86.png");
                addTeam(teams, "3", "1. FC Köln", "https://tmssl.akamaized.net//images/wappen/head/3.png");
                addTeam(teams, "35", "FC St. Pauli", "https://tmssl.akamaized.net//images/wappen/head/35.png");
                addTeam(teams, "82", "VfL Wolfsburg", "https://tmssl.akamaized.net//images/wappen/head/82.png");
                addTeam(teams, "2036", "1. FC Heidenheim", "https://tmssl.akamaized.net//images/wappen/head/2036.png");
                break;

            case "Serie A":
                addTeam(teams, "5", "AC Milan", "https://tmssl.akamaized.net//images/wappen/head/5.png");
                addTeam(teams, "46", "Inter Milan", "https://tmssl.akamaized.net//images/wappen/head/46.png");
                addTeam(teams, "506", "Juventus", "https://tmssl.akamaized.net//images/wappen/head/506.png");
                addTeam(teams, "12", "AS Roma", "https://tmssl.akamaized.net//images/wappen/head/12.png");
                addTeam(teams, "6195", "SSC Napoli", "https://tmssl.akamaized.net//images/wappen/head/6195.png");
                addTeam(teams, "800", "Atalanta", "https://tmssl.akamaized.net//images/wappen/head/800.png");
                addTeam(teams, "1025", "Bologna", "https://tmssl.akamaized.net//images/wappen/head/1025.png");
                addTeam(teams, "398", "Lazio", "https://tmssl.akamaized.net//images/wappen/head/398.png");
                addTeam(teams, "1047", "Como", "https://tmssl.akamaized.net//images/wappen/head/1047.png");
                addTeam(teams, "6574", "Sassuolo", "https://tmssl.akamaized.net//images/wappen/head/6574.png");
                addTeam(teams, "410", "Udinese", "https://tmssl.akamaized.net//images/wappen/head/410.png");
                addTeam(teams, "416", "Torino", "https://tmssl.akamaized.net//images/wappen/head/416.png");
                addTeam(teams, "130", "Parma", "https://tmssl.akamaized.net//images/wappen/head/130.png");
                addTeam(teams, "252", "Genoa", "https://tmssl.akamaized.net//images/wappen/head/252.png");
                addTeam(teams, "430", "Fiorentina", "https://tmssl.akamaized.net//images/wappen/head/430.png");
                addTeam(teams, "1390", "Cagliari", "https://tmssl.akamaized.net//images/wappen/head/1390.png");
                addTeam(teams, "2239", "Cremonese", "https://tmssl.akamaized.net//images/wappen/head/2239.png");
                addTeam(teams, "1005", "Lecce", "https://tmssl.akamaized.net//images/wappen/head/1005.png");
                addTeam(teams, "276", "Hellas Verona", "https://tmssl.akamaized.net//images/wappen/head/276.png");
                addTeam(teams, "4172", "Pisa", "https://tmssl.akamaized.net//images/wappen/head/4172.png");
                break;

            case "La Liga":
                addTeam(teams, "418", "Real Madrid", "https://tmssl.akamaized.net//images/wappen/head/418.png");
                addTeam(teams, "13", "Atlético Madrid", "https://tmssl.akamaized.net//images/wappen/head/13.png");
                addTeam(teams, "368", "Sevilla", "https://tmssl.akamaized.net//images/wappen/head/368.png");
                addTeam(teams, "681", "Real Sociedad", "https://tmssl.akamaized.net//images/wappen/head/681.png");
                addTeam(teams, "621", "Athletic Bilbao", "https://tmssl.akamaized.net//images/wappen/head/621.png");
                addTeam(teams, "331", "Osasuna", "https://tmssl.akamaized.net//images/wappen/head/331.png");
                addTeam(teams, "1050", "Villarreal", "https://tmssl.akamaized.net//images/wappen/head/1050.png");
                addTeam(teams, "1049", "Valencia", "https://tmssl.akamaized.net//images/wappen/head/1049.png");
                addTeam(teams, "367", "Rayo Vallecano", "https://tmssl.akamaized.net//images/wappen/head/367.png");
                addTeam(teams, "1244", "Leganés", "https://tmssl.akamaized.net//images/wappen/head/1244.png");
                addTeam(teams, "472", "Las Palmas", "https://tmssl.akamaized.net//images/wappen/head/472.png");
                addTeam(teams, "940", "Celta Vigo", "https://tmssl.akamaized.net//images/wappen/head/940.png");
                addTeam(teams, "1108", "Alavés", "https://tmssl.akamaized.net//images/wappen/head/1108.png");
                addTeam(teams, "1531", "Elche", "https://tmssl.akamaized.net//images/wappen/head/1531.png");
                addTeam(teams, "237", "Mallorca", "https://tmssl.akamaized.net//images/wappen/head/237.png");
                addTeam(teams, "366", "Real Valladolid", "https://tmssl.akamaized.net//images/wappen/head/366.png");
                addTeam(teams, "3302", "Almería", "https://tmssl.akamaized.net//images/wappen/head/3302.png");
                addTeam(teams, "1084", "Málaga", "https://tmssl.akamaized.net//images/wappen/head/1084.png");
                addTeam(teams, "3368", "Levante", "https://tmssl.akamaized.net//images/wappen/head/3368.png");
                addTeam(teams, "5358", "Huesca", "https://tmssl.akamaized.net//images/wappen/head/5358.png");
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
    public ResponseEntity<List<com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.TheSportsDbLeague>> getTopLeaguesOld() {
        return ResponseEntity.ok(theSportsDbClient.getTopLeagues());
    }

    @GetMapping("/league/{leagueName}")
    public ResponseEntity<List<com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.TheSportsDbTeam>> getTeamsByLeague(@PathVariable String leagueName) {
        List<com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.TheSportsDbTeam> teams = theSportsDbClient.getTeamsByLeague(leagueName);
        for (com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.TheSportsDbTeam team : teams) {
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
        String sql = "SELECT p.player_id as id, p.player_name as name, p.position, p.date_of_birth, p.citizenship as nationality, MAX(mv.value) " +
                "as market_value FROM player_profiles p LEFT JOIN player_market_value mv ON CAST(mv.player_id AS TEXT) = p.player_id WHERE p.current_club_id = ? " +
                "GROUP BY p.player_id ORDER BY p.player_name LIMIT 50";
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
}