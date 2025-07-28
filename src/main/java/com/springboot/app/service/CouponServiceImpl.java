package com.springboot.app.service;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.CouponDTO;
import com.springboot.app.entity.Coupon;
import com.springboot.app.mapper.CouponMapper;
import com.springboot.app.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional
    public String createCoupon(CouponDTO couponDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Creating new coupon with code: {}", couponDTO.getCouponCode());
        }

        Coupon coupon = couponMapper.dtoToCoupon(couponDTO);
        coupon.setCreatedOn(new Date());
        coupon.setIsValid(1); // default to valid
        couponRepository.save(coupon);

        if (logger.isInfoEnabled()) {
            logger.info("Coupon created with ID: {}", coupon.getId());
        }

        return ServiceProviderConstants.ADDED;
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTO getCouponById(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching coupon by ID: {}", id);
        }

        return couponRepository.findById(id)
                .map(couponMapper::couponToDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons(int page, int size) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching all coupons - page: {}, size: {}", page, size);
        }

        Pageable pageable = PageRequest.of(page, size);
        return couponRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(couponMapper::couponToDTO)
                .toList();
    }

    @Override
    @Transactional
    public String updateCoupon(Long id, CouponDTO couponDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("Updating coupon with ID: {}", id);
        }

        return couponRepository.findById(id)
                .map(existing -> {
                    Coupon updated = couponMapper.dtoToCoupon(couponDTO);
                    updated.setId(existing.getId());
                    updated.setCreatedOn(existing.getCreatedOn()); // preserve created date
                    updated.setModifiedOn(new Date());
                    couponRepository.save(updated);

                    if (logger.isInfoEnabled()) {
                        logger.info("Coupon updated with ID: {}", id);
                    }

                    return ServiceProviderConstants.UPDATED;
                })
                .orElseGet(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Coupon not found for update with ID: {}", id);
                    }
                    return ServiceProviderConstants.NOT_FOUND;
                });
    }

    @Override
    @Transactional
    public String deleteCoupon(Long id) {
        if (logger.isInfoEnabled()) {
            logger.info("Soft deleting (invalidating) coupon with ID: {}", id);
        }

        return couponRepository.findById(id)
                .map(coupon -> {
                    coupon.setIsValid(0); // Soft delete by marking invalid
                    coupon.setModifiedOn(new Date()); // Optionally update modified date
                    couponRepository.save(coupon);

                    if (logger.isInfoEnabled()) {
                        logger.info("Coupon marked as invalid with ID: {}", id);
                    }
                    return ServiceProviderConstants.DELETED;
                })
                .orElseGet(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Coupon not found for soft deletion with ID: {}", id);
                    }
                    return ServiceProviderConstants.NOT_FOUND;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTO getByCouponCode(String code) {
        if (logger.isInfoEnabled()) {
            logger.info("Fetching coupon by code: {}", code);
        }

        return couponRepository.findAll()
                .stream()
                .filter(c -> c.getCouponCode().equalsIgnoreCase(code))
                .findFirst()
                .map(couponMapper::couponToDTO)
                .orElseGet(() -> {
                    if (logger.isErrorEnabled()) {
                        logger.error("Coupon not found with code: {}", code);
                    }
                    return null;
                });
    }

}
