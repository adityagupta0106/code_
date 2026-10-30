package com.serviceplus.formDesigner.master.enums;

public enum MasterType {
	FIELDTYPE("FIELDTYPE"), FIELDJSON("FIELDJSON");

	private final String value;

	MasterType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static MasterType fromValue(String value) {
		for (MasterType type : values()) {
			if (type.value.equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unsupported master type: " + value);
	}

}
