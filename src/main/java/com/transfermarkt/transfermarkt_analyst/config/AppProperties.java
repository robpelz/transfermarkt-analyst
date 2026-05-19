package com.transfermarkt.transfermarkt_analyst.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private Scoring scoring = new Scoring();
    private Club club = new Club();
    private Logging logging = new Logging();

    @Data
    public static class Scoring {
        private Map<String, Double> weights = new HashMap<>();
        private Map<String, Integer> thresholds = new HashMap<>();  // ← DAS war falsch!

        // Hilfsmethode für sicheren Zugriff
        public double getWeight(String key, double defaultValue) {
            return weights.getOrDefault(key, defaultValue);
        }

        public int getThreshold(String key, int defaultValue) {
            return thresholds.getOrDefault(key, defaultValue);
        }
    }

    @Data
    public static class Club {
        private int minTransfersForScore = 5;
        private String defaultLeague = "Serie A";
    }

    @Data
    public static class Logging {
        private boolean includeTimestamp = true;
        private boolean includePlayerDetails = false;
    }
}