package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.service.FootballDatasetImportService;
import com.transfermarkt.transfermarkt_analyst.service.MarketValueImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImportController.class)
@DisplayName("ImportController Tests")
class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FootballDatasetImportService footballDatasetImportService;

    @MockitoBean
    private MarketValueImportService marketValueImportService;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void importPlayers_Success_ReturnsOk() throws Exception {
        doNothing().when(footballDatasetImportService).importPlayerProfiles();

        mockMvc.perform(post("/api/import/players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Spieler-Profile Import gestartet!"));
    }

    @Test
    void importMarketValues_Success_ReturnsOk() throws Exception {
        doNothing().when(marketValueImportService).importMarketValues();

        mockMvc.perform(post("/api/import/market-values")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Marktwerte Import gestartet!"));
    }

    @Test
    void createScoutingTable_Success_ReturnsOk() throws Exception {
        doNothing().when(jdbcTemplate).execute(anyString());

        mockMvc.perform(post("/api/import/create-scouting-table")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Scouting table created successfully!"));
    }

    @Test
    void createScoutingTable_Error_ReturnsErrorMessage() throws Exception {
        doThrow(new RuntimeException("Table creation failed")).when(jdbcTemplate).execute(anyString());

        mockMvc.perform(post("/api/import/create-scouting-table")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("❌ Error: Table creation failed"));
    }
}