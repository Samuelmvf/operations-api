package br.com.astro.operations.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.astro.operations.domain.dto.request.OperationRequestDTO;
import br.com.astro.operations.domain.dto.response.OperationDTO;
import br.com.astro.operations.domain.dto.response.OperationResponseDTO;
import br.com.astro.operations.domain.entity.OperationEntity;
import br.com.astro.operations.security.CurrentUser;
import br.com.astro.operations.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/operations")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Operations", description = "Mathematical operations endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class OperationController {

    private final OperationService operationService;

    @Operation(summary = "Get all operations", description = "Retrieve list of all available mathematical operations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operations retrieved successfully",
                content = @Content(schema = @Schema(implementation = OperationDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<OperationDTO>> getAllOperations() {
        List<OperationDTO> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations);
    }

    @Operation(summary = "Perform addition", description = "Add two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Addition performed successfully",
                content = @Content(schema = @Schema(implementation = OperationResponseDTO.class))),
        @ApiResponse(responseCode = "402", description = "Insufficient balance", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/addition")
    public ResponseEntity<OperationResponseDTO> addition(
        @Valid @RequestBody OperationRequestDTO request,
        @CurrentUser UUID userId
    ) {
        OperationResponseDTO response = operationService.performOperation(
            OperationEntity.OperationType.ADDITION, request, userId
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Perform subtraction", description = "Subtract two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subtraction performed successfully"),
        @ApiResponse(responseCode = "402", description = "Insufficient balance"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/subtraction")
    public ResponseEntity<OperationResponseDTO> subtraction(
        @Valid @RequestBody OperationRequestDTO request,
        @CurrentUser UUID userId
    ) {
        OperationResponseDTO response = operationService.performOperation(
            OperationEntity.OperationType.SUBTRACTION, request, userId
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Perform multiplication", description = "Multiply two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Multiplication performed successfully"),
        @ApiResponse(responseCode = "402", description = "Insufficient balance"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/multiplication")
    public ResponseEntity<OperationResponseDTO> multiplication(
        @Valid @RequestBody OperationRequestDTO request,
        @CurrentUser UUID userId
    ) {
        OperationResponseDTO response = operationService.performOperation(
            OperationEntity.OperationType.MULTIPLICATION, request, userId
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Perform division", description = "Divide two numbers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Division performed successfully"),
        @ApiResponse(responseCode = "402", description = "Insufficient balance"),
        @ApiResponse(responseCode = "400", description = "Invalid input or division by zero")
    })
    @PostMapping("/division")
    public ResponseEntity<OperationResponseDTO> division(
        @Valid @RequestBody OperationRequestDTO request,
        @CurrentUser UUID userId
    ) {
        OperationResponseDTO response = operationService.performOperation(
            OperationEntity.OperationType.DIVISION, request, userId
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Calculate square root", description = "Calculate square root of a number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Square root calculated successfully"),
        @ApiResponse(responseCode = "402", description = "Insufficient balance"),
        @ApiResponse(responseCode = "400", description = "Invalid input or negative number")
    })
    @PostMapping("/square-root")
    public ResponseEntity<OperationResponseDTO> squareRoot(
        @Valid @RequestBody OperationRequestDTO request,
        @CurrentUser UUID userId
    ) {
        OperationResponseDTO response = operationService.performOperation(
            OperationEntity.OperationType.SQUARE_ROOT, request, userId
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generate random string", description = "Generate a random string")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Random string generated successfully"),
        @ApiResponse(responseCode = "402", description = "Insufficient balance"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/random-string")
    public ResponseEntity<OperationResponseDTO> randomString(
        @Valid @RequestBody OperationRequestDTO request,
        @CurrentUser UUID userId
    ) {
        OperationResponseDTO response = operationService.performOperation(
            OperationEntity.OperationType.RANDOM_STRING, request, userId
        );
        return ResponseEntity.ok(response);
    }
}
