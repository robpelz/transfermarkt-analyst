package com.transfermarkt.transfermarkt_analyst.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketValueImportService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${import.marketvalues.file:src/main/resources/player_latest_market_value.csv}")
    private String defaultFilepath;

    public MarketValueImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Importiert Marktwerte aus der Standard-Datei
     */
    public void importMarketValues() {
        importMarketValuesFromFile(defaultFilepath);
    }

    /**
     * Importiert Marktwerte aus einer angegebenen CSV-Datei
     * @param filepath Pfad zur CSV-Datei
     * @return Anzahl der importierten Zeilen
     */
    public int importMarketValuesFromFile(String filepath) {
        System.out.println("📥 Importiere Marktwerte aus: " + filepath);

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

                String[] cols = line.split(",");
                if (cols.length < 2) continue;

                String playerId = clean(cols[0]);
                Integer marketValue = null;

                if (cols.length >= 3 && cols[2] != null && !cols[2].isEmpty()) {
                    try {
                        marketValue = (int) Double.parseDouble(clean(cols[2]));
                    } catch (NumberFormatException e) {
                        // Ignorieren, marketValue bleibt null
                    }
                }

                if (playerId != null && !playerId.isEmpty() && marketValue != null) {
                    batchArgs.add(new Object[]{playerId, marketValue});
                    count++;

                    if (batchArgs.size() >= batchSize) {
                        executeBatch(batchArgs);
                        batchArgs.clear();
                        System.out.println("   " + count + " Marktwerte verarbeitet");
                    }
                }
            }

            if (!batchArgs.isEmpty()) {
                executeBatch(batchArgs);
            }

            System.out.println("✅ " + count + " Marktwerte importiert!");
            return count;

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Import: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Führt einen Batch-Insert aus
     */
    private void executeBatch(List<Object[]> batchArgs) {
        String sql = "INSERT INTO player_latest_market_value (player_id, market_value) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    /**
     * Bereinigt einen String
     */
    String clean(String s) {
        if (s == null || s.isEmpty()) return null;
        return s.replace("\"", "").trim();
    }
}