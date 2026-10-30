package com.serviceplus.metadata.dto;

import java.util.List;

public class CommitRequest {

    private List<String> uploadIds;

    public List<String> getUploadIds() {
        return uploadIds;
    }

    public void setUploadIds(List<String> uploadIds) {
        this.uploadIds = uploadIds;
    }

	public CommitRequest(List<String> uploadIds) {
		super();
		this.uploadIds = uploadIds;
	}
}
