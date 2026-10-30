package com.serviceplus.tracking.correlationmapping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSet;

@Repository
public interface ServiceCorrelationSetRepository extends JpaRepository<ServiceCorrelationSet, Long> {

	List<ServiceCorrelationSet> findByServiceIdAndTenantIdAndEntityId(Long serviceId, String tenantId,
			Integer entityId);

	List<ServiceCorrelationSet> findByServiceIdAndSourceTypeAndAttrSource(Long serviceId, String sourceType,
			String attrSource);

	List<ServiceCorrelationSet> findByServiceIdAndSetId(Long serviceId, Long setId);

	List<ServiceCorrelationSet> findByBaseServiceIdAndSetIdAndSourceType(Long baseServiceId, Long setId,
			String sourceType);
}