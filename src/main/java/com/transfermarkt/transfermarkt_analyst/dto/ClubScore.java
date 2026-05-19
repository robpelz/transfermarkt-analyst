package com.transfermarkt.transfermarkt_analyst.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClubScore {
    private String clubName;
    private double successRate;
    private int totalTransfers;
    private String recommendation;
}