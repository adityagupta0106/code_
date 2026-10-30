package com.serviceplus.metadata.versioning.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;

import java.util.Date;
import java.util.List;

public class ServiceVersionChangesResponse {

    private Integer serviceId;
    private Integer baseServiceId;
    private Integer versionNo;
    private Integer minorVersionNo;
    private int totalChanges;
    private List<ChangeLogEntryDTO> changeLogs;

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

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getMinorVersionNo() {
        return minorVersionNo;
    }

    public void setMinorVersionNo(Integer minorVersionNo) {
        this.minorVersionNo = minorVersionNo;
    }

    public int getTotalChanges() {
        return totalChanges;
    }

    public void setTotalChanges(int totalChanges) {
        this.totalChanges = totalChanges;
    }

    public List<ChangeLogEntryDTO> getChangeLogs() {
        return changeLogs;
    }

    public void setChangeLogs(List<ChangeLogEntryDTO> changeLogs) {
        this.changeLogs = changeLogs;
    }

    public static class ChangeLogEntryDTO {
        private Long id;
        private Long versionChangeId;
        private String modificationToken;
        private ServiceComponent component;
        private String action;
        private String changeSummary;
        private List<ChangeItemDTO> changeItems;
        @JsonIgnore
        private Object previousConfig;
        @JsonIgnore
        private Object currentConfig;
        @JsonIgnore
        private Long userId;
        @JsonIgnore
        private String tenantId;
        private Date createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getVersionChangeId() {
            return versionChangeId;
        }

        public void setVersionChangeId(Long versionChangeId) {
            this.versionChangeId = versionChangeId;
        }

        public String getModificationToken() {
            return modificationToken;
        }

        public void setModificationToken(String modificationToken) {
            this.modificationToken = modificationToken;
        }

        public ServiceComponent getComponent() {
            return component;
        }

        public void setComponent(ServiceComponent component) {
            this.component = component;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getChangeSummary() {
            return changeSummary;
        }

        public void setChangeSummary(String changeSummary) {
            this.changeSummary = changeSummary;
        }

        public List<ChangeItemDTO> getChangeItems() {
            return changeItems;
        }

        public void setChangeItems(List<ChangeItemDTO> changeItems) {
            this.changeItems = changeItems;
        }

        public Object getPreviousConfig() {
            return previousConfig;
        }

        public void setPreviousConfig(Object previousConfig) {
            this.previousConfig = previousConfig;
        }

        public Object getCurrentConfig() {
            return currentConfig;
        }

        public void setCurrentConfig(Object currentConfig) {
            this.currentConfig = currentConfig;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
    }
}
