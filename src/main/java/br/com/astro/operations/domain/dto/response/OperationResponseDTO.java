package br.com.astro.operations.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationResponseDTO(
    String result,
    BigDecimal cost,
    BigDecimal remainingBalance,
    UUID recordId
) {}
