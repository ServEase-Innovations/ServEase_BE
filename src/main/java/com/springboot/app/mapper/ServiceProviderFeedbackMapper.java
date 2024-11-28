// package com.springboot.app.mapper;

// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.MappingTarget;
// import org.mapstruct.Named;
// import org.mapstruct.factory.Mappers;
// import com.springboot.app.dto.ServiceProviderFeedbackDTO;
// import com.springboot.app.entity.Customer;
// import com.springboot.app.entity.ServiceProvider;
// import com.springboot.app.entity.ServiceProviderFeedback;

// @Mapper(componentModel = "spring")
// public interface ServiceProviderFeedbackMapper {

// ServiceProviderFeedbackMapper INSTANCE =
// Mappers.getMapper(ServiceProviderFeedbackMapper.class);

// // Map from ServiceProviderFeedback entity to ServiceProviderFeedbackDTO
// @Mapping(target = "customerId", source = "customer.customerId")
// @Mapping(target = "serviceproviderId", source =
// "serviceproviderId.serviceproviderId")
// ServiceProviderFeedbackDTO feedbackToDTO(ServiceProviderFeedback feedback);

// // Map from ServiceProviderFeedbackDTO to ServiceProviderFeedback entity
// @Mapping(target = "customer", source = "customerId", qualifiedByName =
// "mapIdToCustomer")
// @Mapping(target = "serviceproviderId", source = "serviceproviderId",
// qualifiedByName = "mapIdToServiceProvider")
// ServiceProviderFeedback dtoToFeedback(ServiceProviderFeedbackDTO
// feedbackDTO);

// // Update entity fields using values from DTO
// @Mapping(target = "customer", source = "customerId", qualifiedByName =
// "mapIdToCustomer")
// @Mapping(target = "serviceproviderId", source = "serviceproviderId",
// qualifiedByName = "mapIdToServiceProvider")
// void updateEntityFromDTO(ServiceProviderFeedbackDTO feedbackDTO,
// @MappingTarget ServiceProviderFeedback entity);

// // Helper method to map Long to Customer
// @Named("mapIdToCustomer")
// default Customer mapIdToCustomer(Long id) {
// if (id == null) {
// return null;
// }
// Customer customer = new Customer();
// customer.setCustomerId(id);
// return customer;
// }

// // Helper method to map Long to ServiceProvider
// @Named("mapIdToServiceProvider")
// default ServiceProvider mapIdToServiceProvider(Long id) {
// if (id == null) {
// return null;
// }
// ServiceProvider serviceProvider = new ServiceProvider();
// serviceProvider.setServiceproviderId(id);
// return serviceProvider;
// }
// }
