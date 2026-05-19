package com.transfermarkt.transfermarkt_analyst.dto.sofifa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoFifaRating {

    @JsonProperty("base")
    private int base;

    @JsonProperty("delta")
    private int delta;
}