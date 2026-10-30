package com.serviceplus.tracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.ApplicationActionTinyUrl;

@Repository
public interface ApplicationActionTinyUrlRepository
        extends JpaRepository<ApplicationActionTinyUrl, Long> {

    Optional<ApplicationActionTinyUrl> findByUrlKeyAndTenantId(
            String urlKey,
            String tenantId);

    Optional<ApplicationActionTinyUrl> findByApplicationIdAndTenantId(
            String applicationId,
            String tenantId);

	Optional<ApplicationActionTinyUrl> findByUrlKey(String urlKey);
}
