package com.springboot.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.app.entity.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByCompanyNameIgnoreCase(String companyName); // Correctly querying by company name

    boolean existsByPhoneNo(Long phoneNo);

    boolean existsByEmailId(String emailId);

}
