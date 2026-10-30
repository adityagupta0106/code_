package com.serviceplus.metadata.dto;

public class ServiceFormDefinitionDTO {

	private Integer serviceId;
	
	private String taskId;
	
	private String formId;
	
	private FormBlankJsonDTO formBlankJsonDTO;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public FormBlankJsonDTO getFormBlankJsonDTO() {
		return formBlankJsonDTO;
	}

	public void setFormBlankJsonDTO(FormBlankJsonDTO formBlankJsonDTO) {
		this.formBlankJsonDTO = formBlankJsonDTO;
	}
	
	

}
