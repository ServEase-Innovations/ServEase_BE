package com.springboot.app.repository;

import com.springboot.app.entity.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    List<CustomerFeedback> findByServiceProviderServiceproviderId(Long serviceproviderId);

}
