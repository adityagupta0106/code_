package com.serviceplus.metadata.dto;

import java.util.List;

public class NotificationListResponseDTO {

    private Long notificationId;

    private String notificationName;

    private String channel;

    private String triggerPointId;

    private String triggerPointName;

    private Integer serviceId;

    private String serviceName;
    
    private String status;

    private List<Integer> serviceVersions;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTriggerPointId() {
		return triggerPointId;
	}

	public void setTriggerPointId(String triggerPointId) {
		this.triggerPointId = triggerPointId;
	}

	public String getTriggerPointName() {
		return triggerPointName;
	}

	public void setTriggerPointName(String triggerPointName) {
		this.triggerPointName = triggerPointName;
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

	public List<Integer> getServiceVersions() {
		return serviceVersions;
	}

	public void setServiceVersions(List<Integer> serviceVersions) {
		this.serviceVersions = serviceVersions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
