package com.springboot.app.repository;

import com.springboot.app.entity.ServiceProvider;

import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;

@Repository
public interface ServiceProviderRepository
                extends JpaRepository<ServiceProvider, Long>, JpaSpecificationExecutor<ServiceProvider> {

        boolean existsByMobileNo(Long mobileNo);

        boolean existsByEmailId(String emailId);

        // Slice<ServiceProvider> findByLocation(String location, Pageable pageable);
        Page<ServiceProvider> findByLocation(String location, Pageable pageable);

        // boolean existsByEmailIdOrMobileNo(String normalizedEmail, Long mobileNo);

        List<ServiceProvider> findByVendorId(Long vendorId);
        
        List<ServiceProvider> findByGeoHash5In(List<String> geoHashes);

        List<ServiceProvider> findByGeoHash6In(List<String> geoHashes);

        List<ServiceProvider> findByGeoHash7In(List<String> geoHashes);

}
