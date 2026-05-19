package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import java.util.List;

public interface PlayerService {
    SoFifaPlayer getPlayerById(String playerId);
    List<SoFifaPlayer> searchPlayers(String playerName);
}