package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.ServiceProviderUsedCouponDTO;
import com.springboot.app.entity.ServiceProviderCouponId;
import com.springboot.app.entity.ServiceProviderUsedCoupon;
import com.springboot.app.mapper.ServiceProviderUsedCouponMapper;
import com.springboot.app.repository.CouponRepository;
import com.springboot.app.repository.ServiceProviderRepository;
import com.springboot.app.repository.ServiceProviderUsedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderUsedCouponServiceImpl implements ServiceProviderUsedCouponService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderUsedCouponServiceImpl.class);

    private final ServiceProviderUsedCouponRepository serviceProviderUsedCouponRepository;
    private final CouponRepository couponRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceProviderUsedCouponMapper couponMapper;

    @Override
    @Transactional
    public String saveServiceProviderUsedCoupon(ServiceProviderUsedCouponDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.warn("DTO or DTO ID is null.");
            return "Invalid request data.";
        }

        Long serviceProviderId = dto.getId().getServiceProviderId();
        Long couponId = dto.getId().getCouponId();

        if (serviceProviderId == null || couponId == null) {
            logger.warn("ServiceProvider ID or Coupon ID in DTO is null.");
            return "ServiceProvider ID and Coupon ID must be provided.";
        }

        logger.info("Saving coupon use for serviceProviderId={}, couponId={}", serviceProviderId, couponId);

        try {
            var serviceProvider = serviceProviderRepository.findById(serviceProviderId)
                    .orElseThrow(() -> new IllegalArgumentException("Service Provider does not exist."));

            var coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new IllegalArgumentException("Coupon does not exist."));

            ServiceProviderCouponId compoundId = new ServiceProviderCouponId(serviceProviderId, couponId);

            if (serviceProviderUsedCouponRepository.existsById(compoundId)) {
                logger.warn("ServiceProvider {} already used coupon {}.", serviceProviderId, couponId);
                return "Coupon already used by this service provider.";
            }

            ServiceProviderUsedCoupon entity = couponMapper.dtoToEntity(dto);

            // Set required entities
            entity.setId(compoundId);
            entity.setServiceProvider(serviceProvider);
            entity.setCoupon(coupon);

            serviceProviderUsedCouponRepository.save(entity);
            logger.info("Coupon use saved successfully for serviceProviderId={}, couponId={}", serviceProviderId,
                    couponId);
            return ServiceProviderConstants.ADDED;

        } catch (Exception e) {
            logger.error("Error saving coupon usage: {}", e.getMessage(), e);
            return ServiceProviderConstants.FAILED;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceProviderUsedCouponDTO> findAll(int page, Integer size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all used coupons - Page: {}, Size: {}", page, size);
        }

        try {
            List<ServiceProviderUsedCoupon> allCoupons = serviceProviderUsedCouponRepository.findAll(); // Full fetch

            return allCoupons.stream()
                    .skip((long) page * size)
                    .limit(size)
                    .map(couponMapper::entityToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching used coupons: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
