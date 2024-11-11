package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.cus.customertab.dto.ServiceProviderEngagementDTO;
import com.cus.customertab.entity.ServiceProviderEngagement;

@Mapper(componentModel = "spring")
public interface ServiceProviderEngagementMapper {
    ServiceProviderEngagementMapper INSTANCE = Mappers.getMapper(ServiceProviderEngagementMapper.class);

    // Mapping from entity to DTO
    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceproviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    ServiceProviderEngagementDTO serviceProviderEngagementToDTO(ServiceProviderEngagement serviceProviderEngagement);

    // Mapping from DTO to entity
    @Mapping(source = "serviceproviderId", target = "serviceProvider.serviceproviderId")
    @Mapping(source = "customerId", target = "customer.customerId")
    ServiceProviderEngagement dtoToServiceProviderEngagement(ServiceProviderEngagementDTO serviceProviderEngagementDTO);

}
