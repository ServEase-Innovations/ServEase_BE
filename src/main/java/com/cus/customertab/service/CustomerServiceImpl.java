package com.cus.customertab.service;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cus.customertab.entity.Customer;
import jakarta.transaction.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Customer> getAllCustomers() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Customer", Customer.class).list();
    }

    @Override
    @Transactional
    public Customer getCustomerById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Customer.class, id);
    }

    @Override
    @Transactional
    public void saveCustomer(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        customer.setActive(true);
        session.persist(customer);
    }

    @Override
    @Transactional
    public void updateCustomer(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {  
        Session session = sessionFactory.getCurrentSession();
        Customer customer = session.get(Customer.class, id);
        if(customer!=null){
            customer.setActive(false);
            session.merge(customer);
        }  
    }


    
}
