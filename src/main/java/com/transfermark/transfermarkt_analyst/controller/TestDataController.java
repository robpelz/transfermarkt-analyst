package com.transfermark.transfermarkt_analyst.controller;

import com.transfermark.transfermarkt_analyst.model.Transfer;
import com.transfermark.transfermarkt_analyst.repository.TransferRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/test/data")
public class TestDataController {

    private final TransferRepository transferRepository;

    // ✅ WICHTIG: Konstruktor für Dependency Injection
    public TestDataController(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @PostMapping("/all-test-data")
    public String createAllTestData() {

        // Napoli Transfers
        createTransfer("Kvaratskhelia", "Dinamo Batumi", "Napoli", 10.0, true);
        createTransfer("Osimhen", "Lille", "Napoli", 70.0, true);
        createTransfer("Kim Min-jae", "Fenerbahce", "Napoli", 18.0, true);
        createTransfer("Lucca", "Pisa", "Napoli", 30.0, false);
        createTransfer("Lindstrom", "Eintracht", "Napoli", 25.0, false);

        // Bayern München Transfers
        createTransfer("Harry Kane", "Tottenham", "Bayern München", 100.0, true);
        createTransfer("Kim Min-jae", "Napoli", "Bayern München", 50.0, true);
        createTransfer("Sadio Mané", "Liverpool", "Bayern München", 32.0, false);

        // Chelsea Transfers
        createTransfer("Cole Palmer", "Manchester City", "Chelsea", 47.0, true);
        createTransfer("Enzo Fernández", "Benfica", "Chelsea", 121.0, false);
        createTransfer("Mykhailo Mudryk", "Shakhtar", "Chelsea", 100.0, false);
        createTransfer("Lukaku", "Inter", "Chelsea", 115.0, false);

        return "Testdaten für mehrere Clubs wurden angelegt!";
    }

    // ✅ Private Hilfsmethode
    private void createTransfer(String player, String from, String to, double fee, boolean success) {
        Transfer t = new Transfer();
        t.setPlayerName(player);
        t.setFromClub(from);
        t.setToClub(to);
        t.setTransferFee(fee);
        t.setTransferDate(LocalDate.now());
        t.setWasSuccessful(success);
        transferRepository.save(t);
    }

    // ✅ Optional: Einzelne Clubs laden
    @PostMapping("/napoli")
    public String createNapoliData() {
        createTransfer("Kvaratskhelia", "Dinamo Batumi", "Napoli", 10.0, true);
        createTransfer("Osimhen", "Lille", "Napoli", 70.0, true);
        createTransfer("Lucca", "Pisa", "Napoli", 30.0, false);
        return "Napoli-Daten angelegt";
    }

    @PostMapping("/bayern")
    public String createBayernData() {
        createTransfer("Harry Kane", "Tottenham", "Bayern München", 100.0, true);
        createTransfer("Sadio Mané", "Liverpool", "Bayern München", 32.0, false);
        return "Bayern-Daten angelegt";
    }

    @PostMapping("/chelsea")
    public String createChelseaData() {
        createTransfer("Cole Palmer", "Manchester City", "Chelsea", 47.0, true);
        createTransfer("Lukaku", "Inter", "Chelsea", 115.0, false);
        return "Chelsea-Daten angelegt";
    }
}