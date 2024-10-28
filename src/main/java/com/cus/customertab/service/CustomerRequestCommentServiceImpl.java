package com.cus.customertab.service;

import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerRequestCommentDTO;
import com.cus.customertab.entity.CustomerRequest;
import com.cus.customertab.entity.CustomerRequestComment;
import com.cus.customertab.mapper.CustomerRequestCommentMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerRequestCommentServiceImpl implements CustomerRequestCommentService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRequestCommentServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerRequestCommentMapper customerRequestCommentMapper;

    // To get all customer request comments
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestCommentDTO> getAllComments(int page, int size) {
        logger.info("Fetching all customer request comments with page: {} and size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<CustomerRequestComment> comments = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.FROM_CR_COMMENT,
                page,
                size,
                CustomerRequestComment.class);
        logger.debug("Number of comments fetched: {}", comments.size());
        return comments.stream()
                .map(customerRequestCommentMapper::customerRequestCommentToDTO)
                .collect(Collectors.toList());
    }

    // To get a customer request comment by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestCommentDTO getCommentById(Long id) {
        logger.info("Fetching customer request comment by ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        CustomerRequestComment comment = session.get(CustomerRequestComment.class, id);
        if (comment != null) {
            logger.debug("Comment found with ID: {}", id);
        } else {
            logger.error("Comment not found with ID: {}", id);
        }
        return customerRequestCommentMapper.customerRequestCommentToDTO(comment);
    }

    // To add a new customer request comment
    @Override
    @Transactional
    public String addComment(CustomerRequestCommentDTO commentDTO) {
        logger.info("Adding new comment for customer request ID: {}", commentDTO.getRequestId());
        Session session = sessionFactory.getCurrentSession();
        if (commentDTO.getRequestId() == null) {
            logger.warn("CustomerRequestId is required to add a comment.");
            throw new IllegalArgumentException("CustomerRequestId is required.");
        }
        CustomerRequest customerRequest = session.get(CustomerRequest.class, commentDTO.getRequestId());
        if (customerRequest == null) {
            logger.error("CustomerRequest with ID {} does not exist.", commentDTO.getRequestId());
            throw new IllegalArgumentException(
                    "CustomerRequest with ID " + commentDTO.getRequestId() + " does not exist.");
        }
        CustomerRequestComment comment = customerRequestCommentMapper.dtoToCustomerRequestComment(commentDTO);
        customerRequest.getComments().add(comment);
        comment.setCustomerRequest(customerRequest);
        session.persist(comment);
        logger.info("Comment added with ID: {}", comment.getId());
        return CustomerConstants.ADDED;
    }

    // To update a customer request comment
    @Override
    @Transactional
    public String updateComment(Long id, CustomerRequestCommentDTO commentDTO) {
        logger.info("Updating comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        CustomerRequestComment existingComment = session.get(CustomerRequestComment.class, id);
        if (existingComment != null) {
            CustomerRequestComment updatedComment = customerRequestCommentMapper
                    .dtoToCustomerRequestComment(commentDTO);
            updatedComment.setId(existingComment.getId());
            session.merge(updatedComment);
            logger.info("Comment updated successfully with ID: {}", id);
            return CustomerConstants.UPDATED;
        } else {
            logger.error("Comment not found for update with ID: {}", id);
            return CustomerConstants.NOT_FOUND;
        }
    }

    // To delete a customer request comment
    @Override
    @Transactional
    public String deleteComment(Long id) {
        logger.info("Deleting comment with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        CustomerRequestComment existingComment = session.get(CustomerRequestComment.class, id);
        if (existingComment != null) {
            session.remove(existingComment);
            logger.info("Comment deleted with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Comment not found for deletion with ID: {}", id);
            return CustomerConstants.NOT_FOUND;
        }
    }
}
