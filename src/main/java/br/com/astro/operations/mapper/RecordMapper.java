package br.com.astro.operations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import br.com.astro.operations.domain.dto.response.RecordDTO;
import br.com.astro.operations.domain.entity.RecordEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecordMapper {

    @Mapping(source = "operation.id", target = "operationId")
    @Mapping(source = "operation.type", target = "operationType")
    RecordDTO toDTO(RecordEntity record);
}
