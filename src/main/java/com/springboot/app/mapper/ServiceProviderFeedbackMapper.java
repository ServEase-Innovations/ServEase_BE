package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.entity.ServiceProviderFeedback;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderFeedbackMapper {
    ServiceProviderFeedbackMapper INSTANCE = Mappers.getMapper(ServiceProviderFeedbackMapper.class);

    // Map from ServiceProviderFeedback entity to ServiceProviderFeedbackDTO
    @Mapping(target = "customerId", source = "customer.customerId") // Adjust to use 'customerId'
    @Mapping(target = "serviceproviderId", source = "serviceprovider.serviceproviderId")
    ServiceProviderFeedbackDTO serviceProviderFeedbackToDTO(ServiceProviderFeedback serviceProviderFeedback);

    // Map from ServiceProviderFeedbackDTO to ServiceProviderFeedback entity
    @Mapping(target = "customer.customerId", source = "customerId") // Adjust to use 'customerId'
    @Mapping(target = "serviceprovider.serviceproviderId", source = "serviceproviderId")
    ServiceProviderFeedback dtoToServiceProviderFeedback(ServiceProviderFeedbackDTO serviceProviderFeedbackDTO);

    // Default mapping method for converting Long to Customer
    default Customer mapIdToCustomer(Long id) {
        if (id == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setCustomerId(id);
        return customer;
    }

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
