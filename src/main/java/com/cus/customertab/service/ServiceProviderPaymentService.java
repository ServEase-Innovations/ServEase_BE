package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.dto.ServiceProviderPaymentDTO;

public interface ServiceProviderPaymentService {

    List<ServiceProviderPaymentDTO> getAllServiceProviderPayments(int page, int size);
    ServiceProviderPaymentDTO getServiceProviderPaymentById(Long id);
    String addServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);
    String updateServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);
    String deleteServiceProviderPayment(Long id);
    
}