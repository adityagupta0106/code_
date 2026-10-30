package com.serviceplus.tracking.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowInboxResponse {

	private String applId;

	private String applRefNo;

	private Integer serviceId;

	private String serviceName;

	private String taskId;

	private String taskName;

	private String formId;

	private String currentProcessId;

	private Date applRecievedOn;

	private Integer locationId;

	private Boolean isPriority;

	private List<MatchedOutputAttr> matchedOutputAttrs;

	public List<MatchedOutputAttr> getMatchedOutputAttrs() {
		return matchedOutputAttrs;
	}

	public void setMatchedOutputAttrs(List<MatchedOutputAttr> matchedOutputAttrs) {
		this.matchedOutputAttrs = matchedOutputAttrs;
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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

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

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getCurrentProcessId() {
		return currentProcessId;
	}

	public void setCurrentProcessId(String currentProcessId) {
		this.currentProcessId = currentProcessId;
	}

	public Date getApplRecievedOn() {
		return applRecievedOn;
	}

	public void setApplRecievedOn(Date applRecievedOn) {
		this.applRecievedOn = applRecievedOn;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Boolean getIsPriority() {
		return isPriority;
	}

	public void setIsPriority(Boolean isPriority) {
		this.isPriority = isPriority;
	}
}