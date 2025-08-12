package br.com.astro.operations.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.astro.operations.domain.entity.OperationEntity;

public record OperationDTO(
    UUID id,
    OperationEntity.OperationType type,
    BigDecimal cost
) {}
