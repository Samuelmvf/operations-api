package br.com.astro.operations.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.astro.operations.domain.entity.UserEntity;
import lombok.Builder;

@Builder
public record UserDTO(
    UUID id,
    String username,
    UserEntity.UserStatus status,
    BigDecimal balance,
    LocalDateTime createdAt
) {}
