package br.com.astro.operations.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.astro.operations.domain.entity.UserEntity;
import jakarta.persistence.LockModeType;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.deleted = false")
    Optional<UserEntity> findActiveByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserEntity u WHERE u.id = :userId AND u.deleted = false")
    Optional<UserEntity> findByIdForUpdate(@Param("userId") UUID userId);
}
