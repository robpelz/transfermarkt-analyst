package com.transfermark.transfermarkt_analyst.exception;

public class ClubNotFoundException extends RuntimeException {
    public ClubNotFoundException(String clubName) {
        super("Verein '" + clubName + "' nicht gefunden");
    }
}