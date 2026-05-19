package com.transfermarkt.transfermarkt_analyst.service;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import com.transfermarkt.transfermarkt_analyst.repository.ScoutingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScoutingServiceImpl implements ScoutingService {

    private final ScoutingRepository scoutingRepository;

    public ScoutingServiceImpl(ScoutingRepository scoutingRepository) {
        this.scoutingRepository = scoutingRepository;
    }

    @Override
    public Scouting addScouting(String playerId, String playerName, Integer rating, String note) {
        Scouting scouting = new Scouting();
        scouting.setPlayerId(playerId);
        scouting.setPlayerName(playerName);
        scouting.setRating(rating);
        scouting.setNote(note);
        return scoutingRepository.save(scouting);
    }

    @Override
    public List<Scouting> getAllScouting() {
        return scoutingRepository.findAll();
    }

    @Override
    public Scouting getScoutingByPlayerId(String playerId) {
        return scoutingRepository.findByPlayerId(playerId).orElse(null);
    }

    @Override
    public Scouting updateScouting(String playerId, Integer rating, String note) {
        Scouting scouting = scoutingRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new RuntimeException("Scouting entry not found for player: " + playerId));
        if (rating != null) scouting.setRating(rating);
        if (note != null) scouting.setNote(note);
        return scoutingRepository.save(scouting);
    }

    @Override
    public void deleteScouting(String playerId) {
        scoutingRepository.deleteByPlayerId(playerId);
    }

    @Override
    public boolean existsByPlayerId(String playerId) {
        return scoutingRepository.existsByPlayerId(playerId);
    }
}
