package com.transfermark.transfermarkt_analyst.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    private String fromClub;
    private String toClub;
    private Double transferFee;  // in Millionen €
    private LocalDate transferDate;

    // Für Bewertung
    private Integer season;  // z.B. 2024 für Saison 2023/24
    private Boolean isLoan;
    // Für Club-Bewertung
    private Boolean wasSuccessful;     // War der Transfer erfolgreich?
    private Integer goalsAfterTransfer;
    private Integer appearancesAfterTransfer;
    private Double marketValueAfterTransfer;
    private String seasonLoan; // Leihe?

    // Verknüpfung zum Player (später)
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    // Bewertungs-Felder (werden später berechnet)
    private Integer transferScore;
    private String recommendation;  // "Must-Have", "Riskant", etc.
}