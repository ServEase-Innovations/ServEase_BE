package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ServiceProviderDTO;
import com.cus.customertab.entity.ServiceProvider;


@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);
}