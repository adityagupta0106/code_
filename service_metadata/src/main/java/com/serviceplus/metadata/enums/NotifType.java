package com.serviceplus.metadata.enums;

public enum NotifType {
	EMAIL("email"), SMS("sms"), WHATSAPP("whatsapp"), SANDES("sandes");

	private final String id;

	NotifType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static NotifType fromId(String id) {
		for (NotifType type : values()) {
			if (type.id.equalsIgnoreCase(id)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid channel type : " + id);
	}
}
