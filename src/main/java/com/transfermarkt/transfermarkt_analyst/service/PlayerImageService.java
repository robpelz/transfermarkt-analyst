package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlayerImageService {

    private static final Logger log = LoggerFactory.getLogger(PlayerImageService.class);
    private final TheSportsDbClient theSportsDbClient;

    public PlayerImageService(TheSportsDbClient theSportsDbClient) {
        this.theSportsDbClient = theSportsDbClient;
    }

    /**
     * Ergänzt einen SoFifaPlayer mit Bild-URLs
     */
    public PlayerWithImage enrichWithImage(SoFifaPlayer player) {
        if (player == null) return null;

        String imageUrl = theSportsDbClient.getBestPlayerImage(player.getName());

        return PlayerWithImage.builder()
                .player(player)
                .imageUrl(imageUrl)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class PlayerWithImage {
        private SoFifaPlayer player;
        private String imageUrl;
    }
}