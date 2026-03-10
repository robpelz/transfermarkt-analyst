package com.transfermark.transfermarkt_analyst.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PlayerResponse {
    private Long id;
    private String name;
    private String nameFull;  // Mit Vorsilbe "⚽ " o.ä.
    private String position;
    private String positionFull;  // "ST" -> "Stürmer"
    private Integer age;
    private String ageGroup;  // "Young Star", "Prime", "Veteran"
    private String club;
    private String league;  // "Serie A", "Bundesliga", etc.
    private Double marketValueMio;
    private String marketValueFormatted;
    private String nationality;
    private String nationalityFlag;  // Für später: Länderflagge
    private Boolean playsInSerieA;

    // Transfer-spezifisch
    private TransferScore transferScore;  // Dein existierendes DTO
    private Integer competitionCount;
    private String transferRecommendation;

    // Metadaten
    private String lastUpdated;
    private String dataQuality;  // "High", "Medium", "Low"

    @Data
    @Builder
    public static class TransferSummary {
        private int totalTransfers;
        private int successfulTransfers;
        private double successRate;
        private List<SimpleTransfer> recentTransfers;
    }

    @Data
    @Builder
    public static class SimpleTransfer {
        private Long id;
        private String fromClub;
        private String toClub;
        private Double fee;
        private String season;
        private Boolean wasSuccessful;
        private String successIcon;  // "✅" oder "❌"
    }
}