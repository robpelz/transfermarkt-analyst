package com.transfermarkt.transfermarkt_analyst.dto.thesportsdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheSportsDbLeague {

    @JsonProperty("idLeague")
    private String id;

    @JsonProperty("strLeague")
    private String name;

    @JsonProperty("strSport")
    private String sport;

    @JsonProperty("strCountry")
    private String country;

    @JsonProperty("strLogo")
    private String logo;
}