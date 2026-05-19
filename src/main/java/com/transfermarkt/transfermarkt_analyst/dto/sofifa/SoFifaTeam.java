package com.transfermarkt.transfermarkt_analyst.dto.sofifa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoFifaTeam {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("league")
    private String league;

    @JsonProperty("leagueId")
    private int leagueId;

    @JsonProperty("overallRating")
    private int overallRating;

    @JsonProperty("attackRating")
    private int attackRating;

    @JsonProperty("midfieldRating")
    private int midfieldRating;

    @JsonProperty("defenceRating")
    private int defenceRating;

    @JsonProperty("transferBudget")
    private int transferBudget;

    @JsonProperty("players")
    private List<SoFifaPlayer> players;

    @JsonProperty("country")
    private String country;

    @JsonProperty("stadium")
    private String stadium;

    @JsonProperty("capacity")
    private int capacity;
}