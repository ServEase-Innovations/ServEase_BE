package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderPaymentDTO;
import com.springboot.app.entity.ServiceProviderPayment;
import com.springboot.app.mapper.ServiceProviderPaymentMapper;
import com.springboot.app.repository.ServiceProviderPaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Service
public class ServiceProviderPaymentServiceImpl implements ServiceProviderPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderPaymentServiceImpl.class);

    private final ServiceProviderPaymentRepository serviceProviderPaymentRepository;
    private final ServiceProviderPaymentMapper serviceProviderPaymentMapper;

    @Autowired
    public ServiceProviderPaymentServiceImpl(ServiceProviderPaymentRepository serviceProviderPaymentRepository,
            ServiceProviderPaymentMapper serviceProviderPaymentMapper) {
        this.serviceProviderPaymentRepository = serviceProviderPaymentRepository;
        this.serviceProviderPaymentMapper = serviceProviderPaymentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderPaymentDTO> getAllServiceProviderPayments(int page, int size) {
        logger.info("Fetching all service provider payments with pagination - page: {}, size: {}", page, size);

        List<ServiceProviderPayment> payments = serviceProviderPaymentRepository
                .findAll(PageRequest.of(page, size))
                .getContent();

        logger.debug("Fetched {} service provider payments from the database.", payments.size());
        return payments.stream()
                .map(serviceProviderPaymentMapper::serviceProviderPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderPaymentDTO getServiceProviderPaymentById(Long id) {
        logger.info("Fetching service provider payment by ID: {}", id);

        ServiceProviderPayment payment = serviceProviderPaymentRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn(ServiceProviderConstants.PAYMENT_NOT_FOUND_MSG, id);
                    return new RuntimeException("Service Provider Payment not found with ID: " + id);
                });

        logger.debug("Found service provider payment with ID: {}", id);
        return serviceProviderPaymentMapper.serviceProviderPaymentToDTO(payment);
    }

    @Override
    @Transactional
    public String addServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        logger.info("Adding new service provider payment");

        ServiceProviderPayment payment = serviceProviderPaymentMapper
                .dtoToServiceProviderPayment(serviceProviderPaymentDTO);

        serviceProviderPaymentRepository.save(payment);
        logger.debug("Persisted new service provider payment with ID: {}", payment.getId());
        return "Service Provider Payment added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        logger.info("Updating service provider payment with ID: {}", serviceProviderPaymentDTO.getId());

        ServiceProviderPayment existingPayment = serviceProviderPaymentRepository
                .findById(serviceProviderPaymentDTO.getId())
                .orElseThrow(() -> {
                    logger.warn("Service provider payment not found with ID: {}", serviceProviderPaymentDTO.getId());
                    return new RuntimeException(
                            "Service Provider Payment not found with ID: " + serviceProviderPaymentDTO.getId());
                });

        serviceProviderPaymentMapper.updateServiceProviderPaymentFromDTO(serviceProviderPaymentDTO, existingPayment);

        serviceProviderPaymentRepository.save(existingPayment);
        logger.debug("Updated service provider payment with ID: {}", serviceProviderPaymentDTO.getId());
        return "Service Provider Payment updated successfully.";
    }

    @Override
    @Transactional
    public String deleteServiceProviderPayment(Long id) {
        logger.info("Deleting service provider payment with ID: {}", id);

        if (serviceProviderPaymentRepository.existsById(id)) {
            serviceProviderPaymentRepository.deleteById(id);
            logger.debug("Deleted service provider payment with ID: {}", id);
            return "Service Provider Payment deleted successfully.";
        } else {
            logger.error("Service provider payment not found with ID: {}", id);
            return "Service Provider Payment not found.";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderPaymentDTO> getPaymentsByDateRange(Date startDate, Date endDate) {
        logger.info("Fetching payments between {} and {}", startDate, endDate);
        List<ServiceProviderPayment> payments = serviceProviderPaymentRepository
                .findByPaymentOnBetween(startDate, endDate);

        if (payments.isEmpty()) {
            logger.info("No payments found between {} and {}", startDate, endDate);
            return Collections.emptyList();
        }

        logger.debug("Found {} payments between {} and {}", payments.size(), startDate, endDate);
        return payments.stream()
                .map(serviceProviderPaymentMapper::serviceProviderPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderPaymentDTO> getPaymentsByMonthAndYear(int month, int year) {
        logger.info("Fetching payments for month: {} and year: {}", month, year);

        List<ServiceProviderPayment> payments = serviceProviderPaymentRepository
                .findByMonthAndYear(month, year);

        if (payments.isEmpty()) {
            logger.info("No payments found for month: {} and year: {}", month, year);
            return Collections.emptyList();
        }

        logger.debug("Found {} payments for month: {} and year: {}", payments.size(), month, year);
        return payments.stream()
                .map(serviceProviderPaymentMapper::serviceProviderPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderPaymentDTO> getPaymentsByFinancialYear(int year) {
        logger.info("Fetching payments for financial year: {}", year);

        Date fromDate = Date.valueOf((year - 1) + "-04-01");
        Date toDate = Date.valueOf(year + "-03-31");

        List<ServiceProviderPayment> payments = serviceProviderPaymentRepository
                .findByPaymentOnBetween(fromDate, toDate);

        if (payments.isEmpty()) {
            logger.info("No payments found for financial year: {}", year);
            return Collections.emptyList();
        }
        logger.debug("Found {} payments for financial year: {}", payments.size(), year);
        return payments.stream()
                .map(serviceProviderPaymentMapper::serviceProviderPaymentToDTO)
                .toList();
    }

}