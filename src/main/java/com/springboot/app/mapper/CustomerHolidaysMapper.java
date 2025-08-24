package com.springboot.app.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.CustomerHolidaysDTO;
import com.springboot.app.entity.CustomerHolidays;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProviderEngagement;

@Mapper(componentModel = "spring")
public interface CustomerHolidaysMapper {

    CustomerHolidaysMapper INSTANCE = Mappers.getMapper(CustomerHolidaysMapper.class);

    // Map Entity -> DTO
    @Mapping(source = "engagement.id", target = "engagementId") // Mandatory engagement
    @Mapping(source = "customer.customerId", target = "customerId") // Optional customer
    CustomerHolidaysDTO customerHolidaysToDTO(CustomerHolidays customerHolidays);

    // Map DTO -> Entity
    @Mapping(target = "engagement", source = "engagementId", qualifiedByName = "mapEngagement") // Mandatory
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "mapCustomer") // Optional
    CustomerHolidays dtoToCustomerHolidays(CustomerHolidaysDTO customerHolidaysDTO);

    // Custom mapping for mandatory engagement
    @Named("mapEngagement")
    default ServiceProviderEngagement mapEngagement(Long engagementId) {
        if (engagementId == null) {
            throw new IllegalArgumentException("EngagementId cannot be null");
        }
        ServiceProviderEngagement engagement = new ServiceProviderEngagement();
        engagement.setId(engagementId);
        return engagement;
    }

    // Custom mapping for optional customer
    @Named("mapCustomer")
    default Customer mapCustomer(Long customerId) {
        if (customerId == null) {
            return null; // Optional
        }
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        return customer;
    }
}
