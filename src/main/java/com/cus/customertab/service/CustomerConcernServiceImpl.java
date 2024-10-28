package com.cus.customertab.service;

import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerConcernDTO;
import com.cus.customertab.entity.CustomerConcern;
import com.cus.customertab.mapper.CustomerConcernMapper;
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
public class CustomerConcernServiceImpl implements CustomerConcernService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerConcernServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerConcernMapper customerConcernMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerConcernDTO> getAllConcerns(int page, int size) {
        logger.info("Fetching all concerns with pagination - page: {}, size: {}", page, size);
        Session session = sessionFactory.getCurrentSession();
        List<CustomerConcern> concerns = PaginationHelper.getPaginatedResults(
                session,
                CustomerConstants.GET_ALL_CUSTOMER_CONCERNS,
                page,
                size,
                CustomerConcern.class);
        logger.debug("Fetched {} concerns from the database.", concerns.size());

        return concerns.stream()
                .map(customerConcernMapper::customerConcernToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerConcernDTO getConcernById(Long id) {
        logger.info("Fetching concern by ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        CustomerConcern concern = session.get(CustomerConcern.class, id);

        if (concern != null) {
            logger.debug("Found concern with ID: {}", id);
        } else {
            logger.error("No concern found with ID: {}", id);
        }

        return customerConcernMapper.customerConcernToDTO(concern);
    }

    @Override
    @Transactional
    public String addNewConcern(CustomerConcernDTO customerConcernDTO) {
        logger.info("Adding new customer concern");
        Session session = sessionFactory.getCurrentSession();

        CustomerConcern concern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
        session.persist(concern);
        logger.debug("Persisted new concern with ID: {}", concern.getId());
        return CustomerConstants.ADDED;
    }

    @Override
    @Transactional
    public String modifyConcern(CustomerConcernDTO customerConcernDTO) {
        logger.info("Modifying concern with ID: {}", customerConcernDTO.getId());
        Session session = sessionFactory.getCurrentSession();

        CustomerConcern existingConcern = session.get(CustomerConcern.class, customerConcernDTO.getId());
        if (existingConcern == null) {
            logger.warn("Concern not found with ID: {}", customerConcernDTO.getId());
            return CustomerConstants.NOT_FOUND;
        }

        CustomerConcern updatedConcern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
        updatedConcern.setId(existingConcern.getId());
        session.merge(updatedConcern);
        logger.debug("Modified concern with ID: {}", customerConcernDTO.getId());
        return CustomerConstants.UPDATED;
    }

    @Override
    @Transactional
    public String deleteConcern(Long id) {
        logger.info("Deleting concern with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();

        CustomerConcern existingConcern = session.get(CustomerConcern.class, id);
        if (existingConcern != null) {
            session.remove(existingConcern);
            logger.debug("Deleted concern with ID: {}", id);
            return CustomerConstants.DELETED;
        } else {
            logger.error("Concern not found with ID: {}", id);
            return CustomerConstants.NOT_FOUND;
        }
    }
}
