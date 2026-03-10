package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.TransferScore;
import com.transfermark.transfermarkt_analyst.model.Player;
import com.transfermark.transfermarkt_analyst.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TransferScoreServiceImpl implements TransferScoreService {

    private static final Logger log = LoggerFactory.getLogger(TransferScoreServiceImpl.class);
    private final AppProperties appProperties;

    public TransferScoreServiceImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public TransferScore calculateScore(Player player, String targetClub) {
        log.info("⚽ Berechne TransferScore für {} zu {}", player.getName(), targetClub);

        Map<String, Double> weights = appProperties.getScoring().getWeights();

        double positionScore = calculatePositionScore(player, targetClub);
        double priceScore = calculatePriceScore(player);
        double ageScore = calculateAgeScore(player);
        double experienceScore = calculateExperienceScore(player);
        double competitionScore = calculateCompetitionScore(player);

        double totalScore =
                positionScore * weights.getOrDefault("position", 0.30) +
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
    public String comparePlayers(Player player1, Player player2, String targetClub) {
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
    public boolean isEvaluable(Player player) {
        if (player == null) return false;
        if (player.getName() == null || player.getName().trim().isEmpty()) return false;
        if (player.getPosition() == null) return false;
        if (player.getMarketValue() == null || player.getMarketValue() <= 0) return false;
        return true;
    }

    // Position Score
    private double calculatePositionScore(Player player, String targetClub) {
        String position = player.getPosition();

        if ("Napoli".equals(targetClub)) {
            if ("LW".equals(position) || "RW".equals(position)) {
                return 90.0;
            } else if ("RB".equals(position)) {
                return 85.0;
            } else if ("ST".equals(position)) {
                return 60.0;
            } else {
                return 40.0;
            }
        }
        return 50.0;
    }

    // Price Score
    private double calculatePriceScore(Player player) {
        Double marketValue = player.getMarketValue();
        if (marketValue == null || marketValue <= 0) return 50.0;

        double askingPrice = marketValue * 1.1;
        double ratio = marketValue / askingPrice;

        if (ratio >= 1.0) return 100.0;
        if (ratio >= 0.9) return 80.0;
        if (ratio >= 0.8) return 60.0;
        if (ratio >= 0.7) return 40.0;
        return 20.0;
    }

    // Age Score
    private double calculateAgeScore(Player player) {
        Integer age = player.getAge();
        if (age == null || age == 0) return 50.0;

        if (age < 22) return 90.0;
        if (age < 25) return 85.0;
        if (age < 28) return 80.0;
        if (age < 31) return 60.0;
        if (age < 34) return 40.0;
        return 20.0;
    }

    // Experience Score
    private double calculateExperienceScore(Player player) {
        Integer age = player.getAge();
        if (age == null) return 50.0;

        if (age > 28) return 80.0;
        if (age > 24) return 60.0;
        if (age > 21) return 40.0;
        return 20.0;
    }

    // Competition Score
    private double calculateCompetitionScore(Player player) {
        Integer competition = player.getCompetitionCount();
        if (competition == null) return 50.0;

        if (competition == 0) return 100.0;
        if (competition == 1) return 80.0;
        if (competition == 2) return 60.0;
        if (competition == 3) return 40.0;
        if (competition <= 5) return 30.0;
        return 10.0;
    }

    // Recommendation
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