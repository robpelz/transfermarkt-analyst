package com.transfermarkt.transfermarkt_analyst.repository;

import com.transfermarkt.transfermarkt_analyst.model.PlayerMarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerMarketDataRepository extends JpaRepository<PlayerMarketData, Long> {
    Optional<PlayerMarketData> findTopByPlayerNameOrderBySeasonDesc(String playerName);

}