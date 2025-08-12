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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/operations")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OperationController {

    private final OperationService operationService;

    @GetMapping
    public ResponseEntity<List<OperationDTO>> getAllOperations() {
        List<OperationDTO> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations);
    }

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
