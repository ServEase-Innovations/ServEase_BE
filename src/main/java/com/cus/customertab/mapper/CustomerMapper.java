package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Method to map Customer entity to CustomerDTO
    CustomerDTO customerToDTO(Customer customer);

    // Method to map CustomerDTO to Customer entity
    Customer dtoToCustomer(CustomerDTO customerDTO);
}
