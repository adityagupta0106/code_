package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.ApplicationTracking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationTrackingRepository extends JpaRepository<ApplicationTracking, String> ,
        JpaSpecificationExecutor<ApplicationTracking> {

    Optional<ApplicationTracking> findByProcessIdAndTenantId(String processId, String tenantId);

    List<ApplicationTracking> findByApplIdAndTenantIdOrderByCreatedOnAsc(String applId, String tenantId);

    List<ApplicationTracking> findByApplIdAndHolderIdInAndTenantIdOrderByProcessIdAsc(String applId, Collection<String> holderIds, String tenantId);

    List<ApplicationTracking> findByApplRefNoAndHolderIdInAndTenantIdOrderByProcessIdAsc(String applRefNo, Collection<String> holderIds, String tenantId);

    List<ApplicationTracking> findByApplIdAndAppliedUserIdAndTenantIdOrderByProcessIdAsc(String applId, Long appliedUserId, String tenantId);

    List<ApplicationTracking> findByApplRefNoAndAppliedUserIdAndTenantIdOrderByProcessIdAsc(String applRefNo, Long appliedUserId, String tenantId);

    List<ApplicationTracking> findByApplRefNoAndApplyDateBetweenAndTenantIdOrderByProcessIdAsc(String applRefNo, LocalDateTime start, LocalDateTime end, String tenantId);

    List<ApplicationTracking> findByApplRefNoAndApplyDateAndTenantIdOrderByProcessIdAsc(String applRefNo, LocalDateTime applyDate, String tenantId);

    List<ApplicationTracking> findByApplIdAndProcessIdInAndTenantIdOrderByProcessIdAsc(String applId, Collection<String> processIds, String tenantId);

    List<ApplicationTracking> findByApplRefNoAndProcessIdInAndTenantIdOrderByProcessIdAsc(String applRefNo, Collection<String> processIds, String tenantId);

    List<ApplicationTracking> findByApplIdAndTenantIdOrderByProcessIdAsc(String applId, String tenantId);

    List<ApplicationTracking> findByApplRefNoAndTenantIdOrderByProcessIdAsc(String applRefNo, String tenantId);

    List<ApplicationTracking> findByAppliedUserIdAndTenantId(Long userId, String tenantId);

    Page<ApplicationTracking> findByAppliedUserIdAndTenantIdOrderByApplyDateDesc(Long userId, String tenantId, Pageable pageable);

	Optional<ApplicationTracking> findFirstByApplIdAndTenantIdOrderByCreatedOnDesc(String applicationId,
			String tenantId);

	Optional<ApplicationTracking> findFirstByApplIdAndTaskIdAndTenantIdOrderByCreatedOnDesc(String applicationId,
			String string, String tenantId);
}
