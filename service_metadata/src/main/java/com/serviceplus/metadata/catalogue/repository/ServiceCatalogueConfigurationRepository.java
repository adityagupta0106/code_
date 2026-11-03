package com.serviceplus.metadata.catalogue.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT c.data FROM ServiceCatalogueConfiguration c WHERE c.id = :id")
    JsonNode findDataByConfigurationId(@Param("id") Long id);
}