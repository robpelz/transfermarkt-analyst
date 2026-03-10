package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.ClubScore;
import com.transfermark.transfermarkt_analyst.model.Transfer;
import com.transfermark.transfermarkt_analyst.repository.TransferRepository;
import com.transfermark.transfermarkt_analyst.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClubScoreServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private ClubScoreServiceImpl clubScoreService;

    private List<Transfer> napoliTransfers;
    private List<Transfer> emptyTransfers;
    private AppProperties.Club clubProperties;

    @BeforeEach
    void setUp() {
        // 📝 Erfolgreiche Napoli-Transfers
        Transfer kvara = new Transfer();
        kvara.setPlayerName("Kvaratskhelia");
        kvara.setToClub("Napoli");
        kvara.setTransferFee(10.0);
        kvara.setWasSuccessful(true);

        Transfer osimhen = new Transfer();
        osimhen.setPlayerName("Osimhen");
        osimhen.setToClub("Napoli");
        osimhen.setTransferFee(70.0);
        osimhen.setWasSuccessful(true);

        Transfer kim = new Transfer();
        kim.setPlayerName("Kim Min-jae");
        kim.setToClub("Napoli");
        kim.setTransferFee(18.0);
        kim.setWasSuccessful(true);

        // 📝 Flop-Transfers Napoli
        Transfer lucca = new Transfer();
        lucca.setPlayerName("Lucca");
        lucca.setToClub("Napoli");
        lucca.setTransferFee(30.0);
        lucca.setWasSuccessful(false);

        Transfer lindstrom = new Transfer();
        lindstrom.setPlayerName("Lindstrom");
        lindstrom.setToClub("Napoli");
        lindstrom.setTransferFee(25.0);
        lindstrom.setWasSuccessful(false);

        napoliTransfers = Arrays.asList(kvara, osimhen, kim, lucca, lindstrom);
        emptyTransfers = List.of();

        // 📝 Club-Properties
        clubProperties = new AppProperties.Club();
        clubProperties.setMinTransfersForScore(3);

        lenient().when(appProperties.getClub()).thenReturn(clubProperties);
    }

    @Test
    void shouldCalculateNapoliScoreCorrectly() {
        // 📝 ********** ARRANGE **********
        System.out.println("🔧 ARRANGE: Napoli-Test vorbereiten");

        when(transferRepository.findByToClub("Napoli")).thenReturn(napoliTransfers);

        // 📝 ********** ACT **********
        System.out.println("⚡ ACT: Berechne ClubScore für Napoli");

        ClubScore score = clubScoreService.calculateClubScore("Napoli");

        // 📝 ********** ASSERT **********
        System.out.println("✅ ASSERT: Prüfe Ergebnisse");
        System.out.println("📊 Club: " + score.getClubName());
        System.out.println("📊 Erfolgsrate: " + score.getSuccessRate() + "%");
        System.out.println("📊 Transfers: " + score.getTotalTransfers());
        System.out.println("📝 Empfehlung: " + score.getRecommendation());

        // 5 Transfers, 3 erfolgreich = 60% Erfolgsrate
        assertThat(score.getClubName()).isEqualTo("Napoli");
        assertThat(score.getTotalTransfers()).isEqualTo(5);
        assertThat(score.getSuccessRate()).isEqualTo(60.0);

        // Empfehlung sollte "👍 Sehr gute Transferpolitik" sein (60-74%)
        assertThat(score.getRecommendation())
                .contains("👍", "Sehr gute");
    }

    @Test
    void shouldReturnEmptyScoreForUnknownClub() {
        // 📝 ********** ARRANGE **********
        System.out.println("🔧 ARRANGE: Unbekannter Club Test");

        when(transferRepository.findByToClub("Unbekannt")).thenReturn(emptyTransfers);

        // 📝 ********** ACT **********
        System.out.println("⚡ ACT: Berechne Score für unbekannten Club");

        ClubScore score = clubScoreService.calculateClubScore("Unbekannt");

        // 📝 ********** ASSERT **********
        System.out.println("✅ ASSERT: Prüfe Ergebnisse");
        System.out.println("📊 Club: " + score.getClubName());
        System.out.println("📊 Erfolgsrate: " + score.getSuccessRate() + "%");
        System.out.println("📊 Transfers: " + score.getTotalTransfers());

        assertThat(score.getClubName()).isEqualTo("Unbekannt");
        assertThat(score.getTotalTransfers()).isEqualTo(0);
        assertThat(score.getSuccessRate()).isEqualTo(0.0);
        assertThat(score.getRecommendation()).contains("Keine Transferdaten");
    }

    @Test
    void shouldCalculateDifferentSuccessRates() {
        // 📝 Test für verschiedene Erfolgsraten

        // 100% Erfolg
        List<Transfer> perfectTransfers = Arrays.asList(
                createTransfer("Spieler1", true),
                createTransfer("Spieler2", true),
                createTransfer("Spieler3", true)
        );

        // 33% Erfolg
        List<Transfer> poorTransfers = Arrays.asList(
                createTransfer("Spieler1", true),
                createTransfer("Spieler2", false),
                createTransfer("Spieler3", false)
        );

        // 📝 100% Test
        when(transferRepository.findByToClub("Perfect Club")).thenReturn(perfectTransfers);

        ClubScore perfectScore = clubScoreService.calculateClubScore("Perfect Club");
        System.out.println("📊 100% Club: " + perfectScore.getSuccessRate() + "% - " + perfectScore.getRecommendation());

        assertThat(perfectScore.getSuccessRate()).isEqualTo(100.0);
        assertThat(perfectScore.getRecommendation()).contains("🏆", "Weltklasse");

        // 📝 33% Test
        when(transferRepository.findByToClub("Poor Club")).thenReturn(poorTransfers);

        ClubScore poorScore = clubScoreService.calculateClubScore("Poor Club");
        System.out.println("📊 33% Club: " + poorScore.getSuccessRate() + "% - " + poorScore.getRecommendation());

        assertThat(poorScore.getSuccessRate()).isEqualTo(33.3);
        assertThat(poorScore.getRecommendation())
                .containsAnyOf("⚠️", "Verbesserungswürdig", "❌", "Alarmierend");
    }

    @Test
    void shouldHandleMinimumTransfersRequirement() {
        // 📝 Test für die Minimum-Transfers Regel
        clubProperties.setMinTransfersForScore(5); // Erst ab 5 Transfers

        List<Transfer> fewTransfers = Arrays.asList(
                createTransfer("Spieler1", true),
                createTransfer("Spieler2", true),
                createTransfer("Spieler3", false)
        ); // Nur 3 Transfers

        when(transferRepository.findByToClub("Small Club")).thenReturn(fewTransfers);

        ClubScore score = clubScoreService.calculateClubScore("Small Club");

        System.out.println("📊 Small Club (3/5 Transfers): " + score.getSuccessRate() + "%");

        // Auch wenn 66% erfolgreich, aber zu wenig Transfers
        assertThat(score.getSuccessRate()).isEqualTo(66.7);
        assertThat(score.getTotalTransfers()).isEqualTo(3);
        assertThat(score.getRecommendation()).isNotNull();
    }

    // 📝 Hilfsmethode für schnelle Transfer-Erstellung
    private Transfer createTransfer(String playerName, boolean successful) {
        Transfer transfer = new Transfer();
        transfer.setPlayerName(playerName);
        transfer.setToClub("Test Club");
        transfer.setWasSuccessful(successful);
        return transfer;
    }
}