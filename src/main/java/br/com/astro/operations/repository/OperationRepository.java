package br.com.astro.operations.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.astro.operations.domain.entity.OperationEntity;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {
}
