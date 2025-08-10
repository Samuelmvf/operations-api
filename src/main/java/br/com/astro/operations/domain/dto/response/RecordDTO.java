package br.com.astro.operations.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.astro.operations.domain.entity.OperationEntity;

public record RecordDTO(
    UUID id,
    UUID operationId,
    OperationEntity.OperationType operationType,
    BigDecimal amount,
    BigDecimal userBalance,
    String operationResponse,
    LocalDateTime createdAt
) {}
