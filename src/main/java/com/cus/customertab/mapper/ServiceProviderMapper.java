package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ServiceProviderDTO;
import com.cus.customertab.entity.ServiceProvider;


@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

}