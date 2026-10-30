package com.serviceplus.metadata.aadhaarConfiguration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuaApiMessageMappingResponse {

    private Long messageId;

    private String messageType;

    private String rootElement;

    private List<AuaApiFieldMappingResponse> fields;

    public List<AuaApiFieldMappingResponse> getFields() {
        return fields;
    }

    public void setFields(List<AuaApiFieldMappingResponse> fields) {
        this.fields = fields;
    }

    public String getRootElement() {
        return rootElement;
    }

    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
