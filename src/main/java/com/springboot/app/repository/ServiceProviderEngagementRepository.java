package com.springboot.app.repository;

//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import com.springboot.app.dto.ServiceProviderEngagementDTO;
import com.springboot.app.entity.ServiceProviderEngagement;

@Repository
public interface ServiceProviderEngagementRepository extends JpaRepository<ServiceProviderEngagement, Long> {

}
