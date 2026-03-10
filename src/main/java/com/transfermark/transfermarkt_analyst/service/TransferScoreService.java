package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.TransferScore;
import com.transfermark.transfermarkt_analyst.model.Player;
import java.util.Map;

public interface TransferScoreService {

    /**
     * Berechnet den TransferScore für einen Spieler zu einem bestimmten Club
     * @param player Der Spieler
     * @param targetClub Der interessierte Club
     * @return TransferScore mit Einzelbewertungen und Gesamtscore
     */
    TransferScore calculateScore(Player player, String targetClub);

    /**
     * Vergleicht zwei Spieler für einen Club
     * @param player1 Erster Spieler
     * @param player2 Zweiter Spieler
     * @param targetClub Der Club
     * @return Vergleichsergebnis mit Empfehlung
     */
    String comparePlayers(Player player1, Player player2, String targetClub);

    /**
     * Gibt die aktuelle Gewichtung der Bewertungskriterien zurück
     * @return Map mit Kriterien und ihrer Gewichtung
     */
    Map<String, Double> getCurrentWeights();

    /**
     * Prüft ob ein Spieler überhaupt bewertet werden kann
     * @param player Der Spieler
     * @return true wenn alle notwendigen Daten vorhanden
     */
    boolean isEvaluable(Player player);
}