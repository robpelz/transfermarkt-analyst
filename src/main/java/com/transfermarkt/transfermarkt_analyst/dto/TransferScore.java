package com.transfermarkt.transfermarkt_analyst.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferScore {
    private double totalScore;
    private double positionScore;
    private double priceScore;
    private double ageScore;
    private double experienceScore;
    private double competitionScore;
    private String recommendation;
}