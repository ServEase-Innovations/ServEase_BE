package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.CustomerDTO;
import com.springboot.app.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Method to map Customer entity to CustomerDTO
    CustomerDTO customerToDTO(Customer customer);

    // Method to map CustomerDTO to Customer entity
    Customer dtoToCustomer(CustomerDTO customerDTO);
}
