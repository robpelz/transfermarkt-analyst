package com.transfermarkt.transfermarkt_analyst.dto.thesportsdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerImageResponse {

    @JsonProperty("player")
    private List<PlayerImage> players;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerImage {

        @JsonProperty("idPlayer")
        private String id;

        @JsonProperty("strPlayer")
        private String name;

        @JsonProperty("strThumb")
        private String thumb;  // Vorschaubild

        @JsonProperty("strCutout")
        private String cutout;  // Freigestelltes Spielerbild (am besten!)

        @JsonProperty("strRender")
        private String render;  // 3D-Render

        @JsonProperty("strBanner")
        private String banner;  // Banner für Detailseiten

        @JsonProperty("strFanart1")
        private String fanart1;  // Großes Hintergrundbild

        @JsonProperty("strFanart2")
        private String fanart2;

        @JsonProperty("strFanart3")
        private String fanart3;

        @JsonProperty("strFanart4")
        private String fanart4;

        @JsonProperty("strTeam")
        private String team;

        @JsonProperty("strNationality")
        private String nationality;
    }
}