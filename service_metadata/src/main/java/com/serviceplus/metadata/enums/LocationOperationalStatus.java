package com.serviceplus.metadata.enums;

public enum LocationOperationalStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED;

    public static LocationOperationalStatus fromStatusCode(Integer code) {
        if (code == null) {
            return INACTIVE;
        }
        if (code == 1) {
            return ACTIVE;
        }
        if (code == 2) {
            return SUSPENDED;
        }
        return INACTIVE;
    }
}
