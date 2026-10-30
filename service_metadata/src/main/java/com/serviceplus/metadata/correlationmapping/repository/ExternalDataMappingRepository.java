package com.serviceplus.metadata.correlationmapping.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.correlationmapping.entity.ExternalDataMapping;

@Repository
public interface ExternalDataMappingRepository extends JpaRepository<ExternalDataMapping, Integer> {

	Optional<List<ExternalDataMapping>> findByTenantIdAndEntityIdAndCrBy(String tenantId, Integer entityId, Long crBy);

	@Query(value = """
			SELECT EXISTS (
			    SELECT 1
			    FROM schm_sp.external_data_mapping edm,
			         json_array_elements(edm.mapping_data_json -> 'dataSetSources') AS source
			    WHERE source ->> 'dataSetId' = CAST(:dataSetId AS TEXT)
			)
			""", nativeQuery = true)
	boolean existsByDataSetId(@Param("dataSetId") Integer dataSetId);
	
	Optional<List<ExternalDataMapping>> findByTenantIdAndEntityId(String tenantId, Integer entityId);
}