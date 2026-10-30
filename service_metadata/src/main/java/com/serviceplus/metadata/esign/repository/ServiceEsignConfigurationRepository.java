package com.serviceplus.metadata.esign.repository;

import com.serviceplus.metadata.esign.entity.ServiceEsignConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceEsignConfigurationRepository extends JpaRepository<ServiceEsignConfiguration, Long> {

    Optional<ServiceEsignConfiguration> findByServiceIdAndTenantId(Integer serviceId, String tenantId);

    Optional<ServiceEsignConfiguration> findByServiceId(Integer serviceId);

    boolean existsByEsignTemplateIdAndEsignEnabledTrue(Long esignTemplateId);

    boolean existsByEsignTemplateId(Long esignTemplateId);

    void deleteByServiceIdAndTenantId(Integer serviceId, String tenantId);
}
