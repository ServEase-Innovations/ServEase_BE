package com.springboot.app.repository;

import com.springboot.app.entity.CustomerHolidays;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerHolidaysRepository extends JpaRepository<CustomerHolidays, Long> {

        List<CustomerHolidays> findByCustomer_CustomerIdAndIsActive(Long customerId, boolean b);

        @Query("SELECT ch.customer.customerId FROM CustomerHolidays ch " +
                        "WHERE ch.isActive = true AND " +
                        ":startDate >= ch.startDate AND :endDate <= ch.endDate")
        List<Long> findCustomerIdsOnHolidayBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        List<CustomerHolidays> findByCustomer_CustomerId(Long customerId);

}
