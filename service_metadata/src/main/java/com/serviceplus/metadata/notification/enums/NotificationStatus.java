package com.serviceplus.metadata.notification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationStatus {

	DEFINE("D"), ACTIVE("A"), MOVETOPROD("M");

	private final String value;

	NotificationStatus(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static NotificationStatus fromValue(String value) {
		for (NotificationStatus type : values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid Notification Status: " + value);
	}
}