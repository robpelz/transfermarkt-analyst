package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.config.AppProperties;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Service
public class TransferScoreServiceImpl implements TransferScoreService {

    public static final Logger log = LoggerFactory.getLogger(TransferScoreServiceImpl.class);

    private final AppProperties appProperties;
    private final JdbcTemplate jdbcTemplate;

    public TransferScoreServiceImpl(AppProperties appProperties, JdbcTemplate jdbcTemplate) {
        this.appProperties = appProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TransferScore calculateScore(SoFifaPlayer player, String targetClub) {
        log.info("⚽ Berechne TransferScore für {} zu {}", player.getName(), targetClub);

        Map<String, Double> weights = appProperties.getScoring().getWeights();

        // Echte Werte aus der Datenbank holen
        double marketValueMio = getMarketValue(player.getName());
        int age = getAge(player.getName());
        String position = getPosition(player.getName());

        // Falls keine Daten in der DB, Fallback zu den Player-Daten
        if (marketValueMio <= 0 && player.getValueInMillion() > 0) {
            marketValueMio = player.getValueInMillion();
        }
        if (age <= 0 && player.getAge() > 0) {
            age = player.getAge();
        }
        if ((position == null || position.isEmpty()) && player.getPrimaryPosition() != null) {
            position = player.getPrimaryPosition();
        }

        double positionScore = calculatePositionScore(position);
        double priceScore = calculatePriceScore(marketValueMio);
        double ageScore = calculateAgeScore(age);
        double experienceScore = calculateExperienceScore(age);
        double competitionScore = calculateCompetitionScore();

        double totalScore = positionScore * weights.getOrDefault("position", 0.30) +
                priceScore * weights.getOrDefault("price", 0.25) +
                ageScore * weights.getOrDefault("age", 0.20) +
                experienceScore * weights.getOrDefault("experience", 0.15) +
                competitionScore * weights.getOrDefault("competition", 0.10);

        log.debug("Einzelscores für {}: Position={}, Preis={}, Alter={}, Erfahrung={}, Konkurrenz={}",
                player.getName(), positionScore, priceScore, ageScore, experienceScore, competitionScore);

        log.info("✅ TransferScore für {}: {}/100", player.getName(), Math.round(totalScore));

        String recommendation = getRecommendation(totalScore);
        double roundedTotal = Math.round(totalScore * 10) / 10.0;

        return new TransferScore(
                roundedTotal,
                positionScore,
                priceScore,
                ageScore,
                experienceScore,
                competitionScore,
                recommendation
        );
    }

    @Override
    public String comparePlayers(SoFifaPlayer player1, SoFifaPlayer player2, String targetClub) {
        TransferScore score1 = calculateScore(player1, targetClub);
        TransferScore score2 = calculateScore(player2, targetClub);

        if (score1.getTotalScore() > score2.getTotalScore()) {
            return String.format("🏆 %s ist besser für %s (%.1f vs %.1f)",
                    player1.getName(), targetClub, score1.getTotalScore(), score2.getTotalScore());
        } else if (score2.getTotalScore() > score1.getTotalScore()) {
            return String.format("🏆 %s ist besser für %s (%.1f vs %.1f)",
                    player2.getName(), targetClub, score2.getTotalScore(), score1.getTotalScore());
        } else {
            return String.format("🤝 Beide Spieler sind gleich gut für %s (%.1f)",
                    targetClub, score1.getTotalScore());
        }
    }

    @Override
    public Map<String, Double> getCurrentWeights() {
        return appProperties.getScoring().getWeights();
    }

    @Override
    public boolean isEvaluable(SoFifaPlayer player) {
        if (player == null) return false;
        if (player.getName() == null || player.getName().trim().isEmpty()) return false;
        return true;
    }

    // ==================== HILFSMETHODEN AUS DER DATENBANK ====================

    private double getMarketValue(String playerName) {
        try {
            String sql = """
                SELECT wert FROM PLAYER_MARKET_VALUES 
                WHERE player_id IN (SELECT player_id FROM PLAYER_PROFILES WHERE player_name LIKE ?)
                ORDER BY date_unix DESC LIMIT 1
            """;
            String wert = jdbcTemplate.queryForObject(sql, String.class, "%" + playerName + "%");
            if (wert != null && !wert.isEmpty()) {
                double value = Double.parseDouble(wert.replaceAll("\\.0$", ""));
                return value / 1_000_000.0;
            }
        } catch (Exception e) {
            log.warn("Kein Marktwert für {} gefunden: {}", playerName, e.getMessage());
        }
        return 0.0;
    }

    private int getAge(String playerName) {
        try {
            String sql = """
                SELECT date_of_birth FROM PLAYER_PROFILES 
                WHERE player_name LIKE ?
                LIMIT 1
            """;
            String birthStr = jdbcTemplate.queryForObject(sql, String.class, "%" + playerName + "%");
            if (birthStr != null && !birthStr.isEmpty()) {
                LocalDate birth = LocalDate.parse(birthStr);
                return Period.between(birth, LocalDate.now()).getYears();
            }
        } catch (Exception e) {
            log.warn("Kein Alter für {} gefunden: {}", playerName, e.getMessage());
        }
        return 0;
    }

    private String getPosition(String playerName) {
        try {
            String sql = """
                SELECT position FROM PLAYER_PROFILES 
                WHERE player_name LIKE ?
                LIMIT 1
            """;
            return jdbcTemplate.queryForObject(sql, String.class, "%" + playerName + "%");
        } catch (Exception e) {
            log.warn("Keine Position für {} gefunden: {}", playerName, e.getMessage());
            return null;
        }
    }

    // ==================== SCORE-BERECHNUNGEN (NEUTRAL) ====================

    /**
     * Positions-Score: Generisch, keine Vereins-Spezialfälle
     */
    private double calculatePositionScore(String position) {
        if (position == null) return 50.0;

        String posLower = position.toLowerCase();

        if (posLower.contains("attacking") || posLower.contains("midfield") ||
                posLower.contains("wing") || posLower.contains("left") || posLower.contains("right")) {
            return 75.0;  // Offensive/Mittelfeld leicht bevorzugt
        } else if (posLower.contains("forward") || posLower.contains("striker")) {
            return 70.0;  // Stürmer
        } else if (posLower.contains("back") || posLower.contains("defensive") || posLower.contains("defender")) {
            return 60.0;  // Defensive
        } else if (posLower.contains("goalkeeper") || posLower.contains("keeper")) {
            return 55.0;  // Torwart
        }

        return 60.0; // Standard
    }

    /**
     * Preis-Score: Günstiger = besser
     */
    private double calculatePriceScore(double marketValueMio) {
        if (marketValueMio <= 0) return 50.0;
        if (marketValueMio < 30) return 90.0;
        if (marketValueMio < 50) return 80.0;
        if (marketValueMio < 80) return 70.0;
        if (marketValueMio < 120) return 60.0;
        return 40.0;
    }

    /**
     * Alters-Score: Jünger = besser
     */
    private double calculateAgeScore(int age) {
        if (age <= 0) return 50.0;
        if (age < 22) return 90.0;
        if (age < 25) return 85.0;
        if (age < 28) return 80.0;
        if (age < 31) return 60.0;
        if (age < 34) return 40.0;
        return 20.0;
    }

    /**
     * Erfahrungs-Score: Sweet Spot bei 28-32
     */
    private double calculateExperienceScore(int age) {
        if (age <= 0) return 50.0;
        if (age >= 28 && age <= 32) return 80.0;
        if (age >= 25 && age <= 27) return 60.0;
        if (age >= 22 && age <= 24) return 40.0;
        if (age >= 18 && age <= 21) return 20.0;
        if (age > 32) return 70.0;
        return 50.0;
    }

    /**
     * Wettbewerbs-Score: Generisch, keine Club-Spezialfälle
     * Kann später aus Config oder DB kommen
     */
    private double calculateCompetitionScore() {
        return 70.0; // Standard-Liga-Wert
    }

    /**
     * Empfehlung basierend auf Gesamtscore
     */
    private String getRecommendation(double score) {
        Map<String, Integer> thresholds = appProperties.getScoring().getThresholds();

        if (score >= thresholds.getOrDefault("must-have", 85)) {
            return "🚨 MUST-HAVE! Sofort zuschlagen!";
        } else if (score >= thresholds.getOrDefault("very-good", 75)) {
            return "✅ Sehr guter Transfer";
        } else if (score >= thresholds.getOrDefault("solid", 65)) {
            return "👍 Solider Transfer";
        } else if (score >= thresholds.getOrDefault("average", 55)) {
            return "🤔 Durchschnitt - Alternativen prüfen";
        } else if (score >= thresholds.getOrDefault("risky", 45)) {
            return "⚠️ Riskant - Vorsicht!";
        }
        return "❌ Finger weg - nächster Flop vermeiden";
    }
}