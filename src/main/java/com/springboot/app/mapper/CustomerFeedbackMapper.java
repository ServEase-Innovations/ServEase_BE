
package com.springboot.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.springboot.app.dto.CustomerFeedbackDTO;
import com.springboot.app.entity.CustomerFeedback;

@Mapper(componentModel = "spring")
public interface CustomerFeedbackMapper {
    CustomerFeedbackMapper INSTANCE = Mappers.getMapper(CustomerFeedbackMapper.class);

    CustomerFeedbackDTO customerFeedbackToDTO(CustomerFeedback customerFeedback);

    CustomerFeedback dtoToCustomerFeedback(CustomerFeedbackDTO customerFeedbackDTO);
}
