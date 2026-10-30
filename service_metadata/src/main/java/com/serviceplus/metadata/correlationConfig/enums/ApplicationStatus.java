package com.serviceplus.metadata.correlationConfig.enums;

public enum ApplicationStatus {

	INITIATED(1), DELIVERED(2), AFTER_TASK_EXECUTION(3);

	private final int code;

	ApplicationStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static ApplicationStatus fromValue(Integer value) {

		if (value == null) {
			throw new IllegalArgumentException("Application status cannot be null");
		}

		for (ApplicationStatus status : values()) {
			if (status.code == value) {
				return status;
			}
		}

		throw new IllegalArgumentException("Invalid application status: " + value);
	}
}