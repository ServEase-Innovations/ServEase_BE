package com.springboot.app.repository;

import com.springboot.app.entity.KYCComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KYCCommentsRepository extends JpaRepository<KYCComments, Long> {

}
