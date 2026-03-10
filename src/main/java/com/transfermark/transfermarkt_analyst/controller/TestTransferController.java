package com.transfermark.transfermarkt_analyst.controller;

import com.transfermark.transfermarkt_analyst.model.Transfer;
import com.transfermark.transfermarkt_analyst.repository.TransferRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/test/transfers")
public class TestTransferController {

    private final TransferRepository transferRepository;

    // ✅ KONSTRUKTOR HINZUFÜGEN!
    public TestTransferController(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @PostMapping("/create-test")
    public String createTestTransfer() {
        Transfer transfer = new Transfer();
        transfer.setPlayerName("Victor Osimhen");
        transfer.setFromClub("Napoli");
        transfer.setToClub("Chelsea (Gerücht)");
        transfer.setTransferFee(120.0);
        transfer.setTransferDate(LocalDate.now());
        transfer.setSeason(2024);
        transfer.setIsLoan(false);

        transferRepository.save(transfer);
        return "Test-Transfer wurde angelegt!";
    }

    @GetMapping("/all")
    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    @GetMapping("/to-club/{club}")
    public List<Transfer> getTransfersToClub(@PathVariable String club) {
        return transferRepository.findByToClub(club);
    }
}