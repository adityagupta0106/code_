package com.serviceplus.metadata.aadhaarConfiguration.dto;

import java.io.Serializable;

public class AadhaarNodeValueConfiguration implements Serializable {

    private String value;

    private String systemVariable;

    private String pluginMethodCode;

    private boolean visibleToServiceDefiner = true;


    public AadhaarNodeValueConfiguration() {
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSystemVariable() {
        return systemVariable;
    }

    public void setSystemVariable(String systemVariable) {
        this.systemVariable = systemVariable;
    }

    public String getPluginMethodCode() {
        return pluginMethodCode;
    }

    public void setPluginMethodCode(String pluginMethodCode) {
        this.pluginMethodCode = pluginMethodCode;
    }

    public boolean isVisibleToServiceDefiner() {
        return visibleToServiceDefiner;
    }

    public void setVisibleToServiceDefiner(boolean visibleToServiceDefiner) {
        this.visibleToServiceDefiner = visibleToServiceDefiner;
    }
}
