package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderPaymentDTO;
import com.springboot.app.entity.ServiceProviderPayment;

@Mapper(componentModel = "spring")
public interface ServiceProviderPaymentMapper {
    ServiceProviderPaymentMapper INSTANCE = Mappers.getMapper(ServiceProviderPaymentMapper.class);

    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    ServiceProviderPaymentDTO serviceProviderPaymentToDTO(ServiceProviderPayment serviceProviderPayment);

    @Mapping(source = "serviceProviderId", target = "serviceProvider.serviceproviderId")
    @Mapping(source = "customerId", target = "customer.customerId")
    ServiceProviderPayment dtoToServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);

    @Mapping(source = "customerId", target = "customer.customerId")
    @Mapping(source = "serviceProviderId", target = "serviceProvider.serviceproviderId")
    void updateServiceProviderPaymentFromDTO(ServiceProviderPaymentDTO serviceProviderPaymentDTO,
            @MappingTarget ServiceProviderPayment existingPayment);
}
