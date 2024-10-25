package com.springboot.app.mapper;

/*import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    // Map ServiceProvider entity to ServiceProviderDTO
    @Mapping(target = "profilePic", source = "profilePic")
    @Mapping(target = "profilePicUrl", expression = "java(mapToBase64(serviceProvider.getProfilePic()))")
    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    // Method to map ServiceProviderDTO to ServiceProvider entity
    @Mapping(target = "profilePic", source = "profilePic")
    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

    // Custom mapping from byte[] to MultipartFile
    default MultipartFile map(byte[] value) {
        return value != null ? new MockMultipartFile("profilePic", "profilePic.jpg", "image/jpeg", value) : null;
    }

    // Custom mapping from MultipartFile to byte[]
    default byte[] map(MultipartFile file) {
        try {
            return file != null ? file.getBytes() : null; // Convert to byte[]
        } catch (IOException e) {
            throw new RuntimeException("Error while converting MultipartFile to byte[]", e);
        }
    }

    // Custom mapping from byte[] to Base64 String for display purposes
    default String mapToBase64(byte[] value) {
        return value != null ? Base64.getEncoder().encodeToString(value) : null;
    }
}*/

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.springboot.app.dto.ServiceProviderDTO;
import com.springboot.app.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    ServiceProviderDTO serviceProviderToDTO(ServiceProvider serviceProvider);

    ServiceProvider dtoToServiceProvider(ServiceProviderDTO serviceProviderDTO);

}
