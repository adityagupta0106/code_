package com.serviceplus.formDesigner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;

@Repository
public interface ApplicationTemplateStandaloneRepository extends BaseApplicationTemplateRepository<ApplicationTemplateStandalone> {
	public Optional<ApplicationTemplateStandalone> findByTinyUrlToken(String tinyUrlToken);
	boolean existsByTinyUrlToken(String tinyUrlToken);
	
	public ApplicationTemplateStandalone findTopByHolderIdAndStatusNot(String holderId, String status);
	public ApplicationTemplateStandalone findByHolderIdAndFormIdAndStatusNot(String holderId, String formId, String status);
	
	public List<ApplicationTemplateStandalone> findByStatus(String status);
	public Optional<ApplicationTemplateStandalone> findByFormNameAndTenantId(String formName, String tenantId);
    public List<ApplicationTemplateStandalone> findByStatusInAndInstantiable(List<String> status, String instantiable);
    public List<ApplicationTemplateStandalone> findByCreatedByAndTenantIdAndStatus(Long createdBy, String tenantId, String status);
    
    public List<ApplicationTemplateStandalone> findByCreatedByAndTenantId(Long createdBy, String tenantId);
 }
