package com.serviceplus.metadata.enums;

public enum ExternalSystemStatus {

    INACTIVE("D", "Inactive"),
    ACTIVE("A", "Active"),
    ONLINE("O", "Online");

    private final String code;
    private final String label;

    ExternalSystemStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ExternalSystemStatus fromCode(String code) {
        for (ExternalSystemStatus status : ExternalSystemStatus.values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}