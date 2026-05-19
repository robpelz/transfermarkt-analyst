package com.transfermarkt.transfermarkt_analyst.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "player_market_data")
@Data
public class PlayerMarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer season;
    private String league;
    private String club;
    @Column(name = "transfer_window")
    private String transferWindow;
    private String movement;
    private String playerName;
    private Integer playerId;
    private Integer age;
    private String nationality;
    private String position;
    private String posShort;
    private Double marketValue;  // in Euro
    private String dealingClub;
    private String dealingCountry;
    private Double fee;
    private Boolean isLoan;
}