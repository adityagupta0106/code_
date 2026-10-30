package com.serviceplus.metadata.versioning.dto;

import com.serviceplus.metadata.versioning.enums.VersionType;

import jakarta.validation.constraints.NotNull;

public class StartModificationRequest {

    @NotNull
    private VersionType versionType;

    public VersionType getVersionType() {
        return versionType;
    }

    public void setVersionType(VersionType versionType) {
        this.versionType = versionType;
    }
}