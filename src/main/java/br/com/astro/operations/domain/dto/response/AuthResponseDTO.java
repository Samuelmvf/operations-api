package br.com.astro.operations.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthResponseDTO(
    String token,
    String type,
    UUID userId,
    String username,
    BigDecimal balance
) {
    public static AuthResponseDTO of(String token, UUID userId, String username, BigDecimal balance) {
        return new AuthResponseDTO(token, "Bearer", userId, username, balance);
    }
}
