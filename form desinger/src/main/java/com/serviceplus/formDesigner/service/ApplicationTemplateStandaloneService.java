package com.serviceplus.formDesigner.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.serviceplus.formDesigner.dto.CreateUploadSessionsRequest;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsResponse;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.enums.FormStandaloneStatus;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.feignClient.FileMgmtFeignClient;
import com.serviceplus.formDesigner.feignClient.InstanceConfigRegistryFeignClient;
import com.serviceplus.formDesigner.production.repository.ApplicationDataStandaloneProdRepository;
import com.serviceplus.formDesigner.production.repository.ApplicationTemplateStandaloneProdRepository;
import com.serviceplus.formDesigner.repository.ApplicationDataStandaloneRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateStandaloneRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.request.model.IApplicationTemplate;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.ApplicationTemplateCommonUtil;
import com.serviceplus.formDesigner.util.CommonUtil;
import com.serviceplus.formDesigner.util.MessageConstants;
import com.serviceplus.formDesigner.util.SnowflakeIdGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ApplicationTemplateStandaloneService {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationTemplateStandaloneService.class);

	@Value("${max.token.generation.attempts}")
	private Integer MAX_TOKEN_GENERATION_ATTEMPTS;
	
	@Value("${tiny.url.configure}")
	private String shortUrlBaseConfigure;
	
	@Value("${tiny.url.prod}")
    private String shortUrlBaseProd;
	
	private final ApplicationTemplateStandaloneRepository applicationTemplateStandaloneRepository;
	private final ApplicationTemplateStandaloneProdRepository applicationTemplateStandaloneProdRepository;
	private final ApplicationTemplateValidationService applicationTemplateValidationService;
	private final InstanceConfigRegistryFeignClient instanceConfigRegistryFeignClient;
	private final FileMgmtFeignClient fileMgmtFeignClient;
	private final ApplicationTemplateService applicationTemplateService;
	private final ApplicationDataStandaloneRepository applicationDataStandaloneRepository;
	private final ApplicationDataStandaloneProdRepository applicationDataStandaloneProdRepository;

	public ApplicationTemplateStandaloneService(
			ApplicationTemplateStandaloneRepository applicationTemplateStandaloneRepository,
			InstanceConfigRegistryFeignClient instanceConfigRegistryFeignClient, ApplicationTemplateValidationService applicationTemplateValidationService,
			ApplicationDataStandaloneRepository applicationDataStandaloneRepository,ApplicationTemplateStandaloneProdRepository applicationTemplateStandaloneProdRepository,
			ApplicationDataStandaloneProdRepository applicationDataStandaloneProdRepository,
			ApplicationTemplateService applicationTemplateService, FileMgmtFeignClient fileMgmtFeignClient) {
		super();
		this.applicationTemplateStandaloneRepository = applicationTemplateStandaloneRepository;
		this.instanceConfigRegistryFeignClient = instanceConfigRegistryFeignClient;
		this.fileMgmtFeignClient = fileMgmtFeignClient;
		this.applicationTemplateValidationService = applicationTemplateValidationService;
		this.applicationTemplateService = applicationTemplateService;
		this.applicationDataStandaloneRepository = applicationDataStandaloneRepository;
		this.applicationTemplateStandaloneProdRepository = applicationTemplateStandaloneProdRepository;
		this.applicationDataStandaloneProdRepository = applicationDataStandaloneProdRepository;
	}

	public ResponseEntity<?> upsert(ApplicationTemplateStandalone applicationTemplateStandalone,UserSessionDTO userSessionDetails, HttpServletResponse httpServletResponse) {
		try {
			Map<String, Object> response = new HashMap<String, Object>();
			boolean formDataEncryptedAtRest = false;
			boolean formDataEncryptedInTransit = false;
			boolean isValid = applicationTemplateValidationService.checkApplicationTemplateValidation(applicationTemplateStandalone, response, httpServletResponse);

	        if (!isValid) {
	            return ResponseEntity.badRequest().body(response);
	        }
	        String formName = applicationTemplateService.getFormName(applicationTemplateStandalone);
	        
	        try {
	        	formDataEncryptedAtRest = ApplicationTemplateCommonUtil.isFormDataEncryptedAtRest(applicationTemplateStandalone);
	        	if (formDataEncryptedAtRest && (applicationTemplateStandalone.getEncryptionKeyAtRest() == null || applicationTemplateStandalone.getEncryptionKeyAtRest().isBlank())) {
	        		applicationTemplateStandalone.setEncryptionKeyAtRest(CommonUtil.generateKeyAndIV16Char());
	        	}
	        	formDataEncryptedInTransit = ApplicationTemplateCommonUtil.isFormDataEncryptedInTransit(applicationTemplateStandalone);
	        	if (formDataEncryptedInTransit && (applicationTemplateStandalone.getEncryptionKey() == null || applicationTemplateStandalone.getEncryptionKey().isBlank())) {
	        		applicationTemplateStandalone.setEncryptionKey(CommonUtil.generateKeyAndIV16Char());
	        	}
	        }catch (Exception e) {
				e.printStackTrace();
			}
	        
	        if(applicationTemplateStandalone.getFormId() == null) {
	        	if(applicationTemplateStandaloneRepository.findByFormNameAndTenantId(formName, userSessionDetails.getTenantId()).isPresent()) {
	        		return ResponseEntity.badRequest().body(Map.of("message","A form with the name '" + formName + "' already exists."));
	        	}
	        	
	        	applicationTemplateStandalone.setHolderId(SnowflakeIdGenerator.createUniqueId());
	        	applicationTemplateStandalone.setFormName(formName);
	            
	        	applicationTemplateStandalone.setCreatedOn(new Date());
	        	applicationTemplateStandalone.setUpdateOn(new Date());
	        	applicationTemplateStandalone.setInstantiable(ApplicationConstants.DEFAULT_INSTANTIABLE);
	        	applicationTemplateStandalone.setStatus(FormStandaloneStatus.YET_TO_PUBLISH.getValue());
	            
	        	applicationTemplateStandalone.setIsOnline(false);
	        	
	        	String tinyUrlToken = generateUniqueTinyUrlToken();
	        	applicationTemplateStandalone.setTinyUrlToken(tinyUrlToken);
	        	applicationTemplateStandalone.setConfigureTinyUrl(shortUrlBaseConfigure + tinyUrlToken);
	        	
	            ApplicationTemplateCommonUtil.setFormData(applicationTemplateStandalone, userSessionDetails);

	            IApplicationTemplate saveData = applicationTemplateStandaloneRepository.save(applicationTemplateStandalone);

	            applicationTemplateService.indexInSearch(applicationTemplateStandalone, saveData, userSessionDetails.getTenantId(), userSessionDetails.getEntityId().intValue(), userSessionDetails.getLocationId().intValue(), 1);
	            applicationTemplateService.publishRecommendedFormEvent(applicationTemplateStandalone, saveData, 1);
	        	
	            return ResponseEntity.ok(Map.of("formId", saveData.getFormId(), "message", "Form definition saved successfully", "status", saveData.getStatus()));
	            
	        }else {
	        	ApplicationTemplateStandalone applicationTemplateStandaloneDB = applicationTemplateStandaloneRepository.findById(applicationTemplateStandalone.getFormId()).orElseThrow( () -> new SPRuntimeError(MessageConstants.FORM_DATA_NOT_EDIT, HttpStatus.BAD_REQUEST));
	        	String status = applicationTemplateStandaloneDB.getStatus();
	        	if(status.equalsIgnoreCase(FormStandaloneStatus.ARCHIVED.getValue()) || status.equalsIgnoreCase(FormStandaloneStatus.TRASHED.getValue())) {
	        		return ResponseEntity.badRequest().body(Map.of("staus",status,"message","Can't edit form"));
	        	}
	        	
	        	if(applicationTemplateStandaloneRepository.findByFormNameAndTenantId(formName, userSessionDetails.getTenantId()).isPresent()) {
	        		if(!applicationTemplateStandaloneRepository.findByFormNameAndTenantId(formName, userSessionDetails.getTenantId()).get().getFormId().equals(applicationTemplateStandaloneDB.getFormId())){
	        			return ResponseEntity.badRequest().body(Map.of("message", "A form with the name '" + formName + "' already exists."));
	        		}
	        	}
	        	
	        	applicationTemplateStandaloneDB.setFormJsonData(applicationTemplateStandalone.getFormJsonData());
	        	applicationTemplateStandaloneDB.setFormName(formName);
	        	applicationTemplateStandaloneDB.setUpdateOn(new Date());
	        	
	        	applicationTemplateStandaloneDB.setStatus(FormStandaloneStatus.YET_TO_PUBLISH.getValue());
	        	applicationTemplateStandaloneDB.setIsOnline(false);
	        	
	        	applicationTemplateStandaloneDB = applicationTemplateStandaloneRepository.save(applicationTemplateStandaloneDB);
	        	
	        	// prod operation also
	        	return ResponseEntity.ok(Map.of("formId", applicationTemplateStandaloneDB.getFormId(), "message", MessageConstants.FORM_DATA_EDIT, "status", applicationTemplateStandaloneDB.getStatus()));
	        }
	        
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Service : Get standalone form details : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> getFormIdAndFormName() {
		List<Map<String, Object>> resultData = new ArrayList<>();
		try {
			List<ApplicationTemplateStandalone> applicationTemplateStandalones = applicationTemplateStandaloneRepository
					.findByStatusInAndInstantiable(
							List.of(FormStandaloneStatus.YET_TO_PUBLISH.getValue(),
									FormStandaloneStatus.PUBLISHED.getValue()),
							ApplicationConstants.DEFAULT_INSTANTIABLE);
			for (ApplicationTemplateStandalone applTemp : applicationTemplateStandalones) {
				ApplicationTemplateCommonUtil.extractStandaloneFormSummary(applTemp).ifPresent(resultData::add);
			}
			return ResponseEntity.ok(resultData);
		} catch (Exception e) {
			LOGGER.error("Service : Get all standalone form details : {}", e);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> sendDataForRender(String formId, HttpServletRequest request) {
		try {

			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_FORM_ID, HttpStatus.BAD_REQUEST));
			String encryptionKey = "";

			Map<String, Object> finalResponse = new HashMap<String, Object>();
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if(userSessionDetails == null) {
				userSessionDetails = new UserSessionDTO();
				userSessionDetails.setUserID(0L);
			}
			
			String tenantId = userSessionDetails.getTenantId() == null || userSessionDetails.getTenantId().isBlank() ? getTenantId(request) : userSessionDetails.getTenantId();
			userSessionDetails.setTenantId(tenantId);

			formId = applicationTemplateStandalone.getFormId();

			Boolean dataEncryptionIntransit = false;
			CreateUploadSessionsRequest createUploadSessionsRequest = new CreateUploadSessionsRequest();
			createUploadSessionsRequest.setUserId(0);
			createUploadSessionsRequest.setSourceService(ApplicationConstants.SOURCE_SERVICE);
			try {
				dataEncryptionIntransit = ApplicationTemplateCommonUtil
						.isFormDataEncryptedInTransit(applicationTemplateStandalone);
				ApplicationTemplateCommonUtil.setFileAttributesDTO(applicationTemplateStandalone,
						createUploadSessionsRequest);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (dataEncryptionIntransit) {
				encryptionKey = applicationTemplateStandalone.getEncryptionKey();
			}

			if (StringUtils.hasText(encryptionKey)) {
				finalResponse.put("encryptionKey", encryptionKey);
			}
			finalResponse.put("formId", applicationTemplateStandalone.getFormId());

			if (createUploadSessionsRequest.getFiles().size() > 0) {
				try {
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

			Object formJsonData = applicationTemplateStandalone.getFormJsonData();
			finalResponse.put("formJsonData", formJsonData);

			return ResponseEntity.ok(finalResponse);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				LOGGER.error("Service : Get standalone form details : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> formPublish(String formId, Boolean isOnline, UserSessionDTO userSessionDetails) {
		try {
			
			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST));
			if(!applicationTemplateStandalone.getCreatedBy().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Not unauthorized to Publish the form", HttpStatus.UNAUTHORIZED);
			}
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.ARCHIVED.getValue())) {
				throw new SPRuntimeError("ARCHIVED Form can't be Published.", HttpStatus.BAD_REQUEST);
			}
			
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.TRASHED.getValue())) {
				throw new SPRuntimeError("TRASHED Form can't be Published.", HttpStatus.BAD_REQUEST);
			}
			
			applicationTemplateStandalone.setStatus(FormStandaloneStatus.PUBLISHED.getValue());
			applicationTemplateStandalone.setProdTinyUrl(shortUrlBaseProd + applicationTemplateStandalone.getTinyUrlToken());
			applicationTemplateStandalone.setIsOnline(isOnline);
			
			applicationTemplateStandalone = applicationTemplateStandaloneRepository.save(applicationTemplateStandalone);
			
			// move to prod also
			applicationTemplateStandaloneProdRepository.save(applicationTemplateStandalone);
			
			return ResponseEntity.ok(Map.of("message","Standalone Form Published","configureTinyUrl",applicationTemplateStandalone.getConfigureTinyUrl(),"prodTinyUrl",applicationTemplateStandalone.getProdTinyUrl()));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : publish form as standalone: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> online(String formId, Boolean isOnline, UserSessionDTO userSessionDetails) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST));
			if(!applicationTemplateStandalone.getCreatedBy().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Not unauthorized to Publish the form", HttpStatus.UNAUTHORIZED);
			}
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.ARCHIVED.getValue())) {
				throw new SPRuntimeError("Form is in ARCHIVED.", HttpStatus.BAD_REQUEST);
			}
			
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.TRASHED.getValue())) {
				throw new SPRuntimeError("Form is in TRASHED", HttpStatus.BAD_REQUEST);
			}
			
			applicationTemplateStandalone.setIsOnline(isOnline);
			
			applicationTemplateStandalone = applicationTemplateStandaloneRepository.save(applicationTemplateStandalone);
			
			// move to prod also
			applicationTemplateStandaloneProdRepository.save(applicationTemplateStandalone);
			
			String message = isOnline ? "Now Form is online" : "Now Form is offline";
			return ResponseEntity.ok(Map.of("message",message));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : online/offline for production : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> archiveForm(String formId, UserSessionDTO userSessionDetails) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST));
			if(!applicationTemplateStandalone.getCreatedBy().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Not unauthorized to archive the form", HttpStatus.UNAUTHORIZED);
			}
			
			if(applicationTemplateStandalone.getStatus().equals(FormStandaloneStatus.TRASHED.getValue())) {
				throw new SPRuntimeError("TRASHED Form can't be archived.", HttpStatus.BAD_REQUEST);
			}
			
			applicationTemplateStandalone.setIsOnline(false);
//			applicationTemplateStandalone.setConfigureTinyUrl("");
//			applicationTemplateStandalone.setProdTinyUrl("");
			applicationTemplateStandalone.setStatus(FormStandaloneStatus.ARCHIVED.getValue());
			
			applicationTemplateStandaloneRepository.save(applicationTemplateStandalone);

			// move to prod also
			applicationTemplateStandaloneProdRepository.save(applicationTemplateStandalone);
			
			return ResponseEntity.ok(Map.of("message","The form has been deleted successfully and moved to Archieves."));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : archive standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	
	public ResponseEntity<?> trashForm(String formId, UserSessionDTO userSessionDetails) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST));
			if(!applicationTemplateStandalone.getCreatedBy().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Not unauthorized to trash the form", HttpStatus.UNAUTHORIZED);
			}
			
			applicationTemplateStandalone.setStatus(FormStandaloneStatus.TRASHED.getValue());
			applicationTemplateStandalone.setIsOnline(false);
//			applicationTemplateStandalone.setConfigureTinyUrl("");
//			applicationTemplateStandalone.setProdTinyUrl("");
			
			applicationTemplateStandaloneRepository.save(applicationTemplateStandalone);
			
			// move to prod also
			applicationTemplateStandaloneProdRepository.save(applicationTemplateStandalone);
			
			return ResponseEntity.ok(Map.of("message","Standalone Form trashed"));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : archive standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	public ResponseEntity<?> delete(String formId, UserSessionDTO userSessionDetails) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST));
			if(!applicationTemplateStandalone.getCreatedBy().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Not unauthorized to trash the form", HttpStatus.UNAUTHORIZED);
			}
			
			applicationDataStandaloneRepository.deleteByFormId(applicationTemplateStandalone.getFormId());
			applicationTemplateStandaloneRepository.delete(applicationTemplateStandalone);
			
			//prod delete operation
			applicationTemplateStandaloneProdRepository.delete(applicationTemplateStandalone);
			applicationDataStandaloneProdRepository.deleteByFormId(applicationTemplateStandalone.getFormId());
			
			return ResponseEntity.ok(Map.of("message","Standalone Form deleted."));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : delete standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> getAllArchiveForm(UserSessionDTO userSessionDetails) {
		try {
			List<Map<String, Object>> resultData = new ArrayList<>();
			List<ApplicationTemplateStandalone> list = applicationTemplateStandaloneRepository.findByCreatedByAndTenantIdAndStatus(userSessionDetails.getUserID(), userSessionDetails.getTenantId(), FormStandaloneStatus.ARCHIVED.getValue());
			for (ApplicationTemplateStandalone applTemp : list) {
				ApplicationTemplateCommonUtil.extractStandaloneFormSummary(applTemp).ifPresent(resultData::add);
			}
			return ResponseEntity.ok(resultData);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : Get all archive standalone forms: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> getAllTrashForm(UserSessionDTO userSessionDetails) {
		try {
			List<Map<String, Object>> resultData = new ArrayList<>();
			List<ApplicationTemplateStandalone> list = applicationTemplateStandaloneRepository.findByCreatedByAndTenantIdAndStatus(userSessionDetails.getUserID(), userSessionDetails.getTenantId(), FormStandaloneStatus.TRASHED.getValue());
			for (ApplicationTemplateStandalone applTemp : list) {
				ApplicationTemplateCommonUtil.extractStandaloneFormSummary(applTemp).ifPresent(resultData::add);
			}
			return ResponseEntity.ok(resultData);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : Get all archive standalone forms: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> restore(String formId, UserSessionDTO userSessionDetails) {
		try {
			ApplicationTemplateStandalone applicationTemplateStandalone = applicationTemplateStandaloneRepository.findById(formId).orElseThrow( () -> new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST));
			if(!applicationTemplateStandalone.getCreatedBy().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Not unauthorized to trash the form", HttpStatus.UNAUTHORIZED);
			}
			
			applicationTemplateStandalone.setStatus(FormStandaloneStatus.YET_TO_PUBLISH.getValue());
			applicationTemplateStandalone.setIsOnline(false);
			
			applicationTemplateStandaloneRepository.save(applicationTemplateStandalone);
			
//prod restore operation
//			setIsOnline -> false
//			setStatus -> YET_TO_PUBLISH
			
			return ResponseEntity.ok(Map.of("message","Standalone Form restored."));
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				LOGGER.error("Service : restore standalone form: {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	public String getTenantId(HttpServletRequest request) {

		String tenantId = ""; // request.getHeader("x-tenant-id");

		if (tenantId == null || tenantId.trim().isEmpty()) {

			ResponseEntity<?> responseEntity = instanceConfigRegistryFeignClient
					.fetchTenantId(Map.of(ApplicationConstants.CLIENT_DOMAIN_HEADER,
							request.getHeader(ApplicationConstants.CLIENT_DOMAIN_HEADER)));

			tenantId = (String) responseEntity.getBody();
		}

		return tenantId;
	}
	
	private String generateUniqueTinyUrlToken() {
	    for (int attempt = 0; attempt < MAX_TOKEN_GENERATION_ATTEMPTS; attempt++) {
	        String candidate = ApplicationTemplateCommonUtil.randomCode();
	        if (!applicationTemplateStandaloneRepository.existsByTinyUrlToken(candidate)) {
	            return candidate;
	        }
	    }
	    LOGGER.error("Failed to generate a unique tinyUrlToken after {} attempts", MAX_TOKEN_GENERATION_ATTEMPTS);
	    throw new SPRuntimeError("Could not generate short URL, please retry", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
