package com.serviceplus.metadata.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.dto.RecordPendingApplicationRequestDTO;
import com.serviceplus.metadata.dto.SubmissionDecisionResponseDTO;
import com.serviceplus.metadata.dto.SubmissionValidationRequestDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.PendingAdoptionApplication;
import com.serviceplus.metadata.entity.SduAdoptionAuditLog;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceDeliveryUnits;
import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignment;
import com.serviceplus.metadata.entity.ServiceRolloutScope;
import com.serviceplus.metadata.entity.ServiceSubmissionLocationAdoption;
import com.serviceplus.metadata.enums.LocationAdoptionStatus;
import com.serviceplus.metadata.enums.LocationOperationalStatus;
import com.serviceplus.metadata.enums.LocationRolloutScopeStatus;
import com.serviceplus.metadata.enums.NonAdoptedSubmissionPolicy;
import com.serviceplus.metadata.enums.PendingApplicationStatus;
import com.serviceplus.metadata.enums.SlaStartPolicy;
import com.serviceplus.metadata.enums.SubmissionDecisionResult;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IPendingAdoptionApplicationRepository;
import com.serviceplus.metadata.repository.ISduAdoptionAuditLogRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.ServiceDeliveryUnitsRepository;
import com.serviceplus.metadata.repository.ServiceDesignatedOfficerAssignmentRepository;
import com.serviceplus.metadata.entity.ServiceAdoptionPolicy;
import com.serviceplus.metadata.repository.IServiceAdoptionPolicyRepository;
import com.serviceplus.metadata.repository.IServiceRolloutScopeRepository;
import com.serviceplus.metadata.repository.IServiceSubmissionLocationAdoptionRepository;

@Service
public class SubmissionControlService {

    private static final Logger LOG = LogManager.getLogger("SubmissionControlService");

    private final IServiceRolloutScopeRepository rolloutScopeRepo;
    private final IServiceSubmissionLocationAdoptionRepository adoptionRepo;
    private final ServiceDeliveryUnitsRepository sduRepo;
    private final IServiceDefinitionRepository serviceDefRepo;
    private final IPendingAdoptionApplicationRepository pendingRepo;
    private final ISduAdoptionAuditLogRepository auditLogRepo;
    private final ServiceDesignatedOfficerAssignmentRepository doAssignmentRepo;
    private final SduNotificationService notificationService;
    private final IServiceAdoptionPolicyRepository adoptionPolicyRepo;

    @Autowired
    public SubmissionControlService(
            IServiceRolloutScopeRepository rolloutScopeRepo,
            IServiceSubmissionLocationAdoptionRepository adoptionRepo,
            ServiceDeliveryUnitsRepository sduRepo,
            IServiceDefinitionRepository serviceDefRepo,
            IPendingAdoptionApplicationRepository pendingRepo,
            ISduAdoptionAuditLogRepository auditLogRepo,
            ServiceDesignatedOfficerAssignmentRepository doAssignmentRepo,
            SduNotificationService notificationService, IServiceAdoptionPolicyRepository adoptionPolicyRepo) {
        this.rolloutScopeRepo = rolloutScopeRepo;
        this.adoptionRepo = adoptionRepo;
        this.sduRepo = sduRepo;
        this.serviceDefRepo = serviceDefRepo;
        this.pendingRepo = pendingRepo;
        this.auditLogRepo = auditLogRepo;
        this.doAssignmentRepo = doAssignmentRepo;
        this.notificationService = notificationService;
		this.adoptionPolicyRepo = adoptionPolicyRepo;
    }

    public ResponseEntity<?> evaluateSubmissionDecision(SubmissionValidationRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            Integer serviceId = request.getServiceId();
            String tenantId = userSessionDetails.getTenantId();

            // Check if batch list of locations is provided
            List<Integer> locationIds = request.getSubmissionLocationIds();
            if (locationIds != null && !locationIds.isEmpty()) {
                SubmissionDecisionResponseDTO batchResponse = new SubmissionDecisionResponseDTO();
                batchResponse.setServiceId(serviceId);
                Map<Integer, SubmissionDecisionResponseDTO> decisionMap = new LinkedHashMap<>();
                boolean allAllowed = true;
                boolean anyPending = false;

                for (Integer locId : locationIds) {
                    SubmissionDecisionResponseDTO singleDecision = evaluateSingleLocation(
                            serviceId, locId, request.getSduLocationId(), tenantId);
                    decisionMap.put(locId, singleDecision);
                    if (!singleDecision.isAllowed()) {
                        allAllowed = false;
                    }
                    if (singleDecision.isPendingAdoption()) {
                        anyPending = true;
                    }
                }

                batchResponse.setLocationDecisions(decisionMap);
                batchResponse.setAllowed(allAllowed);
                batchResponse.setPendingAdoption(anyPending);
                batchResponse.setDecision(allAllowed ? (anyPending ? SubmissionDecisionResult.PENDING_ADOPTION.name() : SubmissionDecisionResult.NORMAL_SUBMISSION.name()) : SubmissionDecisionResult.BLOCKED.name());
                batchResponse.setMessage(allAllowed ? "All locations validated successfully." : "One or more locations are not allowed for submission.");
                return ResponseEntity.ok(batchResponse);
            }

            Integer subLocId = request.getSubmissionLocationId();
            if (subLocId == null) {
                throw new SPRuntimeError("submissionLocationId or submissionLocationIds is required", HttpStatus.BAD_REQUEST);
            }

            SubmissionDecisionResponseDTO response = evaluateSingleLocation(
                    serviceId, subLocId, request.getSduLocationId(), tenantId);
            return ResponseEntity.ok(response);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error evaluating submission decision", ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SubmissionDecisionResponseDTO evaluateSingleLocation(Integer serviceId, Integer subLocId,
                                                                 Integer sduLocationIdParam, String tenantId) {
    	String locationName="";
        SubmissionDecisionResponseDTO response = new SubmissionDecisionResponseDTO();
        response.setServiceId(serviceId);
        response.setSubmissionLocationId(subLocId);

        // Resolve Non-adopted Submission Policy from service_adoption_policy table
        NonAdoptedSubmissionPolicy policy = NonAdoptedSubmissionPolicy.BLOCK_NON_ADOPTED_SUBMISSION;
        Optional<ServiceAdoptionPolicy> policyOpt = adoptionPolicyRepo
                .findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId);
        if (policyOpt.isPresent() && policyOpt.get().getNonAdoptedSubmissionPolicy() != null) {
            try {
                policy = NonAdoptedSubmissionPolicy.fromValue(policyOpt.get().getNonAdoptedSubmissionPolicy());
            } catch (Exception ignored) {
            }
        }
        response.setNonAdoptedSubmissionPolicy(policy.name());

        // 1. Validate Rollout Scope
        Optional<ServiceRolloutScope> scopeOpt = rolloutScopeRepo
                .findByTenantIdAndServiceIdAndLocationIdAndDeletedFlagFalse(tenantId, serviceId, subLocId);

        String scopeStatus = LocationRolloutScopeStatus.IN_SCOPE.name();
        if (scopeOpt.isPresent()) {
            scopeStatus = scopeOpt.get().getScopeStatus();
            locationName=scopeOpt.get().getLocationName();
        }
        response.setRolloutScopeStatus(scopeStatus);

        if (LocationRolloutScopeStatus.OUT_OF_SCOPE.name().equalsIgnoreCase(scopeStatus)) {
            response.setDecision(SubmissionDecisionResult.BLOCKED.name());
            response.setAllowed(false);
            response.setPendingAdoption(false);
            response.setReason("Application submission for ("+locationName+ ") has not been allowed yet. Please get in touch with the Department for more details.");
            response.setMessage("Submission location ("+locationName+ ") is not permitted: Out of Scope.");
            return response;
        }

        // 2. Resolve SDU and Submission Location adoption
        Integer sduLocationId = sduLocationIdParam;
        if (sduLocationId == null && scopeOpt.isPresent()) {
            sduLocationId = scopeOpt.get().getParentLocationId();
        }

        Optional<ServiceSubmissionLocationAdoption> adoptionOpt;
        if (sduLocationId != null) {
            adoptionOpt = adoptionRepo.findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
                    tenantId, serviceId, sduLocationId, subLocId);
        } else {
            adoptionOpt = adoptionRepo.findByTenantIdAndServiceIdAndSubmissionLocationIdAndDeletedFlagFalse(
                    tenantId, serviceId, subLocId);
            if (adoptionOpt.isPresent()) {
                sduLocationId = adoptionOpt.get().getSduLocationId();
            }
        }
        response.setSduLocationId(sduLocationId);
        final Integer resolvedSduLocId = sduLocationId;
        if (adoptionOpt.isPresent() && adoptionOpt.get().getSubmissionLocationName() != null) {
            locationName = adoptionOpt.get().getSubmissionLocationName();
        }
        response.setSubmissionLocationName(locationName);
        String adoptionStatus = adoptionOpt.map(ServiceSubmissionLocationAdoption::getAdoptionStatus)
                .orElse(LocationAdoptionStatus.NOT_ADOPTED.name());
        response.setAdoptionStatus(adoptionStatus);

        // 3. Resolve parent SDU operational status
        boolean sduOperational = false;
        if (resolvedSduLocId != null) {
            Optional<ServiceDeliveryUnits> sduOpt = sduRepo
                    .findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId)
                    .stream().filter(s -> Objects.equals(s.getLocationId(), resolvedSduLocId)).findFirst();
            if (sduOpt.isPresent() && sduOpt.get().getActivationStatus() != null && sduOpt.get().getActivationStatus() == 1) {
                sduOperational = true;
            }
        }
        response.setSduOperationalStatus(sduOperational ? LocationOperationalStatus.ACTIVE.name() : LocationOperationalStatus.INACTIVE.name());

        // 4. Decision Rules Implementation
        if (LocationAdoptionStatus.SUSPENDED.name().equalsIgnoreCase(adoptionStatus)) {
            response.setDecision(SubmissionDecisionResult.BLOCKED.name());
            response.setAllowed(false);
            response.setPendingAdoption(false);
            response.setReason("Application submission for ("+locationName+ ") has not been allowed yet. Please get in touch with the Department for more details.");
            response.setMessage("Submission blocked: Location suspended.");
            return response;
        }

        if (LocationAdoptionStatus.ADOPTED.name().equalsIgnoreCase(adoptionStatus)) {
            if (sduOperational) {
                response.setDecision(SubmissionDecisionResult.NORMAL_SUBMISSION.name());
                response.setAllowed(true);
                response.setPendingAdoption(false);
                response.setReason("Location is in scope, adopted by Designated Officer, and SDU is operational.");
                response.setMessage("Application eligible for normal submission and processing.");
                return response;
            } else {
                if (policy.isAllow()) {
                    response.setDecision(SubmissionDecisionResult.PENDING_ADOPTION.name());
                    response.setAllowed(true);
                    response.setPendingAdoption(true);
                    response.setSlaStartPolicy(SlaStartPolicy.ON_ADOPTION.name());
                    response.setReason("Parent SDU is not yet active. Application accepted as Pending Adoption.");
                    response.setMessage("Application accepted as Pending Adoption.");
                } else {
                    response.setDecision(SubmissionDecisionResult.BLOCKED.name());
                    response.setAllowed(false);
                    response.setPendingAdoption(false);
                    response.setReason("Application submission for ("+locationName+ ") has not been allowed yet. Please get in touch with the Department for more details.");
                    response.setMessage("Submission blocked.");
                }
                return response;
            }
        }

        // Location is IN_SCOPE but NOT_ADOPTED
        if (policy.isAllow()) {
            response.setDecision(SubmissionDecisionResult.PENDING_ADOPTION.name());
            response.setAllowed(true);
            response.setPendingAdoption(true);
            response.setSlaStartPolicy(SlaStartPolicy.ON_ADOPTION.name());
            response.setReason("Location is in rollout scope but has not yet been adopted by Designated Officer. Service policy allows submission as Pending Adoption.");
            response.setMessage("Application accepted as Pending Adoption.");
        } else {
            response.setDecision(SubmissionDecisionResult.BLOCKED.name());
            response.setAllowed(false);
            response.setPendingAdoption(false);
            response.setReason("Application submission for ("+locationName+ ") has not been allowed yet. Please get in touch with the Department for more details.");
            response.setMessage("Submission not currently available at selected location.");
        }

        return response;
    }

    @Transactional
    public ResponseEntity<?> recordPendingAdoptionApplication(RecordPendingApplicationRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            String tenantId = userSessionDetails.getTenantId();
            Integer serviceId = request.getServiceId();
            Integer subLocId = request.getSubmissionLocationId();
            String applicationId = request.getApplicationId();

            Optional<PendingAdoptionApplication> existingOpt = pendingRepo
                    .findByApplicationIdAndTenantIdAndDeletedFlagFalse(applicationId, tenantId);
            if (existingOpt.isPresent()) {
                PendingAdoptionApplication existing = existingOpt.get();
                return ResponseEntity.ok(existing);
            }

            // Check if this is the first pending application for this location
            long existingPendingCount = pendingRepo.countByTenantIdAndServiceIdAndSubmissionLocationIdAndStatusAndDeletedFlagFalse(
                    tenantId, serviceId, subLocId, PendingApplicationStatus.PENDING_ADOPTION.name());

            boolean isFirst = (existingPendingCount == 0);

            Integer sduLocationId = request.getSduLocationId();
            if (sduLocationId == null) {
                Optional<ServiceSubmissionLocationAdoption> adoptOpt = adoptionRepo
                        .findByTenantIdAndServiceIdAndSubmissionLocationIdAndDeletedFlagFalse(tenantId, serviceId, subLocId);
                if (adoptOpt.isPresent()) {
                    sduLocationId = adoptOpt.get().getSduLocationId();
                } else {
                    Optional<ServiceRolloutScope> scopeOpt = rolloutScopeRepo
                            .findByTenantIdAndServiceIdAndLocationIdAndDeletedFlagFalse(tenantId, serviceId, subLocId);
                    if (scopeOpt.isPresent()) {
                        sduLocationId = scopeOpt.get().getParentLocationId();
                    }
                }
            }
            if (sduLocationId == null) {
                sduLocationId = 0;
            }

            Date now = new Date();
            PendingAdoptionApplication pendingApp = new PendingAdoptionApplication();
            pendingApp.setApplicationId(applicationId);
            pendingApp.setTenantId(tenantId);
            pendingApp.setServiceId(serviceId);
            pendingApp.setBaseServiceId(serviceId / 10000);
            pendingApp.setSduLocationId(sduLocationId);
            pendingApp.setSubmissionLocationId(subLocId);
            pendingApp.setStatus(PendingApplicationStatus.PENDING_ADOPTION.name());
            pendingApp.setSlaStartPolicy(request.getSlaStartPolicy() != null ? request.getSlaStartPolicy() : SlaStartPolicy.ON_ADOPTION.name());
            pendingApp.setSubmissionDate(now);
            pendingApp.setCitizenUserId(request.getCitizenUserId());
            pendingApp.setAcknowledgementNumber(request.getAcknowledgementNumber() != null
                    ? request.getAcknowledgementNumber()
                    : "ACK-PENDING-" + applicationId);
            pendingApp.setFirstNotificationSent(isFirst);
            pendingApp.setReminderCount(0);
            pendingApp.setCreatedBy(userSessionDetails.getUserID().longValue());
            pendingApp.setCreatedOn(now);
            pendingApp.setDeletedFlag(false);

            pendingRepo.save(pendingApp);

            // Audit
            SduAdoptionAuditLog audit = new SduAdoptionAuditLog();
            audit.setTenantId(tenantId);
            audit.setServiceId(serviceId);
            audit.setEntityType("PENDING_APPLICATION");
            audit.setEntityId(applicationId);
            audit.setSduLocationId(sduLocationId);
            audit.setSubmissionLocationId(subLocId);
            audit.setAction("RECORD_PENDING_ADOPTION");
            audit.setOldState("SUBMITTED");
            audit.setNewState(PendingApplicationStatus.PENDING_ADOPTION.name());
            audit.setRemarks("Application recorded as Pending Adoption. Citizen ACK: " + pendingApp.getAcknowledgementNumber());
            audit.setPerformedBy(userSessionDetails.getUserID().longValue());
            audit.setPerformedOn(now);
            auditLogRepo.save(audit);

            // If first pending application, notify Designated Officer
            if (isFirst) {
                Long designatedOfficerId = null;
                List<ServiceDesignatedOfficerAssignment> doAssignments = doAssignmentRepo.findByTenantIdAndServiceId(tenantId, serviceId);
                if (doAssignments != null && !doAssignments.isEmpty()) {
                    designatedOfficerId = doAssignments.get(0).getUserId();
                }
                notificationService.sendFirstPendingApplicationNotification(
                        tenantId, serviceId, sduLocationId, subLocId, applicationId, designatedOfficerId);
            }

            return ResponseEntity.ok(pendingApp);
        } catch (Exception ex) {
            LOG.error("Error recording pending adoption application", ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
