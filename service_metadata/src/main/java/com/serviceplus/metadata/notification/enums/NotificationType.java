package com.serviceplus.metadata.notification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
	SMS("SMS"), SANDES("SANDES"), WHATSAPP("WHATSAPP"), EMAIL("EMAIL");

	private final String value;

	NotificationType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static NotificationType fromValue(String value) {
		for (NotificationType type : values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid Notification Status: " + value);
	}

}
