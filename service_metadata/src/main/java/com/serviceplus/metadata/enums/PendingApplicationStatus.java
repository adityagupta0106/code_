package com.serviceplus.metadata.enums;

public enum PendingApplicationStatus {
    PENDING_ADOPTION,
    READY_FOR_PROCESSING,
    RELEASED_TO_WORKFLOW;

    public static PendingApplicationStatus fromString(String val) {
        if (val == null) {
            return PENDING_ADOPTION;
        }
        for (PendingApplicationStatus status : PendingApplicationStatus.values()) {
            if (status.name().equalsIgnoreCase(val.trim())) {
                return status;
            }
        }
        return PENDING_ADOPTION;
    }
}
