package com.serviceplus.metadata.dto;

import java.util.List;

import com.serviceplus.metadata.dto.OfficeDetailsDTO.OfficeUnitData;

public class EligibleServiceDTO {

	private Integer baseServiceId;
	private Integer serviceId;
	private String formId;
	private String taskId;
	private String serviceName;
	private String stateName;
	private String departmentName;
	private String launchDate;
	private String taskType;
	private String serviceKey;
	private ActivityMapDTO activityMap;
	private List<OfficeUnitData> locations;
	
	public Integer getBaseServiceId() {
		return baseServiceId;
	}
	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
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
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getLaunchDate() {
		return launchDate;
	}
	public void setLaunchDate(String launchDate) {
		this.launchDate = launchDate;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getServiceKey() {
		return serviceKey;
	}
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
	public ActivityMapDTO getActivityMap() {
		return activityMap;
	}
	public void setActivityMap(ActivityMapDTO activityMap) {
		this.activityMap = activityMap;
	}
	public List<OfficeUnitData> getLocations() {
		return locations;
	}
	public void setLocations(List<OfficeUnitData> locations) {
		this.locations = locations;
	}
		
}
