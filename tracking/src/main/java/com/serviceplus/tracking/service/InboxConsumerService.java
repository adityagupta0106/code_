package com.serviceplus.tracking.service;

import static com.serviceplus.tracking.utility.ApplicationConstants.ACTION_CALLBACK;
import static com.serviceplus.tracking.utility.CommonUtil.buildUserToken;
import static com.serviceplus.tracking.utility.CommonUtil.isEmpty;
import static com.serviceplus.tracking.utility.CommonUtil.extractActionTinyUrlKey;
import static com.serviceplus.tracking.utility.CommonUtil.extractDocumentTinyUrlKey;
import static java.util.Objects.isNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.serviceplus.tracking.correlationapplication.service.CorrelationSetFormDataService;
import com.serviceplus.tracking.dto.ApplicantTaskDetails;
import com.serviceplus.tracking.dto.ApplicationCurrentProcess;
import com.serviceplus.tracking.dto.OfficeDetailsDTO;
import com.serviceplus.tracking.dto.TrackingDocument;
import com.serviceplus.tracking.dto.UserInboxDTO;
import com.serviceplus.tracking.dto.UserInboxKafkaDTO;
import com.serviceplus.tracking.entity.ApplicantInbox;
import com.serviceplus.tracking.entity.ApplicationActionTinyUrl;
import com.serviceplus.tracking.entity.ApplicationDocumentTinyUrl;
import com.serviceplus.tracking.entity.ApplicationTracking;
import com.serviceplus.tracking.entity.ApplicationTrackingDocument;
import com.serviceplus.tracking.entity.ApplicationTrackingHolder;
import com.serviceplus.tracking.entity.UserInbox;
import com.serviceplus.tracking.entity.UserSentBox;
import com.serviceplus.tracking.repository.ApplicantInboxRepository;
import com.serviceplus.tracking.repository.ApplicationActionTinyUrlRepository;
import com.serviceplus.tracking.repository.ApplicationDocumentTinyUrlRepository;
import com.serviceplus.tracking.repository.ApplicationTrackingDocumentRepository;
import com.serviceplus.tracking.repository.ApplicationTrackingHolderRepository;
import com.serviceplus.tracking.repository.ApplicationTrackingRepository;
import com.serviceplus.tracking.repository.UserInboxRepository;
import com.serviceplus.tracking.repository.UserSentBoxRepository;

@Service
public class InboxConsumerService {

    private static final Logger log = LoggerFactory.getLogger(InboxConsumerService.class);


    private final UserInboxRepository userInboxRepository;

    private final UserSentBoxRepository userSentBoxRepository;

    private final ApplicationTrackingRepository applicationTrackingRepository;

    private final WorkflowInboxRedisService workflowInboxRedisService;

    private final ApplicationTrackingHolderRepository trackingHolderRepository;

    private final ApplicationTrackingDocumentRepository applicationTrackingDocumentRepository;

    private final ApplicantInboxRepository applicantInboxRepository;
    
    private final ApplicationDocumentTinyUrlRepository applicationDocumentTinyUrlRepository;
    
    private final ApplicationActionTinyUrlRepository applicationActionTinyUrlRepository;
    
    private final CorrelationSetFormDataService correlationSetFormDataService;

    public InboxConsumerService(UserInboxRepository userInboxRepository, UserSentBoxRepository userSentBoxRepository, ApplicationTrackingRepository applicationTrackingRepository, WorkflowInboxRedisService workflowInboxRedisService, ApplicationTrackingHolderRepository trackingHolderRepository, ApplicationTrackingDocumentRepository applicationTrackingDocumentRepository, ApplicantInboxRepository applicantInboxRepository, ApplicationDocumentTinyUrlRepository applicationDocumentTinyUrlRepository, ApplicationActionTinyUrlRepository applicationActionTinyUrlRepository, CorrelationSetFormDataService correlationSetFormDataService) {
        this.userInboxRepository = userInboxRepository;
        this.userSentBoxRepository = userSentBoxRepository;
        this.applicationTrackingRepository = applicationTrackingRepository;
        this.workflowInboxRedisService = workflowInboxRedisService;
        this.trackingHolderRepository = trackingHolderRepository;
        this.applicationTrackingDocumentRepository = applicationTrackingDocumentRepository;
        this.applicantInboxRepository = applicantInboxRepository;
		this.applicationDocumentTinyUrlRepository = applicationDocumentTinyUrlRepository;
		this.applicationActionTinyUrlRepository = applicationActionTinyUrlRepository;
		this.correlationSetFormDataService = correlationSetFormDataService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Transactional
    public void updateUserInboxFromKafka(String key, String message, String topic) {

        try {
            log.info("Received inbox Kafka message. Topic: {}, Key: {}", topic, key);
            System.out.println(message);

            UserInboxKafkaDTO kafkaDTO = objectMapper.readValue(message, UserInboxKafkaDTO.class);
            processApplicationTracking(kafkaDTO);
            processSentBox(kafkaDTO);
            processInbox(kafkaDTO);
            processApplicantInbox(kafkaDTO);

            if(kafkaDTO.getProcessList() != null && kafkaDTO.getProcessList().size() > 0) {
            	correlationSetFormDataService.saveSystemVariable(kafkaDTO);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processInbox(UserInboxKafkaDTO kafkaDTO) {

        List<UserInboxDTO> inboxList = mapDTOValues(kafkaDTO);

        for (UserInboxDTO dto : inboxList) {
            persistInboxEntries(dto);
        }

        log.info("Inbox processing completed for applicationRefNo: {}", kafkaDTO.getApplicationRefNo());
    }

    private void processSentBox(UserInboxKafkaDTO kafkaDTO) {

        if (kafkaDTO == null || kafkaDTO.getProcessList() == null) {
            return;
        }
        //-------------------------
        if (kafkaDTO.getCallbackSourceProcessId() != null
                && !kafkaDTO.getCallbackSourceProcessId().isBlank()) {

            String tenantId =
                    kafkaDTO.getProcessList()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(ApplicationCurrentProcess::getTenantId)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

            if (tenantId != null) {

                int updatedRows =
                        userSentBoxRepository.markAsCalledBack(
                                kafkaDTO.getCallbackSourceProcessId(),
                                tenantId,
                                ACTION_CALLBACK
                        );

                System.out.println(
                        "CALLBACK SOURCE SENTBOX UPDATED = "
                                + updatedRows
                );
            }
        }
      //-------------------------

        List<UserSentBox> sentList = new ArrayList<>();

        for (ApplicationCurrentProcess process : kafkaDTO.getProcessList()) {
        	
        	
        	if (process == null) {
        		continue;
        	}
        	
        	if (ACTION_CALLBACK.equals(process.getActionCode())) {

        	    System.out.println("===== CALLBACK DEBUG =====");
        	    System.out.println("P200 processId       = " + process.getProcessId());
        	    System.out.println("P100 previousProcess = " + process.getPreviousProcessId());
        	    System.out.println("tenantId             = " + process.getTenantId());
        	    System.out.println("actionCode           = " + process.getActionCode());

        	    userInboxRepository.deleteByCurrentProcessIdAndTenantId(process.getProcessId(),process.getTenantId());

        	    
        	    System.out.println("==========================");

        	    continue;
        	}

            if ("N".equals(process.getActionTaken()) || isEmpty(process.getPreviousProcessId()) || process.getGateway()) {
                continue;
            }

            Set<Object> holderIds = workflowInboxRedisService.getTokenByServiceTaskAndUser(
                                            process.getServiceId(),
                                            process.getCurrentTask(),
                                            kafkaDTO.getLoggedInUserId(),
                                            String.valueOf(kafkaDTO.getLoggedInUserLocation())
                                    );

            if (holderIds == null || holderIds.isEmpty()) {
                log.warn("No holder token found for serviceId={}, taskId={}, userId={}, locationId={}", process.getServiceId(), process.getCurrentTask(), kafkaDTO.getLoggedInUserId(), kafkaDTO.getLoggedInUserLocation());
                 continue;
            }

            Set<String> holderIdStrings = holderIds.stream()
                                                .map(String::valueOf)
                                                .map(String::trim)
                                                .collect(Collectors.toSet());

            String processId = process.getProcessId() == null ? null : process.getProcessId().trim();

            String tenantId = process.getTenantId() == null ? null : process.getTenantId().trim();

            List<UserInbox> inboxList =
                                        userInboxRepository
                                                .findByCurrentProcessIdAndUserTokenInAndTenantId(
                                                        processId,
                                                        holderIdStrings,
                                                        tenantId
                                                );

            if (inboxList.isEmpty()) {
                log.warn("No inbox entries found for processId={}, holderIds={}", process.getProcessId(), holderIdStrings);
                continue;
            }

            for (UserInbox inbox : inboxList) {
                sentList.add(getUserSentBox(kafkaDTO, process, inbox));
            }

            userInboxRepository.deleteAll(inboxList);
        }

        if (!sentList.isEmpty()) {

            log.info("Saving {} sent box entries", sentList.size());
            userSentBoxRepository.saveAll(sentList);
        }
    }

    private static UserSentBox getUserSentBox(UserInboxKafkaDTO kafkaDTO, ApplicationCurrentProcess process, UserInbox inbox) {

        UserSentBox sent = new UserSentBox();

        sent.setBaseServiceId(inbox.getBaseServiceId());
        sent.setServiceId(inbox.getServiceId());
        sent.setServiceName(inbox.getServiceName());

        sent.setApplId(inbox.getApplId());
        sent.setApplRefNo(inbox.getApplRefNo());

        sent.setSubmissionDate(kafkaDTO.getApplyDate());
        sent.setProcessId(process.getProcessId());
        sent.setTaskId(inbox.getTaskId());
        sent.setTaskName(inbox.getTaskName());

        sent.setCalledback(Boolean.FALSE);
        sent.setHolderId(inbox.getUserToken());

        sent.setActionCode(process.getActionCode());
        sent.setActionUserId(process.getUserId().intValue());

        sent.setActionOn(process.getActionOn());
        sent.setActionOnDate(process.getActionOn());

        sent.setTenantId(process.getTenantId());

        return sent;
    }

    private List<UserInboxDTO> mapDTOValues(UserInboxKafkaDTO kafkaDTO) {

        List<UserInboxDTO> result = new ArrayList<>();

        if (kafkaDTO == null || kafkaDTO.getProcessList() == null) {
            log.warn("Received null Kafka DTO or process list");
            return result;
        }
        //System.out.println(kafkaDTO.getProcessList().toString());

        log.info("Processing {} current process records", kafkaDTO.getProcessList().size());

        for (ApplicationCurrentProcess process : kafkaDTO.getProcessList()) {

            if (!"N".equals(process.getActionTaken())) {
                log.debug("Skipping processId {} as actionTaken={}", process.getProcessId(), process.getActionTaken());
                //complete closure case
                if(kafkaDTO.isCompleteClosure()) {
                	userInboxRepository.deleteByApplIdAndTenantIdAndBaseServiceId(process.getApplicationId(),process.getTenantId(),process.getBaseServiceId());
                }
                continue;
            }

            UserInboxDTO dto = new UserInboxDTO();

            dto.setApplId(process.getApplicationId());
            dto.setApplRefNo(kafkaDTO.getApplicationRefNo());
            dto.setServiceId(process.getServiceId());
            dto.setServiceName(kafkaDTO.getServiceName());
            dto.setApplRecievedOn(process.getInitiatedOn());
            dto.setLastActionOn(process.getActionOn());
            dto.setTenantId(process.getTenantId());
            dto.setBaseServiceId(process.getBaseServiceId());

            UserInboxDTO.TaskDTO task = new UserInboxDTO.TaskDTO();
            task.setFormId(process.getFormId());
            task.setTaskId(process.getCurrentTask());
            task.setTaskName(process.getCurrentTaskName());
            task.setCurrentProcessId(process.getProcessId());
            task.setIsPriority(process.getIsPriority());

            List<UserInboxDTO.LocationDTO> locations = new ArrayList<>();

            if (kafkaDTO.getOfficeDetails() != null) {

                for (OfficeDetailsDTO office : kafkaDTO.getOfficeDetails()) {

                    if (!process.getCurrentTask().equals(office.getTaskId())) {
                        continue;
                    }

                    if (office.getAllowedOffices() == null) {
                        continue;
                    }

                    for (OfficeDetailsDTO.OfficeUnitData unit : office.getAllowedOffices()) {

                        if (unit.getHolderIds() == null || unit.getHolderIds().isEmpty()) {
                            continue;
                        }

                        for (String holderId : unit.getHolderIds()) {

                            UserInboxDTO.LocationDTO loc = new UserInboxDTO.LocationDTO();

                            loc.setLocationId(unit.getOrgUnitCode());
                            loc.setLocationName(unit.getOrgUnitName());
                            loc.setUserToken(holderId);

                            locations.add(loc);
                        }
                    }
                }
            }
            String urlKey = extractActionTinyUrlKey(kafkaDTO.getActionTinyUrl());

			Optional<ApplicationActionTinyUrl> existingActionTinyUrl = applicationActionTinyUrlRepository
					.findByUrlKeyAndTenantId(urlKey, process.getTenantId());

			if (existingActionTinyUrl.isEmpty()) {

				ApplicationActionTinyUrl actionTinyUrl = new ApplicationActionTinyUrl();

				actionTinyUrl.setApplicationId(process.getApplicationId());
				
				actionTinyUrl.setServiceId(process.getServiceId());
				actionTinyUrl.setApplicationRefNo(kafkaDTO.getApplicationRefNo());

				actionTinyUrl.setTinyUrl(kafkaDTO.getActionTinyUrl());

				actionTinyUrl.setUrlKey(urlKey);

				actionTinyUrl.setTenantId(process.getTenantId());

				actionTinyUrl.setCrDate(OffsetDateTime.now());

				applicationActionTinyUrlRepository.save(actionTinyUrl);
			}
            task.setLocations(locations);
            dto.setTask(task);

            result.add(dto);

            log.debug("Mapped taskId {} with {} location-user mappings", task.getTaskId(), locations.size());
        }

        log.info("Total mapped inbox DTOs: {}", result.size());

        return result;
    }



    private static UserInboxDTO.TaskDTO getTaskDTO(UserInboxKafkaDTO kafkaDTO, ApplicationCurrentProcess process) {
        UserInboxDTO.TaskDTO task = new UserInboxDTO.TaskDTO();

        task.setFormId(process.getFormId());
        task.setTaskId(process.getCurrentTask());
        task.setTaskName(process.getCurrentTaskName());
        task.setCurrentProcessId(process.getProcessId());
        List<UserInboxDTO.LocationDTO> locations = new ArrayList<>();

        for (OfficeDetailsDTO office : kafkaDTO.getOfficeDetails()) {

            if (!process.getCurrentTask().equals(office.getTaskId())) {
                continue;
            }

            if (office.getAllowedOffices() == null) {
                continue;
            }

            for (OfficeDetailsDTO.OfficeUnitData unit : office.getAllowedOffices()) {

                UserInboxDTO.LocationDTO loc = getLocationDTO(process, unit);
                locations.add(loc);
            }
        }

        task.setLocations(locations);
        return task;
    }

    private static UserInboxDTO.LocationDTO getLocationDTO(ApplicationCurrentProcess process, OfficeDetailsDTO.OfficeUnitData unit) {
        UserInboxDTO.LocationDTO loc = new UserInboxDTO.LocationDTO();

        loc.setLocationId(unit.getOrgUnitCode());
        loc.setLocationName(unit.getOrgUnitName());

        loc.setUserToken(buildUserToken(process.getServiceId(), process.getCurrentTask(), unit.getOrgUnitCode()));
        return loc;
    }

    public void persistInboxEntries(UserInboxDTO dto) {

        UserInboxDTO.TaskDTO task = dto.getTask();

        List<UserInbox> inboxList = new ArrayList<>();

        for (UserInboxDTO.LocationDTO loc : task.getLocations()) {

            UserInbox inbox = new UserInbox();

            inbox.setApplId(dto.getApplId());
            inbox.setApplRefNo(dto.getApplRefNo());
            inbox.setServiceId(dto.getServiceId());
            inbox.setBaseServiceId(dto.getBaseServiceId());
            inbox.setServiceName(dto.getServiceName());
            inbox.setFormId(task.getFormId());
            inbox.setTaskId(task.getTaskId());
            inbox.setTaskName(task.getTaskName());
            inbox.setCurrentProcessId(task.getCurrentProcessId());
            inbox.setIsPriority(task.getIsPriority() != null ? task.getIsPriority() : false);
            inbox.setLocationId(loc.getLocationId());
            inbox.setUserToken(loc.getUserToken());
            inbox.setApplRecievedOn(Timestamp.valueOf(dto.getApplRecievedOn()));
            inbox.setTenantId(dto.getTenantId());

            inboxList.add(inbox);
        }

        if (!inboxList.isEmpty()) {
            log.info("Persisting {} inbox entries for applicationId: {}, taskId: {}", inboxList.size(), dto.getApplId(),
                    task.getTaskId());

            inboxList = userInboxRepository.saveAll(inboxList);
        }
        else {
            log.warn("No inbox entries found to persist for applicationId: {}, taskId: {}", dto.getApplId(),
                    task.getTaskId());
        }


}

    private void processApplicationTracking(UserInboxKafkaDTO kafkaDTO) {

        if (isNull(kafkaDTO) || kafkaDTO.getProcessList() == null || kafkaDTO.getProcessList().isEmpty()) {
            return;
        }
        
        
        List<ApplicationTracking> trackingList = new ArrayList<>();
        List<ApplicationTrackingDocument> trackingDocumentList = new ArrayList<>();
        List<ApplicationDocumentTinyUrl> tinyUrlList=new ArrayList<>();
        List<ApplicationTrackingHolder> holderList = new ArrayList<>();

        for (ApplicationCurrentProcess process : kafkaDTO.getProcessList()) {

            if (process.getGateway()) {
                continue;
            }

            Optional<ApplicationTracking> existingTracking =
                    applicationTrackingRepository.findByProcessIdAndTenantId(
                            process.getProcessId(),
                            process.getTenantId());

            ApplicationTracking tracking =
                    existingTracking.orElse(new ApplicationTracking());

            boolean isNew = existingTracking.isEmpty();

            tracking.setProcessId(process.getProcessId());
            tracking.setApplId(process.getApplicationId());
            tracking.setApplRefNo(kafkaDTO.getApplicationRefNo());

            tracking.setServiceId(process.getServiceId());
            tracking.setBaseServiceId(process.getBaseServiceId());
            tracking.setServiceName(kafkaDTO.getServiceName());

            tracking.setTaskId(process.getCurrentTask());
            tracking.setTaskName(process.getCurrentTaskName());

            tracking.setPreviousTaskId(process.getPreviousTask());
            tracking.setPreviousTaskName(process.getPreviousTaskName());

            tracking.setActionCode(process.getActionCode());
            tracking.setActionTaken(process.getActionTaken());
            if (ACTION_CALLBACK.equals(process.getActionCode())) {

                tracking.setActionName("Callbacked");

            } else {

                tracking.setActionName(process.getActionName());
            }
            tracking.setActionName(process.getActionName());
            
            tracking.setActionRemarks(process.getActionRemarks());

            tracking.setBeneficiaryUserName(kafkaDTO.getBeneficiaryName());

            tracking.setApplyDate(kafkaDTO.getApplyDate());
            tracking.setActionOn(process.getActionOn());

            tracking.setTenantId(process.getTenantId());

            if (process.getApplicantTask()) {
                tracking.setAppliedUserId(kafkaDTO.getAppliedBy());

            } else {
                tracking.setAppliedUserId(null);
            }
            if (process.getFormId() != null) {
                tracking.setFormId(process.getFormId());
            }
            tracking.setDataId(process.getDataId());

            trackingList.add(tracking);

            /*
             * Save tracking documents.
             */
            if ("Y".equals(process.getActionTaken()) && process.getDocuments() != null && !process.getDocuments().isEmpty()) {

                for (TrackingDocument document : process.getDocuments()) {

                    ApplicationTrackingDocument trackingDocument = new ApplicationTrackingDocument();

                    trackingDocument.setDocumentId(document.getUploadId());
                    trackingDocument.setDocumentName(document.getDocumentName());
                    trackingDocument.setDocumentType(document.getSourceType());
                    trackingDocument.setTenantId(process.getTenantId());
                    trackingDocument.setViewPermission(document.getViewPermission());

                    trackingDocument.setTracking(tracking);

                    trackingDocumentList.add(trackingDocument);
                    
                    ApplicationDocumentTinyUrl documentTinyUrl = new ApplicationDocumentTinyUrl();

					documentTinyUrl.setApplicationId(process.getApplicationId());
					documentTinyUrl.setReferenceNo(kafkaDTO.getApplicationRefNo());
					documentTinyUrl.setProcessId(process.getProcessId());
					documentTinyUrl.setDocumentId(document.getUploadId());
					documentTinyUrl.setTinyUrl(document.getDocumentTinyUrl());
					documentTinyUrl.setUrlKey(extractDocumentTinyUrlKey(document.getDocumentTinyUrl()));
					documentTinyUrl.setTenantId(process.getTenantId());
					documentTinyUrl.setAccessFlag(false);
					documentTinyUrl.setCrDate(OffsetDateTime.now());

                    tinyUrlList.add(documentTinyUrl);
                }
            }

            /*
             * Save holder mapping only for newly created workflow tasks.
             */
            if (isNew && kafkaDTO.getOfficeDetails() != null) {

                for (OfficeDetailsDTO office : kafkaDTO.getOfficeDetails()) {

                    if (!process.getCurrentTask().equals(office.getTaskId())) {
                        continue;
                    }

                    if (office.getAllowedOffices() == null) {
                        continue;
                    }

                    for (OfficeDetailsDTO.OfficeUnitData unit : office.getAllowedOffices()) {

                        if (unit.getHolderIds() == null || unit.getHolderIds().isEmpty()) {
                            continue;
                        }

                        for (String holderId : unit.getHolderIds()) {

                            ApplicationTrackingHolder holder =
                                    new ApplicationTrackingHolder();

                            holder.setProcessId(process.getProcessId());
                            holder.setHolderId(holderId);
                            holder.setLocationId(unit.getOrgUnitCode());
                            holder.setLocationName(unit.getOrgUnitName());
                            holder.setTenantId(process.getTenantId());

                            holderList.add(holder);
                        }
                    }
                }
            }
        }

        if (!trackingList.isEmpty()) {

            log.info("Saving {} application tracking records", trackingList.size());

            applicationTrackingRepository.saveAll(trackingList);
        }

        if (!trackingDocumentList.isEmpty()) {

            log.info("Saving {} application tracking document records", trackingDocumentList.size());

            applicationTrackingDocumentRepository.saveAll(trackingDocumentList);
        }
        if (!tinyUrlList.isEmpty()) {
        	
        	log.info("Saving {} application document tiny url records", tinyUrlList.size());
        	
        	applicationDocumentTinyUrlRepository.saveAll(tinyUrlList);
        }

        if (!holderList.isEmpty()) {

            log.info("Saving {} application tracking holder mappings", holderList.size());

            trackingHolderRepository.saveAll(holderList);
        }
    }

    private void processApplicantInbox(UserInboxKafkaDTO kafkaDTO) {

        log.info("Checking applicant inbox");

        if (kafkaDTO == null) {
            return;
        }

        if (kafkaDTO.getProcessList() == null || kafkaDTO.getProcessList().isEmpty()) {

            log.warn("Applicant inbox processing received without process list. applicationRefNo={}", kafkaDTO.getApplicationRefNo());
            return;
        }

        deleteCompletedApplicantInboxEntries(kafkaDTO);

        if (kafkaDTO.getApplicantTaskDetails() == null || kafkaDTO.getApplicantTaskDetails().isEmpty()) {
            return;
        }

        List<ApplicantInbox> trackingList = new ArrayList<>();

        for (ApplicantTaskDetails applicantDetails : kafkaDTO.getApplicantTaskDetails()) {

            if (applicantDetails == null || applicantDetails.getCurrentProcessId() == null) {
                continue;
            }

            ApplicationCurrentProcess process =
                    kafkaDTO.getProcessList()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(p ->
                                    applicantDetails
                                            .getCurrentProcessId()
                                            .equals(p.getProcessId())
                            )
                            .findFirst()
                            .orElse(null);

            if (process == null) {

                log.warn("Process not found for applicant task. currentProcessId={}, applicationRefNo={}", applicantDetails.getCurrentProcessId(), kafkaDTO.getApplicationRefNo());
                continue;
            }

            if (!process.getApplicantTask()) {
                continue;
            }

            Optional<ApplicantInbox> existing = applicantInboxRepository.findByCurrentProcessIdAndTenantId(process.getProcessId(), process.getTenantId());

            ApplicantInbox tracking = existing.orElse(new ApplicantInbox());

            boolean isNew = existing.isEmpty();

            populateApplicantTaskTracking(tracking, kafkaDTO, process, applicantDetails);

            if (isNew) {
                tracking.setCreatedOn(LocalDateTime.now());
            }

            tracking.setModifiedOn(LocalDateTime.now());
            trackingList.add(tracking);
        }

        if (!trackingList.isEmpty()) {

            applicantInboxRepository.saveAll(trackingList);
            log.info("Saved {} applicant inbox records for applicationRefNo={}", trackingList.size(), kafkaDTO.getApplicationRefNo());
        }
    }

    private void deleteCompletedApplicantInboxEntries(UserInboxKafkaDTO kafkaDTO) {

        List<ApplicationCurrentProcess> completedApplicantProcesses =
                kafkaDTO.getProcessList()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(process ->
                                "Y".equals(process.getActionTaken())
                        )
                        .filter(process ->
                                process.getProcessId() != null
                        )
                        .toList();

        if (completedApplicantProcesses.isEmpty()) {
            return;
        }

        List<ApplicantInbox> recordsToDelete =
                completedApplicantProcesses
                        .stream()
                        .map(process ->
                                applicantInboxRepository
                                        .findByCurrentProcessIdAndTenantId(
                                                process.getProcessId(),
                                                process.getTenantId()
                                        )
                        )
                        .flatMap(Optional::stream)
                        .toList();

        if (!recordsToDelete.isEmpty()) {

            applicantInboxRepository.deleteAll(recordsToDelete);
            log.info("Deleted {} completed applicant inbox records for applicationRefNo={}", recordsToDelete.size(), kafkaDTO.getApplicationRefNo());
        }
    }

    private void populateApplicantTaskTracking(ApplicantInbox tracking, UserInboxKafkaDTO kafkaDTO, ApplicationCurrentProcess process, ApplicantTaskDetails applicantDetails) {


        tracking.setApplicationId(process.getApplicationId());
        tracking.setApplicationRefNo(kafkaDTO.getApplicationRefNo());
        tracking.setAppliedBy(kafkaDTO.getAppliedBy());
        tracking.setBeneficiaryName(kafkaDTO.getBeneficiaryName());
        tracking.setApplyDate(kafkaDTO.getApplyDate());
        tracking.setServiceId(process.getServiceId());
        tracking.setBaseServiceId(process.getBaseServiceId());
        tracking.setServiceName(kafkaDTO.getServiceName());

        tracking.setTaskId(process.getCurrentTask());
        tracking.setTaskName(process.getCurrentTaskName());
        tracking.setCurrentProcessId(process.getProcessId());


        tracking.setFormId(process.getFormId());
        tracking.setTenantId(process.getTenantId());

        tracking.setSubmissionToSameOfficial(applicantDetails.isSubmissionToSameOfficial());


        tracking.setRequiresForm(applicantDetails.isRequiresForm());
        tracking.setRequiresPayment(applicantDetails.isRequiresPayment());

        if (tracking.getStatus() == null) {
            tracking.setStatus(process.getActionTaken());
        }
    }

}