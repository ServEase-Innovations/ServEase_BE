package com.cus.customertab.service;

import java.util.List;
import com.cus.customertab.dto.ShortListedServiceProviderDTO;

public interface ShortListedServiceProviderService {

    List<ShortListedServiceProviderDTO> getAllShortListedServiceProviders(int page, int size);
    ShortListedServiceProviderDTO getShortListedServiceProviderById(Long id);
    String addShortListedServiceProvider(ShortListedServiceProviderDTO dto);
    String updateShortListedServiceProvider(ShortListedServiceProviderDTO dto);
    String deleteShortListedServiceProvider(Long id);
    String removeFromServiceProviderIdList(Long customerId, String serviceProviderIdToRemove);
    
}
