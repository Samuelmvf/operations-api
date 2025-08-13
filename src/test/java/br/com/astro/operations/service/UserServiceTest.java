package br.com.astro.operations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.astro.operations.domain.dto.response.UserDTO;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.UserNotFoundException;
import br.com.astro.operations.mapper.UserMapper;
import br.com.astro.operations.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock private UserRepository userRepository;
	@Mock private UserMapper userMapper;
	@InjectMocks private UserService userService;

	@Test
	@DisplayName("getCurrentUser ok")
	void getCurrentUser_ok() {
		UUID id = UUID.randomUUID();
		UserEntity user = new UserEntity();
		user.setId(id);
		when(userRepository.findById(id)).thenReturn(Optional.of(user));
		UserDTO dto = UserDTO.builder()
			.id(id)
			.username("u")
			.status(null)
			.balance(null)
			.createdAt(null)
			.build();
		when(userMapper.toDTO(user)).thenReturn(dto);
		UserDTO res = userService.getCurrentUser(id);
		assertEquals(id, res.id());
	}

	@Test
	@DisplayName("getCurrentUser throws when not found")
	void getCurrentUser_notFound() {
		UUID id = UUID.randomUUID();
		when(userRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser(id));
	}

	@Test
	@DisplayName("getCurrentUserByUsername ok")
	void getCurrentUserByUsername_ok() {
		UserEntity user = new UserEntity();
		user.setUsername("john");
		when(userRepository.findActiveByUsername("john")).thenReturn(Optional.of(user));
		UserDTO dto = UserDTO.builder()
			.username("john")
			.build();
		when(userMapper.toDTO(user)).thenReturn(dto);
		UserDTO res = userService.getCurrentUserByUsername("john");
		assertEquals("john", res.username());
	}

	@Test
	@DisplayName("getCurrentUserByUsername throws when not found")
	void getCurrentUserByUsername_notFound() {
		when(userRepository.findActiveByUsername("missing")).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> userService.getCurrentUserByUsername("missing"));
	}
}
