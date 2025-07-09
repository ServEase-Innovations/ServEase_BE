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
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all service provider payments with pagination - page: {}, size: {}", page, size);
        }

        List<ServiceProviderPayment> payments = serviceProviderPaymentRepository
                .findAll(PageRequest.of(page, size))
                .getContent();
        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} service provider payments from the database.", payments.size());
        }
        return payments.stream()
                .map(serviceProviderPaymentMapper::serviceProviderPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderPaymentDTO getServiceProviderPaymentById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching service provider payment by ID: {}", id);
        }
        ServiceProviderPayment payment = serviceProviderPaymentRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn(ServiceProviderConstants.PAYMENT_NOT_FOUND_MSG, id);
                    return new RuntimeException("Service Provider Payment not found with ID: " + id);
                });
        if (logger.isDebugEnabled()) {
            logger.debug("Found service provider payment with ID: {}", id);
        }
        return serviceProviderPaymentMapper.serviceProviderPaymentToDTO(payment);
    }

    @Override
    @Transactional
    public String addServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        if (logger.isInfoEnabled()) {

            logger.info("Adding new service provider payment");
        }

        ServiceProviderPayment payment = serviceProviderPaymentMapper
                .dtoToServiceProviderPayment(serviceProviderPaymentDTO);

        serviceProviderPaymentRepository.save(payment);
        if (logger.isDebugEnabled()) {

            logger.debug("Persisted new service provider payment with ID: {}", payment.getId());
        }
        return "Service Provider Payment added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating service provider payment with ID: {}", serviceProviderPaymentDTO.getId());
        }

        ServiceProviderPayment existingPayment = serviceProviderPaymentRepository
                .findById(serviceProviderPaymentDTO.getId())
                .orElseThrow(() -> {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Service provider payment not found with ID: {}",
                                serviceProviderPaymentDTO.getId());
                    }
                    return new RuntimeException(
                            "Service Provider Payment not found with ID: " + serviceProviderPaymentDTO.getId());
                });

        serviceProviderPaymentMapper.updateServiceProviderPaymentFromDTO(serviceProviderPaymentDTO, existingPayment);

        serviceProviderPaymentRepository.save(existingPayment);
        if (logger.isDebugEnabled()) {
            logger.debug("Updated service provider payment with ID: {}", serviceProviderPaymentDTO.getId());
        }
        return "Service Provider Payment updated successfully.";
    }

    @Override
    @Transactional
    public String deleteServiceProviderPayment(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting service provider payment with ID: {}", id);
        }

        if (serviceProviderPaymentRepository.existsById(id)) {
            serviceProviderPaymentRepository.deleteById(id);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted service provider payment with ID: {}", id);
            }
            return "Service Provider Payment deleted successfully.";
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Service provider payment not found with ID: {}", id);
            }
            return "Service Provider Payment not found.";
        }
    }
}
