package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.util.Base64;
import com.springboot.app.dto.CustomerDTO;
import com.springboot.app.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Map Customer entity to CustomerDTO
    // @Mapping(target = "profilePic", source = "profilePic")
    // @Mapping(target = "profilePicUrl", expression =
    // "java(mapToBase64(customer.getProfilePic()))")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    CustomerDTO customerToDTO(Customer customer);

    // Method to map CustomerDTO to Customer entity, ignoring username and password
    @Mapping(target = "profilePic", source = "profilePic")
    Customer dtoToCustomer(CustomerDTO customerDTO);

    // Custom mapping from byte[] to MultipartFile
    /*
     * default MultipartFile map(byte[] value) {
     * return value != null ? new MockMultipartFile("profilePic", "profilePic.jpg",
     * "image/jpeg", value) : null;
     * }
     * 
     * // Custom mapping from MultipartFile to byte[]
     * default byte[] map(MultipartFile file) {
     * try {
     * return file != null ? file.getBytes() : null;
     * } catch (IOException e) {
     * throw new RuntimeException("Error while converting MultipartFile to byte[]",
     * e);
     * }
     * }
     * 
     * // Custom mapping from byte[] to Base64 String for display purposes
     * default String mapToBase64(byte[] value) {
     * return value != null ? Base64.getEncoder().encodeToString(value) : null;
     * }
     */
}
