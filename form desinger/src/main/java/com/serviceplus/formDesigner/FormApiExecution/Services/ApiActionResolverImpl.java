package com.serviceplus.formDesigner.FormApiExecution.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.FormApiExecution.Interface.ApiActionResolver;
import com.serviceplus.formDesigner.FormApiExecution.Model.ApiActionContext;
import com.serviceplus.formDesigner.dto.ApiActionDefinition;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
public class ApiActionResolverImpl implements ApiActionResolver {

    private final ApplicationTemplateRepository repository;
    private final ObjectMapper objectMapper;

    public ApiActionResolverImpl(ApplicationTemplateRepository repository, ObjectMapper objectMapper) {

        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiActionContext resolve(String formId, String apiId) {

        ApplicationTemplate template = repository.findByFormId(formId);

        if (isNull(template) || isNull(template.getFormJsonData())) {
            return null;
        }

        Map<String, Object> form = objectMapper.convertValue(template.getFormJsonData(), Map.class);


         //First check global actions.
        ApiActionContext context = findInGlobalActions(form, apiId);

        if (context != null) {
            return context;
        }

        //Then check form elements and their zones recursively.
        return findInAttributeActions(form, apiId);
    }

    @SuppressWarnings("unchecked")
    private ApiActionContext findInGlobalActions(Map<String, Object> form, String apiId) {

        Object globalSettingObj = form.get("globalSetting");

        if (!(globalSettingObj instanceof Map<?, ?> globalSetting)) {
            return null;
        }

        Object globalActionsObj = globalSetting.get("globalActions");

        if (!(globalActionsObj instanceof Map<?, ?> globalActions)) {
            return null;
        }

        Object actionsObj = globalActions.get("actionCreators");

        if (!(actionsObj instanceof List<?> actions)) {
            return null;
        }

        return find((List<Map<String, Object>>) actions, apiId, null, true);
    }


    @SuppressWarnings("unchecked")
    private ApiActionContext findInAttributeActions(Map<String, Object> form, String apiId) {

        Object elementsObj = form.get("formElements");

        if (!(elementsObj instanceof List<?> elements)) {
            return null;
        }

        for (Object item : elements) {

            if (!(item instanceof Map<?, ?> element)) {
                continue;
            }

            ApiActionContext context = findInElement((Map<String, Object>) element, apiId);

            if (context != null) {
                return context;
            }
        }

        return null;
    }

    /**
     * Process one element and recursively process its zones.
     */
    @SuppressWarnings("unchecked")
    private ApiActionContext findInElement(Map<String, Object> element, String apiId) {

        String attributeId = (String) element.get("uniqueId");

        /*
         * Check API actions directly attached to this element.
         */
        Object apiSettingsObj = element.get("apiSettings");

        if (apiSettingsObj instanceof Map<?, ?> apiSettings) {

            Object actionsObj = apiSettings.get("actionCreators");

            if (actionsObj instanceof List<?> actions) {

                ApiActionContext context = find((List<Map<String, Object>>) actions, apiId, attributeId, false);

                if (context != null) {
                    return context;
                }
            }
        }

        return findInZones(element.get("zones"), apiId);
    }

    @SuppressWarnings("unchecked")
    private ApiActionContext findInZones(Object zonesObj, String apiId) {

        if (!(zonesObj instanceof List<?> zones)) {
            return null;
        }

        for (Object item : zones) {

            if (item instanceof Map<?, ?> zoneElement) {

                ApiActionContext context = findInElement((Map<String, Object>) zoneElement, apiId);

                if (context != null) {
                    return context;
                }
            }

            /*
             * Nested zone array.
             */
            else if (item instanceof List<?> nestedZones) {

                ApiActionContext context = findInZones(nestedZones, apiId);

                if (context != null) {
                    return context;
                }
            }
        }

        return null;
    }

    /**
     * Find API action by API ID.
     */
    private ApiActionContext find(List<Map<String, Object>> actions, String apiId, String attributeId, boolean global) {

        if (actions == null) {
            return null;
        }

        for (Map<String, Object> action : actions) {

            if (!Objects.equals(action.get("id"), apiId)) {
                continue;
            }

            ApiActionContext context = new ApiActionContext();

            context.setGlobalAction(global);
            context.setOwnerAttributeId(attributeId);

            context.setAction(objectMapper.convertValue(action, ApiActionDefinition.class));

            return context;
        }

        return null;
    }
}