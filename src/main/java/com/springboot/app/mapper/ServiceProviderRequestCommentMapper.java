package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

import com.springboot.app.dto.ServiceProviderRequestCommentDTO;
import com.springboot.app.entity.ServiceProviderRequestComment;
import com.springboot.app.entity.ServiceProviderRequest;

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
