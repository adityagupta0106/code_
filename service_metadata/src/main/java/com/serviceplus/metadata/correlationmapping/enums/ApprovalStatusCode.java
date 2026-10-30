package com.serviceplus.metadata.correlationmapping.enums;

public enum ApprovalStatusCode {

    PENDING(0),
    APPROVED(1),
    REJECTED(2);

    private final int code;

    ApprovalStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ApprovalStatusCode fromValue(Integer value) {

        if (value == null) {
            throw new IllegalArgumentException(
                    "Approval status cannot be null");
        }

        for (ApprovalStatusCode status : values()) {
            if (status.code == value) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Invalid ApprovalStatusCode: " + value);
    }
}