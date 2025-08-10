package com.springboot.app.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.ServiceProviderEngagement;

@Mapper(componentModel = "spring")
public interface ServiceProviderEngagementMapper {
    ServiceProviderEngagementMapper INSTANCE = Mappers.getMapper(ServiceProviderEngagementMapper.class);

    // Mapping from entity to DTO
    @Mapping(source = "serviceProvider.serviceproviderId", target = "serviceProviderId")
    @Mapping(source = "customer.customerId", target = "customerId")
    @Mapping(source = "responsibilities", target = "responsibilities", qualifiedByName = "responsibilitiesToDto")
    @Mapping(target = "availableTimeSlots", ignore = true)
    @Mapping(source = "taskStatus", target = "taskStatus")
    ServiceProviderEngagementDTO serviceProviderEngagementToDTO(ServiceProviderEngagement serviceProviderEngagement);

    // Mapping from DTO to entity
    @Mapping(target = "serviceProvider", ignore = true) // Will be set manually
    @Mapping(target = "customer", ignore = true) // Will be set manually
    @Mapping(source = "responsibilities", target = "responsibilities", qualifiedByName = "responsibilitiesToEntity")
    ServiceProviderEngagement dtoToServiceProviderEngagement(ServiceProviderEngagementDTO dto);

    // Update an existing entity from DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "serviceProvider", ignore = true) // Relationship handled manually
    @Mapping(target = "customer", ignore = true) // Relationship handled manually
    @Mapping(target = "responsibilities", ignore = true)
    @Mapping(target = "monthlyAmount", ignore = true)
    void updateEntityFromDTO(ServiceProviderEngagementDTO dto, @MappingTarget ServiceProviderEngagement entity);

    // Create custom exception
    public class ResponsibilitiesConversionException extends RuntimeException {
        public ResponsibilitiesConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Update methods to throw the custom exception
    @Named("responsibilitiesToDto")
    default List<Map<String, Object>> responsibilitiesToDto(String responsibilitiesJson) {
        try {
            if (responsibilitiesJson != null && !responsibilitiesJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responsibilitiesJson, new TypeReference<List<Map<String, Object>>>() {
                });
            }
            return new ArrayList<>();
        } catch (IOException e) {
            throw new ResponsibilitiesConversionException(
                    "Failed to convert responsibilities JSON to List<Map<String, Object>>", e);
        }
    }

    @Named("responsibilitiesToEntity")
    default String responsibilitiesToEntity(List<Map<String, Object>> responsibilities) {
        try {
            if (responsibilities != null && !responsibilities.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(responsibilities);
            }
            return null;
        } catch (IOException e) {
            throw new ResponsibilitiesConversionException(
                    "Failed to convert responsibilities List<Map<String, Object>> to JSON", e);
        }
    }

}
