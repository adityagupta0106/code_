package com.serviceplus.metadata.esign.repository;

import com.serviceplus.metadata.esign.entity.EsignTemplate;
import com.serviceplus.metadata.esign.enums.EsignTemplateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EsignTemplateRepository extends JpaRepository<EsignTemplate, Long> {

    Optional<EsignTemplate> findByIdAndTenantId(Long id, String tenantId);

    List<EsignTemplate> findByTenantId(String tenantId);

    List<EsignTemplate> findByTenantIdAndStatus(String tenantId, EsignTemplateStatus status);

    boolean existsByTenantIdAndTemplateNameIgnoreCase(String tenantId, String templateName);

    boolean existsByTenantIdAndTemplateNameIgnoreCaseAndIdNot(String tenantId, String templateName, Long id);

    boolean existsByIdAndTenantIdAndStatus(Long id, String tenantId, EsignTemplateStatus status);
}
