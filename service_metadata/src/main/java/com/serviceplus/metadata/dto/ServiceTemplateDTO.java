package com.serviceplus.metadata.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ServiceTemplateDTO {
	
	private Integer serviceId;
	
	private Long templateId;
	
	@NotNull(message="templateName: Template Name is required")
	private String templateName;
		
	private String formId;
	
	private String holderId;
	
	@NotNull(message="instantiableFlag: Instantiable is required")
	@NotBlank(message = "instantiableFlag: Instantiable is required")
    @Pattern(regexp = "^[YN]$", message = "instantiableFlag:Instantiable must be 'Y' or 'N'")
	private String instantiableFlag;
	
	@NotNull(message="taskId: Task is mandatory")
	private String taskId;
	
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
	

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public String getInstantiableFlag() {
		return instantiableFlag;
	}

	public void setInstantiableFlag(String instantiableFlag) {
		this.instantiableFlag = instantiableFlag;
	}

}
