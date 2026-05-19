package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.dto.sofifa.SoFifaPlayer;
import com.transfermarkt.transfermarkt_analyst.dto.thesportsdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Service
public class TheSportsDbClient {

    private static final Logger log = LoggerFactory.getLogger(TheSportsDbClient.class);
    private final RestTemplate restTemplate;

    @Value("${thesportsdb.api.base-url:https://www.thesportsdb.com/api/v1/json/3}")
    private String baseUrl;

    public TheSportsDbClient() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            return execution.execute(request, body);
        });
    }

    public List<SoFifaPlayer> searchPlayers(String playerName) {
        log.info("🔍 TheSportsDB Suche nach Spieler: {}", playerName);

        try {
            String encodedName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
            String url = baseUrl + "/searchplayers.php?p=" + encodedName;

            PlayerImageResponse response = restTemplate.getForObject(url, PlayerImageResponse.class);
            List<SoFifaPlayer> result = new ArrayList<>();

            if (response != null && response.getPlayers() != null) {
                for (PlayerImageResponse.PlayerImage playerImg : response.getPlayers()) {
                    SoFifaPlayer player = new SoFifaPlayer();
                    player.setId(playerImg.getId());  // String, kein parseInt
                    player.setName(playerImg.getName());
                    player.setNationality(playerImg.getNationality());
                    player.setClub(playerImg.getTeam());
                    player.setImageUrl(playerImg.getCutout() != null ? playerImg.getCutout() : playerImg.getThumb());
                    player.setPositions(List.of("?"));
                    player.setAge(0);
                    player.setValue("");
                    result.add(player);
                }
            }

            log.info("✅ {} Spieler gefunden für '{}'", result.size(), playerName);
            return result;

        } catch (Exception e) {
            log.error("❌ Fehler bei TheSportsDB Spielersuche: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public SoFifaPlayer getPlayerById(String playerId) {
        log.info("🔍 TheSportsDB Spieler mit ID: {}", playerId);

        try {
            String url = baseUrl + "/lookupplayer.php?id=" + playerId;
            PlayerImageResponse response = restTemplate.getForObject(url, PlayerImageResponse.class);

            if (response != null && response.getPlayers() != null && !response.getPlayers().isEmpty()) {
                PlayerImageResponse.PlayerImage playerImg = response.getPlayers().get(0);
                SoFifaPlayer player = new SoFifaPlayer();
                player.setId(playerImg.getId());  // String, kein parseInt
                player.setName(playerImg.getName());
                player.setNationality(playerImg.getNationality());
                player.setClub(playerImg.getTeam());
                player.setImageUrl(playerImg.getCutout() != null ? playerImg.getCutout() : playerImg.getThumb());
                player.setPositions(List.of("?"));
                return player;
            }

            return null;

        } catch (Exception e) {
            log.error("❌ Fehler bei TheSportsDB Spieler-Details: {}", e.getMessage());
            return null;
        }
    }

    @Cacheable(value = "playerImages", key = "#playerName")
    public PlayerImageResponse.PlayerImage getPlayerImages(String playerName) {
        log.info("🖼️ Suche Bilder für Spieler: {}", playerName);
        try {
            String encodedName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
            String url = baseUrl + "/searchplayers.php?p=" + encodedName;

            PlayerImageResponse response = restTemplate.getForObject(url, PlayerImageResponse.class);

            if (response != null && response.getPlayers() != null && !response.getPlayers().isEmpty()) {
                return response.getPlayers().get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("❌ Fehler bei Bildersuche: {}", e.getMessage());
            return null;
        }
    }

    public String getBestPlayerImage(String playerName) {
        PlayerImageResponse.PlayerImage player = getPlayerImages(playerName);
        if (player == null) return null;
        if (player.getCutout() != null) return player.getCutout();
        if (player.getRender() != null) return player.getRender();
        if (player.getThumb() != null) return player.getThumb();
        return null;
    }

    @Cacheable(value = "topLeagues", key = "'all'")
    public List<TheSportsDbLeague> getTopLeagues() {
        log.info("🏆 Hole Top-Ligen von TheSportsDB");

        String[] leagueIds = {"4328", "4332", "4334", "4331", "4335"};
        List<TheSportsDbLeague> leagues = new ArrayList<>();

        for (String leagueId : leagueIds) {
            try {
                String url = baseUrl + "/lookupleague.php?id=" + leagueId;
                LeagueResponse response = restTemplate.getForObject(url, LeagueResponse.class);

                if (response != null && response.getLeagues() != null && !response.getLeagues().isEmpty()) {
                    TheSportsDbLeague league = response.getLeagues().get(0);
                    leagues.add(league);
                    log.debug("✅ Liga geladen: {}", league.getName());
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("❌ Fehler beim Laden der Liga {}: {}", leagueId, e.getMessage());
            }
        }

        log.info("✅ {} Ligen geladen", leagues.size());
        return leagues;
    }

    @Cacheable(value = "teamsByLeague", key = "#leagueName")
    public List<TheSportsDbTeam> getTeamsByLeague(String leagueName) {
        log.info("🔍 Suche Teams für Liga: {}", leagueName);

        try {
            Thread.sleep(500);
            String encodedName = URLEncoder.encode(leagueName, StandardCharsets.UTF_8);
            String url = baseUrl + "/search_all_teams.php?l=" + encodedName;
            TheSportsDbTeamSearchResponse response = restTemplate.getForObject(url, TheSportsDbTeamSearchResponse.class);

            if (response != null && response.getTeams() != null) {
                log.info("✅ {} Teams gefunden für {}", response.getTeams().size(), leagueName);
                return response.getTeams();
            }
            return Collections.emptyList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("❌ Fehler bei Team-Suche: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public TheSportsDbTeam getTeamById(String teamId) {
        log.info("🔍 Hole Team mit ID: {}", teamId);

        try {
            String url = baseUrl + "/lookupteam.php?id=" + teamId;
            TheSportsDbTeamSearchResponse response = restTemplate.getForObject(url, TheSportsDbTeamSearchResponse.class);

            if (response != null && response.getTeams() != null && !response.getTeams().isEmpty()) {
                return response.getTeams().get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("❌ Fehler bei Team-Details {}: {}", teamId, e.getMessage());
            return null;
        }
    }

    public boolean isAvailable() {
        try {
            String url = baseUrl + "/searchplayers.php?p=test";
            restTemplate.getForObject(url, String.class);
            log.info("✅ TheSportsDB verbunden");
            return true;
        } catch (Exception e) {
            log.warn("❌ TheSportsDB nicht erreichbar");
            return false;
        }
    }
}