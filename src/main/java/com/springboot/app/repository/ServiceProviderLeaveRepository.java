package com.springboot.app.repository;

import com.springboot.app.entity.ServiceProviderLeave;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderLeaveRepository extends
                JpaRepository<ServiceProviderLeave, Long> {
        List<ServiceProviderLeave> findByServiceProvider_ServiceproviderId(Long serviceproviderId);

}
