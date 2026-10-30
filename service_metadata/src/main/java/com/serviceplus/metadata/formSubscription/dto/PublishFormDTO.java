package com.serviceplus.metadata.formSubscription.dto;

public class PublishFormDTO {

	private String holderId;
	private String publishFormId;
	private Integer publishVersion;

	public String getPublishFormId() {
		return publishFormId;
	}

	public void setPublishFormId(String publishFormId) {
		this.publishFormId = publishFormId;
	}

	public Integer getPublishVersion() {
		return publishVersion;
	}

	public void setPublishVersion(Integer publishVersion) {
		this.publishVersion = publishVersion;
	}

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

}