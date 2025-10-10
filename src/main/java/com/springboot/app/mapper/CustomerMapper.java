package com.springboot.app.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.CustomerDTO;
import com.springboot.app.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Map Customer entity to CustomerDTO
    // @Mapping(target = "profilePic", source = "profilePic")
    // @Mapping(target = "profilePicUrl", expression =
    // "java(mapToBase64(customer.getProfilePic()))")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    CustomerDTO customerToDTO(Customer customer);

    // Method to map CustomerDTO to Customer entity, ignoring username and password
    @Mapping(target = "profilePic", source = "profilePic")
    Customer dtoToCustomer(CustomerDTO customerDTO);

    // ✅ Add this for partial updates
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromDto(CustomerDTO dto, @MappingTarget Customer entity);

}
