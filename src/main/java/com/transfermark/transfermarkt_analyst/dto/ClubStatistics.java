package com.transfermark.transfermarkt_analyst.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor  // ← DAS löst das Problem!
public class ClubStatistics {
    private String clubName;
    private int totalTransfers;
    private int successfulTransfers;
    private double successRate;
    private double totalSpent;
    private double averageFee;
    private String bestTransfer;
    private String worstTransfer;
}