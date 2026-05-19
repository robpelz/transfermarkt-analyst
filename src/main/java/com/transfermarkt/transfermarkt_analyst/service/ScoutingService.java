package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import java.util.List;

public interface ScoutingService {
    Scouting addScouting(String playerId, String playerName, Integer rating, String note);
    List<Scouting> getAllScouting();
    Scouting getScoutingByPlayerId(String playerId);
    Scouting updateScouting(String playerId, Integer rating, String note);
    void deleteScouting(String playerId);
    boolean existsByPlayerId(String playerId);
}
