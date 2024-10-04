package com.cus.customertab.service;

import com.cus.customertab.constants.CustomerConstants;
import com.cus.customertab.dto.CustomerConcernDTO;
import com.cus.customertab.entity.CustomerConcern;
import com.cus.customertab.mapper.CustomerConcernMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerConcernServiceImpl implements CustomerConcernService {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private CustomerConcernMapper customerConcernMapper;

    // To get all concerns
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CustomerConcernDTO>> getAllConcerns() {
        Session session = sessionFactory.getCurrentSession();
        List<CustomerConcern> concerns = session.createQuery("FROM CustomerConcern", CustomerConcern.class)
                .getResultList();
        List<CustomerConcernDTO> dtoList = concerns.stream()
                .map(customerConcernMapper::customerConcernToDTO)
                .toList();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    // To get concern by ID
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CustomerConcernDTO> getConcernById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CustomerConcern concern = session.get(CustomerConcern.class, id);

        if (concern != null) {
            return new ResponseEntity<>(customerConcernMapper.customerConcernToDTO(concern), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // To add a new concern
    @Override
    @Transactional
    public ResponseEntity<String> addNewConcern(CustomerConcernDTO customerConcernDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerConcern concern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
        session.persist(concern);
        return new ResponseEntity<>(CustomerConstants.CUSTOMER_CONCERN_ADDED, HttpStatus.CREATED);
    }

    // To modify a concern
    @Override
    @Transactional
    public ResponseEntity<String> modifyConcern(CustomerConcernDTO customerConcernDTO) {
        Session session = sessionFactory.getCurrentSession();
        CustomerConcern existingConcern = session.get(CustomerConcern.class, customerConcernDTO.getId());

        if (existingConcern == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Use the mapper to map updated fields
        CustomerConcern updatedConcern = customerConcernMapper.dtoToCustomerConcern(customerConcernDTO);
        updatedConcern.setId(existingConcern.getId());
        session.merge(updatedConcern);
        return new ResponseEntity<>(CustomerConstants.CUSTOMER_CONCERN_UPDATED, HttpStatus.OK);
    }

    // To delete a concern
    @Override
    @Transactional
    public ResponseEntity<String> deleteConcern(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CustomerConcern existingConcern = session.get(CustomerConcern.class, id);

        if (existingConcern != null) {
            session.remove(existingConcern);
            return new ResponseEntity<>(CustomerConstants.CUSTOMER_CONCERN_DELETED, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
