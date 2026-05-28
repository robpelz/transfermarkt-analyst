package com.transfermarkt.transfermarkt_analyst.service;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${import.data.directory:src/main/resources}")
    private String dataDirectory;

    public CsvImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Importiert die Premier League
     */
    public void importPremierLeague() {
        String filepath = dataDirectory + "/premier_league_2025.csv";
        importLeague(filepath, "Premier League");
    }

    /**
     * Importiert alle 5 Ligen
     */
    public void importAllLeagues() {
        System.out.println("📥 Starte Import aller Ligen...");

        importPremierLeague();
        importLeague(dataDirectory + "/bundesliga_2025.csv", "Bundesliga");
        importLeague(dataDirectory + "/laliga_2025.csv", "La Liga");
        importLeague(dataDirectory + "/serie_a_2025.csv", "Serie A");
        importLeague(dataDirectory + "/ligue_1_2025.csv", "Ligue 1");

        System.out.println("✅ Alle Ligen importiert!");
    }

    /**
     * Öffentliche Methode für Tests - importiert eine einzelne CSV-Datei
     * @param filepath Pfad zur CSV-Datei
     * @param leagueName Name der Liga
     * @return Anzahl der importierten Zeilen
     */
    public int importLeagueFromFile(String filepath, String leagueName) {
        return importLeague(filepath, leagueName);
    }

    /**
     * Importiert eine einzelne CSV-Datei mit Batch-Insert
     * @param filepath Pfad zur CSV-Datei
     * @param leagueName Name der Liga
     * @return Anzahl der importierten Zeilen
     */
    private int importLeague(String filepath, String leagueName) {
        System.out.println("📂 Versuche zu lesen: " + filepath);

        // Prüfe ob Datei existiert
        File file = new File(filepath);
        if (!file.exists()) {
            System.err.println("❌ Datei nicht gefunden: " + filepath);
            return 0;
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
            return totalCount;

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Import von " + leagueName + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
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
    String clean(String s) {
        if (s == null || s.isEmpty()) return null;
        return s.replace("\"", "").trim();
    }

    /**
     * Parst Integer sicher
     */
    Integer parseInt(String s) {
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
    Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Double.parseDouble(clean(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Test-Hilfsmethode: Erstellt eine Test-CSV-Datei
     * (Nur für Tests verwendet)
     */
    public static String createTestCsvContent() {
        return "season,league,club,transfer_window,movement,player_name,player_id,age,nationality,position,pos_short,market_value,dealing_club,dealing_country,fee,is_loan\n" +
                "2024,Bundesliga,Dortmund,Winter,Transfer,Moukoko,12345,18,Germany,Striker,ST,30000000,,,0,0\n" +
                "2024,Bundesliga,Bayern,Summer,Transfer,Kane,54321,30,England,Striker,ST,100000000,,,0,0";
    }
}