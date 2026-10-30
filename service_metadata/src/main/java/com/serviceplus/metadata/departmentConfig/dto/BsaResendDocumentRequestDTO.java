package com.serviceplus.metadata.departmentConfig.dto;

import java.io.Serializable;

public class BsaResendDocumentRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long departmentId;
    private String reason;

    public BsaResendDocumentRequestDTO() {
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
