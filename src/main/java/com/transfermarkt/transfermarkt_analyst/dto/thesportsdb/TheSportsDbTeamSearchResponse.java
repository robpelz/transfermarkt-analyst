package com.transfermarkt.transfermarkt_analyst.dto.thesportsdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheSportsDbTeamSearchResponse {

    @JsonProperty("teams")
    private List<TheSportsDbTeam> teams;
}