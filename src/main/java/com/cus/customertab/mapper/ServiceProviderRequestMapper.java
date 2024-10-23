package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ServiceProviderRequestDTO;
import com.cus.customertab.entity.ServiceProviderRequest;

@Mapper(componentModel = "spring", uses = ServiceProviderRequestCommentMapper.class)
public interface ServiceProviderRequestMapper {

    ServiceProviderRequestMapper INSTANCE = Mappers.getMapper(ServiceProviderRequestMapper.class);

    ServiceProviderRequestDTO serviceProviderRequestToDTO(ServiceProviderRequest serviceProviderRequest);

    ServiceProviderRequest dtoToServiceProviderRequest(ServiceProviderRequestDTO serviceProviderRequestDTO);
}