package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoFifaClient {

    private static final Logger log = LoggerFactory.getLogger(SoFifaClient.class);
    private final TheSportsDbClient theSportsDbClient;
    private final DatabasePlayerService DatabasePlayerService;

    public SoFifaClient(TheSportsDbClient theSportsDbClient,
                        DatabasePlayerService DatabasePlayerService) {
        this.theSportsDbClient = theSportsDbClient;
        this.DatabasePlayerService = DatabasePlayerService;
    }

    /**
     * Sucht nach Spielern - zuerst eigene DB, dann TheSportsDB
     */
    @Cacheable(value = "sofifaSearch", key = "#query")
    public List<SoFifaPlayer> searchPlayers(String query) {
        log.info("🔍 Suche nach Spieler: {} (zuerst eigene DB)", query);

        // 1. Versuche eigene Datenbank
        try {
            List<SoFifaPlayer> ownResults = DatabasePlayerService.searchPlayers(query);
            if (ownResults != null && !ownResults.isEmpty()) {
                log.info("✅ {} Spieler in eigener DB gefunden", ownResults.size());
                return ownResults;
            }
        } catch (Exception e) {
            log.warn("Fehler bei eigener DB-Suche: {}", e.getMessage());
        }

        // 2. Fallback zu TheSportsDB
        log.info("🔄 Fallback zu TheSportsDB für: {}", query);
        return theSportsDbClient.searchPlayers(query);
    }

    /**
     * Holt einen Spieler per ID - zuerst eigene DB, dann TheSportsDB
     */
    @Cacheable(value = "sofifaPlayer", key = "#playerId")
    public SoFifaPlayer getPlayerById(String playerId) {
        log.info("🔍 Hole Spieler mit ID: {} (zuerst eigene DB)", playerId);

        // 1. Versuche eigene Datenbank
        try {
            SoFifaPlayer ownPlayer = DatabasePlayerService.getPlayerById(playerId);
            if (ownPlayer != null) {
                log.info("✅ Spieler aus eigener DB geladen: {}", ownPlayer.getName());

                // Optional: Bild aus TheSportsDB nachreichen
                try {
                    String imageUrl = theSportsDbClient.getBestPlayerImage(ownPlayer.getName());
                    if (imageUrl != null) {
                        ownPlayer.setImageUrl(imageUrl);
                    }
                } catch (Exception e) {
                    log.warn("Kein Bild für {} gefunden", ownPlayer.getName());
                }

                return ownPlayer;
            }
        } catch (Exception e) {
            log.warn("Fehler bei eigener DB: {}", e.getMessage());
        }

        // 2. Fallback zu TheSportsDB
        log.info("🔄 Fallback zu TheSportsDB für ID: {}", playerId);
        return theSportsDbClient.getPlayerById(playerId);
    }

    public boolean isCrsetAvailable() {
        // Eigene DB ist immer verfügbar (lokal)
        return true;
    }
}