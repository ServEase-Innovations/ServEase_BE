package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.UserCredentials;

@Mapper(componentModel = "spring")
public interface UserCredentialsMapper {

    UserCredentialsMapper INSTANCE = Mappers.getMapper(UserCredentialsMapper.class);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "isTempLocked", source = "tempLocked")
    UserCredentialsDTO userCredentialsToDTO(UserCredentials userCredentials);

    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "tempLocked", source = "isTempLocked")
    UserCredentials dtoToUserCredentials(UserCredentialsDTO userCredentialsDTO);
}
