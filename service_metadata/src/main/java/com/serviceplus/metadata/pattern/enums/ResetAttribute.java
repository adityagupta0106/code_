package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResetAttribute {

    SPGD_ENTITY("SPGD Entity", "SPGD_ENTITY", "Sequence resets when the SPGD Entity code changes"),
    CALENDAR_YEAR("Calendar Year", "CALENDAR_YEAR", "Sequence resets on the change of calendar year (YYYY)"),
    FINANCIAL_YEAR("Financial Year", "FINANCIAL_YEAR", "Sequence resets every financial year (e.g. 2026-27)"),
    SERVICE_ATTRIBUTE_1("Service Attribute 1", "SERVICE_ATTRIBUTE_1", "Resets when Service Attribute 1 value changes (mapped in Service Configuration)"),
    SERVICE_ATTRIBUTE_2("Service Attribute 2", "SERVICE_ATTRIBUTE_2", "Resets when Service Attribute 2 value changes (mapped in Service Configuration)"),
    SERVICE_ATTRIBUTE_3("Service Attribute 3", "SERVICE_ATTRIBUTE_3", "Resets when Service Attribute 3 value changes (mapped in Service Configuration)");

    private final String label;
    private final String value;
    private final String description;

    ResetAttribute(String label, String value, String description) {
        this.label = label;
        this.value = value;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ResetAttribute fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ResetAttribute attribute : values()) {
            if (attribute.value.equalsIgnoreCase(value) || attribute.name().equalsIgnoreCase(value)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("Invalid ResetAttribute value: " + value);
    }
}
