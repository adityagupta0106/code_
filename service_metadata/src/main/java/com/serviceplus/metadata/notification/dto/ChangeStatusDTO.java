package com.serviceplus.metadata.notification.dto;

import jakarta.validation.constraints.NotNull;

public class ChangeStatusDTO {
	@NotNull(message = "Id can't be Null.")
	private Long notificationId;
	@NotNull(message = "Flag can't be Null.")
	private Boolean isActive;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
