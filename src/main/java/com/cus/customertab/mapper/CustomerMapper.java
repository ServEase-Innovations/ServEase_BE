package com.cus.customertab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.cus.customertab.dto.CustomerDTO;
import com.cus.customertab.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    // Method to map Customer entity to CustomerDTO
    @Mapping(target = "profilePic", source = "profilePic")
    CustomerDTO customerToDTO(Customer customer);

    // Method to map CustomerDTO to Customer entity
    @Mapping(target = "profilePic", source = "profilePic")
    Customer dtoToCustomer(CustomerDTO customerDTO);

    // Custom mapping from byte[] to MultipartFile
    default MultipartFile map(byte[] value) {
        // Create a MultipartFile implementation from the byte array
        return new MockMultipartFile("profilePic", "profilePic.jpg", "image/jpeg", value);
    }

    // Custom mapping from MultipartFile to byte[]
    default byte[] map(MultipartFile file) {
        try {
            return file != null ? file.getBytes() : null; // Convert to byte[]
        } catch (IOException e) {
            throw new RuntimeException("Error while converting MultipartFile to byte[]", e);
        }
    }
}





// package com.cus.customertab.mapper;

// import org.mapstruct.Mapper;
// import org.mapstruct.factory.Mappers;
// import com.cus.customertab.dto.CustomerDTO;
// import com.cus.customertab.entity.Customer;

// @Mapper(componentModel = "spring")
// public interface CustomerMapper {
//     CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

//     // Method to map Customer entity to CustomerDTO
//     CustomerDTO customerToDTO(Customer customer);

//     // Method to map CustomerDTO to Customer entity
//     Customer dtoToCustomer(CustomerDTO customerDTO);
// }
