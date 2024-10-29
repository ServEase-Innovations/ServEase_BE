package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.ServiceProviderEngagement;;

@Mapper(componentModel = "spring")
public interface ServiceProviderEngagementMapper {
    ServiceProviderEngagementMapper INSTANCE = Mappers.getMapper(ServiceProviderEngagementMapper.class);

    // Mapping from entity to DTO
    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    ServiceProviderEngagementDTO serviceProviderEngagementToDTO(ServiceProviderEngagement serviceProviderEngagement);

    // Mapping from DTO to entity
    @Mapping(source = "serviceProviderId", target = "serviceProvider.serviceproviderId")
    @Mapping(source = "customerId", target = "customer.customerId")
    ServiceProviderEngagement dtoToServiceProviderEngagement(ServiceProviderEngagementDTO serviceProviderEngagementDTO);

}
