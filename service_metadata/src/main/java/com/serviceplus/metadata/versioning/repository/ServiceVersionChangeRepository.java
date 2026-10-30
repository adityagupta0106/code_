package com.serviceplus.metadata.versioning.repository;

import com.serviceplus.metadata.versioning.entity.ServiceVersionChange;
import com.serviceplus.metadata.versioning.enums.ModificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceVersionChangeRepository extends JpaRepository<ServiceVersionChange, Long> {

    Optional<ServiceVersionChange> findByModificationToken(String modificationToken);

    Optional<ServiceVersionChange> findByServiceIdAndStatus(Integer serviceId, ModificationStatus status);

    Optional<ServiceVersionChange> findByTargetServiceIdAndStatus(Integer targetServiceId, ModificationStatus status);

    Optional<ServiceVersionChange> findByIdAndUserId(Long id, Long userId);

    ServiceVersionChange findByServiceIdAndTenantId(Integer serviceId, String tenantId);

    ServiceVersionChange findByTargetServiceIdAndStatusAndTenantId(Integer serviceId,ModificationStatus status, String tenantId);
}