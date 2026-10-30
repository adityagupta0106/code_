package com.serviceplus.metadata.dto;

import com.serviceplus.metadata.notification.dto.NotificationDTO;

public class NotificationConfigurationKafkaDTO {

    private Long notificationId;

    private Integer baseServiceId;

    private Integer serviceId;

    private String notificationName;

    private String channel;

    private String triggerPointId;

    private String taskId;

    private NotificationDTO configJson;

    private Long createdBy;

    private Long modifiedBy;

    private String tenantId;

    public NotificationConfigurationKafkaDTO() {
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public NotificationDTO getConfigJson() {
        return configJson;
    }

    public void setConfigJson(NotificationDTO configJson) {
        this.configJson = configJson;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
