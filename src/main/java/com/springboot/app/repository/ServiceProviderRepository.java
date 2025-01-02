package com.springboot.app.repository;

import com.springboot.app.entity.ServiceProvider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderRepository
                extends JpaRepository<ServiceProvider, Long>, JpaSpecificationExecutor<ServiceProvider> {

        boolean existsByMobileNo(Long mobileNo);

        boolean existsByEmailId(String emailId);

}
