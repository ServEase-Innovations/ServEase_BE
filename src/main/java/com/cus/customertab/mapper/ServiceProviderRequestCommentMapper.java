package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.cus.customertab.dto.ServiceProviderRequestCommentDTO;
import com.cus.customertab.entity.ServiceProviderRequest;
import com.cus.customertab.entity.ServiceProviderRequestComment;

import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface ServiceProviderRequestCommentMapper {
    ServiceProviderRequestCommentMapper INSTANCE = Mappers.getMapper(ServiceProviderRequestCommentMapper.class);

    @Mapping(source = "serviceProviderRequest.requestId", target = "requestId")
    ServiceProviderRequestCommentDTO toDTO(ServiceProviderRequestComment serviceProviderRequestComment);

    @Mapping(source = "requestId", target = "serviceProviderRequest.requestId")
    ServiceProviderRequestComment toEntity(ServiceProviderRequestCommentDTO serviceProviderRequestCommentDTO);

    default Long map(ServiceProviderRequest serviceProviderRequest) {
        return serviceProviderRequest != null ? serviceProviderRequest.getRequestId() : null;
    }

}