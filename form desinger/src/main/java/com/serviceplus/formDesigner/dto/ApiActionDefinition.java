package com.serviceplus.formDesigner.dto;

import java.util.List;

public class ApiActionDefinition {

    private String id;
    private String actionType;
    private Integer sequenceId;
    private String description;
    private TriggerDefinition trigger;
    private ApiDefinition apiDefinition;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TriggerDefinition getTrigger() {
        return trigger;
    }

    public void setTrigger(TriggerDefinition trigger) {
        this.trigger = trigger;
    }

    public ApiDefinition getApiDefinition() {
        return apiDefinition;
    }

    public void setApiDefinition(ApiDefinition apiDefinition) {
        this.apiDefinition = apiDefinition;
    }


    public static class TriggerDefinition {

        private String event;
        private String lifecycleTrigger;

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getLifecycleTrigger() {
            return lifecycleTrigger;
        }

        public void setLifecycleTrigger(String lifecycleTrigger) {
            this.lifecycleTrigger = lifecycleTrigger;
        }
    }


    public static class ApiDefinition {

        private String activeSection;
        private ApiRequestDefinition request;
        private List<DataMappingDefinition> dataMapping;

        public String getActiveSection() {
            return activeSection;
        }

        public void setActiveSection(String activeSection) {
            this.activeSection = activeSection;
        }

        public ApiRequestDefinition getRequest() {
            return request;
        }

        public void setRequest(ApiRequestDefinition request) {
            this.request = request;
        }

        public List<DataMappingDefinition> getDataMapping() {
            return dataMapping;
        }

        public void setDataMapping(List<DataMappingDefinition> dataMapping) {
            this.dataMapping = dataMapping;
        }
    }


    public static class ApiRequestDefinition {

        private String callType;
        private Boolean storeResponse;
        private Boolean serverValidation;
        private String endpoint;
        private String method;

        private List<ApiKeyValueDefinition> headers;
        private List<ApiKeyValueDefinition> params;
        private ApiBodyDefinition body;
        private ApiAuthorizationDefinition authorization;

        public String getCallType() {
            return callType;
        }

        public void setCallType(String callType) {
            this.callType = callType;
        }

        public Boolean getStoreResponse() {
            return storeResponse;
        }

        public void setStoreResponse(Boolean storeResponse) {
            this.storeResponse = storeResponse;
        }

        public Boolean getServerValidation() {
            return serverValidation;
        }

        public void setServerValidation(Boolean serverValidation) {
            this.serverValidation = serverValidation;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public List<ApiKeyValueDefinition> getHeaders() {
            return headers;
        }

        public void setHeaders(List<ApiKeyValueDefinition> headers) {
            this.headers = headers;
        }

        public List<ApiKeyValueDefinition> getParams() {
            return params;
        }

        public void setParams(List<ApiKeyValueDefinition> params) {
            this.params = params;
        }

        public ApiBodyDefinition getBody() {
            return body;
        }

        public void setBody(ApiBodyDefinition body) {
            this.body = body;
        }

        public ApiAuthorizationDefinition getAuthorization() {
            return authorization;
        }

        public void setAuthorization(ApiAuthorizationDefinition authorization) {
            this.authorization = authorization;
        }
    }


    public static class ApiKeyValueDefinition {

        private String label;
        private String value;
        private String fieldValue;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getFieldValue() {
            return fieldValue;
        }

        public void setFieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
        }
    }


    public static class ApiBodyDefinition {

        private String type;
        private String raw;
        private String rawFormat;

        private List<ApiKeyValueDefinition> formData;
        private List<ApiKeyValueDefinition> urlEncoded;
        private List<ApiKeyValueDefinition> rawJson;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public String getRawFormat() {
            return rawFormat;
        }

        public void setRawFormat(String rawFormat) {
            this.rawFormat = rawFormat;
        }

        public List<ApiKeyValueDefinition> getFormData() {
            return formData;
        }

        public void setFormData(List<ApiKeyValueDefinition> formData) {
            this.formData = formData;
        }

        public List<ApiKeyValueDefinition> getUrlEncoded() {
            return urlEncoded;
        }

        public void setUrlEncoded(List<ApiKeyValueDefinition> urlEncoded) {
            this.urlEncoded = urlEncoded;
        }

        public List<ApiKeyValueDefinition> getRawJson() {
            return rawJson;
        }

        public void setRawJson(List<ApiKeyValueDefinition> rawJson) {
            this.rawJson = rawJson;
        }
    }


    public static class ApiAuthorizationDefinition {

        private String type;
        private ApiKeyDefinition apiKey;
        private BearerDefinition bearer;
        private BasicDefinition basic;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ApiKeyDefinition getApiKey() {
            return apiKey;
        }

        public void setApiKey(ApiKeyDefinition apiKey) {
            this.apiKey = apiKey;
        }

        public BearerDefinition getBearer() {
            return bearer;
        }

        public void setBearer(BearerDefinition bearer) {
            this.bearer = bearer;
        }

        public BasicDefinition getBasic() {
            return basic;
        }

        public void setBasic(BasicDefinition basic) {
            this.basic = basic;
        }
    }


    public static class ApiKeyDefinition {

        private String key;
        private String value;
        private String addTo;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getAddTo() {
            return addTo;
        }

        public void setAddTo(String addTo) {
            this.addTo = addTo;
        }
    }

    public static class BearerDefinition {

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class BasicDefinition {

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


    public static class DataMappingDefinition {

        private String targetType;
        private String label;
        private String dataType;
        private String explicitKeys;
        private String valueFormat;
        private String labelName;
        private String valueName;
        private String mapWith;

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getExplicitKeys() {
            return explicitKeys;
        }

        public void setExplicitKeys(String explicitKeys) {
            this.explicitKeys = explicitKeys;
        }

        public String getValueFormat() {
            return valueFormat;
        }

        public void setValueFormat(String valueFormat) {
            this.valueFormat = valueFormat;
        }

        public String getLabelName() {
            return labelName;
        }

        public void setLabelName(String labelName) {
            this.labelName = labelName;
        }

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public String getMapWith() {
            return mapWith;
        }

        public void setMapWith(String mapWith) {
            this.mapWith = mapWith;
        }
    }
}
