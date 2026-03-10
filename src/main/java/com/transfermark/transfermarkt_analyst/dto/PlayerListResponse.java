package com.transfermark.transfermarkt_analyst.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerListResponse {
    private Long id;
    private String name;
    private String position;
    private String positionBadge;  // z.B. "⚽ ST"
    private String club;
    private Double marketValueMio;
    private String marketValueFormatted;  // z.B. "120 Mio €"
    private Integer age;
    private String nationality;
    private String hotBadge;  // z.B. "🔥 Top-Talent", "⭐ Star"
    private Boolean hasTransferScore;

    // Factory method für leere Builder
    public static PlayerListResponseBuilder builder() {
        return new PlayerListResponseBuilder();
    }
}