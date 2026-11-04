package com.serviceplus.formDesigner.service;

import static com.serviceplus.formDesigner.util.CommonUtil.entityToString;
import static com.serviceplus.formDesigner.util.CommonUtil.getUserSessionDetails;
import static java.util.Objects.isNull;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.FormApiExecution.Services.FormApiSubmissionValidator;
import com.serviceplus.formDesigner.FormApiExecution.Services.ServerApiSanitizer;
import com.serviceplus.formDesigner.autosave.model.ApplicationAutoSave;
import com.serviceplus.formDesigner.autosave.repository.IApplicationAutoSaveRepository;
import com.serviceplus.formDesigner.autosave.request.dto.ApplicationAutoSaveDTO;
import com.serviceplus.formDesigner.dto.ApplicationAttributeValueRequest;
import com.serviceplus.formDesigner.dto.CommitRequest;
import com.serviceplus.formDesigner.dto.CommitResponse;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsRequest;
import com.serviceplus.formDesigner.dto.CreateUploadSessionsResponse;
import com.serviceplus.formDesigner.dto.FileViewResponse;
import com.serviceplus.formDesigner.dto.FormDataEventDTO;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO;
import com.serviceplus.formDesigner.dto.FormProcessCacheDTO.LabelValue;
import com.serviceplus.formDesigner.dto.InValidEncKeyResponseDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.dto.WorkflowAttributeRequest;
import com.serviceplus.formDesigner.enums.FormStatus;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.feignClient.FileMgmtFeignClient;
import com.serviceplus.formDesigner.feignClient.ServiceMataDataFeignClient;
import com.serviceplus.formDesigner.kafka.FormDataKafkaProducer;
import com.serviceplus.formDesigner.repository.ApplicationDataRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.repository.FormEncryptionKeyRepository;
import com.serviceplus.formDesigner.repository.UserFormEncryptionKeyRepository;
import com.serviceplus.formDesigner.request.model.ApplicationData;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.ApplicationTransactionLog;
import com.serviceplus.formDesigner.request.model.FormEncryptionKey;
import com.serviceplus.formDesigner.request.model.UserFormEncryptionKey;
import com.serviceplus.formDesigner.returnToEdit.entity.FormDataReference;
import com.serviceplus.formDesigner.returnToEdit.entity.FormDataReference.FormDataReferenceId;
import com.serviceplus.formDesigner.returnToEdit.repository.FormDataReferenceRepository;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.ApplicationDataCommonUtil;
import com.serviceplus.formDesigner.util.ApplicationTemplateCommonUtil;
import com.serviceplus.formDesigner.util.CommonUtil;
import com.serviceplus.formDesigner.util.GeneralValidation;
import com.serviceplus.formDesigner.util.MessageConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ApplicationDataService {

    private final ApplicationDataRepository applicationDataRepository;

    private final ApplicationTemplateRepository applicationTemplateRepository;

    private final FormRedisService formRedisService;

    private final ApplicationTransactionLogService applTranLogService;

    private final UserFormEncryptionKeyRepository userFormEncryptionKeyRepository;
    private final FormEncryptionKeyRepository formEncryptionKeyRepository;
    private final ApplicationTransactionLogService applicationTransactionLogService;


    private final ApplicationDataCommonUtil applicationDataCommonUtil;


    private final FileMgmtFeignClient fileMgmtFeignClient;

	private final FormDataKafkaProducer kafkaProducer;

	private final ServiceMataDataFeignClient serviceFeignClient;

	private final IApplicationAutoSaveRepository autoSaveRepository;


    private final FormApiSubmissionValidator formApiSubmissionValidator;
    
    private final FormDataReferenceRepository formDataReferenceRepository;


    private final ServerApiSanitizer serverApiSanitizer;

    private final ServiceMetadataService serviceMetadataService;

    public ApplicationDataService(ApplicationDataRepository applicationDataRepository, ApplicationTemplateRepository applicationTemplateRepository, FormRedisService formRedisService, ApplicationTransactionLogService applTranLogService, UserFormEncryptionKeyRepository userFormEncryptionKeyRepository, FormEncryptionKeyRepository formEncryptionKeyRepository, ApplicationTransactionLogService applicationTransactionLogService, ApplicationDataCommonUtil applicationDataCommonUtil, FileMgmtFeignClient fileMgmtFeignClient, FormDataKafkaProducer kafkaProducer, ServiceMataDataFeignClient serviceFeignClient, IApplicationAutoSaveRepository autoSaveRepository, FormApiSubmissionValidator formApiSubmissionValidator, ServerApiSanitizer serverApiSanitizer, ServiceMetadataService serviceMetadataService, FormDataReferenceRepository formDataReferenceRepository) {
        this.applicationDataRepository = applicationDataRepository;
        this.applicationTemplateRepository = applicationTemplateRepository;
        this.formRedisService = formRedisService;
        this.applTranLogService = applTranLogService;
        this.userFormEncryptionKeyRepository = userFormEncryptionKeyRepository;
        this.formEncryptionKeyRepository = formEncryptionKeyRepository;
        this.applicationTransactionLogService = applicationTransactionLogService;
        this.applicationDataCommonUtil = applicationDataCommonUtil;
        this.fileMgmtFeignClient = fileMgmtFeignClient;
        this.kafkaProducer = kafkaProducer;
        this.serviceFeignClient = serviceFeignClient;
        this.autoSaveRepository = autoSaveRepository;
        this.formApiSubmissionValidator = formApiSubmissionValidator;
        this.serverApiSanitizer = serverApiSanitizer;
        this.serviceMetadataService = serviceMetadataService;
        this.formDataReferenceRepository = formDataReferenceRepository;
    }

    @org.springframework.beans.factory.annotation.Value("${tracking.public.strong-attributes:}")
    private String trackingStrongAttributes;
    @org.springframework.beans.factory.annotation.Value("${TRACKING_INTERNAL_TOKEN:}")
    private String trackingInternalToken;
    @org.springframework.beans.factory.annotation.Value("${tracking.public.max-scan-records:10000}")
    private int trackingMaxScanRecords;

    private static final Logger LOGGER = LogManager.getLogger("applicationDataLogger");

    public List<ApplicationData> getAllApplication(String formId,HttpServletResponse response) {
        List<ApplicationData> applicationData = applicationDataRepository.findByFormId(formId);
        try {
            applicationData = applicationDataRepository.findByFormId(formId);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return applicationData;
    }

    public List<Object> applicationTrackStatus(HttpServletRequest request,HttpServletResponse response) {
        List<Object> responseData = new ArrayList<Object>();

        try {
            UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
            Long userId = (long) (userSessionDetails != null && userSessionDetails.getUserID() != null
                    ? userSessionDetails.getUserID()
                    : 0);

            List<ApplicationData> applicationDataList = applicationDataRepository.findByUserIdAndServiceId(userId,null);
            for (ApplicationData applicationData : applicationDataList) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("formId", applicationData.getFormId());
                data.put("formName", applicationData.getFormName());
                data.put("dataId", applicationData.getDataId());

                responseData.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public Map<String,Object> getById(ApplicationData applicationData, Integer serviceId, HttpServletResponse response, HttpServletRequest request) {

        Map<String,Object> responseData = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> status = new HashMap<String, Object>();
        status.put("code", 200);
        status.put("message", "");
        String redisKey = "";
        Boolean isEncryptedInTransit = false;
        Boolean isEncryptedAtRest = false;
        Boolean globalEncFlag = false;
        String encryptionKeyAtRest = null;
        String encryptionKeyinTransit = null;
        Long userId = 0L;


        try {
            UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
            if (userSessionDetails != null) {
                userId  = userSessionDetails.getUserID().longValue();
            }

            List<ApplicationTemplate> formDataList = applicationTemplateRepository.findByFormIdAndStatus(applicationData.getFormId(),FormStatus.PUBLISHED.getValue());
            ApplicationData applicationDataDB = applicationDataRepository.findByDataIdAndFormId(applicationData.getDataId(), applicationData.getFormId());

            if(!formDataList.isEmpty() && !isNull(applicationDataDB)) {
                if(applicationData.getTxnId() != null) {
                    ApplicationTransactionLog appTransLog = applicationTransactionLogService.findByTxnId(applicationData.getTxnId());
                    if(appTransLog == null) {
                        appTransLog = new ApplicationTransactionLog();
                        appTransLog.setTxnId(applicationData.getTxnId());
                        appTransLog.setFormId(applicationData.getFormId());
                        applicationTransactionLogService.save(appTransLog);
                    }
                }
                if (serviceId != null && serviceId > 0 ) {
                    FormEncryptionKey formEncryptionKey = formEncryptionKeyRepository.findByFormIdAndServiceIdAndTaskId(applicationDataDB.getFormId(), applicationDataDB.getServiceId(), applicationDataDB.getTaskId()).orElse(null);
                    if(formEncryptionKey != null) {
                        isEncryptedInTransit = true;
                        isEncryptedAtRest = true;

                        if(formEncryptionKey.getTransitEncFlag()) {
                            encryptionKeyinTransit = CommonUtil.generateKeyAndIV16Char();
                        }

                        encryptionKeyAtRest = formEncryptionKey.getRestEncKey();
                        redisKey = applicationDataDB.getServiceId()+ApplicationConstants.DELIMITER+applicationDataDB.getTaskId()+ApplicationConstants.DELIMITER+applicationDataDB.getFormId();
                        globalEncFlag = true;
                    }
                }

                if(!isEncryptedInTransit) {
                    Boolean formDataEncryptedInTransit = ApplicationTemplateCommonUtil.isFormDataEncryptedInTransit(formDataList.get(0));
                    encryptionKeyinTransit = formDataEncryptedInTransit ? formDataList.get(0).getEncryptionKey() : null;
                }
                if(!isEncryptedAtRest) {
                    Boolean formDataEncryptedAtRest = ApplicationTemplateCommonUtil.isFormDataEncryptedAtRest(formDataList.get(0));
                    redisKey = applicationData.getFormId();
                    if(formDataEncryptedAtRest) {
                        encryptionKeyAtRest = formDataList.get(0).getEncryptionKeyAtRest();
                    }
                }

                if(StringUtils.hasText(encryptionKeyAtRest)){
                    applicationDataCommonUtil.decryptApplicationAttributeData(formDataList.get(0), applicationDataDB, encryptionKeyAtRest,redisKey, globalEncFlag);
                }

                if(StringUtils.hasText(encryptionKeyinTransit)){
                    if(encryptionKeyinTransit!=null) {
                        String[] encryptionKeyArray = encryptionKeyinTransit.split("\\.");
                        try {
                            String encryptApplicationtData = CommonUtil.encryptClientData(applicationDataDB.getFormData(), encryptionKeyArray[0], encryptionKeyArray[1]);
                            applicationDataDB.setFormData(encryptApplicationtData);
                            responseData.put("encryptionKey",encryptionKeyinTransit);
                        }catch (Exception e) {
                            e.printStackTrace();
                            response.setStatus(HttpStatus.BAD_REQUEST.value());
                            status.put("code", 404);
                            return responseData;
                        }
                    }
                }

                Object formJsonData = formDataList.getFirst().getFormJsonData();
                Object sanitizedFormData = serverApiSanitizer.sanitize(formJsonData);

                Map<String,Object> formData = new HashMap<String, Object>();
                formData.put("formId", formDataList.getFirst().getFormId());
                formData.put("formJsonData", sanitizedFormData);
                CreateUploadSessionsRequest createUploadSessionsRequest = new CreateUploadSessionsRequest();
                createUploadSessionsRequest.setUserId(userId.intValue());
                createUploadSessionsRequest.setSourceService(ApplicationConstants.SOURCE_SERVICE);

                ApplicationTemplateCommonUtil.setFileAttributesDTO(formDataList.get(0), createUploadSessionsRequest);
                if(!createUploadSessionsRequest.getFiles().isEmpty()) {
                    try {
                        userSessionDetails = CommonUtil.getUserSessionDetails(request);
                        String userSessionDtl = CommonUtil.entityToString(userSessionDetails);
                        ResponseEntity<CreateUploadSessionsResponse> apiResponse = fileMgmtFeignClient
                                .generateCreateUploadSessionsResponse(userSessionDtl, createUploadSessionsRequest);
                        if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
                            formData.put("uploads", apiResponse.getBody().getUploads());
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                responseData.put("formData", formData);

                if (!isNull(createUploadSessionsRequest.getFiles())) {
                    ApplicationData appData = new ApplicationData();
                    appData.setFormData(applicationDataDB.getFormData());
                    List<String> uploadIds = getStrings(appData, createUploadSessionsRequest);

                    LinkedHashMap<String, Object> appliedFormData = mapper.convertValue(applicationDataDB.getFormData(), LinkedHashMap.class);
                    ResponseEntity<FileViewResponse> fileViewResp = fileMgmtFeignClient.filePreview(entityToString(getUserSessionDetails(request)), new CommitRequest(uploadIds));

                    FileViewResponse body = fileViewResp.getBody();

                    if(isNull(body) || isNull(body.getResults())){
                        throw new SPRuntimeError("Failed to fetch document"
                                , HttpStatus.FAILED_DEPENDENCY);
                    }

                    body.getResults().forEach((key, value) -> {
                        for (Map.Entry<String, Object> entry : appliedFormData.entrySet()) {
                            if(entry.getValue() instanceof Map) {
                                LinkedHashMap<String, Object> valueMap = mapper.convertValue(entry.getValue(), LinkedHashMap.class);
                                if (valueMap.get("uploadId") != null && valueMap.get("uploadId").equals(key)) {
                                    entry.setValue(value);
                                }
                            }
                        }
                    });

                    applicationDataDB.setFormData(appliedFormData);
                }

                responseData.put("applicationData", applicationDataDB.getFormData());
                responseData.put("dataId", applicationDataDB.getDataId());
            }else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                status.put("code", 404);
                status.put("message", MessageConstants.RECORD_NOT_FOUND);
            }

        }catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            status.put("code", 500);
            status.put("message", e.getMessage());
        }
        responseData.put("status", status);
        return responseData;
    }

    public ResponseEntity<?> addApplicationData(ApplicationData applicationData, Boolean draft ,String txnId,Integer serviceId, String taskId,String dataId,String applId,  HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> finalResponse = new HashMap<String, Object>();
        List<ApplicationTemplate> applicationTemplatesList = new ArrayList<ApplicationTemplate>();
        ApplicationTransactionLog applTranLog = null;
        ApplicationData saveData;
        String redisKey = "";
        Boolean isEncryptedInTransit = false;
        Boolean isEncryptedAtRest = false;
        Boolean globalEncFlag = false;
        String encryptionKeyAtRest = null;
        String encryptionKeyinTransit = null;
        try {
            if (StringUtils.hasText(applicationData.getFormId())) {
                UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
                
                Long userId = userSessionDetails != null && userSessionDetails.getUserID() != null
                        ? userSessionDetails.getUserID()
                        : 0;

                applicationTemplatesList = applicationTemplateRepository.findByFormIdAndStatus(applicationData.getFormId(), FormStatus.PUBLISHED.getValue());
                if (applicationTemplatesList != null && !applicationTemplatesList.isEmpty()) {
                    Boolean flag = false;
                    if (StringUtils.hasText(txnId)) {
                        applTranLog = applTranLogService.findByTxnIdAndFormId(txnId, applicationData.getFormId());
                        if (applTranLog != null) {
                            flag = true;
                            
                            applicationData.setServiceId(serviceId);
                            applicationData.setApplId(applId);
                            applicationData.setTaskId(taskId);
                            

                            UserFormEncryptionKey userFormEncryptionKey = userFormEncryptionKeyRepository.findByUserIdAndServiceIdAndTaskId(userId, serviceId, taskId).orElse(null);
                            if(userFormEncryptionKey != null) {
                                if(StringUtils.hasText(userFormEncryptionKey.getTransitEncKey())) {
                                    isEncryptedInTransit = true;
                                    encryptionKeyinTransit = userFormEncryptionKey.getTransitEncKey();
                                    redisKey = applicationData.getServiceId()+ApplicationConstants.DELIMITER+applicationData.getTaskId()+ApplicationConstants.DELIMITER+applicationData.getFormId();

                                }
                            }

                            FormEncryptionKey formEncryptionKey = formEncryptionKeyRepository.findByFormIdAndServiceIdAndTaskId(applicationData.getFormId(), applicationData.getServiceId(), applicationData.getTaskId()).orElse(null);
                            if(formEncryptionKey != null) {
                                isEncryptedAtRest = true;
                                encryptionKeyAtRest = formEncryptionKey.getRestEncKey();
                                globalEncFlag = true;
                            }
                        }
                    } else {
                        flag = true;
                    }


                    if (flag) {
                        if(!isEncryptedInTransit) {
                            isEncryptedInTransit = ApplicationTemplateCommonUtil.isFormDataEncryptedInTransit(applicationTemplatesList.get(0));
                            encryptionKeyinTransit = isEncryptedInTransit ? applicationTemplatesList.get(0).getEncryptionKey() : null;
                        }

                        if(!isEncryptedAtRest) {
                            Boolean formDataEncryptedAtRest = ApplicationTemplateCommonUtil.isFormDataEncryptedAtRest(applicationTemplatesList.get(0));
                            redisKey = applicationData.getFormId();
                            globalEncFlag = false;
                            if (formDataEncryptedAtRest) {
                                encryptionKeyAtRest = applicationTemplatesList.get(0).getEncryptionKeyAtRest();
                            }
                        }

                        if (encryptionKeyinTransit != null) {
                            String[] encryptionKeyArray = encryptionKeyinTransit.split("\\.");
                            try {
                                Object decryptData = CommonUtil.decryptClientData(
                                        applicationData.getFormData().toString(), encryptionKeyArray[0],
                                        encryptionKeyArray[1]);
                                applicationData.setFormData(decryptData);
                            } catch (Exception e) {
                                e.printStackTrace();
                                InValidEncKeyResponseDTO data = new InValidEncKeyResponseDTO();
                                data.setEncryptionKey(encryptionKeyinTransit);
                                data.setFormData(applicationData.getFormData());
                                finalResponse.put("data", data);
                                finalResponse.put("message", MessageConstants.DATA_NOT_SAVE + ","+ MessageConstants.INVALID_ENCRYPTION_KEY);
                                finalResponse.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
                                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(finalResponse);
                            }
                        }

                        formApiSubmissionValidator.validate(
                                applicationTemplatesList.getFirst().getFormJsonData(),
                                (Map<String, Object>) applicationData.getFormData(),
                                applicationData.getValidationTokens());

                        Map<String, Object> validationMessage = new HashMap<String, Object>();
                        validationMessage = checkValidationOfApplicationData(draft, applicationTemplatesList.get(0).getFormJsonData(), applicationTemplatesList.get(0).getFormId(), applicationData, redisKey, globalEncFlag);
                        if (validationMessage.isEmpty()) {
                            if (StringUtils.hasText(encryptionKeyAtRest)) {
                                applicationDataCommonUtil.encryptApplicationAttributeData(encryptionKeyAtRest, redisKey, globalEncFlag, applicationTemplatesList.get(0), applicationData);
                            }

                            CreateUploadSessionsRequest createUploadSessionsRequest = new CreateUploadSessionsRequest();
                            ApplicationTemplateCommonUtil.setFileAttributesDTO(applicationTemplatesList.getFirst(),createUploadSessionsRequest);

                            if (!isNull(createUploadSessionsRequest.getFiles())) {
                                List<String> uploadIds = getStrings(applicationData, createUploadSessionsRequest);

                                ResponseEntity<CommitResponse> commitResponse = fileMgmtFeignClient.commitFiles(entityToString(userSessionDetails), new CommitRequest(uploadIds));
                                CommitResponse body = commitResponse.getBody();

                                if ((isNull(body) || isNull(body.getResults()))) {
                                    throw new SPRuntimeError("Failed to save document with ID"
                                            , HttpStatus.INTERNAL_SERVER_ERROR);
                                }

                                body.getResults().forEach(req -> {
                                    if (req.getMessage().equals("FAILED")) {
                                        throw new SPRuntimeError("Failed to save document with ID " + req.getUploadId() + " (" + req.getMessage() + ")"
                                                , HttpStatus.FAILED_DEPENDENCY);
                                    }
                                });
                            }

                            if (dataId==null ||dataId.isEmpty()) {
                                applicationData.setCreatedOn(new Date());
                                applicationData.setModifyOn(new Date());
                                applicationData.setUserId(userId);
                                applicationData.setTxnId(txnId);
                                applicationData.setTenantId(userSessionDetails.getTenantId());
                                applicationData.setFormId(applicationTemplatesList.get(0).getFormId());
                                applicationData.setHolderId(applicationTemplatesList.get(0).getHolderId());
                                saveData = applicationDataRepository.save(applicationData);
                                finalResponse.put("dataId", saveData.getDataId());
                                finalResponse.put("message", MessageConstants.DATA_SAVE);

                            } else {
                                ApplicationData applicationDataDB = applicationDataRepository
                                        .findByDataId(dataId);

                                if (isNull(applicationDataDB)) {
                                    finalResponse.put("status", HttpStatus.BAD_REQUEST.value());
                                    finalResponse.put("message", MessageConstants.INVALID_DATA);
                                    return ResponseEntity.badRequest().body(finalResponse);
                                }
                                if (!userId.equals(applicationDataDB.getUserId())) {
                                    finalResponse.put("status", HttpStatus.UNAUTHORIZED.value());
                                    finalResponse.put("message", MessageConstants.INVALID_USER);
                                    return new ResponseEntity<>(finalResponse, HttpStatus.UNAUTHORIZED);
                                }
                                applicationDataDB.setFormData(applicationData.getFormData());
                                applicationDataDB.setModifyOn(new Date());
                                saveData = applicationDataRepository.save(applicationDataDB);
                                finalResponse.put("message", MessageConstants.APPL_DATA_EDIT);
                            }
                            
                            // delete auto save form data
                            ApplicationAutoSave applicationAutoSave = autoSaveRepository.findByUserIDAndServiceIdAndTaskIdAndFormId(userId, saveData.getServiceId(), saveData.getTaskId(), saveData.getFormId()).orElse(null);
                			if(!isNull(applicationAutoSave)) {
                				autoSaveRepository.delete(applicationAutoSave);
                			}
                			
                            finalResponse.put("dataId", saveData.getDataId());
                            finalResponse.put("status", response.getStatus());

                            Map<String, Object> workflowData = applicationDataCommonUtil.getAllWorkflowAttrVal(applicationTemplatesList.getFirst(), saveData);

                            if (!workflowData.isEmpty()) {
                                finalResponse.putAll(workflowData);
                            }

                            if (applTranLog != null) {
                                applTranLog.setDataId(saveData.getDataId());
                                applTranLogService.save(applTranLog);
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationMessage);
                        }
                    } else {
                        finalResponse.put("message", MessageConstants.DATA_NOT_SAVE);
                        finalResponse.put("status", HttpStatus.BAD_REQUEST.value());
                        return ResponseEntity.badRequest().body(finalResponse);
                    }
                } else {
                    finalResponse.put("message", MessageConstants.DATA_NOT_SAVE);
                    finalResponse.put("status", HttpStatus.OK.value());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(finalResponse);
                }
            } else {
                finalResponse.put("message", MessageConstants.INVALID_DATA);
                finalResponse.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(finalResponse);
            }

            if(saveData.getServiceId()!=null) {
            	Boolean isGlobalForm = false;
            	if(!draft) {
            		isGlobalForm = ApplicationTemplateCommonUtil.isGlobalForm(applicationTemplatesList.get(0));
            	}
            	produceFormData(saveData, isGlobalForm);
            }
            return ResponseEntity.status(HttpStatus.OK).body(finalResponse);
            
        } catch (Exception e) {

            if(e instanceof SPRuntimeError){
                throw e;
            }

            e.printStackTrace();
            finalResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            finalResponse.put("message", MessageConstants.INTERNAL_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalResponse);
        }
    }
    
    
	private void produceFormData(ApplicationData formData, Boolean isGlobalForm) {

		if (formData == null || formData.getApplId() == null || formData.getApplId().isBlank()) {
			return;
		}

		String[] applicationIds = formData.getApplId().split(",");

		for (String applicationId : applicationIds) {

			String applId = applicationId.trim();

			if (applId.isBlank()) {
				continue;
			}

			ApplicationData applicationData = new ApplicationData();

			applicationData.setDataId(formData.getDataId());
			applicationData.setServiceId(formData.getServiceId());
			applicationData.setApplId(applId);
			applicationData.setTaskId(formData.getTaskId());
			applicationData.setFormId(formData.getFormId());
			applicationData.setHolderId(formData.getHolderId());
			applicationData.setTxnId(formData.getTxnId());
			applicationData.setFormName(formData.getFormName());
			applicationData.setUserId(formData.getUserId());
			applicationData.setTenantId(formData.getTenantId());
			applicationData.setFormData(formData.getFormData());

			Set<String> uniqueTaskIds = new LinkedHashSet<>();

			if (Boolean.TRUE.equals(isGlobalForm)) {

				List<FormDataReference> references = formDataReferenceRepository.findByServiceIdAndApplicationId(formData.getServiceId(), applId);

				for (FormDataReference reference : references) {
					if (reference.getTaskId() != null && !reference.getTaskId().isBlank()) {
						uniqueTaskIds.add(reference.getTaskId());
					}
				}

				if (formData.getTaskId() != null && !formData.getTaskId().isBlank()) {
					uniqueTaskIds.add(formData.getTaskId());
				}

				FormDataReferenceId referenceId = new FormDataReferenceId(applId, formData.getDataId());

				if (!formDataReferenceRepository.existsById(referenceId)) {

					FormDataReference reference = new FormDataReference();

					reference.setDataId(formData.getDataId());
					reference.setServiceId(formData.getServiceId());
					reference.setTaskId(formData.getTaskId());
					reference.setApplicationId(applId);
					reference.setHolderId(formData.getHolderId());
					reference.setFormId(formData.getFormId());
					reference.setUserId(formData.getUserId());
					reference.setTenantId(formData.getTenantId());

					reference.setBaseServiceId(formData.getServiceId()/10000);

					formDataReferenceRepository.save(reference);
				}

			} else {
				if (formData.getTaskId() != null && !formData.getTaskId().isBlank()) {
					uniqueTaskIds.add(formData.getTaskId());
				}
			}

			List<String> taskIds = new ArrayList<>(uniqueTaskIds);

			FormDataEventDTO formDataDTO = new FormDataEventDTO(applicationData, taskIds);

			publishFormDate(formDataDTO);
			produceFormDataForMvel(formDataDTO);
			produceFormDataForDocument(formDataDTO);
			produceFormDataForNotification(formDataDTO);
			produceFormDataForInboxSentboxFilters(formDataDTO);
			produceFormDataForCorrelationSet(formDataDTO);
		}
	}


    private void publishFormDate(FormDataEventDTO formDataDTO) {
        ServiceMetadataDTO metadata = serviceMetadataService.fetchServiceMetadata(formDataDTO.getServiceId());

        if (metadata == null) {
            return;
        }

        formDataDTO.setUsedAttributeList(metadata.getUsedAttributeList());
        kafkaProducer.sendFormData(formDataDTO);
    }

	private void produceFormDataForDocument(FormDataEventDTO formDataDTO) {
		ResponseEntity<Set<String>> apiResponse = serviceFeignClient.getUsedAttributes(formDataDTO.getServiceId(), "document");
		if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
			Set<String> usedAttrSet=apiResponse.getBody();
			if(!usedAttrSet.isEmpty()) {
				formDataDTO.setKeys(usedAttrSet);
				kafkaProducer.sendDocument(formDataDTO);
			}
		}
	}

	private void produceFormDataForMvel(FormDataEventDTO formDataDTO) {
		ResponseEntity<Set<String>> apiResponse = serviceFeignClient.getUsedAttributes(formDataDTO.getServiceId(), "mvel");
		if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
			Set<String> usedAttrSet=apiResponse.getBody();
			if(!usedAttrSet.isEmpty()) {
				formDataDTO.setKeys(usedAttrSet);
				kafkaProducer.sendMvel(formDataDTO);
			}
		}
	}
	private void produceFormDataForNotification(FormDataEventDTO formDataDTO) {
		ResponseEntity<Set<String>> apiResponse = serviceFeignClient.getUsedAttributes(formDataDTO.getServiceId(), "notification");
		if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
			Set<String> usedAttrSet=apiResponse.getBody();
			if(!usedAttrSet.isEmpty()) {
				formDataDTO.setKeys(usedAttrSet);
				kafkaProducer.sendNotification(formDataDTO);
			}
		}
	}
	
	private void produceFormDataForInboxSentboxFilters(FormDataEventDTO formDataDTO) {
		ResponseEntity<Set<String>> apiResponse = serviceFeignClient.getUsedAttributes(formDataDTO.getServiceId(), "inboxSentboxFilter");
		if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
			Set<String> usedAttrSet=apiResponse.getBody();
			if(!usedAttrSet.isEmpty()) {
				formDataDTO.setKeys(usedAttrSet);
				kafkaProducer.sendInboxSentboxFilter(formDataDTO);
			}
		}
	}
	
	private void produceFormDataForCorrelationSet(FormDataEventDTO formDataDTO) {
		ResponseEntity<Set<String>> apiResponse = serviceFeignClient.getUsedAttributes(formDataDTO.getServiceId(), "correlationSet");
		if (HttpStatus.OK.equals(apiResponse.getStatusCode()) && apiResponse.getBody() != null) {
			Set<String> usedAttrSet=apiResponse.getBody();
			if(!usedAttrSet.isEmpty()) {
				formDataDTO.setKeys(usedAttrSet);
				kafkaProducer.sendCorrelationSet(formDataDTO);
			}
		}
	}
	


	private static List<String> getStrings(ApplicationData applicationData, CreateUploadSessionsRequest createUploadSessionsRequest) throws JSONException {

		ObjectMapper mapper = new ObjectMapper();
		LinkedHashMap<String, Object> appliedFormData = mapper.convertValue(applicationData.getFormData(), LinkedHashMap.class);
		List<CreateUploadSessionsRequest.FileUploadRequest> files = createUploadSessionsRequest.getFiles();
		List<String> uploadIds = new ArrayList<>();

		if (files == null || files.isEmpty()) {
			return uploadIds;
		}

		files.forEach(file -> {

			if (file == null) {
				return;
			}

			String referenceId = file.getReferenceId();
			if (referenceId == null || referenceId.trim().isEmpty()) {
				return;
			}

			Object value = appliedFormData.get(referenceId);
			if (value == null) {
				return;
			}

			if (value instanceof String && ((String) value).trim().isEmpty()) {
				return;
			}

			LinkedHashMap<String, Object> valueMap = mapper.convertValue(value, LinkedHashMap.class);

			Object uploadId = valueMap.get("uploadId");

			if (uploadId != null && !uploadId.toString().trim().isEmpty()) {
				uploadIds.add(uploadId.toString());
			}
		});

		return uploadIds;
	}


	public Map<String, Object> checkValidationOfApplicationData(Boolean draft, Object formJsonData, String formId, ApplicationData applicationData, String redisKey, Boolean globalFormEncripted) throws Exception {
		Map<String, Object> validationMessage = new HashMap<>();

		try {
			FormProcessCacheDTO formProcessCacheDTO;
			if (formRedisService.dataExists(redisKey)) {
				formProcessCacheDTO = (FormProcessCacheDTO) formRedisService.getDataFromRedis(redisKey);
			} else {
				formProcessCacheDTO = formRedisService.getAttributeDetails(formJsonData, formId, redisKey,globalFormEncripted);
			}

			List<FormProcessCacheDTO.Attribute> attributes = formProcessCacheDTO.getAttributes();
			if (attributes == null || attributes.isEmpty()) {
				return validationMessage;
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
			String formDataJson = objectMapper.writeValueAsString(applicationData.getFormData());
			JSONObject jsonFormDataObject = new JSONObject(formDataJson);
			Map<String, FormProcessCacheDTO.Attribute> attributeMap = new HashMap<>();
			for (FormProcessCacheDTO.Attribute attribute : attributes) {
				if (attribute.getAttributeId() != null) {
					attributeMap.put(attribute.getAttributeId(), attribute);
				}
			}
			
			Set<String> dynamicParentIds = new HashSet<>();
			Map<String, List<FormProcessCacheDTO.Attribute>> childrenByParentId = new HashMap<>();
			for (FormProcessCacheDTO.Attribute attribute : attributes) {
				String parentId = attribute.getParentId();
				if (parentId != null && !"null".equalsIgnoreCase(parentId.trim()) && !parentId.trim().isEmpty()) {
					dynamicParentIds.add(parentId);
					childrenByParentId.computeIfAbsent(parentId, k -> new ArrayList<>()).add(attribute);
				}
			}
			
			Set<String> foundAttributeIds = new HashSet<>();
			Set<String> invalidIds = new LinkedHashSet<>();
			validateFormJSONObject(draft, jsonFormDataObject, attributeMap, dynamicParentIds, childrenByParentId,
					foundAttributeIds, invalidIds, validationMessage);
			if (!validationMessage.isEmpty()) {
				return validationMessage;
			}
			for (FormProcessCacheDTO.Attribute attribute : attributes) {
				String attributeId = attribute.getAttributeId();
				String parentId = attribute.getParentId();
				boolean isDynamicChild = parentId != null && !"null".equalsIgnoreCase(parentId.trim())&& !parentId.trim().isEmpty();
				if (isDynamicChild) {
					continue;
				}
				if (!foundAttributeIds.contains(attributeId) && Boolean.TRUE.equals(attribute.getRequired())) {

					if (!validAttribute(draft, attribute.getTypeId(), attribute, null, validationMessage)) {

						return validationMessage;
					}
				}
			}
			if (!invalidIds.isEmpty()) {
				StringJoiner remainingKeys = new StringJoiner(", ");
				for (String invalidId : invalidIds) {
					remainingKeys.add(invalidId);
				}
				validationMessage.put("message", MessageConstants.INVALID_FORM_FEILDS_DATA);
				validationMessage.put("invalidIds", remainingKeys.toString());
				return validationMessage;
			}

		} catch (Exception e) {
			e.printStackTrace();
			validationMessage.put("message", MessageConstants.INTERNAL_ERROR);
			validationMessage.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return validationMessage;
	}


	private void validateFormJSONObject(Boolean draft, JSONObject jsonObject, Map<String, FormProcessCacheDTO.Attribute> attributeMap,
			Set<String> dynamicParentIds, Map<String, List<FormProcessCacheDTO.Attribute>> childrenByParentId,
			Set<String> foundAttributeIds, Set<String> invalidIds, Map<String, Object> validationMessage)
			throws Exception {

		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {

			String uniqueId = keys.next();
			Object attrValue = jsonObject.opt(uniqueId);
			FormProcessCacheDTO.Attribute attribute = attributeMap.get(uniqueId);
			if (attribute == null) {
				if (dynamicParentIds.contains(uniqueId)) {
					processDynamicRows(draft, uniqueId, attrValue, attributeMap, dynamicParentIds, childrenByParentId,
							foundAttributeIds, invalidIds, validationMessage);
					if (!validationMessage.isEmpty()) {
						return;
					}
					continue;
				}
				invalidIds.add(uniqueId);
				continue;
			}

			foundAttributeIds.add(uniqueId);
			if (!validAttribute(draft, attribute.getTypeId(), attribute, attrValue, validationMessage)) {
				return;
			}
			if (dynamicParentIds.contains(uniqueId)) {
				processDynamicRows(draft, uniqueId, attrValue, attributeMap, dynamicParentIds, childrenByParentId,
						foundAttributeIds, invalidIds, validationMessage);
				if (!validationMessage.isEmpty()) {
					return;
				}
				continue;
			}
			if (attrValue instanceof JSONObject) {
				continue;
			}
			if (attrValue instanceof JSONArray) {
				continue;
			}
		}
	}

	private void processDynamicRows(Boolean draft, String parentUniqueId, Object attrValue,
			Map<String, FormProcessCacheDTO.Attribute> attributeMap, Set<String> dynamicParentIds,
			Map<String, List<FormProcessCacheDTO.Attribute>> childrenByParentId, Set<String> foundAttributeIds,
			Set<String> invalidIds, Map<String, Object> validationMessage) throws Exception {

		if (!(attrValue instanceof JSONArray)) {
			return;
		}
		JSONArray rows = (JSONArray) attrValue;
		for (int i = 0; i < rows.length(); i++) {
			Object rowObject = rows.opt(i);
			if (!(rowObject instanceof JSONObject)) {
				continue;
			}
			JSONObject row = (JSONObject) rowObject;
			Set<String> rowFoundAttributeIds = new HashSet<>();
			validateFormJSONObject(draft, row, attributeMap, dynamicParentIds, childrenByParentId, rowFoundAttributeIds,
					invalidIds, validationMessage);
			if (!validationMessage.isEmpty()) {
				return;
			}
			List<FormProcessCacheDTO.Attribute> children = childrenByParentId.get(parentUniqueId);
			if (children != null) {
				for (FormProcessCacheDTO.Attribute child : children) {
					String childId = child.getAttributeId();
					if (!rowFoundAttributeIds.contains(childId) && Boolean.TRUE.equals(child.getRequired())) {
						if (!validAttribute(draft, child.getTypeId(), child, null, validationMessage)) {
							return;
						}
					}
				}
			}
			foundAttributeIds.addAll(rowFoundAttributeIds);
		}
	}


	private boolean validAttribute(Boolean draft, Integer typeId, FormProcessCacheDTO.Attribute element, Object attrValue,
			Map<String, Object> validationMessage) throws JSONException {
	    String uniqueId = element.getAttributeId();
	    boolean required = Boolean.TRUE.equals(element.getRequired());
	    boolean isEmpty = isEmptyValue(attrValue);
	    if (!draft && required && isEmpty) {
	        String validationError = defaultOr(element.getErrorLabel(), MessageConstants.REQUIRED_FIELD);
	        validationMessage.put("uniqueId", uniqueId);
	        validationMessage.put("validationError", validationError);
	        return false;
	    }
	    if (isEmpty) {
	        return true;
	    }
	    if (GeneralValidation.isXss(attrValue.toString())) {
	        validationMessage.put("uniqueId", uniqueId);
	        validationMessage.put("message", MessageConstants.MALICIOUS_REQUEST);
	        return false;
	    }

	    if (element.getOptionList() != null && !element.getOptionList().isEmpty()) {
	        List<LabelValue> formAttrOptionList = element.getOptionList();
	        boolean exists;
	        if (attrValue instanceof JSONObject) {
	            JSONObject json = (JSONObject) attrValue;
	            LabelValue applLabelValue = new LabelValue();
	            applLabelValue.setLabel(json.optString("label"));
	            applLabelValue.setValue(json.optString("value"));

	            exists = formAttrOptionList.stream().anyMatch(opt -> opt.getLabel().equals(applLabelValue.getLabel()) && opt.getValue().equals(applLabelValue.getValue()));
	        } else if (attrValue instanceof JSONArray) {
	            JSONArray jsonArray = (JSONArray) attrValue;
	            exists = jsonArray.length() > 0;
	            for (int i = 0; i < jsonArray.length() && exists; i++) {
	                Object entry = jsonArray.opt(i);
	                if (!(entry instanceof JSONObject)) {
	                    exists = false;
	                    break;
	                }
	                JSONObject json = (JSONObject) entry;
	                LabelValue applLabelValue = new LabelValue();
	                applLabelValue.setLabel(json.optString("label"));
	                applLabelValue.setValue(json.optString("value"));

	                exists = formAttrOptionList.stream().anyMatch(opt ->opt.getLabel().equals(applLabelValue.getLabel())&& opt.getValue().equals(applLabelValue.getValue()));
	            }

	        } else {
	            exists = false;
	        }

	        if (!exists) {
	            String validationError = defaultOr(element.getErrorLabel(), MessageConstants.INVALID_DATA);
	            validationMessage.put("uniqueId", uniqueId);
	            validationMessage.put("message", validationError);
	            return false;
	        }

	    } else {

	        Integer min = element.getMin();
	        Integer max = element.getMax();
	        Integer minLength = element.getMinLenght();
	        Integer maxLength = element.getMaxLength();
	        LocalDate minDate = element.getMinDate();
	        LocalDate maxDate = element.getMaxDate();

	        if (min != null || max != null) {

	            Integer numericValue = tryParseInt(attrValue);

	            if (numericValue == null) {
	                return invalid(element, validationMessage, uniqueId);
	            }
	            if (min != null && numericValue < min) {
	                return invalid(element, validationMessage, uniqueId);
	            }
	            if (max != null && numericValue > max) {
	                return invalid(element, validationMessage, uniqueId);
	            }
	        }

	        if (minLength != null && attrValue.toString().length() < minLength) {
	            return invalid(element, validationMessage, uniqueId);
	        }

	        if (maxLength != null && attrValue.toString().length() > maxLength) {
	            return invalid(element, validationMessage, uniqueId);
	        }

	        if (minDate != null || maxDate != null) {

	            LocalDate submittedDate = tryParseDate(attrValue);

	            if (submittedDate == null) {
	                return invalid(element, validationMessage, uniqueId);
	            }
	            if (minDate != null && minDate.isAfter(submittedDate)) {
	                return invalid(element, validationMessage, uniqueId);
	            }
	            if (maxDate != null && maxDate.isBefore(submittedDate)) {
	                return invalid(element, validationMessage, uniqueId);
	            }
	        }
	    }

	    return true;
	}


	private boolean isEmptyValue(Object attrValue) {

	    if (attrValue == null || attrValue == JSONObject.NULL) {
	        return true;
	    }

	    if (attrValue instanceof String) {
	        String value = ((String) attrValue).trim();
	        return value.isEmpty() || "null".equalsIgnoreCase(value);
	    }

	    if (attrValue instanceof JSONArray) {
	        return ((JSONArray) attrValue).length() == 0;
	    }

	    if (attrValue instanceof JSONObject) {
	        return ((JSONObject) attrValue).length() == 0;
	    }

	    return false;
	}


	private boolean invalid(FormProcessCacheDTO.Attribute element, Map<String, Object> validationMessage, String uniqueId) {
	    String validationError = defaultOr(element.getErrorLabel(), MessageConstants.INVALID_DATA);
	    validationMessage.put("uniqueId", uniqueId);
	    validationMessage.put("message", validationError);
	    return false;
	}


	private String defaultOr(String value, String fallback) {
	    return (value != null && !value.isEmpty()) ? value : fallback;
	}

	private Integer tryParseInt(Object attrValue) {
	    try {
	        return Integer.parseInt(attrValue.toString().trim());
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}
	private LocalDate tryParseDate(Object attrValue) {
	    if (!(attrValue instanceof String)) {
	        return null;
	    }
	    String value = ((String) attrValue).trim();
	    try {
	        return LocalDate.parse(value);
	    } catch (DateTimeParseException e) {
	        try {
	            return Instant.parse(value).atZone(ZoneOffset.UTC).toLocalDate();
	        } catch (DateTimeParseException ex) {
	            return null;
	        }
	    }
	}

	public ResponseEntity<?> autoSave(ApplicationAutoSaveDTO applicationAutoSaveDTO, UserSessionDTO userSessionDetails) {
		try {
			Long userId = userSessionDetails.getUserID();
			ApplicationAutoSave applicationAutoSave = autoSaveRepository.findByUserIDAndServiceIdAndTaskIdAndFormIdAndApplicationId(userId, applicationAutoSaveDTO.getServiceId(), applicationAutoSaveDTO.getTaskId(), applicationAutoSaveDTO.getFormId(), applicationAutoSaveDTO.getApplicationId()).orElse(null);
			if(isNull(applicationAutoSave)) {
				applicationAutoSave = new ApplicationAutoSave();
			}
			applicationAutoSave.setUserID(userId);
			applicationAutoSave.setServiceId(applicationAutoSaveDTO.getServiceId());
			applicationAutoSave.setTaskId(applicationAutoSaveDTO.getTaskId());
			applicationAutoSave.setFormId(applicationAutoSaveDTO.getFormId());
			applicationAutoSave.setApplicationId(applicationAutoSaveDTO.getApplicationId());
			applicationAutoSave.setFormData(applicationAutoSaveDTO.getFormData());
			applicationAutoSave.setLastUpdateDate(new Date());
			autoSaveRepository.save(applicationAutoSave);
			return ResponseEntity.ok(Map.of("message", "ok", "status", HttpStatus.OK.value()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError(MessageConstants.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getAutoSave(UserSessionDTO userSessionDetails, Integer serviceId, String taskId,
			String formId, String applicationId) {
		try {
			Long userId = userSessionDetails.getUserID();
			ApplicationAutoSave applicationAutoSave = autoSaveRepository.findByUserIDAndServiceIdAndTaskIdAndFormIdAndApplicationId(userId, serviceId, taskId, formId, applicationId).orElse(null);
			if(isNull(applicationAutoSave)) {
				return ResponseEntity.ok(Collections.emptyMap());
			}
			return ResponseEntity.ok(applicationAutoSave.getFormData());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError(MessageConstants.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    public ResponseEntity<?> fetchWorkflowAttributes(WorkflowAttributeRequest request) throws Exception {

        Map<String, Object> response = new HashMap<>();

        List<ApplicationTemplate> templates = applicationTemplateRepository.findByFormIdAndStatus(request.getFormId(), FormStatus.PUBLISHED.getValue());

        LOGGER.info("Fetching workflow attributes | formId={} | dataId={}", request.getFormId(), request.getDataId());

        if (isNull(templates) || templates.isEmpty()) {

            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Invalid Form Id");
            return ResponseEntity.badRequest().body(response);
        }

        ApplicationData applicationData = applicationDataRepository.findByDataId(request.getDataId());

        if (isNull(applicationData)) {

            LOGGER.error("Invalid Data Id | formId={} | dataId={}", request.getFormId(), request.getDataId());

            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Invalid Data Id");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> workflowData = applicationDataCommonUtil.getAllWorkflowAttrVal(templates.getFirst(), applicationData);

        LOGGER.info("Workflow attributes extracted | formId={} | dataId={} | workflowData={}", request.getFormId(), request.getDataId(), workflowData);


        LOGGER.info("Returning workflow attributes successfully | formId={} | dataId={}", request.getFormId(), request.getDataId());

        return ResponseEntity.ok(workflowData);
    }

	public Map<String, Object> getApplicationAttributeValue(ApplicationAttributeValueRequest request,
			HttpServletResponse response, HttpServletRequest servletRequest) {

		Map<String, Object> responseData = new HashMap<>();
		Map<String, Object> status = new HashMap<>();

		status.put("code", 200);
		status.put("message", "");

		try {

			if (request == null || request.getApplicationId() == null || request.getTaskId() == null
					|| request.getHolderId() == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				status.put("code", 400);
				status.put("message", "applicationId, taskId, holderId");
				responseData.put("status", status);

				return responseData;
			}

			ApplicationData applicationDataDB = applicationDataRepository.findByApplIdAndTaskIdAndHolderId(
					request.getApplicationId(), request.getTaskId(), request.getHolderId());

			if (applicationDataDB == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				status.put("code", 404);
				status.put("message", MessageConstants.RECORD_NOT_FOUND);
				responseData.put("status", status);
				return responseData;
			}

			Object formData = applicationDataDB.getFormData();

			if (formData == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				status.put("code", 404);
				status.put("message", "Application form data not found");
				responseData.put("status", status);
				return responseData;
			}

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> applicationData = mapper.convertValue(formData, LinkedHashMap.class);
			Map<String, Object> attributeValues = new LinkedHashMap<>();

			if (request.getAttributeIds() != null && !request.getAttributeIds().isEmpty()) {
				for (String attributeId : request.getAttributeIds()) {
					if (attributeId == null || attributeId.isBlank()) {
						continue;
					}
					Object attributeValue = applicationData.get(attributeId);
					attributeValues.put(attributeId, attributeValue);
				}
			} else {
				attributeValues.putAll(applicationData);
			}
			responseData.put("applicationId", request.getApplicationId());
			responseData.put("taskId", request.getTaskId());
			responseData.put("holderId", request.getHolderId());
			responseData.put("attributeValues", attributeValues);

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			status.put("code", 500);
			status.put("message", e.getMessage());
		}
		responseData.put("status", status);

		return responseData;
	}
	


public ApplicationData getTrackingApplicationData(
        String tenantId, Integer serviceId, String applId) {

    if (tenantId == null || tenantId.isBlank()
            || serviceId == null
            || applId == null || applId.isBlank()) {
        throw new IllegalArgumentException(
                "Invalid tracking application details");
    }

    List<ApplicationData> records =
            applicationDataRepository.findTrackingApplicationData(
                    tenantId, serviceId, applId);

    if (records == null) {
        throw new IllegalStateException(
                "Tracking application data unavailable");
    }

    List<ApplicationData> submissions = records.stream()
            .filter(data -> "s_1".equals(data.getTaskId()))
            .toList();

    if (submissions.size() != 1) {
        throw new IllegalStateException(
                "Tracking application data unavailable or ambiguous");
    }

    ApplicationData submitted = submissions.get(0);

    if (!tenantId.equals(submitted.getTenantId())
            || !serviceId.equals(submitted.getServiceId())
            || !applId.equals(submitted.getApplId())
            || submitted.getDataId() == null
            || submitted.getFormId() == null) {
        throw new IllegalStateException("Submission scope invalid");
    }

    ApplicationTemplate template =
            applicationTemplateRepository.findByFormId(
                    submitted.getFormId());

    if (template == null || template.getFormJsonData() == null) {
        throw new IllegalStateException(
                "Submission template unavailable");
    }

    // A form owned by another tenant is allowed only when the submitted
    // application has an authoritative form_data_reference for this exact scope.
    // Never trust formId alone to establish a cross-tenant sharing relationship.
    if (template.getTenantId() == null || template.getTenantId().isBlank()) {
        throw new IllegalStateException("Submission form tenant unavailable");
    }
    if (!tenantId.equals(template.getTenantId())) {
        List<FormDataReference> references = formDataReferenceRepository == null
                ? Collections.emptyList()
                : formDataReferenceRepository.findByServiceIdAndApplicationId(serviceId, applId);
        boolean authorizedReference = references != null && references.stream().anyMatch(ref ->
                tenantId.equals(ref.getTenantId())
                        && serviceId.equals(ref.getServiceId())
                        && applId.equals(ref.getApplicationId())
                        && submitted.getDataId().equals(ref.getDataId())
                        && submitted.getFormId().equals(ref.getFormId())
                        && "s_1".equals(ref.getTaskId()));
        if (!authorizedReference) {
            throw new IllegalStateException("Submission form tenant unauthorized");
        }
    }

    ApplicationData copy =
            new ObjectMapper().convertValue(
                    submitted, ApplicationData.class);

    try {
        FormEncryptionKey key =
                formEncryptionKeyRepository
                    .findByFormIdAndServiceIdAndTaskId(
                            submitted.getFormId(),
                            serviceId,
                            submitted.getTaskId())
                    .orElse(null);

        String restKey = key != null
                ? key.getRestEncKey()
                : template.getEncryptionKeyAtRest();

        boolean global = key != null;

        if (global
                || ApplicationTemplateCommonUtil
                    .isFormDataEncryptedAtRest(template)) {

            if (restKey == null || restKey.isBlank()) {
                throw new IllegalStateException(
                        "Submission key unavailable");
            }

            String cacheKey = global
                    ? serviceId
                        + ApplicationConstants.DELIMITER
                        + submitted.getTaskId()
                        + ApplicationConstants.DELIMITER
                        + submitted.getFormId()
                    : submitted.getFormId();

            applicationDataCommonUtil.decryptApplicationAttributeData(
                    template,
                    copy,
                    restKey,
                    cacheKey,
                    global);
        }

    } catch (Exception ex) {
        throw new IllegalStateException(
                "Submission verification data unavailable", ex);
    }

    if (!(copy.getFormData() instanceof Map<?, ?>)) {
        throw new IllegalStateException(
                "Submission values unavailable");
    }

    return copy;
}



    /** Private Mode O lookup. Never return submitted fields or complete forms to TrackingService. */

public Map<String, Object> matchTrackingApplications(Map<String, Object> body) {
	

    if (!(body.get("tenantId") instanceof String tenant) || tenant.isBlank()
            || !(body.get("serviceId") instanceof Number number)
            || number.longValue() < 1
            || number.longValue() > Integer.MAX_VALUE
            || number.doubleValue() != number.intValue()
            || !(body.get("method") instanceof String method)) {
        throw new IllegalArgumentException("Invalid tracking scope");
    }

    int service = number.intValue();

    Map<String, Object> config =
            serviceFeignClient.getPublicTrackingConfiguration(trackingInternalToken, (long) service, tenant);

    if (config == null
            || !(config.get("serviceId") instanceof Number configuredService)
            || configuredService.longValue() != service
            || !(config.get("trackingTypes") instanceof List<?> types)
            || !types.contains(method)) {
        throw new IllegalArgumentException("Verification method unavailable");
    }

    Map<String, Integer> fields = new LinkedHashMap<>();
    Map<String, String> expected = new LinkedHashMap<>();

    boolean attributes = "FORM_ATTRIBUTES".equals(method);
    String contact = null;

    if (attributes) {

        if (!(config.get("formAttributes") instanceof List<?> configured)
                || configured.size() < 3
                || configured.size() > 5
                || !(body.get("values") instanceof Map<?, ?> supplied)) {
            throw new IllegalArgumentException("Invalid tracking fields");
        }

        for (Object item : configured) {
            trackingField(fields, item);
        }

        if (fields.size() != configured.size()
                || !fields.keySet().equals(supplied.keySet())) {
            throw new IllegalArgumentException("Invalid tracking fields");
        }

        // Attribute-only access requires an operator-approved, high-entropy
        // identifying field. Name, DOB, address and other guessable fields
        // must never grant public access on their own.
        if (!hasApprovedStrongTrackingAttribute(tenant, service, fields.keySet())) {
            return Map.of("applicationIds", List.of(), "sufficient", false);
        }

        for (String id : fields.keySet()) {
            String normalized =
                    normalizeTrackingValue(supplied.get(id), fields.get(id));

            if (normalized == null) {
                throw new IllegalArgumentException("Invalid tracking values");
            }

            expected.put(id, normalized);
        }

    } else {

        if (!"MOBILE_OTP".equals(method)
                && !"EMAIL_OTP".equals(method)) {
            throw new IllegalArgumentException("Invalid method");
        }

        trackingField(
                fields,
                config.get("MOBILE_OTP".equals(method)
                        ? "mobileAttribute"
                        : "emailAttribute")
        );

        if (!(body.get("contact") instanceof String text)) {
            throw new IllegalArgumentException("Invalid contact");
        }

        contact = "MOBILE_OTP".equals(method)
                ? normalizeTrackingMobile(text)
                : normalizeTrackingEmail(text);

        if (contact == null) {
            throw new IllegalArgumentException("Invalid contact");
        }

        expected.put(fields.keySet().iterator().next(), contact);
    }

    Set<String> matches = new LinkedHashSet<>();
    Set<String> visited = new HashSet<>();

    int scanned = 0;

    org.springframework.data.domain.Pageable pageable =
            org.springframework.data.domain.PageRequest.of(
                    0,
                    200,
                    org.springframework.data.domain.Sort.by("dataId")
            );

    while (true) {

        org.springframework.data.domain.Page<ApplicationData> page =
                applicationDataRepository.findSubmittedTrackingApplications(
                        tenant, service, pageable
                );

        if (page == null) {
            throw new IllegalStateException("Submission search unavailable");
        }

        for (ApplicationData row : page.getContent()) {

            if (++scanned > trackingMaxScanRecords) {
                throw new IllegalStateException(
                        "Submission search limit exceeded; narrow verification required"
                );
            }

            if (!"s_1".equals(row.getTaskId())
                    || !visited.add(row.getApplId())) {
                continue;
            }

            try {

                ApplicationData data =
                        getTrackingApplicationData(
                                tenant, service, row.getApplId()
                        );

                if (data == null || data.getFormId() == null) {
                    throw new IllegalStateException("Tracking submission form unavailable");
                }

                ApplicationTemplate template =
                        applicationTemplateRepository.findByFormId(
                                data.getFormId()
                        );

                if (template == null) {
                    throw new IllegalStateException("Tracking submission template unavailable");
                }

                Map<String, Integer> actualFields = new HashMap<>();

                trackingTemplateFields(
                        new ObjectMapper().valueToTree(
                                template.getFormJsonData()
                        ),
                        actualFields
                );

                if (!(data.getFormData() instanceof Map<?, ?> stored)) {
                    throw new IllegalStateException("Tracking submission values unavailable");
                }

                boolean matched = true;

                for (String id : fields.keySet()) {

                    if (!fields.get(id).equals(actualFields.get(id))) {
                        matched = false;
                        break;
                    }

                    Object value = stored.get(id);

                    String actual;

                    if (attributes) {
                        actual = normalizeTrackingValue(
                                value, fields.get(id)
                        );
                    } else if ("MOBILE_OTP".equals(method)) {
                        actual = normalizeTrackingMobile(value);
                    } else {
                        actual = value instanceof String text
                                ? normalizeTrackingEmail(text)
                                : null;
                    }

                    if (!expected.get(id).equals(actual)) {
                        matched = false;
                        break;
                    }
                }

                if (matched) {
                    matches.add(data.getApplId());
                }

                if (matches.size() > 100) {
                    throw new IllegalStateException(
                            "Too many matching applications; narrow verification required"
                    );
                }

            } catch (IllegalStateException ex) {
                // Any unreadable/ambiguous submission makes the search incomplete.
                // Never issue a public verification proof from partial results.
                LOGGER.error("Public tracking verification scan failed for serviceId={}", service, ex);
                throw new IllegalStateException("Submission verification unavailable", ex);
            }
        }

        if (!page.hasNext()) {
            break;
        }

        pageable = page.nextPageable();
    }

    if (attributes) {
        boolean sufficient = matches.size() == 1;

        return Map.of(
                "applicationIds",
                sufficient ? new ArrayList<>(matches) : List.of(),
                "sufficient",
                sufficient
        );
    }

    return Map.of(
            "applicationIds", new ArrayList<>(matches),
            "contact", contact
    );
}

    private void trackingField(Map<String,Integer> fields,Object item) {
        if(!(item instanceof Map<?,?> field) || !(field.get("attributeId") instanceof String id) || id.isBlank()
            || !(field.get("typeId") instanceof Number type) || fields.put(id,type.intValue())!=null)
            throw new IllegalArgumentException("Invalid configured field");
    }
    private void trackingTemplateFields(com.fasterxml.jackson.databind.JsonNode node,Map<String,Integer> fields) {
        if(node.isObject()) {
            if(node.hasNonNull("uniqueId") && node.hasNonNull("typeId")) {
                String id=node.get("uniqueId").asText();int type=node.get("typeId").asInt();
                if(fields.put(id,type)!=null) throw new IllegalStateException("Ambiguous template field");
            }
            node.elements().forEachRemaining(child->trackingTemplateFields(child,fields));
        } else if(node.isArray()) node.elements().forEachRemaining(child->trackingTemplateFields(child,fields));
    }


    /**
     * Server-side allowlist syntax: tenantId:serviceId:attributeId
     * Multiple entries may be separated by commas, semicolons or newlines.
     * This is an explicit policy, not a claim that any configured form field
     * automatically provides sufficient identity assurance.
     */
    private boolean hasApprovedStrongTrackingAttribute(String tenant, int service, Set<String> configuredIds) {
        if (trackingStrongAttributes == null || trackingStrongAttributes.isBlank()) {
            return false;
        }
        for (String entry : trackingStrongAttributes.split("[,;\\r\\n]+")) {
            String[] parts = entry.trim().split(":", 3);
            if (parts.length == 3
                    && tenant.equals(parts[0].trim())
                    && Integer.toString(service).equals(parts[1].trim())
                    && configuredIds.contains(parts[2].trim())) {
                return true;
            }
        }
        return false;
    }

    // Tracking field comparisons stay here because only this service uses them.
    static String normalizeTrackingValue(Object value,int type) {
        if(type==7) return normalizeTrackingCheckboxCodes(value);
        if(value==null || !(value instanceof String || value instanceof Number)) return null;
        String text=Normalizer.normalize(value.toString(),Normalizer.Form.NFKC).strip().replaceAll("(?U)\\s+"," ");
        if(text.isBlank() || text.length()>512) return null;
        try {
            return switch(type) {
                case 1,4 -> text.toLowerCase(Locale.ROOT);
                case 2 -> new BigDecimal(text).stripTrailingZeros().toPlainString();
                case 3 -> normalizeTrackingEmail(text);
                case 8 -> normalizeTrackingDate(text);
                case 6,40 -> text; // option codes are case sensitive
                case 46 -> normalizeTrackingRichText(text);
                default -> null;
            };
        } catch(RuntimeException ex) {return null;}
    }
    private static String normalizeTrackingDate(String text) {
        for (String pattern : List.of(
                "uuuu-MM-dd", "dd/MM/uuuu", "dd-MM-uuuu")) {
            try {
                return LocalDate.parse(text,
                        DateTimeFormatter.ofPattern(pattern)
                                .withResolverStyle(ResolverStyle.STRICT))
                        .toString();
            } catch (DateTimeParseException ignored) {
            }
        }

        try {
            return Instant.parse(text)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                    .toString();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
    private static String normalizeTrackingEmail(String text) {
        String email=text.strip().toLowerCase(Locale.ROOT);
        return email.length()<=254 && email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")?email:null;
    }
    private static String normalizeTrackingMobile(Object value) {
        String text;
        try { text=value instanceof Number?new BigDecimal(value.toString()).stripTrailingZeros().toPlainString():value instanceof String string?string:null; }
        catch(RuntimeException invalid) {return null;}
        if(text==null || !text.strip().matches("\\+?[0-9 ()-]{8,25}")) return null;
        String digits=text.replaceAll("[^0-9]","");
        return digits.matches("[0-9]{8,15}")?digits:null;
    }

    private static String normalizeTrackingCheckboxCodes(Object value) {
        if(!(value instanceof Collection<?> codes) || codes.isEmpty() || codes.size()>100) return null;
        SortedSet<String> normalized=new TreeSet<>();
        for(Object code:codes) {
            String text=normalizeTrackingValue(code,40);
            if(text==null || !normalized.add(text)) return null;
        }
        StringBuilder result=new StringBuilder();
        for(String code:normalized) result.append(code.length()).append(':').append(code);
        return result.toString();
    }
    private static String normalizeTrackingRichText(String html) {
        StringBuilder plain=new StringBuilder();
        try {
            new javax.swing.text.html.parser.ParserDelegator().parse(new java.io.StringReader(html),
                new javax.swing.text.html.HTMLEditorKit.ParserCallback() {
                    private boolean hidden;
                    public void handleStartTag(javax.swing.text.html.HTML.Tag tag,javax.swing.text.MutableAttributeSet attrs,int pos) {
                        if(tag==javax.swing.text.html.HTML.Tag.SCRIPT || tag==javax.swing.text.html.HTML.Tag.STYLE) hidden=true;
                        if(tag.isBlock()) plain.append(' ');
                    }
                    public void handleEndTag(javax.swing.text.html.HTML.Tag tag,int pos) {
                        if(tag==javax.swing.text.html.HTML.Tag.SCRIPT || tag==javax.swing.text.html.HTML.Tag.STYLE) hidden=false;
                        if(tag.isBlock()) plain.append(' ');
                    }
                    public void handleSimpleTag(javax.swing.text.html.HTML.Tag tag,javax.swing.text.MutableAttributeSet attrs,int pos) {
                        if(tag==javax.swing.text.html.HTML.Tag.BR) plain.append(' ');
                    }
                    public void handleText(char[] data,int pos) {if(!hidden) plain.append(data);}
                },true);
            return normalizeTrackingValue(plain.toString(),1);
        } catch(java.io.IOException invalid) {return null;}
    }
}
