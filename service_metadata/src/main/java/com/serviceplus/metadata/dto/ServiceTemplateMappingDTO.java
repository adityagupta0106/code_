package com.serviceplus.metadata.dto;

public class ServiceTemplateMappingDTO {

	private String formId;
	private String instantiable;
	private Long createdBy;
	private Integer stateId;
	private Integer departmentId;
	private Integer locationId;
	
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getInstantiable() {
		return instantiable;
	}
	public void setInstantiable(String instantiable) {
		this.instantiable = instantiable;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getStateId() {
		return stateId;
	}
	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}
	public Integer getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	

}
