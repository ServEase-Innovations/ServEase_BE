package com.springboot.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderFeedbackDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ServiceProviderFeedback;
import com.springboot.app.mapper.ServiceProviderFeedbackMapper;

import jakarta.transaction.Transactional;

@Service
public class ServiceProviderFeedbackServiceImpl implements ServiceProviderFeedbackService {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ServiceProviderFeedbackMapper serviceProviderFeedbackMapper; // Inject the feedback mapper

    @Override
    @Transactional
    public List<ServiceProviderFeedbackDTO> getAllServiceProviderFeedbackDTOs() {
        Session session = sessionFactory.getCurrentSession();
        List<ServiceProviderFeedback> feedbacks = session
                .createQuery("from ServiceProviderFeedback", ServiceProviderFeedback.class)
                .list();

        return feedbacks.stream()
                .map(serviceProviderFeedbackMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceProviderFeedbackDTO getServiceProviderFeedbackDTOById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback feedback = session.get(ServiceProviderFeedback.class, id);

        if (feedback == null) {
            throw new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND + id);
        }

        return serviceProviderFeedbackMapper.toDTO(feedback);
    }

    @Override
    @Transactional
    public void saveServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO feedbackDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback feedback = serviceProviderFeedbackMapper.toEntity(feedbackDTO);
        session.persist(feedback);
        updateCustomerAverageRating(feedback.getCustomerId());
    }

    // to update
    @Override
    @Transactional
    public void updateServiceProviderFeedbackDTO(ServiceProviderFeedbackDTO feedbackDTO) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback existingFeedback = session.get(ServiceProviderFeedback.class, feedbackDTO.getId());

        if (existingFeedback == null) {
            throw new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND + feedbackDTO.getId());
        }
        existingFeedback = serviceProviderFeedbackMapper.toEntity(feedbackDTO);
        session.merge(existingFeedback);
        updateCustomerAverageRating(feedbackDTO.getCustomerId());

        // existingFeedback = serviceProviderFeedbackMapper.toEntity(feedbackDTO);
        // session.merge(existingFeedback);
    }

    @Override
    @Transactional
    public void deleteServiceProviderFeedbackDTO(Long id) {
        Session session = sessionFactory.getCurrentSession();
        ServiceProviderFeedback feedback = session.get(ServiceProviderFeedback.class, id);

        if (feedback != null) {
            Long customerId = feedback.getCustomerId();
            session.remove(feedback);
            updateCustomerAverageRating(customerId);
        } else {
            throw new RuntimeException(ServiceProviderConstants.FEEDBACK_NOT_FOUND + id);
        }
    }

    private void updateCustomerAverageRating(Long customerId) {
        Session session = sessionFactory.getCurrentSession();

        // Fetch all feedback for this customer
        List<ServiceProviderFeedback> providerFeedbacks = session.createQuery(
                "FROM ServiceProviderFeedback WHERE customerId = :customerId",
                ServiceProviderFeedback.class)
                .setParameter("customerId", customerId)
                .list();

        if (!providerFeedbacks.isEmpty()) {
            double totalRating = 0.0;
            for (ServiceProviderFeedback feedback : providerFeedbacks) {
                totalRating += feedback.getRating();
            }

            double averageRating = totalRating / providerFeedbacks.size();

            // Update the corresponding Customer with the new average
            Customer customer = session.get(Customer.class, customerId);
            if (customer != null) {
                customer.setRating(averageRating);
                session.merge(customer);
            }
        } else {
            // If no feedback exists, you may want to reset the average rating
            Customer customer = session.get(Customer.class, customerId);
            if (customer != null) {
                customer.setRating(0.0); // Reset to 0 if no feedback
                session.merge(customer);
            }
        }
    }
}
