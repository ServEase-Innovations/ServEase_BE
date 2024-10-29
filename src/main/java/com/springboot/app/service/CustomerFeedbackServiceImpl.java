package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerFeedbackDTO;
import com.springboot.app.entity.CustomerFeedback;
import com.springboot.app.mapper.CustomerFeedbackMapper;
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

    // To add new customer feedback
    @Override
    @Transactional
    public String addFeedback(CustomerFeedbackDTO customerFeedbackDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerFeedback feedback = customerFeedbackMapper.dtoToCustomerFeedback(customerFeedbackDTO);
        session.persist(feedback);
        return CustomerConstants.ADDED;
    }

    // To delete customer feedback
    @Override
    @Transactional
    public String deleteFeedback(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CustomerFeedback existingFeedback = session.get(CustomerFeedback.class, id);
        session.remove(existingFeedback);
        return CustomerConstants.DELETED;
    }
}
