package br.com.astro.operations.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.astro.operations.domain.dto.request.OperationRequestDTO;
import br.com.astro.operations.domain.dto.response.OperationDTO;
import br.com.astro.operations.domain.dto.response.OperationResponseDTO;
import br.com.astro.operations.domain.entity.OperationEntity;
import br.com.astro.operations.domain.entity.RecordEntity;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.exception.InsufficientBalanceException;
import br.com.astro.operations.exception.InvalidOperationException;
import br.com.astro.operations.exception.OperationNotFoundException;
import br.com.astro.operations.exception.UserNotFoundException;
import br.com.astro.operations.mapper.OperationMapper;
import br.com.astro.operations.repository.OperationRepository;
import br.com.astro.operations.repository.RecordRepository;
import br.com.astro.operations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OperationService {

    private final OperationRepository repository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final RandomStringService randomStringService;
    private final OperationMapper mapper;

    public List<OperationDTO> getAllOperations() {
        log.debug("Fetching all available operations");
        List<OperationDTO> operations = repository.findAll()
            .stream()
            .map(mapper::toDTO)
            .toList();
        log.debug("Found {} operations", operations.size());
        return operations;
    }

    @Transactional
    public OperationResponseDTO performOperation(
        OperationEntity.OperationType operationType,
        OperationRequestDTO request,
        UUID userId
    ) {
        log.debug("Starting operation {} for user {}", operationType, userId);
        
        OperationEntity operation = repository.findByType(operationType)
            .orElseThrow(() -> new OperationNotFoundException("Operation not found: " + operationType));

        UserEntity user = userRepository.findByIdForUpdate(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        log.debug("User {} current balance: {}, operation cost: {}", 
                 userId, user.getBalance(), operation.getCost());

        if (!user.hasEnoughBalance(operation.getCost())) {
            log.warn("Insufficient balance for user {}: balance={}, cost={}", 
                    userId, user.getBalance(), operation.getCost());
            throw new InsufficientBalanceException("Insufficient balance for this operation");
        }

        String result = executeOperation(operationType, request);
        
        user.deductBalance(operation.getCost());
        userRepository.save(user);

        log.debug("Balance deducted. User {} new balance: {}", userId, user.getBalance());

        RecordEntity record = RecordEntity.builder()
            .operation(operation)
            .user(user)
            .amount(operation.getCost())
            .userBalance(user.getBalance())
            .operationResponse(result)
            .build();
        
        record = recordRepository.save(record);

        log.info("Operation {} completed for user {}. Result: {}, New balance: {}", 
                operationType, userId, result, user.getBalance());

        return new OperationResponseDTO(
            result,
            operation.getCost(),
            user.getBalance(),
            record.getId()
        );
    }

    private String executeOperation(OperationEntity.OperationType type, OperationRequestDTO request) {
        return switch (type) {
            case ADDITION -> {
                validateBinaryOperation(request);
                yield request.operand1().add(request.operand2()).toString();
            }
            case SUBTRACTION -> {
                validateBinaryOperation(request);
                yield request.operand1().subtract(request.operand2()).toString();
            }
            case MULTIPLICATION -> {
                validateBinaryOperation(request);
                yield request.operand1().multiply(request.operand2()).toString();
            }
            case DIVISION -> {
                validateBinaryOperation(request);
                if (request.operand2().compareTo(BigDecimal.ZERO) == 0) {
                    throw new InvalidOperationException("Division by zero is not allowed");
                }
                yield request.operand1().divide(request.operand2(), 10, RoundingMode.HALF_UP).toString();
            }
            case SQUARE_ROOT -> {
                validateUnaryOperation(request);
                if (request.operand1().compareTo(BigDecimal.ZERO) < 0) {
                    throw new InvalidOperationException("Square root of negative number is not allowed");
                }
                yield BigDecimal.valueOf(Math.sqrt(request.operand1().doubleValue())).toString();
            }
            case RANDOM_STRING -> {
                validateRandomStringOperation(request);
                yield randomStringService.generateRandomString(request.length());
            }
        };
    }

    private void validateBinaryOperation(OperationRequestDTO request) {
        if (request.operand1() == null || request.operand2() == null) {
            throw new InvalidOperationException("Both operands are required for this operation");
        }
    }

    private void validateUnaryOperation(OperationRequestDTO request) {
        if (request.operand1() == null) {
            throw new InvalidOperationException("Operand is required for this operation");
        }
    }

    private void validateRandomStringOperation(OperationRequestDTO request) {
        if (request.length() == null || request.length() <= 0) {
            throw new InvalidOperationException("Valid length is required for random string generation");
        }
    }
}
