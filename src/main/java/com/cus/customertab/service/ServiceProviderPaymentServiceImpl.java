package com.cus.customertab.service;

import com.cus.customertab.dto.ServiceProviderPaymentDTO;
import com.cus.customertab.entity.ServiceProviderPayment;
import com.cus.customertab.mapper.ServiceProviderPaymentMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceProviderPaymentServiceImpl implements ServiceProviderPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderPaymentServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServiceProviderPaymentMapper serviceProviderPaymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderPaymentDTO> getAllServiceProviderPayments(int page, int size) {
        logger.info("Fetching all service provider payments");
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProviderPayment> payments = session
                .createQuery("from ServiceProviderPayment", ServiceProviderPayment.class)
                .getResultList();
        logger.debug("Fetched {} service provider payments from the database.", payments.size());
        return payments.stream()
                .map(serviceProviderPaymentMapper::serviceProviderPaymentToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceProviderPaymentDTO getServiceProviderPaymentById(Long id) {
        logger.info("Fetching service provider payment by ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderPayment payment = session.get(ServiceProviderPayment.class, id);
        logger.debug("Found service provider payment with ID: {}", id);
        return serviceProviderPaymentMapper.serviceProviderPaymentToDTO(payment);
    }

    @Override
    @Transactional
    public String addServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        logger.info("Adding new service provider payment");
        Session session = sessionFactory.getCurrentSession();

        ServiceProviderPayment payment = serviceProviderPaymentMapper
                .dtoToServiceProviderPayment(serviceProviderPaymentDTO);
        session.persist(payment);
        logger.debug("Persisted new service provider payment with ID: {}", payment.getId());
        return "Service Provider Payment added successfully.";
    }

    @Override
    @Transactional
    public String updateServiceProviderPayment(ServiceProviderPaymentDTO serviceProviderPaymentDTO) {
        logger.info("Updating service provider payment with ID: {}", serviceProviderPaymentDTO.getId());
        Session session = sessionFactory.getCurrentSession();

        ServiceProviderPayment existingPayment = session.get(ServiceProviderPayment.class,
                serviceProviderPaymentDTO.getId());
        if (existingPayment == null) {
            logger.warn("Service provider payment not found with ID: {}", serviceProviderPaymentDTO.getId());
            return "Service Provider Payment not found.";
        }
        ServiceProviderPayment updatedPayment = serviceProviderPaymentMapper
                .dtoToServiceProviderPayment(serviceProviderPaymentDTO);
        updatedPayment.setId(existingPayment.getId());
        session.merge(updatedPayment);
        logger.debug("Updated service provider payment with ID: {}", serviceProviderPaymentDTO.getId());
        return "Service Provider Payment updated successfully.";
    }

    @Override
    @Transactional
    public String deleteServiceProviderPayment(Long id) {
        logger.info("Deleting service provider payment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderPayment existingPayment = session.get(ServiceProviderPayment.class, id);
        if (existingPayment != null) {
            session.remove(existingPayment);
            logger.debug("Deleted service provider payment with ID: {}", id);
            return "Service Provider Payment deleted successfully.";
        } else {
            logger.error("Service provider payment not found with ID: {}", id);
            return "Service Provider Payment not found.";
        }
    }
}
