package com.springboot.app.task;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.entity.ServiceProviderPayment;
import com.springboot.app.enums.PaymentMode;
import com.springboot.app.repository.ServiceProviderEngagementRepository;
import com.springboot.app.repository.ServiceProviderPaymentRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthlyPaymentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyPaymentScheduler.class);

    @Autowired
    private ServiceProviderEngagementRepository engagementRepository;

    @Autowired
    private ServiceProviderPaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 0 0 L * ?") // Run on last day of every month
    public void processMonthlyPayments() {
        logger.info("Monthly payment processing started.");

        List<Long> failedList = new ArrayList<>();

        try {
            // Fetching all ServiceProviderEngagements
            List<ServiceProviderEngagement> engagements = engagementRepository.findAll();

            for (ServiceProviderEngagement engagement : engagements) {
                try {
                    // Get the last payment date if exists
                    ServiceProviderPayment lastPayment = getLastPayment(engagement);
                    LocalDate startDate = (lastPayment != null) ? lastPayment.getEndDate().toLocalDate().plusDays(1)
                            : engagement.getStartDate();

                    LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

                    ServiceProviderPayment payment = calculatePayment(engagement, startDate, endDate);
                    paymentRepository.save(payment);

                    logger.info("Processed payment for engagement ID: {}", engagement.getId());
                } catch (Exception e) {
                    logger.error("Failed to process payment for engagement ID: {}", engagement.getId(), e);
                    failedList.add(engagement.getId());
                }
            }

            if (!failedList.isEmpty()) {
                logger.warn("Payment processing completed with errors. Failed engagements: {}", failedList);
            } else {
                logger.info("Monthly payment processing completed successfully without errors.");
            }
        } catch (Exception e) {
            logger.error("Error during monthly payment processing.", e);
        }
    }

    private ServiceProviderPayment calculatePayment(ServiceProviderEngagement engagement, LocalDate startDate,
            LocalDate endDate) {
        long noOfDays = (ChronoUnit.DAYS.between(startDate, endDate)) + 1;
        int monthlyAmount = (int) engagement.getMonthlyAmount();
        int daysInMonth = endDate.lengthOfMonth();
        int calculatedAmount = (int) ((double) monthlyAmount / daysInMonth * noOfDays);

        ServiceProviderPayment payment = new ServiceProviderPayment();
        payment.setServiceProvider(engagement.getServiceProvider());
        payment.setCustomer(engagement.getCustomer());
        payment.setStartDate(Date.valueOf(startDate));
        payment.setEndDate(Date.valueOf(endDate));
        payment.setNoOfDays(noOfDays);
        payment.setAmount(calculatedAmount);
        payment.setPaymentMode(engagement.getPaymentMode() != null ? engagement.getPaymentMode() : PaymentMode.CASH);
        payment.setMonth(endDate.getMonthValue());
        payment.setYear(endDate.getYear());
        payment.setMonthlyAmount(monthlyAmount);

        return payment;
    }

    private ServiceProviderPayment getLastPayment(ServiceProviderEngagement engagement) {
        // Create the JPQL query
        TypedQuery<ServiceProviderPayment> query = entityManager.createQuery(
                "FROM ServiceProviderPayment sp " +
                        "WHERE sp.serviceProvider.id = :spId AND sp.customer.id = :cId " +
                        "ORDER BY sp.endDate DESC",
                ServiceProviderPayment.class);

        // Set parameters
        query.setParameter("spId", engagement.getServiceProvider().getServiceproviderId());
        query.setParameter("cId", engagement.getCustomer().getCustomerId());

        // Limit result to one and return
        query.setMaxResults(1);

        // Fetch the result
        return query.getResultStream().findFirst().orElse(null);
    }
}
