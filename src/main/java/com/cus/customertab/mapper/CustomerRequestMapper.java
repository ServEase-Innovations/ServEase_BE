package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.cus.customertab.dto.CustomerRequestDTO;
import com.cus.customertab.entity.CustomerRequest;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {
    CustomerRequestMapper INSTANCE = Mappers.getMapper(CustomerRequestMapper.class);

    CustomerRequestDTO customerRequestToDTO(CustomerRequest customerRequest);
    CustomerRequest dtoToCustomerRequest(CustomerRequestDTO customerRequestDTO);

}
