package com.serviceplus.formDesigner.dto;

import java.util.List;

public class CommitRequest {

    private List<String> uploadIds;

    public CommitRequest() {

    }

    public CommitRequest(List<String> uploadIds) {
        this.uploadIds = uploadIds;
    }

    public List<String> getUploadIds() {
        return uploadIds;
    }

    public void setUploadIds(List<String> uploadIds) {
        this.uploadIds = uploadIds;
    }
}
