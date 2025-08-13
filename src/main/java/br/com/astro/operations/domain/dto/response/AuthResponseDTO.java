package br.com.astro.operations.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthResponseDTO(
    String accessToken,
    String refreshToken,
    String type,
    UUID userId,
    String username,
    BigDecimal balance,
    long expiresIn
) {
    public static AuthResponseDTO of(String accessToken, String refreshToken, UUID userId, String username, BigDecimal balance, long expiresIn) {
        return new AuthResponseDTO(accessToken, refreshToken, "Bearer", userId, username, balance, expiresIn);
    }
}
