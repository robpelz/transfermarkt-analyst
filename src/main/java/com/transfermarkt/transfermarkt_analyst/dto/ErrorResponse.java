package com.transfermarkt.transfermarkt_analyst.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    // Factory Methods für häufige Fehlertypen
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse notFound(String message, String path) {
        return of(HttpStatus.NOT_FOUND, message, path);
    }

    public static ErrorResponse badRequest(String message, String path) {
        return of(HttpStatus.BAD_REQUEST, message, path);
    }

    public static ErrorResponse unauthorized(String message, String path) {
        return of(HttpStatus.UNAUTHORIZED, message, path);
    }

    public static ErrorResponse forbidden(String message, String path) {
        return of(HttpStatus.FORBIDDEN, message, path);
    }

    public static ErrorResponse internalError(String message, String path) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, message, path);
    }

    public static ErrorResponse conflict(String message, String path) {
        return of(HttpStatus.CONFLICT, message, path);
    }
}