package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.UserCredentialsDTO;
import com.springboot.app.entity.UserCredentials;
import com.springboot.app.enums.UserRole;

@Mapper(componentModel = "spring")
public interface UserCredentialsMapper {

    UserCredentialsMapper INSTANCE = Mappers.getMapper(UserCredentialsMapper.class);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "isTempLocked", source = "tempLocked")
    @Mapping(target = "role", source = "role")
    UserCredentialsDTO userCredentialsToDTO(UserCredentials userCredentials);

    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "tempLocked", source = "isTempLocked")
    @Mapping(target = "role", source = "role")
    UserCredentials dtoToUserCredentials(UserCredentialsDTO userCredentialsDTO);

    @Mapping(target = "password", ignore = true) // Ignore password updates
    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "tempLocked", source = "isTempLocked")
    @Mapping(target = "role", source = "role")
    void updateEntityFromDTO(UserCredentialsDTO dto, @MappingTarget UserCredentials entity);

    // Custom mapping method: UserRole to int
    default int mapRoleToInt(UserRole role) {
        return role != null ? role.getValue() : 0; // default 0
    }

    // Custom mapping method: int to UserRole
    default UserRole mapIntToRole(int value) {
        return UserRole.fromValue(value);
    }
}
