package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.dto.TransferScore;
import com.transfermarkt.transfermarkt_analyst.service.SoFifaClient;
import com.transfermarkt.transfermarkt_analyst.service.TransferScoreService;
import com.transfermarkt.transfermarkt_analyst.repository.PlayerMarketDataRepository;
import com.transfermarkt.transfermarkt_analyst.model.PlayerMarketData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerLiveController.class)
@DisplayName("PlayerLiveController Tests")
class PlayerLiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SoFifaClient soFifaClient;

    @MockitoBean
    private TransferScoreService transferScoreService;

    @MockitoBean
    private PlayerMarketDataRepository marketDataRepository;

    private SoFifaPlayer createSamplePlayer() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setId("12345");
        player.setName("Moukoko");
        player.setAge(18);
        player.setNationality("Germany");
        player.setClub("Borussia Dortmund");
        return player;
    }

    private TransferScore createSampleScore() {
        return new TransferScore(85.5, 90, 85, 95, 80, 70, "Sehr guter Transfer");
    }

    @Test
    void searchPlayers_Success_ReturnsPlayerList() throws Exception {
        List<SoFifaPlayer> players = new ArrayList<>();
        players.add(createSamplePlayer());

        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);

        mockMvc.perform(get("/api/live/players/search")
                        .param("query", "Moukoko")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Moukoko"));
    }

    @Test
    void searchPlayers_Empty_ReturnsEmptyList() throws Exception {
        when(soFifaClient.searchPlayers("Unknown")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/live/players/search")
                        .param("query", "Unknown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getPlayerScore_Success_ReturnsScore() throws Exception {
        List<SoFifaPlayer> players = new ArrayList<>();
        players.add(createSamplePlayer());

        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);
        when(transferScoreService.calculateScore(any(SoFifaPlayer.class), eq("Napoli"))).thenReturn(createSampleScore());

        mockMvc.perform(get("/api/live/players/score")
                        .param("name", "Moukoko")
                        .param("club", "Napoli")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(85.5))
                .andExpect(jsonPath("$.recommendation").value("Sehr guter Transfer"));
    }

    @Test
    void getPlayerScore_DefaultClub_ReturnsScore() throws Exception {
        List<SoFifaPlayer> players = new ArrayList<>();
        players.add(createSamplePlayer());

        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);
        when(transferScoreService.calculateScore(any(SoFifaPlayer.class), eq("Napoli"))).thenReturn(createSampleScore());

        mockMvc.perform(get("/api/live/players/score")
                        .param("name", "Moukoko")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(85.5));
    }

    @Test
    void getPlayerScore_PlayerNotFound_Returns404() throws Exception {
        when(soFifaClient.searchPlayers("Unknown")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/live/players/score")
                        .param("name", "Unknown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testScore_Found_ReturnsMessage() throws Exception {
        PlayerMarketData data = new PlayerMarketData();
        data.setMarketValue(100000000.0);

        when(marketDataRepository.findTopByPlayerNameOrderBySeasonDesc("Harry Kane")).thenReturn(Optional.of(data));

        mockMvc.perform(get("/api/live/players/test-score")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Gefunden: 1.0E8"));
    }

    @Test
    void testScore_NotFound_ReturnsNotFoundMessage() throws Exception {
        when(marketDataRepository.findTopByPlayerNameOrderBySeasonDesc("Harry Kane")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/live/players/test-score")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Nicht gefunden"));
    }
}