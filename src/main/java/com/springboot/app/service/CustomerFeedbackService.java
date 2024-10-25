
package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.CustomerFeedbackDTO;

public interface CustomerFeedbackService {
    List<CustomerFeedbackDTO> getAllFeedback();

    CustomerFeedbackDTO getFeedbackById(Long id);

    String addFeedback(CustomerFeedbackDTO customerFeedbackDTO);

    String deleteFeedback(Long id);
}