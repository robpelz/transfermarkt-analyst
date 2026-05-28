package com.transfermarkt.transfermarkt_analyst.repository;

import com.transfermarkt.transfermarkt_analyst.model.PlayerMarketData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PlayerMarketDataRepository Tests")
class PlayerMarketDataRepositoryTest {

    @Autowired
    private PlayerMarketDataRepository repository;

    private PlayerMarketData sampleData;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        sampleData = new PlayerMarketData();
        sampleData.setPlayerId(12345);
        sampleData.setPlayerName("Moukoko");
        sampleData.setClub("Borussia Dortmund");
        sampleData.setLeague("Bundesliga");
        sampleData.setMarketValue(30_000_000.0);
        sampleData.setAge(18);
        sampleData.setPosition("Striker");
        sampleData.setPosShort("ST");
        sampleData.setNationality("Germany");
        sampleData.setSeason(2024);
        sampleData.setMovement("None");
        sampleData.setFee(0.0);
        sampleData.setIsLoan(false);
        sampleData.setDealingClub(null);
        sampleData.setDealingCountry(null);
        sampleData.setTransferWindow(null);
    }

    // ==================== save() Tests ====================

    @Test
    @DisplayName("save - sollte PlayerMarketData speichern und ID generieren")
    void save_ValidData_GeneratesId() {
        PlayerMarketData saved = repository.save(sampleData);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlayerId()).isEqualTo(12345);
        assertThat(saved.getPlayerName()).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("save - speichert alle Felder korrekt")
    void save_ValidData_SavesAllFields() {
        PlayerMarketData saved = repository.save(sampleData);

        assertThat(saved.getMarketValue()).isEqualTo(30_000_000.0);
        assertThat(saved.getAge()).isEqualTo(18);
        assertThat(saved.getSeason()).isEqualTo(2024);
        assertThat(saved.getClub()).isEqualTo("Borussia Dortmund");
        assertThat(saved.getLeague()).isEqualTo("Bundesliga");
    }

    // ==================== findById() Tests ====================

    @Test
    @DisplayName("findById - existierende ID gibt PlayerMarketData zurück")
    void findById_ExistingId_ReturnsData() {
        PlayerMarketData saved = repository.save(sampleData);

        Optional<PlayerMarketData> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPlayerName()).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("findById - nicht existierende ID gibt Optional.empty()")
    void findById_NonExistingId_ReturnsEmpty() {
        Optional<PlayerMarketData> found = repository.findById(999L);
        assertThat(found).isEmpty();
    }

    // ==================== findAll() Tests ====================

    @Test
    @DisplayName("findAll - gibt alle Einträge zurück")
    void findAll_ReturnsAllEntries() {
        repository.save(sampleData);

        PlayerMarketData second = new PlayerMarketData();
        second.setPlayerId(54321);
        second.setPlayerName("Bellingham");
        second.setClub("Real Madrid");
        second.setMarketValue(100_000_000.0);
        second.setSeason(2024);
        repository.save(second);

        List<PlayerMarketData> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PlayerMarketData::getPlayerName)
                .containsExactlyInAnyOrder("Moukoko", "Bellingham");
    }

    @Test
    @DisplayName("findAll - leere Tabelle gibt leere Liste")
    void findAll_EmptyTable_ReturnsEmptyList() {
        List<PlayerMarketData> result = repository.findAll();
        assertThat(result).isEmpty();
    }

    // ==================== findTopByPlayerNameOrderBySeasonDesc Tests ====================

    @Test
    @DisplayName("findTopByPlayerNameOrderBySeasonDesc - findet aktuellste Saison")
    void findTopByPlayerNameOrderBySeasonDesc_ReturnsLatestSeason() {
        repository.save(sampleData);

        PlayerMarketData olderSeason = new PlayerMarketData();
        olderSeason.setPlayerId(12345);
        olderSeason.setPlayerName("Moukoko");
        olderSeason.setSeason(2023);
        olderSeason.setMarketValue(20_000_000.0);
        repository.save(olderSeason);

        Optional<PlayerMarketData> result = repository.findTopByPlayerNameOrderBySeasonDesc("Moukoko");

        assertThat(result).isPresent();
        assertThat(result.get().getSeason()).isEqualTo(2024);
        assertThat(result.get().getMarketValue()).isEqualTo(30_000_000.0);
    }

    @Test
    @DisplayName("findTopByPlayerNameOrderBySeasonDesc - nicht existierender Spieler gibt empty")
    void findTopByPlayerNameOrderBySeasonDesc_NonExisting_ReturnsEmpty() {
        Optional<PlayerMarketData> result = repository.findTopByPlayerNameOrderBySeasonDesc("NonExisting");

        assertThat(result).isEmpty();
    }

    // ==================== deleteById() Tests ====================

    @Test
    @DisplayName("deleteById - löscht existierenden Eintrag")
    void deleteById_ExistingId_DeletesEntry() {
        PlayerMarketData saved = repository.save(sampleData);
        Long id = saved.getId();

        assertThat(repository.findById(id)).isPresent();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    // ==================== update Tests ====================

    @Test
    @DisplayName("update - aktualisiert vorhandenen Eintrag")
    void update_ExistingEntry_UpdatesFields() {
        PlayerMarketData saved = repository.save(sampleData);
        Long id = saved.getId();

        saved.setMarketValue(50_000_000.0);
        saved.setClub("New Club");
        saved.setMovement("Transfer");
        PlayerMarketData updated = repository.save(saved);

        assertThat(updated.getMarketValue()).isEqualTo(50_000_000.0);
        assertThat(updated.getClub()).isEqualTo("New Club");
        assertThat(updated.getMovement()).isEqualTo("Transfer");

        // Verify from DB
        Optional<PlayerMarketData> fromDb = repository.findById(id);
        assertThat(fromDb.get().getMarketValue()).isEqualTo(50_000_000.0);
    }

    // ==================== count() Tests ====================

    @Test
    @DisplayName("count - gibt Anzahl der Einträge zurück")
    void count_ReturnsCorrectCount() {
        assertThat(repository.count()).isZero();

        repository.save(sampleData);
        assertThat(repository.count()).isEqualTo(1);

        PlayerMarketData second = new PlayerMarketData();
        second.setPlayerId(54321);
        second.setPlayerName("Bellingham");
        second.setSeason(2024);
        repository.save(second);

        assertThat(repository.count()).isEqualTo(2);
    }

    // ==================== existsById() Tests ====================

    @Test
    @DisplayName("existsById - existierende ID gibt true")
    void existsById_ExistingId_ReturnsTrue() {
        PlayerMarketData saved = repository.save(sampleData);
        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById - nicht existierende ID gibt false")
    void existsById_NonExistingId_ReturnsFalse() {
        assertThat(repository.existsById(999L)).isFalse();
    }

    // ==================== Null/Edge Cases ====================

    @Test
    @DisplayName("save - mit minimalen Daten (nur Pflichtfelder)")
    void save_WithMinimalData_SavesSuccessfully() {
        PlayerMarketData minimal = new PlayerMarketData();
        minimal.setPlayerId(999);
        minimal.setPlayerName("Minimal");
        minimal.setMarketValue(0.0);
        minimal.setSeason(2024);

        PlayerMarketData saved = repository.save(minimal);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlayerId()).isEqualTo(999);
        assertThat(saved.getPlayerName()).isEqualTo("Minimal");
    }

    @Test
    @DisplayName("save - mit null-Feldern (wo erlaubt)")
    void save_WithNullFields_SavesSuccessfully() {
        PlayerMarketData nullFields = new PlayerMarketData();
        nullFields.setPlayerId(888);
        nullFields.setPlayerName("NullTest");
        nullFields.setSeason(2024);
        nullFields.setMarketValue(null);
        nullFields.setClub(null);
        nullFields.setLeague(null);

        PlayerMarketData saved = repository.save(nullFields);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMarketValue()).isNull();
        assertThat(saved.getClub()).isNull();
    }

    // ==================== Mehrere Einträge gleicher Player ====================

    @Test
    @DisplayName("mehrere Einträge für gleichen Player über verschiedene Seasons")
    void multipleEntriesForSamePlayer_DifferentSeasons_AllSaved() {
        repository.save(sampleData);

        PlayerMarketData season2023 = new PlayerMarketData();
        season2023.setPlayerId(12345);
        season2023.setPlayerName("Moukoko");
        season2023.setSeason(2023);
        season2023.setMarketValue(15_000_000.0);
        repository.save(season2023);

        List<PlayerMarketData> all = repository.findAll();
        assertThat(all).hasSize(2);

        // Beide sollten unterschiedliche IDs haben
        assertThat(all.get(0).getId()).isNotEqualTo(all.get(1).getId());
    }
}