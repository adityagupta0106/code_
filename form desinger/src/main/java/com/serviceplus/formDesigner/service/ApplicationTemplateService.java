package com.serviceplus.formDesigner.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.FormApiExecution.Services.ServerApiSanitizer;
import com.serviceplus.formDesigner.autosave.request.dto.FilterAttributeDataResponseDTO;
import com.serviceplus.formDesigner.controller.ApplicationTemplateController;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsRequest;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsResponse;
import com.serviceplus.formDesigner.dto.FilterFormAttributeRequest;
import com.serviceplus.formDesigner.dto.FormApplicationResponseDTO;
import com.serviceplus.formDesigner.dto.FormApplicationResponseDTO.PaginationDTO;
import com.serviceplus.formDesigner.dto.FormAttributeRequest;
import com.serviceplus.formDesigner.dto.FormDTO;
import com.serviceplus.formDesigner.dto.FormDTO.Attribute;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO;
import com.serviceplus.formDesigner.dto.LinkAttributeNodeDTO;
import com.serviceplus.formDesigner.dto.RecommendedFormEventDTO;
import com.serviceplus.formDesigner.dto.SpecificAttributesDTO;
import com.serviceplus.formDesigner.dto.SpecificAttributesReqDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.enums.FormStatus;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.feignClient.CaptchaFeignClient;
import com.serviceplus.formDesigner.feignClient.FileMgmtFeignClient;
import com.serviceplus.formDesigner.formrecommendation.service.FormSearchDocument;
import com.serviceplus.formDesigner.formrecommendation.service.FormSearchElasticService;
import com.serviceplus.formDesigner.kafka.RecommendedFormKafkaProducer;
import com.serviceplus.formDesigner.repository.ApplicationDataRepository;
import com.serviceplus.formDesigner.repository.ApplicationDataStandaloneRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateStandaloneRepository;
import com.serviceplus.formDesigner.repository.FormEncryptionKeyRepository;
import com.serviceplus.formDesigner.repository.UserFormEncryptionKeyRepository;
import com.serviceplus.formDesigner.request.model.ApplicationData;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.request.model.ApplicationTransactionLog;
import com.serviceplus.formDesigner.request.model.FormEncryptionKey;
import com.serviceplus.formDesigner.request.model.IApplicationTemplate;
import com.serviceplus.formDesigner.request.model.UserFormEncryptionKey;
import com.serviceplus.formDesigner.returnToEdit.entity.FormDataReference;
import com.serviceplus.formDesigner.returnToEdit.repository.FormDataReferenceRepository;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.ApplicationTemplateCommonUtil;
import com.serviceplus.formDesigner.util.CommonUtil;
import com.serviceplus.formDesigner.util.MessageConstants;
import com.serviceplus.formDesigner.util.SnowflakeIdGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ApplicationTemplateService {

    private final ApplicationTemplateRepository applicationTemplateRepository;
    private final ApplicationTemplateStandaloneRepository applicationTemplateStandaloneRepository;
    private final ApplicationTemplateValidationService applicationTemplateValidationService;
    private final ApplicationDataRepository applicationDataRepository;
    private final ApplicationDataStandaloneRepository applicationDataStandaloneRepository;
    private final FormRedisService formRedisService;
    private final UserFormEncryptionKeyRepository userFormEncryptionKeyRepository;
    private final ApplicationTransactionLogService applicationTransactionLogService;
    private final FormEncryptionKeyRepository formEncryptionKeyRepository;
    private final FormSearchElasticService formSearchElasticService;
    private final RecommendedFormKafkaProducer copyFormDataKafkaProducer;
    private final CaptchaFeignClient captchaFeignClient;
    private final FileMgmtFeignClient fileMgmtFeignClient;
    private final ServerApiSanitizer serverApiSanitizer;
    private final FormDataReferenceRepository formDataReferenceRepository;


    private static final Logger LOGGER = LogManager.getLogger(ApplicationTemplateController.class);
    
    public ApplicationTemplateService(ApplicationTemplateRepository applicationTemplateRepository, ApplicationTemplateValidationService applicationTemplateValidationService, ApplicationDataRepository applicationDataRepository, FormRedisService formRedisService, UserFormEncryptionKeyRepository userFormEncryptionKeyRepository, ApplicationTransactionLogService applicationTransactionLogService, FormEncryptionKeyRepository formEncryptionKeyRepository, FormSearchElasticService formSearchElasticService, RecommendedFormKafkaProducer copyFormDataKafkaProducer, CaptchaFeignClient captchaFeignClient, FileMgmtFeignClient fileMgmtFeignClient, ServerApiSanitizer serverApiSanitizer, ApplicationTemplateStandaloneRepository applicationTemplateStandaloneRepository, ApplicationDataStandaloneRepository applicationDataStandaloneRepository, FormDataReferenceRepository formDataReferenceRepository) {
        this.applicationTemplateRepository = applicationTemplateRepository;
        this.applicationTemplateValidationService = applicationTemplateValidationService;
        this.applicationDataRepository = applicationDataRepository;
        this.formRedisService = formRedisService;
        this.userFormEncryptionKeyRepository = userFormEncryptionKeyRepository;
        this.applicationTransactionLogService = applicationTransactionLogService;
        this.formEncryptionKeyRepository = formEncryptionKeyRepository;
        this.formSearchElasticService = formSearchElasticService;
        this.copyFormDataKafkaProducer = copyFormDataKafkaProducer;
        this.captchaFeignClient = captchaFeignClient;
        this.fileMgmtFeignClient = fileMgmtFeignClient;
        this.serverApiSanitizer = serverApiSanitizer;
        this.applicationTemplateStandaloneRepository = applicationTemplateStandaloneRepository;
        this.applicationDataStandaloneRepository = applicationDataStandaloneRepository;
        this.formDataReferenceRepository = formDataReferenceRepository;
    }

	public ResponseEntity<?> getApplications(String holderId, String formId, Integer page, Integer size) {
		try {
			ApplicationTemplate applicationTemplate = null;
			if(formId == null || formId.equals("")) {
				applicationTemplate = applicationTemplateRepository.findTopByHolderIdAndStatusNotOrderByVersionDesc(holderId, FormStatus.INACTIVE.getValue());
			}else {				
				applicationTemplate = applicationTemplateRepository.findByHolderIdAndFormIdAndStatusNot(holderId, formId, FormStatus.INACTIVE.getValue());
			}
			if (applicationTemplate == null) {
				throw new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
			FormProcessCacheDTO formProcessCacheDTO;
			if (formRedisService.dataExists(applicationTemplate.getFormId())) {
				formProcessCacheDTO = (FormProcessCacheDTO) formRedisService
						.getDataFromRedis(applicationTemplate.getFormId());
			} else {
				formProcessCacheDTO = formRedisService.getAttributeDetails(applicationTemplate.getFormJsonData(),
						applicationTemplate.getFormId(), applicationTemplate.getFormId(), false);
			}

			Page<ApplicationData> applicationPage = null;
			if(formId == null || formId.equals("")) {
				applicationPage = applicationDataRepository.findByHolderIdAndServiceId(applicationTemplate.getHolderId(),null, PageRequest.of(page, size));
			}else {
				applicationPage = applicationDataRepository.findByHolderIdAndFormIdAndServiceId(applicationTemplate.getHolderId(), applicationTemplate.getFormId(),null, PageRequest.of(page, size));
			}

			List<Object> headers = new ArrayList<>();
			List<FormProcessCacheDTO.Attribute> attributes = formProcessCacheDTO.getAttributes();

			for (FormProcessCacheDTO.Attribute attribute : attributes) {
				if ((attribute.getTypeId() == ApplicationConstants.ATTR_SECTION)
						|| (attribute.getTypeId() == ApplicationConstants.ATTR_PAGE_BREAK)
						|| (attribute.getTypeId() == ApplicationConstants.ATTR_DYNAMIC_ROW)) {
					continue;
				}
				Map<String, String> headerElement = new LinkedHashMap<String, String>();
				headerElement.put(attribute.getAttributeId(), attribute.getAttributeLabel());
				headers.add(headerElement);
			}
			
			List<Object> body = new ArrayList<>();
			for (ApplicationData application : applicationPage.getContent()) {
				List<Object> row = new ArrayList<>();
				Map<String, Object> formData = (Map<String, Object>)application.getFormData();
				body.add(formData);
			}

			PaginationDTO pagination = new PaginationDTO();
			pagination.setPage(applicationPage.getNumber());
			pagination.setSize(applicationPage.getSize());
			pagination.setTotalElements(applicationPage.getTotalElements());
			pagination.setTotalPages(applicationPage.getTotalPages());
			pagination.setFirst(applicationPage.isFirst());
			pagination.setLast(applicationPage.isLast());

			FormApplicationResponseDTO response = new FormApplicationResponseDTO();
			response.setHeader(headers);
			response.setBody(body);
			response.setPagination(pagination);

			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOGGER.error("Get all applications of forms : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> getApplicationsForStandaloneForm(String holderId, String formId, Integer page, Integer size) {
		try {
			ApplicationTemplateStandalone applicationTemplate = null;
			if(formId == null || formId.equals("")) {
				applicationTemplate = applicationTemplateStandaloneRepository.findTopByHolderIdAndStatusNot(holderId, FormStatus.INACTIVE.getValue());
			}else {				
				applicationTemplate = applicationTemplateStandaloneRepository.findByHolderIdAndFormIdAndStatusNot(holderId, formId, FormStatus.INACTIVE.getValue());
			}
			if (applicationTemplate == null) {
				throw new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
			FormProcessCacheDTO formProcessCacheDTO;
			if (formRedisService.dataExists(applicationTemplate.getFormId())) {
				formProcessCacheDTO = (FormProcessCacheDTO) formRedisService
						.getDataFromRedis(applicationTemplate.getFormId());
			} else {
				formProcessCacheDTO = formRedisService.getAttributeDetails(applicationTemplate.getFormJsonData(),
						applicationTemplate.getFormId(), applicationTemplate.getFormId(), false);
			}
			
			Page<ApplicationData> applicationPage = null;
			if(formId == null || formId.equals("")) {
				applicationPage = applicationDataStandaloneRepository.findByHolderId(applicationTemplate.getHolderId(), PageRequest.of(page, size));
			}else {
				applicationPage = applicationDataStandaloneRepository.findByHolderIdAndFormId(applicationTemplate.getHolderId(), applicationTemplate.getFormId(), PageRequest.of(page, size));
			}
			
			List<Object> headers = new ArrayList<>();
			List<FormProcessCacheDTO.Attribute> attributes = formProcessCacheDTO.getAttributes();
			
			for (FormProcessCacheDTO.Attribute attribute : attributes) {
				if ((attribute.getTypeId() == ApplicationConstants.ATTR_SECTION)
						|| (attribute.getTypeId() == ApplicationConstants.ATTR_PAGE_BREAK)
						|| (attribute.getTypeId() == ApplicationConstants.ATTR_DYNAMIC_ROW)) {
					continue;
				}
				Map<String, String> headerElement = new LinkedHashMap<String, String>();
				headerElement.put(attribute.getAttributeId(), attribute.getAttributeLabel());
				headers.add(headerElement);
			}
			
			List<Object> body = new ArrayList<>();
			for (ApplicationData application : applicationPage.getContent()) {
				List<Object> row = new ArrayList<>();
				Map<String, Object> formData = (Map<String, Object>)application.getFormData();
				body.add(formData);
			}
			
			PaginationDTO pagination = new PaginationDTO();
			pagination.setPage(applicationPage.getNumber());
			pagination.setSize(applicationPage.getSize());
			pagination.setTotalElements(applicationPage.getTotalElements());
			pagination.setTotalPages(applicationPage.getTotalPages());
			pagination.setFirst(applicationPage.isFirst());
			pagination.setLast(applicationPage.isLast());
			
			FormApplicationResponseDTO response = new FormApplicationResponseDTO();
			response.setHeader(headers);
			response.setBody(body);
			response.setPagination(pagination);
			
			return ResponseEntity.ok(response);
			
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOGGER.error("Get all applications of forms : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

    public ResponseEntity<?> getByIdApplicant(String applId, String formId, Integer serviceId, String taskId, String txnId, HttpServletRequest request){

            ResponseEntity<?> response = getById(applId, formId, serviceId, taskId, txnId, request);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return response;
            }

            if (!(response.getBody() instanceof Map<?, ?> body)) {
                return response;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> finalResponse = (Map<String, Object>) body;

            Object formJsonData = finalResponse.get("formJsonData");

            if (formJsonData != null) {

                Object sanitizedFormData = serverApiSanitizer.sanitize(formJsonData);
                finalResponse.put("formJsonData", sanitizedFormData);
            }

            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(finalResponse);
    }


	public ResponseEntity<?> getById(String applId, String formId, Integer serviceId, String taskId, String txnId, HttpServletRequest request) {
		try {
			List<ApplicationTemplate> applicationTemplates = applicationTemplateRepository
					.findByFormIdAndStatusNot(formId, FormStatus.INACTIVE.getValue());
			if (!applicationTemplates.isEmpty()) {
				return sendDataForRender(applId, applicationTemplates.get(0), serviceId, taskId, txnId, request);
			} 
			throw new SPRuntimeError(MessageConstants.INVALID_FORM_ID.toString(), HttpStatus.BAD_REQUEST);
		} catch (SPRuntimeError e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    public Map<String, Object> addFormData(ApplicationTemplate applicationTemplate, HttpServletResponse httpServletResponse, HttpServletRequest requestHttpServletRequest) {
    Map<String, Object> response = new HashMap<>();
    try {
        boolean isValid = applicationTemplateValidationService.checkApplicationTemplateValidation(applicationTemplate, response, httpServletResponse);

        if (!isValid) {
            return response;
        }
        if (applicationTemplate.getFormId() != null) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            response.put("message", MessageConstants.INVALID_DATA);
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return response;
        }

        boolean formDataEncryptedAtRest = ApplicationTemplateCommonUtil.isFormDataEncryptedAtRest(applicationTemplate);
        if (formDataEncryptedAtRest) {
            applicationTemplate.setEncryptionKeyAtRest(CommonUtil.generateKeyAndIV16Char());
        }
        boolean formDataEncryptedInTransit = ApplicationTemplateCommonUtil.isFormDataEncryptedInTransit(applicationTemplate);
        if (formDataEncryptedInTransit) {
            applicationTemplate.setEncryptionKey(CommonUtil.generateKeyAndIV16Char());
        }

        UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(requestHttpServletRequest);
        

        String formName = getFormName(applicationTemplate);

        int departmentId = (userSessionDetails != null && userSessionDetails.getEntityId() != null) ? userSessionDetails.getEntityId() : 0;
        int locationId = (userSessionDetails != null && userSessionDetails.getLocationId() != null) ? userSessionDetails.getLocationId() : 0;
        String tenantId = userSessionDetails != null ? userSessionDetails.getTenantId() : null;
        
        boolean present = applicationTemplateRepository.findByFormNameAndTenantIdAndArchiveAndStatusNot(formName, tenantId, false, FormStatus.INACTIVE.getValue()).isPresent();
        if (present) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            response.put("message", "A form with the name '" + formName + "' already exists.");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return response;
        }

        applicationTemplate.setHolderId(SnowflakeIdGenerator.createUniqueId());
        applicationTemplate.setFormName(formName);
        
        applicationTemplate.setCreatedOn(new Date());
        applicationTemplate.setUpdateOn(new Date());
        applicationTemplate.setInstantiable(ApplicationConstants.DEFAULT_INSTANTIABLE);
        applicationTemplate.setStatus(FormStatus.ACTIVE.getValue());
        applicationTemplate.setVersion(1);
        applicationTemplate.setSubscriptionCount(0);
        applicationTemplate.setArchive(false);
        
        ApplicationTemplateCommonUtil.setFormData(applicationTemplate, userSessionDetails);

        IApplicationTemplate saveData = saveEntity(applicationTemplate, applicationTemplate, applicationTemplateRepository);

        if (saveData == null) {
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", MessageConstants.DATA_NOT_SAVE);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }

        response.put("formId", saveData.getFormId());
        response.put("message", "Form definition saved successfully");
        response.put("status", httpServletResponse.getStatus());

        //persistEncryptionKey(saveData, formDataEncryptedInTransit);
        indexInSearch(applicationTemplate, saveData, tenantId, departmentId, locationId, applicationTemplate.getVersion());
        publishRecommendedFormEvent(applicationTemplate, saveData, applicationTemplate.getVersion());

    } catch (Exception e) {
    	LOGGER.error("addFormData failed", e);
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", MessageConstants.INTERNAL_ERROR);
    }
    return response;
}

    public String getFormName(IApplicationTemplate applicationTemplate) {

        String formName = "";
        ObjectMapper objectMapper = new ObjectMapper();
        String stringFormTemplateObject;
        try {
            stringFormTemplateObject = objectMapper.writeValueAsString(applicationTemplate.getFormJsonData());
            JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);
            JSONObject globalSetting = jsonFormTemplateObject.getJSONObject("globalSetting");
            if (globalSetting != null) {
                formName = globalSetting.optString("formName", "").trim();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formName;
    }
    public String getformDescription(IApplicationTemplate applicationTemplate) {

        String formName = "";
        ObjectMapper objectMapper = new ObjectMapper();
        String stringFormTemplateObject;
        try {
            stringFormTemplateObject = objectMapper.writeValueAsString(applicationTemplate.getFormJsonData());
            JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);
            JSONObject globalSetting = jsonFormTemplateObject.getJSONObject("globalSetting");
            if(globalSetting != null ) {
                formName = globalSetting.optString("formDescription");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formName;
    }

	public List<Map<String, Object>> getFormIdAndFormName(HttpServletResponse httpServletResponse) {
		List<Map<String, Object>> resultData = new ArrayList<>();

		try {
			List<ApplicationTemplate> applicationTemplatesList = applicationTemplateRepository
					.findByStatusNotAndInstantiableAndArchive(FormStatus.INACTIVE.getValue(),
							ApplicationConstants.DEFAULT_INSTANTIABLE, false);
			for (ApplicationTemplate applTemp : applicationTemplatesList) {
				ApplicationTemplateCommonUtil.extractFormSummary(applTemp, false).ifPresent(resultData::add);
			}
			httpServletResponse.setStatus(HttpStatus.OK.value());

		} catch (Exception e) {
			LOGGER.error("getFormIdAndFormName failed", e);
			httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return resultData;
	}

	public Map<String, Object> deleteById(String formId,
            HttpServletRequest request,
			HttpServletResponse httpServletResponse) {
		Map<String, Object> responseData = new HashMap<>();
		try {
			ApplicationTemplate activeTemplate = findActiveByFormId(formId);

			if (activeTemplate == null) {
				httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				responseData.put("status", HttpStatus.BAD_REQUEST.value());
				responseData.put("message", MessageConstants.RECORD_NOT_FOUND);
				return responseData;
			}
			if (activeTemplate.getSubscriptionCount() != null && activeTemplate.getSubscriptionCount() > 0) {
				httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				responseData.put("status", HttpStatus.BAD_REQUEST.value());
				responseData.put("message", "Form is already mapped to a service, It cannot be deleted");
				return responseData;
			}

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			long userId = (userSessionDetails != null && userSessionDetails.getUserID() != null)
					? userSessionDetails.getUserID()
					: 0L;

			if (!Long.valueOf(userId).equals(activeTemplate.getCreatedBy())) {
				httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
				responseData.put("status", HttpStatus.UNAUTHORIZED.value());
				responseData.put("message", MessageConstants.INVALID_USER);
				return responseData;
			}

			activeTemplate.setStatus(FormStatus.INACTIVE.getValue());
			activeTemplate.setUpdateOn(new Date());

			formRedisService.deleteRecord(activeTemplate.getFormId());

			applicationTemplateRepository.save(activeTemplate);
			httpServletResponse.setStatus(HttpStatus.OK.value());
			responseData.put("status", HttpStatus.OK.value());
			responseData.put("message", MessageConstants.RECORD_DELETE);

		} catch (Exception e) {
			LOGGER.error("deleteById failed for formId={}", formId, e);
			httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseData.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseData.put("message", MessageConstants.INTERNAL_ERROR);
		}
		return responseData;
	}

		public Map<String, Object> editFormData(ApplicationTemplate applicationTemplate, HttpServletRequest request, HttpServletResponse servletResponse) {
			Map<String, Object> responseData = new HashMap<>();
			try {
				if (applicationTemplate.getFormId() == null || applicationTemplate.getFormId().isEmpty()) {
					return badRequest(servletResponse, responseData, MessageConstants.GIVE_FORM_ID);
				}
	
				List<ApplicationTemplate> list = applicationTemplateRepository.findByFormIdAndStatusNotAndArchive(applicationTemplate.getFormId(), FormStatus.INACTIVE.getValue(), false);
	
				if (list.isEmpty()) {
					return badRequest(servletResponse, responseData, MessageConstants.FORM_DATA_NOT_EDIT);
				}
				
				ApplicationTemplate existing = list.getFirst();
				
				if (!FormStatus.ACTIVE.getValue().equals(existing.getStatus())) {
					return badRequest(servletResponse, responseData, "Publish Form can't be edit.");
				}
	
				UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
				long userId = (userSessionDetails != null && userSessionDetails.getUserID() != null) ? userSessionDetails.getUserID() : 0L;
	
				if (!Long.valueOf(userId).equals(existing.getCreatedBy())) {
					servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
					responseData.put("status", HttpStatus.UNAUTHORIZED.value());
					responseData.put("message", MessageConstants.INVALID_USER);
					return responseData;
				}
	
				String tenantId = userSessionDetails != null ? userSessionDetails.getTenantId() : null;
				String formName = getFormName(applicationTemplate);
	
				if (isFormNameTakenByOther(formName, tenantId, applicationTemplate.getFormId())) {
					servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
					responseData.put("message", "A form with the name '" + formName + "' already exists.");
					responseData.put("status", HttpStatus.BAD_REQUEST.value());
		            return responseData;
				}
	
				boolean isValid = applicationTemplateValidationService.checkApplicationTemplateValidation(applicationTemplate, responseData, servletResponse);
				if (!isValid) {
					return responseData;
				}
	
				Boolean existingFormIsGlobal = ApplicationTemplateCommonUtil.isGlobalForm(existing);
				Boolean updateFormIsGlobal = ApplicationTemplateCommonUtil.isGlobalForm(applicationTemplate);
				
				if(existing.getSubscriptionCount() != null && existing.getSubscriptionCount() > 0 && !existingFormIsGlobal.equals(updateFormIsGlobal)) {
					servletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
					responseData.put("message", "This form is already subscribed to a service. You can not change the global form property.");
					responseData.put("status", HttpStatus.BAD_REQUEST.value());
		            return responseData;
				}
				
				boolean formDataEncryptedAtRest = ApplicationTemplateCommonUtil.isFormDataEncryptedAtRest(applicationTemplate);
				String encryptionKeyAtRest = formDataEncryptedAtRest
						? (isBlank(existing.getEncryptionKeyAtRest()) ? CommonUtil.generateKeyAndIV16Char()
								: existing.getEncryptionKeyAtRest())
						: null;
	
				boolean formDataEncryptedInTransit = ApplicationTemplateCommonUtil
						.isFormDataEncryptedInTransit(applicationTemplate);
				String encryptionKey = formDataEncryptedInTransit
						? (isBlank(existing.getEncryptionKey()) ? CommonUtil.generateKeyAndIV16Char()
								: existing.getEncryptionKey())
						: null;
	
				existing.setFormName(formName);
				existing.setEncryptionKey(encryptionKey);
				existing.setEncryptionKeyAtRest(encryptionKeyAtRest);
				existing.setFormJsonData(applicationTemplate.getFormJsonData());
				existing.setUpdateOn(new Date());
	
//				applicationDataRepository.deleteByFormIdAndServiceId(existing.getFormId(), null);
	
				
				
				IApplicationTemplate saved = applicationTemplateRepository.save(existing);
	
				indexInSearch(applicationTemplate, saved, saved.getTenantId(),
						saved.getDepartmentId() != null ? saved.getDepartmentId() : 0,
						saved.getLocationId() != null ? saved.getLocationId() : 0, existing.getVersion());
				publishRecommendedFormEvent((IApplicationTemplate)applicationTemplate, saved, existing.getVersion());
	
				formEncryptionKeyRepository.findByFormIdAndServiceIdAndTaskId(saved.getFormId(), 0, null).ifPresent(key -> {
					key.setUdate(new Date());
					key.setTransitEncFlag(formDataEncryptedInTransit);
					key.setRestEncKey(saved.getEncryptionKeyAtRest());
					formEncryptionKeyRepository.save(key);
				});
	
				responseData.put("formId", saved.getFormId());
				responseData.put("status", HttpStatus.OK.value());
				responseData.put("message", MessageConstants.FORM_DATA_EDIT);
	
			} catch (SPRuntimeError e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("editFormData failed", e);
				servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				responseData.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
				responseData.put("message", MessageConstants.INTERNAL_ERROR);
			}
			return responseData;
		}

    public Map<String,Object> getAllDeletedForm(HttpServletRequest request,HttpServletResponse httpServletResponse) {
        Map<String,Object> responseData = new HashMap<String, Object>();
        List<Object> data = new LinkedList<Object>();
        try {
            UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
            Long userId = (long) (userSessionDetails != null && userSessionDetails.getUserID() != null ? userSessionDetails.getUserID() : 0);
            List<ApplicationTemplate> applicationTemplatesList = applicationTemplateRepository.findByCreatedByAndStatus(userId, FormStatus.INACTIVE.getValue());
            int recordNo = 0;
            for(ApplicationTemplate applTemp : applicationTemplatesList) {
                ++recordNo;
                Map<String,Object> applicationTemplate = new HashMap<String, Object>();
                applicationTemplate.put("SNo", recordNo);
                applicationTemplate.put("formId", applTemp.getFormId());
                applicationTemplate.put("formName", getFormName(applTemp));
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                applicationTemplate.put("deletedOn", formatter.format(applTemp.getUpdateOn()));
                data.add(applicationTemplate);
            }
            Map<String,Object> headers = new LinkedHashMap<String, Object>();
            headers.put("sNo", "Serial Number");
            headers.put("formId", "Form Id");
            headers.put("formName", "Form Name");
            headers.put("deletedOn", "Deleted On");

            responseData.put("headers", headers);
            responseData.put("data", data);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public Map<String, Object> restoreDeletedForm(String formId, HttpServletResponse response,HttpServletRequest request) {
        Map<String, Object> responseData = new HashMap<String, Object>();
        List<ApplicationTemplate> applicationTemplates;
        try {
            try {
                applicationTemplates = applicationTemplateRepository.findByFormIdAndStatus(formId,
                        FormStatus.INACTIVE.getValue());
                UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
                Long userId = (userSessionDetails != null && userSessionDetails.getUserID() != null ? userSessionDetails.getUserID().longValue() : 0l);

                if (!applicationTemplates.isEmpty()) {
                    if(userId.equals(applicationTemplates.get(0).getCreatedBy())) {
                        applicationTemplates.get(0).setStatus(FormStatus.ACTIVE.getValue());
                        applicationTemplates.get(0).setUpdateOn(new Date());
                        applicationTemplateRepository.save(applicationTemplates.get(0));
                        responseData.put("message", MessageConstants.RESTORE_RECORD);
                        responseData.put("status", HttpStatus.OK.value());
                    }else {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        responseData.put("status", HttpStatus.UNAUTHORIZED.value());
                        responseData.put("message", MessageConstants.INVALID_USER);
                    }
                } else {
                    responseData.put("message", MessageConstants.RECORD_NOT_FOUND);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                responseData.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                responseData.put("message", MessageConstants.INTERNAL_ERROR);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public ResponseEntity<?> getAllAttributesByFormId(String formId) {
        List<ApplicationTemplate> applicationTemplates = new ArrayList<ApplicationTemplate>();
        FormDTO formDTO = null;
        FormProcessCacheDTO formProcessCacheDTO;
        try {
            applicationTemplates = applicationTemplateRepository.findByFormIdAndStatus(formId,
                    FormStatus.PUBLISHED.getValue());
            if (!applicationTemplates.isEmpty()) {
                List<Attribute> attributes = new ArrayList<>();
                if(formRedisService.dataExists(applicationTemplates.get(0).getFormId())) {
                    formProcessCacheDTO = (FormProcessCacheDTO) formRedisService.getDataFromRedis(applicationTemplates.get(0).getFormId());
                }else {
                    formProcessCacheDTO = formRedisService.getAttributeDetails(
                            applicationTemplates.get(0).getFormJsonData(), applicationTemplates.get(0).getFormId(),applicationTemplates.get(0).getFormId(),false);
                }
                if (formProcessCacheDTO.getAttributes().size() > 0) {
                    List<FormProcessCacheDTO.Attribute> list = formProcessCacheDTO.getAttributes();
                    for (FormProcessCacheDTO.Attribute fAttribute : list) {
                        //	if(fAttribute.getParentId().equals("null")) {
                        attributes.add(new Attribute(fAttribute.getAttributeId(),fAttribute.getParentId(), fAttribute.getAttributeLabel(), fAttribute.getTypeId(), fAttribute.getRequired()));
                        //	}
                    }
                }
                formDTO = new FormDTO(attributes);
                formDTO.setFormName(applicationTemplates.get(0).getFormName());
                return ResponseEntity.ok(formDTO);
            } else
                return ResponseEntity.badRequest().body(MessageConstants.RECORD_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAllFormsAttributesByUserId(UserSessionDTO userSessionDetails) {
        List<FormDTO> formDTOs = new ArrayList<FormDTO>();
        try {
            List<ApplicationTemplate> applicationTemplatesList = applicationTemplateRepository
                    .findByStatusAndDepartmentIdAndArchive(FormStatus.PUBLISHED.getValue(),
                            userSessionDetails.getEntityId(), false);
            for (ApplicationTemplate applTemp : applicationTemplatesList) {
                FormDTO formDTO = null;
                FormProcessCacheDTO formProcessCacheDTO;

                List<Attribute> attributes = new ArrayList<>();
                if (formRedisService.dataExists(applTemp.getFormId())) {
                    formProcessCacheDTO = (FormProcessCacheDTO) formRedisService.getDataFromRedis(applTemp.getFormId());
                } else {
                    formProcessCacheDTO = formRedisService.getAttributeDetails(applTemp.getFormJsonData(),
                            applTemp.getFormId(), applTemp.getFormId(), false);
                }
                if (formProcessCacheDTO.getAttributes().size() > 0) {
                    List<FormProcessCacheDTO.Attribute> list = formProcessCacheDTO.getAttributes();
                    for (FormProcessCacheDTO.Attribute fAttribute : list) {
                        // if(fAttribute.getParentId().equals("null")) {
                        attributes.add(new Attribute(fAttribute.getAttributeId(), fAttribute.getParentId(),
                                fAttribute.getAttributeLabel(), fAttribute.getTypeId(), fAttribute.getRequired()));
                        // }
                    }
                }
                formDTO = new FormDTO(attributes);
                formDTO.setFormName(applTemp.getFormName());
                formDTOs.add(formDTO);
            }
            return ResponseEntity.ok(formDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAttributeForLink(String formId, UserSessionDTO userSessionDetails) {
        try {
            List<ApplicationTemplate> applicationTemplates = applicationTemplateRepository.findByFormIdAndStatusAndArchive(formId, FormStatus.PUBLISHED.getValue(), false);
            if(!applicationTemplates.isEmpty()) {
                ApplicationTemplate applicationTemplate = applicationTemplates.getFirst();

                List<LinkAttributeNodeDTO> allAttributeForLink = ApplicationTemplateCommonUtil.extractAttributesForLink(applicationTemplate.getFormJsonData());
                return ResponseEntity.ok(allAttributeForLink);
            }else
                return ResponseEntity.badRequest().body(MessageConstants.INVALID_DATA);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAllAttributesOfForms(FormAttributeRequest formAttributeRequest) {
        List<String> formIds = formAttributeRequest.getFormIds();
        List<FormDTO> resObj = new ArrayList<FormDTO>();
        try {
            for (String formId : formIds) {
                FormProcessCacheDTO formProcessCacheDTO;
                FormDTO formDTO = null;
                List<ApplicationTemplate> applicationTemplates = applicationTemplateRepository
                        .findByFormIdAndStatus(formId, FormStatus.PUBLISHED.getValue());
                if (!applicationTemplates.isEmpty()) {
                    List<Attribute> attributes = new ArrayList<>();
                    if (formRedisService.dataExists(applicationTemplates.get(0).getFormId())) {
                        formProcessCacheDTO = (FormProcessCacheDTO) formRedisService
                                .getDataFromRedis(applicationTemplates.get(0).getFormId());
                    } else {
                        formProcessCacheDTO = formRedisService.getAttributeDetails(
                                applicationTemplates.get(0).getFormJsonData(), applicationTemplates.get(0).getFormId(),
                                applicationTemplates.get(0).getFormId(), false);
                    }
                    if (formProcessCacheDTO.getAttributes().size() > 0) {
                        List<FormProcessCacheDTO.Attribute> list = formProcessCacheDTO.getAttributes();
                        for (FormProcessCacheDTO.Attribute fAttribute : list) {
                            // if(fAttribute.getParentId().equals("null")) {
                            attributes.add(new Attribute(fAttribute.getAttributeId(), fAttribute.getParentId(),
                                    fAttribute.getAttributeLabel(), fAttribute.getTypeId(), fAttribute.getRequired()));
                            // }
                        }
                    }
                    formDTO = new FormDTO(attributes);
                    formDTO.setFormName(applicationTemplates.get(0).getFormName());
                    resObj.add(formDTO);
                }
            }
            return ResponseEntity.ok(resObj);
        } catch (Exception e) {
            if (e instanceof SPRuntimeError) {
                throw e;
            } else {
                throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

	public ResponseEntity<?> getSpecificAttributes(SpecificAttributesReqDTO specificAttributesReqDTO) {

		try {

			Set<String> formIdsSet = specificAttributesReqDTO.getFormIds();
			String attrType = specificAttributesReqDTO.getAttrType();

			Set<Integer> attrTypeIds = Arrays.stream(attrType.split(",")).map(String::trim)
					.filter(type -> !type.isEmpty()).map(Integer::valueOf)
					.collect(Collectors.toCollection(LinkedHashSet::new));

			Map<String, SpecificAttributesDTO> responseDto = specificAttributes(formIdsSet, attrTypeIds);

			return ResponseEntity.ok(responseDto);

		} catch (NumberFormatException ex) {

			throw new SPRuntimeError("Invalid attribute type ID", HttpStatus.BAD_REQUEST);

		} catch (Exception ex) {

			ex.printStackTrace();

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Map<String, SpecificAttributesDTO> specificAttributes(Set<String> formIdsSet, Set<Integer> attrTypeIds)
			throws Exception {

		Map<String, SpecificAttributesDTO> responseDto = new LinkedHashMap<>();

		ObjectMapper mapper = new ObjectMapper();

		for (String formId : formIdsSet) {

			SpecificAttributesDTO attributesDTO = new SpecificAttributesDTO();

			ApplicationTemplate applicationTemplate = applicationTemplateRepository.findById(formId).orElse(null);

			if (applicationTemplate != null && applicationTemplate.getFormJsonData() != null) {

				JSONObject formJsonObject = new JSONObject(
						mapper.writeValueAsString(applicationTemplate.getFormJsonData()));

				JSONArray formElements = formJsonObject.optJSONArray("formElements");

				if (formElements == null) {
					attributesDTO.setAttributes(new ArrayList<>());
					responseDto.put(formId, attributesDTO);
					continue;
				}

				List<SpecificAttributesDTO.Attribute> specificAttributesNode = new ArrayList<>();

				for (int i = 0; i < formElements.length(); i++) {

					JSONObject element = formElements.optJSONObject(i);

					if (element == null) {
						continue;
					}

					for (Integer attrTypeId : attrTypeIds) {

						specificAttributesNode
								.addAll(ApplicationTemplateCommonUtil.buildSpecificAttributesNode(element, attrTypeId));
					}
				}

				attributesDTO.setAttributes(specificAttributesNode);

			} else {

				attributesDTO.setAttributes(new ArrayList<>());
			}

			responseDto.put(formId, attributesDTO);
		}

		return responseDto;
	}

    public Map<String, SpecificAttributesDTO> specificAttributes(Set<String> formIdsSet, Integer attrType) throws Exception {

        Map<String, SpecificAttributesDTO> responseDto = new LinkedHashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String formId : formIdsSet) {
            SpecificAttributesDTO attributesDTO = new SpecificAttributesDTO();
            ApplicationTemplate applicationTemplate = applicationTemplateRepository.findById(formId).orElse(null);
            if (applicationTemplate != null && applicationTemplate.getFormJsonData() != null) {

                JSONObject formJsonObject = new JSONObject( mapper.writeValueAsString(applicationTemplate.getFormJsonData()));
                JSONArray formElements = formJsonObject.optJSONArray("formElements");
                if (formElements == null) {
                    continue;
                }

                List<SpecificAttributesDTO.Attribute> specificAttributesNode = new ArrayList<>();
                for (int i = 0; i < formElements.length(); i++) {
                    JSONObject element = formElements.optJSONObject(i);
                    if (element == null) {
                        continue;
                    }
                    specificAttributesNode.addAll(ApplicationTemplateCommonUtil.buildSpecificAttributesNode(element, attrType));
                }
                attributesDTO.setAttributes(specificAttributesNode);
            } else {
                attributesDTO.setAttributes(new ArrayList<>());
            }

            responseDto.put(formId, attributesDTO);
        }

        return responseDto;

    }


	private <T extends IApplicationTemplate> T saveEntity(IApplicationTemplate source, T target,
			MongoRepository<T, String> repo) {
		if (source != target) {
			BeanUtils.copyProperties(source, target);
		}
		return repo.save(target);
	}

	private void persistEncryptionKey(IApplicationTemplate saveData, boolean formDataEncryptedInTransit) {
		FormEncryptionKey formEncryptionKey = new FormEncryptionKey();
		formEncryptionKey.setFormId(saveData.getFormId());
		formEncryptionKey.setServiceId(0);
		formEncryptionKey.setTransitEncFlag(formDataEncryptedInTransit);
		formEncryptionKey.setRestEncKey(saveData.getEncryptionKeyAtRest());
		formEncryptionKey.setUdate(new Date());
		formEncryptionKeyRepository.save(formEncryptionKey);
	}

	public void indexInSearch(IApplicationTemplate applicationTemplate, IApplicationTemplate saveData, String tenantId,
			int departmentId, int locationId, int version) {
		try {
			String formDesc = getformDescription(applicationTemplate);
			FormSearchDocument doc = new FormSearchDocument();
			
			doc.setHolderId(saveData.getHolderId());
			doc.setFormId(saveData.getFormId());
			doc.setFormName(saveData.getFormName());
			doc.setFormDescription(formDesc);
			doc.setTenantId(tenantId);
			doc.setDepartmentId(String.valueOf(departmentId));
			doc.setLocationId(String.valueOf(locationId));
			formSearchElasticService.upsert(doc);
		} catch (Exception e) {
			LOGGER.error("Elastic indexing failed on publish for holderId={}, formId={}", saveData.getHolderId(), saveData.getFormId(), e);
		}
	}

	public void publishRecommendedFormEvent(IApplicationTemplate applicationTemplate, IApplicationTemplate saveData, Integer version) {
		try {
			List<LinkAttributeNodeDTO> allAttributeForLink = ApplicationTemplateCommonUtil
					.extractAttributesForLink(applicationTemplate.getFormJsonData());
			List<String> labels = allAttributeForLink.stream().map(LinkAttributeNodeDTO::getLabel)
					.filter(Objects::nonNull).collect(Collectors.toList());

			RecommendedFormEventDTO event = new RecommendedFormEventDTO();
			event.setFormId(saveData.getFormId());
			event.setFormName(saveData.getFormName() + "_V" + version);
			event.setFormDesc(getformDescription(applicationTemplate));
			event.setLabels(labels);

			copyFormDataKafkaProducer.publishRecommendedForm(event);
		} catch (Exception e) {
			LOGGER.warn("Kafka publish failed for formId={}", saveData.getFormId(), e);
		}
	}

	public boolean isFormNameTakenByOther(String formName, String tenantId, String currentFormId) {
		Optional<? extends IApplicationTemplate> match = applicationTemplateRepository.findByFormNameAndTenantIdAndArchiveAndStatusNot(formName, tenantId,
				false, FormStatus.INACTIVE.getValue());
		return match.isPresent() && !match.get().getFormId().equals(currentFormId);
	}

	private Map<String, Object> badRequest(HttpServletResponse resp, Map<String, Object> responseData, String message) {
		resp.setStatus(HttpStatus.BAD_REQUEST.value());
		responseData.put("status", HttpStatus.BAD_REQUEST.value());
		responseData.put("message", message);
		return responseData;
	}

	private boolean isBlank(String s) {
		return s == null || s.isEmpty();
	}

	private ApplicationTemplate findActiveByFormId(String formId) {
		List<ApplicationTemplate> matches = applicationTemplateRepository.findByFormIdAndStatus(formId, FormStatus.ACTIVE.getValue());
		return matches.isEmpty() ? null : matches.get(0);
	}
	
	public ResponseEntity<?> sendDataForRender(String applId, ApplicationTemplate applicationTemplate, Integer serviceId, String taskId, String txnId, HttpServletRequest request) throws Exception {
		Map<String, Object> finalResponse = new HashMap<String, Object>();
		Boolean flag = false;
		ObjectMapper mapper = new ObjectMapper();
		String encryptionKey = "";
		Long userId = 0L;
		Boolean dataEncryptionIntransit = false;
		String formId = applicationTemplate.getFormId();
		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
		if (userSessionDetails != null) {
			userId = userSessionDetails.getUserID().longValue();
		}
		if (txnId != null) {
			ApplicationTransactionLog appTransLog = applicationTransactionLogService.findByTxnId(txnId);
			if (appTransLog == null) {
				appTransLog = new ApplicationTransactionLog();
				appTransLog.setTxnId(txnId);
				appTransLog.setFormId(formId);
				applicationTransactionLogService.save(appTransLog);
				flag = true;
			}
			FormEncryptionKey formEncryptionKey = formEncryptionKeyRepository.findByFormIdAndServiceIdAndTaskId(formId, serviceId, taskId).orElse(null);
			if (formEncryptionKey != null) {
				if (formEncryptionKey.getTransitEncFlag()) {
					dataEncryptionIntransit = true;
					UserFormEncryptionKey userFormEncryptionKey = userFormEncryptionKeyRepository
							.findByUserIdAndServiceIdAndTaskId(userId, serviceId, taskId).orElse(null);
					if (userFormEncryptionKey == null) {
						userFormEncryptionKey = new UserFormEncryptionKey();
						userFormEncryptionKey.setUserId(userId);
						userFormEncryptionKey.setServiceId(serviceId);
						userFormEncryptionKey.setTaskId(formEncryptionKey.getTaskId());
					}
					encryptionKey = CommonUtil.generateKeyAndIV16Char();
					userFormEncryptionKey.setFormId(formId);
					userFormEncryptionKey.setTransitEncKey(encryptionKey);
					userFormEncryptionKey.setLastUpdatedAt(new Date());

					userFormEncryptionKeyRepository.save(userFormEncryptionKey);
				}
			}
		} else {
			flag = true;
		}

		if (!dataEncryptionIntransit) {
			dataEncryptionIntransit = ApplicationTemplateCommonUtil.isFormDataEncryptedInTransit(applicationTemplate);
			if (dataEncryptionIntransit) {
				encryptionKey = applicationTemplate.getEncryptionKey();
			}
		}

		if (flag) {
			if (StringUtils.hasText(encryptionKey)) {
				finalResponse.put("encryptionKey", encryptionKey);
			}
			finalResponse.put("formId", applicationTemplate.getFormId());

			CreateUploadSessionsRequest createUploadSessionsRequest = new CreateUploadSessionsRequest();
			createUploadSessionsRequest.setUserId(userId.intValue());
			createUploadSessionsRequest.setSourceService(ApplicationConstants.SOURCE_SERVICE);

			ApplicationTemplateCommonUtil.setFileAttributesDTO(applicationTemplate, createUploadSessionsRequest);
			if (createUploadSessionsRequest.getFiles().size() > 0) {
				try {
					userSessionDetails = CommonUtil.getUserSessionDetails(request);
					String userSessionDtl = CommonUtil.entityToString(userSessionDetails);
					ResponseEntity<CreateUploadSessionsResponse> apiResponse = fileMgmtFeignClient
							.generateCreateUploadSessionsResponse(userSessionDtl, createUploadSessionsRequest);
					if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
						finalResponse.put("uploads", apiResponse.getBody().getUploads());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Object formJsonData = applicationTemplate.getFormJsonData();
			finalResponse.put("formJsonData", formJsonData);
			finalResponse.put("version", applicationTemplate.getVersion());
			Boolean isGlobalDisabled = applicationTemplate.getSubscriptionCount() != null
					&& applicationTemplate.getSubscriptionCount() > 0 ? true : false;
			finalResponse.put("isGlobalDisabled", isGlobalDisabled);

			JSONObject jsonFormTemplateObject = new JSONObject(mapper.writeValueAsString(formJsonData));
			JSONObject globalSetting = jsonFormTemplateObject.getJSONObject("globalSetting");
			if (globalSetting != null) {
				boolean captchaRequired = globalSetting.has("isCaptcha") ? globalSetting.getBoolean("isCaptcha")
						: false;
				userSessionDetails = CommonUtil.getUserSessionDetails(request);
				String userSessionDtl = CommonUtil.entityToString(userSessionDetails);
				if (captchaRequired) {
					ResponseEntity<?> captchaResponse = captchaFeignClient.generateCaptcha(userSessionDtl,
							"internalCall", "");
					if (captchaResponse.getStatusCode().equals(HttpStatus.OK)) {
						Map<String, String> response = (Map<String, String>) captchaResponse.getBody();
						String captchaCode = response.getOrDefault("captchaCode", "");
						finalResponse.put("captchaCode", captchaCode);
					}
				} else {
					ResponseEntity<?> captchaResponse = captchaFeignClient.generateCaptcha(userSessionDtl,
							"internalCall", "remove");
					if (captchaResponse.getStatusCode().equals(HttpStatus.OK)) {
					}
				}
			}

			Boolean isGlobalForm = ApplicationTemplateCommonUtil.isGlobalForm(applicationTemplate);

			if (Boolean.TRUE.equals(isGlobalForm)) {

				if (serviceId != null && applId != null && !applId.isBlank()) {
					Optional<FormDataReference> reference = formDataReferenceRepository.findLatestReference(serviceId, applId.trim());
					if (reference.isPresent()) {
						String dataId = reference.get().getDataId();
						Optional<ApplicationData> preApplData = applicationDataRepository.findById(dataId);
						preApplData.ifPresent(data -> finalResponse.put("preApplData", data.getFormData()));
					}
				}
			}

		} else {
			throw new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(finalResponse);
	}

	public ResponseEntity<?> getFilterAttributes(FilterFormAttributeRequest formAttributeRequest) {

		try {

			String formId = formAttributeRequest.getFormId();

			LOGGER.info("Fetching filter attributes. formId={}", formId);

			ApplicationTemplate applicationTemplate = applicationTemplateRepository.findById(formId).orElseThrow(() -> new SPRuntimeError("Form Not found.", HttpStatus.BAD_REQUEST));

			List<FilterAttributeDataResponseDTO> attributesDTOs = new ArrayList<>();

			if (applicationTemplate.getFormJsonData() == null) {

				LOGGER.warn("Form JSON data is null. formId={}", formId);
				return ResponseEntity.ok(attributesDTOs);
			}

			ObjectMapper mapper = new ObjectMapper();

			JSONObject formJsonObject = new JSONObject(mapper.writeValueAsString(applicationTemplate.getFormJsonData()));

			JSONArray formElements = formJsonObject.optJSONArray("formElements");

			if (formElements == null) {

				LOGGER.warn("formElements not found in form JSON. formId={}", formId);
				return ResponseEntity.ok(attributesDTOs);
			}

			List<String> requestedAttributes = formAttributeRequest.getAttributes();

			if (requestedAttributes == null || requestedAttributes.isEmpty()) {

				LOGGER.warn("No attributes requested. formId={}", formId);
				return ResponseEntity.ok(attributesDTOs);
			}

			Map<String, String> attributeMapping = formAttributeRequest.getAttributeMapping();

			LOGGER.info("Searching {} attribute(s). formId={}", requestedAttributes.size(), formId);

			for (String attributeId : requestedAttributes) {

				if (attributeId == null || attributeId.isBlank()) {
					continue;
				}

				Optional<JSONObject> attribute = ApplicationTemplateCommonUtil.findAttributeByUniqueId(applicationTemplate.getFormJsonData(),attributeId);

				if (attribute.isPresent()) {

					JSONObject formElement = attribute.get();

					Map<String, Object> formElementMap = mapper.readValue(formElement.toString(), Map.class);

					String responseAttrId = (attributeMapping != null && attributeMapping.get(attributeId) != null)
							? attributeMapping.get(attributeId)
							: attributeId;

					attributesDTOs.add(new FilterAttributeDataResponseDTO(responseAttrId, formElementMap));

					LOGGER.debug("Attribute found. formId={}, attributeId={}, responseAttrId={}", formId, attributeId,
							responseAttrId);
				}
			}

			LOGGER.info("Successfully fetched {} attribute(s). formId={}", attributesDTOs.size(), formId);

			return ResponseEntity.ok(attributesDTOs);

		} catch (Exception e) {

			LOGGER.error("Error while fetching filter attributes. formId={}",
					formAttributeRequest != null ? formAttributeRequest.getFormId() : null, e);

			throw new SPRuntimeError("Error while fetching filter attributes.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
