package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoFifaClient Tests")
class SoFifaClientTest {

    @Mock
    private TheSportsDbClient theSportsDbClient;

    @Mock
    private DatabasePlayerService databasePlayerService;

    @InjectMocks
    private SoFifaClient soFifaClient;

    private SoFifaPlayer samplePlayer;

    @BeforeEach
    void setUp() {
        samplePlayer = new SoFifaPlayer();
        samplePlayer.setId("12345");
        samplePlayer.setName("Moukoko");
        samplePlayer.setNationality("Germany");
        samplePlayer.setClub("Borussia Dortmund");
    }

    // ==================== searchPlayers() Tests ====================

    @Test
    @DisplayName("searchPlayers - eigene DB liefert Ergebnisse -> gibt eigene zurück")
    void searchPlayers_OwnDbHasResults_ReturnsOwnResults() {
        List<SoFifaPlayer> ownResults = List.of(samplePlayer);
        when(databasePlayerService.searchPlayers("Moukoko")).thenReturn(ownResults);

        List<SoFifaPlayer> result = soFifaClient.searchPlayers("Moukoko");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Moukoko");
        verify(databasePlayerService).searchPlayers("Moukoko");
        verify(theSportsDbClient, never()).searchPlayers(anyString());
    }

    @Test
    @DisplayName("searchPlayers - eigene DB leer -> Fallback zu TheSportsDB")
    void searchPlayers_OwnDbEmpty_FallsBackToTheSportsDb() {
        when(databasePlayerService.searchPlayers("Unknown")).thenReturn(List.of());
        when(theSportsDbClient.searchPlayers("Unknown")).thenReturn(List.of(samplePlayer));

        List<SoFifaPlayer> result = soFifaClient.searchPlayers("Unknown");

        assertThat(result).hasSize(1);
        verify(databasePlayerService).searchPlayers("Unknown");
        verify(theSportsDbClient).searchPlayers("Unknown");
    }

    @Test
    @DisplayName("searchPlayers - eigene DB wirft Exception -> Fallback zu TheSportsDB")
    void searchPlayers_OwnDbThrowsException_FallsBackToTheSportsDb() {
        when(databasePlayerService.searchPlayers("Moukoko")).thenThrow(new RuntimeException("DB error"));
        when(theSportsDbClient.searchPlayers("Moukoko")).thenReturn(List.of(samplePlayer));

        List<SoFifaPlayer> result = soFifaClient.searchPlayers("Moukoko");

        assertThat(result).hasSize(1);
        verify(theSportsDbClient).searchPlayers("Moukoko");
    }

    @Test
    @DisplayName("searchPlayers - eigene DB null -> Fallback zu TheSportsDB")
    void searchPlayers_OwnDbReturnsNull_FallsBackToTheSportsDb() {
        when(databasePlayerService.searchPlayers("Moukoko")).thenReturn(null);
        when(theSportsDbClient.searchPlayers("Moukoko")).thenReturn(List.of(samplePlayer));

        List<SoFifaPlayer> result = soFifaClient.searchPlayers("Moukoko");

        assertThat(result).hasSize(1);
        verify(theSportsDbClient).searchPlayers("Moukoko");
    }

    // ==================== getPlayerById() Tests ====================

    @Test
    @DisplayName("getPlayerById - eigene DB liefert Spieler -> gibt eigenen zurück")
    void getPlayerById_OwnDbHasPlayer_ReturnsOwnPlayer() {
        when(databasePlayerService.getPlayerById("12345")).thenReturn(samplePlayer);
        when(theSportsDbClient.getBestPlayerImage("Moukoko")).thenReturn("http://image.url");

        SoFifaPlayer result = soFifaClient.getPlayerById("12345");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Moukoko");
        assertThat(result.getImageUrl()).isEqualTo("http://image.url");
        verify(databasePlayerService).getPlayerById("12345");
        verify(theSportsDbClient).getBestPlayerImage("Moukoko");
        verify(theSportsDbClient, never()).getPlayerById(anyString());
    }

    @Test
    @DisplayName("getPlayerById - eigene DB null -> Fallback zu TheSportsDB")
    void getPlayerById_OwnDbNull_FallsBackToTheSportsDb() {
        when(databasePlayerService.getPlayerById("12345")).thenReturn(null);
        when(theSportsDbClient.getPlayerById("12345")).thenReturn(samplePlayer);

        SoFifaPlayer result = soFifaClient.getPlayerById("12345");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Moukoko");
        verify(theSportsDbClient).getPlayerById("12345");
    }

    @Test
    @DisplayName("getPlayerById - eigene DB Exception -> Fallback zu TheSportsDB")
    void getPlayerById_OwnDbThrowsException_FallsBackToTheSportsDb() {
        when(databasePlayerService.getPlayerById("12345")).thenThrow(new RuntimeException("DB error"));
        when(theSportsDbClient.getPlayerById("12345")).thenReturn(samplePlayer);

        SoFifaPlayer result = soFifaClient.getPlayerById("12345");

        assertThat(result).isNotNull();
        verify(theSportsDbClient).getPlayerById("12345");
    }

    @Test
    @DisplayName("getPlayerById - Bild von TheSportsDB auch wenn eigene DB erfolgreich")
    void getPlayerById_OwnDbHasPlayer_AddsImageFromTheSportsDb() {
        when(databasePlayerService.getPlayerById("12345")).thenReturn(samplePlayer);
        when(theSportsDbClient.getBestPlayerImage("Moukoko")).thenReturn("http://image.url");

        SoFifaPlayer result = soFifaClient.getPlayerById("12345");

        assertThat(result.getImageUrl()).isEqualTo("http://image.url");
        verify(theSportsDbClient).getBestPlayerImage("Moukoko");
    }

    @Test
    @DisplayName("getPlayerById - Bild von TheSportsDB nicht verfügbar")
    void getPlayerById_BildNotAvailable_NoImageSet() {
        when(databasePlayerService.getPlayerById("12345")).thenReturn(samplePlayer);
        when(theSportsDbClient.getBestPlayerImage("Moukoko")).thenReturn(null);

        SoFifaPlayer result = soFifaClient.getPlayerById("12345");

        assertThat(result.getImageUrl()).isNull();
    }

    // ==================== isCrsetAvailable() Tests ====================

    @Test
    @DisplayName("isCrsetAvailable - gibt immer true zurück (eigene DB)")
    void isCrsetAvailable_AlwaysReturnsTrue() {
        assertThat(soFifaClient.isCrsetAvailable()).isTrue();
    }
}