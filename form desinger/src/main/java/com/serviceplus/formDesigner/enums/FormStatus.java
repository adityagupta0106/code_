package com.serviceplus.formDesigner.enums;

public enum FormStatus {

	ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), PUBLISHED("PUBLISHED");

	private final String value;

	FormStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FormStatus fromValue(String value) {
		for (FormStatus status : FormStatus.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid FormStatus: " + value);
	}
}