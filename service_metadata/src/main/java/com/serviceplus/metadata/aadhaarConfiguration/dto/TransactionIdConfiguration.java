package com.serviceplus.metadata.aadhaarConfiguration.dto;

import java.io.Serializable;

public class TransactionIdConfiguration implements Serializable {

    private boolean enabled;

    private String prefix;

    private String suffix;

    public TransactionIdConfiguration() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
