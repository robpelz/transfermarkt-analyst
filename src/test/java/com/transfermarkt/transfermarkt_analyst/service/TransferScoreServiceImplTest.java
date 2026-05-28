package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.config.AppProperties;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransferScoreServiceImpl Tests")
class TransferScoreServiceImplTest {

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.Scoring scoring;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TransferScoreServiceImpl transferScoreService;

    private SoFifaPlayer samplePlayer;

    @BeforeEach
    void setUp() {
        // Test-Spieler mit korrektem Preis-Format für getValueInMillion()
        samplePlayer = new SoFifaPlayer();
        samplePlayer.setName("Moukoko");
        samplePlayer.setAge(18);
        samplePlayer.setValue("€10M");
        samplePlayer.setPositions(List.of("Attacking Midfielder"));

        // Gewichte mocken
        Map<String, Double> weights = new HashMap<>();
        weights.put("position", 0.30);
        weights.put("price", 0.25);
        weights.put("age", 0.20);
        weights.put("experience", 0.15);
        weights.put("competition", 0.10);

        // Schwellwerte mocken
        Map<String, Integer> thresholds = new HashMap<>();
        thresholds.put("must-have", 85);
        thresholds.put("very-good", 75);
        thresholds.put("solid", 65);
        thresholds.put("average", 55);
        thresholds.put("risky", 45);

        // lenient() verhindert UnnecessaryStubbingException
        lenient().when(scoring.getWeights()).thenReturn(weights);
        lenient().when(scoring.getThresholds()).thenReturn(thresholds);
        lenient().when(appProperties.getScoring()).thenReturn(scoring);
    }

    // ==================== calculateScore() Tests ====================

    @Test
    @DisplayName("calculateScore - Fallback zu Player-Daten wenn DB nichts liefert")
    void calculateScore_fallbackToPlayerData_whenDbReturnsNull() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(samplePlayer, "Club");

        assertNotNull(result);
        assertThat(result.getTotalScore()).isBetween(0.0, 100.0);
        assertThat(result.getRecommendation()).isNotNull();
    }

    @Test
    @DisplayName("calculateScore - DB-Daten werden bevorzugt verwendet")
    void calculateScore_usesDbData_whenAvailable() {
        when(jdbcTemplate.queryForObject(contains("wert"), eq(String.class), anyString()))
                .thenReturn("50000000");
        when(jdbcTemplate.queryForObject(contains("date_of_birth"), eq(String.class), anyString()))
                .thenReturn("2000-01-01");
        when(jdbcTemplate.queryForObject(contains("position"), eq(String.class), anyString()))
                .thenReturn("Defender");

        TransferScore result = transferScoreService.calculateScore(samplePlayer, "Club");

        assertNotNull(result);
        assertThat(result.getPriceScore()).isBetween(65.0, 75.0);
    }

    @Test
    @DisplayName("calculateScore - junger Spieler mit niedrigem Marktwert gibt hohen Score")
    void calculateScore_youngCheapPlayer_returnsHighScore() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(samplePlayer, "Club");

        assertThat(result.getAgeScore()).isGreaterThan(80);
        assertThat(result.getPriceScore()).isGreaterThan(80);
    }

    @Test
    @DisplayName("calculateScore - alter Spieler mit hohem Marktwert gibt niedrigen Score")
    void calculateScore_oldExpensivePlayer_returnsLowScore() {
        SoFifaPlayer oldPlayer = new SoFifaPlayer();
        oldPlayer.setName("Ronaldo");
        oldPlayer.setAge(37);
        oldPlayer.setValue("€150M");
        oldPlayer.setPositions(List.of("Striker"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(oldPlayer, "Club");

        assertThat(result.getAgeScore()).isLessThan(30);
        assertThat(result.getPriceScore()).isLessThan(50);
    }

    @Test
    @DisplayName("calculateScore - offensive Positionen werden bevorzugt bewertet")
    void calculateScore_attackingPositionGetsHigherScore() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(samplePlayer, "Club");

        // Attacking Midfielder sollte guten PositionScore bekommen
        assertThat(result.getPositionScore()).isGreaterThan(70);
    }

    // ==================== comparePlayers() Tests ====================

    @Test
    @DisplayName("comparePlayers - erster Spieler besser -> Nachricht mit erstem Namen")
    void comparePlayers_firstPlayerBetter_returnsFirstPlayerMessage() {
        SoFifaPlayer player1 = new SoFifaPlayer();
        player1.setName("Haaland");
        player1.setAge(23);
        player1.setValue("€100M");
        player1.setPositions(List.of("Striker"));

        SoFifaPlayer player2 = new SoFifaPlayer();
        player2.setName("Moukoko");
        player2.setAge(18);
        player2.setValue("€10M");
        player2.setPositions(List.of("Attacking Midfielder"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        String result = transferScoreService.comparePlayers(player1, player2, "Club");

        // Moukoko (jung + günstig) sollte besser sein
        assertThat(result).contains("Moukoko ist besser");
    }

    @Test
    @DisplayName("comparePlayers - zweiter Spieler besser -> Nachricht mit zweitem Namen")
    void comparePlayers_secondPlayerBetter_returnsSecondPlayerMessage() {
        SoFifaPlayer player1 = new SoFifaPlayer();
        player1.setName("Flop");
        player1.setAge(35);
        player1.setValue("€100M");
        player1.setPositions(List.of("Defender"));

        SoFifaPlayer player2 = new SoFifaPlayer();
        player2.setName("Talent");
        player2.setAge(18);
        player2.setValue("€5M");
        player2.setPositions(List.of("Attacking Midfielder"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        String result = transferScoreService.comparePlayers(player1, player2, "Club");

        assertThat(result).contains("Talent ist besser");
    }

    @Test
    @DisplayName("comparePlayers - gleiche Spieler -> Gleichstandsnachricht")
    void comparePlayers_samePlayers_returnsTieMessage() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setName("Same");
        player.setAge(25);
        player.setValue("€20M");
        player.setPositions(List.of("Midfielder"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        String result = transferScoreService.comparePlayers(player, player, "Club");

        assertThat(result).contains("gleich gut");
    }

    // ==================== getCurrentWeights() Tests ====================

    @Test
    @DisplayName("getCurrentWeights - gibt aktuelle Gewichte zurück")
    void getCurrentWeights_returnsWeights() {
        Map<String, Double> result = transferScoreService.getCurrentWeights();

        assertThat(result).isNotNull();
        assertThat(result).containsKeys("position", "price", "age", "experience", "competition");
    }

    // ==================== isEvaluable() Tests ====================

    @Test
    @DisplayName("isEvaluable - gültiger Spieler gibt true")
    void isEvaluable_validPlayer_returnsTrue() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setName("Valid");

        assertTrue(transferScoreService.isEvaluable(player));
    }

    @Test
    @DisplayName("isEvaluable - null Spieler gibt false")
    void isEvaluable_nullPlayer_returnsFalse() {
        assertFalse(transferScoreService.isEvaluable(null));
    }

    @Test
    @DisplayName("isEvaluable - Spieler ohne Namen gibt false")
    void isEvaluable_playerWithoutName_returnsFalse() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setName(null);

        assertFalse(transferScoreService.isEvaluable(player));
    }

    @Test
    @DisplayName("isEvaluable - Spieler mit leerem Namen gibt false")
    void isEvaluable_playerWithEmptyName_returnsFalse() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setName("");

        assertFalse(transferScoreService.isEvaluable(player));
    }

    // ==================== Preis-Parser Tests ====================

    @Test
    @DisplayName("calculateScore - Preisformat '€5M' wird korrekt geparst (günstig -> hoher Score)")
    void calculateScore_parsesMillionFormatCorrectly() {
        SoFifaPlayer cheapPlayer = new SoFifaPlayer();
        cheapPlayer.setName("Bargain");
        cheapPlayer.setAge(22);
        cheapPlayer.setValue("€5M");
        cheapPlayer.setPositions(List.of("Forward"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(cheapPlayer, "Club");

        // Preis < 30 Mio. -> 90
        assertThat(result.getPriceScore()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("calculateScore - Preisformat '€150M' wird korrekt geparst (teuer -> niedriger Score)")
    void calculateScore_parsesHighMillionFormatCorrectly() {
        SoFifaPlayer expensivePlayer = new SoFifaPlayer();
        expensivePlayer.setName("Star");
        expensivePlayer.setAge(26);
        expensivePlayer.setValue("€150M");
        expensivePlayer.setPositions(List.of("Forward"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(expensivePlayer, "Club");

        // Preis > 120 Mio. -> 40
        assertThat(result.getPriceScore()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("calculateScore - unbekannter Preis (?) wird zu 0 und dann default 50")
    void calculateScore_unknownPrice_ReturnsDefault50() {
        SoFifaPlayer unknownPlayer = new SoFifaPlayer();
        unknownPlayer.setName("Unknown");
        unknownPlayer.setAge(25);
        unknownPlayer.setValue("?");
        unknownPlayer.setPositions(List.of("Forward"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(unknownPlayer, "Club");

        assertThat(result.getPriceScore()).isEqualTo(50.0);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("calculateScore - Position null gibt default 60")
    void calculateScore_nullPosition_ReturnsDefault60() {
        SoFifaPlayer noPositionPlayer = new SoFifaPlayer();
        noPositionPlayer.setName("NoPosition");
        noPositionPlayer.setAge(25);
        noPositionPlayer.setValue("€20M");
        noPositionPlayer.setPositions(null);

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(noPositionPlayer, "Club");

        assertThat(result.getPositionScore()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("calculateScore - leere Positionsliste gibt default 60")
    void calculateScore_emptyPositionList_ReturnsDefault60() {
        SoFifaPlayer emptyPositionPlayer = new SoFifaPlayer();
        emptyPositionPlayer.setName("NoPosition");
        emptyPositionPlayer.setAge(25);
        emptyPositionPlayer.setValue("€20M");
        emptyPositionPlayer.setPositions(List.of());

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(emptyPositionPlayer, "Club");

        assertThat(result.getPositionScore()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("calculateScore - null Value in Player führt zu Fallback 50")
    void calculateScore_nullValue_ReturnsDefault50() {
        SoFifaPlayer nullValuePlayer = new SoFifaPlayer();
        nullValuePlayer.setName("NullValue");
        nullValuePlayer.setAge(25);
        nullValuePlayer.setValue(null);
        nullValuePlayer.setPositions(List.of("Forward"));

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), anyString()))
                .thenReturn(null);

        TransferScore result = transferScoreService.calculateScore(nullValuePlayer, "Club");

        assertThat(result.getPriceScore()).isEqualTo(50.0);
    }
}