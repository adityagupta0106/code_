package com.serviceplus.tracking.correlationmapping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationMapping;

@Repository
public interface ServiceCorrelationMappingRepository extends JpaRepository<ServiceCorrelationMapping, Long> {

	long deleteByServiceIdAndTaskIdAndCombinationIdAndTenantIdAndEntityId(Long serviceId, String taskId,
			Long combinationId, String tenantId, Integer entityId);
	List<ServiceCorrelationMapping> findByServiceIdAndTaskIdOrderByPriorityAsc(Long serviceId, String taskId);
}
