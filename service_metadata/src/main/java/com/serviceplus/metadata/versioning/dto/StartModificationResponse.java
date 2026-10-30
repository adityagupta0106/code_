package com.serviceplus.metadata.versioning.dto;

import com.serviceplus.metadata.versioning.enums.VersionType;

public class StartModificationResponse {

    private Long modificationId;

    private String modificationToken;

    private Integer serviceId;

    private Integer baseServiceId;

    private VersionType versionType;

    private Integer currentVersion;

    private Integer currentMinorVersion;

    private Integer targetServiceId;

    private Integer targetVersion;

    private Integer targetMinorVersion;

    private String status;

    public Long getModificationId() {
        return modificationId;
    }

    public void setModificationId(Long modificationId) {
        this.modificationId = modificationId;
    }

    public String getModificationToken() {
        return modificationToken;
    }

    public void setModificationToken(String modificationToken) {
        this.modificationToken = modificationToken;
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

    public VersionType getVersionType() {
        return versionType;
    }

    public void setVersionType(VersionType versionType) {
        this.versionType = versionType;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Integer getCurrentMinorVersion() {
        return currentMinorVersion;
    }

    public void setCurrentMinorVersion(Integer currentMinorVersion) {
        this.currentMinorVersion = currentMinorVersion;
    }

    public Integer getTargetServiceId() {
        return targetServiceId;
    }

    public void setTargetServiceId(Integer targetServiceId) {
        this.targetServiceId = targetServiceId;
    }

    public Integer getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(Integer targetVersion) {
        this.targetVersion = targetVersion;
    }

    public Integer getTargetMinorVersion() {
        return targetMinorVersion;
    }

    public void setTargetMinorVersion(Integer targetMinorVersion) {
        this.targetMinorVersion = targetMinorVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}