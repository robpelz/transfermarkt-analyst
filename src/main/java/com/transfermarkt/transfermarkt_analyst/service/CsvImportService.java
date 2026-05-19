package com.transfermarkt.transfermarkt_analyst.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private final JdbcTemplate jdbcTemplate;

    public CsvImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Importiert die Premier League (wo Wirtz, Kane, Haaland sind)
     */
    public void importPremierLeague() {
        String filepath = "C:/Users/49152/Desktop/java/transfermarkt-analyst/backend/transfermarkt-analyst/src/main/resources/premier_league_2025.csv";
        importLeague(filepath, "Premier League");
    }

    /**
     * Importiert alle 5 Ligen
     */
    public void importAllLeagues() {
        System.out.println("📥 Starte Import aller Ligen...");

        importPremierLeague();
        importLeague("C:/Users/49152/Desktop/java/transfermarkt-analyst/backend/transfermarkt-analyst/src/main/resources/bundesliga_2025.csv", "Bundesliga");
        importLeague("C:/Users/49152/Desktop/java/transfermarkt-analyst/backend/transfermarkt-analyst/src/main/resources/laliga_2025.csv", "La Liga");
        importLeague("C:/Users/49152/Desktop/java/transfermarkt-analyst/backend/transfermarkt-analyst/src/main/resources/serie_a_2025.csv", "Serie A");
        importLeague("C:/Users/49152/Desktop/java/transfermarkt-analyst/backend/transfermarkt-analyst/src/main/resources/ligue_1_2025.csv", "Ligue 1");

        System.out.println("✅ Alle Ligen importiert!");
    }

    /**
     * Importiert eine einzelne CSV-Datei mit Batch-Insert (kein Timeout!)
     */
    private void importLeague(String filepath, String leagueName) {

            System.out.println("📂 Versuche zu lesen: " + filepath);

            // Prüfe ob Datei existiert
            File file = new File(filepath);
            if (!file.exists()) {
                System.err.println("❌ Datei nicht gefunden: " + filepath);
                return;
            }
            System.out.println("✅ Datei gefunden, Größe: " + file.length() + " bytes");

        List<Object[]> batchArgs = new ArrayList<>();
        int batchSize = 1000;
        int totalCount = 0;
        int batchCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 16) continue;

                // Daten für Batch sammeln
                batchArgs.add(new Object[]{
                        parseInt(cols[0]),   // season
                        clean(cols[1]),      // league
                        clean(cols[2]),      // club
                        clean(cols[3]),      // transfer_window
                        clean(cols[4]),      // movement
                        clean(cols[5]),      // player_name
                        parseInt(cols[6]),   // player_id
                        parseInt(cols[7]),   // age
                        clean(cols[8]),      // nationality
                        clean(cols[9]),      // position
                        clean(cols[10]),     // pos_short
                        parseDouble(cols[11]), // market_value
                        clean(cols[12]),     // dealing_club
                        clean(cols[13]),     // dealing_country
                        parseDouble(cols[14]), // fee
                        parseInt(cols[15]) == 1 // is_loan
                });

                totalCount++;
                batchCount++;

                // Batch ausführen, wenn voll
                if (batchCount >= batchSize) {
                    executeBatch(batchArgs);
                    batchArgs.clear();
                    batchCount = 0;
                    System.out.println("   " + leagueName + ": " + totalCount + " Zeilen verarbeitet");
                }
            }

            // Letzten Rest speichern
            if (!batchArgs.isEmpty()) {
                executeBatch(batchArgs);
            }

            System.out.println("✅ " + leagueName + ": " + totalCount + " Spieler importiert");

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Import von " + leagueName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Führt einen Batch-Insert aus
     */
    private void executeBatch(List<Object[]> batchArgs) {
        String sql = "INSERT INTO player_market_data " +
                "(season, league, club, transfer_window, movement, player_name, player_id, age, " +
                "nationality, position, pos_short, market_value, dealing_club, dealing_country, fee, is_loan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    /**
     * Bereinigt einen String
     */
    private String clean(String s) {
        if (s == null || s.isEmpty()) return null;
        return s.replace("\"", "").trim();
    }

    /**
     * Parst Integer sicher
     */
    private Integer parseInt(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(clean(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parst Double sicher
     */
    private Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Double.parseDouble(clean(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}