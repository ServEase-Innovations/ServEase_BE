package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.CustomerUsedCouponDTO;
import com.springboot.app.entity.CustomerCouponId;
import com.springboot.app.entity.CustomerUsedCoupon;
import com.springboot.app.mapper.CustomerUsedCouponMapper;
import com.springboot.app.repository.CouponRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.CustomerUsedCouponRepository;
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
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public String saveCustomerUsedCoupon(CustomerUsedCouponDTO dto) {
        if (dto == null || dto.getId() == null) {
            logger.warn("DTO or DTO ID is null.");
            return "Invalid request data.";
        }

        Long customerId = dto.getId().getCustomerId();
        Long couponId = dto.getId().getCouponId();

        if (customerId == null || couponId == null) {
            logger.warn("Customer ID or Coupon ID in DTO is null.");
            return "Customer ID and Coupon ID must be provided.";
        }

        logger.info("Saving coupon use for customerId={}, couponId={}", customerId, couponId);

        try {
            var customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer does not exist."));

            var coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new IllegalArgumentException("Coupon does not exist."));

            CustomerCouponId compoundId = new CustomerCouponId(customerId, couponId);
            if (customerUsedCouponRepository.existsById(compoundId)) {
                logger.warn("Customer {} already used coupon {}.", customerId, couponId);
                return "Coupon already used by this customer.";
            }

            CustomerUsedCoupon entity = couponMapper.dtoToEntity(dto);

            // ✅ Set required entities
            entity.setId(compoundId);
            entity.setCustomer(customer);
            entity.setCoupon(coupon);

            customerUsedCouponRepository.save(entity);
            logger.info("Coupon use saved successfully for customerId={}, couponId={}", customerId, couponId);
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
