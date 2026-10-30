package com.serviceplus.metadata.departmentConfig.repository;

import com.serviceplus.metadata.departmentConfig.entity.BsaServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BsaServiceMappingRepository extends JpaRepository<BsaServiceMapping, Long> {

    Optional<BsaServiceMapping> findByServiceIdAndTenantId(Integer serviceId, String tenantId);

    Optional<BsaServiceMapping> findByServiceId(Integer serviceId);

    Optional<BsaServiceMapping> findFirstByServiceIdAndActiveStatusTrue(Integer serviceId);

    List<BsaServiceMapping> findByTenantId(String tenantId);

    List<BsaServiceMapping> findByBsaConfigIdAndTenantId(Long bsaConfigId, String tenantId);

    void deleteByServiceIdAndTenantId(Integer serviceId, String tenantId);
}
