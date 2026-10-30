package com.serviceplus.formDesigner.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.domain.cache.CacheService;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO.Attribute;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO.LabelValue;
import com.serviceplus.formDesigner.util.ApplicationConstants;

@Service
public class FormRedisService {

	@Autowired
	private CacheService cacheService;

	public Boolean dataExists(String key) {		
		return cacheService.dataExists(key);
	}
	
	public Object getDataFromRedis(String key) {		
		return cacheService.get(key);
	}
	// Edit Form , delete this form record from redis also
	public void deleteRecord(String formId) {
		cacheService.delete(formId);
	}
	// Delete Form , delete this form record from redis also


	// save this record on redis
	public void copyRecordOnCache(FormProcessCacheDTO cacheDTO,String key) {
		cacheService.saveJson(cacheDTO,key);
	}

	// Process Form Data and fetch records
	public FormProcessCacheDTO getAttributeDetails(Object formJsonData, String formId, String key,
			Boolean globalFormEncripted) {

		FormProcessCacheDTO formProcessCacheDTO = new FormProcessCacheDTO();
		ArrayList<FormProcessCacheDTO.Attribute> dtoList = new ArrayList<FormProcessCacheDTO.Attribute>();

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			String stringFormTemplateObject = objectMapper.writeValueAsString(formJsonData);

			JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);

			JSONArray formElements = jsonFormTemplateObject.getJSONArray("formElements");

			if (!Boolean.TRUE.equals(globalFormEncripted)) {
				JSONObject globalSetting = jsonFormTemplateObject.optJSONObject("globalSetting");

				if (globalSetting != null) {
					globalFormEncripted = globalSetting.optBoolean("isEncryptionDataAtRest", false);
				} else {
					globalFormEncripted = false;
				}
			}

			for (int i = 0; i < formElements.length(); i++) {

				JSONObject element = formElements.getJSONObject(i);

				dtoList.addAll(getIdAndLabel(element, globalFormEncripted));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		formProcessCacheDTO.setAttributes(dtoList);
		formProcessCacheDTO.setFormId(formId);

		copyRecordOnCache(formProcessCacheDTO, key);

		return formProcessCacheDTO;
	}

	private List<FormProcessCacheDTO.Attribute> getIdAndLabel(JSONObject element, Boolean globalFormEncripted)
			throws Exception {

		List<FormProcessCacheDTO.Attribute> attributesList = new ArrayList<FormProcessCacheDTO.Attribute>();

		String attributeId = element.getString("uniqueId");

		Integer attributetypeId = element.getInt("typeId");

		String attributetype = "";

		String parentId = element.optString("parentId", null);

		List<LabelValue> optionList = new ArrayList<FormProcessCacheDTO.LabelValue>();

		Boolean isEncryptionDataAtRest = false;

		String attributeLabel = "";

		try {
			JSONObject displaySettings = element.optJSONObject("displaySettings");

			if (displaySettings != null) {

				JSONObject labelJSONObject = displaySettings.optJSONObject("label");

				if (labelJSONObject != null) {
					attributeLabel = labelJSONObject.optString("value", "");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject validationSettings = element.optJSONObject("validationSettings");

		Integer minLenght = null;
		Integer maxLength = null;
		Integer min = null;
		Integer max = null;

		LocalDate minDate = null;
		LocalDate maxDate = null;

		Boolean required = false;

		String errorLabel = "";

		String validationType = null;

		if (validationSettings != null) {

			minLenght = getIntegerValidationValue(validationSettings, "minLength");

			maxLength = getIntegerValidationValue(validationSettings, "maxLength");

			JSONObject requiredObject = validationSettings.optJSONObject("required");

			if (requiredObject != null) {
				required = requiredObject.optBoolean("value", false);
			}
			JSONObject errorLabelObject = validationSettings.optJSONObject("errorLabel");

			if (errorLabelObject != null) {
				errorLabel = errorLabelObject.optString("value", null);
			}

			JSONObject minObject = validationSettings.optJSONObject("minDate");

			JSONObject maxObject = validationSettings.optJSONObject("maxDate");

			if (minObject != null) {
				String minDateStr = minObject.optString("value", null);
				if (minDateStr != null && !minDateStr.isEmpty()) {
					minDate = LocalDate.parse(minDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				}
			}

			if (maxObject != null) {
				String maxDateStr = maxObject.optString("value", null);
				maxDate = LocalDate.parse(maxDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
			min = getIntegerValidationValue(validationSettings, "min");
			max = getIntegerValidationValue(validationSettings, "max");
		}

		if (attributetypeId.equals(ApplicationConstants.ATTR_RADIO)
				|| attributetypeId.equals(ApplicationConstants.ATTR_CHECK_BOX)
				|| attributetypeId.equals(ApplicationConstants.ATTR_DROP_DOWN)) {

			JSONObject dataSettings = element.optJSONObject("dataSettings");

			if (dataSettings != null) {

				JSONObject options = dataSettings.optJSONObject("options");

				if (options != null) {

					JSONArray values = options.optJSONArray("value");

					if (values != null) {

						for (int i = 0; i < values.length(); i++) {

							JSONObject obj = values.getJSONObject(i);

							LabelValue lv = new LabelValue();

							lv.setLabel(obj.optString("label", ""));

							lv.setValue(obj.optString("value", ""));

							optionList.add(lv);
						}
					}
				}
			}
		}

		if (Boolean.TRUE.equals(globalFormEncripted)) {

			isEncryptionDataAtRest = true;

		} else {

			JSONObject attributeDisplaySettings = element.optJSONObject("displaySettings");

			if (attributeDisplaySettings != null && attributeDisplaySettings.has("dataRestStatus")) {

				JSONObject attributeDataRestStatus = attributeDisplaySettings.optJSONObject("dataRestStatus");

				if (attributeDataRestStatus != null) {

					isEncryptionDataAtRest = attributeDataRestStatus.optBoolean("value", false);
				}
			}
		}

		if (attributetypeId.equals(ApplicationConstants.ATTR_SECTION)
				|| attributetypeId.equals(ApplicationConstants.ATTR_DYNAMIC_ROW)) {

			List<FormProcessCacheDTO.Attribute> section = getSectionAttributes(element, isEncryptionDataAtRest);

			attributesList.addAll(section);

		} else if (attributetypeId.equals(ApplicationConstants.ATTR_PAGE_BREAK)) {

		} else {
			attributesList.add(new Attribute(attributeId, parentId, attributeLabel, attributetypeId, attributetype,
					required, min, max, minLenght, maxLength, errorLabel, maxDate, minDate, optionList,
					isEncryptionDataAtRest));
		}

		return attributesList;
	}

	private Integer getIntegerValidationValue(JSONObject validationSettings, String key) {

		JSONObject validationObject = validationSettings.optJSONObject(key);

		if (validationObject == null || validationObject.isNull("value")) {

			return null;
		}

		String value = validationObject.optString("value", null);

		if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {

			return null;
		}

		return Integer.valueOf(value.trim());
	}

	private String getStringValidationValue(JSONObject validationSettings, String key) {

		JSONObject validationObject = validationSettings.optJSONObject(key);

		if (validationObject == null || validationObject.isNull("value")) {

			return null;
		}

		String value = validationObject.optString("value", null);

		if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {

			return null;
		}

		return value.trim();
	}

	private List<FormProcessCacheDTO.Attribute> getSectionAttributes(JSONObject element, Boolean globalFormEncripted) {

		List<FormProcessCacheDTO.Attribute> section = new ArrayList<FormProcessCacheDTO.Attribute>();

		try {

			JSONArray sectionArray = element.optJSONArray("zones");

			if (sectionArray == null) {
				return section;
			}

			for (int rowIndex = 0; rowIndex < sectionArray.length(); rowIndex++) {

				JSONArray rows = sectionArray.optJSONArray(rowIndex);

				if (rows == null) {
					continue;
				}

				for (int colIndex = 0; colIndex < rows.length(); colIndex++) {

					JSONArray attributesArray = rows.optJSONArray(colIndex);

					if (attributesArray == null) {
						continue;
					}

					for (int attrIndex = 0; attrIndex < attributesArray.length(); attrIndex++) {

						JSONObject attributeObject = attributesArray.getJSONObject(attrIndex);

						section.addAll(getIdAndLabel(attributeObject, globalFormEncripted));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return section;
	}
}
