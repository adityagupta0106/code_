package com.serviceplus.metadata.enums;

public enum LocationAdoptionStatus {
    NOT_ADOPTED,
    ADOPTED,
    SUSPENDED;

    public static LocationAdoptionStatus fromString(String val) {
        if (val == null) {
            return NOT_ADOPTED;
        }
        for (LocationAdoptionStatus status : LocationAdoptionStatus.values()) {
            if (status.name().equalsIgnoreCase(val.trim())) {
                return status;
            }
        }
        return NOT_ADOPTED;
    }
}
