package com.serviceplus.metadata.pattern.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.pattern.entity.ServicePatternConfiguration;

@Repository
public interface ServicePatternConfigurationRepository extends JpaRepository<ServicePatternConfiguration, Long> {

	Optional<ServicePatternConfiguration> findByServiceIdAndPatternId(Long serviceId, Long patternId);

	Optional<List<ServicePatternConfiguration>> findByServiceId(Long serviceId);

	List<ServicePatternConfiguration> findAllByServiceId(Long serviceId);

	List<ServicePatternConfiguration> findAllByTenantId(String tenantId);

	List<ServicePatternConfiguration> findAllByServiceIdAndTenantId(Long serviceId, String tenantId);

	boolean existsByServiceIdAndPatternId(Long serviceId, Long patternId);

	boolean existsByServiceIdAndPatternIdAndIdNot(Long serviceId, Long patternId, Long id);

	List<ServicePatternConfiguration> findAllByPatternId(Long patternId);
}
