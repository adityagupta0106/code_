package com.serviceplus.metadata.notification.dto;

import jakarta.validation.constraints.NotNull;

public class NotificationReqDTO {

	private Long notificationId;
	
	@NotNull(message = "notificationDTO can not be null")
	private NotificationDTO notificationDTO;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public NotificationDTO getNotificationDTO() {
		return notificationDTO;
	}

	public void setNotificationDTO(NotificationDTO notificationDTO) {
		this.notificationDTO = notificationDTO;
	}

}
