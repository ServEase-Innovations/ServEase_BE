package com.springboot.app.repository;

import com.springboot.app.entity.CustomerHolidays;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerHolidaysRepository extends JpaRepository<CustomerHolidays, Long> {

    List<CustomerHolidays> findByCustomer_CustomerIdAndIsActive(Long customerId, boolean b);

}
