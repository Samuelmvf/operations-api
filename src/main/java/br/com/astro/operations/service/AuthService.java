package br.com.astro.operations.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.astro.operations.domain.dto.request.LoginRequestDTO;
import br.com.astro.operations.domain.dto.request.RegisterRequestDTO;
import br.com.astro.operations.domain.dto.response.AuthResponseDTO;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.AuthenticationException;
import br.com.astro.operations.exception.UserAlreadyExistsException;
import br.com.astro.operations.repository.UserRepository;
import br.com.astro.operations.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for user: {}", request.username());
        
        UserEntity user = repository.findActiveByUsername(request.username())
            .orElseThrow(() -> {
                log.warn("Login failed - user not found: {}", request.username());
                return new AuthenticationException("Invalid credentials");
            });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed - invalid password for user: {}", request.username());
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.isActive()) {
            log.warn("Login failed - inactive user: {}", request.username());
            throw new AuthenticationException("User account is inactive");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        
        log.info("Login successful for user: {} (ID: {})", user.getUsername(), user.getId());
        
        return AuthResponseDTO.of(accessToken, refreshToken, user.getId(), user.getUsername(), user.getBalance(), 86400L);
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Registration attempt for user: {}", request.username());
        
        if (repository.existsByUsername(request.username())) {
            log.warn("Registration failed - username already exists: {}", request.username());
            throw new UserAlreadyExistsException("Username already exists");
        }

        UserEntity user = UserEntity.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .balance(BigDecimal.valueOf(250.00))
            .status(UserEntity.UserStatus.ACTIVE)
            .deleted(false)
            .build();

        user = repository.save(user);
        
        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());
        
        log.info("Registration successful for user: {} (ID: {})", user.getUsername(), user.getId());

        return AuthResponseDTO.of(accessToken, refreshToken, user.getId(), user.getUsername(), user.getBalance(), 86400L);
    }

    public void logout(String token) {
        log.info("Logout request received");
        jwtTokenProvider.blacklistToken(token);
        log.info("User logged out successfully");
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        log.info("Refresh token request received");
        
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Refresh token validation failed");
            throw new AuthenticationException("Invalid refresh token");
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            log.warn("Invalid token type for refresh: {}", tokenType);
            throw new AuthenticationException("Invalid token type");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        UserEntity user = repository.findActiveByUsername(username)
            .orElseThrow(() -> {
                log.warn("User not found for refresh token: {}", username);
                return new AuthenticationException("User not found");
            });

        jwtTokenProvider.blacklistToken(refreshToken);

        String newAccessToken = jwtTokenProvider.generateToken(user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        log.info("Token refreshed successfully for user: {}", username);
        
        return AuthResponseDTO.of(newAccessToken, newRefreshToken, user.getId(), user.getUsername(), user.getBalance(), 86400L);
    }
}
