
package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.CustomerFeedbackDTO;
import com.springboot.app.entity.CustomerFeedback;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface CustomerFeedbackMapper {
    CustomerFeedbackMapper INSTANCE = Mappers.getMapper(CustomerFeedbackMapper.class);

    // Map from CustomerFeedback entity to CustomerFeedbackDTO
    @Mapping(target = "serviceProviderId", source = "serviceProvider.serviceproviderId")
    CustomerFeedbackDTO customerFeedbackToDTO(CustomerFeedback customerFeedback);

    // Map from CustomerFeedbackDTO to CustomerFeedback entity
    @Mapping(target = "serviceProvider.serviceproviderId", source = "serviceProviderId")
    CustomerFeedback dtoToCustomerFeedback(CustomerFeedbackDTO customerFeedbackDTO);

    // Default mapping method for converting Long to ServiceProvider
    default ServiceProvider mapIdToServiceProvider(Long id) {
        if (id == null) {
            return null;
        }
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setServiceproviderId(id);
        return serviceProvider;
    }
}