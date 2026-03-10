package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.ClubScore;

public interface ClubScoreService {

    /**
     * Berechnet den Transfer-Score für einen Verein basierend auf seinen Transfer-Historien
     * @param clubName Name des Vereins
     * @return ClubScore mit Erfolgsrate und Empfehlung
     */
    ClubScore calculateClubScore(String clubName);

    /**
     * Gibt die Top-Vereine basierend auf ihrer Transfer-Erfolgsrate zurück
     * @param limit Maximale Anzahl der zurückzugebenden Vereine
     * @return Liste der Vereinsnamen
     */
    java.util.List<String> getTopClubs(int limit);

    /**
     * Prüft ob ein Verein genügend Transferdaten für eine aussagekräftige Bewertung hat
     * @param clubName Name des Vereins
     * @return true wenn genügend Daten vorhanden
     */
    boolean hasEnoughData(String clubName);
}