package com.transfermarkt.transfermarkt_analyst.repository;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ScoutingRepository extends JpaRepository<Scouting, Long> {
    Optional<Scouting> findByPlayerId(String playerId);
    boolean existsByPlayerId(String playerId);
    void deleteByPlayerId(String playerId);
}
