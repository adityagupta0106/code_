package com.serviceplus.metadata.catalogue.enums;

public enum ServiceCatalogueConfigurationStatus {

    DRAFT(0),
    ACTIVE(1),
    DEACTIVATED(2);

    private final int value;

    ServiceCatalogueConfigurationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ServiceCatalogueConfigurationStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }

        for (ServiceCatalogueConfigurationStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid configuration status: " + value);
    }
}
