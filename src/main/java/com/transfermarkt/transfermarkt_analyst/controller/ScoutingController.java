package com.transfermarkt.transfermarkt_analyst.controller;

import com.transfermarkt.transfermarkt_analyst.model.Scouting;
import com.transfermarkt.transfermarkt_analyst.repository.ScoutingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scouting")
@CrossOrigin(origins = "http://localhost:5173")
public class ScoutingController {

    private final ScoutingRepository scoutingRepository;

    public ScoutingController(ScoutingRepository scoutingRepository) {
        this.scoutingRepository = scoutingRepository;
    }

    @GetMapping
    public List<Scouting> getAll() {
        return scoutingRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scouting> getById(@PathVariable Long id) {
        Optional<Scouting> scouting = scoutingRepository.findById(id);
        return scouting.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{playerId}")
    public ResponseEntity<Scouting> create(@PathVariable String playerId,
                                           @RequestParam String playerName,
                                           @RequestParam Integer rating,
                                           @RequestParam(required = false) String note,
                                           @RequestParam(required = false) Integer talent,
                                           @RequestParam(required = false) Integer speed,
                                           @RequestParam(required = false) Integer tactics,
                                           @RequestParam(required = false) Integer passing,
                                           @RequestParam(required = false) Integer technique,
                                           @RequestParam(required = false) Integer fitness,
                                           @RequestParam(required = false) Integer tackling) {
        Scouting scouting = new Scouting();
        scouting.setPlayerId(playerId);
        scouting.setPlayerName(playerName);
        scouting.setRating(rating);
        scouting.setNote(note);
        scouting.setTalent(talent != null ? talent : 70);
        scouting.setSpeed(speed != null ? speed : 70);
        scouting.setTactics(tactics != null ? tactics : 70);
        scouting.setPassing(passing != null ? passing : 70);
        scouting.setTechnique(technique != null ? technique : 70);
        scouting.setFitness(fitness != null ? fitness : 70);
        scouting.setTackling(tackling != null ? tackling : 30);
        return ResponseEntity.ok(scoutingRepository.save(scouting));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Scouting> update(@PathVariable Long id,
                                           @RequestParam Integer rating,
                                           @RequestParam(required = false) String note,
                                           @RequestParam(required = false) Integer talent,
                                           @RequestParam(required = false) Integer speed,
                                           @RequestParam(required = false) Integer tactics,
                                           @RequestParam(required = false) Integer passing,
                                           @RequestParam(required = false) Integer technique,
                                           @RequestParam(required = false) Integer fitness,
                                           @RequestParam(required = false) Integer tackling) {
        Optional<Scouting> existing = scoutingRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Scouting scouting = existing.get();
        scouting.setRating(rating);
        if (note != null) scouting.setNote(note);
        if (talent != null) scouting.setTalent(talent);
        if (speed != null) scouting.setSpeed(speed);
        if (tactics != null) scouting.setTactics(tactics);
        if (passing != null) scouting.setPassing(passing);
        if (technique != null) scouting.setTechnique(technique);
        if (fitness != null) scouting.setFitness(fitness);
        if (tackling != null) scouting.setTackling(tackling);
        return ResponseEntity.ok(scoutingRepository.save(scouting));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!scoutingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        scoutingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}