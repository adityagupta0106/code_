package com.serviceplus.tracking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserSentBoxDTO {

    private String applId;
    private String applRefNo;

    private Integer serviceId;
    private String serviceName;

    private String taskId;
    private String taskName;

    private LocalDateTime actionOn;
    
    private List<MatchedOutputAttr> matchedOutputAttrs;
    
    private String currentProcessId;

    public List<MatchedOutputAttr> getMatchedOutputAttrs() {
        return matchedOutputAttrs;
    }

    public void setMatchedOutputAttrs(List<MatchedOutputAttr> matchedOutputAttrs) {
        this.matchedOutputAttrs = matchedOutputAttrs;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public LocalDateTime getActionOn() {
        return actionOn;
    }

    public void setActionOn(LocalDateTime actionOn) {
        this.actionOn = actionOn;
    }

	public String getCurrentProcessId() {
		return currentProcessId;
	}

	public void setCurrentProcessId(String currentProcessId) {
		this.currentProcessId = currentProcessId;
	}
}
