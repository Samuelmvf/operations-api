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

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDTO getCurrentUser(UUID userId) {
        UserEntity user = repository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return mapper.toDTO(user);
    }

    public UserDTO getCurrentUserByUsername(String username) {
        UserEntity user = repository.findActiveByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return mapper.toDTO(user);
    }
}
