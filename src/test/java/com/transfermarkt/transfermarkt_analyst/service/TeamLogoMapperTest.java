package com.transfermarkt.transfermarkt_analyst.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TeamLogoMapper Tests")
class TeamLogoMapperTest {

    private final TeamLogoMapper teamLogoMapper = new TeamLogoMapper();

    @Test
    @DisplayName("Service instanziiert sich ohne Exception")
    void serviceInstantiatesWithoutException() {
        assertNotNull(teamLogoMapper);
    }
}