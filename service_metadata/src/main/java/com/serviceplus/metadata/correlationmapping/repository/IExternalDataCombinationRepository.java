package com.serviceplus.metadata.correlationmapping.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.correlationmapping.entity.ExternalDataCombination;

@Repository
public interface IExternalDataCombinationRepository extends JpaRepository<ExternalDataCombination, Long> {

	Optional<ExternalDataCombination> findByIdAndTenantIdAndEntityId(Long id, String tenantId, Integer entityId);

	Optional<List<ExternalDataCombination>> findByTenantIdAndEntityIdAndCrBy(String tenantId, Integer entityId,
			Long crBy);

	Optional<List<ExternalDataCombination>> findByTenantIdAndEntityId(String tenantId, Integer entityId);
}