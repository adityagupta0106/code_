package com.serviceplus.formDesigner.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO;
import com.serviceplus.formDesigner.request.model.ApplicationData;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.service.FormRedisService;

import static com.serviceplus.formDesigner.util.ApplicationConstants.*;

@Component
public class ApplicationDataCommonUtil {
	@Autowired
	private FormRedisService formRedisService;

    private static final Logger LOGGER = LogManager.getLogger("applicationDataLogger");


    public void encryptApplicationAttributeData(String encryptionKeyAtRest, String redisKey,Boolean globalEncFlag,ApplicationTemplate applicationTemplate,ApplicationData applicationData) throws Exception {
		String[] encryptionKeyArray = encryptionKeyAtRest.split("\\.");
		String key = encryptionKeyArray[0];
		String iv = encryptionKeyArray[1];
		FormProcessCacheDTO formProcessCacheDTO;

		ObjectMapper objectMapper = new ObjectMapper();
		String stringFormDataObject = objectMapper.writeValueAsString(applicationData.getFormData());
		JSONObject jsonFormDataObject = new JSONObject(stringFormDataObject);

		if (formRedisService.dataExists(redisKey)) {
			formProcessCacheDTO = (FormProcessCacheDTO) formRedisService.getDataFromRedis(redisKey);
		} else {
			formProcessCacheDTO = formRedisService.getAttributeDetails(applicationTemplate.getFormJsonData(),
					applicationTemplate.getFormId(),redisKey,globalEncFlag);
		}
		if (formProcessCacheDTO.getAttributes().size() > 0) {
			List<FormProcessCacheDTO.Attribute> list = formProcessCacheDTO.getAttributes();
			for (FormProcessCacheDTO.Attribute fAttribute : list) {
				Boolean isEncryptionDataAtRest = fAttribute.getIsEncryptionDataAtRest();
				if (isEncryptionDataAtRest) {
					String uniqueId = fAttribute.getAttributeId();
					Object attrValue = jsonFormDataObject.opt(uniqueId);
					String encryptClientData = CommonUtil.encryptClientData(attrValue, key, iv);
					jsonFormDataObject.put(uniqueId, encryptClientData);
				}
			}

			Map<String, Object> updatedFormData = objectMapper.readValue(jsonFormDataObject.toString(),
					new TypeReference<Map<String, Object>>() {
					});

			applicationData.setFormData(updatedFormData);

		}
	}
	public void decryptApplicationAttributeData(ApplicationTemplate applicationTemplate,ApplicationData applicationData, String encryptionKeyAtRest, String redisKey, Boolean globalFormEncripted) throws Exception {
		String[] encryptionKeyArray = encryptionKeyAtRest.split("\\.");
		String key = encryptionKeyArray[0];
		String iv = encryptionKeyArray[1];
		FormProcessCacheDTO formProcessCacheDTO;
		
		ObjectMapper objectMapper = new ObjectMapper();
		String stringFormDataObject = objectMapper.writeValueAsString(applicationData.getFormData());
		JSONObject jsonFormDataObject = new JSONObject(stringFormDataObject);
		
		if (formRedisService.dataExists(redisKey)) {
			formProcessCacheDTO = (FormProcessCacheDTO) formRedisService.getDataFromRedis(redisKey);
		} else {
			formProcessCacheDTO = formRedisService.getAttributeDetails(applicationTemplate.getFormJsonData(),
					applicationTemplate.getFormId(), redisKey, globalFormEncripted);
		}
		if (formProcessCacheDTO.getAttributes().size() > 0) {
			List<FormProcessCacheDTO.Attribute> list = formProcessCacheDTO.getAttributes();
			for (FormProcessCacheDTO.Attribute fAttribute : list) {
				Boolean isEncryptionDataAtRest = fAttribute.getIsEncryptionDataAtRest();
				if (isEncryptionDataAtRest) {
					String uniqueId = fAttribute.getAttributeId();
					Object attrValue = jsonFormDataObject.opt(uniqueId);
					Object encryptClientData = CommonUtil.decryptClientData(attrValue.toString(), key, iv);
					jsonFormDataObject.put(uniqueId, encryptClientData);
				}
			}
			
			Map<String, Object> updatedFormData = objectMapper.readValue(jsonFormDataObject.toString(),
					new TypeReference<Map<String, Object>>() {
			});
			
			applicationData.setFormData(updatedFormData);
			
		}
	}

    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllWorkflowAttrVal(ApplicationTemplate applicationTemplate, ApplicationData applicationData) throws Exception {

        Map<String, Object> workflow = new HashMap<>();

        Map<String, Object> applData = (Map<String, Object>) applicationData.getFormData();

        LOGGER.info("Starting workflow attribute extraction | formId={} | applicationDataFields={}", applicationTemplate.getFormId(), applData != null ? applData.keySet() : Collections.emptySet());

        ObjectMapper mapper = new ObjectMapper();

        JSONObject formJsonObject = new JSONObject(mapper.writeValueAsString(applicationTemplate.getFormJsonData()));

        JSONArray formElements = formJsonObject.optJSONArray("formElements");

        if (formElements == null) {
            return workflow;
        }

        for (int i = 0; i < formElements.length(); i++) {

            JSONObject element = formElements.optJSONObject(i);

            if (element == null) {
                LOGGER.warn("No formElements found | formId={}", applicationTemplate.getFormId());
                continue;
            }

            processElement(element, applData, workflow);

            JSONArray zones = element.optJSONArray("zones");

            if (zones != null) {
                processZones(zones, applData, workflow);
            }
        }

        LOGGER.info("Workflow attribute extraction completed | formId={} | workflowData={}", applicationTemplate.getFormId(), workflow);

        return workflow;
    }

    private void processZones(JSONArray zones, Map<String, Object> applData, Map<String, Object> workflow) {

        LOGGER.info("Processing zones | zoneCount={}", zones.length());

        for (int i = 0; i < zones.length(); i++) {

            Object item = zones.opt(i);

            if (item instanceof JSONObject element) {

                LOGGER.info("Processing zone element | index={} | uniqueId={} | typeId={}", i, element.optString("uniqueId"), element.optInt("typeId", -1));

                processElement(element, applData, workflow);

                // Check deeper nested zones if they exist
                JSONArray nestedZones = element.optJSONArray("zones");

                if (nestedZones != null) {
                    LOGGER.info("Processing nested zones | uniqueId={} | nestedZoneCount={}", element.optString("uniqueId"), nestedZones.length());
                    processZones(nestedZones, applData, workflow);
                }

            } else if (item instanceof JSONArray nestedArray) {
                // zones -> [ [ element1, element2, ... ] ]

                LOGGER.info("Processing nested zone array | index={} | size={}", i, nestedArray.length());
                processZones(nestedArray, applData, workflow);
            }
        }
    }

    private void processElement(JSONObject element, Map<String, Object> applData, Map<String, Object> workflow) {

        int typeId = element.optInt("typeId", -1);
        String uniqueId = element.optString("uniqueId", "");

        if (uniqueId.isEmpty()) {
            return;
        }

        if (!applData.containsKey(uniqueId)) {
            LOGGER.info("Element value not present in application data | uniqueId={} | typeId={}", uniqueId, typeId);
            return;
        }

        Object value = applData.get(uniqueId);

        LOGGER.info("Workflow element value found | uniqueId={} | typeId={} | value={}", uniqueId, typeId, value);

        switch (typeId) {

            case ATTR_TYPE_ACTION:
                workflow.put("action", value);
                LOGGER.info("Action attribute extracted | uniqueId={} | value={}", uniqueId, value);
                break;

            case ATTR_TYPE_TASK:
                workflow.put("task", value);
                LOGGER.info("Task  attribute extracted | uniqueId={} | value={}", uniqueId, value);
                break;

            case ATTR_TYPE_USER:
                workflow.put("user", value);
                LOGGER.info("User  attribute extracted | uniqueId={} | value={}", uniqueId, value);
                break;

            default:
                LOGGER.info("Non-workflow element | uniqueId={} | typeId={}", uniqueId, typeId);
                break;
        }
    }
}
