package br.com.astro.operations.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.astro.operations.domain.entity.RecordEntity;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, UUID> {
}
