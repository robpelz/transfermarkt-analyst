package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.service.DatabasePlayerService;
import com.transfermarkt.transfermarkt_analyst.service.ScoreService;
import com.transfermarkt.transfermarkt_analyst.service.SoFifaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoFifaController.class)
@DisplayName("SoFifaController Tests")
class SoFifaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SoFifaClient soFifaClient;

    @MockitoBean
    private DatabasePlayerService databasePlayerService;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private ScoreService scoreService;

    private SoFifaPlayer samplePlayer;

    @BeforeEach
    void setUp() {
        samplePlayer = new SoFifaPlayer();
        samplePlayer.setId("12345");
        samplePlayer.setName("Moukoko");
        samplePlayer.setAge(18);
        samplePlayer.setNationality("Germany");
        samplePlayer.setClub("Borussia Dortmund");
        samplePlayer.setValue("10 Mio. €");
        samplePlayer.setPositions(List.of("Striker"));
    }

    @Test
    void search_Success_ReturnsPlayerList() throws Exception {
        List<SoFifaPlayer> players = List.of(samplePlayer);
        when(soFifaClient.searchPlayers("Moukoko")).thenReturn(players);

        mockMvc.perform(get("/api/sofifa/search")
                        .param("query", "Moukoko")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Moukoko"));
    }

    @Test
    void search_Empty_ReturnsEmptyList() throws Exception {
        when(soFifaClient.searchPlayers("Unknown")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/sofifa/search")
                        .param("query", "Unknown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void health_ReturnsOk() throws Exception {
        when(soFifaClient.isCrsetAvailable()).thenReturn(true);

        mockMvc.perform(get("/api/sofifa/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ CRSet verbunden"));
    }

    @Test
    void health_NotAvailable_ReturnsError() throws Exception {
        when(soFifaClient.isCrsetAvailable()).thenReturn(false);

        mockMvc.perform(get("/api/sofifa/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("❌ CRSet nicht erreichbar"));
    }

    @Test
    void getPlayerByIdSimple_Existing_ReturnsPlayer() throws Exception {
        when(databasePlayerService.getPlayerById("12345")).thenReturn(samplePlayer);

        mockMvc.perform(get("/api/sofifa/player/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Moukoko"))
                .andExpect(jsonPath("$.id").value("12345"));
    }

    @Test
    void getPlayerByIdSimple_NotFound_Returns404() throws Exception {
        when(databasePlayerService.getPlayerById("99999")).thenReturn(null);

        mockMvc.perform(get("/api/sofifa/player/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlayerByName_Existing_ReturnsPlayer() throws Exception {
        List<SoFifaPlayer> players = List.of(samplePlayer);
        when(databasePlayerService.searchPlayers("Moukoko")).thenReturn(players);

        mockMvc.perform(get("/api/sofifa/player/name/Moukoko")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Moukoko"));
    }

    @Test
    void getPlayerByName_NotFound_Returns404() throws Exception {
        when(databasePlayerService.searchPlayers("Unknown")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/sofifa/player/name/Unknown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlayerMarketValue_Existing_ReturnsValue() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq("12345"))).thenReturn("30000000");

        mockMvc.perform(get("/api/sofifa/player/12345/market-value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.market_value").isNotEmpty());
    }

    @Test
    void getPlayerMarketValue_NotFound_ReturnsQuestionMark() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq("99999"))).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/sofifa/player/99999/market-value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.market_value").value("?"));
    }

    @Test
    void comparePlayers_BothExist_ReturnsComparison() throws Exception {
        SoFifaPlayer player1 = new SoFifaPlayer();
        player1.setId("12345");
        player1.setName("Moukoko");
        player1.setAge(18);
        player1.setValue("10 Mio. €");
        player1.setClub("Dortmund");
        player1.setNationality("Germany");
        player1.setPositions(List.of("Striker"));

        SoFifaPlayer player2 = new SoFifaPlayer();
        player2.setId("54321");
        player2.setName("Bellingham");
        player2.setAge(20);
        player2.setValue("100 Mio. €");
        player2.setClub("Real Madrid");
        player2.setNationality("England");
        player2.setPositions(List.of("Midfielder"));

        when(databasePlayerService.getPlayerById("12345")).thenReturn(player1);
        when(databasePlayerService.getPlayerById("54321")).thenReturn(player2);

        mockMvc.perform(get("/api/sofifa/compare")
                        .param("player1Id", "12345")
                        .param("player2Id", "54321")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player1.name").value("Moukoko"))
                .andExpect(jsonPath("$.player2.name").value("Bellingham"));
    }

    @Test
    void comparePlayers_OneNotFound_Returns404() throws Exception {
        when(databasePlayerService.getPlayerById("12345")).thenReturn(samplePlayer);
        when(databasePlayerService.getPlayerById("99999")).thenReturn(null);

        mockMvc.perform(get("/api/sofifa/compare")
                        .param("player1Id", "12345")
                        .param("player2Id", "99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTeamById_Existing_ReturnsTeam() throws Exception {
        Map<String, Object> team = new HashMap<>();
        team.put("id", "16");
        team.put("name", "Borussia Dortmund");
        team.put("logo", "logo.png");
        team.put("country", "Germany");

        when(jdbcTemplate.queryForMap(anyString(), eq("16"))).thenReturn(team);

        mockMvc.perform(get("/api/sofifa/teams/by-id/16")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Borussia Dortmund"));
    }

    @Test
    void getTeamById_NotFound_Returns404() throws Exception {
        when(jdbcTemplate.queryForMap(anyString(), eq("99999"))).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/sofifa/teams/by-id/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}