package br.com.astro.operations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import br.com.astro.operations.domain.dto.response.OperationDTO;
import br.com.astro.operations.domain.entity.OperationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationMapper {

    OperationDTO toDTO(OperationEntity operation);
}
