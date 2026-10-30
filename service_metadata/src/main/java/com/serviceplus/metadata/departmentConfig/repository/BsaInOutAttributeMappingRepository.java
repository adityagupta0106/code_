package com.serviceplus.metadata.departmentConfig.repository;

import com.serviceplus.metadata.departmentConfig.entity.BsaInOutAttributeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BsaInOutAttributeMappingRepository extends JpaRepository<BsaInOutAttributeMapping, Long> {

//    List<BsaInOutAttributeMapping> findByServiceIdAndTenantId(Integer serviceId, String tenantId);
//
//    List<BsaInOutAttributeMapping> findByServiceId(Integer serviceId);
//
//    List<BsaInOutAttributeMapping> findByBsaConfigIdAndTenantId(Long bsaConfigId, String tenantId);
//
//    void deleteByServiceIdAndTenantId(Integer serviceId, String tenantId);
}
