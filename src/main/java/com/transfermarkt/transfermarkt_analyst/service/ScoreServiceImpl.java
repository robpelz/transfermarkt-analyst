package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.config.ScoringConstants;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {

    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);

    public TransferScore calculateScore(SoFifaPlayer player, String targetClub) {
        log.info("⚽ Berechne TransferScore für {} zu {}", player.getName(), targetClub);

        int ageScore = calculateAgeScore(player.getAge());
        int priceScore = calculatePriceScore(player.getValue());
        int positionScore = calculatePositionScore(player.getPrimaryPosition());
        int experienceScore = calculateExperienceScore(player.getAge());
        int competitionScore = calculateCompetitionScore();

        double totalScore =
                positionScore * ScoringConstants.WEIGHT_POSITION +
                        priceScore * ScoringConstants.WEIGHT_PRICE +
                        ageScore * ScoringConstants.WEIGHT_AGE +
                        experienceScore * ScoringConstants.WEIGHT_EXPERIENCE +
                        competitionScore * ScoringConstants.WEIGHT_COMPETITION;

        double roundedTotal = Math.round(totalScore * 10) / 10.0;
        String recommendation = getRecommendation(roundedTotal);

        log.info("✅ TransferScore für {}: {}/100", player.getName(), roundedTotal);

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

    private int calculateAgeScore(int age) {
        if (age <= 0) return 50;
        if (age <= 21) return 100;
        if (age <= 23) return 90;
        if (age <= 25) return 80;
        if (age <= 27) return 70;
        if (age <= 29) return 55;
        if (age <= 31) return 40;
        if (age <= 34) return 30;
        return 20;
    }

    private int calculatePriceScore(String valueStr) {
        if (valueStr == null || valueStr.equals("?") || valueStr.isEmpty()) return 50;

        try {
            String clean = valueStr.replace("€", "").trim();
            double value = 0;

            if (clean.contains("Mio.")) {
                value = Double.parseDouble(clean.replace("Mio.", "").trim());
            } else if (clean.contains("M")) {
                value = Double.parseDouble(clean.replace("M", "").trim());
            } else if (clean.contains("K")) {
                value = Double.parseDouble(clean.replace("K", "").trim()) / 1000.0;
            } else {
                value = Double.parseDouble(clean);
            }

            if (value <= 5) return 95;
            if (value <= 10) return 85;
            if (value <= 20) return 70;
            if (value <= 35) return 55;
            if (value <= 50) return 40;
            if (value <= 75) return 30;
            if (value <= 100) return 20;
            return 10;
        } catch (Exception e) {
            log.warn("Preis nicht parsbar: {}", valueStr);
            return 50;
        }
    }

    /**
     * Positions-Score: Generisch, keine Vereins-Spezialfälle
     */
    private int calculatePositionScore(String position) {
        if (position == null) return 50;
        String posLower = position.toLowerCase();

        if (posLower.contains("attacking") || posLower.contains("forward") || posLower.contains("striker")) {
            return 75;
        } else if (posLower.contains("midfield")) {
            return 70;
        } else if (posLower.contains("defender") || posLower.contains("back")) {
            return 60;
        } else if (posLower.contains("goalkeeper") || posLower.contains("keeper")) {
            return 55;
        }

        return 65;
    }

    private int calculateExperienceScore(int age) {
        if (age <= 0) return 50;
        if (age >= 28 && age <= 32) return 90;
        if (age >= 25 && age <= 27) return 75;
        if (age >= 33 && age <= 35) return 60;
        if (age >= 22 && age <= 24) return 45;
        if (age >= 18 && age <= 21) return 25;
        if (age > 35) return 40;
        return 30;
    }

    /**
     * Wettbewerbs-Score: Generisch
     */
    private int calculateCompetitionScore() {
        return 70;
    }

    private String getRecommendation(double score) {
        if (score >= ScoringConstants.THRESHOLD_MUST_HAVE) {
            return "🚨 MUST-HAVE! Sofort zuschlagen!";
        } else if (score >= ScoringConstants.THRESHOLD_VERY_GOOD) {
            return "✅ Sehr guter Transfer";
        } else if (score >= ScoringConstants.THRESHOLD_SOLID) {
            return "👍 Solider Transfer";
        } else if (score >= ScoringConstants.THRESHOLD_AVERAGE) {
            return "🤔 Durchschnitt - Alternativen prüfen";
        } else if (score >= ScoringConstants.THRESHOLD_RISKY) {
            return "⚠️ Riskant - Vorsicht!";
        }
        return "❌ Finger weg - nächster Flop vermeiden";
    }
}