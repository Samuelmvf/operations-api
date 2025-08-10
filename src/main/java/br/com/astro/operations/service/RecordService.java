package br.com.astro.operations.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.astro.operations.domain.dto.response.RecordDTO;
import br.com.astro.operations.domain.entity.RecordEntity;
import br.com.astro.operations.exception.RecordNotFoundException;
import br.com.astro.operations.mapper.RecordMapper;
import br.com.astro.operations.repository.RecordRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository repository;
    private final RecordMapper mapper;

    @Transactional(readOnly = true)
    public Page<RecordDTO> getUserRecords(UUID userId, String search, Pageable pageable) {
        Page<RecordEntity> records;
        
        if (search != null && !search.trim().isEmpty()) {
            records = repository.findByUserIdAndSearchWithOperation(userId, search.trim(), pageable);
        } else {
            records = repository.findByUserIdWithOperation(userId, pageable);
        }
        
        return records.map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RecordDTO getRecordById(UUID recordId, UUID userId) {
        RecordEntity record = repository.findById(recordId)
            .orElseThrow(() -> new RecordNotFoundException());

        if (!record.getUser().getId().equals(userId)) {
            throw new RecordNotFoundException();
        }

        return mapper.toDTO(record);
    }

    public void deleteRecord(UUID recordId, UUID userId) {
        RecordEntity record = repository.findById(recordId)
            .orElseThrow(() -> new RecordNotFoundException());

        if (!record.getUser().getId().equals(userId)) {
            throw new RecordNotFoundException();
        }

        repository.delete(record);
    }
}
