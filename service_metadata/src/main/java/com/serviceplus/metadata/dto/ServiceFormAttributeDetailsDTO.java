package com.serviceplus.metadata.dto;

import java.util.List;

import com.serviceplus.metadata.dto.FormDTO.Attribute;

public class ServiceFormAttributeDetailsDTO {
	
	private String templateName;
	private String formId;
	private String holderId;
	
	private Long templateId;
	
	private List<Attribute> attributeList;
	
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public List<Attribute> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(List<Attribute> attributeList) {
		this.attributeList = attributeList;
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
	
	
}
