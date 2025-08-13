package br.com.astro.operations.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegisterRequestDTO(
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email")
    String username,
    
    @NotBlank(message = "Password is required")
    String password
) {}
