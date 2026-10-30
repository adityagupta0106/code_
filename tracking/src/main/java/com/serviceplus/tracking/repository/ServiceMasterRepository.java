package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.ServiceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceMasterRepository extends JpaRepository<ServiceMaster, Integer> {

    Optional<ServiceMaster> findByBaseServiceIdAndTenantId(Integer baseServiceId, String tenantId);
}
