package com.transfermarkt.transfermarkt_analyst.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scouting")
public class Scouting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private String playerId;

    @Column(name = "player_name")
    private String playerName;

    private Integer rating;

    @Column(length = 500)
    private String note;

    // Neue Felder für Stärken/Schwächen
    private Integer talent = 70;
    private Integer speed = 70;
    private Integer tactics = 70;
    private Integer passing = 70;
    private Integer technique = 70;
    private Integer fitness = 70;
    private Integer tackling = 30;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getter und Setter (alle Felder)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getTalent() { return talent; }
    public void setTalent(Integer talent) { this.talent = talent; }

    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }

    public Integer getTactics() { return tactics; }
    public void setTactics(Integer tactics) { this.tactics = tactics; }

    public Integer getPassing() { return passing; }
    public void setPassing(Integer passing) { this.passing = passing; }

    public Integer getTechnique() { return technique; }
    public void setTechnique(Integer technique) { this.technique = technique; }

    public Integer getFitness() { return fitness; }
    public void setFitness(Integer fitness) { this.fitness = fitness; }

    public Integer getTackling() { return tackling; }
    public void setTackling(Integer tackling) { this.tackling = tackling; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}