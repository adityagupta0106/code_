package com.serviceplus.metadata.esign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsignConfigurationKafkaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventType;
    private Integer serviceId;
    private String tenantId;
    private ServiceEsignConfigurationDTO esignConfiguration;
    private EsignTemplateJSONDTO template;

    public EsignConfigurationKafkaDTO() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public ServiceEsignConfigurationDTO getEsignConfiguration() {
        return esignConfiguration;
    }

    public void setEsignConfiguration(ServiceEsignConfigurationDTO esignConfiguration) {
        this.esignConfiguration = esignConfiguration;
    }

    public EsignTemplateJSONDTO getTemplate() {
        return template;
    }

    public void setTemplate(EsignTemplateJSONDTO template) {
        this.template = template;
    }

}
