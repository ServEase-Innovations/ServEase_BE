package com.springboot.app.repository;

import com.springboot.app.entity.ServiceProviderCouponId;
import com.springboot.app.entity.ServiceProviderUsedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderUsedCouponRepository
        extends JpaRepository<ServiceProviderUsedCoupon, ServiceProviderCouponId> {

    List<ServiceProviderUsedCoupon> findByServiceProvider_ServiceproviderId(Long serviceproviderId);
}
