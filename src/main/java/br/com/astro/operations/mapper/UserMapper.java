package br.com.astro.operations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import br.com.astro.operations.domain.dto.response.UserDTO;
import br.com.astro.operations.domain.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO toDTO(UserEntity user);
}
