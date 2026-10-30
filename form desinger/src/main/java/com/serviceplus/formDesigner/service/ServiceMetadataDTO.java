package com.serviceplus.formDesigner.service;

import com.serviceplus.formDesigner.dto.UsedAttributeListDTO;

public class ServiceMetadataDTO {

    private Integer serviceId;
    private UsedAttributeListDTO usedAttributeList;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public UsedAttributeListDTO getUsedAttributeList() {
        return usedAttributeList;
    }

    public void setUsedAttributeList(UsedAttributeListDTO usedAttributeList) {
        this.usedAttributeList = usedAttributeList;
    }
}
