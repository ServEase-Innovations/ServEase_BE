package com.springboot.app.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.app.repository.ServiceProviderEngagementRepository;

import java.time.LocalDate;

@Service
public class ServiceProviderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderScheduler.class);

    private final ServiceProviderEngagementRepository engagementRepository;

    public ServiceProviderScheduler(ServiceProviderEngagementRepository engagementRepository) {
        this.engagementRepository = engagementRepository;
    }

    @Scheduled(fixedDelay = 60000) // Runs every minute
    @Transactional
    public void updateServiceProviderTimeslots() {
        String inactiveTimeslot = "00:00-00:00";

        logger.info("Updating engagements with timeslot: {} and date: {}", inactiveTimeslot, LocalDate.now());

        engagementRepository.bulkUpdateEndedEngagements(inactiveTimeslot, LocalDate.now());

        logger.info("Engagements updated successfully");
    }
}
