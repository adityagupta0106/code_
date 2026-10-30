package com.serviceplus.metadata.correlationConfig.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfiguration;

@Repository
public interface ICorrelationConfigurationRepository extends JpaRepository<CorrelationConfiguration, Integer> {

	@Query("""
			SELECT c
			FROM CorrelationConfiguration c
			WHERE c.tenantId = :tenantId AND c.setType = :setType AND c.serviceId = :serviceId
			""")
	Page<CorrelationConfiguration> findSetNamesByTenantIdAndSetType(@Param("tenantId") String tenantId,
			@Param("setType") String setType,@Param("serviceId") Integer serviceId, Pageable pageable);

	Optional<List<CorrelationConfiguration>> findByEntityIdAndTenantId(Integer entityId, String tenantId);

	Optional<CorrelationConfiguration> findByServiceIdAndSetName(Integer serviceId, String setName);

	Optional<List<CorrelationConfiguration>> findByTenantId(String tenantId);

	Optional<List<CorrelationConfiguration>> findByServiceId(Integer requestServiceId);

	Optional<List<CorrelationConfiguration>> findByTenantIdAndServiceId(String tenantId, Integer serviceId);

	void deleteByServiceId(Integer serviceId);
}
