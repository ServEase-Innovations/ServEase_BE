package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.app.entity.ServiceProviderRequestComment;

@Repository
public interface ServiceProviderRequestCommentRepository extends JpaRepository<ServiceProviderRequestComment, Long> {

}
