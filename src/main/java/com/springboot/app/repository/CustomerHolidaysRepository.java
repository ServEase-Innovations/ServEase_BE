package com.springboot.app.repository;

import com.springboot.app.entity.CustomerHolidays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerHolidaysRepository extends JpaRepository<CustomerHolidays, Long> {

        // Existing methods
        List<CustomerHolidays> findByCustomer_CustomerIdAndIsActive(Long customerId, boolean isActive);

        List<CustomerHolidays> findByCustomer_CustomerId(Long customerId);

        @Query("SELECT ch.customer.customerId FROM CustomerHolidays ch " +
                        "WHERE ch.isActive = true AND :startDate >= ch.startDate AND :endDate <= ch.endDate")
        List<Long> findCustomerIdsOnHolidayBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ✅ New methods for engagement
        List<CustomerHolidays> findByEngagement_IdAndIsActive(Long engagementId, boolean isActive);

        List<CustomerHolidays> findByEngagement_Id(Long engagementId);
}
