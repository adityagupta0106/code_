package com.serviceplus.formDesigner.request.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApplicationDataStandalone {
	@Id
	private String dataId;
	private String formId;
	private String holderId;
	@CreatedDate
	private Date createdOn;
	private Object formData;
	@Transient
	private Map<String, String> validationTokens;
	public String getDataId() {
		return dataId;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Object getFormData() {
		return formData;
	}
	public void setFormData(Object formData) {
		this.formData = formData;
	}
	public Map<String, String> getValidationTokens() {
		return validationTokens;
	}
	public void setValidationTokens(Map<String, String> validationTokens) {
		this.validationTokens = validationTokens;
	}
	public String getHolderId() {
		return holderId;
	}
	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}
	
}
