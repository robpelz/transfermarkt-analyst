package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.service.TheSportsDbClient;
import com.transfermarkt.transfermarkt_analyst.service.TeamLogoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamLiveController.class)
@DisplayName("TeamLiveController Tests")
class TeamLiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TheSportsDbClient theSportsDbClient;

    @MockitoBean
    private TeamLogoMapper teamLogoMapper;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void getTopLeagues_ReturnsLeagues() throws Exception {
        mockMvc.perform(get("/api/live/teams/top-leagues")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Premier League"))
                .andExpect(jsonPath("$[1].name").value("Bundesliga"));
    }

    @Test
    void getTeamsByLeagueFromDB_PremierLeague_ReturnsTeams() throws Exception {
        mockMvc.perform(get("/api/live/teams/teams/by-league")
                        .param("league", "Premier League")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getTeamsByLeagueFromDB_Bundesliga_ReturnsTeams() throws Exception {
        mockMvc.perform(get("/api/live/teams/teams/by-league")
                        .param("league", "Bundesliga")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getTeamsByLeagueFromDB_SerieA_ReturnsTeams() throws Exception {
        mockMvc.perform(get("/api/live/teams/teams/by-league")
                        .param("league", "Serie A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getTeamsByLeagueFromDB_LaLiga_ReturnsTeams() throws Exception {
        mockMvc.perform(get("/api/live/teams/teams/by-league")
                        .param("league", "La Liga")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getTeamsByLeagueFromDB_UnknownLeague_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/live/teams/teams/by-league")
                        .param("league", "Unknown League")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTeamsByLeagueShort_PremierLeague_ReturnsTeams() throws Exception {
        mockMvc.perform(get("/api/live/teams/by-league")
                        .param("league", "Premier League")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void getTeamById_Existing_ReturnsTeam() throws Exception {
        Map<String, Object> team = new HashMap<>();
        team.put("id", "16");
        team.put("name", "Borussia Dortmund");
        team.put("logo", "logo.png");
        team.put("country", "Germany");

        when(jdbcTemplate.queryForMap(anyString(), anyString())).thenReturn(team);

        mockMvc.perform(get("/api/live/teams/teams/by-id/16")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Borussia Dortmund"));
    }

    @Test
    void getTeamById_NotFound_Returns404() throws Exception {
        when(jdbcTemplate.queryForMap(anyString(), anyString())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/live/teams/teams/by-id/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getClubPlayers_ExistingClub_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/live/teams/16/players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}