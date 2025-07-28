package com.springboot.app.repository;

import com.springboot.app.entity.CustomerCouponId;
import com.springboot.app.entity.CustomerUsedCoupon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerUsedCouponRepository extends JpaRepository<CustomerUsedCoupon, CustomerCouponId> {
    List<CustomerUsedCoupon> findByCustomer_CustomerId(Long customerId);

}
