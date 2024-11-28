package com.springboot.app.service;

import java.util.List;
import com.springboot.app.dto.ServiceProviderPaymentDTO;

public interface ServiceProviderPaymentService {

    List<ServiceProviderPaymentDTO> getAllServiceProviderPayments(int page, int size);

    ServiceProviderPaymentDTO getServiceProviderPaymentById(Long id);

    String addServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);

    String updateServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);

    String deleteServiceProviderPayment(Long id);

}
