package com.serviceplus.metadata.dto;

import com.serviceplus.metadata.dto.ProcessFlowDTO.FormDetail;

public class FormEncryptionKeyDTO {
	
    private Integer serviceId;

    private String formId;
    
    private String taskId;

    private Boolean defaultFormEncryptionRequired;

    private Boolean encryptDataInTransit;
    
    private Boolean encryptDataAtRest;

	public FormEncryptionKeyDTO() {
		super();
	}

	public FormEncryptionKeyDTO(Integer serviceId, FormDetail formDetail, String taskId) {
		super();
		this.serviceId = serviceId;
		this.formId = formDetail.getFormId();
		this.taskId = taskId;
		this.defaultFormEncryptionRequired = formDetail.getDefaultFormEncryptionRequired();
		this.encryptDataInTransit = formDetail.getEncryptDataInTransit();
		this.encryptDataAtRest = formDetail.getEncryptDataAtRest();
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public Boolean getDefaultFormEncryptionRequired() {
		return defaultFormEncryptionRequired;
	}

	public void setDefaultFormEncryptionRequired(Boolean defaultFormEncryptionRequired) {
		this.defaultFormEncryptionRequired = defaultFormEncryptionRequired;
	}

	public Boolean getEncryptDataInTransit() {
		return encryptDataInTransit;
	}

	public void setEncryptDataInTransit(Boolean encryptDataInTransit) {
		this.encryptDataInTransit = encryptDataInTransit;
	}

	public Boolean getEncryptDataAtRest() {
		return encryptDataAtRest;
	}

	public void setEncryptDataAtRest(Boolean encryptDataAtRest) {
		this.encryptDataAtRest = encryptDataAtRest;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
    
    
}
