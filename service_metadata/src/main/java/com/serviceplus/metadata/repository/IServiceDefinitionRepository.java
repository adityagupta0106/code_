package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IServiceDefinitionRepository extends JpaRepository<ServiceDefinition, Object>{

	ServiceDefinition findByServiceIdAndUserId(Integer serviceId, Long userId);

	List<ServiceDefinition> findByUserId(Long userId);
	
	@Query("SELECT DISTINCT sd FROM ServiceLog sl JOIN ServiceDefinition sd ON sl.serviceId = sd.serviceId WHERE sl.tenantId = :tenantId AND sl.serviceStatus IN :statuses")
	List<ServiceDefinition> findActiveServicesAndStatus(@Param("tenantId") String tenantId, @Param("statuses") List<Integer> statuses);
	
	public Optional<List<ServiceDefinition>> findByBaseServiceId(Integer baseServiceId);
//    boolean existsByDefinitionJsonNameIgnoreCase(String serviceName);
//
//    boolean existsByDefinitionJsonNameIgnoreCaseAndServiceIdNot(String serviceName,Integer serviceId);
	
	boolean existsByServiceNameIgnoreCase(String serviceName);

	boolean existsByServiceAbbreviation(String serviceAbbreviation);

	boolean existsByServiceNameIgnoreCaseAndServiceIdNot(String serviceName, Integer serviceId);

	boolean existsByServiceAbbreviationAndServiceIdNot(String serviceAbbreviation, Integer serviceId);


    List<ServiceDefinition> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
    
    @Query(value = """
            SELECT sd.definition_json ->> 'registerToAvail'
            FROM schm_sp.service_definition sd
            WHERE sd.service_id = :serviceId
            """, nativeQuery = true)
    String findRegisterToAvailByServiceId(
            @Param("serviceId") Integer serviceId);

	List<ServiceDefinition> findByDepartmentId(Long entityId);

	List<ServiceDefinition> findByServiceIdIn(List<Integer> serviceIds);
	
	@Query("""
		    SELECT sd FROM ServiceDefinition sd
		    WHERE (:state IS NULL OR :state = 0 OR sd.stateId = :state)
		    AND (:dept IS NULL OR sd.departmentId = :dept)
		    AND EXISTS (
		        SELECT 1 FROM ServiceLog sl
		        WHERE sl.serviceId = sd.serviceId
		        AND sl.serviceStatus IN :statuses
		    )
		    """)
		Page<ServiceDefinition> findCataloguePage(
		        @Param("state") Integer state,
		        @Param("dept") Long dept,
		        @Param("statuses") List<Integer> statuses,
		        Pageable pageable
		);
}
