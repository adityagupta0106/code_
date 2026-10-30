package com.serviceplus.tracking.correlationmapping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSetOrder;

@Repository
public interface ServiceCorrelationSetOrderRepository extends JpaRepository<ServiceCorrelationSetOrder, Long> {

	List<ServiceCorrelationSetOrder> findByServiceIdAndTenantIdAndEntityId(Long serviceId, String tenantId,
			Integer entityId);

	List<ServiceCorrelationSetOrder> findByBaseServiceIdAndServiceIdAndTenantIdAndEntityIdOrderBySetIdAscAttrSortOrderAsc(
			Long baseServiceId, Long serviceId, String tenantId, Integer entityId);

	List<ServiceCorrelationSetOrder> findByServiceIdAndSetId(Long serviceId, Long setId);
}