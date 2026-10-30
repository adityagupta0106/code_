package com.serviceplus.metadata.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.PendingAdoptionApplication;

@Repository
public interface IPendingAdoptionApplicationRepository extends JpaRepository<PendingAdoptionApplication, String> {

    Optional<PendingAdoptionApplication> findByApplicationIdAndTenantIdAndDeletedFlagFalse(
            String applicationId, String tenantId);

    List<PendingAdoptionApplication> findByTenantIdAndServiceIdAndSubmissionLocationIdAndStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer submissionLocationId, String status);

    List<PendingAdoptionApplication> findByTenantIdAndServiceIdAndSduLocationIdAndStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer sduLocationId, String status);

    long countByTenantIdAndServiceIdAndSubmissionLocationIdAndStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer submissionLocationId, String status);

    long countByTenantIdAndServiceIdAndStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, String status);

    List<PendingAdoptionApplication> findByTenantIdAndStatusAndDeletedFlagFalse(
            String tenantId, String status);

    @Query("""
            select p from PendingAdoptionApplication p
            where p.status = 'PENDING_ADOPTION'
              and p.deletedFlag = false
              and (p.lastReminderDate is null or p.lastReminderDate < :thresholdDate)
            """)
    List<PendingAdoptionApplication> findPendingApplicationsDueForReminder(@Param("thresholdDate") Date thresholdDate);

    @Query("""
            select p.serviceId as serviceId,
                   p.sduLocationId as sduLocationId,
                   p.submissionLocationId as submissionLocationId,
                   count(p.applicationId) as pendingCount,
                   min(p.submissionDate) as oldestSubmissionDate,
                   max(p.submissionDate) as latestSubmissionDate
            from PendingAdoptionApplication p
            where p.tenantId = :tenantId
              and p.serviceId = :serviceId
              and p.status = 'PENDING_ADOPTION'
              and p.deletedFlag = false
            group by p.serviceId, p.sduLocationId, p.submissionLocationId
            """)
    List<Object[]> findPendingSummaryByService(
            @Param("tenantId") String tenantId,
            @Param("serviceId") Integer serviceId);

    List<PendingAdoptionApplication> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
