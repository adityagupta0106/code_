package com.serviceplus.metadata.enums;

public enum ChannelType {

    EMAIL("EMAIL"),
    SMS("SMS"),
    WHATSAPP("WHATSAPP"),
    SANDES("SANDES");

    private final String id;

    ChannelType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ChannelType fromId(String id) {
        for (ChannelType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid channel type : " + id);
    }
}
