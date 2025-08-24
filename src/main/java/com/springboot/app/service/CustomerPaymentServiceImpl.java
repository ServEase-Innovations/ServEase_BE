
package com.springboot.app.service;

import com.springboot.app.constant.CustomerConstants;
import com.springboot.app.dto.CustomerPaymentDTO;
import com.springboot.app.entity.Coupon;
import com.springboot.app.entity.Customer;
import com.springboot.app.entity.CustomerCouponId;
import com.springboot.app.entity.CustomerHolidays;
import com.springboot.app.entity.CustomerPayment;
import com.springboot.app.entity.CustomerUsedCoupon;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.entity.ServiceProviderPayment;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.PaymentMode;
import com.springboot.app.mapper.CustomerPaymentMapper;
import com.springboot.app.repository.CouponRepository;
import com.springboot.app.repository.CustomerHolidaysRepository;
import com.springboot.app.repository.CustomerPaymentRepository;
import com.springboot.app.repository.CustomerRepository;
import com.springboot.app.repository.CustomerUsedCouponRepository;
import com.springboot.app.repository.ServiceProviderEngagementRepository;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

@Service
public class CustomerPaymentServiceImpl implements CustomerPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerPaymentServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerHolidaysRepository customerHolidaysRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final CustomerPaymentMapper customerPaymentMapper;
    private final CustomerUsedCouponRepository customerUsedCouponRepository;
    private final ServiceProviderEngagementRepository engagementRepository;
    private final CouponRepository couponRepository;

    @Value("${discount.enabled}")
    private boolean isDiscountEnabled;

    // @Value("${discount.range1}")
    // private double discount1to7;

    // @Value("${discount.range2}")
    // private double discount10to15;

    // @Value("${discount.range3}")
    // private double discountAbove15;

    @Value("${discount.rules}")
    private String discountRulesConfig;

    public CustomerPaymentServiceImpl(CustomerRepository customerRepository,
            CustomerHolidaysRepository customerHolidaysRepository,
            CustomerPaymentRepository customerPaymentRepository,
            CustomerPaymentMapper customerPaymentMapper,
            CustomerUsedCouponRepository customerUsedCouponRepository,
            ServiceProviderEngagementRepository engagementRepository,
            CouponRepository couponRepository

    ) {
        this.customerRepository = customerRepository;
        this.customerHolidaysRepository = customerHolidaysRepository;
        this.customerPaymentRepository = customerPaymentRepository;
        this.customerPaymentMapper = customerPaymentMapper;
        this.customerUsedCouponRepository = customerUsedCouponRepository;
        this.engagementRepository = engagementRepository;
        this.couponRepository = couponRepository;

    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPaymentDTO> getPaymentsByCustomerId(Long customerId) {
        logger.info("Fetching payments for customer ID: {}", customerId);
        List<CustomerPayment> payments = customerPaymentRepository.findByCustomer_CustomerId(customerId);

        logger.debug("Fetched {} payments for customer ID: {}", payments.size(), customerId);
        return payments.stream()
                .map(customerPaymentMapper::customerPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerPaymentDTO> getPaymentByCustomerIdAndMonth(Long customerId, LocalDate paymentMonth) {
        logger.info("Fetching payment for customer ID: {} and month: {}", customerId, paymentMonth);
        return customerPaymentRepository.findByCustomer_CustomerIdAndPaymentMonth(customerId, paymentMonth)
                .map(customerPaymentMapper::customerPaymentToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPaymentDTO> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        // if (logger.isInfoEnabled()) {
        logger.info("Fetching customer payments between {} and {}", startDate, endDate);

        List<CustomerPayment> payments = customerPaymentRepository.findByPaymentOnBetween(startDate, endDate);

        if (payments.isEmpty()) {
            logger.info("No customer payments found between {} and {}", startDate, endDate);
            return Collections.emptyList();
        }

        logger.debug("Found {} customer payments between {} and {}", payments.size(), startDate, endDate);

        return payments.stream()
                .map(customerPaymentMapper::customerPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPaymentDTO> getPaymentsByMonthAndYear(int month, int year) {
        logger.info("Fetching customer payments for month: {} and year: {}", month, year);

        List<CustomerPayment> payments = customerPaymentRepository.findByMonthAndYear(month, year);

        if (payments.isEmpty()) {
            logger.info("No customer payments found for month: {} and year: {}", month, year);
            return Collections.emptyList();
        }

        logger.debug("Found {} customer payments for month: {} and year: {}", payments.size(), month, year);

        return payments.stream()
                .map(customerPaymentMapper::customerPaymentToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPaymentDTO> getPaymentsByFinancialYear(int year) {
        logger.info("Fetching customer payments for financial year: {}", year);

        // Define financial year range using java.time.LocalDate
        LocalDate fromDate = LocalDate.of(year, 4, 1); // 1st April of given year
        LocalDate toDate = LocalDate.of(year + 1, 3, 31); // 31st March of next year

        logger.debug("Searching payments from {} to {}", fromDate, toDate);

        List<CustomerPayment> payments = customerPaymentRepository.findByPaymentOnBetween(fromDate, toDate);

        if (payments.isEmpty()) {
            logger.info("No customer payments found for financial year: {}", year);
            return Collections.emptyList();
        }

        logger.debug("Found {} customer payments for financial year: {}", payments.size(), year);

        return payments.stream()
                .map(customerPaymentMapper::customerPaymentToDTO)
                .toList();
    }

    // @Transactional
    // public CustomerPaymentDTO calculateAndSavePayment(Long customerId, double
    // baseAmount,
    // LocalDate startDate_P, LocalDate endDate_P,
    // PaymentMode paymentMode) {

    // // validations...
    // Customer customer = customerRepository.findById(customerId)
    // .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

    // double dailyRate = baseAmount / 30;

    // List<CustomerHolidays> holidays = customerHolidaysRepository
    // .findByCustomer_CustomerIdAndIsActive(customerId, true);

    // int totalVacationDays = holidays.stream()
    // .mapToInt(h -> (int) ChronoUnit.DAYS.between(h.getStartDate(),
    // h.getEndDate()) + 1)
    // .sum();

    // // double discountPercentage = getDiscountPercentage(totalVacationDays);
    // // double discountAmount = (dailyRate * totalVacationDays) *
    // (discountPercentage
    // // / 100);

    // // ✅ Calculate discount only if applicable
    // double discountPercentage = getDiscountPercentage(totalVacationDays);
    // double discountAmount = 0;
    // if (discountPercentage > 0) {
    // discountAmount = (dailyRate * totalVacationDays) * (discountPercentage /
    // 100);
    // }
    // double finalAmount = baseAmount - discountAmount;

    // LocalDate paymentMonth = LocalDate.now().withDayOfMonth(1);
    // LocalDateTime generatedOn = LocalDateTime.now();
    // // Date paymentOn = new Date();
    // // Date paymentOn = new Date(System.currentTimeMillis());
    // LocalDate paymentOn = LocalDate.now();

    // String transactionId = UUID.randomUUID().toString();

    // CustomerPayment payment = new CustomerPayment();
    // payment.setCustomer(customer);
    // payment.setBaseAmount(baseAmount);
    // payment.setDiscountAmount(discountAmount);
    // payment.setFinalAmount(finalAmount);
    // payment.setPaymentMonth(paymentMonth);
    // payment.setStartDate_P(startDate_P);
    // payment.setEndDate_P(endDate_P);
    // payment.setGeneratedOn(generatedOn);
    // payment.setPaymentOn(paymentOn);
    // payment.setTransactionId(transactionId);
    // payment.setPaymentMode(paymentMode);

    // customerPaymentRepository.save(payment);

    // return CustomerPaymentDTO.builder()
    // .id(payment.getId())
    // .customerId(customerId)
    // .baseAmount(baseAmount)
    // .discountAmount(discountAmount)
    // .finalAmount(finalAmount)
    // .paymentMonth(paymentMonth)
    // .startDate_P(startDate_P)
    // .endDate_P(endDate_P)
    // .generatedOn(generatedOn)
    // .paymentOn(paymentOn)
    // .transactionId(transactionId)
    // .paymentMode(paymentMode)
    // .build();
    // }
    // @Transactional
    // public CustomerPaymentDTO calculateAndSavePayment(Long customerId, double
    // baseAmount,
    // LocalDate startDate_P, LocalDate endDate_P,
    // PaymentMode paymentMode, Long couponId, HousekeepingRole serviceType) {

    // Customer customer = customerRepository.findById(customerId)
    // .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

    // double dailyRate = baseAmount / 30;

    // // ✅ Only consider holidays that fall within the payment period
    // List<CustomerHolidays> holidays = customerHolidaysRepository
    // .findByCustomer_CustomerIdAndIsActive(customerId, true).stream()
    // .filter(h -> h.getServiceType() == serviceType)
    // .filter(h -> !(h.getEndDate().isBefore(startDate_P) ||
    // h.getStartDate().isAfter(endDate_P)))
    // .collect(Collectors.toList());

    // int totalVacationDays = holidays.stream()
    // .mapToInt(h -> {
    // LocalDate start = h.getStartDate().isBefore(startDate_P) ? startDate_P :
    // h.getStartDate();
    // LocalDate end = h.getEndDate().isAfter(endDate_P) ? endDate_P :
    // h.getEndDate();
    // return (int) ChronoUnit.DAYS.between(start, end) + 1;
    // })
    // .sum();

    // // ✅ Calculate discount only if applicable

    // double discountPercentage = getDiscountPercentage(totalVacationDays);
    // double discountAmount = 0;
    // if (discountPercentage > 0) {
    // discountAmount = (dailyRate * totalVacationDays) * (discountPercentage /
    // 100);
    // }
    // // ✅ Coupon logic
    // double couponDiscount = 0;
    // Coupon appliedCoupon = null;

    // if (couponId != null) {
    // CustomerCouponId couponKey = new CustomerCouponId(customerId, couponId);
    // CustomerUsedCoupon usedCoupon =
    // customerUsedCouponRepository.findById(couponKey).orElse(null);
    // if (usedCoupon != null) {
    // couponDiscount = usedCoupon.getAvailedAmount();
    // appliedCoupon = usedCoupon.getCoupon();
    // logger.info("Valid coupon applied. Coupon ID: {}, Discount: {}", couponId,
    // couponDiscount);
    // } else {
    // logger.warn("Invalid coupon: No usage found for customerId={} and
    // couponId={}", customerId, couponId);
    // }
    // }

    // // ✅ Combine discounts based on valid conditions
    // double totalDiscountAmount = 0;
    // if (discountAmount > 0 && couponDiscount > 0) {
    // logger.info("Both holiday and coupon discounts applied.");
    // totalDiscountAmount = discountAmount + couponDiscount;
    // } else if (discountAmount > 0) {
    // logger.info("Only holiday discount applied.");
    // totalDiscountAmount = discountAmount;
    // } else if (couponDiscount > 0) {
    // logger.info("Only coupon discount applied.");
    // totalDiscountAmount = couponDiscount;
    // } else {
    // logger.info("No discount applied.");
    // }

    // double finalAmount = baseAmount - totalDiscountAmount;

    // // double finalAmount = baseAmount - discountAmount;

    // LocalDate paymentMonth = LocalDate.now().withDayOfMonth(1);
    // LocalDateTime generatedOn = LocalDateTime.now();
    // LocalDate paymentOn = LocalDate.now();
    // String transactionId = UUID.randomUUID().toString();

    // CustomerPayment payment = new CustomerPayment();
    // payment.setCustomer(customer);
    // payment.setBaseAmount(baseAmount);
    // payment.setDiscountAmount(totalDiscountAmount);
    // payment.setFinalAmount(finalAmount);
    // payment.setPaymentMonth(paymentMonth);
    // payment.setStartDate_P(startDate_P);
    // payment.setEndDate_P(endDate_P);
    // payment.setGeneratedOn(generatedOn);
    // payment.setPaymentOn(paymentOn);
    // payment.setTransactionId(transactionId);
    // payment.setPaymentMode(paymentMode);
    // payment.setCoupon(appliedCoupon); // sets the @ManyToOne relation
    // payment.setCouponDiscountAmount(couponDiscount); // sets the amount (Double)

    // customerPaymentRepository.save(payment);

    // return CustomerPaymentDTO.builder()
    // .id(payment.getId())
    // .customerId(customerId)
    // .baseAmount(baseAmount)
    // .discountAmount(totalDiscountAmount)

    // .couponId(appliedCoupon != null ? appliedCoupon.getId() : null)
    // .couponDiscount(couponDiscount)
    // .finalAmount(finalAmount)
    // .paymentMonth(paymentMonth)
    // .startDate_P(startDate_P)
    // .endDate_P(endDate_P)
    // .generatedOn(generatedOn)
    // .paymentOn(paymentOn)
    // .transactionId(transactionId)
    // .paymentMode(paymentMode)
    // .discountAmount(totalDiscountAmount)
    // .finalAmount(finalAmount)

    // .build();
    // }

    // private double getDiscountPercentage(int days) {
    // String[] rules = discountRulesConfig.split(";");
    // for (String rule : rules) {
    // String[] parts = rule.split(",");
    // int minDays = Integer.parseInt(parts[0]);
    // int maxDays = Integer.parseInt(parts[1]);
    // double percentage = Double.parseDouble(parts[2]);

    // if (days >= minDays && days <= maxDays) {
    // return percentage;
    // }
    // }
    // return 0;

    // }

    // @Transactional(readOnly = true)
    // public CustomerPaymentDTO calculatePayment(Long engagementId, Long
    // customerId, double baseAmount,
    // LocalDate startDate_P, LocalDate endDate_P,
    // PaymentMode paymentMode, Long couponId, HousekeepingRole serviceType) {

    // // ✅ Engagement is mandatory
    // ServiceProviderEngagement engagement =
    // engagementRepository.findById(engagementId)
    // .orElseThrow(() -> new EntityNotFoundException("Engagement not found"));

    // // Customer is optional
    // Customer customer = null;
    // if (customerId != null) {
    // customer = customerRepository.findById(customerId)
    // .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    // }

    // double dailyRate = baseAmount / 30;

    // // ✅ Only consider holidays linked to the engagement or customer (if
    // provided)
    // List<CustomerHolidays> holidays = (customer != null
    // ? customerHolidaysRepository.findByCustomer_CustomerIdAndIsActive(customerId,
    // true)
    // : customerHolidaysRepository.findByEngagement_IdAndIsActive(engagementId,
    // true))
    // .stream()
    // .filter(h -> h.getServiceType() == serviceType)
    // .filter(h -> !(h.getEndDate().isBefore(startDate_P) ||
    // h.getStartDate().isAfter(endDate_P)))
    // .collect(Collectors.toList());

    // int totalVacationDays = holidays.stream()
    // .mapToInt(h -> {
    // LocalDate start = h.getStartDate().isBefore(startDate_P) ? startDate_P :
    // h.getStartDate();
    // LocalDate end = h.getEndDate().isAfter(endDate_P) ? endDate_P :
    // h.getEndDate();
    // return (int) ChronoUnit.DAYS.between(start, end) + 1;
    // })
    // .sum();

    // double discountPercentage = getDiscountPercentage(totalVacationDays);
    // double discountAmount = discountPercentage > 0
    // ? (dailyRate * totalVacationDays) * (discountPercentage / 100)
    // : 0;

    // // ✅ Coupon logic using engagementId instead of customerId
    // double couponDiscount = 0;
    // Coupon appliedCoupon = null;
    // if (couponId != null) {
    // CustomerCouponId couponKey = new CustomerCouponId(engagementId, couponId);
    // CustomerUsedCoupon usedCoupon =
    // customerUsedCouponRepository.findById(couponKey).orElse(null);

    // if (usedCoupon != null) {
    // couponDiscount = usedCoupon.getAvailedAmount();
    // appliedCoupon = usedCoupon.getCoupon();
    // logger.info("✅ Valid coupon applied for engagementId={}, couponId={},
    // discount={}",
    // engagementId, couponId, couponDiscount);
    // } else {
    // logger.warn("❌ Invalid coupon for engagementId={} and couponId={}",
    // engagementId, couponId);
    // }
    // }

    // double totalDiscountAmount = discountAmount + couponDiscount;
    // double finalAmount = baseAmount - totalDiscountAmount;

    // LocalDate paymentMonth = LocalDate.now().withDayOfMonth(1);
    // LocalDateTime generatedOn = LocalDateTime.now();
    // LocalDate paymentOn = LocalDate.now();
    // String transactionId = UUID.randomUUID().toString();

    // return CustomerPaymentDTO.builder()
    // .id(null)
    // .customerId(customerId) // optional
    // .engagementId(engagementId) // mandatory
    // .baseAmount(baseAmount)
    // .discountAmount(totalDiscountAmount)
    // .couponId(appliedCoupon != null ? appliedCoupon.getId() : null)
    // .couponDiscount(couponDiscount)
    // .finalAmount(finalAmount)
    // .paymentMonth(paymentMonth)
    // .startDate_P(startDate_P)
    // .endDate_P(endDate_P)
    // .generatedOn(generatedOn)
    // .paymentOn(paymentOn)
    // .transactionId(transactionId)
    // .paymentMode(paymentMode)
    // .build();
    // }

    @Transactional
    public CustomerPaymentDTO calculatePayment(Long engagementId, Long customerId, double baseAmount,
            LocalDate startDate_P, LocalDate endDate_P,
            PaymentMode paymentMode, Long couponId, HousekeepingRole serviceType) {

        // ✅ Engagement is mandatory
        ServiceProviderEngagement engagement = engagementRepository.findById(engagementId)
                .orElseThrow(() -> new EntityNotFoundException("Engagement not found"));

        // Customer is optional
        Customer customer = null;
        if (customerId != null) {
            customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        }

        double dailyRate = baseAmount / 30;

        // ✅ Calculate holidays
        List<CustomerHolidays> holidays = (customer != null
                ? customerHolidaysRepository.findByCustomer_CustomerIdAndIsActive(customerId, true)
                : customerHolidaysRepository.findByEngagement_IdAndIsActive(engagementId, true))
                .stream()
                .filter(h -> h.getServiceType() == serviceType)
                .filter(h -> !(h.getEndDate().isBefore(startDate_P) || h.getStartDate().isAfter(endDate_P)))
                .toList();

        int totalVacationDays = holidays.stream()
                .mapToInt(h -> {
                    LocalDate start = h.getStartDate().isBefore(startDate_P) ? startDate_P : h.getStartDate();
                    LocalDate end = h.getEndDate().isAfter(endDate_P) ? endDate_P : h.getEndDate();
                    return (int) ChronoUnit.DAYS.between(start, end) + 1;
                })
                .sum();

        double discountPercentage = getDiscountPercentage(totalVacationDays);
        double discountAmount = discountPercentage > 0
                ? (dailyRate * totalVacationDays) * (discountPercentage / 100)
                : 0;

        // ✅ Apply coupon
        double couponDiscount = 0;
        Coupon appliedCoupon = null;
        if (couponId != null) {
            var couponOpt = couponRepository.findById(couponId);
            if (couponOpt.isPresent()) {
                appliedCoupon = couponOpt.get();
                // Calculate discount from coupon
                if (appliedCoupon.getDiscountPercent() != null && appliedCoupon.getDiscountPercent() > 0) {
                    couponDiscount = Math.min(baseAmount * appliedCoupon.getDiscountPercent() / 100,
                            appliedCoupon.getMaxDiscountAmount());
                } else if (appliedCoupon.getFixedDiscountAmount() != null) {
                    couponDiscount = appliedCoupon.getFixedDiscountAmount();
                }

                // ✅ Automatically save used coupon
                CustomerCouponId compoundId = new CustomerCouponId(engagementId, couponId);
                if (!customerUsedCouponRepository.existsById(compoundId)) {
                    CustomerUsedCoupon usedCoupon = new CustomerUsedCoupon();
                    usedCoupon.setId(compoundId);
                    usedCoupon.setEngagement(engagement);
                    usedCoupon.setCoupon(appliedCoupon);
                    usedCoupon.setAvailedAmount((int) couponDiscount);
                    usedCoupon.setAvailedOn(new java.sql.Timestamp(System.currentTimeMillis()));
                    // usedCoupon.getAvailedOn(LocalDateTime.now());
                    customerUsedCouponRepository.save(usedCoupon);
                }
            }
        }

        double totalDiscountAmount = discountAmount + couponDiscount;
        double finalAmount = baseAmount - totalDiscountAmount;

        LocalDate paymentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime generatedOn = LocalDateTime.now();
        LocalDate paymentOn = LocalDate.now();
        String transactionId = UUID.randomUUID().toString();

        return CustomerPaymentDTO.builder()
                .id(null)
                .customerId(customerId)
                .engagementId(engagementId)
                .baseAmount(baseAmount)
                .discountAmount(totalDiscountAmount)
                .couponId(appliedCoupon != null ? appliedCoupon.getId() : null)
                .couponDiscount(couponDiscount)
                .finalAmount(finalAmount)
                .paymentMonth(paymentMonth)
                .startDate_P(startDate_P)
                .endDate_P(endDate_P)
                .generatedOn(generatedOn)
                .paymentOn(paymentOn)
                .transactionId(transactionId)
                .paymentMode(paymentMode)
                .build();
    }

    private double getDiscountPercentage(int days) {
        String[] rules = discountRulesConfig.split(";");
        for (String rule : rules) {
            String[] parts = rule.split(",");
            int minDays = Integer.parseInt(parts[0]);
            int maxDays = Integer.parseInt(parts[1]);
            double percentage = Double.parseDouble(parts[2]);

            if (days >= minDays && days <= maxDays) {
                return percentage;
            }
        }
        return 0;
    }

    // private double getDiscountPercentage(int days) {
    // if (days >= 1 && days <= 7) {
    // return discount1to7;
    // }
    // if (days >= 8 && days <= 15) {
    // return discount8to15;
    // }
    // if (days > 15) {
    // return discountAbove15;
    // }
    // return 0;
    // }

}
