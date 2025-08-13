package br.com.astro.operations.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecordService {

    private final RecordRepository repository;
    private final RecordMapper mapper;

    @Transactional(readOnly = true)
    public Page<RecordDTO> getUserRecords(UUID userId, String search, Pageable pageable) {
        log.debug("Fetching records for userId={}, search='{}'", userId, search);
        Page<RecordEntity> records;

        if (search != null && !search.trim().isEmpty()) {
            records = repository.findByUserIdAndSearchWithOperation(userId, search.trim(), pageable);
            log.debug("Search applied. Found {} records", records.getTotalElements());
        } else {
            records = repository.findByUserIdWithOperation(userId, pageable);
            log.debug("No search. Found {} records", records.getTotalElements());
        }

        return records.map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RecordDTO getRecordById(UUID recordId, UUID userId) {
        log.debug("Fetching record by id={}, userId={}", recordId, userId);
        RecordEntity record = repository.findById(recordId)
            .orElseThrow(() -> {
                log.warn("Record not found: {}", recordId);
                return new RecordNotFoundException();
            });

        if (!record.getUser().getId().equals(userId)) {
            log.warn("Record {} does not belong to user {}", recordId, userId);
            throw new RecordNotFoundException();
        }

        log.debug("Record {} retrieved successfully", recordId);
        return mapper.toDTO(record);
    }

    public void deleteRecord(UUID recordId, UUID userId) {
        log.info("Delete record request id={}, userId={}", recordId, userId);
        RecordEntity record = repository.findById(recordId)
            .orElseThrow(() -> {
                log.warn("Record not found for deletion: {}", recordId);
                return new RecordNotFoundException();
            });

        if (!record.getUser().getId().equals(userId)) {
            log.warn("Attempt to delete record {} not owned by user {}", recordId, userId);
            throw new RecordNotFoundException();
        }

        repository.delete(record);
        log.info("Record {} deleted by user {}", recordId, userId);
    }
}