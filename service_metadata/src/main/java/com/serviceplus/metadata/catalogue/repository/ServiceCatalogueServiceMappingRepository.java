package com.serviceplus.metadata.catalogue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.catalogue.entity.ServiceCatalogueServiceMapping;

@Repository
public interface ServiceCatalogueServiceMappingRepository extends JpaRepository<ServiceCatalogueServiceMapping, Long> {

	List<ServiceCatalogueServiceMapping> findByConfigurationId(Long configurationId);

	Optional<ServiceCatalogueServiceMapping> findByServiceId(Long serviceId);

	boolean existsByServiceId(Long serviceId);

	void deleteByConfigurationId(Long configurationId);

	List<ServiceCatalogueServiceMapping> findByServiceIdIn(List<Long> serviceIdList);
}
