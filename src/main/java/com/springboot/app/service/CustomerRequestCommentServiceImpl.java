package com.springboot.app.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerRequestCommentDTO;
import com.springboot.app.entity.CustomerRequest;
import com.springboot.app.entity.CustomerRequestComment;
import com.springboot.app.mapper.CustomerRequestCommentMapper;
import com.springboot.app.repository.CustomerRequestCommentRepository;
import com.springboot.app.repository.CustomerRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class CustomerRequestCommentServiceImpl implements CustomerRequestCommentService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRequestCommentServiceImpl.class);

    private final CustomerRequestCommentMapper customerRequestCommentMapper;
    private final CustomerRequestCommentRepository customerRequestCommentRepository;
    private final CustomerRequestRepository customerRequestRepository;

    @Autowired
    public CustomerRequestCommentServiceImpl(
            CustomerRequestCommentMapper customerRequestCommentMapper,
            CustomerRequestCommentRepository customerRequestCommentRepository,
            CustomerRequestRepository customerRequestRepository) {
        this.customerRequestCommentMapper = customerRequestCommentMapper;
        this.customerRequestCommentRepository = customerRequestCommentRepository;
        this.customerRequestRepository = customerRequestRepository;
    }

    // To get all customer request comments
    @Override
    @Transactional(readOnly = true)
    public List<CustomerRequestCommentDTO> getAllComments(int page, int size) {
        logger.info("Fetching all customer request comments with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<CustomerRequestComment> commentsList = customerRequestCommentRepository.findAll(pageable).getContent();

        logger.debug("Fetched {} customer request comments from the database.", commentsList.size());

        return commentsList.stream()
                .map(customerRequestCommentMapper::customerRequestCommentToDTO)
                .toList();
    }

    // To get a customer request comment by ID
    @Override
    @Transactional(readOnly = true)
    public CustomerRequestCommentDTO getCommentById(Long id) {
        logger.info("Fetching customer request comment by ID: {}", id);
        CustomerRequestComment comment = customerRequestCommentRepository.findById(id).orElse(null);
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

        if (commentDTO.getRequestId() == null) {
            logger.warn("CustomerRequestId is required to add a comment.");
            throw new IllegalArgumentException("CustomerRequestId is required.");
        }

        CustomerRequest customerRequest = customerRequestRepository.findById(commentDTO.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "CustomerRequest with ID " + commentDTO.getRequestId() + " does not exist."));

        CustomerRequestComment comment = customerRequestCommentMapper.dtoToCustomerRequestComment(commentDTO);
        customerRequest.getComments().add(comment);
        comment.setCustomerRequest(customerRequest);
        customerRequestCommentRepository.save(comment);

        logger.info("Comment added with ID: {}", comment.getId());
        return CustomerConstants.ADDED;
    }

    // To update a customer request comment
    @Override
    @Transactional
    public String updateComment(Long id, CustomerRequestCommentDTO commentDTO) {
        logger.info("Updating comment with ID: {}", id);

        CustomerRequestComment existingComment = customerRequestCommentRepository.findById(id).orElse(null);
        if (existingComment != null) {
            CustomerRequestComment updatedComment = customerRequestCommentMapper
                    .dtoToCustomerRequestComment(commentDTO);
            updatedComment.setId(existingComment.getId());
            customerRequestCommentRepository.save(updatedComment);

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

        CustomerRequestComment existingComment = customerRequestCommentRepository.findById(id).orElse(null);
        if (existingComment != null) {
            customerRequestCommentRepository.delete(existingComment);
            logger.info("Comment deleted with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Comment not found for deletion with ID: {}", id);
            return CustomerConstants.NOT_FOUND;
        }
    }
}
