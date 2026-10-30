package com.serviceplus.metadata.pattern.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Placeholder {

    PREFIX("Prefix", "PREFIX", "Configurable static value or mapped system value", "GOV"),
    SUFFIX("Suffix", "SUFFIX", "Configurable static value or mapped system value", "SEC"),
    SERVICE_ATTRIBUTE("Service Attribute", "SERVICE_ATTRIBUTE", "Value of configured service attribute", "EWS"),
    SERVICE_CODE("Service Code", "SERVICE_CODE", "Alphanumeric code of the service", "INC"),
    SPGD_ENTITY("SPGD Entity", "SPGD_ENTITY", "Resolved SPGD code for LGD entity", "0412"),
    YYYY("Calendar Year", "YYYY", "Four-digit calendar year", "2026"),
    FY("Financial Year", "FY", "Financial year notation (e.g. 2026-27)", "2026-27"),
    MM("Month", "MM", "Two-digit month (01 to 12)", "10"),
    DD("Day", "DD", "Two-digit day of the month (01 to 31)", "16"),
    TIME_STAMP("Time Stamp", "TIME_STAMP", "Time formatted in hours, minutes and seconds", "14:30:15"),
    SEQ("Sequential Counter", "SEQ", "Incrementing sequence counter (numeric, auto-incremented)", "000001"),
    CHECKSUM("Checksum", "CHECKSUM", "Calculated checksum value (e.g. Luhn, Mod10, Verhoeff)", "7");

    private final String label;
    private final String value;
    private final String description;
    private final String sample;

    Placeholder(String label, String value, String description, String sample) {
        this.label = label;
        this.value = value;
        this.description = description;
        this.sample = sample;
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

    public String getSample() {
        return sample;
    }

    @JsonCreator
    public static Placeholder fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (Placeholder placeholder : values()) {
            if (placeholder.value.equalsIgnoreCase(value) || placeholder.name().equalsIgnoreCase(value)) {
                return placeholder;
            }
        }
        throw new IllegalArgumentException("Invalid Placeholder value: " + value);
    }
}
