package com.serviceplus.metadata.aadhaarConfiguration.model;

public enum AadhaarType {

    NIC(1),
    HARYANA(2);

    private final Integer id;

    AadhaarType(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
