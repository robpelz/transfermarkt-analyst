package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TheSportsDbClient Tests")
class TheSportsDbClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TheSportsDbClient theSportsDbClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(theSportsDbClient, "baseUrl", "https://test.api.com");
        // RestTemplate wird injected, aber wir müssen es manuell setzen, da Constructor ohne Parameter
        ReflectionTestUtils.setField(theSportsDbClient, "restTemplate", restTemplate);
    }

    // ==================== searchPlayers() Tests ====================

    @Test
    @DisplayName("searchPlayers - erfolgreiche Suche gibt Spielerliste zurück")
    void searchPlayers_Success_ReturnsPlayerList() {
        PlayerImageResponse response = new PlayerImageResponse();
        PlayerImageResponse.PlayerImage playerImg = new PlayerImageResponse.PlayerImage();
        playerImg.setId("123");
        playerImg.setName("Moukoko");
        playerImg.setNationality("Germany");
        playerImg.setTeam("Dortmund");
        response.setPlayers(List.of(playerImg));

        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(response);

        List<SoFifaPlayer> result = theSportsDbClient.searchPlayers("Moukoko");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("searchPlayers - leere Response gibt leere Liste")
    void searchPlayers_EmptyResponse_ReturnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(null);

        List<SoFifaPlayer> result = theSportsDbClient.searchPlayers("Moukoko");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("searchPlayers - Exception gibt leere Liste")
    void searchPlayers_Exception_ReturnsEmptyList() {
        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenThrow(new RuntimeException("API error"));

        List<SoFifaPlayer> result = theSportsDbClient.searchPlayers("Moukoko");

        assertThat(result).isEmpty();
    }

    // ==================== getPlayerById() Tests ====================

    @Test
    @DisplayName("getPlayerById - erfolgreich gibt Spieler zurück")
    void getPlayerById_Success_ReturnsPlayer() {
        PlayerImageResponse response = new PlayerImageResponse();
        PlayerImageResponse.PlayerImage playerImg = new PlayerImageResponse.PlayerImage();
        playerImg.setId("123");
        playerImg.setName("Moukoko");
        response.setPlayers(List.of(playerImg));

        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(response);

        SoFifaPlayer result = theSportsDbClient.getPlayerById("123");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("getPlayerById - keine Ergebnisse gibt null")
    void getPlayerById_NoResults_ReturnsNull() {
        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(null);

        SoFifaPlayer result = theSportsDbClient.getPlayerById("123");

        assertThat(result).isNull();
    }

    // ==================== getBestPlayerImage() Tests ====================

    @Test
    @DisplayName("getBestPlayerImage - bevorzugt Cutout")
    void getBestPlayerImage_PrefersCutout() {
        PlayerImageResponse.PlayerImage playerImg = new PlayerImageResponse.PlayerImage();
        playerImg.setCutout("cutout.jpg");
        playerImg.setRender("render.jpg");
        playerImg.setThumb("thumb.jpg");

        PlayerImageResponse response = new PlayerImageResponse();
        response.setPlayers(List.of(playerImg));

        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(response);

        String result = theSportsDbClient.getBestPlayerImage("Moukoko");

        assertThat(result).isEqualTo("cutout.jpg");
    }

    @Test
    @DisplayName("getBestPlayerImage - kein Cutout -> Render")
    void getBestPlayerImage_NoCutout_ReturnsRender() {
        PlayerImageResponse.PlayerImage playerImg = new PlayerImageResponse.PlayerImage();
        playerImg.setRender("render.jpg");

        PlayerImageResponse response = new PlayerImageResponse();
        response.setPlayers(List.of(playerImg));

        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(response);

        String result = theSportsDbClient.getBestPlayerImage("Moukoko");

        assertThat(result).isEqualTo("render.jpg");
    }

    @Test
    @DisplayName("getBestPlayerImage - kein Spieler gibt null")
    void getBestPlayerImage_NoPlayer_ReturnsNull() {
        when(restTemplate.getForObject(anyString(), eq(PlayerImageResponse.class)))
                .thenReturn(null);

        String result = theSportsDbClient.getBestPlayerImage("Moukoko");

        assertThat(result).isNull();
    }

    // ==================== isAvailable() Tests ====================

    @Test
    @DisplayName("isAvailable - API erreichbar gibt true")
    void isAvailable_ApiReachable_ReturnsTrue() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn("OK");

        boolean result = theSportsDbClient.isAvailable();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAvailable - API nicht erreichbar gibt false")
    void isAvailable_ApiUnreachable_ReturnsFalse() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        boolean result = theSportsDbClient.isAvailable();

        assertThat(result).isFalse();
    }
}