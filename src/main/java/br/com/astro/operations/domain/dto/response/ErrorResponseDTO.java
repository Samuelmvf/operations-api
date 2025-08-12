package br.com.astro.operations.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record ErrorResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<String> details
) {
    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return ErrorResponseDTO.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .path(path)
            .details(null)
            .build();
    }

    public static ErrorResponseDTO of(int status, String error, String message, String path, List<String> details) {
        return ErrorResponseDTO.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .path(path)
            .details(details)
            .build();
    }
}
