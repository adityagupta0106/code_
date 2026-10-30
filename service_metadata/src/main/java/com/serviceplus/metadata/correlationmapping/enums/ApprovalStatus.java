package com.serviceplus.metadata.correlationmapping.enums;

public enum ApprovalStatus {

    PENDING("PENDING", 0),
    APPROVED("APPROVED", 1),
    REJECTED("REJECT", 2);

    private final String value;
    private final int code;

    ApprovalStatus(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static ApprovalStatus fromCode(int code) {
        for (ApprovalStatus status : ApprovalStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Invalid ApprovalStatus code: " + code);
    }

    public static ApprovalStatus fromValue(String value) {
        for (ApprovalStatus status : ApprovalStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Invalid ApprovalStatus value: " + value);
    }
}