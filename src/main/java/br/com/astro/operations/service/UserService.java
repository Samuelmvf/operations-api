package br.com.astro.operations.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.astro.operations.domain.dto.response.UserDTO;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.UserNotFoundException;
import br.com.astro.operations.mapper.UserMapper;
import br.com.astro.operations.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO getCurrentUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return userMapper.toDTO(user);
    }

    public UserDTO getCurrentUserByUsername(String username) {
        UserEntity user = userRepository.findActiveByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return userMapper.toDTO(user);
    }
}
