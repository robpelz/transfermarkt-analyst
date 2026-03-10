package com.transfermark.transfermarkt_analyst.controller;

import com.transfermark.transfermarkt_analyst.model.Player;
import com.transfermark.transfermarkt_analyst.model.Transfer;
import com.transfermark.transfermarkt_analyst.repository.PlayerRepository;
import com.transfermark.transfermarkt_analyst.repository.TransferRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/test/player-transfers")
public class PlayerTransferController {

    private final PlayerRepository playerRepository;
    private final TransferRepository transferRepository;

    public PlayerTransferController(PlayerRepository playerRepository, TransferRepository transferRepository) {
        this.playerRepository = playerRepository;
        this.transferRepository = transferRepository;
    }

    @PostMapping("/create-complete")
    public String createPlayerWithTransfer() {
        // 1. Neuen Spieler anlegen
        Player player = new Player();
        player.setName("Victor Osimhen");
        player.setPosition("ST");
        player.setCurrentClub("Napoli");
        player.setMarketValue(120.0);
        player.setPlaysInSerieA(true);

        Player savedPlayer = playerRepository.save(player);

        // 2. Neuen Transfer anlegen und mit Spieler verknüpfen
        Transfer transfer = new Transfer();
        transfer.setPlayerName(player.getName());
        transfer.setFromClub("Napoli");
        transfer.setToClub("Chelsea (Gerücht)");
        transfer.setTransferFee(120.0);
        transfer.setTransferDate(LocalDate.now());
        transfer.setSeason(2024);
        transfer.setPlayer(savedPlayer);  // ← Verknüpfung!

        transferRepository.save(transfer);

        return "Spieler und Transfer wurden verknüpft! Player ID: " + savedPlayer.getId();
    }

    @GetMapping("/player/{id}")
    public Player getPlayerWithTransfers(@PathVariable Long id) {
        return playerRepository.findById(id).orElse(null);
    }
}