package com.serviceplus.metadata.enums;

public enum JsonRootType {

	OBJECT("OBJECT"), ARRAY("ARRAY");

	private final String id;

	JsonRootType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static JsonRootType fromId(String id) {
		for (JsonRootType type : values()) {
			if (type.id.equalsIgnoreCase(id)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid JSON root type : " + id);
	}
}
