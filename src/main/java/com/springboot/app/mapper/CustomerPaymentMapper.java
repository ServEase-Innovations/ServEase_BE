package com.springboot.app.mapper;

import org.mapstruct.*;

import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.entity.CustomerPayment;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProviderEngagement;

@Mapper(componentModel = "spring")
public interface CustomerPaymentMapper {

    // Map entity -> DTO
    @Mapping(source = "engagement.id", target = "engagementId") // Mandatory engagement
    @Mapping(source = "customer.customerId", target = "customerId") // Optional customer
    CustomerPaymentDTO customerPaymentToDTO(CustomerPayment customerPayment);

    // Map DTO -> entity
    @Mapping(target = "engagement", source = "engagementId", qualifiedByName = "mapEngagement") // mandatory
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "mapCustomer") // optional
    CustomerPayment dtoToCustomerPayment(CustomerPaymentDTO customerPaymentDTO);

    // Custom mapping for engagement
    @Named("mapEngagement")
    default ServiceProviderEngagement mapEngagement(Long engagementId) {
        if (engagementId == null) {
            throw new IllegalArgumentException("EngagementId cannot be null"); // Mandatory
        }
        ServiceProviderEngagement engagement = new ServiceProviderEngagement();
        engagement.setId(engagementId);
        return engagement;
    }

    // Custom mapping for optional customer
    @Named("mapCustomer")
    default Customer mapCustomer(Long customerId) {
        if (customerId == null) {
            return null; // optional
        }
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        return customer;
    }
}
