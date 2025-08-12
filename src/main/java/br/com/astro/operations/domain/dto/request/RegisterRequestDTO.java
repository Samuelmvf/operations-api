package br.com.astro.operations.domain.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email")
    String username,
    
    @NotBlank(message = "Password is required")
    String password,
    
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", message = "Initial balance must be positive")
    BigDecimal initialBalance
) {}
