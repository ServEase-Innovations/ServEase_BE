package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.ServiceProviderEngagement;

@Mapper(componentModel = "spring")
public interface ServiceProviderEngagementMapper {
    ServiceProviderEngagementMapper INSTANCE = Mappers.getMapper(ServiceProviderEngagementMapper.class);

    // Mapping from entity to DTO
    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    ServiceProviderEngagementDTO serviceProviderEngagementToDTO(ServiceProviderEngagement serviceProviderEngagement);

    // Mapping from DTO to entity
    @Mapping(target = "serviceProvider", ignore = true) // Will be set manually
    @Mapping(target = "customer", ignore = true) // Will be set manually
    ServiceProviderEngagement dtoToServiceProviderEngagement(ServiceProviderEngagementDTO dto);

    // Update an existing entity from DTO
    @Mapping(target = "serviceProvider", ignore = true) // Relationship handled manually
    @Mapping(target = "customer", ignore = true) // Relationship handled manually
    void updateEntityFromDTO(ServiceProviderEngagementDTO dto, @MappingTarget ServiceProviderEngagement entity);
}
