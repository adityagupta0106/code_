package com.serviceplus.tracking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserInboxDTO {

	private String applId;
	
	private String applRefNo;
	
	private Integer serviceId;
	
	private Integer baseServiceId;
	
	private String serviceName;
	
	private LocalDateTime applRecievedOn;
	
	private LocalDateTime lastActionOn;
	
	private TaskDTO task;
	
	private String tenantId;

	public static class TaskDTO {

		private String taskId;
		private String taskName;
		private String currentProcessId;
		private List<LocationDTO> locations;
		private String formId;
		private Boolean isPriority;

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getTaskName() {
			return taskName;
		}

		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}

		public String getCurrentProcessId() {
			return currentProcessId;
		}

		public void setCurrentProcessId(String currentProcessId) {
			this.currentProcessId = currentProcessId;
		}

		public List<LocationDTO> getLocations() {
			return locations;
		}

		public void setLocations(List<LocationDTO> locations) {
			this.locations = locations;
		}

		public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

		public Boolean getIsPriority() {
			return isPriority;
		}

		public void setIsPriority(Boolean isPriority) {
			this.isPriority = isPriority;
		}

	}

	public static class LocationDTO {

		private Integer locationId;
		private String locationName;
		private String userToken;

		public Integer getLocationId() {
			return locationId;
		}

		public void setLocationId(Integer locationId) {
			this.locationId = locationId;
		}

		public String getLocationName() {
			return locationName;
		}

		public void setLocationName(String locationName) {
			this.locationName = locationName;
		}

		public String getUserToken() {
			return userToken;
		}

		public void setUserToken(String userToken) {
			this.userToken = userToken;
		}

	}

	public String getApplId() {
		return applId;
	}

	public void setApplId(String applId) {
		this.applId = applId;
	}

	public String getApplRefNo() {
		return applRefNo;
	}

	public void setApplRefNo(String applRefNo) {
		this.applRefNo = applRefNo;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public LocalDateTime getApplRecievedOn() {
		return applRecievedOn;
	}

	public void setApplRecievedOn(LocalDateTime applRecievedOn) {
		this.applRecievedOn = applRecievedOn;
	}

	public LocalDateTime getLastActionOn() {
		return lastActionOn;
	}

	public void setLastActionOn(LocalDateTime lastActionOn) {
		this.lastActionOn = lastActionOn;
	}

	public TaskDTO getTask() {
		return task;
	}

	public void setTask(TaskDTO task) {
		this.task = task;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
