package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.ApplicantInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApplicantInboxRepository extends JpaRepository<ApplicantInbox, Long>, JpaSpecificationExecutor<ApplicantInbox> {

    Optional<ApplicantInbox> findByCurrentProcessIdAndTenantId(String currentProcessId, String tenantId);

    List<ApplicantInbox> findByApplicationIdInAndTenantId(List<String> applicationIds, String tenantId);

    long countByAppliedByAndTenantIdAndStatus(Long userId, String tenantId, String pending);

    Optional<ApplicantInbox> findByApplicationIdAndCurrentProcessIdAndTenantId(String applId, String processId, String tenantId);

	Optional<ApplicantInbox> findByApplicationIdAndTenantId(String applicationId, String tenantId);
}
