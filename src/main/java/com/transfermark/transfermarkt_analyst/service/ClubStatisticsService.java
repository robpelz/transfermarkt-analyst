package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.ClubStatistics;
import java.util.List;

public interface ClubStatisticsService {

    /**
     * Holt detaillierte Statistiken für einen bestimmten Verein
     * @param clubName Name des Vereins
     * @return ClubStatistics Objekt mit allen Statistiken
     */
    ClubStatistics getClubStatistics(String clubName);

    /**
     * Holt die Top-Vereine basierend auf Transfer-Erfolgsrate
     * @param limit Maximale Anzahl
     * @return Liste der Top-Vereine mit Statistiken
     */
    List<ClubStatistics> getTopClubs(int limit);

    /**
     * Vergleicht zwei Vereine miteinander
     * @param club1 Erster Verein
     * @param club2 Zweiter Verein
     * @return Vergleichsstring
     */
    String compareClubs(String club1, String club2);

    /**
     * Holt die schlechtesten Vereine (für interessanten Kontrast)
     * @param limit Maximale Anzahl
     * @return Liste der schlechtesten Vereine
     */
    List<ClubStatistics> getWorstClubs(int limit);
}