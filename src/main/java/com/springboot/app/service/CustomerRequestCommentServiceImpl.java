package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerRequestCommentDTO;
import com.springboot.app.entity.CustomerRequest;
import com.springboot.app.entity.CustomerRequestComment;
import com.springboot.app.mapper.CustomerRequestCommentMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerRequestCommentServiceImpl implements CustomerRequestCommentService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerRequestCommentMapper customerRequestCommentMapper;

    // To get all customer request comments
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestCommentDTO> getAllComments() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequestComment> comments = session
                .createQuery("FROM CustomerRequestComment", CustomerRequestComment.class)
                .getResultList();
        return comments.stream()
                .map(customerRequestCommentMapper::customerRequestCommentToDTO)
                .toList();
    }

    // To get a customer request comment by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestCommentDTO getCommentById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequestComment comment = session.get(CustomerRequestComment.class, id);
        return customerRequestCommentMapper.customerRequestCommentToDTO(comment);
    }

    // To add a new customer request comment
    @Override
    @Transactional
    public String addComment(CustomerRequestCommentDTO commentDTO) {
        Session session = sessionFactory.getCurrentSession();
        if (commentDTO.getRequestId() == null) {
            throw new IllegalArgumentException("CustomerRequestId is required.");
        }
        CustomerRequest customerRequest = session.get(CustomerRequest.class, commentDTO.getRequestId());

        if (customerRequest == null) {
            throw new IllegalArgumentException(
                    "CustomerRequest with ID " + commentDTO.getRequestId() + " does not exist.");
        }
        CustomerRequestComment comment = customerRequestCommentMapper.dtoToCustomerRequestComment(commentDTO);
        customerRequest.getComments().add(comment);
        comment.setCustomerRequest(customerRequest);
        session.persist(comment);
        return CustomerConstants.ADDED;
    }

    // To update a customer request comment
    @Override
    @Transactional
    public String updateComment(Long id, CustomerRequestCommentDTO commentDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequestComment existingComment = session.get(CustomerRequestComment.class, id);
        CustomerRequestComment updatedComment = customerRequestCommentMapper.dtoToCustomerRequestComment(commentDTO);
        if (existingComment != null) {
            updatedComment.setId(existingComment.getId());
            session.merge(updatedComment);
        }
        return CustomerConstants.UPDATED;
    }

    // To delete a customer request comment
    @Override
    @Transactional
    public String deleteComment(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CustomerRequestComment existingComment = session.get(CustomerRequestComment.class, id);
        if (existingComment != null) {
            session.remove(existingComment);
        }
        return CustomerConstants.DELETED;
    }
}
