package com.transfermark.transfermarkt_analyst.repository;



import com.transfermark.transfermarkt_analyst.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Suchmethoden (Spring macht das automatisch!)
    List<Player> findByCurrentClub(String club);

    List<Player> findByPosition(String position);

    List<Player> findByMarketValueGreaterThan(double value);

    List<Player> findByPlaysInSerieATrue();
}