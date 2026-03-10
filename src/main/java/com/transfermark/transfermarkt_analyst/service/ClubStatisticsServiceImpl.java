package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.ClubStatistics;
import com.transfermark.transfermarkt_analyst.model.Transfer;
import com.transfermark.transfermarkt_analyst.repository.TransferRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class ClubStatisticsServiceImpl implements ClubStatisticsService {

    private static final Logger log = LoggerFactory.getLogger(ClubStatisticsServiceImpl.class);
    private final TransferRepository transferRepository;

    public ClubStatisticsServiceImpl(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    public ClubStatistics getClubStatistics(String clubName) {
        log.info("📊 Hole detaillierte Statistiken für: {}", clubName);

        List<Transfer> transfers = transferRepository.findByToClub(clubName);

        if (transfers.isEmpty()) {
            log.warn("⚠️ Keine Transfers gefunden für: {}", clubName);
            return createEmptyStatistics(clubName);
        }

        int total = transfers.size();
        long successful = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && t.getWasSuccessful())
                .count();

        double successRate = (double) successful / total * 100;

        double totalSpent = transfers.stream()
                .mapToDouble(t -> t.getTransferFee() != null ? t.getTransferFee() : 0.0)
                .sum();

        double averageFee = totalSpent / total;

        // Besten und schlechtesten Transfer finden
        Optional<Transfer> bestTransfer = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && t.getWasSuccessful())
                .max(Comparator.comparing(t -> t.getTransferFee() != null ? t.getTransferFee() : 0));

        Optional<Transfer> worstTransfer = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && !t.getWasSuccessful())
                .max(Comparator.comparing(t -> t.getTransferFee() != null ? t.getTransferFee() : 0));

        ClubStatistics stats = ClubStatistics.builder()
                .clubName(clubName)
                .totalTransfers(total)
                .successfulTransfers((int) successful)
                .successRate(Math.round(successRate * 10) / 10.0)
                .totalSpent(Math.round(totalSpent * 10) / 10.0)
                .averageFee(Math.round(averageFee * 10) / 10.0)
                .bestTransfer(bestTransfer.map(t -> t.getPlayerName() + " (" + t.getTransferFee() + " Mio)").orElse("Keine"))
                .worstTransfer(worstTransfer.map(t -> t.getPlayerName() + " (" + t.getTransferFee() + " Mio)").orElse("Keine"))
                .build();

        log.info("✅ Statistiken für {} geladen: {}% Erfolg, {} Mio gesamt",
                clubName, stats.getSuccessRate(), stats.getTotalSpent());

        return stats;
    }

    @Override
    public List<ClubStatistics> getTopClubs(int limit) {
        log.info("🏆 Hole Top {} Vereine", limit);

        // Alle Clubs mit Transfers finden
        Map<String, List<Transfer>> clubTransfers = transferRepository.findAll().stream()
                .filter(t -> t.getToClub() != null)
                .collect(Collectors.groupingBy(Transfer::getToClub));

        List<ClubStatistics> allClubStats = clubTransfers.entrySet().stream()
                .map(entry -> calculateBasicStats(entry.getKey(), entry.getValue()))
                .filter(stats -> stats.getTotalTransfers() >= 3) // Mindestens 3 Transfers für Aussagekraft
                .sorted((a, b) -> Double.compare(b.getSuccessRate(), a.getSuccessRate()))
                .limit(limit)
                .collect(Collectors.toList());

        log.info("✅ {} Top-Vereine gefunden", allClubStats.size());
        return allClubStats;
    }

    @Override
    public List<ClubStatistics> getWorstClubs(int limit) {
        log.info("📉 Hole schlechteste {} Vereine", limit);

        Map<String, List<Transfer>> clubTransfers = transferRepository.findAll().stream()
                .filter(t -> t.getToClub() != null)
                .collect(Collectors.groupingBy(Transfer::getToClub));

        List<ClubStatistics> allClubStats = clubTransfers.entrySet().stream()
                .map(entry -> calculateBasicStats(entry.getKey(), entry.getValue()))
                .filter(stats -> stats.getTotalTransfers() >= 3)
                .sorted(Comparator.comparing(ClubStatistics::getSuccessRate))
                .limit(limit)
                .collect(Collectors.toList());

        log.info("✅ {} schlechteste Vereine gefunden", allClubStats.size());
        return allClubStats;
    }

    @Override
    public String compareClubs(String club1, String club2) {
        ClubStatistics stats1 = getClubStatistics(club1);
        ClubStatistics stats2 = getClubStatistics(club2);

        StringBuilder comparison = new StringBuilder();
        comparison.append(String.format("📊 Vergleich: %s vs %s\n\n", club1, club2));
        comparison.append(String.format("%s: %.1f%% Erfolg (%.1f Mio gesamt)\n",
                club1, stats1.getSuccessRate(), stats1.getTotalSpent()));
        comparison.append(String.format("%s: %.1f%% Erfolg (%.1f Mio gesamt)\n",
                club2, stats2.getSuccessRate(), stats2.getTotalSpent()));

        if (stats1.getSuccessRate() > stats2.getSuccessRate()) {
            comparison.append(String.format("\n🏆 %s ist erfolgreicher im Transfergeschäft!", club1));
        } else if (stats2.getSuccessRate() > stats1.getSuccessRate()) {
            comparison.append(String.format("\n🏆 %s ist erfolgreicher im Transfergeschäft!", club2));
        } else {
            comparison.append("\n🤝 Beide Clubs sind gleich erfolgreich");
        }

        return comparison.toString();
    }

    private ClubStatistics calculateBasicStats(String clubName, List<Transfer> transfers) {
        int total = transfers.size();
        long successful = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && t.getWasSuccessful())
                .count();

        double successRate = (double) successful / total * 100;

        double totalSpent = transfers.stream()
                .mapToDouble(t -> t.getTransferFee() != null ? t.getTransferFee() : 0.0)
                .sum();

        double averageFee = totalSpent / total;  // ← NEU berechnen

        // Besten und schlechtesten Transfer finden
        Optional<Transfer> bestTransfer = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && t.getWasSuccessful())
                .max(Comparator.comparing(t -> t.getTransferFee() != null ? t.getTransferFee() : 0));

        Optional<Transfer> worstTransfer = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && !t.getWasSuccessful())
                .max(Comparator.comparing(t -> t.getTransferFee() != null ? t.getTransferFee() : 0));

        return ClubStatistics.builder()
                .clubName(clubName)
                .totalTransfers(total)
                .successfulTransfers((int) successful)
                .successRate(Math.round(successRate * 10) / 10.0)
                .totalSpent(Math.round(totalSpent * 10) / 10.0)
                .averageFee(Math.round(averageFee * 10) / 10.0)  // ← NEU
                .bestTransfer(bestTransfer.map(t -> t.getPlayerName() + " (" + t.getTransferFee() + " Mio)").orElse("Keine"))  // ← NEU
                .worstTransfer(worstTransfer.map(t -> t.getPlayerName() + " (" + t.getTransferFee() + " Mio)").orElse("Keine"))  // ← NEU
                .build();
    }

    private ClubStatistics createEmptyStatistics(String clubName) {
        return ClubStatistics.builder()
                .clubName(clubName)
                .totalTransfers(0)
                .successfulTransfers(0)
                .successRate(0.0)
                .totalSpent(0.0)
                .averageFee(0.0)
                .bestTransfer("Keine Daten")
                .worstTransfer("Keine Daten")
                .build();
    }
}