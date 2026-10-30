package com.serviceplus.tracking.dto;

import java.util.List;
import java.util.Map;

public class FormAttributeRequest {

	private String formId;

	private List<String> attributes;

	private Map<String, String> attributeMapping;

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public Map<String, String> getAttributeMapping() {
		return attributeMapping;
	}

	public void setAttributeMapping(Map<String, String> attributeMapping) {
		this.attributeMapping = attributeMapping;
	}

}