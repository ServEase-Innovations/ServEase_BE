package com.springboot.app.repository;

import com.springboot.app.entity.ShortListedServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortListedServiceProviderRepository extends JpaRepository<ShortListedServiceProvider, Long> {

}
