package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.ShortListedServiceProviderDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ShortListedServiceProvider;
import com.springboot.app.mapper.ShortListedServiceProviderMapper;
import com.springboot.app.repository.ShortListedServiceProviderRepository;
import com.springboot.app.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ShortListedServiceProviderServiceImpl implements ShortListedServiceProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ShortListedServiceProviderService.class);

    @Autowired
    private ShortListedServiceProviderRepository shortListedServiceProviderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShortListedServiceProviderMapper shortListedServiceProviderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ShortListedServiceProviderDTO> getAllShortListedServiceProviders(int page, int size) {
        if(logger.isInfoEnabled()) {
            logger.info("Fetching all short-listed service providers with pagination - page: {}, size: {}", page, size);
        }
        return shortListedServiceProviderRepository
                .findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(shortListedServiceProviderMapper::shortListedServiceProviderToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShortListedServiceProviderDTO getShortListedServiceProviderById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching short-listed service provider by ID: {}", id);
        }
        ShortListedServiceProvider provider = shortListedServiceProviderRepository.findById(id).orElse(null);
        if (logger.isDebugEnabled()) {
            logger.debug("Found short-listed service provider: {}", provider);
        }
        return provider != null ? shortListedServiceProviderMapper.shortListedServiceProviderToDTO(provider) : null;
    }

    @Override
    @Transactional
    public String addShortListedServiceProvider(ShortListedServiceProviderDTO dto) {
        if (logger.isInfoEnabled()) {
            logger.info("Adding new short-listed service provider for customer ID: {}", dto.getCustomerId());
        }
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        if (customer == null) {
            return CustomerConstants.NOT_FOUND;
        }

        List<ShortListedServiceProvider> existingProviders = shortListedServiceProviderRepository.findAll();

        ShortListedServiceProvider existingProvider = existingProviders.stream()
                .filter(provider -> provider.getCustomer().getCustomerId().equals(dto.getCustomerId()))
                .findFirst()
                .orElse(null);

        if (logger.isDebugEnabled()) {
            logger.debug("Existing provider for customer ID {}: {}", dto.getCustomerId(), existingProvider);
        }
        if (existingProvider != null) {
            Set<String> updatedServiceProviderIds = new HashSet<>(
                    Arrays.asList(existingProvider.getServiceProviderIdList().split(",")));
            updatedServiceProviderIds.addAll(Arrays.asList(dto.getServiceProviderIdList().split(",")));
            existingProvider.setServiceProviderIdList(String.join(",", updatedServiceProviderIds));
            shortListedServiceProviderRepository.save(existingProvider);
            if (logger.isDebugEnabled()) {
                logger.debug("Updated service provider ID list for customer ID: {}", dto.getCustomerId());
            }
            return CustomerConstants.UPDATED;
        } else {
            ShortListedServiceProvider newProvider = new ShortListedServiceProvider();
            newProvider.setCustomer(customer);
            newProvider.setServiceProviderIdList(dto.getServiceProviderIdList());
            shortListedServiceProviderRepository.save(newProvider);
            if (logger.isDebugEnabled()) {
                logger.debug("Added new ShortListedServiceProvider for customer ID: {}", dto.getCustomerId());
            }
            return CustomerConstants.ADDED;
        }
    }

    @Override
    @Transactional
    public String updateShortListedServiceProvider(ShortListedServiceProviderDTO dto) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating short-listed service provider with ID: {}", dto.getId());
        }
        ShortListedServiceProvider providerToUpdate = shortListedServiceProviderRepository.findById(dto.getId())
                .orElse(null);
        if (providerToUpdate != null) {
            providerToUpdate.setServiceProviderIdList(dto.getServiceProviderIdList());
            shortListedServiceProviderRepository.save(providerToUpdate);
            if (logger.isDebugEnabled()) {
                logger.debug("ShortListedServiceProvider with ID {} updated successfully.", dto.getId());
            }
            return CustomerConstants.UPDATED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("ShortListedServiceProvider with ID {} not found.", dto.getId());
            }
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String deleteShortListedServiceProvider(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting short-listed service provider with ID: {}", id);
        }
        if (shortListedServiceProviderRepository.existsById(id)) {
            shortListedServiceProviderRepository.deleteById(id);
            if (logger.isDebugEnabled()) {
                logger.debug("ShortListedServiceProvider with ID {} deleted successfully.", id);
            }
            return CustomerConstants.DELETED;
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("ShortListedServiceProvider with ID {} not found.", id);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }

    @Override
    @Transactional
    public String removeFromServiceProviderIdList(Long customerId, String serviceProviderIdToRemove) {
        if (logger.isInfoEnabled()) {
            logger.info("Removing service provider ID: {} from customer ID: {}", serviceProviderIdToRemove, customerId);
        }
        List<ShortListedServiceProvider> providers = shortListedServiceProviderRepository.findAll();

        ShortListedServiceProvider existingProvider = providers.stream()
                .filter(provider -> provider.getCustomer().getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);

        if (existingProvider == null) {
            return CustomerConstants.NOT_FOUND;
        }

        List<String> serviceProviderIds = new ArrayList<>(
                Arrays.asList(existingProvider.getServiceProviderIdList().split(",")));
        boolean removed = serviceProviderIds.remove(serviceProviderIdToRemove);

        if (removed) {
            existingProvider.setServiceProviderIdList(String.join(",", serviceProviderIds));
            shortListedServiceProviderRepository.save(existingProvider);
            if (logger.isDebugEnabled()) {
                logger.debug("Removed service provider ID: {} from customer ID: {}", serviceProviderIdToRemove, customerId);
            }
            return CustomerConstants.UPDATED;
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Service provider ID: {} not found in the list for customer ID: {}", serviceProviderIdToRemove, customerId);
            }
            return CustomerConstants.NOT_FOUND;
        }
    }
}
