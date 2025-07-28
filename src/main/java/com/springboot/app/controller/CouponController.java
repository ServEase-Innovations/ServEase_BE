package com.springboot.app.controller;

import com.springboot.app.constant.ServiceProviderConstants;
import com.springboot.app.dto.CouponDTO;
import com.springboot.app.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ServiceProviderConstants.COUPON_API_PATH)
@Api(value = "Coupon Management", tags = "Coupons")
public class CouponController {

    private final CouponService couponService;

    @Value("${app.pagination.default-page-size:10}")
    private int defaultPageSize;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/add")
    @ApiOperation(value = "Add a new coupon")
    public ResponseEntity<?> addCoupon(
            @ApiParam(value = "Coupon details", required = true) @RequestBody CouponDTO couponDTO) {
        try {
            String result = couponService.createCoupon(couponDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add coupon: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get all coupons with pagination", response = List.class)
    public ResponseEntity<?> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            if (size == null)
                size = defaultPageSize;
            List<CouponDTO> coupons = couponService.getAllCoupons(page, size);
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch coupons: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get coupon by ID", response = CouponDTO.class)
    public ResponseEntity<?> getCouponById(
            @ApiParam(value = "Coupon ID", required = true) @PathVariable Long id) {
        try {
            CouponDTO coupon = couponService.getCouponById(id);
            return ResponseEntity.ok(coupon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found with ID: " + id);
        }
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "Update an existing coupon")
    public ResponseEntity<?> updateCoupon(
            @ApiParam(value = "Coupon ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated coupon data", required = true) @RequestBody CouponDTO couponDTO) {
        try {
            couponDTO.setId(id);
            String result = couponService.updateCoupon(id, couponDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update coupon: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "Delete a coupon")
    public ResponseEntity<?> deleteCoupon(
            @ApiParam(value = "Coupon ID", required = true) @PathVariable Long id) {
        try {
            String result = couponService.deleteCoupon(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete coupon: " + e.getMessage());
        }
    }

    @GetMapping("/code/{code}")
    @ApiOperation(value = "Get coupon by code")
    public ResponseEntity<?> getCouponByCode(
            @ApiParam(value = "Coupon Code", required = true) @PathVariable String code) {
        try {
            CouponDTO couponDTO = couponService.getByCouponCode(code);
            if (couponDTO != null) {
                return ResponseEntity.ok(couponDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Coupon not found with code: " + code);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch coupon: " + e.getMessage());
        }
    }

}
