package com.serviceplus.formDesigner.request.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Languages {

	private String id;
	private String value;
	private Boolean isActive;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
