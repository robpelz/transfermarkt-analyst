package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import com.transfermarkt.transfermarkt_analyst.repository.ScoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoutingController.class)
@DisplayName("ScoutingController Tests")
class ScoutingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScoutingRepository scoutingRepository;

    private Scouting sampleScouting;

    @BeforeEach
    void setUp() {
        sampleScouting = new Scouting();
        sampleScouting.setId(1L);
        sampleScouting.setPlayerId("player123");
        sampleScouting.setPlayerName("Moukoko");
        sampleScouting.setRating(85);
        sampleScouting.setNote("Great talent");
        sampleScouting.setTalent(90);
        sampleScouting.setSpeed(88);
        sampleScouting.setTactics(82);
        sampleScouting.setPassing(80);
        sampleScouting.setTechnique(86);
        sampleScouting.setFitness(85);
        sampleScouting.setTackling(40);
    }

    @Test
    void getAll_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/scouting")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getById_ExistingId_ReturnsScouting() throws Exception {
        when(scoutingRepository.findById(1L)).thenReturn(Optional.of(sampleScouting));

        mockMvc.perform(get("/api/scouting/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Moukoko"))
                .andExpect(jsonPath("$.rating").value(85));
    }

    @Test
    void getById_NonExistingId_Returns404() throws Exception {
        when(scoutingRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/scouting/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ValidData_ReturnsCreatedScouting() throws Exception {
        when(scoutingRepository.save(any(Scouting.class))).thenReturn(sampleScouting);

        mockMvc.perform(post("/api/scouting/player123")
                        .param("playerName", "Moukoko")
                        .param("rating", "85")
                        .param("note", "Great talent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value("player123"))
                .andExpect(jsonPath("$.playerName").value("Moukoko"));
    }

    @Test
    void create_WithMinimalParams_UsesDefaultValues() throws Exception {
        Scouting saved = new Scouting();
        saved.setPlayerId("player456");
        saved.setPlayerName("Wirtz");
        saved.setRating(90);
        saved.setTalent(70);
        saved.setSpeed(70);
        saved.setTackling(30);

        when(scoutingRepository.save(any(Scouting.class))).thenReturn(saved);

        mockMvc.perform(post("/api/scouting/player456")
                        .param("playerName", "Wirtz")
                        .param("rating", "90")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.talent").value(70))
                .andExpect(jsonPath("$.tackling").value(30));
    }

    @Test
    void update_ExistingId_UpdatesScouting() throws Exception {
        Scouting updatedScouting = new Scouting();
        updatedScouting.setId(1L);
        updatedScouting.setPlayerId("player123");
        updatedScouting.setPlayerName("Moukoko");
        updatedScouting.setRating(95);
        updatedScouting.setNote("World class!");

        when(scoutingRepository.findById(1L)).thenReturn(Optional.of(sampleScouting));
        when(scoutingRepository.save(any(Scouting.class))).thenReturn(updatedScouting);

        mockMvc.perform(put("/api/scouting/1")
                        .param("rating", "95")
                        .param("note", "World class!")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(95));
    }

    @Test
    void update_NonExistingId_Returns404() throws Exception {
        when(scoutingRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/scouting/999")
                        .param("rating", "80")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ExistingId_Returns204() throws Exception {
        when(scoutingRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/scouting/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_NonExistingId_Returns404() throws Exception {
        when(scoutingRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/scouting/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}