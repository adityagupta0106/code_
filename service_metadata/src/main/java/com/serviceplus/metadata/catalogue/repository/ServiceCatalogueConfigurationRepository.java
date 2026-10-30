package com.serviceplus.metadata.catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.catalogue.entity.ServiceCatalogueConfiguration;

@Repository
public interface ServiceCatalogueConfigurationRepository extends JpaRepository<ServiceCatalogueConfiguration, Long> {

	List<ServiceCatalogueConfiguration> findByServiceDefinerIdOrderByCreatedOnDesc(Long serviceDefinerId);

	List<ServiceCatalogueConfiguration> findByServiceDefinerIdAndStatusOrderByCreatedOnDesc(Long serviceDefinerId,
			Integer status);

	Optional<ServiceCatalogueConfiguration> findByIdAndServiceDefinerId(Long id, Long serviceDefinerId);

	boolean existsByConfigurationCode(String configurationCode);
}