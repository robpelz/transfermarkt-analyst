package com.transfermarkt.transfermarkt_analyst.dto.thesportsdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheSportsDbTeam {

    @JsonProperty("idTeam")
    private String id;

    @JsonProperty("strTeam")
    private String strTeam;  // ← so heißt das Feld

    @JsonProperty("strStadium")
    private String strStadium;

    @JsonProperty("intFormedYear")
    private String intFormedYear;

    @JsonProperty("strTeamBadge")
    private String strTeamBadge;

    @JsonProperty("strLeague")
    private String strLeague;

    private String localLogoUrl;


}