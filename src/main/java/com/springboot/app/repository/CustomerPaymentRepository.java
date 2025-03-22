package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.app.entity.CustomerPayment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    Optional<CustomerPayment> findByCustomer_CustomerIdAndPaymentMonth(Long customerId, LocalDate paymentMonth);

    List<CustomerPayment> findByCustomer_CustomerId(Long customerId);
}
