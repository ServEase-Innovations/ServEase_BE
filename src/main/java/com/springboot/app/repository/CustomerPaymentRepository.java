package com.springboot.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.app.entity.CustomerPayment;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    Optional<CustomerPayment> findByCustomer_CustomerIdAndPaymentMonth(Long customerId, LocalDate paymentMonth);

    List<CustomerPayment> findByCustomer_CustomerId(Long customerId);

    // List<CustomerPayment> findByPaymentOnBetween(LocalDate from, LocalDate to);

    // List<CustomerPayment> findByPaymentOnBetween(Date startDate, Date endDate);

    @Query("SELECT cp FROM CustomerPayment cp WHERE MONTH(cp.paymentOn) = :month AND YEAR(cp.paymentOn) = :year")
    List<CustomerPayment> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // @Query("SELECT cp FROM CustomerPayment cp WHERE MONTH(cp.paymentOn) = :month
    // AND YEAR(cp.paymentOn) = :year")
    // List<CustomerPayment> findByMonthAndYear(@Param("month") int month,
    // @Param("year") int year);
    List<CustomerPayment> findByPaymentOnBetween(LocalDate from, LocalDate to);

}
