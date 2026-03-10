package com.transfermark.transfermarkt_analyst.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransferRequest {

    @NotBlank(message = "Spielername ist Pflicht")
    private String playerName;

    private Long playerId;  // Optional: Verknüpfung zu existierendem Spieler

    @NotBlank(message = "Abgebender Verein ist Pflicht")
    private String fromClub;

    @NotBlank(message = "Aufnehmender Verein ist Pflicht")
    private String toClub;

    @NotNull(message = "Ablösesumme ist Pflicht")
    @Positive(message = "Ablösesumme muss positiv sein")
    private Double transferFee;

    @PastOrPresent(message = "Transferdatum kann nicht in der Zukunft liegen")
    private LocalDate transferDate;

    private Integer season;  // z.B. 2024 für Saison 2023/24

    private Boolean isLoan;
    private Boolean wasSuccessful;  // Für nachträgliche Bewertung

    // Optional
    private Integer goalsAfterTransfer;
    private Integer appearancesAfterTransfer;
}