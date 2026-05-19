package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import java.util.Map;

public interface TransferScoreService {

    TransferScore calculateScore(SoFifaPlayer player, String targetClub);

    String comparePlayers(SoFifaPlayer player1, SoFifaPlayer player2, String targetClub);

    Map<String, Double> getCurrentWeights();

    boolean isEvaluable(SoFifaPlayer player);
}