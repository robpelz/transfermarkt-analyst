package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.ClubScore;
import com.transfermark.transfermarkt_analyst.model.Transfer;
import com.transfermark.transfermarkt_analyst.repository.TransferRepository;
import com.transfermark.transfermarkt_analyst.config.AppProperties;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ClubScoreServiceImpl implements ClubScoreService {

    private static final Logger log = LoggerFactory.getLogger(ClubScoreServiceImpl.class);
    private final TransferRepository transferRepository;
    private final AppProperties appProperties;

    public ClubScoreServiceImpl(TransferRepository transferRepository, AppProperties appProperties) {
        this.transferRepository = transferRepository;
        this.appProperties = appProperties;
    }

    @Override
    public ClubScore calculateClubScore(String clubName) {
        log.info("📊 Berechne Club-Score für: {}", clubName);

        List<Transfer> transfers = transferRepository.findByToClub(clubName);
        int total = transfers.size();

        log.debug("Gefundene Transfers für {}: {}", clubName, total);

        if (total == 0) {
            log.warn("⚠️ Keine Transfers gefunden für: {}", clubName);
            return new ClubScore(
                    clubName,
                    0.0,
                    0,
                    "Keine Transferdaten für " + clubName
            );
        }

        long successful = transfers.stream()
                .filter(t -> t.getWasSuccessful() != null && t.getWasSuccessful())
                .count();

        log.debug("Erfolgreiche Transfers: {} von {}", successful, total);

        double successRate = (double) successful / total * 100;

        String recommendation = generateRecommendation(successRate, clubName);
        double roundedRate = Math.round(successRate * 10) / 10.0;

        log.info("📊 Finaler Score für {}: {}% ({} Transfers)",
                clubName, roundedRate, total);

        return new ClubScore(
                clubName,
                roundedRate,
                total,
                recommendation
        );
    }

    @Override
    public List<String> getTopClubs(int limit) {
        // Diese Methode könntest du später implementieren
        return List.of(); // Platzhalter
    }

    @Override
    public boolean hasEnoughData(String clubName) {
        int minTransfers = appProperties.getClub().getMinTransfersForScore();
        long count = transferRepository.countByToClub(clubName);
        return count >= minTransfers;
    }

    private String generateRecommendation(double successRate, String clubName) {
        if (successRate >= 75) {
            log.info("🏆 {} gehört zur Weltspitze", clubName);
            return "🏆 Weltklasse! Dieser Club macht fantastische Transfers.";
        } else if (successRate >= 60) {
            log.info("👍 {} macht solide bis gute Transfers", clubName);
            return "👍 Sehr gute Transferpolitik.";
        } else if (successRate >= 45) {
            log.info("📊 {} hat durchschnittliche Transferbilanz", clubName);
            return "📊 Solide - viele gute, aber auch einige Flops.";
        } else if (successRate >= 30) {
            log.warn("⚠️ {} sollte Transferstrategie überdenken", clubName);
            return "⚠️ Verbesserungswürdig - zu viele Fehlkäufe.";
        } else {
            log.error("❌ {} hat sehr schlechte Transferbilanz", clubName);
            return "❌ Alarmierend! Dieser Club muss seine Transferstrategie überdenken.";
        }
    }
}