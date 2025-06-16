package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerConcernDTO;
import com.springboot.app.entity.CustomerConcern;
import com.springboot.app.mapper.CustomerConcernMapper;
import com.springboot.app.repository.CustomerConcernRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerConcernServiceImpl implements CustomerConcernService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConcernServiceImpl.class);

    private final CustomerConcernMapper customerConcernMapper;
    private final CustomerConcernRepository customerConcernRepository;

    // Constructor injection
    public CustomerConcernServiceImpl(CustomerConcernMapper customerConcernMapper,
            CustomerConcernRepository customerConcernRepository) {
        this.customerConcernMapper = customerConcernMapper;
        this.customerConcernRepository = customerConcernRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerConcernDTO> getAllConcerns(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all concerns with pagination - page: {}, size: {}", page, size);
        }
        Pageable pageable = PageRequest.of(page, size);
        List<CustomerConcern> concerns = customerConcernRepository.findAll(pageable).getContent();

        if (logger.isDebugEnabled()) {
            logger.debug("Fetched {} concerns from the database.", concerns.size());
        }
        return concerns.stream()
                .map(customerConcernMapper::customerConcernToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerConcernDTO getConcernById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching concern by ID: {}", id);
        }
        // Use the repository to fetch the concern by ID
        Optional<CustomerConcern> concernOptional = customerConcernRepository.findById(id);
        if (concernOptional.isPresent()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found concern with ID: {}", id);
            }
            return customerConcernMapper.customerConcernToDTO(concernOptional.get());
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("No concern found with ID: {}", id);
            }
            return null; 
        }
    }

    @Override
    @Transactional
    public String addNewConcern(CustomerConcernDTO customerConcernDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding new customer concern");
        }
        CustomerConcern concern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
        customerConcernRepository.save(concern); 

        if (logger.isDebugEnabled()) {
            logger.debug("Persisted new concern with ID: {}", concern.getId());
        }
        return CustomerConstants.ADDED;
    }

    @Override
    @Transactional
    public String modifyConcern(CustomerConcernDTO customerConcernDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Modifying concern with ID: {}", customerConcernDTO.getId());
        }
        // Fetch the existing concern using the repository
        Optional<CustomerConcern> existingConcernOptional = customerConcernRepository
                .findById(customerConcernDTO.getId());
        if (existingConcernOptional.isPresent()) {
            CustomerConcern existingConcern = existingConcernOptional.get();
            CustomerConcern updatedConcern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
            updatedConcern.setId(existingConcern.getId());
            customerConcernRepository.save(updatedConcern); // Use repository to save the updated entity

            if (logger.isDebugEnabled()) {
                logger.debug("Modified concern with ID: {}", customerConcernDTO.getId());
            }
            return CustomerConstants.UPDATED;
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Concern not found with ID: {}", customerConcernDTO.getId());
            }
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deleteConcern(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting concern with ID: {}", id);
        }
        // Use the repository to check if the concern exists
        Optional<CustomerConcern> existingConcernOptional = customerConcernRepository.findById(id);
        if (existingConcernOptional.isPresent()) {
            CustomerConcern existingConcern = existingConcernOptional.get();
            customerConcernRepository.delete(existingConcern); // Use repository to delete the entity

            if (logger.isDebugEnabled()) {
                logger.debug("Deleted concern with ID: {}", id);
            }
            return CustomerConstants.DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Concern not found with ID: {}", id);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }
}
