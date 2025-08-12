package br.com.astro.operations.service;

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

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthResponseDTO login(LoginRequestDTO request) {
        UserEntity user = repository.findActiveByUsername(request.username())
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new AuthenticationException("User account is inactive");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        return AuthResponseDTO.of(token, user.getId(), user.getUsername(), user.getBalance());
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (repository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        UserEntity user = UserEntity.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .balance(request.initialBalance())
            .status(UserEntity.UserStatus.ACTIVE)
            .deleted(false)
            .build();

        user = repository.save(user);
        String token = jwtTokenProvider.generateToken(user.getUsername());

        return AuthResponseDTO.of(token, user.getId(), user.getUsername(), user.getBalance());
    }
}
