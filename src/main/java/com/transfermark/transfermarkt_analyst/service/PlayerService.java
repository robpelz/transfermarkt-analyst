package com.transfermark.transfermarkt_analyst.service;

import com.transfermark.transfermarkt_analyst.dto.PlayerListResponse;
import com.transfermark.transfermarkt_analyst.dto.PlayerResponse;
import com.transfermark.transfermarkt_analyst.dto.PlayerRequest;
import com.transfermark.transfermarkt_analyst.model.Player;
import java.util.List;
import java.util.Optional;

public interface PlayerService {

    List<PlayerListResponse> getAllPlayers();

    Optional<PlayerResponse> getPlayerById(Long id);

    PlayerResponse createPlayer(PlayerRequest request);

    PlayerResponse updatePlayer(Long id, PlayerRequest request);

    void deletePlayer(Long id);

    List<Player> findByClub(String clubName);

    boolean existsByName(String name);
}