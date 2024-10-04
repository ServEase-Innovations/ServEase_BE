package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.entity.ServiceProviderFeedback;

@Mapper(componentModel = "spring")
public interface ServiceProviderFeedbackMapper {

    ServiceProviderFeedbackMapper INSTANCE = Mappers.getMapper(ServiceProviderFeedbackMapper.class);

    ServiceProviderFeedbackDTO toDTO(ServiceProviderFeedback serviceProviderFeedback);

    ServiceProviderFeedback toEntity(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO);
}
