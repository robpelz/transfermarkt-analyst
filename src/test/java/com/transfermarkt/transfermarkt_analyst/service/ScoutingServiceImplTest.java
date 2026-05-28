package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import com.transfermarkt.transfermarkt_analyst.repository.ScoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScoutingServiceImpl Tests")
class ScoutingServiceImplTest {

    @Mock
    private ScoutingRepository scoutingRepository;

    @InjectMocks
    private ScoutingServiceImpl scoutingService;

    private Scouting sampleScouting;

    @BeforeEach
    void setUp() {
        sampleScouting = new Scouting();
        sampleScouting.setId(1L);
        sampleScouting.setPlayerId("player123");
        sampleScouting.setPlayerName("Moukoko");
        sampleScouting.setRating(85);
        sampleScouting.setNote("Great talent");
    }

    // ==================== addScouting Tests ====================

    @Test
    @DisplayName("addScouting - soll neuen Scouting-Eintrag speichern")
    void addScouting_SavesAndReturnsScouting() {
        when(scoutingRepository.save(any(Scouting.class))).thenReturn(sampleScouting);

        Scouting result = scoutingService.addScouting("player123", "Moukoko", 85, "Great talent");

        assertThat(result).isNotNull();
        assertThat(result.getPlayerId()).isEqualTo("player123");
        assertThat(result.getPlayerName()).isEqualTo("Moukoko");
        assertThat(result.getRating()).isEqualTo(85);

        verify(scoutingRepository).save(any(Scouting.class));
    }

    @Test
    @DisplayName("addScouting - mit null Note speichern")
    void addScouting_WithNullNote_StillSaves() {
        when(scoutingRepository.save(any(Scouting.class))).thenReturn(sampleScouting);

        Scouting result = scoutingService.addScouting("player123", "Moukoko", 85, null);

        assertThat(result).isNotNull();
        verify(scoutingRepository).save(any(Scouting.class));
    }

    // ==================== getAllScouting Tests ====================

    @Test
    @DisplayName("getAllScouting - soll alle Scouting-Einträge zurückgeben")
    void getAllScouting_ReturnsAllEntries() {
        List<Scouting> scoutings = Arrays.asList(sampleScouting, new Scouting());
        when(scoutingRepository.findAll()).thenReturn(scoutings);

        List<Scouting> result = scoutingService.getAllScouting();

        assertThat(result).hasSize(2);
        verify(scoutingRepository).findAll();
    }

    @Test
    @DisplayName("getAllScouting - leere Liste wenn keine Einträge")
    void getAllScouting_NoEntries_ReturnsEmptyList() {
        when(scoutingRepository.findAll()).thenReturn(List.of());

        List<Scouting> result = scoutingService.getAllScouting();

        assertThat(result).isEmpty();
        verify(scoutingRepository).findAll();
    }

    // ==================== getScoutingByPlayerId Tests ====================

    @Test
    @DisplayName("getScoutingByPlayerId - existierende PlayerId gibt Scouting zurück")
    void getScoutingByPlayerId_ExistingId_ReturnsScouting() {
        when(scoutingRepository.findByPlayerId("player123")).thenReturn(Optional.of(sampleScouting));

        Scouting result = scoutingService.getScoutingByPlayerId("player123");

        assertThat(result).isNotNull();
        assertThat(result.getPlayerId()).isEqualTo("player123");
        verify(scoutingRepository).findByPlayerId("player123");
    }

    @Test
    @DisplayName("getScoutingByPlayerId - nicht existierende PlayerId gibt null zurück")
    void getScoutingByPlayerId_NonExistingId_ReturnsNull() {
        when(scoutingRepository.findByPlayerId("unknown")).thenReturn(Optional.empty());

        Scouting result = scoutingService.getScoutingByPlayerId("unknown");

        assertThat(result).isNull();
        verify(scoutingRepository).findByPlayerId("unknown");
    }

    // ==================== updateScouting Tests ====================

    @Test
    @DisplayName("updateScouting - existierenden Eintrag aktualisieren")
    void updateScouting_ExistingEntry_UpdatesAndReturns() {
        when(scoutingRepository.findByPlayerId("player123")).thenReturn(Optional.of(sampleScouting));
        when(scoutingRepository.save(any(Scouting.class))).thenReturn(sampleScouting);

        Scouting result = scoutingService.updateScouting("player123", 95, "Updated note");

        assertThat(result.getRating()).isEqualTo(95);
        assertThat(result.getNote()).isEqualTo("Updated note");
        verify(scoutingRepository).save(any(Scouting.class));
    }

    @Test
    @DisplayName("updateScouting - nur Rating aktualisieren (Note null)")
    void updateScouting_OnlyRating_UpdatesOnlyRating() {
        when(scoutingRepository.findByPlayerId("player123")).thenReturn(Optional.of(sampleScouting));
        when(scoutingRepository.save(any(Scouting.class))).thenReturn(sampleScouting);

        Scouting result = scoutingService.updateScouting("player123", 95, null);

        assertThat(result.getRating()).isEqualTo(95);
        assertThat(result.getNote()).isEqualTo("Great talent"); // unchanged
        verify(scoutingRepository).save(any(Scouting.class));
    }

    @Test
    @DisplayName("updateScouting - nicht existierende PlayerId wirft RuntimeException")
    void updateScouting_NonExistingId_ThrowsException() {
        when(scoutingRepository.findByPlayerId("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                scoutingService.updateScouting("unknown", 80, "Note"));

        verify(scoutingRepository, never()).save(any());
    }

    // ==================== deleteScouting Tests ====================

    @Test
    @DisplayName("deleteScouting - löscht existierenden Eintrag")
    void deleteScouting_ExistingEntry_Deletes() {
        doNothing().when(scoutingRepository).deleteByPlayerId("player123");

        scoutingService.deleteScouting("player123");

        verify(scoutingRepository).deleteByPlayerId("player123");
    }

    // ==================== existsByPlayerId Tests ====================

    @Test
    @DisplayName("existsByPlayerId - existierende ID gibt true zurück")
    void existsByPlayerId_ExistingId_ReturnsTrue() {
        when(scoutingRepository.existsByPlayerId("player123")).thenReturn(true);

        boolean result = scoutingService.existsByPlayerId("player123");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByPlayerId - nicht existierende ID gibt false zurück")
    void existsByPlayerId_NonExistingId_ReturnsFalse() {
        when(scoutingRepository.existsByPlayerId("unknown")).thenReturn(false);

        boolean result = scoutingService.existsByPlayerId("unknown");

        assertThat(result).isFalse();
    }
}