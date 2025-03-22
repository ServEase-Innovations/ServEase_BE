package com.springboot.app.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.CustomerHolidaysDTO;
import com.springboot.app.entity.CustomerHolidays;
import com.springboot.app.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerHolidaysMapper {

    CustomerHolidaysMapper INSTANCE = Mappers.getMapper(CustomerHolidaysMapper.class);

    @Mapping(source = "customer.customerId", target = "customerId") // Map Customer entity's customerId to DTO
    CustomerHolidaysDTO customerHolidaysToDTO(CustomerHolidays customerHolidays);

    @Mapping(target = "customer", expression = "java(mapCustomer(customerHolidaysDTO.getCustomerId()))") // Custom
    CustomerHolidays dtoToCustomerHolidays(CustomerHolidaysDTO customerHolidaysDTO);

    default Customer mapCustomer(Long customerId) {
        if (customerId == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setCustomerId(customerId); // Use setCustomerId() instead of setId()
        return customer;
    }
}
