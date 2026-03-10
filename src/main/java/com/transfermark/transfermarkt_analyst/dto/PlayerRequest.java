package com.transfermark.transfermarkt_analyst.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlayerRequest {

    @NotBlank(message = "Spielername ist Pflicht")
    @Size(min = 2, max = 100, message = "Name muss zwischen 2 und 100 Zeichen liegen")
    private String name;

    @NotBlank(message = "Position ist Pflicht")
    @Pattern(regexp = "^(ST|LW|RW|MF|DM|CM|AM|CB|RB|LB|GK)$",
            message = "Ungültige Position. Erlaubt: ST, LW, RW, MF, DM, CM, AM, CB, RB, LB, GK")
    private String position;

    @NotNull(message = "Marktwert ist Pflicht")
    @Positive(message = "Marktwert muss positiv sein")
    @Max(value = 500, message = "Marktwert kann maximal 500 Mio betragen")
    private Double marketValue;

    @Min(value = 15, message = "Spieler muss mindestens 15 Jahre alt sein")
    @Max(value = 45, message = "Spieler kann maximal 45 Jahre alt sein")
    private Integer age;

    @Size(min = 2, max = 50, message = "Vereinsname muss zwischen 2 und 50 Zeichen liegen")
    private String currentClub;

    private String nationality;
    private Boolean playsInSerieA;
    private Integer competitionCount;
}