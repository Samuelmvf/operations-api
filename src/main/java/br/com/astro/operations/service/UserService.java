package br.com.astro.operations.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.astro.operations.domain.dto.response.UserDTO;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.UserNotFoundException;
import br.com.astro.operations.mapper.UserMapper;
import br.com.astro.operations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDTO getCurrentUser(UUID userId) {
        log.debug("Fetching user profile for user ID: {}", userId);
        UserEntity user = repository.findById(userId)
            .orElseThrow(() -> {
                log.warn("User not found with ID: {}", userId);
                return new UserNotFoundException("User not found");
            });
        
        log.debug("User profile retrieved successfully for: {}", user.getUsername());
        return mapper.toDTO(user);
    }

    public UserDTO getCurrentUserByUsername(String username) {
        log.debug("Fetching user profile for username: {}", username);
        UserEntity user = repository.findActiveByUsername(username)
            .orElseThrow(() -> {
                log.warn("Active user not found with username: {}", username);
                return new UserNotFoundException("User not found");
            });
        
        log.debug("User profile retrieved successfully for: {}", username);
        return mapper.toDTO(user);
    }
}
