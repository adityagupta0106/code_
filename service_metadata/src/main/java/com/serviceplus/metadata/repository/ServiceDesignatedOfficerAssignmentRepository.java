package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignment;

@Repository
public interface ServiceDesignatedOfficerAssignmentRepository
        extends JpaRepository<ServiceDesignatedOfficerAssignment, Long> {

    void deleteByTenantIdAndServiceIdAndDeliveryUnitLevelId(String tenantId,Integer serviceId,
            Integer deliveryUnitLevelId);

    List<ServiceDesignatedOfficerAssignment> findByTenantIdAndServiceId(String tenantId,
            Integer serviceId);

	Optional<ServiceDesignatedOfficerAssignment> findFirstByTenantIdAndServiceIdAndDeliveryUnitLevelId(String tenantId,
			Integer serviceId, Integer deliveryUnitLevelId);

	@Query("""
			select distinct s.serviceId
			from ServiceDesignatedOfficerAssignment s
			where s.tenantId = :tenantId
			  and s.userId = :userId
			  and s.deletedFlag = false
			""")
	List<Integer> findAssignedServiceIds(@Param("tenantId") String tenantId, @Param("userId") Long userId);

	@Query("""
			select distinct s.deliveryUnitLevelId
			from ServiceDesignatedOfficerAssignment s
			where s.tenantId = :tenantId
			and s.serviceId = :serviceId
			and s.userId = :userId
			and s.deletedFlag = false
			""")
	List<Integer> findAssignedDeliveryUnitLevels(@Param("tenantId") String tenantId,
			@Param("serviceId") Integer serviceId, @Param("userId") Long userId);


	List<ServiceDesignatedOfficerAssignment> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndUserIdAndDeletedFlagFalse(
			String tenantId, Integer serviceId, Integer deliveryUnitLevelId, Long userId);

	List<ServiceDesignatedOfficerAssignment> findByServiceId(Integer serviceId);

	void deleteByServiceId(Integer serviceId);
}
