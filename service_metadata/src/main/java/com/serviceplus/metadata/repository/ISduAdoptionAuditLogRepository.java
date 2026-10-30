package com.serviceplus.metadata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.SduAdoptionAuditLog;

@Repository
public interface ISduAdoptionAuditLogRepository extends JpaRepository<SduAdoptionAuditLog, Long> {

    List<SduAdoptionAuditLog> findByTenantIdAndServiceIdOrderByPerformedOnDesc(String tenantId, Integer serviceId);

    List<SduAdoptionAuditLog> findByTenantIdAndServiceIdAndSubmissionLocationIdOrderByPerformedOnDesc(
            String tenantId, Integer serviceId, Integer submissionLocationId);

    List<SduAdoptionAuditLog> findByTenantIdAndServiceIdAndSduLocationIdOrderByPerformedOnDesc(
            String tenantId, Integer serviceId, Integer sduLocationId);
}
