package br.com.astro.operations.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.astro.operations.domain.entity.UserEntity;
import br.com.astro.operations.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultUsersLoader implements ApplicationRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.users.default.username:admin}")
    private String defaultUsername;
    
    @Value("${app.users.default.password:admin}")
    private String defaultPassword;
    
    @Value("${app.users.default.balance:250.00}")
    private BigDecimal defaultBalance;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultUsers();
    }
    
    private void createDefaultUsers() {
        createUserIfNotExists(defaultUsername, defaultPassword, defaultBalance);
    }
    
    private void createUserIfNotExists(String username, String password, BigDecimal balance) {
        if (userRepository.findActiveByUsername(username).isEmpty()) {
            UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .status(UserEntity.UserStatus.ACTIVE)
                .balance(balance)
                .deleted(false)
                .build();
            
            userRepository.save(user);
        }
    }
}