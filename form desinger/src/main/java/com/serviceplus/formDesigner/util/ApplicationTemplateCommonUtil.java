package com.serviceplus.formDesigner.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsRequest;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsRequest.FileUploadRequest;
import com.serviceplus.formDesigner.dto.LinkAttributeNodeDTO;
import com.serviceplus.formDesigner.dto.SpecificAttributesDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.enums.FormStandaloneStatus;
import com.serviceplus.formDesigner.enums.FormStatus;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.request.model.IApplicationTemplate;

public class ApplicationTemplateCommonUtil {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();
    
    public static String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

	public static Boolean isFormDataEncryptedInTransit(IApplicationTemplate applicationTemplate) throws Exception {
		Boolean flag = false;
		ObjectMapper objectmapper = new ObjectMapper();
		String formJsonDataStr = objectmapper.writeValueAsString(applicationTemplate.getFormJsonData());
		JSONObject formDataJsonObject = new JSONObject(formJsonDataStr);
		JSONObject globalSetting = formDataJsonObject.getJSONObject("globalSetting");
		boolean formEncripted = globalSetting.optBoolean("formEncrypted");
		flag = formEncripted;
		return flag;
	}
	
	public static Boolean isFormDataEncryptedAtRest(IApplicationTemplate applicationTemplate) throws Exception {
		Boolean flag = false;
		ObjectMapper objectmapper = new ObjectMapper();
		String formJsonDataStr = objectmapper.writeValueAsString(applicationTemplate.getFormJsonData());
		JSONObject formDataJsonObject = new JSONObject(formJsonDataStr);
		JSONObject globalSetting = formDataJsonObject.getJSONObject("globalSetting");
		boolean formEncripted = globalSetting.optBoolean("isEncryptionDataAtRest");
		if(!formEncripted) {
			JSONArray formElementsArray = formDataJsonObject.getJSONArray("formElements");
			for(int item =  0; item < formElementsArray.length(); item++) {
				JSONObject attribute = formElementsArray.getJSONObject(item);
				JSONObject attributeDisplaySettings = attribute.optJSONObject("displaySettings");
				if(attributeDisplaySettings != null) {
					boolean hasDataRestStatus = attributeDisplaySettings.has("dataRestStatus");
					if(hasDataRestStatus) {					
						JSONObject attributeDataRestStatus = attributeDisplaySettings.getJSONObject("dataRestStatus");
						boolean encryptedAtRest = attributeDataRestStatus.getBoolean("value");
						if(encryptedAtRest) {
							formEncripted = encryptedAtRest;
							break;
						}
					}
				}
			}
		}
		flag = formEncripted;
		return flag;
	}
	
	
	public static void setFileAttributesDTO(IApplicationTemplate applicationTemplate,
			CreateUploadSessionsRequest createUploadSessionsRequest) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		String formJsonDataStr = objectMapper.writeValueAsString(applicationTemplate.getFormJsonData());
		JSONObject formDataJsonObject = new JSONObject(formJsonDataStr);

		JSONArray formElements = formDataJsonObject.optJSONArray("formElements");

		List<FileUploadRequest> fileUploadRequests = new ArrayList<>();

		if (formElements != null) {
			for (int i = 0; i < formElements.length(); i++) {
				JSONObject attribute = formElements.optJSONObject(i);
				if (attribute != null) {
					collectFileAttributes(attribute, fileUploadRequests);
				}
			}
		}

		createUploadSessionsRequest.setFiles(fileUploadRequests);
	}

	private static void collectFileAttributes(JSONObject attribute, List<FileUploadRequest> fileUploadRequests) throws Exception {

		Integer typeId = attribute.optInt("typeId");
		if (ApplicationConstants.ATTR_FILE_OR_CAMERA.equals(typeId)) {

			String labelValue = "";

			JSONObject displaySettings = attribute.optJSONObject("displaySettings");
			if (displaySettings != null) {
				JSONObject label = displaySettings.optJSONObject("label");
				if (label != null) {
					labelValue = label.optString("value");
				}
			}

			FileUploadRequest fileUploadRequest = new FileUploadRequest();
			fileUploadRequest.setReferenceId(attribute.optString("uniqueId"));
			fileUploadRequest.setCategory(labelValue);
			fileUploadRequest.setFunctionality("form-file-up");

			setFileValidationAttributes(attribute, fileUploadRequest);

			fileUploadRequests.add(fileUploadRequest);
		}
		JSONArray zones = attribute.optJSONArray("zones");
		if (zones == null) {
			return;
		}

		for (int i = 0; i < zones.length(); i++) {

			JSONArray rows = zones.optJSONArray(i);
			if (rows == null) {
				continue;
			}

			for (int j = 0; j < rows.length(); j++) {

				JSONArray columns = rows.optJSONArray(j);
				if (columns == null) {
					continue;
				}

				for (int k = 0; k < columns.length(); k++) {

					JSONObject childAttribute = columns.optJSONObject(k);
					if (childAttribute != null) {
						collectFileAttributes(childAttribute, fileUploadRequests);
					}
				}
			}
		}
	}

	private static void setFileValidationAttributes( JSONObject attribute, FileUploadRequest fileUploadRequest) {

	    // Set default values first
	    setDefaultValueForFileUpload(fileUploadRequest);

	    JSONObject validationSettings =
	            attribute.optJSONObject("validationSettings");

	    if (validationSettings == null) {
	        return;
	    }
	    JSONObject maxFileSize = validationSettings.optJSONObject("maxFileSize");

	    if (maxFileSize != null) {
	        String value = maxFileSize.optString("value", "").trim();
	        if (!value.isEmpty()) {
	            try {
	                long maxFileSizeKb = Long.parseLong(value);

	                fileUploadRequest.setMaxFileSize(
	                        maxFileSizeKb * 1024L
	                );

	            } catch (NumberFormatException ignored) {
	                // Keep default value
	            }
	        }
	    }

	    JSONObject minFileSize = validationSettings.optJSONObject("minFileSize");
	    if (minFileSize != null) {
	        String value = minFileSize.optString("value", "").trim();
	        if (!value.isEmpty()) {
	            try {
	                long minFileSizeKb = Long.parseLong(value);
	                fileUploadRequest.setMinFileSize( minFileSizeKb * 1024L );

	            } catch (NumberFormatException ignored) {
	                // Keep default value
	            }
	        }
	    }

	    JSONObject accept = validationSettings.optJSONObject("accept");
	    if (accept != null) {
	        JSONArray acceptValues = accept.optJSONArray("value");
	        if (acceptValues != null && acceptValues.length() > 0) {
	            List<String> allowedMimeTypes = new ArrayList<>();
	            for (int i = 0; i < acceptValues.length(); i++) {
	                JSONObject mimeObject = acceptValues.optJSONObject(i);

	                if (mimeObject == null) {
	                    continue;
	                }
	                String mimeType = mimeObject.optString("value", "").trim();

	                if (!mimeType.isEmpty()) {
	                    allowedMimeTypes.add(mimeType);
	                }
	            }

	            if (!allowedMimeTypes.isEmpty()) {
	                fileUploadRequest.setAllowedMime(allowedMimeTypes);
	            }
	        }
	    }
	}
	
	public static List<LinkAttributeNodeDTO> extractAttributesForLink(Object formJsonData) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		JSONObject formJsonObject = new JSONObject(mapper.writeValueAsString(formJsonData));

		JSONArray formElements = formJsonObject.optJSONArray("formElements");

		List<LinkAttributeNodeDTO> response = new ArrayList<>();

		if (formElements == null) {
			return response;
		}

		for (int i = 0; i < formElements.length(); i++) {

			JSONObject element = formElements.optJSONObject(i);

			if (element == null) {
				continue;
			}

			response.add(buildNode(element));
		}

		return response;
	}

    private static LinkAttributeNodeDTO buildNode(JSONObject element)
            throws Exception {

        Integer typeId = element.optInt("typeId");
        String uniqueId = element.optString("uniqueId");
        String label = getLabel(element);

        LinkAttributeNodeDTO builderNode = new LinkAttributeNodeDTO();
        
                
        builderNode.setTypeId(typeId);
        builderNode.setLabel(label);
        builderNode.setValue(uniqueId);
        builderNode.setType("field");

        // Section handling
		if (ApplicationConstants.ATTR_SECTION.equals(typeId) || ApplicationConstants.ATTR_DYNAMIC_ROW.equals(typeId)) {
			builderNode.setType("group");
			List<LinkAttributeNodeDTO> children = extractSectionChildren(element);
			builderNode.setChildren(children);
		}

        return builderNode;

    }

    private static List<LinkAttributeNodeDTO> extractSectionChildren(JSONObject sectionObject) throws Exception {

        List<LinkAttributeNodeDTO> children = new ArrayList<>();

        JSONArray zones = sectionObject.optJSONArray("zones");

        if (zones == null) {
            return children;
        }

        for (int zoneIndex = 0; zoneIndex < zones.length(); zoneIndex++) {

            JSONArray zone = zones.optJSONArray(zoneIndex);

            if (zone == null) {
                continue;
            }

            for (int rowIndex = 0; rowIndex < zone.length(); rowIndex++) {

                JSONArray row = zone.optJSONArray(rowIndex);

                if (row == null) {
                    continue;
                }

                for (int colIndex = 0; colIndex < row.length(); colIndex++) {

                    JSONObject childElement =
                            row.optJSONObject(colIndex);

                    if (childElement == null) {
                        continue;
                    }

                    children.add(buildNode(childElement));
                }
            }
        }

        return children;
    }

    private static String getLabel(JSONObject element) {

        return Optional.ofNullable(
                        element.optJSONObject("displaySettings"))
                .map(ds -> ds.optJSONObject("label"))
                .map(label -> label.optString("value"))
                .orElse("");
    }
    
	private static void setDefaultValueForFileUpload(FileUploadRequest fileUploadRequest) {
		fileUploadRequest.setMaxFileSize(5000000L);
		fileUploadRequest.setMinFileSize(1000L);
		fileUploadRequest.setAllowedMime(List.of("image/jpeg", "image/png", "application/pdf"));
		fileUploadRequest.setChunkedUpload(true);
		fileUploadRequest.setExpiresInMinutes(60);
	}

    public static List<SpecificAttributesDTO.Attribute> buildSpecificAttributesNode(JSONObject element, Integer typeId) {
        List<SpecificAttributesDTO.Attribute> attributesDTOs = new ArrayList<SpecificAttributesDTO.Attribute>();

        Integer dbTypeId = element.optInt("typeId");
        String uniqueId = element.optString("uniqueId");
        String entityLevel=element.optString("entityLevel",null);
        Integer levelHierachy=null;
        if(entityLevel!=null) {
        	try {
        		JSONObject entityLevelJson = element.optJSONObject("entityLevel");

        		if (entityLevelJson != null) {
        		    JSONObject valueJson = entityLevelJson.optJSONObject("value");

        		    if (valueJson != null) {
        		        levelHierachy = valueJson.optInt("value", 0);
        		    }
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        String label = getLabel(element);

        if (dbTypeId.equals(typeId)) {
            SpecificAttributesDTO.Attribute attributesDTO = new SpecificAttributesDTO.Attribute();
            attributesDTO.setAttributeLabel(label);
            attributesDTO.setAttributeId(uniqueId);
            attributesDTO.setTypeId(typeId);
            attributesDTO.setHierachyLevel(levelHierachy);
            attributesDTOs.add(attributesDTO);
        }

        // Section handling
        if (ApplicationConstants.ATTR_SECTION.equals(dbTypeId) || ApplicationConstants.ATTR_DYNAMIC_ROW.equals(dbTypeId)) {
            JSONArray zones = element.optJSONArray("zones");
            if (zones != null) {
                for (int i = 0; i < zones.length(); i++) {

                    JSONArray rows = zones.optJSONArray(i);
                    if (rows == null) {
                        continue;
                    }
                    for (int j = 0; j < rows.length(); j++) {
                        JSONArray cols = rows.optJSONArray(j);
                        if (cols == null) {
                            continue;
                        }
                        for (int k = 0; k < cols.length(); k++) {

                            JSONObject child = cols.optJSONObject(k);

                            if (child != null) {
                                attributesDTOs.addAll(
                                        buildSpecificAttributesNode(child, typeId));
                            }
                        }
                    }
                }
            }
        }


        return attributesDTOs;

    }
	public static Boolean isGlobalForm(IApplicationTemplate applicationTemplate) {

		Boolean isGlobal = false;
		ObjectMapper objectMapper = new ObjectMapper();
		String stringFormTemplateObject;
		try {
			stringFormTemplateObject = objectMapper.writeValueAsString(applicationTemplate.getFormJsonData());
			JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);
			JSONObject globalSetting = jsonFormTemplateObject.getJSONObject("globalSetting");
			if (globalSetting != null) {
				isGlobal = globalSetting.optBoolean("isGlobal");
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return isGlobal;
	}
	
    public static Optional<Map<String, Object>> extractFormSummary(IApplicationTemplate template, Boolean standalone) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(template);
			JSONObject root = new JSONObject(json);

			String formName = null;
			if (root.has("formJsonData") && !root.isNull("formJsonData")) {
				JSONObject formJsonData = root.optJSONObject("formJsonData");
				if (formJsonData != null) {
					JSONObject globalSetting = formJsonData.optJSONObject("globalSetting");
					if (globalSetting != null) {
						formName = globalSetting.optString("formName", null);
					}
				}
			}

			Boolean globalForm = isGlobalForm(template);
			Map<String, Object> summary = new HashMap<>();
			summary.put("formId", template.getFormId());
			summary.put("formName", formName);
			summary.put("global", globalForm);
			summary.put("status", template.getStatus());
			if(standalone && template.getStatus().equals(FormStatus.PUBLISHED.getValue())) {	
				summary.put("configureTinyUrl",((ApplicationTemplateStandalone) template).getConfigureTinyUrl());
				summary.put("prodTinyUrl",((ApplicationTemplateStandalone) template).getProdTinyUrl());
				summary.put("tinyUrlToken",((ApplicationTemplateStandalone) template).getTinyUrlToken());
			}
			return Optional.of(summary);

		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
    
    public static Optional<Map<String, Object>> extractStandaloneFormSummary(ApplicationTemplateStandalone template) {
    	try {
    		ObjectMapper objectMapper = new ObjectMapper();
    		String json = objectMapper.writeValueAsString(template);
    		JSONObject root = new JSONObject(json);
    		
    		String formName = null;
    		if (root.has("formJsonData") && !root.isNull("formJsonData")) {
    			JSONObject formJsonData = root.optJSONObject("formJsonData");
    			if (formJsonData != null) {
    				JSONObject globalSetting = formJsonData.optJSONObject("globalSetting");
    				if (globalSetting != null) {
    					formName = globalSetting.optString("formName", null);
    				}
    			}
    		}
    		
    		Map<String, Object> summary = new HashMap<>();
    		summary.put("formId", template.getFormId());
    		summary.put("formName", formName);
    		summary.put("status", FormStandaloneStatus.fromValue(template.getStatus()));
    		summary.put("isOnline", (template.getIsOnline()));
			summary.put("configureTinyUrl",(template.getConfigureTinyUrl()));
			summary.put("prodTinyUrl",(template.getProdTinyUrl()));
			summary.put("tinyUrlToken",(template.getTinyUrlToken()));
    		return Optional.of(summary);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return Optional.empty();
    	}
    }

    public static void setFormData(IApplicationTemplate applicationTemplate, UserSessionDTO userSessionDetails) {
    	long userId = (userSessionDetails != null && userSessionDetails.getUserID() != null) ? userSessionDetails.getUserID() : 0;
        int departmentId = (userSessionDetails != null && userSessionDetails.getEntityId() != null) ? userSessionDetails.getEntityId() : 0;
        int stateId = (userSessionDetails != null && userSessionDetails.getClcId() != null) ? userSessionDetails.getClcId() : 0;
        int locationId = (userSessionDetails != null && userSessionDetails.getLocationId() != null) ? userSessionDetails.getLocationId() : 0;
        String tenantId = userSessionDetails != null ? userSessionDetails.getTenantId() : null;
        
        applicationTemplate.setTenantId(tenantId);
        applicationTemplate.setStateId(stateId);
        applicationTemplate.setDepartmentId(departmentId);
        applicationTemplate.setLocationId(locationId);
        applicationTemplate.setCreatedBy(userId);
    	
    }
    
    public static Optional<JSONObject> findAttributeByUniqueId(Object formJsonData, String attributeId) throws Exception {

		if (formJsonData == null || attributeId == null || attributeId.isBlank()) {
			return Optional.empty();
		}

		ObjectMapper mapper = new ObjectMapper();

		String formJsonDataStr = mapper.writeValueAsString(formJsonData);

		JSONObject formJsonObject = new JSONObject(formJsonDataStr);

		JSONArray formElements = formJsonObject.optJSONArray("formElements");

		if (formElements == null) {
			return Optional.empty();
		}

		for (int i = 0; i < formElements.length(); i++) {

			JSONObject element = formElements.optJSONObject(i);

			if (element == null) {
				continue;
			}

			Optional<JSONObject> result = findAttributeRecursive(element, attributeId);

			if (result.isPresent()) {
				return result;
			}
		}

		return Optional.empty();
	}
    
	private static Optional<JSONObject> findAttributeRecursive(JSONObject element, String attributeId) {

		String uniqueId = element.optString("uniqueId", null);

		if (attributeId.equals(uniqueId)) {
			return Optional.of(element);
		}

		JSONArray zones = element.optJSONArray("zones");

		if (zones == null) {
			return Optional.empty();
		}

		for (int i = 0; i < zones.length(); i++) {

			JSONArray rows = zones.optJSONArray(i);

			if (rows == null) {
				continue;
			}

			for (int j = 0; j < rows.length(); j++) {

				JSONArray columns = rows.optJSONArray(j);

				if (columns == null) {
					continue;
				}

				for (int k = 0; k < columns.length(); k++) {

					JSONObject child = columns.optJSONObject(k);

					if (child == null) {
						continue;
					}

					Optional<JSONObject> result = findAttributeRecursive(child, attributeId);

					if (result.isPresent()) {
						return result;
					}
				}
			}
		}

		return Optional.empty();
	}
}
