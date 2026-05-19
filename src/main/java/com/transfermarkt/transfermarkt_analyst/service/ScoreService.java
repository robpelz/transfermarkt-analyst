package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;

public interface ScoreService {
    TransferScore calculateScore(SoFifaPlayer player, String targetClub);
}