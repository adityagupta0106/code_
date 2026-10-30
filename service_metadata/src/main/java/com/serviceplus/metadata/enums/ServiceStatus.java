package com.serviceplus.metadata.enums;

public enum ServiceStatus {

    DEFINED(0),
    FROZEN(1),
    ACTIVATED(2),
    PENDING_APPROVAL(3),
    LAUNCHED(4),
    MODIFICATION(5);

    private final int code;

    ServiceStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ServiceStatus fromCode(int code) {
        for (ServiceStatus status : ServiceStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ServiceStatus code: " + code);
    }
}
