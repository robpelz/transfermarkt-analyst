package com.transfermarkt.transfermarkt_analyst.dto.sofifa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoFifaResponse<T> {

    @JsonProperty("page")
    private int page;

    @JsonProperty("count")
    private int count;

    @JsonProperty("has_next")
    private boolean hasNext;

    @JsonProperty("results")
    private List<T> results;

    @JsonProperty("next")
    private String next;
}