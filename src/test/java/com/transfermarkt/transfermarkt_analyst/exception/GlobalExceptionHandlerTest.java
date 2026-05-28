package com.transfermarkt.transfermarkt_analyst.exception;

import com.transfermarkt.transfermarkt_analyst.controller.ScoutingController;
import com.transfermarkt.transfermarkt_analyst.repository.ScoutingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoutingController.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  // ← Ersetzt @MockBean (Spring Boot 3.4+)
    private ScoutingRepository scoutingRepository;

    @Test
    @DisplayName("IllegalArgumentException -> 400 Bad Request")
    void illegalArgumentException_ReturnsBadRequest() throws Exception {
        when(scoutingRepository.findById(anyLong()))
                .thenThrow(new IllegalArgumentException("Ungültige ID"));

        mockMvc.perform(get("/api/scouting/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ungültige ID"));
    }

    @Test
    @DisplayName("Generic Exception -> 500 Internal Server Error")
    void genericException_ReturnsInternalServerError() throws Exception {
        when(scoutingRepository.findById(anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/scouting/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Ein unerwarteter Fehler ist aufgetreten."));
    }

    @Test
    @DisplayName("NullPointerException -> 500 Internal Server Error")
    void nullPointerException_ReturnsInternalServerError() throws Exception {
        when(scoutingRepository.findById(anyLong()))
                .thenThrow(new NullPointerException("Object is null"));

        mockMvc.perform(get("/api/scouting/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}