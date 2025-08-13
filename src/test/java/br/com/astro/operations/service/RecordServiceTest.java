package br.com.astro.operations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.astro.operations.domain.dto.response.RecordDTO;
import br.com.astro.operations.domain.entity.RecordEntity;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.RecordNotFoundException;
import br.com.astro.operations.mapper.RecordMapper;
import br.com.astro.operations.repository.RecordRepository;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

	@Mock private RecordRepository recordRepository;
	@Mock private RecordMapper recordMapper;
	@InjectMocks private RecordService recordService;

	@Test
	@DisplayName("getUserRecords returns page with mapping")
	void getUserRecords_ok() {
		UUID userId = UUID.randomUUID();
		RecordEntity e = new RecordEntity();
		Page<RecordEntity> page = new PageImpl<>(List.of(e));
		when(recordRepository.findByUserIdWithOperation(eq(userId), any())).thenReturn(page);
		when(recordMapper.toDTO(e)).thenReturn(RecordDTO.builder().build());

		Page<RecordDTO> result = recordService.getUserRecords(userId, null, PageRequest.of(0, 10));
		assertEquals(1, result.getTotalElements());
	}

	@Test
	@DisplayName("getRecordById throws when not found")
	void getRecordById_notFound() {
		UUID recordId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		when(recordRepository.findById(recordId)).thenReturn(Optional.empty());
		assertThrows(RecordNotFoundException.class, () -> recordService.getRecordById(recordId, userId));
	}

	@Test
	@DisplayName("getRecordById throws when not owned by user")
	void getRecordById_wrongOwner() {
		UUID recordId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		RecordEntity e = new RecordEntity();
		UserEntity u = new UserEntity();
		u.setId(UUID.randomUUID());
		e.setUser(u);
		when(recordRepository.findById(recordId)).thenReturn(Optional.of(e));
		assertThrows(RecordNotFoundException.class, () -> recordService.getRecordById(recordId, userId));
	}

	@Test
	@DisplayName("deleteRecord removes when owned")
	void deleteRecord_ok() {
		UUID recordId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		RecordEntity e = new RecordEntity();
		UserEntity u = new UserEntity();
		u.setId(userId);
		e.setUser(u);
		when(recordRepository.findById(recordId)).thenReturn(Optional.of(e));

		recordService.deleteRecord(recordId, userId);
		verify(recordRepository).delete(e);
	}
}
