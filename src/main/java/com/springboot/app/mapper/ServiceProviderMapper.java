package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    // Map ServiceProvider entity to ServiceProviderDTO
    // @Mapping(target = "profilePic", source = "profilePic")
    // @Mapping(target = "profilePicUrl", expression =
    // "java(mapToBase64(serviceProvider.getProfilePic()))")
    @Mapping(target = "username", ignore = true) // Explicitly ignore
    @Mapping(target = "password", ignore = true) // Explicitly ignore
    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    // Map ServiceProviderDTO to ServiceProvider entity
    // @Mapping(target = "profilePic", source = "profilePic")
    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

    void updateServiceProviderFromDTO(ServiceProviderDTO serviceProviderDTO,
            @MappingTarget ServiceProvider existingServiceProvider);

}
