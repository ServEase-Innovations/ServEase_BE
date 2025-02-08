package com.springboot.app.repository;

import com.springboot.app.entity.KYC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KYCRepository extends JpaRepository<KYC, Long> {

}
