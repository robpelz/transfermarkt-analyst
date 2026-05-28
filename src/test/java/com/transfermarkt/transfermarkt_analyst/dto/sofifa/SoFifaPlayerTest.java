package com.transfermarkt.transfermarkt_analyst.dto.sofifa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SoFifaPlayer DTO Tests")
class SoFifaPlayerTest {

    @Test
    @DisplayName("getPrimaryPosition - mit Positionsliste gibt erste Position zurück")
    void getPrimaryPosition_WithPositions_ReturnsFirstPosition() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setPositions(List.of("Striker", "Winger", "Forward"));

        assertThat(player.getPrimaryPosition()).isEqualTo("Striker");
    }

    @Test
    @DisplayName("getPrimaryPosition - leere Positionsliste gibt N/A")
    void getPrimaryPosition_EmptyPositions_ReturnsNA() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setPositions(List.of());

        assertThat(player.getPrimaryPosition()).isEqualTo("N/A");
    }

    @Test
    @DisplayName("getPrimaryPosition - null Positionsliste gibt N/A")
    void getPrimaryPosition_NullPositions_ReturnsNA() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setPositions(null);

        assertThat(player.getPrimaryPosition()).isEqualTo("N/A");
    }

    @Test
    @DisplayName("getValueInMillion - null gibt 0")
    void getValueInMillion_NullValue_ReturnsZero() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setValue(null);

        assertThat(player.getValueInMillion()).isZero();
    }

    @Test
    @DisplayName("getValueInMillion - ? gibt 0")
    void getValueInMillion_QuestionMark_ReturnsZero() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setValue("?");

        assertThat(player.getValueInMillion()).isZero();
    }

    @Test
    @DisplayName("getOverallRating - mit overall Map gibt base zurück")
    void getOverallRating_WithOverallMap_ReturnsBase() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setOverall(Map.of("base", 85));

        assertThat(player.getOverallRating()).isEqualTo(85);
    }

    @Test
    @DisplayName("getOverallRating - null overall gibt 0")
    void getOverallRating_NullOverall_ReturnsZero() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setOverall(null);

        assertThat(player.getOverallRating()).isZero();
    }

    @Test
    @DisplayName("getPotentialRating - mit potential Map gibt base zurück")
    void getPotentialRating_WithPotentialMap_ReturnsBase() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setPotential(Map.of("base", 90));

        assertThat(player.getPotentialRating()).isEqualTo(90);
    }

    @Test
    @DisplayName("getPotentialRating - null potential gibt 0")
    void getPotentialRating_NullPotential_ReturnsZero() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setPotential(null);

        assertThat(player.getPotentialRating()).isZero();
    }

    @Test
    @DisplayName("getWageInThousands - null gibt 0")
    void getWageInThousands_NullValue_ReturnsZero() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setWage(null);

        assertThat(player.getWageInThousands()).isZero();
    }
}