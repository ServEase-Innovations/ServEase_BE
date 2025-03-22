package com.springboot.app.mapper;

import org.mapstruct.*;

import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.entity.CustomerPayment;
import com.springboot.app.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerPaymentMapper {

    @Mapping(source = "customer.customerId", target = "customerId") // Ensure customerId is mapped
    CustomerPaymentDTO customerPaymentToDTO(CustomerPayment customerPayment);

    @Mapping(target = "customer", source = "customerId", qualifiedByName = "mapCustomer") // Custom mapping
    CustomerPayment dtoToCustomerPayment(CustomerPaymentDTO customerPaymentDTO);

    @Named("mapCustomer") // Ensure MapStruct recognizes the method
    default Customer mapCustomer(Long customerId) {
        if (customerId == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setCustomerId(customerId); // Ensure correct field is set
        return customer;
    }
}
