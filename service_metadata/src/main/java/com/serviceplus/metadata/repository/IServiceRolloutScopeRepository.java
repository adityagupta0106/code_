package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceRolloutScope;

@Repository
public interface IServiceRolloutScopeRepository extends JpaRepository<ServiceRolloutScope, Long> {

    Optional<ServiceRolloutScope> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer deliveryUnitLevelId, Integer locationId);

    Optional<ServiceRolloutScope> findByTenantIdAndServiceIdAndLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer locationId);

    List<ServiceRolloutScope> findByTenantIdAndServiceIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId);

    List<ServiceRolloutScope> findByTenantIdAndServiceIdAndParentLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer parentLocationId);

    List<ServiceRolloutScope> findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer deliveryUnitLevelId);

    boolean existsByTenantIdAndServiceIdAndLocationIdAndScopeStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer locationId, String scopeStatus);

    boolean existsByTenantIdAndServiceIdAndLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer locationId);

    List<ServiceRolloutScope> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
