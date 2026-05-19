package com.transfermarkt.transfermarkt_analyst.config;

/**
 * Zentrale Konstanten für die gesamte Anwendung.
 *
 */
public final class ScoringConstants {

    private ScoringConstants() {
        // Private Konstruktor verhindert Instanziierung (Utility-Class Pattern)
    }

    // ===== SUCHE =====
    /** Maximale Anzahl von Suchergebnissen pro Anfrage */
    public static final int MAX_SEARCH_RESULTS = 20;

    /** Timeout für API-Anfragen in Millisekunden (15 Sekunden) */
    public static final int API_TIMEOUT_MS = 15000;

    // ===== SCORE-GEWICHTE (müssen mit app.yml übereinstimmen) =====
    /** Gewichtung der Position im Gesamtscore */
    public static final double WEIGHT_POSITION = 0.30;

    /** Gewichtung des Marktwerts im Gesamtscore */
    public static final double WEIGHT_PRICE = 0.25;

    /** Gewichtung des Alters im Gesamtscore */
    public static final double WEIGHT_AGE = 0.20;

    /** Gewichtung der Erfahrung im Gesamtscore */
    public static final double WEIGHT_EXPERIENCE = 0.15;

    /** Gewichtung der Wettbewerbsstärke im Gesamtscore */
    public static final double WEIGHT_COMPETITION = 0.10;

    // ===== SCORE-SCHWELLEN =====
    /** Ab diesem Score gilt der Transfer als "Must-Have" */
    public static final int THRESHOLD_MUST_HAVE = 85;

    /** Ab diesem Score gilt der Transfer als "Sehr gut" */
    public static final int THRESHOLD_VERY_GOOD = 75;

    /** Ab diesem Score gilt der Transfer als "Solide" */
    public static final int THRESHOLD_SOLID = 65;

    /** Ab diesem Score gilt der Transfer als "Durchschnitt" */
    public static final int THRESHOLD_AVERAGE = 55;

    /** Ab diesem Score gilt der Transfer als "Riskant" */
    public static final int THRESHOLD_RISKY = 45;
}