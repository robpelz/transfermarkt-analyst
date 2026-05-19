package com.transfermarkt.transfermarkt_analyst.service;

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

    public MarketValueImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void importMarketValues() {
        String filepath = "C:/Users/49152/Desktop/java/transfermarkt-analyst/football-datasets/datalake/transfermarkt/player_latest_market_value/player_latest_market_value.csv";
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

                String[] cols = line.split(",");
                if (cols.length < 2) continue;

                String playerId = clean(cols[0]);
                Integer marketValue = null;

                if (cols.length >= 3 && cols[2] != null && !cols[2].isEmpty()) {
                    try {
                        marketValue = (int) Double.parseDouble(clean(cols[2]));
                    } catch (NumberFormatException e) {
                        // Ignorieren
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

        } catch (IOException e) {
            System.err.println("❌ Fehler: " + e.getMessage());
        }
    }

    private void executeBatch(List<Object[]> batchArgs) {
        String sql = "INSERT INTO player_latest_market_value (player_id, market_value) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private String clean(String s) {
        if (s == null || s.isEmpty()) return null;
        return s.replace("\"", "").trim();
    }
}