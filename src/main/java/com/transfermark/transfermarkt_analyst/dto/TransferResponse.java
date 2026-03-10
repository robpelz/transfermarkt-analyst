package com.transfermark.transfermarkt_analyst.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class TransferResponse {
    private Long id;
    private String playerName;
    private Long playerId;  // Verknüpfung zu Player

    private String fromClub;
    private String toClub;
    private String transferType;  // "Transfer" oder "Leihe"

    private Double transferFee;
    private String transferFeeFormatted;
    private String transferFeeCategory;  // "Schnäppchen", "Fair", "Teuer"

    private LocalDate transferDate;
    private String transferDateFormatted;
    private String season;

    private Boolean wasSuccessful;
    private String successIcon;
    private String successBadge;  // "✅ Erfolg", "❌ Flop", "⏳ In Bewertung"

    // Statistiken
    private Integer goalsAfterTransfer;
    private Integer appearancesAfterTransfer;
    private String performanceRating;  // "Excellent", "Good", "Poor"

    // Verknüpfte Daten
    private TransferScore transferScore;  // Falls vorhanden

    // Formatierungs-Hilfen
    public String getTransferDateFormatted() {
        if (transferDate == null) return "";
        return transferDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Builder
    public static class TransferSummary {
        private Long id;
        private String playerName;
        private String fromTo;  // "Napoli → Chelsea"
        private String fee;
        private String season;
        private String successStatus;
    }
}