package com.serviceplus.metadata.pattern.enums;

public enum MappedAttrType {
	F("F", "Form Attribute"),
	S("S", "System Variable");

	private final String code;
	private final String description;

	MappedAttrType(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public static MappedAttrType fromCode(String code) {
		for (MappedAttrType type : values()) {
			if (type.code.equalsIgnoreCase(code)) {
				return type;
			}
		}
		return null;
	}
}
