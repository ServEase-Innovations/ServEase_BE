package com.cus.customertab.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import com.cus.customertab.config.PaginationHelper;
import com.cus.customertab.dto.ShortListedServiceProviderDTO;
import com.cus.customertab.entity.Customer;
import com.cus.customertab.entity.ShortListedServiceProvider;
import com.cus.customertab.mapper.ShortListedServiceProviderMapper;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ShortListedServiceProviderServiceImpl implements ShortListedServiceProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ShortListedServiceProviderServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ShortListedServiceProviderMapper shortListedServiceProviderMapper;

    @Override
@Transactional(readOnly = true)
public List<ShortListedServiceProviderDTO> getAllShortListedServiceProviders(int page, int size) {
    logger.info("Fetching all ShortListedServiceProviders with page: {} and size: {}", page, size);
    Session session = sessionFactory.getCurrentSession();
    String hql = "from ShortListedServiceProvider";
    List<ShortListedServiceProvider> shortListedProviders = PaginationHelper.getPaginatedResults(
            session,
            hql,
            page,
            size,
            ShortListedServiceProvider.class
    );
    logger.debug("Number of ShortListedServiceProviders fetched: {}", shortListedProviders.size());
    return shortListedProviders.stream()
            .map(shortListedServiceProviderMapper::shortListedServiceProviderToDTO)
            .collect(Collectors.toList());
}


    @Override
    @Transactional(readOnly = true)
    public ShortListedServiceProviderDTO getShortListedServiceProviderById(Long id) {
        logger.info("Fetching ShortListedServiceProvider by id: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ShortListedServiceProvider shortListedProvider = session.get(ShortListedServiceProvider.class, id);
        return shortListedProvider != null
                ? shortListedServiceProviderMapper.shortListedServiceProviderToDTO(shortListedProvider)
                : null;
    }

    @Override
    @Transactional
    public String addShortListedServiceProvider(ShortListedServiceProviderDTO dto) {
        logger.info("Adding new ShortListedServiceProvider: {}", dto);
        Session session = sessionFactory.getCurrentSession();

        // Find the Customer object by customerId
        Customer customer = session.get(Customer.class, dto.getCustomerId());

        if (customer == null) {
            logger.error("Customer with ID {} not found.", dto.getCustomerId());
            return "Customer not found.";
        }

        // Query to find existing provider for the customer
        Query<ShortListedServiceProvider> query = session.createQuery(
                "from ShortListedServiceProvider where customer.id = :customerId",
                ShortListedServiceProvider.class);
        query.setParameter("customerId", dto.getCustomerId());
        ShortListedServiceProvider existingProvider = query.uniqueResult();

        if (existingProvider != null) {
            logger.info("ShortListedServiceProvider exists for customer ID: {}. Updating serviceProviderIdList.",
                    dto.getCustomerId());

            // Combine existing and new service provider IDs while avoiding duplicates
            Set<String> updatedServiceProviderIds = new HashSet<>(
                    Arrays.asList(existingProvider.getServiceProviderIdList().split(",")));
            updatedServiceProviderIds.addAll(Arrays.asList(dto.getServiceProviderIdList().split(",")));

            // Update the existing provider with the merged IDs
            existingProvider.setServiceProviderIdList(String.join(",", updatedServiceProviderIds));
            session.merge(existingProvider);

            logger.info("Updated serviceProviderIdList for customer ID: {}", dto.getCustomerId());
            return "Updated ShortListedServiceProvider for existing customer ID.";
        } else {
            // New entry for a ShortListedServiceProvider
            ShortListedServiceProvider newProvider = new ShortListedServiceProvider();
            newProvider.setCustomer(customer); // Set the customer object
            newProvider.setServiceProviderIdList(dto.getServiceProviderIdList());
            session.persist(newProvider);

            logger.info("Added new ShortListedServiceProvider with ID: {}", newProvider.getId());
            return "Added new ShortListedServiceProvider.";
        }
    }
            

    @Override
    @Transactional
    public String updateShortListedServiceProvider(ShortListedServiceProviderDTO dto) {
        logger.info("Updating ShortListedServiceProvider with ID: {}", dto.getId());
        Session session = sessionFactory.getCurrentSession();
        ShortListedServiceProvider providerToUpdate = session.get(ShortListedServiceProvider.class, dto.getId());

        if (providerToUpdate != null) {
            providerToUpdate.setServiceProviderIdList(dto.getServiceProviderIdList());
            session.merge(providerToUpdate);
            logger.info("ShortListedServiceProvider updated with ID: {}", dto.getId());
            return "ShortListedServiceProvider updated successfully.";
        } else {
            logger.error("ShortListedServiceProvider not found for ID: {}", dto.getId());
            return "ShortListedServiceProvider not found.";
        }
    }

    @Override
    @Transactional
    public String deleteShortListedServiceProvider(Long id) {
        logger.info("Deleting ShortListedServiceProvider with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        ShortListedServiceProvider providerToDelete = session.get(ShortListedServiceProvider.class, id);

        if (providerToDelete != null) {
            session.remove(providerToDelete);
            logger.info("ShortListedServiceProvider deleted with ID: {}", id);
            return "ShortListedServiceProvider deleted successfully.";
        } else {
            logger.error("ShortListedServiceProvider not found for ID: {}", id);
            return "ShortListedServiceProvider not found.";
        }
    }

    @Override
    @Transactional
    public String removeFromServiceProviderIdList(Long customerId, String serviceProviderIdToRemove) {
        logger.info("Removing service provider ID: {} from customer ID: {}", serviceProviderIdToRemove, customerId);
        Session session = sessionFactory.getCurrentSession();

        // Find existing provider for the customer
        Query<ShortListedServiceProvider> query = session.createQuery(
                "from ShortListedServiceProvider where customer.id = :customerId",
                ShortListedServiceProvider.class);
        query.setParameter("customerId", customerId);
        ShortListedServiceProvider existingProvider = query.uniqueResult();

        if (existingProvider == null) {
            logger.error("No ShortListedServiceProvider found for customer ID: {}", customerId);
            return "No ShortListedServiceProvider found for the provided customer ID.";
        }

        // Split the existing serviceProviderIdList into a list
        List<String> serviceProviderIds = new ArrayList<>(
                Arrays.asList(existingProvider.getServiceProviderIdList().split(",")));

        // Remove the specified service provider ID
        boolean removed = serviceProviderIds.remove(serviceProviderIdToRemove);

        if (removed) {
            // Update the list and persist changes
            existingProvider.setServiceProviderIdList(String.join(",", serviceProviderIds));
            session.merge(existingProvider);
            logger.info("Removed service provider ID: {} from customer ID: {}", serviceProviderIdToRemove, customerId);
            return "Removed service provider ID from the list.";
        } else {
            logger.warn("Service provider ID: {} not found in the list for customer ID: {}", serviceProviderIdToRemove,
                    customerId);
            return "Service provider ID not found in the list.";
        }
    }

}