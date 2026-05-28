package com.transfermarkt.transfermarkt_analyst.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerImageService Tests")
class PlayerImageServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private PlayerImageService playerImageService;

    @Test
    @DisplayName("Service instanziiert sich ohne Exception")
    void serviceInstantiatesWithoutException() {
        assertNotNull(playerImageService);
    }
}