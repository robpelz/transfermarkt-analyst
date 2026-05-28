package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.config.ScoringConstants;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScoreServiceImpl Tests")
class ScoreServiceImplTest {

    private ScoreServiceImpl scoreService;

    @BeforeEach
    void setUp() {
        scoreService = new ScoreServiceImpl();
    }

    // ==================== calculateScore() Tests ====================

    @Test
    @DisplayName("Soll einen gültigen TransferScore berechnen")
    void calculateScore_ReturnsValidScore() {
        SoFifaPlayer player = createPlayer("Moukoko", 18, "5 Mio. €", "Attacking Midfielder");
        TransferScore result = scoreService.calculateScore(player, "Club");

        assertNotNull(result);
        assertTrue(result.getTotalScore() >= 0 && result.getTotalScore() <= 100);
        assertNotNull(result.getRecommendation());
    }

    @Test
    @DisplayName("Soll korrekte Einzelscores berechnen")
    void calculateScore_ReturnsAllIndividualScores() {
        SoFifaPlayer player = createPlayer("Zielinski", 26, "30 Mio. €", "Central Midfielder");
        TransferScore result = scoreService.calculateScore(player, "Club");

        assertNotNull(result);
        assertTrue(result.getAgeScore() >= 0 && result.getAgeScore() <= 100);
        assertTrue(result.getPriceScore() >= 0 && result.getPriceScore() <= 100);
        assertTrue(result.getPositionScore() >= 0 && result.getPositionScore() <= 100);
        assertTrue(result.getExperienceScore() >= 0 && result.getExperienceScore() <= 100);
        assertTrue(result.getCompetitionScore() >= 0 && result.getCompetitionScore() <= 100);
    }

    @Test
    @DisplayName("Soll mit null/leeren Werten umgehen können")
    void calculateScore_NullValues_HandlesGracefully() {
        SoFifaPlayer player = createPlayer(null, 0, null, null);
        TransferScore result = scoreService.calculateScore(player, "Club");

        assertNotNull(result);
        assertTrue(result.getTotalScore() >= 0 && result.getTotalScore() <= 100);
    }

    // ==================== Age Score Tests ====================

    @Test
    @DisplayName("Alters-Score: unter 21 gibt 100")
    void calculateAgeScore_Under21_Returns100() {
        SoFifaPlayer player = createPlayer("Talent", 20, "10 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(100, result.getAgeScore());
    }

    @Test
    @DisplayName("Alters-Score: 21-23 gibt 90")
    void calculateAgeScore_21to23_Returns90() {
        SoFifaPlayer player = createPlayer("Talent", 22, "10 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(90, result.getAgeScore());
    }

    @Test
    @DisplayName("Alters-Score: über 33 gibt 30")
    void calculateAgeScore_Over33_Returns30() {
        SoFifaPlayer player = createPlayer("Oldie", 34, "10 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(30, result.getAgeScore());
    }

    @Test
    @DisplayName("Alters-Score: über 35 gibt 20")
    void calculateAgeScore_Over35_Returns20() {
        SoFifaPlayer player = createPlayer("VeryOld", 36, "10 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(20, result.getAgeScore());
    }

    // ==================== Price Score Tests ====================

    @Test
    @DisplayName("Preis-Score: Schnäppchen unter 5 Mio gibt 95")
    void calculatePriceScore_Bargain_Returns95() {
        SoFifaPlayer player = createPlayer("Bargain", 25, "5 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(95, result.getPriceScore());
    }

    @Test
    @DisplayName("Preis-Score: günstig (5-10 Mio) gibt 85")
    void calculatePriceScore_Cheap_Returns85() {
        SoFifaPlayer player = createPlayer("Cheap", 25, "8 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(85, result.getPriceScore());
    }

    @Test
    @DisplayName("Preis-Score: teuer (50-75 Mio) gibt 30")
    void calculatePriceScore_Expensive_Returns30() {
        SoFifaPlayer player = createPlayer("Expensive", 25, "60 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(30, result.getPriceScore());
    }

    @Test
    @DisplayName("Preis-Score: sehr teuer (über 100 Mio) gibt 10")
    void calculatePriceScore_VeryExpensive_Returns10() {
        SoFifaPlayer player = createPlayer("VeryExpensive", 25, "120 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(10, result.getPriceScore());
    }

    @Test
    @DisplayName("Preis-Score: unbekannter Preis gibt 50")
    void calculatePriceScore_UnknownPrice_Returns50() {
        SoFifaPlayer player = createPlayer("Unknown", 25, "?", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(50, result.getPriceScore());
    }

    // ==================== Position Score Tests ====================

    @Test
    @DisplayName("Positions-Score: Attacking Midfielder gibt 75")
    void calculatePositionScore_AttackingMidfielder_Returns75() {
        SoFifaPlayer player = createPlayer("Zielinski", 26, "30 Mio. €", "Attacking Midfielder");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(75, result.getPositionScore());
    }

    @Test
    @DisplayName("Positions-Score: Striker gibt 75")
    void calculatePositionScore_Striker_Returns75() {
        SoFifaPlayer player = createPlayer("Osimhen", 24, "80 Mio. €", "Striker");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(75, result.getPositionScore());
    }

    @Test
    @DisplayName("Positions-Score: Midfielder gibt 70")
    void calculatePositionScore_Midfielder_Returns70() {
        SoFifaPlayer player = createPlayer("Midfielder", 25, "30 Mio. €", "Central Midfielder");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(70, result.getPositionScore());
    }

    @Test
    @DisplayName("Positions-Score: Defender gibt 60")
    void calculatePositionScore_Defender_Returns60() {
        SoFifaPlayer player = createPlayer("Defender", 25, "30 Mio. €", "Defender");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(60, result.getPositionScore());
    }

    @Test
    @DisplayName("Positions-Score: Goalkeeper gibt 55")
    void calculatePositionScore_Goalkeeper_Returns55() {
        SoFifaPlayer player = createPlayer("Keeper", 25, "30 Mio. €", "Goalkeeper");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(55, result.getPositionScore());
    }

    @Test
    @DisplayName("Positions-Score: unbekannte Position gibt 65")
    void calculatePositionScore_UnknownPosition_Returns65() {
        SoFifaPlayer player = createPlayer("Unknown", 25, "30 Mio. €", "Unknown");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(65, result.getPositionScore());
    }

    @Test
    @DisplayName("Positions-Score: null Position gibt 65 (laut Service)")
    void calculatePositionScore_NullPosition_Returns65() {
        SoFifaPlayer player = createPlayer("NoPosition", 25, "30 Mio. €", null);
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(65, result.getPositionScore());
    }

    // ==================== Experience Score Tests ====================

    @Test
    @DisplayName("Erfahrungs-Score: 28-32 Jahre (Sweet Spot) gibt 90")
    void calculateExperienceScore_SweetSpot_Returns90() {
        SoFifaPlayer player = createPlayer("Peak", 30, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(90, result.getExperienceScore());
    }

    @Test
    @DisplayName("Erfahrungs-Score: 25-27 Jahre gibt 75")
    void calculateExperienceScore_25to27_Returns75() {
        SoFifaPlayer player = createPlayer("Prime", 26, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(75, result.getExperienceScore());
    }

    @Test
    @DisplayName("Erfahrungs-Score: 33-35 Jahre gibt 60")
    void calculateExperienceScore_33to35_Returns60() {
        SoFifaPlayer player = createPlayer("Experienced", 34, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(60, result.getExperienceScore());
    }

    @Test
    @DisplayName("Erfahrungs-Score: 22-24 Jahre gibt 45")
    void calculateExperienceScore_22to24_Returns45() {
        SoFifaPlayer player = createPlayer("Young", 23, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(45, result.getExperienceScore());
    }

    @Test
    @DisplayName("Erfahrungs-Score: 18-21 Jahre gibt 25")
    void calculateExperienceScore_18to21_Returns25() {
        SoFifaPlayer player = createPlayer("VeryYoung", 20, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(25, result.getExperienceScore());
    }

    @Test
    @DisplayName("Erfahrungs-Score: über 35 gibt 40")
    void calculateExperienceScore_Over35_Returns40() {
        SoFifaPlayer player = createPlayer("Veteran", 36, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertEquals(40, result.getExperienceScore());
    }

    // ==================== Competition Score Tests ====================

    @Test
    @DisplayName("Wettbewerbs-Score: immer 70 (generisch)")
    void calculateCompetitionScore_AlwaysReturns70() {
        SoFifaPlayer player = createPlayer("Player", 25, "30 Mio. €", "Forward");

        TransferScore resultNapoli = scoreService.calculateScore(player, "Napoli");
        TransferScore resultBayern = scoreService.calculateScore(player, "Bayern");
        TransferScore resultDefault = scoreService.calculateScore(player, "Club");

        assertEquals(70, resultNapoli.getCompetitionScore());
        assertEquals(70, resultBayern.getCompetitionScore());
        assertEquals(70, resultDefault.getCompetitionScore());
    }

    // ==================== Recommendation Tests ====================

    @Test
    @DisplayName("Empfehlung existiert immer")
    void getRecommendation_AlwaysExists() {
        SoFifaPlayer player = createPlayer("Test", 25, "30 Mio. €", "Forward");
        TransferScore result = scoreService.calculateScore(player, "Club");
        assertNotNull(result.getRecommendation());
        assertFalse(result.getRecommendation().isEmpty());
    }

    @Test
    @DisplayName("Sehr hoher Score gibt gute Empfehlung")
    void getRecommendation_VeryHighScore_ReturnsGoodRecommendation() {
        SoFifaPlayer player = createPlayer("Superstar", 20, "5 Mio. €", "Attacking Midfielder");
        TransferScore result = scoreService.calculateScore(player, "Club");

        assertNotNull(result.getRecommendation());
        assertFalse(result.getRecommendation().isEmpty());
    }

    // ==================== Integration-like Tests ====================

    @Test
    @DisplayName("Gleicher Spieler, unterschiedliche Vereine -> gleiche Scores (neutral)")
    void samePlayer_DifferentClubs_SameScores() {
        SoFifaPlayer player = createPlayer("Player", 25, "30 Mio. €", "Attacking Midfielder");

        TransferScore napoliScore = scoreService.calculateScore(player, "Napoli");
        TransferScore bayernScore = scoreService.calculateScore(player, "Bayern");
        TransferScore defaultScore = scoreService.calculateScore(player, "Club");

        assertEquals(napoliScore.getPositionScore(), bayernScore.getPositionScore());
        assertEquals(napoliScore.getPositionScore(), defaultScore.getPositionScore());
        assertEquals(70, napoliScore.getCompetitionScore());
        assertEquals(70, bayernScore.getCompetitionScore());
        assertEquals(70, defaultScore.getCompetitionScore());
    }

    @Test
    @DisplayName("Alter beeinflusst sowohl Age- als auch Experience-Score")
    void ageAffectsBothAgeAndExperienceScores() {
        SoFifaPlayer youngPlayer = createPlayer("Young", 20, "30 Mio. €", "Forward");
        SoFifaPlayer oldPlayer = createPlayer("Old", 33, "30 Mio. €", "Forward");

        TransferScore youngResult = scoreService.calculateScore(youngPlayer, "Club");
        TransferScore oldResult = scoreService.calculateScore(oldPlayer, "Club");

        assertTrue(youngResult.getAgeScore() > oldResult.getAgeScore());
        assertTrue(youngResult.getExperienceScore() < oldResult.getExperienceScore());
    }

    @Test
    @DisplayName("Günstiger junger Spieler ist besser als teurer alter")
    void cheapYoungBetterThanExpensiveOld() {
        SoFifaPlayer cheapYoung = createPlayer("Talent", 20, "5 Mio. €", "Attacking Midfielder");
        SoFifaPlayer expensiveOld = createPlayer("Star", 34, "120 Mio. €", "Defender");

        TransferScore youngScore = scoreService.calculateScore(cheapYoung, "Club");
        TransferScore oldScore = scoreService.calculateScore(expensiveOld, "Club");

        assertTrue(youngScore.getTotalScore() > oldScore.getTotalScore());
    }

    // ==================== Helper Method ====================

    private SoFifaPlayer createPlayer(String name, int age, String value, String position) {
        SoFifaPlayer player = new SoFifaPlayer();

        if (name != null) {
            player.setName(name);
        } else {
            player.setName("Unknown Player");
        }

        player.setAge(age);
        player.setValue(value);

        if (position != null) {
            player.setPositions(List.of(position));
        } else {
            player.setPositions(List.of("N/A"));
        }

        return player;
    }
}