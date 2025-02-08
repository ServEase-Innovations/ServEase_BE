package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerConcernDTO;
import com.springboot.app.entity.CustomerConcern;
import com.springboot.app.mapper.CustomerConcernMapper;
import com.springboot.app.repository.CustomerConcernRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerConcernServiceImpl implements CustomerConcernService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConcernServiceImpl.class);

    @Autowired
    private CustomerConcernMapper customerConcernMapper;

    @Autowired
    private CustomerConcernRepository customerConcernRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerConcernDTO> getAllConcerns(int page, int size) {
        logger.info("Fetching all concerns with pagination - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<CustomerConcern> concerns = customerConcernRepository.findAll(pageable).getContent();

        logger.debug("Fetched {} concerns from the database.", concerns.size());

        return concerns.stream()
                .map(customerConcernMapper::customerConcernToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerConcernDTO getConcernById(Long id) {
        logger.info("Fetching concern by ID: {}", id);

        // Use the repository to fetch the concern by ID
        Optional<CustomerConcern> concernOptional = customerConcernRepository.findById(id);
        if (concernOptional.isPresent()) {
            logger.debug("Found concern with ID: {}", id);
            return customerConcernMapper.customerConcernToDTO(concernOptional.get());
        } else {
            logger.error("No concern found with ID: {}", id);
            return null; // Return null or throw an exception based on your preference
        }
    }

    @Override
    @Transactional
    public String addNewConcern(CustomerConcernDTO customerConcernDTO) {
        logger.info("Adding new customer concern");

        CustomerConcern concern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
        customerConcernRepository.save(concern); // Use repository to save the entity

        logger.debug("Persisted new concern with ID: {}", concern.getId());
        return CustomerConstants.ADDED;
    }

    @Override
    @Transactional
    public String modifyConcern(CustomerConcernDTO customerConcernDTO) {
        logger.info("Modifying concern with ID: {}", customerConcernDTO.getId());

        // Fetch the existing concern using the repository
        Optional<CustomerConcern> existingConcernOptional = customerConcernRepository
                .findById(customerConcernDTO.getId());
        if (existingConcernOptional.isPresent()) {
            CustomerConcern existingConcern = existingConcernOptional.get();
            CustomerConcern updatedConcern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
            updatedConcern.setId(existingConcern.getId());
            customerConcernRepository.save(updatedConcern); // Use repository to save the updated entity

            logger.debug("Modified concern with ID: {}", customerConcernDTO.getId());
            return CustomerConstants.UPDATED;
        } else {
            logger.warn("Concern not found with ID: {}", customerConcernDTO.getId());
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deleteConcern(Long id) {
        logger.info("Deleting concern with ID: {}", id);

        // Use the repository to check if the concern exists
        Optional<CustomerConcern> existingConcernOptional = customerConcernRepository.findById(id);
        if (existingConcernOptional.isPresent()) {
            CustomerConcern existingConcern = existingConcernOptional.get();
            customerConcernRepository.delete(existingConcern); // Use repository to delete the entity

            logger.debug("Deleted concern with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Concern not found with ID: {}", id);
            return CustomerConstants.NOT_FOUND;
        }
    }
}
