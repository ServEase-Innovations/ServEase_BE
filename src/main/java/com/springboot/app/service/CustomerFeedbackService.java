
package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.CustomerFeedbackDTO;

public interface CustomerFeedbackService {
    List<CustomerFeedbackDTO> getAllFeedback(int page, int size);

    CustomerFeedbackDTO getFeedbackById(Long id);

    List<CustomerFeedbackDTO> getFeedbacksByServiceProviderId(Long serviceproviderId);

    String addFeedback(CustomerFeedbackDTO customerFeedbackDTO);

    String deleteFeedback(Long id);
}
