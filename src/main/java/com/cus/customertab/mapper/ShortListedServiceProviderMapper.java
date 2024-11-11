package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ShortListedServiceProviderDTO;
import com.cus.customertab.entity.ShortListedServiceProvider;

@Mapper(componentModel = "spring")
public interface ShortListedServiceProviderMapper {

    ShortListedServiceProviderMapper INSTANCE = Mappers.getMapper(ShortListedServiceProviderMapper.class);

    @Mapping(source = "customer.customerId", target = "customerId")
    ShortListedServiceProviderDTO shortListedServiceProviderToDTO(
            ShortListedServiceProvider shortListedServiceProvider);

    @Mapping(source = "customerId", target = "customer.customerId")
    ShortListedServiceProvider dtoToShortListedServiceProvider(
            ShortListedServiceProviderDTO shortListedServiceProviderDTO);
}