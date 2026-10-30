package com.serviceplus.metadata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceplus.metadata.entity.ServiceTemplate;

public interface IServiceTemplateRepository extends JpaRepository<ServiceTemplate, Object>{

	List<ServiceTemplate> findByServiceIdAndUserId(Integer serviceId, Long userId);

	ServiceTemplate findByIdAndUserId(Long templateId, Long userId);

	ServiceTemplate findByIdAndServiceIdAndUserId(Integer templateId, Integer serviceId, Long userId);

	ServiceTemplate findByTaskId(String name);

	ServiceTemplate findByServiceIdAndTaskId(Integer serviceId, String taskId);
	
	ServiceTemplate findByServiceIdAndFormId(Integer serviceId, String formId);

	ServiceTemplate findByServiceIdAndTaskIdAndUserId(Integer serviceId, String nodeId, Long longValue);
	
	ServiceTemplate findByServiceIdAndTaskIdAndFormId(Integer serviceId, String taskId, String formId);

	List<ServiceTemplate> findByServiceId(Integer serviceId);

	void deleteByServiceId(Integer serviceId);
}
