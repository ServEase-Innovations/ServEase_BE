package com.cus.customertab.task;

import com.cus.customertab.entity.ServiceProviderEngagement;
import com.cus.customertab.entity.ServiceProviderPayment;
import com.cus.customertab.enums.PaymentMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthlyPaymentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyPaymentScheduler.class);

    private final SessionFactory sessionFactory;

    @Scheduled(cron = "0 0 0 L * ?") // Run on last day of every month
    public void processMonthlyPayments() {
        logger.info("Monthly payment processing started.");

        Transaction transaction = null;
        List<Long> failedList = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Fetching all ServiceProviderEngagements
            List<ServiceProviderEngagement> engagements = session
                    .createQuery("from ServiceProviderEngagement", ServiceProviderEngagement.class).list();

            for (ServiceProviderEngagement engagement : engagements) {
                try {
                    // Get the last payment date if exists
                    ServiceProviderPayment lastPayment = getLastPayment(session, engagement);
                    LocalDate startDate = (lastPayment != null) ? lastPayment.getEndDate().toLocalDate().plusDays(1)
                            : engagement.getStartDate().toLocalDate();

                    LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

                    ServiceProviderPayment payment = calculatePayment(engagement, startDate, endDate);
                    session.persist(payment);

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
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error during monthly payment processing. Transaction rolled back.", e);
        }
    }

    private ServiceProviderPayment calculatePayment(ServiceProviderEngagement engagement, LocalDate startDate,
            LocalDate endDate) {
        long noOfDays = (ChronoUnit.DAYS.between(startDate, endDate)) + 1 ;
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

    private ServiceProviderPayment getLastPayment(Session session, ServiceProviderEngagement engagement) {
        return session.createQuery(
                "from ServiceProviderPayment where serviceProvider.id = :spId and customer.id = :cId order by endDate desc",
                ServiceProviderPayment.class)
                .setParameter("spId", engagement.getServiceProvider().getServiceproviderId())
                .setParameter("cId", engagement.getCustomer().getCustomerId())
                .setMaxResults(1)
                .uniqueResult();
    }
}