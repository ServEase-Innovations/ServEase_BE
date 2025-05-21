package com.springboot.app.repository;

import com.springboot.app.entity.ServiceProviderPayment;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderPaymentRepository extends JpaRepository<ServiceProviderPayment, Long> {

    List<ServiceProviderPayment> findByPaymentOnBetween(Date startDate, Date endDate);

    List<ServiceProviderPayment> findByMonthAndYear(int month, int year);

}
