package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ServiceProviderPaymentDTO;
import com.cus.customertab.entity.ServiceProviderPayment;

@Mapper(componentModel = "spring")
public interface ServiceProviderPaymentMapper {
    ServiceProviderPaymentMapper INSTANCE = Mappers.getMapper(ServiceProviderPaymentMapper.class);

    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    ServiceProviderPaymentDTO serviceProviderPaymentToDTO(ServiceProviderPayment serviceProviderPayment);

    @Mapping(source = "serviceProviderId", target = "serviceProvider.serviceproviderId")
    @Mapping(source = "customerId", target = "customer.customerId")
    ServiceProviderPayment dtoToServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);
}