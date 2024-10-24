package com.cus.customertab.service;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerFeedbackDTO;
import com.cus.customertab.entity.CustomerFeedback;
import com.cus.customertab.entity.ServiceProvider;
import com.cus.customertab.mapper.CustomerFeedbackMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class CustomerFeedbackServiceImpl implements CustomerFeedbackService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerFeedbackMapper customerFeedbackMapper;

    // To get all customer feedback
    @Override
    @Transactional(readOnly = true)
    public List<CustomerFeedbackDTO> getAllFeedback() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerFeedback> feedbackList = session.createQuery(
                CustomerConstants.GET_ALL_CUSTOMER_FEEDBACK, CustomerFeedback.class)
                .getResultList();
        return feedbackList.stream()
                .map(customerFeedbackMapper::customerFeedbackToDTO)
                .toList();
    }

    // To get customer feedback by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerFeedbackDTO getFeedbackById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CustomerFeedback feedback = session.get(CustomerFeedback.class, id);
        return customerFeedbackMapper.customerFeedbackToDTO(feedback);
    }

    
    //to add feedback
    @Override
    @Transactional
    public String addFeedback(CustomerFeedbackDTO customerFeedbackDTO) {
        Session session = sessionFactory.getCurrentSession();

        // Fetch the service provider by ID
        ServiceProvider serviceProvider = session.get(ServiceProvider.class,
                customerFeedbackDTO.getServiceProviderId());

        // Check if the ServiceProvider exists
        if (serviceProvider == null) {
            throw new IllegalArgumentException(
                    "Service Provider not found with ID: " + customerFeedbackDTO.getServiceProviderId());
        }

        // Map DTO to entity
        CustomerFeedback feedback = customerFeedbackMapper.dtoToCustomerFeedback(customerFeedbackDTO);
        feedback.setServiceProvider(serviceProvider);
        session.persist(feedback);

        // Fetch all feedback for this service provider
        List<CustomerFeedback> providerFeedbacks = session.createQuery(
                "FROM CustomerFeedback WHERE serviceProvider.serviceProviderId = :serviceProviderId",
                CustomerFeedback.class)
                .setParameter("serviceProviderId", serviceProvider.getServiceProviderId())
                .list();

        if (!providerFeedbacks.isEmpty()) {
            double totalRating = 0.0;
            for (CustomerFeedback providerFeedback : providerFeedbacks) {
                totalRating += providerFeedback.getRating();
            }

            double averageRating = totalRating / providerFeedbacks.size();
            serviceProvider.setRating(averageRating);
        } else {
            // If no feedback exists, set the rating to the current feedback rating
            serviceProvider.setRating(customerFeedbackDTO.getRating());
        }

        session.merge(serviceProvider);
        return CustomerConstants.ADDED;
    }

    // To delete customer feedback
    @Override
    @Transactional
    public String deleteFeedback(Long feedbackId) {
        Session session = sessionFactory.getCurrentSession();
        CustomerFeedback feedback = session.get(CustomerFeedback.class, feedbackId);

        if (feedback != null) {
            session.remove(feedback);
            return CustomerConstants.DELETED;
        } else {
            return CustomerConstants.NOT_FOUND; 
        }
    }
    


}
