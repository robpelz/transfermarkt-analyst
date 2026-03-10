package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.TransferScore;
import com.transfermark.transfermarkt_analyst.model.Player;
import com.transfermark.transfermarkt_analyst.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferScoreServiceTest {

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private TransferScoreServiceImpl transferScoreService;

    private Player lookman;
    private Player badPlayer;
    private Player nullPlayer;
    private Map<String, Double> testWeights;
    private Map<String, Integer> testThresholds;

    @BeforeEach
    void setUp() {
        lookman = new Player();
        lookman.setName("Ademola Lookman");
        lookman.setPosition("LW");
        lookman.setAge(28);
        lookman.setMarketValue(40.0);
        lookman.setCompetitionCount(3);

        badPlayer = new Player();
        badPlayer.setName("Teurer Alter");
        badPlayer.setPosition("ST");
        badPlayer.setAge(34);
        badPlayer.setMarketValue(100.0);
        badPlayer.setCompetitionCount(5);

        nullPlayer = new Player();
        nullPlayer.setName("Null Player");

        testWeights = new HashMap<>();
        testWeights.put("position", 0.30);
        testWeights.put("price", 0.25);
        testWeights.put("age", 0.20);
        testWeights.put("experience", 0.15);
        testWeights.put("competition", 0.10);

        testThresholds = new HashMap<>();
        testThresholds.put("must-have", 85);
        testThresholds.put("very-good", 75);
        testThresholds.put("solid", 65);
        testThresholds.put("average", 55);
        testThresholds.put("risky", 45);
    }

    @Test
    void shouldCalculateScoreForLookmanToNapoli() {
        AppProperties.Scoring scoring = new AppProperties.Scoring();
        scoring.setWeights(testWeights);
        scoring.setThresholds(testThresholds);
        when(appProperties.getScoring()).thenReturn(scoring);

        TransferScore score = transferScoreService.calculateScore(lookman, "Napoli");

        assertThat(score.getTotalScore()).isBetween(65.0, 75.0);
        assertThat(score.getRecommendation()).contains("👍", "Solider");
    }

    @Test
    void shouldReturnLowScoreForExpensiveOldPlayer() {
        AppProperties.Scoring scoring = new AppProperties.Scoring();
        scoring.setWeights(testWeights);
        scoring.setThresholds(testThresholds);
        when(appProperties.getScoring()).thenReturn(scoring);

        TransferScore score = transferScoreService.calculateScore(badPlayer, "Napoli");

        assertThat(score.getTotalScore()).isBetween(50.0, 70.0);
        assertThat(score.getPriceScore()).isEqualTo(80.0);
        assertThat(score.getAgeScore()).isEqualTo(20.0);
        assertThat(score.getExperienceScore()).isEqualTo(80.0);
        assertThat(score.getCompetitionScore()).isEqualTo(30.0);
    }

    @Test
    void shouldHandleNullValues() {
        AppProperties.Scoring scoring = new AppProperties.Scoring();
        scoring.setWeights(testWeights);
        scoring.setThresholds(testThresholds);
        when(appProperties.getScoring()).thenReturn(scoring);

        TransferScore score = transferScoreService.calculateScore(nullPlayer, "Napoli");

        assertThat(score).isNotNull();
        assertThat(score.getTotalScore()).isBetween(0.0, 100.0);
        assertThat(score.getRecommendation()).isNotNull();
    }

    @Test
    void shouldCalculateCorrectPositionScoreForNapoli() {
        AppProperties.Scoring scoring = new AppProperties.Scoring();
        scoring.setWeights(testWeights);
        scoring.setThresholds(testThresholds);
        when(appProperties.getScoring()).thenReturn(scoring);

        Player player = new Player();
        player.setAge(25);
        player.setMarketValue(30.0);
        player.setCompetitionCount(0);

        // Teste nur die Positionen, die wirklich im Code sind
        player.setPosition("LW");
        assertThat(transferScoreService.calculateScore(player, "Napoli").getPositionScore()).isEqualTo(90.0);

        player.setPosition("RW");
        assertThat(transferScoreService.calculateScore(player, "Napoli").getPositionScore()).isEqualTo(90.0);

        player.setPosition("RB");
        assertThat(transferScoreService.calculateScore(player, "Napoli").getPositionScore()).isEqualTo(85.0);

        player.setPosition("ST");
        assertThat(transferScoreService.calculateScore(player, "Napoli").getPositionScore()).isEqualTo(60.0);
    }

    @Test
    void shouldCalculateAgeScoreCorrectly() {
        AppProperties.Scoring scoring = new AppProperties.Scoring();
        scoring.setWeights(testWeights);
        scoring.setThresholds(testThresholds);
        when(appProperties.getScoring()).thenReturn(scoring);

        Player player = new Player();
        player.setPosition("ST");
        player.setMarketValue(30.0);
        player.setCompetitionCount(0);

        player.setAge(21);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getAgeScore()).isEqualTo(90.0);

        player.setAge(24);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getAgeScore()).isEqualTo(85.0);

        player.setAge(27);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getAgeScore()).isEqualTo(80.0);

        player.setAge(30);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getAgeScore()).isEqualTo(60.0);

        player.setAge(33);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getAgeScore()).isEqualTo(40.0);

        player.setAge(34);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getAgeScore()).isEqualTo(20.0);
    }

    @Test
    void shouldCalculateCompetitionScoreCorrectly() {
        AppProperties.Scoring scoring = new AppProperties.Scoring();
        scoring.setWeights(testWeights);
        scoring.setThresholds(testThresholds);
        when(appProperties.getScoring()).thenReturn(scoring);

        Player player = new Player();
        player.setPosition("ST");
        player.setAge(25);
        player.setMarketValue(30.0);

        player.setCompetitionCount(0);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getCompetitionScore()).isEqualTo(100.0);

        player.setCompetitionCount(1);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getCompetitionScore()).isEqualTo(80.0);

        player.setCompetitionCount(2);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getCompetitionScore()).isEqualTo(60.0);

        player.setCompetitionCount(3);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getCompetitionScore()).isEqualTo(40.0);

        player.setCompetitionCount(5);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getCompetitionScore()).isEqualTo(30.0);

        player.setCompetitionCount(6);
        assertThat(transferScoreService.calculateScore(player, "Napoli").getCompetitionScore()).isEqualTo(10.0);
    }
}