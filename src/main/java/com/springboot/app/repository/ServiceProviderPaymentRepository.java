package com.springboot.app.repository;

import com.springboot.app.entity.ServiceProviderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderPaymentRepository extends JpaRepository<ServiceProviderPayment, Long> {
}
