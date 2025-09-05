package br.com.astro.operations.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.astro.operations.domain.entity.RecordEntity;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, UUID> {

    @Query("""
        SELECT r FROM RecordEntity r 
        JOIN FETCH r.operation 
        WHERE r.user.id = :userId 
        AND r.deleted = false
        """)
    Page<RecordEntity> findByUserIdWithOperation(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        SELECT r FROM RecordEntity r 
        JOIN FETCH r.operation 
        WHERE r.user.id = :userId 
        AND r.deleted = false 
        AND (r.operationResponse LIKE CONCAT('%', :search, '%') OR CAST(r.operation.type AS string) LIKE CONCAT('%', :search, '%'))
        """)
    Page<RecordEntity> findByUserIdAndSearchWithOperation(
        @Param("userId") UUID userId, 
        @Param("search") String search, 
        Pageable pageable
    );
}
