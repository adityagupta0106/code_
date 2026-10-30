package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.ApplicationTrackingHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ApplicationTrackingHolderRepository extends JpaRepository<ApplicationTrackingHolder, Long> {

    List<ApplicationTrackingHolder> findByHolderIdInAndTenantId(Collection<String> holderIds, String tenantId);

    boolean existsByProcessIdAndHolderIdInAndTenantId(String processId, Set<String> holderIdStrings, String tenantId);

    List<ApplicationTrackingHolder> findByProcessIdAndTenantId(String processId, String tenantId);
}
