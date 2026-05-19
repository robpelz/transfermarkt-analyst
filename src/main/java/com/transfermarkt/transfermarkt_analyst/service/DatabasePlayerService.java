package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class DatabasePlayerService {

    private static final Logger log = LoggerFactory.getLogger(DatabasePlayerService.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabasePlayerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Holt einen Spieler aus deiner eigenen Datenbank (inkl. Alter + Marktwert)
     */
    public SoFifaPlayer getPlayerById(String playerId) {
        log.info("📊 Eigene DB: Suche Spieler mit ID: {}", playerId);

        try {
            String profileSql = """
                SELECT player_id, player_name, position, date_of_birth, citizenship, 
                       current_club_name, current_club_id
                FROM player_profiles 
                WHERE player_id = ?
            """;

            Map<String, Object> profile = jdbcTemplate.queryForMap(profileSql, playerId);

            String marketValueSql = """
                SELECT wert FROM player_market_values 
                WHERE player_id = ? 
                ORDER BY date_unix DESC 
                LIMIT 1
            """;

            Long marketValue = null;
            try {
                String marketValueStr = jdbcTemplate.queryForObject(marketValueSql, String.class, playerId);
                if (marketValueStr != null && !marketValueStr.isEmpty()) {
                    marketValueStr = marketValueStr.replaceAll("\\.0$", "");
                    marketValue = Long.parseLong(marketValueStr);
                }
            } catch (Exception e) {
                log.warn("Kein Marktwert für Spieler {} gefunden", playerId);
            }

            SoFifaPlayer player = new SoFifaPlayer();
            player.setId(playerId);  // String, nicht int
            player.setName((String) profile.get("player_name"));
            player.setNationality((String) profile.get("citizenship"));
            player.setClub((String) profile.get("current_club_name"));
            player.setPositions(List.of((String) profile.get("position")));

            // Alter berechnen
            Object birthDateObj = profile.get("date_of_birth");
            int age = 0;
            if (birthDateObj != null) {
                String birthDateStr = birthDateObj.toString();
                if (!birthDateStr.isEmpty()) {
                    try {
                        LocalDate birthDate = LocalDate.parse(birthDateStr);
                        age = Period.between(birthDate, LocalDate.now()).getYears();
                    } catch (Exception e) {
                        log.warn("Datum nicht parsbar: {}", birthDateStr);
                    }
                }
            }
            player.setAge(age);

            if (marketValue != null) {
                player.setValue(formatMarketValue(marketValue));
            } else {
                player.setValue("?");
            }

            log.info("✅ Spieler aus eigener DB geladen: {} (Alter: {}, Wert: {})",
                    player.getName(), player.getAge(), player.getValue());
            return player;

        } catch (Exception e) {
            log.error("❌ Fehler beim Laden aus eigener DB für ID {}: {}", playerId, e.getMessage());
            return null;
        }
    }

    /**
     * Sucht Spieler nach Namen (mit Marktwert)
     */
    public List<SoFifaPlayer> searchPlayers(String playerName) {
        log.info("📊 Eigene DB: Suche nach: {}", playerName);

        String profileSql = """
            SELECT player_id, player_name, position, date_of_birth, citizenship, current_club_name
            FROM player_profiles 
            WHERE LOWER(player_name) LIKE LOWER(?)
            LIMIT 20
        """;

        List<SoFifaPlayer> results = new ArrayList<>();

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(profileSql, "%" + playerName + "%");

            for (Map<String, Object> row : rows) {
                SoFifaPlayer player = new SoFifaPlayer();

                Object playerIdObj = row.get("player_id");
                if (playerIdObj == null) continue;

                String playerId = playerIdObj.toString();
                player.setId(playerId);  // String, nicht int

                player.setName(row.get("player_name") != null ? row.get("player_name").toString() : "?");
                player.setNationality(row.get("citizenship") != null ? row.get("citizenship").toString() : "?");
                player.setClub(row.get("current_club_name") != null ? row.get("current_club_name").toString() : "?");
                player.setPositions(List.of(row.get("position") != null ? row.get("position").toString() : "?"));

                // Alter berechnen
                Object birthObj = row.get("date_of_birth");
                int age = 0;
                if (birthObj != null && !birthObj.toString().isEmpty()) {
                    try {
                        LocalDate birth = LocalDate.parse(birthObj.toString());
                        age = Period.between(birth, LocalDate.now()).getYears();
                    } catch (Exception e) {
                        log.warn("Datum nicht parsbar: {}", birthObj.toString());
                    }
                }
                player.setAge(age);
                player.setValue("?");

                results.add(player);
            }
        } catch (Exception e) {
            log.error("Fehler in searchPlayers: {}", e.getMessage());
            e.printStackTrace();
        }

        log.info("✅ {} Spieler in eigener DB gefunden", results.size());
        return results;
    }

    /**
     * Formatiert Marktwert für Anzeige (z.B. 140 Mio. €)
     */
    private String formatMarketValue(Long valueInEuro) {
        if (valueInEuro == null) return "?";

        if (valueInEuro >= 1_000_000_000) {
            return String.format("%.1f Mrd. €", valueInEuro / 1_000_000_000.0);
        } else if (valueInEuro >= 1_000_000) {
            return String.format("%.0f Mio. €", valueInEuro / 1_000_000.0);
        } else if (valueInEuro >= 1_000) {
            return String.format("%.0f Tsd. €", valueInEuro / 1000.0);
        } else {
            return valueInEuro + " €";
        }
    }
}