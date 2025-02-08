package com.springboot.app.service;

import com.springboot.app.dto.ShortListedServiceProviderDTO;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.ShortListedServiceProvider;
import com.springboot.app.mapper.ShortListedServiceProviderMapper;
import com.springboot.app.repository.ShortListedServiceProviderRepository;
import com.springboot.app.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShortListedServiceProviderServiceImpl implements ShortListedServiceProviderService {

    @Autowired
    private ShortListedServiceProviderRepository shortListedServiceProviderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShortListedServiceProviderMapper shortListedServiceProviderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ShortListedServiceProviderDTO> getAllShortListedServiceProviders(int page, int size) {
        return shortListedServiceProviderRepository
                .findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(shortListedServiceProviderMapper::shortListedServiceProviderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShortListedServiceProviderDTO getShortListedServiceProviderById(Long id) {
        ShortListedServiceProvider provider = shortListedServiceProviderRepository.findById(id).orElse(null);
        return provider != null ? shortListedServiceProviderMapper.shortListedServiceProviderToDTO(provider) : null;
    }

    @Override
    @Transactional
    public String addShortListedServiceProvider(ShortListedServiceProviderDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        if (customer == null) {
            return "Customer not found.";
        }

        List<ShortListedServiceProvider> existingProviders = shortListedServiceProviderRepository.findAll();

        ShortListedServiceProvider existingProvider = existingProviders.stream()
                .filter(provider -> provider.getCustomer().getCustomerId().equals(dto.getCustomerId()))
                .findFirst()
                .orElse(null);

        if (existingProvider != null) {
            Set<String> updatedServiceProviderIds = new HashSet<>(
                    Arrays.asList(existingProvider.getServiceProviderIdList().split(",")));
            updatedServiceProviderIds.addAll(Arrays.asList(dto.getServiceProviderIdList().split(",")));
            existingProvider.setServiceProviderIdList(String.join(",", updatedServiceProviderIds));
            shortListedServiceProviderRepository.save(existingProvider);
            return "Updated ShortListedServiceProvider for existing customer ID.";
        } else {
            ShortListedServiceProvider newProvider = new ShortListedServiceProvider();
            newProvider.setCustomer(customer);
            newProvider.setServiceProviderIdList(dto.getServiceProviderIdList());
            shortListedServiceProviderRepository.save(newProvider);
            return "Added new ShortListedServiceProvider.";
        }
    }

    @Override
    @Transactional
    public String updateShortListedServiceProvider(ShortListedServiceProviderDTO dto) {
        ShortListedServiceProvider providerToUpdate = shortListedServiceProviderRepository.findById(dto.getId())
                .orElse(null);
        if (providerToUpdate != null) {
            providerToUpdate.setServiceProviderIdList(dto.getServiceProviderIdList());
            shortListedServiceProviderRepository.save(providerToUpdate);
            return "ShortListedServiceProvider updated successfully.";
        } else {
            return "ShortListedServiceProvider not found.";
        }
    }

    @Override
    @Transactional
    public String deleteShortListedServiceProvider(Long id) {
        if (shortListedServiceProviderRepository.existsById(id)) {
            shortListedServiceProviderRepository.deleteById(id);
            return "ShortListedServiceProvider deleted successfully.";
        } else {
            return "ShortListedServiceProvider not found.";
        }
    }

    @Override
    @Transactional
    public String removeFromServiceProviderIdList(Long customerId, String serviceProviderIdToRemove) {
        List<ShortListedServiceProvider> providers = shortListedServiceProviderRepository.findAll();

        ShortListedServiceProvider existingProvider = providers.stream()
                .filter(provider -> provider.getCustomer().getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);

        if (existingProvider == null) {
            return "No ShortListedServiceProvider found for the provided customer ID.";
        }

        List<String> serviceProviderIds = new ArrayList<>(
                Arrays.asList(existingProvider.getServiceProviderIdList().split(",")));
        boolean removed = serviceProviderIds.remove(serviceProviderIdToRemove);

        if (removed) {
            existingProvider.setServiceProviderIdList(String.join(",", serviceProviderIds));
            shortListedServiceProviderRepository.save(existingProvider);
            return "Removed service provider ID from the list.";
        } else {
            return "Service provider ID not found in the list.";
        }
    }
}
