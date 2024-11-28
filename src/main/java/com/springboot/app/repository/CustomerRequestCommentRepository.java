package com.springboot.app.repository;

import com.springboot.app.entity.CustomerRequestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRequestCommentRepository extends JpaRepository<CustomerRequestComment, Long> {

}
