package com.springboot.app.mapper;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
//import org.mapstruct.MappingTarget;
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
    ServiceProviderEngagementDTO serviceProviderEngagementToDTO(ServiceProviderEngagement serviceProviderEngagement);

    // Mapping from DTO to entity
    @Mapping(target = "serviceProvider", ignore = true) // Will be set manually
    @Mapping(target = "customer", ignore = true) // Will be set manually
    @Mapping(source = "responsibilities", target = "responsibilities", qualifiedByName = "responsibilitiesToEntity")
    ServiceProviderEngagement dtoToServiceProviderEngagement(ServiceProviderEngagementDTO dto);

    // Update an existing entity from DTO
    @Mapping(target = "serviceProvider", ignore = true) // Relationship handled manually
    @Mapping(target = "customer", ignore = true) // Relationship handled manually
    @Mapping(target = "responsibilities", ignore = true)
    void updateEntityFromDTO(ServiceProviderEngagementDTO dto, @MappingTarget ServiceProviderEngagement entity);

    // Convert responsibilities JSON string to List<Map<String, Object>> (DTO)
    @Named("responsibilitiesToDto")
    default List<Map<String, Object>> responsibilitiesToDto(String responsibilitiesJson) {
        try {
            if (responsibilitiesJson != null && !responsibilitiesJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responsibilitiesJson, new TypeReference<List<Map<String, Object>>>() {
                });
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert responsibilities JSON to List<Map<String, Object>>", e);
        }
    }

    // Convert List<Map<String, Object>> (DTO) to responsibilities JSON string
    // (Entity)
    @Named("responsibilitiesToEntity")
    default String responsibilitiesToEntity(List<Map<String, Object>> responsibilities) {
        try {
            if (responsibilities != null && !responsibilities.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(responsibilities);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert responsibilities List<Map<String, Object>> to JSON", e);
        }
    }

    // // Convert responsibilities JSON string to List<Map<String, Object>> (DTO)
    // @org.mapstruct.Named("responsibilitiesToDto")
    // default List<Map<String, Object>> responsibilitiesToDto(String
    // responsibilitiesJson) {
    // try {
    // if (responsibilitiesJson != null && !responsibilitiesJson.isEmpty()) {
    // ObjectMapper objectMapper = new ObjectMapper();
    // return objectMapper.readValue(responsibilitiesJson, new
    // TypeReference<List<Map<String, Object>>>() {
    // });
    // }
    // return null;
    // } catch (IOException e) {
    // throw new RuntimeException("Failed to convert responsibilities JSON to
    // List<Map<String, Object>>", e);
    // }
    // }

    // // Convert List<Map<String, Object>> (DTO) to responsibilities JSON string
    // // (Entity)
    // @org.mapstruct.Named("responsibilitiesToEntity")
    // default String responsibilitiesToEntity(List<Map<String, Object>>
    // responsibilities) {
    // try {
    // if (responsibilities != null && !responsibilities.isEmpty()) {
    // ObjectMapper objectMapper = new ObjectMapper();
    // return objectMapper.writeValueAsString(responsibilities);
    // }
    // return null;
    // } catch (IOException e) {
    // throw new RuntimeException("Failed to convert responsibilities
    // List<Map<String, Object>> to JSON", e);
    // }
    // }
}

// @Mapper(componentModel = "spring")
// public interface ServiceProviderEngagementMapper {
// ServiceProviderEngagementMapper INSTANCE =
// Mappers.getMapper(ServiceProviderEngagementMapper.class);

// // Mapping from entity to DTO
// @Mapping(source = "serviceProvider.serviceproviderId", target =
// "serviceProviderId")
// @Mapping(source = "customer.customerId", target = "customerId")
// @Mapping(source = "responsibilities", target = "responsibilities",
// qualifiedByName = "responsibilitiesToDto")
// ServiceProviderEngagementDTO
// serviceProviderEngagementToDTO(ServiceProviderEngagement
// serviceProviderEngagement);

// // Mapping from DTO to entity
// @Mapping(target = "serviceProvider", ignore = true) // Will be set manually
// @Mapping(target = "customer", ignore = true) // Will be set manually
// @Mapping(target = "responsibilities", ignore = true)
// ServiceProviderEngagement
// dtoToServiceProviderEngagement(ServiceProviderEngagementDTO dto);

// // Update an existing entity from DTO
// @Mapping(target = "serviceProvider", ignore = true) // Relationship handled
// manually
// @Mapping(target = "customer", ignore = true) // Relationship handled manually
// @Mapping(target = "responsibilities", ignore = true)
// void updateEntityFromDTO(ServiceProviderEngagementDTO dto, @MappingTarget
// ServiceProviderEngagement entity);

// // Convert responsibilities JSON string to List<Map<String, Object>> (DTO)
// @org.mapstruct.Named("responsibilitiesToDto")
// default List<Map<String, Object>> responsibilitiesToDto(String
// responsibilitiesJson) {
// try {
// if (responsibilitiesJson != null && !responsibilitiesJson.isEmpty()) {
// ObjectMapper objectMapper = new ObjectMapper();
// return objectMapper.readValue(responsibilitiesJson, new
// TypeReference<List<Map<String, Object>>>() {
// });
// }
// return null;
// } catch (IOException e) {
// throw new RuntimeException("Failed to convert responsibilities JSON to
// List<Map<String, Object>>", e);
// }
// }

// // Convert List<Map<String, Object>> (DTO) to responsibilities JSON string
// // (Entity)
// @org.mapstruct.Named("responsibilitiesToEntity")
// default String responsibilitiesToEntity(List<Map<String, Object>>
// responsibilities) {
// try {
// if (responsibilities != null && !responsibilities.isEmpty()) {
// ObjectMapper objectMapper = new ObjectMapper();
// return objectMapper.writeValueAsString(responsibilities);
// }
// return null;
// } catch (IOException e) {
// throw new RuntimeException("Failed to convert responsibilities
// List<Map<String, Object>> to JSON", e);
// }
// }
// }
