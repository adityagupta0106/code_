package com.serviceplus.tracking.correlationmapping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationAttrMapping;

@Repository
public interface ServiceCorrelationAttrMappingRepository extends JpaRepository<ServiceCorrelationAttrMapping, Long> {

	long deleteByServiceIdAndTaskIdAndCombinationIdAndTenantIdAndEntityId(Long serviceId, String taskId,
			Long combinationId, String tenantId, Integer entityId);

	List<ServiceCorrelationAttrMapping> findByServiceIdAndTaskIdAndMappedBaseServiceIdAndMappedSetId(Long serviceId,
			String taskId, Long mappedBaseServiceId, Long mappedSetId);
}