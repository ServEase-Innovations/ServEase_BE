package com.springboot.app.service;

import java.sql.Date;
import java.util.List;
import com.springboot.app.dto.ServiceProviderPaymentDTO;

public interface ServiceProviderPaymentService {

    List<ServiceProviderPaymentDTO> getAllServiceProviderPayments(int page, int size);

    ServiceProviderPaymentDTO getServiceProviderPaymentById(Long id);

    String addServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);

    String updateServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO);

    String deleteServiceProviderPayment(Long id);

    List<ServiceProviderPaymentDTO> getPaymentsByDateRange(Date startDate, Date endDate);

    List<ServiceProviderPaymentDTO> getPaymentsByMonthAndYear(int month, int year);

    List<ServiceProviderPaymentDTO> getPaymentsByFinancialYear(int year);

    List<ServiceProviderPaymentDTO> getPaymentsByCustomerId(Long customerId);

    List<ServiceProviderPaymentDTO> getPaymentsByServiceProviderId(Long serviceProviderId);

}
