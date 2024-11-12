package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

}
