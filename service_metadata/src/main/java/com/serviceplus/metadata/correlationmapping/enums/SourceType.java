package com.serviceplus.metadata.correlationmapping.enums;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.serviceplus.metadata.exception.SPRuntimeError;
public enum SourceType {

    SERVICE("S", "Service"),

    EXTERNAL_DATA_SET("E", "External Data Set");

    private final String value;
    private final String label;

    SourceType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static SourceType fromValue(String value) {

        return Arrays.stream(SourceType.values())
                .filter(sourceType ->
                        sourceType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new SPRuntimeError(
                                "Invalid sourceType: " + value,
                                HttpStatus.BAD_REQUEST));
    }
}