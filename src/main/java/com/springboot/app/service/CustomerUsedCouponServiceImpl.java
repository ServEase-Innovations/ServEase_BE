package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.CustomerUsedCouponDTO;
import com.springboot.app.entity.CustomerCouponId;
import com.springboot.app.entity.CustomerUsedCoupon;
import com.springboot.app.mapper.CustomerUsedCouponMapper;
import com.springboot.app.repository.CouponRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.CustomerUsedCouponRepository;
import com.springboot.app.repository.ServiceProviderEngagementRepository;
import com.springboot.app.service.CustomerUsedCouponService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerUsedCouponServiceImpl implements CustomerUsedCouponService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerUsedCouponServiceImpl.class);

    private final CustomerUsedCouponRepository customerUsedCouponRepository;
    private final CouponRepository couponRepository;
    private final CustomerUsedCouponMapper couponMapper;
    private final ServiceProviderEngagementRepository engagementRepository;

    @Override
    @Transactional
    public String saveCustomerUsedCoupon(CustomerUsedCouponDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.warn("DTO or DTO ID is null.");
            return "Invalid request data.";
        }

        Long engagementId = dto.getId().getEngagementId();
        Long couponId = dto.getId().getCouponId();

        if (engagementId == null || couponId == null) {
            logger.warn("Engagement ID or Coupon ID in DTO is null.");
            return "Engagement ID and Coupon ID must be provided.";
        }

        logger.info("Saving coupon use for engagementId={}, couponId={}", engagementId, couponId);

        try {
            // Fetch engagement
            var engagement = engagementRepository.findById(engagementId)
                    .orElseThrow(() -> new IllegalArgumentException("Engagement does not exist."));

            // Fetch coupon
            var coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new IllegalArgumentException("Coupon does not exist."));

            // Create composite key
            CustomerCouponId compoundId = new CustomerCouponId(engagementId, couponId);

            if (customerUsedCouponRepository.existsById(compoundId)) {
                logger.warn("Engagement {} already used coupon {}.", engagementId, couponId);
                return "Coupon already used for this engagement.";
            }

            // Map DTO to entity
            CustomerUsedCoupon entity = couponMapper.dtoToEntity(dto);

            // Set required entities
            entity.setId(compoundId);
            entity.setEngagement(engagement);
            entity.setCoupon(coupon);

            // Save
            customerUsedCouponRepository.save(entity);
            logger.info("Coupon use saved successfully for engagementId={}, couponId={}", engagementId, couponId);
            return ServiceProviderConstants.ADDED;

        } catch (Exception e) {
            logger.error("Error saving coupon usage: {}", e.getMessage(), e);
            return ServiceProviderConstants.FAILED;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerUsedCouponDTO> findAll(int page, Integer size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all used coupons - Page: {}, Size: {}", page, size);
        }

        try {
            List<CustomerUsedCoupon> allCoupons = customerUsedCouponRepository.findAll(); // full fetch

            return allCoupons.stream()
                    .skip((long) page * size) // simulate pagination
                    .limit(size)
                    .map(couponMapper::entityToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching used coupons: {}", e.getMessage(), e);
            return List.of();
        }
    }

}
