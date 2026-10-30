package com.serviceplus.metadata.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceDeliveryUnits;

@Repository
public interface ServiceDeliveryUnitsRepository
        extends JpaRepository<ServiceDeliveryUnits, Long> {

    Optional<ServiceDeliveryUnits> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
            String tenantId,
            Integer serviceId,
            Integer deliveryUnitLevelId,
            Integer locationId);

    List<ServiceDeliveryUnits> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndDeletedFlagFalse(
            String tenantId,
            Integer serviceId,
            Integer deliveryUnitLevelId);

    List<ServiceDeliveryUnits> findByTenantIdAndServiceIdAndDeletedFlagFalse(
            String tenantId,
            Integer serviceId);

    List<ServiceDeliveryUnits> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndActivationStatusAndDeletedFlagFalse(
            String tenantId,
            Integer serviceId,
            Integer deliveryUnitLevelId,
            Integer activationStatus);

    Page<ServiceDeliveryUnits> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndDeletedFlagFalse(
            String tenantId,
            Integer serviceId,
            Integer deliveryUnitLevelId,
            Pageable pageable);

    @Modifying
    @Query("""
            update ServiceDeliveryUnits s
            set s.activationStatus = :activationStatus,
                s.modifiedBy = :modifiedBy,
                s.modifiedOn = :modifiedOn
            where s.tenantId = :tenantId
              and s.serviceId = :serviceId
              and s.deliveryUnitLevelId = :deliveryUnitLevelId
              and s.locationId in :locationIds
              and s.deletedFlag = false
            """)
    int updateActivationStatus(
            @Param("tenantId") String tenantId,
            @Param("serviceId") Integer serviceId,
            @Param("deliveryUnitLevelId") Integer deliveryUnitLevelId,
            @Param("locationIds") List<Integer> locationIds,
            @Param("activationStatus") Integer activationStatus,
            @Param("modifiedBy") Long modifiedBy,
            @Param("modifiedOn") Date modifiedOn);

    boolean existsByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
            String tenantId,
            Integer serviceId,
            Integer deliveryUnitLevelId,
            Integer locationId);

	Page<ServiceDeliveryUnits> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationNameContainingIgnoreCaseAndDeletedFlagFalse(
			String tenantId, Integer serviceId, Integer deliveryUnitLevelId, String trim, Pageable pageable);
	
	 boolean existsByTenantIdAndServiceIdAndActivationStatusAndDeletedFlagFalse(
	            String tenantId,
	            Integer serviceId,
	            Integer activationStatus
	    );

	List<ServiceDeliveryUnits> findByServiceId(Integer serviceId);

	void deleteByServiceId(Integer serviceId);
}
