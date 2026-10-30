package com.serviceplus.metadata.notification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SmsServiceProvider {

	NIC("NIC"), CDAC("CDAC"), MSDG("MSDG"), AMTRON("AMTRON"), BSNL("BSNL");

	private final String value;

	SmsServiceProvider(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static SmsServiceProvider fromValue(String value) {
		for (SmsServiceProvider provider : values()) {
			if (provider.value.equalsIgnoreCase(value)) {
				return provider;
			}
		}
		throw new IllegalArgumentException("Invalid SMS Service Provider: " + value);
	}

	@Override
	public String toString() {
		return value;
	}
}