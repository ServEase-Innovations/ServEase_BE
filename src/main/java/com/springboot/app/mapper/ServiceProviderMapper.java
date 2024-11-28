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

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

    // Method to update an existing ServiceProvider entity from a DTO
    void updateServiceProviderFromDTO(ServiceProviderDTO serviceProviderDTO,
            @MappingTarget ServiceProvider existingServiceProvider);
}
