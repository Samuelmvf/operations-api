package br.com.astro.operations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.astro.operations.domain.dto.request.OperationRequestDTO;
import br.com.astro.operations.domain.dto.response.OperationDTO;
import br.com.astro.operations.domain.dto.response.OperationResponseDTO;
import br.com.astro.operations.domain.entity.OperationEntity;
import br.com.astro.operations.domain.entity.RecordEntity;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.InsufficientBalanceException;
import br.com.astro.operations.exception.OperationNotFoundException;
import br.com.astro.operations.exception.UserNotFoundException;
import br.com.astro.operations.mapper.OperationMapper;
import br.com.astro.operations.repository.OperationRepository;
import br.com.astro.operations.repository.RecordRepository;
import br.com.astro.operations.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

	@Mock private OperationRepository operationRepository;
	@Mock private UserRepository userRepository;
	@Mock private RecordRepository recordRepository;
	@Mock private RandomStringService randomStringService;
	@Mock private OperationMapper operationMapper;

	@InjectMocks private OperationService operationService;

	private OperationEntity opAdd;
	private UserEntity user;
	private UUID userId;

	@BeforeEach
	void setup() {
		userId = UUID.randomUUID();
		opAdd = OperationEntity.builder().type(OperationEntity.OperationType.ADDITION).cost(BigDecimal.ONE).build();
		user = UserEntity.builder().id(userId).username("u").password("p").balance(BigDecimal.TEN).status(UserEntity.UserStatus.ACTIVE).deleted(false).build();
	}

	@Test
	@DisplayName("getAllOperations returns mapped list")
	void getAllOperations_ok() {
		when(operationRepository.findAll()).thenReturn(List.of(opAdd));
		OperationDTO dto = OperationDTO.builder()
			.type(opAdd.getType())
			.cost(opAdd.getCost())
			.build();

		when(operationMapper.toDTO(opAdd)).thenReturn(dto);
        
		List<OperationDTO> list = operationService.getAllOperations();
		
		assertEquals(1, list.size());
		assertEquals("ADDITION", list.get(0).type().name());
	}

	@Test
	@DisplayName("performOperation fails when operation not found")
	void perform_op_notFound() {
		when(operationRepository.findByType(any())).thenReturn(Optional.empty());
		assertThrows(
            OperationNotFoundException.class,
            () -> operationService.performOperation(
                OperationEntity.OperationType.ADDITION,
                OperationRequestDTO.builder()
                    .operand1(null)
                    .operand2(null)
                    .length(1)
                    .build(),
                userId
            )
        );
	}

	@Test
	@DisplayName("performOperation fails when user not found")
	void perform_user_notFound() {
		when(operationRepository.findByType(any())).thenReturn(Optional.of(opAdd));
		when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.empty());
		assertThrows(
            UserNotFoundException.class,
            () -> operationService.performOperation(
                OperationEntity.OperationType.ADDITION,
                OperationRequestDTO.builder()
                    .operand1(BigDecimal.ONE)
                    .operand2(BigDecimal.ONE)
                    .length(1)
                    .build(),
                userId
            )
        );
	}

	@Test
	@DisplayName("performOperation fails on insufficient balance")
	void perform_insufficientBalance() {
		when(operationRepository.findByType(any())).thenReturn(Optional.of(opAdd));
		user.setBalance(BigDecimal.ZERO);
		when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.of(user));
		assertThrows(
            InsufficientBalanceException.class,
            () -> operationService.performOperation(
                OperationEntity.OperationType.ADDITION,
                OperationRequestDTO.builder()
                    .operand1(BigDecimal.ONE)
                    .operand2(BigDecimal.ONE)
                    .length(1)
                    .build(),
                userId
            )
        );
	}

	@Test
	@DisplayName("performOperation addition succeeds")
	void perform_addition_ok() {
		when(operationRepository.findByType(OperationEntity.OperationType.ADDITION)).thenReturn(Optional.of(opAdd));
		when(userRepository.findByIdForUpdate(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(any())).thenReturn(user);
		when(recordRepository.save(any())).thenReturn(RecordEntity.builder().id(UUID.randomUUID()).build());
		
		OperationResponseDTO res = operationService.performOperation(
            OperationEntity.OperationType.ADDITION, 
            OperationRequestDTO.builder()
                .operand1(BigDecimal.ONE)
                .operand2(BigDecimal.ONE)
                .length(1).build(),
            userId
        );
		assertNotNull(res);
		assertEquals(opAdd.getCost(), res.cost());
		verify(userRepository).save(any());
		verify(recordRepository).save(any());
	}
}
