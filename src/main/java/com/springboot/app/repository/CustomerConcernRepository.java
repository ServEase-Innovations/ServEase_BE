package com.springboot.app.repository;

import com.springboot.app.entity.CustomerConcern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerConcernRepository extends JpaRepository<CustomerConcern, Long> {

}
