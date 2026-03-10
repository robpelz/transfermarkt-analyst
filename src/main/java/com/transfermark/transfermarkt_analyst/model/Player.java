package com.transfermark.transfermarkt_analyst.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name="players")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Spielername ist Pflicht")
    @Size(min = 2, max = 100, message = "Name muss zwischen 2 und 100 Zeichen haben")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Position ist Pflicht")
    @Pattern(regexp = "^(ST|LW|RW|MF|DM|CM|AM|CB|RB|LB|GK)$",
            message = "Ungültige Position")
    private String position;

    private LocalDate birthDate;
    private String nationality;
    private String currentClub;

    @NotNull(message = "Marktwert ist Pflicht")
    @Positive(message = "Marktwert muss positiv sein")
    @Max(value = 500, message = "Marktwert maximal 500 Mio")
    private Double marketValue;

    private int goals;
    private int assists;
    private int appearances;

    @Min(value = 15, message = "Mindestalter 15")
    @Max(value = 45, message = "Maximalalter 45")
    private Integer age;

    // Für Bewertung später
    private boolean playsInSerieA;
    private int serieAAppearances;
    private int competitionCount;
}