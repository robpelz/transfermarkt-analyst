package com.transfermarkt.transfermarkt_analyst.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FootballDatasetImportService {

    private final JdbcTemplate jdbcTemplate;

    public FootballDatasetImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Erstellt die Tabellen, falls sie nicht existieren
     */
    public void createTables() {
        System.out.println("📋 Erstelle Tabellen...");

        String createPlayerProfiles = """
            CREATE TABLE IF NOT EXISTS player_profiles (
                player_id VARCHAR(50) PRIMARY KEY,
                player_name VARCHAR(200),
                position VARCHAR(100),
                date_of_birth DATE,
                citizenship VARCHAR(100),
                foot VARCHAR(20),
                current_club_id VARCHAR(50),
                current_club_name VARCHAR(200)
            )
        """;

        String createMarketValues = """
            CREATE TABLE IF NOT EXISTS player_market_values (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                player_id VARCHAR(50),
                date_unix BIGINT,
                wert BIGINT
            )
        """;

        jdbcTemplate.execute(createPlayerProfiles);
        jdbcTemplate.execute(createMarketValues);

        System.out.println("✅ Tabellen bereit");
    }

    /**
     * Importiert die Spieler-Profile (92.671 Spieler)
     */
    public void importPlayerProfiles() {
        String filepath = "C:/Users/49152/Desktop/java/transfermarkt-analyst/football-datasets/datalake/transfermarkt/player_profiles/player_profiles.csv";
        System.out.println("📥 Importiere Spieler-Profile...");

        List<Object[]> batchArgs = new ArrayList<>();
        int batchSize = 1000;
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 20) continue;

                batchArgs.add(new Object[]{
                        clean(cols[0]),   // player_id
                        clean(cols[2]),   // player_name
                        clean(cols[12]),  // position
                        parseDate(cols[8]), // date_of_birth
                        clean(cols[11]),  // citizenship
                        clean(cols[14]),  // foot
                        clean(cols[18]),  // current_club_id
                        clean(cols[20])   // current_club_name
                });

                count++;

                if (batchArgs.size() >= batchSize) {
                    executePlayerBatch(batchArgs);
                    batchArgs.clear();
                    System.out.println("   " + count + " Spieler verarbeitet");
                }
            }

            if (!batchArgs.isEmpty()) {
                executePlayerBatch(batchArgs);
            }

            System.out.println("✅ " + count + " Spieler-Profile importiert!");

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Import: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Importiert die Marktwerte (901.457 Einträge)
     */
    public void importMarketValues() {
        String filepath = "C:/Users/49152/Desktop/java/transfermarkt-analyst/football-datasets/datalake/transfermarkt/raw/player_market_values/player_market_values.csv";
        System.out.println("📥 Importiere Marktwerte...");

        List<Object[]> batchArgs = new ArrayList<>();
        int batchSize = 1000;
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 3) continue;

                batchArgs.add(new Object[]{
                        clean(cols[0]),           // player_id
                        parseLong(cols[1]),       // date_unix
                        parseLong(cols[2])        // wert (Marktwert in Euro)
                });

                count++;

                if (batchArgs.size() >= batchSize) {
                    executeMarketValueBatch(batchArgs);
                    batchArgs.clear();
                    System.out.println("   " + count + " Marktwerte verarbeitet");
                }
            }

            if (!batchArgs.isEmpty()) {
                executeMarketValueBatch(batchArgs);
            }

            System.out.println("✅ " + count + " Marktwerte importiert!");

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Import: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executePlayerBatch(List<Object[]> batchArgs) {
        String sql = "INSERT INTO player_profiles " +
                "(player_id, player_name, position, date_of_birth, citizenship, foot, current_club_id, current_club_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void executeMarketValueBatch(List<Object[]> batchArgs) {
        String sql = "INSERT INTO player_market_values (player_id, date_unix, wert) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private String clean(String s) {
        if (s == null || s.isEmpty()) return null;
        return s.replace("\"", "").trim();
    }

    private java.sql.Date parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return java.sql.Date.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Long parseLong(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Long.parseLong(clean(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}