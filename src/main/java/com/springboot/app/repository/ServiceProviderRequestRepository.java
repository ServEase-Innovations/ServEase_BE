package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.app.entity.ServiceProviderRequest;

@Repository
public interface ServiceProviderRequestRepository extends JpaRepository<ServiceProviderRequest, Long> {

}
