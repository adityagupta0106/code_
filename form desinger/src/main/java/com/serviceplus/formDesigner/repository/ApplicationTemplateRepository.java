package com.serviceplus.formDesigner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.ApplicationTemplate;

@Repository
public interface ApplicationTemplateRepository extends BaseApplicationTemplateRepository<ApplicationTemplate> {
	public ApplicationTemplate findByFormId(String formId);
	public List<ApplicationTemplate> findByCreatedByAndStatus(Long createdBy,String status);
	public List<ApplicationTemplate> findByStatusAndDepartmentIdAndArchive(String status,Integer departmentId, Boolean archive);
	public List<ApplicationTemplate> findByStatusAndDepartmentIdAndInstantiable(String status,Integer departmentId,String instantiable);
	public List<ApplicationTemplate> findByFormIdAndStatusAndDepartmentId(String formId,String status,Integer departmentId);
	
	public List<ApplicationTemplate> findByFormIdAndStatusNotAndArchive(String FormId,String status, Boolean archive);
	public ApplicationTemplate findByHolderIdAndFormIdAndStatusNot(String holderId, String formId, String status);
	public ApplicationTemplate findTopByHolderIdAndStatusNotOrderByVersionDesc(String holderId, String status);
	public List<ApplicationTemplate> findByFormIdAndStatusNot(String formId,String status);
	public List<ApplicationTemplate> findByStatusAndArchive(String status , Boolean archive);
	public ApplicationTemplate findTopByHolderIdAndStatusAndArchiveOrderByVersionDesc(String holderId, String status, Boolean archive);
	
	public Optional<ApplicationTemplate> findByFormNameAndTenantId(String formId, String tenantId );
	
	Optional<ApplicationTemplate> findByFormNameAndTenantIdAndArchiveAndStatusNot(String formName, String tenantId, Boolean archive, String status);
	
	List<ApplicationTemplate> findByFormIdAndStatusAndArchive(String formId, String status, Boolean archive);
    List<ApplicationTemplate> findByStatusNotAndInstantiableAndArchive(String status, String instantiable, Boolean archive);
}
