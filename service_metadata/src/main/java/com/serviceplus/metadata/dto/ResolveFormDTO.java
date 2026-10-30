package com.serviceplus.metadata.dto;

import java.util.List;

import com.serviceplus.metadata.dto.OfficeDetailsDTO.OfficeUnitData;

public class ResolveFormDTO {

	private Integer serviceId;

	private String serviceName;

	private String formId;

	private String taskId;

	private String taskType;

	private String serviceKey;

	private Integer baseServiceId;

	private ActivityMapDTO activityMap;

	private List<OfficeUnitData> locations;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
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
