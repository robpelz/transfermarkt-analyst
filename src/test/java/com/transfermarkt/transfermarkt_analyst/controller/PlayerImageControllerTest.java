package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.service.PlayerImageService;
import com.transfermarkt.transfermarkt_analyst.service.SoFifaClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerImageController.class)
@DisplayName("PlayerImageController Tests")
class PlayerImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SoFifaClient soFifaClient;

    @MockitoBean
    private PlayerImageService playerImageService;

    private SoFifaPlayer createSamplePlayer() {
        SoFifaPlayer player = new SoFifaPlayer();
        player.setId("12345");
        player.setName("Moukoko");
        player.setAge(18);
        return player;
    }

    private PlayerImageService.PlayerWithImage createPlayerWithImage(SoFifaPlayer player, String imageUrl) {
        return PlayerImageService.PlayerWithImage.builder()
                .player(player)
                .imageUrl(imageUrl)
                .build();
    }

    @Test
    void searchPlayerWithImage_Success_ReturnsEnrichedPlayer() throws Exception {
        List<SoFifaPlayer> players = new ArrayList<>();
        SoFifaPlayer samplePlayer = createSamplePlayer();
        players.add(samplePlayer);

        PlayerImageService.PlayerWithImage enriched = createPlayerWithImage(samplePlayer, "https://example.com/image.jpg");

        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);
        when(playerImageService.enrichWithImage(any(SoFifaPlayer.class))).thenReturn(enriched);

        mockMvc.perform(get("/api/player-images/search")
                        .param("query", "Moukoko")
                        .param("index", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player.name").value("Moukoko"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"));
    }

    @Test
    void searchPlayerWithImage_NoPlayers_Returns404() throws Exception {
        when(soFifaClient.searchPlayers("Unknown")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/player-images/search")
                        .param("query", "Unknown")
                        .param("index", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchPlayerWithImage_IndexOutOfBounds_Returns404() throws Exception {
        List<SoFifaPlayer> players = new ArrayList<>();
        players.add(createSamplePlayer());

        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);

        mockMvc.perform(get("/api/player-images/search")
                        .param("query", "Moukoko")
                        .param("index", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchPlayerWithImage_DefaultIndexZero_Works() throws Exception {
        List<SoFifaPlayer> players = new ArrayList<>();
        SoFifaPlayer samplePlayer = createSamplePlayer();
        players.add(samplePlayer);

        PlayerImageService.PlayerWithImage enriched = createPlayerWithImage(samplePlayer, "https://example.com/image.jpg");

        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);
        when(playerImageService.enrichWithImage(any(SoFifaPlayer.class))).thenReturn(enriched);

        mockMvc.perform(get("/api/player-images/search")
                        .param("query", "Moukoko")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player.name").value("Moukoko"));
    }
}