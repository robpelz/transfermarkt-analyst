package com.transfermarkt.transfermarkt_analyst.repository;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ScoutingRepository Tests")
class ScoutingRepositoryTest {

    @Autowired
    private ScoutingRepository scoutingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Scouting sampleScouting;

    @BeforeEach
    void setUp() {
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

    // ==================== save() Tests ====================

    @Test
    @DisplayName("save - sollte einen Scouting-Eintrag speichern und ID generieren")
    void save_ValidScouting_GeneratesId() {
        Scouting saved = scoutingRepository.save(sampleScouting);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlayerId()).isEqualTo("player123");
        assertThat(saved.getPlayerName()).isEqualTo("Moukoko");
        assertThat(saved.getRating()).isEqualTo(85);
    }

    @Test
    @DisplayName("save - sollte alle Felder korrekt speichern")
    void save_ValidScouting_SavesAllFields() {
        Scouting saved = scoutingRepository.save(sampleScouting);

        assertThat(saved.getTalent()).isEqualTo(90);
        assertThat(saved.getSpeed()).isEqualTo(88);
        assertThat(saved.getTactics()).isEqualTo(82);
        assertThat(saved.getPassing()).isEqualTo(80);
        assertThat(saved.getTechnique()).isEqualTo(86);
        assertThat(saved.getFitness()).isEqualTo(85);
        assertThat(saved.getTackling()).isEqualTo(40);
    }

    // ==================== findById() Tests ====================

    @Test
    @DisplayName("findById - existierende ID gibt Scouting zurück")
    void findById_ExistingId_ReturnsScouting() {
        Scouting saved = scoutingRepository.save(sampleScouting);
        entityManager.flush();
        entityManager.clear(); // Cache leeren, um echten DB-Zugriff zu testen

        Optional<Scouting> found = scoutingRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPlayerName()).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("findById - nicht existierende ID gibt Optional.empty()")
    void findById_NonExistingId_ReturnsEmpty() {
        Optional<Scouting> found = scoutingRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    // ==================== findAll() Tests ====================

    @Test
    @DisplayName("findAll - sollte alle Scouting-Einträge zurückgeben")
    void findAll_ReturnsAllScoutings() {
        scoutingRepository.save(sampleScouting);

        Scouting secondScouting = new Scouting();
        secondScouting.setPlayerId("player456");
        secondScouting.setPlayerName("Wirtz");
        secondScouting.setRating(90);
        scoutingRepository.save(secondScouting);

        List<Scouting> result = scoutingRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Scouting::getPlayerName)
                .containsExactlyInAnyOrder("Moukoko", "Wirtz");
    }

    @Test
    @DisplayName("findAll - leere Tabelle gibt leere Liste")
    void findAll_EmptyTable_ReturnsEmptyList() {
        List<Scouting> result = scoutingRepository.findAll();

        assertThat(result).isEmpty();
    }

    // ==================== findByPlayerId() Tests ====================

    @Test
    @DisplayName("findByPlayerId - existierende PlayerId gibt Scouting zurück")
    void findByPlayerId_ExistingId_ReturnsScouting() {
        scoutingRepository.save(sampleScouting);

        Optional<Scouting> found = scoutingRepository.findByPlayerId("player123");

        assertThat(found).isPresent();
        assertThat(found.get().getPlayerName()).isEqualTo("Moukoko");
        assertThat(found.get().getRating()).isEqualTo(85);
    }

    @Test
    @DisplayName("findByPlayerId - nicht existierende PlayerId gibt Optional.empty()")
    void findByPlayerId_NonExistingId_ReturnsEmpty() {
        Optional<Scouting> found = scoutingRepository.findByPlayerId("unknown");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByPlayerId - case sensitive? (sollte genau passen)")
    void findByPlayerId_CaseSensitive_MatchesExactly() {
        scoutingRepository.save(sampleScouting);

        Optional<Scouting> foundLower = scoutingRepository.findByPlayerId("PLAYER123");

        assertThat(foundLower).isEmpty();
    }

    // ==================== existsByPlayerId() Tests ====================

    @Test
    @DisplayName("existsByPlayerId - existierende PlayerId gibt true")
    void existsByPlayerId_ExistingId_ReturnsTrue() {
        scoutingRepository.save(sampleScouting);

        boolean exists = scoutingRepository.existsByPlayerId("player123");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByPlayerId - nicht existierende PlayerId gibt false")
    void existsByPlayerId_NonExistingId_ReturnsFalse() {
        boolean exists = scoutingRepository.existsByPlayerId("unknown");

        assertThat(exists).isFalse();
    }

    // ==================== deleteByPlayerId() Tests ====================

    @Test
    @DisplayName("deleteByPlayerId - löscht existierenden Eintrag")
    void deleteByPlayerId_ExistingId_DeletesEntry() {
        scoutingRepository.save(sampleScouting);

        // verify before delete
        assertThat(scoutingRepository.findByPlayerId("player123")).isPresent();

        // delete
        scoutingRepository.deleteByPlayerId("player123");
        entityManager.flush();

        // verify after delete
        Optional<Scouting> deleted = scoutingRepository.findByPlayerId("player123");
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("deleteByPlayerId - nicht existierende ID tut nichts")
    void deleteByPlayerId_NonExistingId_DoesNothing() {
        // Sollte keine Exception werfen
        scoutingRepository.deleteByPlayerId("unknown");

        // Alles OK, keine Exception
    }

    // ==================== deleteById() Tests ====================

    @Test
    @DisplayName("deleteById - löscht existierenden Eintrag")
    void deleteById_ExistingId_DeletesEntry() {
        Scouting saved = scoutingRepository.save(sampleScouting);
        Long id = saved.getId();

        assertThat(scoutingRepository.findById(id)).isPresent();

        scoutingRepository.deleteById(id);
        entityManager.flush();

        assertThat(scoutingRepository.findById(id)).isEmpty();
    }

    // ==================== update Tests ====================

    @Test
    @DisplayName("update - sollte vorhandenen Eintrag aktualisieren")
    void update_ExistingScouting_UpdatesFields() {
        Scouting saved = scoutingRepository.save(sampleScouting);
        Long id = saved.getId();

        saved.setRating(95);
        saved.setNote("World class!");
        saved.setTalent(98);

        Scouting updated = scoutingRepository.save(saved);

        assertThat(updated.getRating()).isEqualTo(95);
        assertThat(updated.getNote()).isEqualTo("World class!");
        assertThat(updated.getTalent()).isEqualTo(98);

        // Verify from DB
        Optional<Scouting> fromDb = scoutingRepository.findById(id);
        assertThat(fromDb.get().getRating()).isEqualTo(95);
    }

    // ==================== Default Values Tests ====================

    @Test
    @DisplayName("save - verwendet Standardwerte für nicht gesetzte Felder")
    void save_WithMinimalData_UsesDefaults() {
        Scouting minimal = new Scouting();
        minimal.setPlayerId("minimal");
        minimal.setPlayerName("Minimal Player");
        minimal.setRating(70);
        // Talent, Speed etc. nicht gesetzt

        Scouting saved = scoutingRepository.save(minimal);

        assertThat(saved.getTalent()).isEqualTo(70);
        assertThat(saved.getSpeed()).isEqualTo(70);
        assertThat(saved.getTactics()).isEqualTo(70);
        assertThat(saved.getPassing()).isEqualTo(70);
        assertThat(saved.getTechnique()).isEqualTo(70);
        assertThat(saved.getFitness()).isEqualTo(70);
        assertThat(saved.getTackling()).isEqualTo(30);
    }

    // ==================== mehrere Einträge für gleichen Player? ====================

    @Test
    @DisplayName("save - kann mehrere Scouting-Einträge für verschiedene Player speichern")
    void save_MultipleDifferentPlayers_AllSaved() {
        scoutingRepository.save(sampleScouting);

        Scouting second = new Scouting();
        second.setPlayerId("player456");
        second.setPlayerName("Wirtz");
        second.setRating(90);
        scoutingRepository.save(second);

        List<Scouting> all = scoutingRepository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(Scouting::getPlayerId)
                .containsExactlyInAnyOrder("player123", "player456");
    }
}