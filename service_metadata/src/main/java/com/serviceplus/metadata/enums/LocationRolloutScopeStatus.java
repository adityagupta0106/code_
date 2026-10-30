package com.serviceplus.metadata.enums;

public enum LocationRolloutScopeStatus {
    IN_SCOPE,
    OUT_OF_SCOPE;

    public static LocationRolloutScopeStatus fromString(String val) {
        if (val == null) {
            return OUT_OF_SCOPE;
        }
        for (LocationRolloutScopeStatus status : LocationRolloutScopeStatus.values()) {
            if (status.name().equalsIgnoreCase(val.trim())) {
                return status;
            }
        }
        return OUT_OF_SCOPE;
    }
}
