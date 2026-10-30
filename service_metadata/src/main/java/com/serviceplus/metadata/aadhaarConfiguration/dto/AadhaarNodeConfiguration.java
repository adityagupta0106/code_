package com.serviceplus.metadata.aadhaarConfiguration.dto;


import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarMappingType;

import java.io.Serializable;

public class AadhaarNodeConfiguration implements Serializable {

    private String nodeCode;

    private String displayName;

    private String xpath;

    private boolean enableLogs;

    private boolean maskValue;

    private AadhaarMappingType mappingType;

    private AadhaarNodeValueConfiguration valueConfiguration;


    public AadhaarNodeConfiguration() {
    }


    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public boolean isEnableLogs() {
        return enableLogs;
    }

    public void setEnableLogs(boolean enableLogs) {
        this.enableLogs = enableLogs;
    }

    public boolean isMaskValue() {
        return maskValue;
    }

    public void setMaskValue(boolean maskValue) {
        this.maskValue = maskValue;
    }

    public AadhaarMappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(
            AadhaarMappingType mappingType
    ) {
        this.mappingType = mappingType;
    }

    public AadhaarNodeValueConfiguration getValueConfiguration() {
        return valueConfiguration;
    }

    public void setValueConfiguration(
            AadhaarNodeValueConfiguration valueConfiguration
    ) {
        this.valueConfiguration = valueConfiguration;
    }
}
