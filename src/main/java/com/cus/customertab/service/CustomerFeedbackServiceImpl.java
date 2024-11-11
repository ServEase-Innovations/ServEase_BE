package com.cus.customertab.service;

import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerFeedbackDTO;
import com.cus.customertab.entity.CustomerFeedback;
import com.cus.customertab.entity.ServiceProvider;
import com.cus.customertab.mapper.CustomerFeedbackMapper;
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
public class CustomerFeedbackServiceImpl implements CustomerFeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerFeedbackServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerFeedbackMapper customerFeedbackMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerFeedbackDTO> getAllFeedback(int page, int size) {
        logger.info("Fetching all feedback with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<CustomerFeedback> feedbackList = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_ALL_CUSTOMER_FEEDBACK,
                page,
                size,
                CustomerFeedback.class);
        logger.debug("Fetched {} feedback entries from the database.", feedbackList.size());

        return feedbackList.stream()
                .map(customerFeedbackMapper::customerFeedbackToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerFeedbackDTO getFeedbackById(Long id) {
        logger.info("Fetching feedback by ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        CustomerFeedback feedback = session.get(CustomerFeedback.class, id);

        if (feedback != null) {
            logger.debug("Found feedback entry with ID: {}", id);
        } else {
            logger.error("No feedback found with ID: {}", id);
        }

        return customerFeedbackMapper.customerFeedbackToDTO(feedback);
    }

    @Override
    @Transactional
    public String addFeedback(CustomerFeedbackDTO customerFeedbackDTO) {
        logger.info("Adding new feedback for ServiceProvider ID: {}", customerFeedbackDTO.getServiceProviderId());
        Session session = sessionFactory.getCurrentSession();

        ServiceProvider serviceProvider = session.get(ServiceProvider.class,
                customerFeedbackDTO.getServiceProviderId());

        if (serviceProvider == null) {
            logger.error("Service Provider not found with ID: {}", customerFeedbackDTO.getServiceProviderId());
            throw new IllegalArgumentException(
                    "Service Provider not found with ID: " + customerFeedbackDTO.getServiceProviderId());
        }

        CustomerFeedback feedback = customerFeedbackMapper.dtoToCustomerFeedback(customerFeedbackDTO);
        feedback.setServiceProvider(serviceProvider);
        session.persist(feedback);
        logger.debug("Persisted new feedback for ServiceProvider ID: {}", customerFeedbackDTO.getServiceProviderId());

        List<CustomerFeedback> providerFeedbacks = session.createQuery(
                "FROM CustomerFeedback WHERE serviceProvider.serviceProviderId = :serviceProviderId",
                CustomerFeedback.class)
                .setParameter("serviceProviderId", serviceProvider.getServiceproviderId())
                .list();

        double totalRating = providerFeedbacks.stream()
                .mapToDouble(CustomerFeedback::getRating)
                .sum();
        double averageRating = providerFeedbacks.isEmpty() ? feedback.getRating()
                : totalRating / providerFeedbacks.size();
        serviceProvider.setRating(averageRating);

        session.merge(serviceProvider);
        logger.info("Updated ServiceProvider rating to: {}", averageRating);

        return CustomerConstants.ADDED;
    }

    @Override
    @Transactional
    public String deleteFeedback(Long feedbackId) {
        logger.info("Deleting feedback with ID: {}", feedbackId);
        Session session = sessionFactory.getCurrentSession();
        CustomerFeedback feedback = session.get(CustomerFeedback.class, feedbackId);

        if (feedback != null) {
            session.remove(feedback);
            logger.debug("Deleted feedback with ID: {}", feedbackId);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Feedback not found with ID: {}", feedbackId);
            return CustomerConstants.NOT_FOUND;
        }
    }
}
