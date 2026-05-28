package com.transfermarkt.transfermarkt_analyst.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import com.transfermarkt.transfermarkt_analyst.repository.ScoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ScoutingController Integration Tests")
class ScoutingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScoutingRepository scoutingRepository;

    private Scouting sampleScouting;

    @BeforeEach
    void setUp() {
        scoutingRepository.deleteAll();

        sampleScouting = new Scouting();
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

    // ==================== GET /api/scouting ====================

    @Test
    @DisplayName("GET /api/scouting - leere Liste zurückgeben")
    void getAll_EmptyDatabase_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/scouting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/scouting - alle Scouting-Einträge zurückgeben")
    void getAll_WithData_ReturnsAllEntries() throws Exception {
        scoutingRepository.save(sampleScouting);

        mockMvc.perform(get("/api/scouting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].playerName").value("Moukoko"))
                .andExpect(jsonPath("$[0].rating").value(85));
    }

    // ==================== GET /api/scouting/{id} ====================

    @Test
    @DisplayName("GET /api/scouting/{id} - existierende ID gibt 200")
    void getById_ExistingId_ReturnsScouting() throws Exception {
        Scouting saved = scoutingRepository.save(sampleScouting);

        mockMvc.perform(get("/api/scouting/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Moukoko"))
                .andExpect(jsonPath("$.rating").value(85));
    }

    @Test
    @DisplayName("GET /api/scouting/{id} - nicht existierende ID gibt 404")
    void getById_NonExistingId_Returns404() throws Exception {
        mockMvc.perform(get("/api/scouting/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== POST /api/scouting/{playerId} ====================

    @Test
    @DisplayName("POST /api/scouting/{playerId} - neuen Scouting-Eintrag erstellen")
    void create_ValidData_ReturnsCreatedScouting() throws Exception {
        mockMvc.perform(post("/api/scouting/player123")
                        .param("playerName", "Moukoko")
                        .param("rating", "85")
                        .param("note", "Great talent")
                        .param("talent", "90")
                        .param("speed", "88")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value("player123"))
                .andExpect(jsonPath("$.playerName").value("Moukoko"))
                .andExpect(jsonPath("$.rating").value(85))
                .andExpect(jsonPath("$.talent").value(90));

        // Verifiziere in der Datenbank
        List<Scouting> scoutings = scoutingRepository.findAll();
        assertThat(scoutings).hasSize(1);
        assertThat(scoutings.get(0).getPlayerName()).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("POST /api/scouting/{playerId} - mit minimalen Parametern (Defaultwerte)")
    void create_WithMinimalParams_UsesDefaultValues() throws Exception {
        mockMvc.perform(post("/api/scouting/player456")
                        .param("playerName", "Wirtz")
                        .param("rating", "90")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.talent").value(70))
                .andExpect(jsonPath("$.speed").value(70))
                .andExpect(jsonPath("$.tackling").value(30));
    }

    // ==================== PUT /api/scouting/{id} ====================

    @Test
    @DisplayName("PUT /api/scouting/{id} - existierenden Eintrag aktualisieren")
    void update_ExistingId_UpdatesScouting() throws Exception {
        Scouting saved = scoutingRepository.save(sampleScouting);

        mockMvc.perform(put("/api/scouting/{id}", saved.getId())
                        .param("rating", "95")
                        .param("note", "World class!")
                        .param("talent", "98")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(95))
                .andExpect(jsonPath("$.note").value("World class!"))
                .andExpect(jsonPath("$.talent").value(98));

        // Verifiziere in der Datenbank
        Scouting updated = scoutingRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getRating()).isEqualTo(95);
        assertThat(updated.getNote()).isEqualTo("World class!");
    }

    @Test
    @DisplayName("PUT /api/scouting/{id} - nicht existierende ID gibt 404")
    void update_NonExistingId_Returns404() throws Exception {
        mockMvc.perform(put("/api/scouting/999")
                        .param("rating", "80")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/scouting/{id} - nur Rating aktualisieren")
    void update_OnlyRating_UpdatesOnlyRating() throws Exception {
        Scouting saved = scoutingRepository.save(sampleScouting);
        String originalNote = saved.getNote();

        mockMvc.perform(put("/api/scouting/{id}", saved.getId())
                        .param("rating", "90")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(90))
                .andExpect(jsonPath("$.note").value(originalNote));
    }

    // ==================== DELETE /api/scouting/{id} ====================

    @Test
    @DisplayName("DELETE /api/scouting/{id} - existierenden Eintrag löschen")
    void delete_ExistingId_DeletesScouting() throws Exception {
        Scouting saved = scoutingRepository.save(sampleScouting);

        mockMvc.perform(delete("/api/scouting/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Verifiziere Löschung
        assertThat(scoutingRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/scouting/{id} - nicht existierende ID gibt 404")
    void delete_NonExistingId_Returns404() throws Exception {
        mockMvc.perform(delete("/api/scouting/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE und dann GET ====================

    @Test
    @DisplayName("DELETE dann GET - Eintrag nicht mehr vorhanden")
    void deleteThenGet_EntryNotExists_Returns404() throws Exception {
        Scouting saved = scoutingRepository.save(sampleScouting);
        Long id = saved.getId();

        mockMvc.perform(delete("/api/scouting/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/scouting/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ==================== POST und dann GET ====================

    @Test
    @DisplayName("POST dann GET - gleiche Daten zurück")
    void postThenGet_ReturnsSameData() throws Exception {
        mockMvc.perform(post("/api/scouting/player123")
                        .param("playerName", "Moukoko")
                        .param("rating", "85")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/scouting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].playerName").value("Moukoko"));
    }

    // ==================== Mehrere Einträge ====================

    @Test
    @DisplayName("POST mehrere Einträge - GET gibt alle zurück")
    void postMultipleEntries_GetAll_ReturnsAll() throws Exception {
        mockMvc.perform(post("/api/scouting/player1")
                        .param("playerName", "Player One")
                        .param("rating", "80")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/scouting/player2")
                        .param("playerName", "Player Two")
                        .param("rating", "90")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/scouting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].playerName").value("Player One"))
                .andExpect(jsonPath("$[1].playerName").value("Player Two"));
    }

    // ==================== PUT mit teilweisen Updates ====================

    @Test
    @DisplayName("PUT /api/scouting/{id} - teilweises Update (nur Talent)")
    void update_OnlyTalent_UpdatesOnlyTalent() throws Exception {
        Scouting saved = scoutingRepository.save(sampleScouting);
        int originalRating = saved.getRating();

        mockMvc.perform(put("/api/scouting/{id}", saved.getId())
                        .param("rating", String.valueOf(originalRating))
                        .param("talent", "95")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.talent").value(95))
                .andExpect(jsonPath("$.rating").value(originalRating));
    }
}