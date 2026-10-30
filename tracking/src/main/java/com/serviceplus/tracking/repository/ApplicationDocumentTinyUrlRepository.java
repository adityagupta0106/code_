package com.serviceplus.tracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.ApplicationDocumentTinyUrl;

@Repository
public interface ApplicationDocumentTinyUrlRepository extends JpaRepository<ApplicationDocumentTinyUrl, Long> {

	Optional<ApplicationDocumentTinyUrl> findByUrlKeyAndTenantId(String urlKey, String tenantId);
}
