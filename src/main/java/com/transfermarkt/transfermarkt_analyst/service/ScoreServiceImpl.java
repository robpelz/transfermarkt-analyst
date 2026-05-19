package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.config.ScoringConstants;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service zur Berechnung des TransferScores.
 *
 * Diese Klasse ist nach dem Single-Responsibility-Prinzip nur für die Score-Berechnung zuständig.
 * Sie wird vom Controller aufgerufen, enthält aber keine HTTP-Logik.
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);

    /**
     * Berechnet den TransferScore für einen Spieler basierend auf:
     * - Alter (jünger = besser)
     * - Marktwert (günstiger = besser)
     * - Position (offensiv = höher gewichtet)
     * - Erfahrung (älter = mehr Erfahrung)
     * - Wettbewerb (höhere Liga = besser)
     *
     * @param player der zu bewertende Spieler
     * @param targetClub optionaler Zielverein (für positionsspezifische Bewertung)
     * @return TransferScore-Objekt mit Gesamtscore und Einzelscores
     */
    public TransferScore calculateScore(SoFifaPlayer player, String targetClub) {
        log.info("⚽ Berechne TransferScore für {} zu {}", player.getName(), targetClub);

        // Einzelscores berechnen (jeweils 0-100)
        int ageScore = calculateAgeScore(player.getAge());
        int priceScore = calculatePriceScore(player.getValue());
        int positionScore = calculatePositionScore(player.getPrimaryPosition(), targetClub);
        int experienceScore = calculateExperienceScore(player.getAge());
        int competitionScore = calculateCompetitionScore(targetClub);

        // Gesamtscore = gewichteter Durchschnitt
        // Achtung: Die Gewichte müssen in der Summe 1.0 ergeben
        double totalScore =
                positionScore * ScoringConstants.WEIGHT_POSITION +
                        priceScore * ScoringConstants.WEIGHT_PRICE +
                        ageScore * ScoringConstants.WEIGHT_AGE +
                        experienceScore * ScoringConstants.WEIGHT_EXPERIENCE +
                        competitionScore * ScoringConstants.WEIGHT_COMPETITION;

        // Auf eine Dezimalstelle runden
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

    /**
     * Alters-Score: Je jünger, desto besser (Zukunftspotential)
     *
     * Begründung: Spieler unter 23 haben noch Entwicklungspotential,
     * Spieler über 32 haben geringeren Wiederverkaufswert.
     */
    private int calculateAgeScore(int age) {
        if (age <= 0) return 50;      // Unbekanntes Alter → Mittelwert
        if (age < 23) return 95;      // Talent mit Zukunft
        if (age < 26) return 85;      // Im besten Alter
        if (age < 29) return 70;      // Leistungsträger
        if (age < 32) return 50;      // Absteigende Phase
        return 30;                     // Karriereende nah
    }

    /**
     * Preis-Score: Niedrigerer Marktwert = bessere Bewertung (Schnäppchen)
     *
     * Begründung: Ein Spieler mit 30 Mio. € ist ein besseres Investment
     * als einer mit 120 Mio. € bei ähnlicher Leistung.
     */
    private int calculatePriceScore(String valueStr) {
        if (valueStr == null || valueStr.equals("?")) return 50;

        try {
            // Entferne " €" und konvertiere zu Millionen
            double value = Double.parseDouble(valueStr.replace(" €", "").replace(" Mio.", "").trim());

            if (value >= 100) return 90;   // Superstar, aber teuer
            if (value >= 50) return 75;    // Teuer, aber etabliert
            if (value >= 20) return 60;    // Mittelfeld
            if (value >= 10) return 45;    // Erschwinglich
            return 30;                     // Schnäppchen
        } catch (Exception e) {
            log.warn("Preis nicht parsbar: {}", valueStr);
            return 50;
        }
    }

    /**
     * Positions-Score: Abhängig von der benötigten Position für den Zielverein
     *
     * Hier könnte man später eine dynamischere Logik einbauen,
     * z.B. basierend auf den Schwachstellen des Zielvereins.
     */
    private int calculatePositionScore(String position, String targetClub) {
        if (position == null) return 50;

        String posLower = position.toLowerCase();

        // Beispiel: Napoli sucht offensive Mittelfeldspieler
        if ("Napoli".equals(targetClub)) {
            if (posLower.contains("attacking") || posLower.contains("midfield")) {
                return 90;  // Perfekte Position für Napoli
            } else if (posLower.contains("forward") || posLower.contains("striker")) {
                return 80;  // Gut, aber nicht priorisiert
            } else if (posLower.contains("defender") || posLower.contains("back")) {
                return 70;  // Weniger Priorität
            }
        }

        return 60; // Standard für andere Vereine
    }

    /**
     * Erfahrungs-Score: Ältere Spieler haben mehr Erfahrung
     *
     * Begründung: Ein 30-jähriger Spieler bringt mehr Stabilität
     * und Führung als ein 20-jähriger.
     */
    private int calculateExperienceScore(int age) {
        if (age <= 0) return 50;
        if (age > 28) return 80;   // Erfahren, Führungsspieler
        if (age > 24) return 60;   // Im Aufbau
        if (age > 21) return 40;   // Talent mit wenig Erfahrung
        return 20;                  // Sehr jung, unerfahren
    }

    /**
     * Wettbewerbs-Score: Höhere Liga = höherer Score
     *
     * Hier könnte man später die tatsächliche Liga des Zielvereins
     * aus der Datenbank holen.
     */
    private int calculateCompetitionScore(String targetClub) {
        // TODO: Aus Datenbank die Liga des Zielvereins ermitteln
        // Vorab: Fester Wert für Präsentation
        if ("Napoli".equals(targetClub)) {
            return 85;  // Serie A ist starke Liga
        }
        return 70; // Standard
    }

    /**
     * Empfehlung basierend auf dem Gesamtscore
     */
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
