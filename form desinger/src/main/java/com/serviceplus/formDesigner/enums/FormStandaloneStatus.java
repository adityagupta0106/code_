package com.serviceplus.formDesigner.enums;

public enum FormStandaloneStatus {
	YET_TO_PUBLISH("0"), PUBLISHED("1"), ARCHIVED("2"), TRASHED("3");

	private final String value;

	FormStandaloneStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static FormStandaloneStatus fromValue(String value) {
		for (FormStandaloneStatus status : FormStandaloneStatus.values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid FormStatus: " + value);
	}
}
