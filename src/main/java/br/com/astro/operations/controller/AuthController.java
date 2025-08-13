package br.com.astro.operations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.astro.operations.domain.dto.request.LoginRequestDTO;
import br.com.astro.operations.domain.dto.request.RefreshTokenRequestDTO;
import br.com.astro.operations.domain.dto.request.RegisterRequestDTO;
import br.com.astro.operations.domain.dto.response.AuthResponseDTO;
import br.com.astro.operations.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService service;

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User registration", description = "Register a new user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration successful",
                content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "User already exists or invalid input",
                content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh token", description = "Refresh a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        AuthResponseDTO response = service.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout user", description = "Logout user and invalidate JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Invalid token",
                content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            service.logout(token);
        }
        return ResponseEntity.ok().build();
    }
}
