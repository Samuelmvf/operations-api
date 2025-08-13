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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/records")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Records", description = "User operation records management")
@SecurityRequirement(name = "Bearer Authentication")
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "Get user records", description = "Retrieve paginated list of user operation records with optional search")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Records retrieved successfully",
                content = @Content(schema = @Schema(implementation = RecordDTO.class)))
    })
    @GetMapping
    public ResponseEntity<Page<RecordDTO>> getUserRecords(
        @CurrentUser UUID userId,
        @Parameter(description = "Search term for filtering records") @RequestParam(required = false) String search,
        @Parameter(description = "Pagination parameters") @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<RecordDTO> records = recordService.getUserRecords(userId, search, pageable);
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "Get record by ID", description = "Retrieve a specific record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Record retrieved successfully",
                content = @Content(schema = @Schema(implementation = RecordDTO.class))),
        @ApiResponse(responseCode = "404", description = "Record not found", content = @Content)
    })
    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDTO> getRecordById(
        @Parameter(description = "Record ID") @PathVariable UUID recordId,
        @CurrentUser UUID userId
    ) {
        RecordDTO record = recordService.getRecordById(recordId, userId);
        return ResponseEntity.ok(record);
    }

    @Operation(summary = "Delete record", description = "Soft delete a record (mark as deleted)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Record deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
        @Parameter(description = "Record ID") @PathVariable UUID recordId,
        @CurrentUser UUID userId
    ) {
        recordService.deleteRecord(recordId, userId);
        return ResponseEntity.noContent().build();
    }
}
