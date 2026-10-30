package com.serviceplus.tracking.correlationmapping.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationapplication.entity.ApplicationSetOrderAttrData;

@Repository
public interface ApplicationSetOrderAttrDataRepository extends JpaRepository<ApplicationSetOrderAttrData, Long> {

	Optional<ApplicationSetOrderAttrData> findByApplicationIdAndServiceIdAndSetIdAndTenantId(String applicationId,
			Long serviceId, Long setId, String tenantId);

	Optional<ApplicationSetOrderAttrData> findByApplicationIdAndSetId(String applicationId, Long setId);
}