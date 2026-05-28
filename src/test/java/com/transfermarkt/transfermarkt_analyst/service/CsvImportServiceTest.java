package com.transfermarkt.transfermarkt_analyst.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CsvImportService Tests")
class CsvImportServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CsvImportService csvImportService;

    @TempDir
    Path tempDir;

    private Path testCsvFile;
    private String testCsvContent;

    @BeforeEach
    void setUp() throws Exception {
        // Test-CSV-Inhalt
        testCsvContent = "season,league,club,transfer_window,movement,player_name,player_id,age,nationality,position,pos_short,market_value,dealing_club,dealing_country,fee,is_loan\n" +
                "2024,Bundesliga,Borussia Dortmund,Winter,Transfer,Moukoko,12345,18,Germany,Striker,ST,30000000,,,0,0\n" +
                "2024,Bundesliga,FC Bayern,Summer,Transfer,Kane,54321,30,England,Striker,ST,100000000,,,0,0";

        testCsvFile = tempDir.resolve("test.csv");
        Files.writeString(testCsvFile, testCsvContent);

        // Setze dataDirectory für Tests
        ReflectionTestUtils.setField(csvImportService, "dataDirectory", tempDir.toString());
    }

    // ==================== importLeagueFromFile() Tests ====================

    @Test
    @DisplayName("importLeagueFromFile - valide CSV-Datei wird korrekt importiert")
    void importLeagueFromFile_ValidFile_ImportsCorrectly() {
        // Given
        when(jdbcTemplate.batchUpdate(anyString(), any(List.class))).thenReturn(new int[]{1, 1});

        // When
        int importedCount = csvImportService.importLeagueFromFile(testCsvFile.toString(), "Bundesliga");

        // Then
        assertThat(importedCount).isEqualTo(2);
        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), any(List.class));
    }

    @Test
    @DisplayName("importLeagueFromFile - CSV mit einer Zeile (nur Header)")
    void importLeagueFromFile_OnlyHeader_ImportsZeroRows() throws Exception {
        // Given
        Path emptyCsv = tempDir.resolve("empty.csv");
        Files.writeString(emptyCsv, "season,league,club,transfer_window,movement,player_name,player_id,age,nationality,position,pos_short,market_value,dealing_club,dealing_country,fee,is_loan\n");

        // When
        int importedCount = csvImportService.importLeagueFromFile(emptyCsv.toString(), "Test");

        // Then
        assertThat(importedCount).isZero();
        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(List.class));
    }

    @Test
    @DisplayName("importLeagueFromFile - CSV mit unvollständigen Zeilen")
    void importLeagueFromFile_IncompleteRows_SkipsInvalidRows() throws Exception {
        // Given
        Path invalidCsv = tempDir.resolve("invalid.csv");
        String content = "season,league,club,transfer_window,movement,player_name,player_id,age,nationality,position,pos_short,market_value,dealing_club,dealing_country,fee,is_loan\n" +
                "2024,Bundesliga,Dortmund,Winter,Transfer,Moukoko,12345,18,Germany\n" +  // zu wenige Spalten
                "2024,Bundesliga,Bayern,Summer,Transfer,Kane,54321,30,England,Striker,ST,100000000,,,0,0";
        Files.writeString(invalidCsv, content);

        when(jdbcTemplate.batchUpdate(anyString(), any(List.class))).thenReturn(new int[]{1});

        // When
        int importedCount = csvImportService.importLeagueFromFile(invalidCsv.toString(), "Test");

        // Then
        assertThat(importedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("importLeagueFromFile - Datei nicht gefunden gibt 0")
    void importLeagueFromFile_FileNotFound_ReturnsZero() {
        // When
        int importedCount = csvImportService.importLeagueFromFile("/nonexistent/file.csv", "Test");

        // Then
        assertThat(importedCount).isZero();
        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(List.class));
    }

    @Test
    @DisplayName("importLeagueFromFile - leere Datei importiert 0 Zeilen")
    void importLeagueFromFile_EmptyFile_ReturnsZero() throws Exception {
        // Given
        Path emptyFile = tempDir.resolve("empty.csv");
        Files.writeString(emptyFile, "");

        // When
        int importedCount = csvImportService.importLeagueFromFile(emptyFile.toString(), "Test");

        // Then
        assertThat(importedCount).isZero();
    }

    // ==================== clean() Tests ====================

    @Test
    @DisplayName("clean - null gibt null")
    void clean_Null_ReturnsNull() {
        String result = csvImportService.clean(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("clean - leerer String gibt null")
    void clean_EmptyString_ReturnsNull() {
        String result = csvImportService.clean("");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("clean - entfernt Anführungszeichen")
    void clean_RemovesQuotes() {
        String result = csvImportService.clean("\"Moukoko\"");
        assertThat(result).isEqualTo("Moukoko");
    }

    @Test
    @DisplayName("clean - trimmt Leerzeichen")
    void clean_TrimsWhitespace() {
        String result = csvImportService.clean("  Moukoko  ");
        assertThat(result).isEqualTo("Moukoko");
    }

    // ==================== parseInt() Tests ====================

    @Test
    @DisplayName("parseInt - null gibt null")
    void parseInt_Null_ReturnsNull() {
        Integer result = csvImportService.parseInt(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parseInt - leeren String gibt null")
    void parseInt_EmptyString_ReturnsNull() {
        Integer result = csvImportService.parseInt("");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parseInt - valide Zahl gibt Integer")
    void parseInt_ValidNumber_ReturnsInteger() {
        Integer result = csvImportService.parseInt("12345");
        assertThat(result).isEqualTo(12345);
    }

    @Test
    @DisplayName("parseInt - ungültige Zahl gibt null")
    void parseInt_InvalidNumber_ReturnsNull() {
        Integer result = csvImportService.parseInt("abc");
        assertThat(result).isNull();
    }

    // ==================== parseDouble() Tests ====================

    @Test
    @DisplayName("parseDouble - null gibt null")
    void parseDouble_Null_ReturnsNull() {
        Double result = csvImportService.parseDouble(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parseDouble - leeren String gibt null")
    void parseDouble_EmptyString_ReturnsNull() {
        Double result = csvImportService.parseDouble("");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("parseDouble - valide Zahl gibt Double")
    void parseDouble_ValidNumber_ReturnsDouble() {
        Double result = csvImportService.parseDouble("30000000");
        assertThat(result).isEqualTo(30000000.0);
    }

    @Test
    @DisplayName("parseDouble - ungültige Zahl gibt null")
    void parseDouble_InvalidNumber_ReturnsNull() {
        Double result = csvImportService.parseDouble("abc");
        assertThat(result).isNull();
    }

    // ==================== Batch Processing Tests ====================

    @Test
    @DisplayName("importLeagueFromFile - Batch-Insert wird korrekt aufgerufen")
    void importLeagueFromFile_BatchInsertCalledWithCorrectData() {
        // Given
        ArgumentCaptor<List<Object[]>> batchCaptor = ArgumentCaptor.forClass(List.class);
        when(jdbcTemplate.batchUpdate(anyString(), batchCaptor.capture())).thenReturn(new int[]{1, 1});

        // When
        csvImportService.importLeagueFromFile(testCsvFile.toString(), "Bundesliga");

        // Then
        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), any(List.class));

        List<Object[]> batchArgs = batchCaptor.getValue();
        assertThat(batchArgs).hasSize(2);

        // Erste Zeile prüfen
        Object[] firstRow = batchArgs.get(0);
        assertThat(firstRow[0]).isEqualTo(2024);  // season
        assertThat(firstRow[1]).isEqualTo("Bundesliga");  // league
        assertThat(firstRow[5]).isEqualTo("Moukoko");  // player_name
        assertThat(firstRow[6]).isEqualTo(12345);  // player_id
        assertThat(firstRow[7]).isEqualTo(18);  // age

        // Zweite Zeile prüfen
        Object[] secondRow = batchArgs.get(1);
        assertThat(secondRow[5]).isEqualTo("Kane");
        assertThat(secondRow[6]).isEqualTo(54321);
    }

    // ==================== createTestCsvContent() Tests ====================

    @Test
    @DisplayName("createTestCsvContent - erstellt gültigen CSV-Inhalt")
    void createTestCsvContent_ReturnsValidContent() {
        String content = CsvImportService.createTestCsvContent();
        assertThat(content).isNotEmpty();
        assertThat(content).contains("Moukoko");
        assertThat(content).contains("Kane");
        assertThat(content).contains("season,league,club");
    }
}