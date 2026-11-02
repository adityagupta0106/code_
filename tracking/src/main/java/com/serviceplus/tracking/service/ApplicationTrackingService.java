package com.serviceplus.tracking.service;

import com.serviceplus.tracking.dto.*;
import com.serviceplus.tracking.entity.ApplicantInbox;
import com.serviceplus.tracking.entity.ApplicationTracking;
import com.serviceplus.tracking.entity.ApplicationTrackingHolder;
import com.serviceplus.tracking.entity.UserInbox;
import com.serviceplus.tracking.repository.*;
import com.serviceplus.tracking.repository.specification.ApplicantInboxSpecification;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.serviceplus.tracking.utility.ApplicationConstants.*;
import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;
import static com.serviceplus.tracking.utility.CommonUtil.isEmpty;
import static java.util.Objects.isNull;





@Service
public class ApplicationTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTrackingService.class);

    private final ApplicationTrackingRepository trackingRepository;

    private final WorkflowInboxRedisService workflowInboxRedisService;

    private final ApplicationTrackingRepositoryCustomImpl applicationTrackingRepositoryCustom;

    private final PublicTrackingVerificationService publicVerification;

    public ApplicationTrackingService(
            ApplicationTrackingRepository trackingRepository,
            WorkflowInboxRedisService workflowInboxRedisService,
            ApplicationTrackingRepositoryCustomImpl applicationTrackingRepositoryCustom,
            ApplicationTrackingHolderRepository applicationTrackingHolderRepository,
            ApplicationTrackingDocumentRepository applicationTrackingDocumentRepository,
            ApplicantInboxRepository applicantInboxRepository,
            PublicTrackingVerificationService publicVerification) {
        this.trackingRepository = trackingRepository;
        this.workflowInboxRedisService = workflowInboxRedisService;
        this.applicationTrackingRepositoryCustom = applicationTrackingRepositoryCustom;
        this.applicationTrackingHolderRepository = applicationTrackingHolderRepository;
        this.applicationTrackingDocumentRepository = applicationTrackingDocumentRepository;
        this.applicantInboxRepository = applicantInboxRepository;
        this.publicVerification = publicVerification;
    }

    public ApplicationTrackingResponse getTracking(ApplicationTrackingRequest requestDTO, HttpServletRequest request) {

        UserSessionDTO session = getUserSessionDetails(request);
        if (session == null || session.getUserID() == null || session.getUserID() == 0) {
            return commonTrack(requestDTO, request);
        }

        logger.info("Tracking request received. userId={}, applId={}, applRefNo={}", session.getUserID(), requestDTO.getApplId(), requestDTO.getApplRefNo());

        Set<Object> holderIds = workflowInboxRedisService.getTokensByUserAndLocation(
                session.getUserID(),
                String.valueOf(session.getLocationId()));

        boolean officialUser = holderIds != null && !holderIds.isEmpty();

        Set<String> officialProcessIds = Collections.emptySet();

        if (officialUser) {

            logger.info("Official flow detected. holderCount={}", holderIds.size());
            Set<String> holderIdStrings = holderIds.stream().map(String::valueOf).collect(Collectors.toSet());

            List<ApplicationTrackingHolder> holderMappings = applicationTrackingHolderRepository
                    .findByHolderIdInAndTenantId(
                            holderIdStrings,
                            session.getTenantId());

            officialProcessIds = holderMappings.stream().map(ApplicationTrackingHolder::getProcessId).collect(Collectors.toSet());

            logger.info("Official has access to {} process ids", officialProcessIds.size());

        } else {

            logger.info("Citizen flow detected. userId={}", session.getUserID());
        }

        List<ApplicationTracking> trackingList;

        if (!isEmpty(requestDTO.getApplId())) {

            trackingList = trackingRepository
                    .findByApplIdAndTenantIdOrderByProcessIdAsc(
                            requestDTO.getApplId(),
                            session.getTenantId());

        } else {

            trackingList = trackingRepository
                    .findByApplRefNoAndTenantIdOrderByProcessIdAsc(
                            requestDTO.getApplRefNo(),
                            session.getTenantId());
        }

        logger.info("Tracking records fetched count={}", trackingList.size());

        if (trackingList.isEmpty()) {
            return new ApplicationTrackingResponse();
        }

        return convertToDTO(trackingList, officialUser, session.getUserID(), officialProcessIds, holderIds);
    }

    private ApplicationTrackingResponse convertToDTO(List<ApplicationTracking> trackingList, boolean officialUser, Long sessionUserId, Set<String> officialProcessIds, Set<Object> holderIds) {

        ApplicationTracking first = trackingList.getFirst();

        first.setTaskName(APPLICATION_SUBMISSION_TASK_NAME);

        ApplicationTrackingResponse response = new ApplicationTrackingResponse();

        response.setApplId(first.getApplId());
        response.setApplRefNo(first.getApplRefNo());
        response.setServiceName(first.getServiceName());
        response.setBeneficiaryUserName(first.getBeneficiaryUserName());
        response.setApplyDate(first.getApplyDate());

        response.setTrackingDetails(
                buildTrackingDetails(
                        trackingList,
                        officialUser,
                        sessionUserId,
                        officialProcessIds,
                        holderIds));

        return response;
    }

    private List<ApplicationTrackingResponse.TrackingDetailDTO> buildTrackingDetails(List<ApplicationTracking> trackingList, boolean officialUser, Long sessionUserId, Set<String> officialProcessIds, Set<Object> holderIds) {

        List<ApplicationTrackingResponse.TrackingDetailDTO> result = new ArrayList<>();

        boolean applicantAccess = trackingList.stream()
                .anyMatch(tracking ->
                        tracking.getAppliedUserId() != null && tracking.getAppliedUserId().equals(sessionUserId)
                );

        for (ApplicationTracking tracking : trackingList) {

            ApplicationTrackingResponse.TrackingDetailDTO dto = new ApplicationTrackingResponse.TrackingDetailDTO();

            ApplicationTrackingResponse.TrackingPermissions permissions = new ApplicationTrackingResponse.TrackingPermissions();

            dto.setProcessId(tracking.getProcessId());
            dto.setTaskName(tracking.getTaskName());
            dto.setActionTaken(tracking.getActionTaken());
            dto.setActionOn(tracking.getActionOn());
            dto.setActionCode(tracking.getActionCode());
            dto.setActionName(tracking.getActionName());

            boolean officialAccess = false;

            if (officialUser) {

                officialAccess = officialProcessIds.contains(tracking.getProcessId());

                if (!officialAccess) {

                    Set<Object> serviceHolderIds = workflowInboxRedisService.getHolderIdsByService(tracking.getServiceId());

                    if (serviceHolderIds != null && !serviceHolderIds.isEmpty()) {

                        Set<String> serviceHolders = serviceHolderIds.stream().map(String::valueOf).collect(Collectors.toSet());

                        officialAccess = holderIds.stream().map(String::valueOf).anyMatch(serviceHolders::contains);
                    }
                }
            }

            if (applicantAccess || officialAccess) {

                if(officialAccess || tracking.getAppliedUserId() != null) {
                    dto.setFormId(tracking.getFormId());
                    dto.setDataId(tracking.getDataId());
                }
                
                dto.setActionRemarks(tracking.getActionRemarks());

                final boolean finalOfficialAccess = officialAccess;
                final boolean finalApplicantAccess = applicantAccess;

                List<ApplicationTrackingResponse.TrackingDetailDTO.DocumentDTO> documents =
                        applicationTrackingDocumentRepository
                                .findByTrackingProcessId(tracking.getProcessId())
                                .stream()
                                .filter(entity -> {

                                    List<String> viewPermission = entity.getViewPermission();

                                    if (viewPermission == null || viewPermission.isEmpty()) {
                                        return false;
                                    }

                                    if (finalApplicantAccess && viewPermission.contains("applicant")) {
                                        return true;
                                    }

                                    // Official must have access AND document
                                    // must allow official viewing.
                                    return finalOfficialAccess && viewPermission.contains("official");
                                })
                                .map(entity -> {

                                    ApplicationTrackingResponse.TrackingDetailDTO.DocumentDTO document =
                                            new ApplicationTrackingResponse.TrackingDetailDTO.DocumentDTO();

                                    document.setDocumentId(entity.getId());
                                    document.setDocumentName(entity.getDocumentName());

                                    return document;
                                })
                                .toList();

                dto.setDocuments(documents);
            }

            dto.setPermissions(permissions);
            result.add(dto);
        }

        return result;
    }

    public ApplicationTrackingResponse trackPublicApplication(ApplicationTrackingRequest requestDTO,
            HttpServletRequest request) {
        return publicVerification.trackPublicApplication(requestDTO, request);
    }

    public ServerSidePaginationRecord<Applications> trackList(ApplicationSearchRequest requestDTO, HttpServletRequest request) {
        return applicationTrackingRepositoryCustom.search(requestDTO, Objects.requireNonNull(getUserSessionDetails(request)));
    }

    public DocumentDownloadResponse getDocument(Long documentId, UserSessionDTO user) {
        return applicationTrackingRepositoryCustom.getDocument(documentId, user);
    }

    public ApplicationTrackingResponse commonTrack(ApplicationTrackingRequest requestDTO, HttpServletRequest request) {

        UserSessionDTO user = getUserSessionDetails(request);

        if (isNull(user) || isNull(user.getUserID()) || user.getUserID() == 0) {
            return trackPublicApplication(requestDTO, request);
        }

        return getTracking(requestDTO, request);
    }

    public ServerSidePaginationRecord<ApplicationApplicantTrackingResponse.ApplicantApplicationDTO> getApplicantApplications(ApplicationTrackingFilter filter, UserSessionDTO user) {

        int page = filter != null && filter.getPage() != null ? Math.max(filter.getPage(), 0) : 0;

        int size = filter != null && filter.getSize() != null ? Math.max(filter.getSize(), 1) : 25;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdOn")
        );

        String tenantId = user.getTenantId();
        Long userId = user.getUserID();


        Specification<ApplicantInbox> specification = ApplicantInboxSpecification.applicantApplications(userId, tenantId, filter);

        Page<ApplicantInbox> inboxPage = applicantInboxRepository.findAll(specification, pageable);

        List<ApplicationApplicantTrackingResponse.ApplicantApplicationDTO>
                applications =
                inboxPage.getContent()
                        .stream()
                        .map(this::buildApplicantApplication)
                        .toList();


        ServerSidePaginationRecord<ApplicationApplicantTrackingResponse.ApplicantApplicationDTO> pagination = new ServerSidePaginationRecord<>();


        pagination.setData(applications);
        pagination.setTotalRecords(inboxPage.getTotalElements());
        pagination.setCurrentPage(inboxPage.getNumber());
        pagination.setPageSize(inboxPage.getSize());
        pagination.setTotalPages(inboxPage.getTotalPages());


        return pagination;
    }

    private ApplicationApplicantTrackingResponse.ApplicantApplicationDTO buildApplicantApplication(ApplicantInbox inbox) {

        ApplicationApplicantTrackingResponse.ApplicantApplicationDTO dto = new ApplicationApplicantTrackingResponse.ApplicantApplicationDTO();


        dto.setApplId(inbox.getApplicationId());
        dto.setTaskName(inbox.getTaskName());
        dto.setPendingApplicantAction(Boolean.TRUE);
        dto.setInboxAction(buildInboxAction(inbox));
        dto.setApplRefNo(inbox.getApplicationRefNo());
        dto.setServiceId(inbox.getServiceId());
        dto.setServiceName(inbox.getServiceName());
        dto.setBeneficiaryName(inbox.getBeneficiaryName());
        dto.setReceivedDate(inbox.getApplyDate());
        dto.setTaskId(inbox.getTaskId());
        dto.setFormId(inbox.getFormId());

        return dto;
    }


    private ApplicationApplicantTrackingResponse.InboxActionDTO buildInboxAction(ApplicantInbox inbox) {

        ApplicationApplicantTrackingResponse.InboxActionDTO dto = new ApplicationApplicantTrackingResponse.InboxActionDTO();

        dto.setRequiresPayment(inbox.isRequiresPayment());
        dto.setRequiresForm(inbox.isRequiresForm());


        return dto;
    }

//    private String resolveActionUrl(ApplicationTracking tracking, Long sessionUserId, boolean officialAccess, boolean applicantAccess,String tenantId) {
//
//        if (applicantAccess) {
//            Optional<ApplicantInbox> applicantInbox = applicantInboxRepository
//                                                                .findByApplicationIdAndCurrentProcessIdAndTenantId(tracking.getApplId(), tracking.getProcessId(),tenantId);
//
//            if (applicantInbox.isPresent()) {
//
//                return inboxActionUrlService.getOrCreateUrl(
//                        ActionUrlTargetType.APPLICANT_INBOX,
//                        String.valueOf(applicantInbox.get().getId()), tracking.getApplId(), applicantInbox.get().getTenantId()
//                );
//            }
//        }
//
//
//        if (officialAccess) {
//
//            Optional<UserInbox> userInbox = userInboxRepository
//                    .findActiveByApplicationIdAndCurrentProcessId(tracking.getApplId(), tracking.getProcessId());
//
//            if (userInbox.isPresent()) {
//                return inboxActionUrlService.getOrCreateUrl(
//                        ActionUrlTargetType.USER_INBOX,
//                        String.valueOf(userInbox.get().getId()), tracking.getApplId(), userInbox.get().getTenantId());
//            }
//        }
//
//        return null;
//    }




    // Public verification (OTP and FORM_ATTRIBUTES) is centralized in PublicTrackingVerificationService.
    // These delegates preserve existing callers outside PublicApiController.
    public List<Map<String, Object>> getConfiguredServices(HttpServletRequest request) {
        return publicVerification.getConfiguredServices(request);
    }

    public Map<String, Object> getPublicTrackingConfig(Long serviceId, HttpServletRequest request) {
        return publicVerification.getPublicTrackingConfig(serviceId, request);
    }

    public Map<String, Object> sendReferenceEmailOtp(PublicApplicationTrackingRequest dto,
            HttpServletRequest request) {
        return publicVerification.sendReferenceEmailOtp(dto, request);
    }

    public Map<String, Object> sendServiceTrackingOtp(Map<String, Object> body,
            HttpServletRequest request) {
        return publicVerification.sendServiceTrackingOtp(body, request);
    }

    public Map<String, Object> verifyServiceTrackingOtp(Map<String, Object> body,
            HttpServletRequest request) {
        return publicVerification.verifyServiceTrackingOtp(body, request);
    }

    public Map<String, Object> verifyServiceTrackingAttributes(Map<String, Object> body,
            HttpServletRequest request) {
        return publicVerification.verifyServiceTrackingAttributes(body, request);
    }

}
