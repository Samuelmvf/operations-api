package br.com.astro.operations.domain.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record OperationRequestDTO(
    BigDecimal operand1,
    BigDecimal operand2,
    
    @Min(value = 1, message = "Length must be at least 1")
    Integer length
) {}
