package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.dto.CustomerFeedbackDTO;

public interface CustomerFeedbackService {
    List<CustomerFeedbackDTO> getAllFeedback();
    CustomerFeedbackDTO getFeedbackById(Long id); 
    String addFeedback(CustomerFeedbackDTO customerFeedbackDTO); 
    String deleteFeedback(Long id);
}
