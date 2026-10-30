package com.serviceplus.tracking.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.AttributeDataResponseDTO;
import com.serviceplus.tracking.dto.FormAttributeRequest;
import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.feignClient.FormDesignerFeignClient;
import com.serviceplus.tracking.repository.InboxSentBoxFilterConfigRepository;
import com.serviceplus.tracking.utility.ApplicationConstants;

@Service
public class ServiceTaskFilterService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskFilterService.class);

	private final InboxSentBoxFilterConfigRepository repository;

	private final FormDesignerFeignClient formDesignerFeignClient;

	public ServiceTaskFilterService(InboxSentBoxFilterConfigRepository repository,
			FormDesignerFeignClient formDesignerFeignClient) {

		this.repository = repository;
		this.formDesignerFeignClient = formDesignerFeignClient;
	}

	public ResponseEntity<?>  getFilterAttribute(Long serviceId,String taskId, Character attrType, List<Character> filterTypes) {

		try {
			List<AttributeDataResponseDTO> attributeDataResponseDTOs = new ArrayList<AttributeDataResponseDTO>();
			
			logger.info("Fetching filter attributes. serviceId={}, taskId={}", serviceId, taskId);

			List<InboxSentBoxFilterConfig> configs = repository.findByServiceIdAndTaskIdAndAttrTypeAndFilterTypeIn(serviceId, taskId, attrType, filterTypes);

			if (configs == null || configs.isEmpty()) {

				logger.info("No filter attributes found. serviceId={}, taskId={}", serviceId, taskId);

				return ResponseEntity.ok(attributeDataResponseDTOs);
			}

			logger.info("Found {} filter attribute configuration(s). serviceId={}, taskId={}", configs.size(),
					serviceId, taskId);

			List<FormAttributeRequest> formRequests = prepareFormAttributeRequests(configs);

			logger.info("Prepared {} form attribute request(s). serviceId={}, taskId={}", formRequests.size(),
					serviceId, taskId);

				attributeDataResponseDTOs = getAttributeDataofForm(formRequests);
				List<AttributeDataResponseDTO> systemAttributeRequests = prepareSystemAttributeRequests(configs);
				if(!systemAttributeRequests.isEmpty()) {
					attributeDataResponseDTOs.addAll(systemAttributeRequests);
				}
				return ResponseEntity.ok(attributeDataResponseDTOs);

		} catch (Exception e) {
			logger.error("Error while fetching filter attributes. serviceId={}, taskId={}", serviceId, taskId, e);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<AttributeDataResponseDTO> prepareSystemAttributeRequests(List<InboxSentBoxFilterConfig> configs) {
		List<AttributeDataResponseDTO> sysAttributeData = new ArrayList<AttributeDataResponseDTO>();
		for (InboxSentBoxFilterConfig config : configs) {

			if (config == null) {
				logger.warn("Skipping null filter configuration");
				continue;
			}

			String fullAttrId = config.getAttrId();

			if (fullAttrId != null && fullAttrId.startsWith("Sys_")) {

			    AttributeDataResponseDTO dto = new AttributeDataResponseDTO();

			    dto.setAttrId(fullAttrId);

			    if (ApplicationConstants.SERVICE_COMPLETION_DATE.equals(fullAttrId)) {

			        Map<String, Object> attributes = new HashMap<>();
			        attributes.put("type", "date");

			        Map<String, Object> htmlTag = new HashMap<>();
			        htmlTag.put("tag", "input");
			        htmlTag.put("selfClose", true);
			        htmlTag.put("attributes", attributes);

			        Map<String, Object> label = new HashMap<>();
			        label.put("value", "Service Completion Date");

			        Map<String, Object> displaySettings = new HashMap<>();
			        displaySettings.put("label", label);

			        Map<String, Object> attrData = new HashMap<>();
			        attrData.put("htmlTag", htmlTag);
			        attrData.put("displaySettings", displaySettings);

			        dto.setAttrData(attrData);
			    }

			    sysAttributeData.add(dto);
			}
		}
		return sysAttributeData;
	}

	private List<FormAttributeRequest> prepareFormAttributeRequests(List<InboxSentBoxFilterConfig> configs) {

		Map<String, FormAttributeRequest> formRequestMap = new LinkedHashMap<>();

		for (InboxSentBoxFilterConfig config : configs) {

			if (config == null) {
				logger.warn("Skipping null filter configuration");
				continue;
			}

			String fullAttrId = config.getAttrId();
			String formId = config.getAttrFormId();

			if (fullAttrId == null || fullAttrId.startsWith("Sys_")) {
				logger.debug("Skipping system/null attribute. attrId={}", fullAttrId);
				continue;
			}

			if (formId == null || formId.isBlank()) {
				logger.warn("Skipping attribute because formId is empty. attrId={}", fullAttrId);
				continue;
			}

			String attributeId = extractAttributeId(fullAttrId);

	        if (attributeId == null || attributeId.isBlank()) {
	            logger.warn(
	                    "Skipping attribute because extracted attributeId is empty. attrId={}",
	                    fullAttrId);
	            continue;
	        }
	        
			FormAttributeRequest request = formRequestMap.computeIfAbsent(formId, key -> {

	            FormAttributeRequest newRequest = new FormAttributeRequest();

	            newRequest.setFormId(key);
	            newRequest.setAttributes(new ArrayList<>());
	            newRequest.setAttributeMapping(new LinkedHashMap<>());

	            return newRequest;
	        });

	        // Add attribute only once
	        if (!request.getAttributes().contains(attributeId)) {

	            request.getAttributes().add(attributeId);

	            logger.debug(
	                    "Added attribute. formId={}, attributeId={}",
	                    formId,
	                    attributeId);
	        }

	        // Maintain attribute mapping
	        request.getAttributeMapping().put(attributeId, fullAttrId);
		}

		logger.info("Successfully prepared attribute requests for {} form(s)", formRequestMap.size());

		return new ArrayList<>(formRequestMap.values());

	}

	private String extractAttributeId(String fullAttrId) {

		int lastDollarIndex = fullAttrId.lastIndexOf('$');

		if (lastDollarIndex >= 0 && lastDollarIndex < fullAttrId.length() - 1) {

			return fullAttrId.substring(lastDollarIndex + 1);
		}

		return fullAttrId;

	}

	private List<AttributeDataResponseDTO> getAttributeDataofForm(List<FormAttributeRequest> formRequests) {

		List<AttributeDataResponseDTO> finalResponse = new ArrayList<>();

		if (formRequests == null || formRequests.isEmpty()) {
			logger.info("No form attribute requests found");
			return finalResponse;
		}

		for (FormAttributeRequest request : formRequests) {

			if (request == null || request.getAttributes() == null || request.getAttributes().isEmpty()) {

				logger.warn("Skipping empty form attribute request");
				continue;
			}

			logger.info("Fetching attributes from Form Designer. formId={}, attributeCount={}", request.getFormId(),
					request.getAttributes().size());

			ResponseEntity<?> response = formDesignerFeignClient.getFilterAttributes(request);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				finalResponse.addAll((List<AttributeDataResponseDTO>) response.getBody());
			}

			logger.info("Completed Form Designer request. formId={}", request.getFormId());
		}

		logger.info("Completed fetching attribute data. Total response attributes={}", finalResponse.size());

		return finalResponse;
	}
}