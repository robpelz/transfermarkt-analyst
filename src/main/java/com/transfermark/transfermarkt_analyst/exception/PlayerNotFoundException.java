package com.transfermark.transfermarkt_analyst.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long id) {
        super("Spieler mit ID " + id + " nicht gefunden");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}