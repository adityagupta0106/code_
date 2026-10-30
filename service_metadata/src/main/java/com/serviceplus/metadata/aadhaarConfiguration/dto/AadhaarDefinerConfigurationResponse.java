package com.serviceplus.metadata.aadhaarConfiguration.dto;


import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarApiType;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarAuthenticationType;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarConfigurationMode;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarMappingType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AadhaarDefinerConfigurationResponse implements Serializable {

    private String configurationId;
    private String configurationName;
    private AadhaarAuthenticationType authenticationType;
    private List<AadhaarConfigurationMode> configurationMode;
    private List<AadhaarDefinerApiDTO> apiConfigurations = new ArrayList<>();

    public static class AadhaarDefinerApiDTO implements Serializable {

        private AadhaarApiType apiType;
        private String displayName;
        private AadhaarDefinerPayloadDTO requestPayload;
        private AadhaarDefinerPayloadDTO responsePayload;

        public AadhaarDefinerApiDTO() {
        }

        public AadhaarApiType getApiType() {
            return apiType;
        }

        public void setApiType(AadhaarApiType apiType) {
            this.apiType = apiType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public AadhaarDefinerPayloadDTO getRequestPayload() {
            return requestPayload;
        }

        public void setRequestPayload(AadhaarDefinerPayloadDTO requestPayload) {
            this.requestPayload = requestPayload;
        }

        public AadhaarDefinerPayloadDTO getResponsePayload() {
            return responsePayload;
        }

        public void setResponsePayload(AadhaarDefinerPayloadDTO responsePayload) {
            this.responsePayload = responsePayload;
        }
    }

    public static class AadhaarDefinerPayloadDTO implements Serializable {

        private String schemaName;
        private List<AadhaarDefinerNodeDTO> nodes = new ArrayList<>();

        public AadhaarDefinerPayloadDTO() {
        }

        public String getSchemaName() {
            return schemaName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
        }

        public List<AadhaarDefinerNodeDTO> getNodes() {
            return nodes;
        }

        public void setNodes(List<AadhaarDefinerNodeDTO> nodes) {
            this.nodes = nodes;
        }
    }

    public static class AadhaarDefinerNodeDTO implements Serializable {

        private String code;
        private String name;
        private String dataType;
        private String xpath;
        private AadhaarMappingType mappingType;
        private AadhaarDefinerMappingDTO mapping;

        public AadhaarDefinerNodeDTO() {
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getXpath() {
            return xpath;
        }

        public void setXpath(String xpath) {
            this.xpath = xpath;
        }

        public AadhaarMappingType getMappingType() {
            return mappingType;
        }

        public void setMappingType(AadhaarMappingType mappingType) {
            this.mappingType = mappingType;
        }

        public AadhaarDefinerMappingDTO getMapping() {
            return mapping;
        }

        public void setMapping(AadhaarDefinerMappingDTO mapping) {
            this.mapping = mapping;
        }
    }

    public static class AadhaarDefinerMappingDTO implements Serializable {

        private String fieldCode;
        private String fieldName;
        private String value;
        private String expectedValue;

        public AadhaarDefinerMappingDTO() {
        }

        public String getFieldCode() {
            return fieldCode;
        }

        public void setFieldCode(String fieldCode) {
            this.fieldCode = fieldCode;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getExpectedValue() {
            return expectedValue;
        }

        public void setExpectedValue(String expectedValue) {
            this.expectedValue = expectedValue;
        }
    }

    public AadhaarDefinerConfigurationResponse() {
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public AadhaarAuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AadhaarAuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public List<AadhaarConfigurationMode> getConfigurationMode() {
        return configurationMode;
    }

    public void setConfigurationMode(List<AadhaarConfigurationMode> configurationMode) {
        this.configurationMode = configurationMode;
    }

    public List<AadhaarDefinerApiDTO> getApiConfigurations() {
        return apiConfigurations;
    }

    public void setApiConfigurations(List<AadhaarDefinerApiDTO> apiConfigurations) {
        this.apiConfigurations = apiConfigurations;
    }
}
