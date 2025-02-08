package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.app.entity.ServiceProviderFeedback;
import java.util.List;

@Repository
public interface ServiceProviderFeedbackRepository extends
        JpaRepository<ServiceProviderFeedback, Long> {
    List<ServiceProviderFeedback> findByCustomer_CustomerId(Long customerId);

}