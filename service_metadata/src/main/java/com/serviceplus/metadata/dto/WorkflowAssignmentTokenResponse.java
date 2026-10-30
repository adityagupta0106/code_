package com.serviceplus.metadata.dto;

import java.time.Instant;

public class WorkflowAssignmentTokenResponse {

    private WorkflowAssignmentTokenRequest success;

    public WorkflowAssignmentTokenResponse() {
    }

    public WorkflowAssignmentTokenResponse(WorkflowAssignmentTokenRequest response) {
        this.success = response;
    }

    public WorkflowAssignmentTokenRequest getSuccess() {
        return success;
    }

    public void setSuccess(WorkflowAssignmentTokenRequest success) {
        this.success = success;
    }
}
