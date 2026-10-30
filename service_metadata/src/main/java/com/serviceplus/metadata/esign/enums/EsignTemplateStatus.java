package com.serviceplus.metadata.esign.enums;

public enum EsignTemplateStatus {
    ACTIVE,
    INACTIVE;

    public static EsignTemplateStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (EsignTemplateStatus status : values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid eSign template status: " + value);
    }
}
