package com.springboot.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.app.entity.ServiceProviderEngagement;
import com.springboot.app.enums.HousekeepingRole;
import com.springboot.app.enums.UserRole;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ServiceProviderEngagementRepository extends JpaRepository<ServiceProviderEngagement, Long> {
       List<ServiceProviderEngagement> findByEndDateBeforeAndTimeslotNot(LocalDate date, String timeslot);

       List<ServiceProviderEngagement> findByEndDateBeforeAndIsActive(LocalDate date, boolean isActive);

       Optional<ServiceProviderEngagement> findFirstByCustomer_CustomerIdAndIsActiveTrue(Long customerId);

       // Bulk update: Set timeslot to "00:00-00:00" and deactivate engagements that
       // ended before today
       @Modifying
       @Transactional
       @Query("UPDATE ServiceProviderEngagement e SET e.timeslot = :timeslot " +
                     "WHERE e.endDate < :today")
       void bulkUpdateEndedEngagements(@Param("timeslot") String timeslot, @Param("today") LocalDate today);

       // @Query("SELECT e FROM ServiceProviderEngagement e " +
       // "WHERE e.startDate = :startDate " +
       // "AND e.endDate = :endDate " +
       // "AND LOWER(e.timeslot) = LOWER(:timeslot) " +
       // "AND e.housekeepingRole = :housekeepingRole")
       // List<ServiceProviderEngagement> findByExactDateTimeslotAndHousekeepingRole(
       // @Param("startDate") LocalDate startDate,
       // @Param("endDate") LocalDate endDate,
       // @Param("timeslot") String timeslot,
       // @Param("housekeepingRole") HousekeepingRole housekeepingRole);

       @Query("SELECT e FROM ServiceProviderEngagement e " +
                     "WHERE e.startDate = :startDate " +
                     "AND e.endDate = :endDate " +
                     "AND e.housekeepingRole = :housekeepingRole")
       List<ServiceProviderEngagement> findByDateAndHousekeepingRole(
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("housekeepingRole") HousekeepingRole housekeepingRole);

       // @Query("SELECT e FROM ServiceProviderEngagement e WHERE
       // e.serviceProvider.serviceproviderId = :serviceProviderId "
       // +
       // "AND e.startDate <= :endDate AND (e.endDate IS NULL OR e.endDate >=
       // :startDate)")
       // List<ServiceProviderEngagement> findByServiceProviderAndDateRange(
       // @Param("serviceProviderId") Long serviceProviderId,
       // @Param("startDate") LocalDate startDate,
       // @Param("endDate") LocalDate endDate);

       // @Query("SELECT e FROM ServiceProviderEngagement e " +
       // "WHERE e.customer.customerId IN :customerIds AND " +
       // "e.housekeepingRole = :role")
       // List<ServiceProviderEngagement> findEngagementsByCustomerIdsAndRole(
       // @Param("customerIds") List<Long> customerIds,
       // @Param("role") HousekeepingRole role);

       // NEWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
       // FETCH engaged providers who are nearby, in date range and role
       @Query("SELECT e FROM ServiceProviderEngagement e " +
                     "JOIN e.serviceProvider sp " +
                     "WHERE e.startDate = :startDate " +
                     "AND e.endDate = :endDate " +
                     "AND e.housekeepingRole = :housekeepingRole " +
                     "AND (sp.geoHash5 IN :geoHashes OR sp.geoHash6 IN :geoHashes OR sp.geoHash7 IN :geoHashes)")
       List<ServiceProviderEngagement> findEngagedProvidersNearby(
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("housekeepingRole") HousekeepingRole housekeepingRole,
                     @Param("geoHashes") List<String> geoHashes);

       @Query("SELECT e FROM ServiceProviderEngagement e WHERE e.customer.customerId IN :customerIds AND e.housekeepingRole = :role")
       List<ServiceProviderEngagement> findEngagementsByCustomerIdsAndRole(
                     @Param("customerIds") List<Long> customerIds,
                     @Param("role") HousekeepingRole role);

       @Query("SELECT e FROM ServiceProviderEngagement e WHERE e.serviceProvider.serviceproviderId = :serviceProviderId "
                     +
                     "AND e.startDate <= :endDate AND (e.endDate IS NULL OR e.endDate >= :startDate)")
       List<ServiceProviderEngagement> findByServiceProviderAndDateRange(
                     @Param("serviceProviderId") Long serviceProviderId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

       List<ServiceProviderEngagement> findAllByCustomer_CustomerIdAndServiceTypeAndIsActiveTrue(
                     Long customerId,
                     HousekeepingRole serviceType);

}
