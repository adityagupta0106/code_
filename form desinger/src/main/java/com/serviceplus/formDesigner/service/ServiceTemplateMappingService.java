package com.serviceplus.formDesigner.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.FormEncryptionKeyDTO;
import com.serviceplus.formDesigner.dto.ServiceTemplateMappingDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.enums.FormStatus;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.repository.FormEncryptionKeyRepository;
import com.serviceplus.formDesigner.repository.UserFormEncryptionKeyRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.FormEncryptionKey;
import com.serviceplus.formDesigner.request.model.UserFormEncryptionKey;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.CommonUtil;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class ServiceTemplateMappingService {

	@Autowired
	private ApplicationTemplateRepository applicationTemplateRepository;
	
	@Autowired private FormEncryptionKeyRepository encryptionKeyRepository;
	@Autowired private FormRedisService formRedisService;
	@Autowired private UserFormEncryptionKeyRepository userFormEncryptionKeyRepository;
	
	public ResponseEntity<?> getAllTemplateInDepartment(HttpServletRequest request) {
		List<Object> resultData = new ArrayList<Object>();
		Map<String, Object> applicationTemplates = null;
		List<ApplicationTemplate> applicationTemplatesList = null;

		try {
			UserSessionDTO user = CommonUtil.getUserSessionDetails(request);
			Integer departmentId = user.getEntityId();
			applicationTemplatesList = applicationTemplateRepository.findByStatusAndDepartmentIdAndArchive(FormStatus.PUBLISHED.getValue(), departmentId, false);
			for (ApplicationTemplate applTemp : applicationTemplatesList) {
				applicationTemplates = new HashMap<String, Object>();
				applicationTemplates.put("formId", applTemp.getFormId());
				applicationTemplates.put("formName", applTemp.getFormName());
				applicationTemplates.put("instantiable", applTemp.getInstantiable());
				resultData.add(applicationTemplates);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultData);
		}
		return ResponseEntity.status(HttpStatus.OK).body(resultData);
	}

	public ResponseEntity<Map<String, Object>> saveInstantiableForm(ServiceTemplateMappingDTO serviceTemplateMapping) {
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<ApplicationTemplate> applicationTemplates = new ArrayList<ApplicationTemplate>();
		try {
			applicationTemplates = applicationTemplateRepository.findByFormIdAndStatus(serviceTemplateMapping.getFormId(), FormStatus.ACTIVE.getValue());
			if (!applicationTemplates.isEmpty()) {
				if(serviceTemplateMapping.getInstantiable().equalsIgnoreCase("N")) {
					ApplicationTemplate newApplicationTemplate = new ApplicationTemplate();
					newApplicationTemplate.setFormName(serviceTemplateMapping.getFormName());
					
					ObjectMapper objectmapper = new ObjectMapper();
					String formJsonDataStr = objectmapper.writeValueAsString(applicationTemplates.get(0).getFormJsonData());
					JSONObject formDataJsonObject = new JSONObject(formJsonDataStr);
					JSONObject globalSetting = formDataJsonObject.optJSONObject("globalSetting");
					if(globalSetting != null) {
						globalSetting.put("formName", serviceTemplateMapping.getFormName());
						Map<String, Object> updatedFormData = objectmapper.readValue(formDataJsonObject.toString(),new TypeReference<Map<String, Object>>() {});
						newApplicationTemplate.setFormJsonData(updatedFormData);
					}else {
						newApplicationTemplate.setFormJsonData(applicationTemplates.get(0).getFormJsonData());
					}
					
					newApplicationTemplate.setStatus(applicationTemplates.get(0).getStatus());
					newApplicationTemplate.setCreatedOn(applicationTemplates.get(0).getCreatedOn());
					newApplicationTemplate.setCreatedBy(serviceTemplateMapping.getCreatedBy());
					newApplicationTemplate.setDepartmentId(serviceTemplateMapping.getDepartmentId());
					newApplicationTemplate.setStateId(serviceTemplateMapping.getStateId());
					newApplicationTemplate.setLocationId(serviceTemplateMapping.getLocationId());
					newApplicationTemplate.setInstantiable(serviceTemplateMapping.getInstantiable());
					
					
					ApplicationTemplate saveNewApplicationTemplate = applicationTemplateRepository.save(newApplicationTemplate);
					
					resultData.put("formId", saveNewApplicationTemplate.getFormId());
					resultData.put("formName", saveNewApplicationTemplate.getFormName());
					resultData.put("status", 200);
					resultData.put("message", MessageConstants.DATA_SAVE);
				}else if(serviceTemplateMapping.getInstantiable().equalsIgnoreCase(ApplicationConstants.DEFAULT_INSTANTIABLE)) {
					ApplicationTemplate applicationTemplate = applicationTemplates.get(0);
					applicationTemplate.setUpdateOn(new Date());
					applicationTemplate.setInstantiable(serviceTemplateMapping.getInstantiable());
					ApplicationTemplate saveNewApplicationTemplate = applicationTemplateRepository.save(applicationTemplate);
					
					resultData.put("formId", saveNewApplicationTemplate.getFormId());
					resultData.put("status", 200);
					resultData.put("message", MessageConstants.DATA_EDIT);
				}
			} else {
				resultData.put("status", 400);
				resultData.put("message", MessageConstants.INVALID_DATA);
				return new ResponseEntity<Map<String,Object>>(resultData, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultData.put("status", 500);
			resultData.put("message", MessageConstants.INTERNAL_ERROR);
			return new ResponseEntity<Map<String,Object>>(resultData, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String,Object>>(resultData, HttpStatus.OK);
	}

	public ResponseEntity<?> isFormExistInDepartment(ServiceTemplateMappingDTO serviceTemplateMapping) {
		Boolean isFormExist =  false;
		try {
			List<ApplicationTemplate> applList = applicationTemplateRepository.findByFormIdAndStatusAndDepartmentId(serviceTemplateMapping.getFormId(), FormStatus.ACTIVE.getValue(),
					 serviceTemplateMapping.getDepartmentId());
			if(!applList.isEmpty()) {
				isFormExist = true;
			}
		}catch (Exception e) {
			e.printStackTrace();
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(isFormExist);
		}
		return ResponseEntity.status(HttpStatus.OK).body(isFormExist);
	}

	public ResponseEntity<Map<String, Object>> saveFormEncryptionKey(FormEncryptionKeyDTO formEncryptionKeyDTO) {
		Map<String, Object> resultData = new HashMap<String, Object>();
		Boolean recordSave = false;
		try {
			FormEncryptionKey fromEncryptionKey = encryptionKeyRepository
					.findByFormIdAndServiceIdAndTaskId(formEncryptionKeyDTO.getFormId(), formEncryptionKeyDTO.getServiceId(), formEncryptionKeyDTO.getTaskId())
					.orElse(null);
			
			if(fromEncryptionKey == null) {
				if(!formEncryptionKeyDTO.getDefaultFormEncryptionRequired()) { 
					fromEncryptionKey = new FormEncryptionKey();
					fromEncryptionKey.setFormId(formEncryptionKeyDTO.getFormId());
					fromEncryptionKey.setTaskId(formEncryptionKeyDTO.getTaskId());
					fromEncryptionKey.setServiceId(formEncryptionKeyDTO.getServiceId());
					fromEncryptionKey.setTransitEncFlag(formEncryptionKeyDTO.getEncryptDataInTransit());
					fromEncryptionKey.setCdate(new Date());
					fromEncryptionKey.setUdate(new Date());
					if(formEncryptionKeyDTO.getEncryptDataAtRest()) {
						String encryptionKeyAtRest = CommonUtil.generateKeyAndIV16Char();
						fromEncryptionKey.setRestEncKey(encryptionKeyAtRest);
					}
					encryptionKeyRepository.save(fromEncryptionKey);
					recordSave = true;
				}
			}else {
				formRedisService.deleteRecord(fromEncryptionKey.getServiceId()+ApplicationConstants.DELIMITER+fromEncryptionKey.getTaskId()+ApplicationConstants.DELIMITER+fromEncryptionKey.getFormId());
				UserFormEncryptionKey userFormEncryptionKey = userFormEncryptionKeyRepository.findByServiceIdAndTaskId(formEncryptionKeyDTO.getServiceId(), formEncryptionKeyDTO.getTaskId()).orElse(null);
				if(userFormEncryptionKey != null) {
					userFormEncryptionKeyRepository.delete(userFormEncryptionKey);
				}
				if(formEncryptionKeyDTO.getDefaultFormEncryptionRequired()) {
					encryptionKeyRepository.delete(fromEncryptionKey);
				}else {
					fromEncryptionKey.setUdate(new Date());
					fromEncryptionKey.setTransitEncFlag(formEncryptionKeyDTO.getEncryptDataInTransit());
					if(formEncryptionKeyDTO.getEncryptDataAtRest()) {
						if(fromEncryptionKey.getRestEncKey() == null || fromEncryptionKey.getRestEncKey().isBlank()) {
							String encryptionKeyAtRest = CommonUtil.generateKeyAndIV16Char();
							fromEncryptionKey.setRestEncKey(encryptionKeyAtRest);
						}
					}
					encryptionKeyRepository.save(fromEncryptionKey);
					recordSave = true;
				}
			}
			resultData.put("status", 200);
			if(recordSave)
				resultData.put("message", MessageConstants.DATA_SAVE);
			else
				resultData.put("message", "No record for update/save.");
			
			return new ResponseEntity<Map<String,Object>>(resultData, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			resultData.put("status", 500);
			resultData.put("message", MessageConstants.INTERNAL_ERROR);
			return new ResponseEntity<Map<String,Object>>(resultData, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
