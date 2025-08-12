package br.com.astro.operations.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.astro.operations.domain.entity.OperationEntity;
import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.repository.OperationRepository;
import br.com.astro.operations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultUsersLoader implements ApplicationRunner {
    
    private final UserRepository userRepository;
    private final OperationRepository operationRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.users.default.username:admin}")
    private String defaultUsername;
    
    @Value("${app.users.default.password:admin}")
    private String defaultPassword;
    
    @Value("${app.users.default.balance:250.00}")
    private BigDecimal defaultBalance;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting default data creation...");
        createDefaultOperations();
        createDefaultUsers();
        log.info("Default data creation completed.");
    }
    
    @Transactional
    private void createDefaultOperations() {
        log.info("Creating default operations...");
        createOperationIfNotExists(OperationEntity.OperationType.ADDITION, new BigDecimal("1.00"));
        createOperationIfNotExists(OperationEntity.OperationType.SUBTRACTION, new BigDecimal("1.00"));
        createOperationIfNotExists(OperationEntity.OperationType.MULTIPLICATION, new BigDecimal("2.00"));
        createOperationIfNotExists(OperationEntity.OperationType.DIVISION, new BigDecimal("2.00"));
        createOperationIfNotExists(OperationEntity.OperationType.SQUARE_ROOT, new BigDecimal("3.00"));
        createOperationIfNotExists(OperationEntity.OperationType.RANDOM_STRING, new BigDecimal("5.00"));
        log.info("Default operations created successfully.");
    }

    @Transactional
    private void createDefaultUsers() {
        log.info("Creating default users...");
        createUserIfNotExists(defaultUsername, defaultPassword, defaultBalance);
        log.info("Default users created successfully.");
    }
    
    private void createUserIfNotExists(String username, String password, BigDecimal balance) {
        log.info("Checking if user '{}' exists...", username);
        
        if (userRepository.findActiveByUsername(username).isEmpty()) {
            log.info("User '{}' does not exist. Creating new user...", username);
            
            UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .status(UserEntity.UserStatus.ACTIVE)
                .balance(balance)
                .deleted(false)
                .build();
            
            log.info("Saving user '{}' to database...", username);
            userRepository.save(user);
            log.info("User '{}' created successfully.", username);
        } else {
            log.info("User '{}' already exists. Skipping creation.", username);
        }
    }
    
    private void createOperationIfNotExists(OperationEntity.OperationType type, BigDecimal cost) {
        if (operationRepository.findByType(type).isEmpty()) {
            log.info("Creating operation: {} with cost: {}", type, cost);
            
            OperationEntity operation = OperationEntity.builder()
                .type(type)
                .cost(cost)
                .build();
                
            operationRepository.save(operation);
            log.info("Operation {} created successfully.", type);
        } else {
            log.info("Operation {} already exists. Skipping creation.", type);
        }
    }
}