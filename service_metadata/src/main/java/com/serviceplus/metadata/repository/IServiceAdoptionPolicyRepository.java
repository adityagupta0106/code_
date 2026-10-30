package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceAdoptionPolicy;

@Repository
public interface IServiceAdoptionPolicyRepository extends JpaRepository<ServiceAdoptionPolicy, Long> {

    Optional<ServiceAdoptionPolicy> findByTenantIdAndServiceIdAndDeletedFlagFalse(String tenantId, Integer serviceId);

    List<ServiceAdoptionPolicy> findByTenantIdAndDeletedFlagFalse(String tenantId);

    List<ServiceAdoptionPolicy> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
    
	List<ServiceAdoptionPolicy> findByTenantIdAndCreatedByAndDeletedFlagFalse(String tenantId, Long userId);
}
