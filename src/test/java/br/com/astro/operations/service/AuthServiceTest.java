package br.com.astro.operations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.astro.operations.domain.dto.request.LoginRequestDTO;
import br.com.astro.operations.domain.dto.request.RegisterRequestDTO;
import br.com.astro.operations.domain.dto.response.AuthResponseDTO;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.AuthenticationException;
import br.com.astro.operations.exception.UserAlreadyExistsException;
import br.com.astro.operations.repository.UserRepository;
import br.com.astro.operations.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private AuthService authService;

	private UserEntity activeUser;

	@BeforeEach
	void setUp() {
		activeUser = UserEntity.builder()
			.id(UUID.randomUUID())
			.username("john@example.com")
			.password("encoded")
			.balance(BigDecimal.valueOf(100))
			.status(UserEntity.UserStatus.ACTIVE)
			.deleted(false)
			.build();
	}

	@Test
	@DisplayName("login should return token on valid credentials")
	void login_success() {
		when(userRepository.findActiveByUsername("john@example.com")).thenReturn(Optional.of(activeUser));
		when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
		when(jwtTokenProvider.generateToken("john@example.com")).thenReturn("jwt");

		LoginRequestDTO req = LoginRequestDTO.builder()
			.username("john@example.com")
			.password("password")
			.build();
		AuthResponseDTO res = authService.login(req);

		assertNotNull(res);
		assertEquals("john@example.com", res.username());
		assertEquals(activeUser.getId(), res.userId());
		verify(jwtTokenProvider).generateToken("john@example.com");
	}

	@Test
	@DisplayName("login should fail when user not found")
	void login_userNotFound() {
		when(userRepository.findActiveByUsername(anyString())).thenReturn(Optional.empty());
		LoginRequestDTO req = LoginRequestDTO.builder()
			.username("john@example.com")
			.password("password")
			.build();
		assertThrows(AuthenticationException.class, () -> authService.login(req));
	}

	@Test
	@DisplayName("login should fail when password invalid")
	void login_invalidPassword() {
		when(userRepository.findActiveByUsername("john@example.com")).thenReturn(Optional.of(activeUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
		LoginRequestDTO req = LoginRequestDTO.builder()
			.username("john@example.com")
			.password("bad")
			.build();
		assertThrows(AuthenticationException.class, () -> authService.login(req));
	}

	@Test
	@DisplayName("login should fail when user inactive")
	void login_inactiveUser() {
		activeUser.setStatus(UserEntity.UserStatus.INACTIVE);
		when(userRepository.findActiveByUsername("john@example.com")).thenReturn(Optional.of(activeUser));
		when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
		LoginRequestDTO req = LoginRequestDTO.builder()
			.username("john@example.com")
			.password("password")
			.build();
		assertThrows(AuthenticationException.class, () -> authService.login(req));
	}

	@Test
	@DisplayName("register should create user and return token")
	void register_success() {
		RegisterRequestDTO req = new RegisterRequestDTO("new@example.com", "pass");
		when(userRepository.existsByUsername("new@example.com")).thenReturn(false);
		when(passwordEncoder.encode("pass")).thenReturn("encoded");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
			UserEntity u = inv.getArgument(0);
			u.setId(UUID.randomUUID());
			return u;
		});
		when(jwtTokenProvider.generateToken("new@example.com")).thenReturn("jwt");

		AuthResponseDTO res = authService.register(req);
		assertNotNull(res);
		assertEquals("new@example.com", res.username());
		verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	@DisplayName("register should fail when username exists")
	void register_exists() {
		RegisterRequestDTO req = RegisterRequestDTO.builder()
			.username("exists@example.com")
			.password("pass")
			.build();
		when(userRepository.existsByUsername("exists@example.com")).thenReturn(true);
		assertThrows(UserAlreadyExistsException.class, () -> authService.register(req));
	}
}
