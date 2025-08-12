package br.com.astro.operations.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.astro.operations.domain.dto.response.RecordDTO;
import br.com.astro.operations.security.CurrentUser;
import br.com.astro.operations.service.RecordService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/records")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<Page<RecordDTO>> getUserRecords(
        @CurrentUser UUID userId,
        @RequestParam(required = false) String search,
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<RecordDTO> records = recordService.getUserRecords(userId, search, pageable);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDTO> getRecordById(
        @PathVariable UUID recordId,
        @CurrentUser UUID userId
    ) {
        RecordDTO record = recordService.getRecordById(recordId, userId);
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
        @PathVariable UUID recordId,
        @CurrentUser UUID userId
    ) {
        recordService.deleteRecord(recordId, userId);
        return ResponseEntity.noContent().build();
    }
}
