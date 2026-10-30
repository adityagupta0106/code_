package com.serviceplus.metadata.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.aadhaarConfiguration.repository.AuaApiFieldMappingRepository;
import com.serviceplus.metadata.dto.AdoptSubmissionLocationsRequestDTO;
import com.serviceplus.metadata.dto.AdoptSubmissionLocationsResponseDTO;
import com.serviceplus.metadata.dto.LocationSuspensionRequestDTO;
import com.serviceplus.metadata.dto.PendingAdoptionSummaryDTO;
import com.serviceplus.metadata.dto.RolloutScopeUpsertDTO;
import com.serviceplus.metadata.dto.SduActivationRequestDTO;
import com.serviceplus.metadata.dto.SduActivationResponseDTO;
import com.serviceplus.metadata.dto.SduConfigurationStatusDTO;
import com.serviceplus.metadata.dto.SduHierarchyViewDTO;
import com.serviceplus.metadata.dto.SduHierarchyViewDTO.SduNodeDTO;
import com.serviceplus.metadata.dto.SduHierarchyViewDTO.SubmissionLocationNodeDTO;
import com.serviceplus.metadata.dto.ServiceAdoptionPolicyDTO;
import com.serviceplus.metadata.dto.ServiceListDetailsDTO;
import com.serviceplus.metadata.dto.UnitRequestDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.PendingAdoptionApplication;
import com.serviceplus.metadata.entity.SduAdoptionAuditLog;
import com.serviceplus.metadata.entity.ServiceAdoptionPolicy;
import com.serviceplus.metadata.entity.ServiceChargeDetail;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.entity.ServiceDeliveryUnits;
import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignment;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.entity.ServiceRolloutScope;
import com.serviceplus.metadata.entity.ServiceSubmissionLocationAdoption;
import com.serviceplus.metadata.enums.LocationAdoptionStatus;
import com.serviceplus.metadata.enums.LocationOperationalStatus;
import com.serviceplus.metadata.enums.LocationRolloutScopeStatus;
import com.serviceplus.metadata.enums.NonAdoptedSubmissionPolicy;
import com.serviceplus.metadata.enums.PendingApplicationStatus;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.LgdFeignClient;
import com.serviceplus.metadata.repository.IPendingAdoptionApplicationRepository;
import com.serviceplus.metadata.repository.ISduAdoptionAuditLogRepository;
import com.serviceplus.metadata.repository.IServiceAdoptionPolicyRepository;
import com.serviceplus.metadata.repository.IServiceChargeRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;
import com.serviceplus.metadata.repository.IServiceRolloutScopeRepository;
import com.serviceplus.metadata.repository.IServiceSubmissionLocationAdoptionRepository;
import com.serviceplus.metadata.repository.ServiceDeliveryUnitsRepository;
import com.serviceplus.metadata.repository.ServiceDesignatedOfficerAssignmentRepository;
import com.serviceplus.metadata.repository.WorkflowAssignmentRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

@Service
public class SduAdoptionService {

    private final AuaApiFieldMappingRepository auaApiFieldMappingRepository;

    private static final Logger LOG = LogManager.getLogger("SduAdoptionService");

    private final ServiceDeliveryUnitsRepository sduRepo;
    private final IServiceDeliveryUnitDefinitionRepository sduDefRepo;
    private final IServiceRolloutScopeRepository rolloutScopeRepo;
    private final IServiceSubmissionLocationAdoptionRepository adoptionRepo;
    private final IPendingAdoptionApplicationRepository pendingRepo;
    private final ISduAdoptionAuditLogRepository auditLogRepo;
    private final ServiceDesignatedOfficerAssignmentRepository doAssignmentRepo;
    private final IServiceDefinitionRepository serviceDefRepo;
    private final IServiceLogRepository serviceLogRepo;
    private final IServiceMetadataRepository metadataRepo;
    private final WorkflowAssignmentRepository workflowAssignmentRepo;
    private final IServiceChargeRepository serviceChargeRepo;
    private final LgdFeignClient lgdFeignClient;
    private final SduNotificationService notificationService;
    private final IServiceAdoptionPolicyRepository adoptionPolicyRepo;
    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final IServiceLogRepository serviceLogRepository;
    @Autowired
    public SduAdoptionService(
            ServiceDeliveryUnitsRepository sduRepo,
            IServiceDeliveryUnitDefinitionRepository sduDefRepo,
            IServiceRolloutScopeRepository rolloutScopeRepo,
            IServiceSubmissionLocationAdoptionRepository adoptionRepo,
            IPendingAdoptionApplicationRepository pendingRepo,
            ISduAdoptionAuditLogRepository auditLogRepo,
            ServiceDesignatedOfficerAssignmentRepository doAssignmentRepo,
            IServiceDefinitionRepository serviceDefRepo,
            IServiceLogRepository serviceLogRepo,
            IServiceMetadataRepository metadataRepo,
            WorkflowAssignmentRepository workflowAssignmentRepo,
            IServiceChargeRepository serviceChargeRepo,
            @Autowired(required = false) LgdFeignClient lgdFeignClient,
            SduNotificationService notificationService, AuaApiFieldMappingRepository auaApiFieldMappingRepository,
            IServiceAdoptionPolicyRepository adoptionPolicyRepo, IServiceLogRepository serviceLogRepository, IServiceDefinitionRepository serviceDefinitionRepository) {
        this.sduRepo = sduRepo;
        this.sduDefRepo = sduDefRepo;
        this.rolloutScopeRepo = rolloutScopeRepo;
        this.adoptionRepo = adoptionRepo;
        this.pendingRepo = pendingRepo;
        this.auditLogRepo = auditLogRepo;
        this.doAssignmentRepo = doAssignmentRepo;
        this.serviceDefRepo = serviceDefRepo;
        this.serviceLogRepo = serviceLogRepo;
        this.metadataRepo = metadataRepo;
        this.workflowAssignmentRepo = workflowAssignmentRepo;
        this.serviceChargeRepo = serviceChargeRepo;
        this.lgdFeignClient = lgdFeignClient;
        this.notificationService = notificationService;
        this.auaApiFieldMappingRepository = auaApiFieldMappingRepository;
        this.adoptionPolicyRepo = adoptionPolicyRepo;
		this.serviceDefinitionRepository = serviceDefinitionRepository;
		this.serviceLogRepository = serviceLogRepository;
    }

    public ResponseEntity<?> getHierarchyView(Integer serviceId, Integer sduLocationId, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(serviceId, userSessionDetails);

            SduHierarchyViewDTO response = new SduHierarchyViewDTO();
            response.setServiceId(serviceId);

            List<ServiceDefinition> serviceDefs = serviceDefRepo.findByServiceId(serviceId);
            ServiceDefinition serviceDefinition = (serviceDefs != null && !serviceDefs.isEmpty()) ? serviceDefs.get(0) : null;
            if (serviceDefinition != null) {
                response.setServiceName(serviceDefinition.getServiceName());
            }

            String tenantId = userSessionDetails.getTenantId();
            List<ServiceDeliveryUnits> sduList = sduRepo.findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId);

            if (sduLocationId != null && sduList != null) {
                sduList = sduList.stream()
                        .filter(sdu -> sdu != null && sduLocationId.equals(sdu.getLocationId()))
                        .toList();
            }

            List<ServiceRolloutScope> allScopes = rolloutScopeRepo.findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId);
            Map<Integer, ServiceRolloutScope> scopeByLocation = allScopes.stream()
                    .collect(Collectors.toMap(ServiceRolloutScope::getLocationId, s -> s, (s1, s2) -> s1));

            List<ServiceSubmissionLocationAdoption> allAdoptions = adoptionRepo.findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId);
            Map<String, ServiceSubmissionLocationAdoption> adoptionMap = allAdoptions.stream()
                    .collect(Collectors.toMap(a -> a.getSduLocationId() + "_" + a.getSubmissionLocationId(), a -> a, (a1, a2) -> a1));

            List<SduNodeDTO> sduNodes = new ArrayList<>();
            for (ServiceDeliveryUnits sdu : sduList) {
                SduNodeDTO node = new SduNodeDTO();
                node.setSduLocationId(sdu.getLocationId());
                node.setSduLocationName(sdu.getLocationName());
                node.setSduLevelId(sdu.getDeliveryUnitLevelId());

                ServiceRolloutScope sduScope = scopeByLocation.get(sdu.getLocationId());
                node.setRolloutScopeStatus(sduScope != null ? sduScope.getScopeStatus() : LocationRolloutScopeStatus.IN_SCOPE.name());

                node.setOperationalStatus(sdu.getActivationStatus() != null && sdu.getActivationStatus() == 1
                        ? LocationOperationalStatus.ACTIVE.name()
                        : (sdu.getActivationStatus() != null && sdu.getActivationStatus() == 2
                                ? LocationOperationalStatus.SUSPENDED.name()
                                : LocationOperationalStatus.INACTIVE.name()));

                Map<String, Boolean> checklist = new LinkedHashMap<>();
                List<String> missing = new ArrayList<>();
                boolean configComplete = isSduConfigurationComplete(serviceId, sdu.getLocationId(), tenantId, missing, checklist);
                node.setConfigurationComplete(configComplete);
                node.setMissingConfigurations(missing);

                long adoptedCount = adoptionRepo.countByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
                        tenantId, serviceId, sdu.getLocationId(), LocationAdoptionStatus.ADOPTED.name());
                node.setHasAdoptedChild(adoptedCount > 0);
                node.setActivatable(configComplete && adoptedCount > 0);

                // Resolve child locations under this SDU
                List<SubmissionLocationNodeDTO> childNodes = resolveChildSubmissionLocations(
                        serviceId, sdu.getLocationId(), sdu.getDeliveryUnitLevelId(), tenantId, scopeByLocation, adoptionMap);
                node.setSubmissionLocations(childNodes);

                sduNodes.add(node);
            }

            response.setSduList(sduNodes);
            return ResponseEntity.ok(response);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error retrieving hierarchy view for serviceId={}, tenantId={}", serviceId, userSessionDetails.getTenantId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getSduConfigurationStatus(Integer serviceId, Integer sduLocationId, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(serviceId, userSessionDetails);

            String tenantId = userSessionDetails.getTenantId();
            Optional<ServiceDeliveryUnits> sduOpt = sduRepo.findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId)
                    .stream()
                    .filter(s -> Objects.equals(s.getLocationId(), sduLocationId))
                    .findFirst();

            if (sduOpt.isEmpty()) {
                throw new SPRuntimeError("SDU location not found for service: " + sduLocationId, HttpStatus.BAD_REQUEST);
            }

            ServiceDeliveryUnits sdu = sduOpt.get();
            SduConfigurationStatusDTO statusDTO = new SduConfigurationStatusDTO();
            statusDTO.setServiceId(serviceId);
            statusDTO.setSduLocationId(sduLocationId);
            statusDTO.setSduLocationName(sdu.getLocationName());
            statusDTO.setSduLevelId(sdu.getDeliveryUnitLevelId());
            statusDTO.setCurrentActivationStatus(sdu.getActivationStatus());
            statusDTO.setOperationalStatus(sdu.getActivationStatus() != null && sdu.getActivationStatus() == 1
                    ? LocationOperationalStatus.ACTIVE.name()
                    : LocationOperationalStatus.INACTIVE.name());

            Map<String, Boolean> checklist = new LinkedHashMap<>();
            List<String> rejectionReasons = new ArrayList<>();
            boolean configComplete = isSduConfigurationComplete(serviceId, sduLocationId, tenantId, rejectionReasons, checklist);
            statusDTO.setSduConfigurationComplete(configComplete);
            statusDTO.setChecklist(checklist);

            long adoptedChildren = adoptionRepo.countByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
                    tenantId, serviceId, sduLocationId, LocationAdoptionStatus.ADOPTED.name());
            boolean hasAdoptedChild = adoptedChildren > 0;
            statusDTO.setChildLocationsAdopted(hasAdoptedChild);

            if (!hasAdoptedChild) {
                rejectionReasons.add("No child Submission Location has been adopted under SDU " + sduLocationId);
            }

            statusDTO.setSduActivatable(configComplete && hasAdoptedChild);
            statusDTO.setRejectionReasons(rejectionReasons);

            return ResponseEntity.ok(statusDTO);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error inspecting SDU configuration for serviceId={}, sduLocationId={}", serviceId, sduLocationId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isSduConfigurationComplete(Integer serviceId, Integer sduLocationId, String tenantId,
                                             List<String> missingComponents, Map<String, Boolean> checklist) {
        boolean complete = true;

        // 1. Service Definition exists and is valid
        List<ServiceDefinition> serviceDefs = serviceDefRepo.findByServiceId(serviceId);
        ServiceDefinition serviceDefinition = (serviceDefs != null && !serviceDefs.isEmpty()) ? serviceDefs.get(0) : null;
        boolean defValid = serviceDefinition != null && serviceDefinition.getDefinitionJson() != null;
        checklist.put("Service Definition Completed", defValid);
        if (!defValid) {
            missingComponents.add("Service Definition is missing or incomplete");
            complete = false;
        }

        // 2. Service Log status is at least frozen or defined with necessary tabs
        ServiceLog serviceLog = serviceLogRepo.findByServiceId(serviceId);
        boolean logValid = serviceLog != null && (serviceLog.getServiceStatus().intValue()==(ServiceStatus.FROZEN.getCode()) || serviceLog.getServiceStatus().intValue()==(ServiceStatus.ACTIVATED.getCode())) ;
        checklist.put("Service status is not valid", logValid);
        if (!logValid) {
            missingComponents.add("Service status is not valid");
            complete = false;
        }

        // 3. SDU Definition exists
        ServiceDeliveryUnitDefinition sduDef = sduDefRepo.findByServiceId(serviceId);
        boolean sduDefExists = sduDef != null && sduDef.getServiceDeliveryUnits() != null && !sduDef.getServiceDeliveryUnits().isEmpty();
        checklist.put("Service Delivery Unit Defined", sduDefExists);
        if (!sduDefExists) {
            missingComponents.add("Service Delivery Unit definition is missing");
            complete = false;
        }

        // 4. Designated Officer assigned for service
        List<ServiceDesignatedOfficerAssignment> doAssignments = doAssignmentRepo.findByTenantIdAndServiceId(tenantId, serviceId);
        boolean doAssigned = doAssignments != null && !doAssignments.isEmpty();
        checklist.put("Designated Officer Assigned", doAssigned);
        if (!doAssigned) {
            missingComponents.add("Designated Officer has not been assigned for the service");
            complete = false;
        }

        // 5. Workflow Task assignments exist
//        List<WorkflowAssignment> assignments = workflowAssignmentRepo.findAllByServiceIdAndUserIdIsNotNull(serviceId);
//        boolean workflowAssigned = assignments != null && !assignments.isEmpty();
//        checklist.put("Workflow Tasks Mapped", workflowAssigned);
//        if (!workflowAssigned) {
//            missingComponents.add("Workflow task user assignments are missing for the service");
//            complete = false;
//        }

        // 6. Payment configuration check (if charges are configured, gateway must be configured)
        List<ServiceChargeDetail> chargesList = serviceChargeRepo.findAllByServiceId(serviceId);
        boolean chargesExist = chargesList != null && !chargesList.isEmpty();
        if (chargesExist) {
            ServiceMetadata metadata = metadataRepo.findByServiceIdAndTenantId(serviceId, tenantId);
            boolean paymentConfigured = metadata != null && metadata.getMetadataJson() != null && metadata.getMetadataJson().getPaymentConfiguration() != null;
            checklist.put("Payment Gateway Configured", paymentConfigured);
            if (!paymentConfigured) {
                missingComponents.add("Payment configuration is required but missing");
                complete = false;
            }
        } else {
            checklist.put("Payment Gateway Configured (Not Required)", true);
        }

        return complete;
    }

    @Transactional
    public ResponseEntity<?> adoptSubmissionLocations(AdoptSubmissionLocationsRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            Integer serviceId = request.getServiceId();
            Integer sduLocationId = request.getSduLocationId();
            String tenantId = userSessionDetails.getTenantId();
            Long officerUserId = userSessionDetails.getUserID().longValue();

            // Validate Designated Officer authorization
            validateDesignatedOfficerAuthorization(serviceId, sduLocationId, userSessionDetails);

            // Verify parent SDU exists in service_delivery_units
            Optional<ServiceDeliveryUnits> parentSduOpt = sduRepo.findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
                    tenantId, serviceId, request.getSduLevelId(), sduLocationId);
            if (parentSduOpt.isEmpty()) {
                throw new SPRuntimeError("SDU location " + sduLocationId + " is not mapped to service " + serviceId, HttpStatus.BAD_REQUEST);
            }
            ServiceDeliveryUnits parentSdu = parentSduOpt.get();

            AdoptSubmissionLocationsResponseDTO response = new AdoptSubmissionLocationsResponseDTO();
            response.setServiceId(serviceId);
            response.setSduLocationId(sduLocationId);

            Date now = new Date();
            long releasedCountTotal = 0;

            for (Integer subLocId : request.getSubmissionLocationIds()) {
                // Rule 2: Location must belong to service rollout scope
                Optional<ServiceRolloutScope> scopeOpt = rolloutScopeRepo.findByTenantIdAndServiceIdAndLocationIdAndDeletedFlagFalse(
                        tenantId, serviceId, subLocId);
                if (scopeOpt.isPresent() && LocationRolloutScopeStatus.OUT_OF_SCOPE.name().equalsIgnoreCase(scopeOpt.get().getScopeStatus())) {
                    response.getFailedLocations().put(subLocId, "Location " + subLocId + " is OUT_OF_SCOPE and cannot be adopted");
                    continue;
                }

                // Check current adoption state
                Optional<ServiceSubmissionLocationAdoption> existingAdoptionOpt = adoptionRepo
                        .findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
                                tenantId, serviceId, sduLocationId, subLocId);

                ServiceSubmissionLocationAdoption adoption;
                String oldState = "NOT_ADOPTED";

                if (existingAdoptionOpt.isPresent()) {
                    adoption = existingAdoptionOpt.get();
                    oldState = adoption.getAdoptionStatus();
                    if (LocationAdoptionStatus.ADOPTED.name().equalsIgnoreCase(adoption.getAdoptionStatus())) {
                        // Already adopted - idempotent success
                        response.getSuccessfullyAdoptedLocationIds().add(subLocId);
                        continue;
                    }
                } else {
                    adoption = new ServiceSubmissionLocationAdoption();
                    adoption.setTenantId(tenantId);
                    adoption.setServiceId(serviceId);
                    adoption.setBaseServiceId(serviceId / 10000);
                    adoption.setSduLevelId(request.getSduLevelId());
                    adoption.setSduLocationId(sduLocationId);
                    adoption.setSubmissionLevelId(request.getSubmissionLevelId() != null ? request.getSubmissionLevelId() : request.getSduLevelId() + 1);
                    adoption.setSubmissionLocationId(subLocId);
                    adoption.setCreatedBy(officerUserId);
                    adoption.setCreatedOn(now);
                    adoption.setDeletedFlag(false);
                }

                // Set location name from scope if available
                if (scopeOpt.isPresent() && scopeOpt.get().getLocationName() != null) {
                    adoption.setSubmissionLocationName(scopeOpt.get().getLocationName());
                } else if (adoption.getSubmissionLocationName() == null) {
                	UnitRequestDTO unitRequest = new UnitRequestDTO();
                    unitRequest.setUnitCode(subLocId.longValue());

                    ResponseEntity<String> unitNameResponse = lgdFeignClient.getUnitName(unitRequest);

                    if (unitNameResponse.getStatusCode().is2xxSuccessful() && unitNameResponse.getBody() != null) {
                        adoption.setSubmissionLocationName(unitNameResponse.getBody().toString());
                    } else {
                        adoption.setSubmissionLocationName("Location-" + subLocId);
                    }
                }

                adoption.setAdoptionStatus(LocationAdoptionStatus.ADOPTED.name());
                adoption.setAdoptedBy(officerUserId);
                adoption.setAdoptedOn(now);
                adoption.setDesignatedOfficerUserId(officerUserId);
                adoption.setModifiedBy(officerUserId);
                adoption.setModifiedOn(now);

                adoptionRepo.save(adoption);
                response.getSuccessfullyAdoptedLocationIds().add(subLocId);

                // Audit logging
                createAuditLog(tenantId, serviceId, "SUBMISSION_LOCATION", subLocId.toString(),
                        sduLocationId, subLocId, "ADOPT", oldState, LocationAdoptionStatus.ADOPTED.name(),
                        "Adopted by Designated Officer " + officerUserId, officerUserId);

                // If parent SDU is already operational, release pending applications for this location
                if (parentSdu.getActivationStatus() != null && parentSdu.getActivationStatus() == 1) {
                    long released = releasePendingApplicationsForLocation(tenantId, serviceId, sduLocationId, subLocId, officerUserId);
                    releasedCountTotal += released;
                }
            }

            response.setReleasedPendingApplicationsCount(releasedCountTotal);
            response.setMessage("Adoption processed successfully. " + response.getSuccessfullyAdoptedLocationIds().size()
                    + " locations adopted, " + response.getFailedLocations().size() + " failed.");
            return ResponseEntity.ok(response);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error adopting submission locations for serviceId={}, sduLocationId={}",
                    request.getServiceId(), request.getSduLocationId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> activateSdu(SduActivationRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            Integer serviceId = request.getServiceId();
            Integer levelId = request.getDeliveryUnitLevelId();
            String tenantId = userSessionDetails.getTenantId();
            Long userId = userSessionDetails.getUserID().longValue();

            SduActivationResponseDTO response = new SduActivationResponseDTO();
            response.setServiceId(serviceId);

            Date now = new Date();
            boolean anyActivated = false;

            for (Integer sduLocationId : request.getLocationIds()) {
                List<String> rejectionReasons = new ArrayList<>();
                Map<String, Boolean> checklist = new LinkedHashMap<>();

                // Condition A: All required SDU-wise configuration is complete
                boolean configComplete = isSduConfigurationComplete(serviceId, sduLocationId, tenantId, rejectionReasons, checklist);

                // Condition B: At least one child Submission Location has been adopted
                long adoptedChildCount = adoptionRepo.countByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
                        tenantId, serviceId, sduLocationId, LocationAdoptionStatus.ADOPTED.name());
                boolean hasAdoptedChild = adoptedChildCount > 0;

                if (!hasAdoptedChild) {
                    rejectionReasons.add("Condition B failed: No child Submission Location has been adopted for SDU " + sduLocationId);
                }
                if (!configComplete) {
                    rejectionReasons.add("Condition A failed: Required SDU-wise configuration is incomplete for SDU " + sduLocationId);
                }

                if (!configComplete || !hasAdoptedChild) {
                    response.getRejectedSduLocations().put(sduLocationId, rejectionReasons);
                    continue;
                }

                // Both Condition A and B satisfied: Activate SDU
                Optional<ServiceDeliveryUnits> existingSduOpt = sduRepo
                        .findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
                                tenantId, serviceId, levelId, sduLocationId);

                ServiceDeliveryUnits sduEntity;
                String oldState = "INACTIVE";
                if (existingSduOpt.isPresent()) {
                    sduEntity = existingSduOpt.get();
                    oldState = sduEntity.getActivationStatus() != null && sduEntity.getActivationStatus() == 1 ? "ACTIVE" : "INACTIVE";
                } else {
                    sduEntity = new ServiceDeliveryUnits();
                    sduEntity.setTenantId(tenantId);
                    sduEntity.setServiceId(serviceId);
                    sduEntity.setBaseServiceId(serviceId / 10000);
                    sduEntity.setDeliveryUnitLevelId(levelId);
                    sduEntity.setLocationId(sduLocationId);
                    try {
                        UnitRequestDTO unitRequest = new UnitRequestDTO();
                        unitRequest.setUnitCode(sduLocationId.longValue());

                        ResponseEntity<String> responseUnitName = lgdFeignClient.getUnitName(unitRequest);

                        if (responseUnitName.getStatusCode().is2xxSuccessful() && responseUnitName.getBody() != null) {
                            sduEntity.setLocationName(responseUnitName.getBody().toString());
                        } else {
                            sduEntity.setLocationName("SDU-" + sduLocationId);
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to fetch SDU location name for sduLocationId: {}", sduLocationId, e);
                        sduEntity.setLocationName("SDU-" + sduLocationId);
                    }                    sduEntity.setCreatedBy(userId);
                    sduEntity.setCreatedOn(now);
                    sduEntity.setDeletedFlag(false);
                }

                sduEntity.setActivationStatus(1);
                sduEntity.setActivatedBy(userId);
                sduEntity.setActivatedOn(now);
                sduEntity.setModifiedBy(userId);
                sduEntity.setModifiedOn(now);
                sduEntity.setRemarks(request.getRemarks());
                sduRepo.save(sduEntity);

                response.getActivatedSduLocationIds().add(sduLocationId);
                anyActivated = true;

                createAuditLog(tenantId, serviceId, "SDU", sduLocationId.toString(),
                        sduLocationId, null, "ACTIVATE_SDU", oldState, "ACTIVE",
                        "SDU Activated by user " + userId + ". Remarks: " + request.getRemarks(), userId);

                // Release pending applications for all adopted child locations under this newly activated SDU
                List<ServiceSubmissionLocationAdoption> adoptedChildren = adoptionRepo
                        .findByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
                                tenantId, serviceId, sduLocationId, LocationAdoptionStatus.ADOPTED.name());
                for (ServiceSubmissionLocationAdoption child : adoptedChildren) {
                    releasePendingApplicationsForLocation(tenantId, serviceId, sduLocationId, child.getSubmissionLocationId(), userId);
                }
            }

            // Update overall service log status if active
            if (anyActivated) {
                ServiceLog log = serviceLogRepo.findByServiceId(serviceId);
                if (log != null) {
                    if (log.getServiceStatus() == ServiceStatus.FROZEN.getCode() || log.getServiceStatus() == ServiceStatus.DEFINED.getCode()) {
                        log.setServiceStatus(ServiceStatus.ACTIVATED.getCode());
                        log.setuDate(now);
                        serviceLogRepo.save(log);
                    }
                }
            }

            if (response.getActivatedSduLocationIds().isEmpty() && !response.getRejectedSduLocations().isEmpty()) {
                response.setMessage("SDU activation rejected. Prerequisite conditions not met.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.setMessage("SDU activation completed. Activated: " + response.getActivatedSduLocationIds().size()
                    + ", Rejected: " + response.getRejectedSduLocations().size());
            return ResponseEntity.ok(response);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error activating SDU for serviceId={}", request.getServiceId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> deactivateSdu(SduActivationRequestDTO requestDTO, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(requestDTO.getServiceId(), userSessionDetails);

            String tenantId = userSessionDetails.getTenantId();
            Long userId = userSessionDetails.getUserID().longValue();
            Date now = new Date();

            for (Integer locationId : requestDTO.getLocationIds()) {
                Optional<ServiceDeliveryUnits> sduOpt = sduRepo
                        .findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
                                tenantId, requestDTO.getServiceId(), requestDTO.getDeliveryUnitLevelId(), locationId);
                if (sduOpt.isPresent()) {
                    ServiceDeliveryUnits sdu = sduOpt.get();
                    sdu.setActivationStatus(0);
                    sdu.setDeactivatedBy(userId);
                    sdu.setDeactivatedOn(now);
                    sdu.setModifiedBy(userId);
                    sdu.setModifiedOn(now);
                    sduRepo.save(sdu);

                    createAuditLog(tenantId, requestDTO.getServiceId(), "SDU", locationId.toString(),
                            locationId, null, "DEACTIVATE_SDU", "ACTIVE", "INACTIVE",
                            "SDU deactivated by user " + userId, userId);
                }
            }

            boolean anyActive = sduRepo.existsByTenantIdAndServiceIdAndActivationStatusAndDeletedFlagFalse(tenantId, requestDTO.getServiceId(), 1);
            if (!anyActive) {
                ServiceLog log = serviceLogRepo.findByServiceId(requestDTO.getServiceId());
                if (log != null) {
                    if (log.getServiceStatus() == ServiceStatus.ACTIVATED.getCode()) {
                        log.setServiceStatus(ServiceStatus.FROZEN.getCode());
                        log.setuDate(now);
                        serviceLogRepo.save(log);
                    }
                }
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "SDU(s) deactivated successfully.");
            resp.put("serviceId", requestDTO.getServiceId());
            resp.put("deactivatedLocationIds", requestDTO.getLocationIds());
            return ResponseEntity.ok(resp);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error deactivating SDU for serviceId={}", requestDTO.getServiceId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> suspendSubmissionLocation(LocationSuspensionRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            String tenantId = userSessionDetails.getTenantId();
            Long userId = userSessionDetails.getUserID().longValue();
            Date now = new Date();

            Optional<ServiceSubmissionLocationAdoption> adoptionOpt = adoptionRepo
                    .findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
                            tenantId, request.getServiceId(), request.getSduLocationId(), request.getSubmissionLocationId());

            if (adoptionOpt.isEmpty() || !LocationAdoptionStatus.ADOPTED.name().equalsIgnoreCase(adoptionOpt.get().getAdoptionStatus())) {
                throw new SPRuntimeError("Submission location is not in ADOPTED state and cannot be suspended", HttpStatus.BAD_REQUEST);
            }

            ServiceSubmissionLocationAdoption adoption = adoptionOpt.get();
            String oldState = adoption.getAdoptionStatus();
            adoption.setAdoptionStatus(LocationAdoptionStatus.SUSPENDED.name());
            adoption.setSuspendedBy(userId);
            adoption.setSuspendedOn(now);
            adoption.setSuspensionRemarks(request.getReason());
            adoption.setModifiedBy(userId);
            adoption.setModifiedOn(now);
            adoptionRepo.save(adoption);

            createAuditLog(tenantId, request.getServiceId(), "SUBMISSION_LOCATION",
                    request.getSubmissionLocationId().toString(), request.getSduLocationId(),
                    request.getSubmissionLocationId(), "SUSPEND", oldState, LocationAdoptionStatus.SUSPENDED.name(),
                    "Suspended: " + request.getReason(), userId);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Submission location suspended successfully.");
            resp.put("serviceId", request.getServiceId());
            resp.put("submissionLocationId", request.getSubmissionLocationId());
            resp.put("status", LocationAdoptionStatus.SUSPENDED.name());
            return ResponseEntity.ok(resp);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error suspending submission location for serviceId={}, locationId={}",
                    request.getServiceId(), request.getSubmissionLocationId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getRelatedUnitsFromLgd(Integer serviceId, Integer sourceLevelCode, Long sourceUnitCode, Integer destLevelCode, UserSessionDTO userSessionDetails) {
        try {
            if (lgdFeignClient == null) {
                LOG.error("LgdFeignClient is not available");
                throw new SPRuntimeError("LGD Service integration unavailable", HttpStatus.SERVICE_UNAVAILABLE);
            }
            ResponseEntity<List<com.serviceplus.metadata.dto.LgdUnitDTO>> response;
            boolean sameLevel = sourceLevelCode != null && destLevelCode != null && sourceLevelCode.equals(destLevelCode);
            if (sameLevel) {
                response = lgdFeignClient.getUnitsByLevel(destLevelCode, userSessionDetails.getEntityId().longValue());
            } else {
                response = lgdFeignClient.getRelatedUnits(sourceLevelCode, sourceUnitCode, destLevelCode);
            }
            if (response != null && response.getBody() != null) {
                List<com.serviceplus.metadata.dto.LgdUnitDTO> units = response.getBody();
                if (sameLevel && sourceUnitCode != null) {
                    units = units.stream()
                            .filter(unit -> unit != null && sourceUnitCode.equals(unit.getChildEntityUnitCode()))
                            .toList();
                }

                // Check schm_sp.service_submission_location_adoption with serviceId and tenantId to send adoptionStatus against childEntityUnitCode
                if (serviceId != null && userSessionDetails.getTenantId() != null && !units.isEmpty()) {
                    List<Integer> childUnitCodes = units.stream()
                            .filter(Objects::nonNull)
                            .map(com.serviceplus.metadata.dto.LgdUnitDTO::getChildEntityUnitCode)
                            .filter(Objects::nonNull)
                            .map(Long::intValue)
                            .distinct()
                            .toList();

                    if (!childUnitCodes.isEmpty()) {
                        List<ServiceSubmissionLocationAdoption> adoptions = adoptionRepo.findByLocationIds(
                                userSessionDetails.getTenantId(), serviceId, childUnitCodes);

                        Map<Integer, String> adoptionStatusMap = new HashMap<>();
                        if (adoptions != null) {
                            for (ServiceSubmissionLocationAdoption adoption : adoptions) {
                                if (adoption != null && adoption.getSubmissionLocationId() != null) {
                                    adoptionStatusMap.put(adoption.getSubmissionLocationId(), adoption.getAdoptionStatus());
                                }
                            }
                        }

                        for (com.serviceplus.metadata.dto.LgdUnitDTO unit : units) {
                            if (unit != null && unit.getChildEntityUnitCode() != null) {
                                String status = adoptionStatusMap.get(unit.getChildEntityUnitCode().intValue());
                                unit.setAdoptionStatus(status != null ? status : LocationAdoptionStatus.NOT_ADOPTED.name());
                            }
                        }
                    }
                }

                return ResponseEntity.ok(units);
            }
            return ResponseEntity.ok(Collections.emptyList());
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error fetching related units from ServiceplusLgd for sourceLevelCode={}, sourceUnitCode={}, destLevelCode={}",
                    sourceLevelCode, sourceUnitCode, destLevelCode, ex);
            throw new SPRuntimeError("Internal Server Error while communicating with LGD", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> reactivateSubmissionLocation(LocationSuspensionRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            String tenantId = userSessionDetails.getTenantId();
            Long userId = userSessionDetails.getUserID().longValue();
            Date now = new Date();

            Optional<ServiceSubmissionLocationAdoption> adoptionOpt = adoptionRepo
                    .findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
                            tenantId, request.getServiceId(), request.getSduLocationId(), request.getSubmissionLocationId());

            if (adoptionOpt.isEmpty() || !LocationAdoptionStatus.SUSPENDED.name().equalsIgnoreCase(adoptionOpt.get().getAdoptionStatus())) {
                throw new SPRuntimeError("Submission location is not SUSPENDED and cannot be reactivated", HttpStatus.BAD_REQUEST);
            }

            ServiceSubmissionLocationAdoption adoption = adoptionOpt.get();
            String oldState = adoption.getAdoptionStatus();
            adoption.setAdoptionStatus(LocationAdoptionStatus.ADOPTED.name());
            adoption.setReactivatedBy(userId);
            adoption.setReactivatedOn(now);
            adoption.setModifiedBy(userId);
            adoption.setModifiedOn(now);
            adoptionRepo.save(adoption);

            createAuditLog(tenantId, request.getServiceId(), "SUBMISSION_LOCATION",
                    request.getSubmissionLocationId().toString(), request.getSduLocationId(),
                    request.getSubmissionLocationId(), "REACTIVATE", oldState, LocationAdoptionStatus.ADOPTED.name(),
                    "Reactivated by user " + userId, userId);

            // Release any pending applications if parent SDU is active
            Optional<ServiceDeliveryUnits> sduOpt = sduRepo.findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, request.getServiceId())
                    .stream().filter(s -> Objects.equals(s.getLocationId(), request.getSduLocationId())).findFirst();
            if (sduOpt.isPresent() && sduOpt.get().getActivationStatus() != null && sduOpt.get().getActivationStatus() == 1) {
                releasePendingApplicationsForLocation(tenantId, request.getServiceId(), request.getSduLocationId(), request.getSubmissionLocationId(), userId);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Submission location reactivated successfully.");
            resp.put("serviceId", request.getServiceId());
            resp.put("submissionLocationId", request.getSubmissionLocationId());
            resp.put("status", LocationAdoptionStatus.ADOPTED.name());
            return ResponseEntity.ok(resp);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error reactivating submission location for serviceId={}, locationId={}",
                    request.getServiceId(), request.getSubmissionLocationId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> unadoptSubmissionLocations(AdoptSubmissionLocationsRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            Integer serviceId = request.getServiceId();
            Integer sduLocationId = request.getSduLocationId();
            String tenantId = userSessionDetails.getTenantId();
            Long officerUserId = userSessionDetails.getUserID().longValue();

            Date now = new Date();
            List<Integer> successfullyUnadoptedLocationIds = new ArrayList<>();
            Map<Integer, String> failedLocations = new LinkedHashMap<>();

            for (Integer subLocId : request.getSubmissionLocationIds()) {
                Optional<ServiceSubmissionLocationAdoption> existingAdoptionOpt = adoptionRepo
                        .findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
                                tenantId, serviceId, sduLocationId, subLocId);

                if (existingAdoptionOpt.isEmpty()) {
                    // Create entry as NOT_ADOPTED so it is explicitly recorded
                    ServiceSubmissionLocationAdoption adoption = new ServiceSubmissionLocationAdoption();
                    adoption.setTenantId(tenantId);
                    adoption.setServiceId(serviceId);
                    adoption.setBaseServiceId(serviceId / 10000);
                    adoption.setSduLevelId(request.getSduLevelId());
                    adoption.setSduLocationId(sduLocationId);
                    adoption.setSubmissionLevelId(request.getSubmissionLevelId() != null ? request.getSubmissionLevelId() : request.getSduLevelId() + 1);
                    adoption.setSubmissionLocationId(subLocId);
                    if (adoption.getSubmissionLocationName() == null) {
                    	UnitRequestDTO unitRequest = new UnitRequestDTO();
                        unitRequest.setUnitCode(subLocId.longValue());

                        ResponseEntity<String> unitNameResponse = lgdFeignClient.getUnitName(unitRequest);

                        if (unitNameResponse.getStatusCode().is2xxSuccessful() && unitNameResponse.getBody() != null) {
                            adoption.setSubmissionLocationName(unitNameResponse.getBody().toString());
                        } else {
                            adoption.setSubmissionLocationName("Location-" + subLocId);
                        }
                    }
                    adoption.setAdoptionStatus(LocationAdoptionStatus.NOT_ADOPTED.name());
                    adoption.setDesignatedOfficerUserId(officerUserId);
                    adoption.setCreatedBy(officerUserId);
                    adoption.setCreatedOn(now);
                    adoption.setModifiedBy(officerUserId);
                    adoption.setModifiedOn(now);
                    adoption.setDeletedFlag(false);
                    adoptionRepo.save(adoption);

                    createAuditLog(tenantId, serviceId, "SUBMISSION_LOCATION", subLocId.toString(),
                            sduLocationId, subLocId, "UNADOPT", null, LocationAdoptionStatus.NOT_ADOPTED.name(),
                            "Set to NOT_ADOPTED by Designated Officer " + officerUserId, officerUserId);
                    successfullyUnadoptedLocationIds.add(subLocId);
                    continue;
                }

                ServiceSubmissionLocationAdoption adoption = existingAdoptionOpt.get();
                String oldState = adoption.getAdoptionStatus();

                if (LocationAdoptionStatus.NOT_ADOPTED.name().equalsIgnoreCase(oldState)) {
                    // Already not adopted - idempotent
                    successfullyUnadoptedLocationIds.add(subLocId);
                    continue;
                }

                adoption.setAdoptionStatus(LocationAdoptionStatus.NOT_ADOPTED.name());
                adoption.setModifiedBy(officerUserId);
                adoption.setModifiedOn(now);
                adoptionRepo.save(adoption);

                createAuditLog(tenantId, serviceId, "SUBMISSION_LOCATION", subLocId.toString(),
                        sduLocationId, subLocId, "UNADOPT", oldState, LocationAdoptionStatus.NOT_ADOPTED.name(),
                        "Unadopted (set to NOT_ADOPTED) by Designated Officer " + officerUserId, officerUserId);
                successfullyUnadoptedLocationIds.add(subLocId);
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("serviceId", serviceId);
            response.put("sduLocationId", sduLocationId);
            response.put("successfullyUnadoptedLocationIds", successfullyUnadoptedLocationIds);
            response.put("failedLocations", failedLocations);
            response.put("message", "Locations updated to NOT_ADOPTED successfully. "
                    + successfullyUnadoptedLocationIds.size() + " locations updated, " + failedLocations.size() + " failed.");

            return ResponseEntity.ok(response);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error setting submission locations to NOT_ADOPTED for serviceId={}, sduLocationId={}",
                    request.getServiceId(), request.getSduLocationId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> upsertRolloutScope(RolloutScopeUpsertDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            Integer serviceId = request.getServiceId();
            String tenantId = userSessionDetails.getTenantId();
            Long userId = userSessionDetails.getUserID().longValue();
            Date now = new Date();

            List<ServiceRolloutScope> savedScopes = new ArrayList<>();
            for (RolloutScopeUpsertDTO.RolloutLocationItem item : request.getLocations()) {
                Optional<ServiceRolloutScope> existingOpt = rolloutScopeRepo
                        .findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
                                tenantId, serviceId, request.getDeliveryUnitLevelId(), item.getLocationId());

                ServiceRolloutScope scope;
                String oldState = "OUT_OF_SCOPE";
                if (existingOpt.isPresent()) {
                    scope = existingOpt.get();
                    oldState = scope.getScopeStatus();
                } else {
                    scope = new ServiceRolloutScope();
                    scope.setTenantId(tenantId);
                    scope.setServiceId(serviceId);
                    scope.setBaseServiceId(serviceId / 10000);
                    scope.setDeliveryUnitLevelId(request.getDeliveryUnitLevelId());
                    scope.setLocationId(item.getLocationId());
                    scope.setCreatedBy(userId);
                    scope.setCreatedOn(now);
                    scope.setDeletedFlag(false);
                }

                if (item.getLocationName() != null) {
                    scope.setLocationName(item.getLocationName());
                } else {
                    try {
                        UnitRequestDTO unitRequest = new UnitRequestDTO();
                        unitRequest.setUnitCode(item.getLocationId().longValue());

                        ResponseEntity<String> response = lgdFeignClient.getUnitName(unitRequest);

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            scope.setLocationName(response.getBody().toString());
                        } else {
                            scope.setLocationName("Loc-" + item.getLocationId());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        scope.setLocationName("Loc-" + item.getLocationId());
                    }
                }
                scope.setParentLocationId(item.getParentLocationId());
                scope.setScopeStatus(LocationRolloutScopeStatus.fromString(item.getScopeStatus()).name());
                scope.setModifiedBy(userId);
                scope.setModifiedOn(now);
                savedScopes.add(rolloutScopeRepo.save(scope));

                createAuditLog(tenantId, serviceId, "ROLLOUT_SCOPE", item.getLocationId().toString(),
                        item.getParentLocationId(), item.getLocationId(), "SCOPE_CHANGE", oldState,
                        scope.getScopeStatus(), "Scope updated by user " + userId, userId);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Rollout scope updated successfully.");
            resp.put("serviceId", serviceId);
            resp.put("count", savedScopes.size());
            return ResponseEntity.ok(resp);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error updating rollout scope for serviceId={}", request.getServiceId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getRolloutScope(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(serviceId, userSessionDetails);

            List<ServiceRolloutScope> scopes = rolloutScopeRepo.findByTenantIdAndServiceIdAndDeletedFlagFalse(
                    userSessionDetails.getTenantId(), serviceId);
            return ResponseEntity.ok(scopes);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error fetching rollout scope for serviceId={}", serviceId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<?> saveServiceAdoptionPolicy(ServiceAdoptionPolicyDTO request, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(request.getServiceId(), userSessionDetails);

            Integer serviceId = request.getServiceId();
            String tenantId = userSessionDetails.getTenantId();
            Long userId = userSessionDetails.getUserID().longValue();
            Date now = new Date();

            NonAdoptedSubmissionPolicy policy = NonAdoptedSubmissionPolicy.fromValue(request.getNonAdoptedSubmissionPolicy());

            Optional<ServiceAdoptionPolicy> existingOpt = adoptionPolicyRepo
                    .findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId);

            ServiceAdoptionPolicy entity;
            String oldPolicy = NonAdoptedSubmissionPolicy.BLOCK_NON_ADOPTED_SUBMISSION.name();
            if (existingOpt.isPresent()) {
                entity = existingOpt.get();
                oldPolicy = entity.getNonAdoptedSubmissionPolicy();
            } else {
                entity = new ServiceAdoptionPolicy();
                entity.setTenantId(tenantId);
                entity.setServiceId(serviceId);
                entity.setBaseServiceId(serviceId / 10000);
                entity.setCreatedBy(userId);
                entity.setCreatedOn(now);
                entity.setDeletedFlag(false);
            }

            entity.setNonAdoptedSubmissionPolicy(policy.name());
            entity.setRemarks(request.getRemarks());
            entity.setModifiedBy(userId);
            entity.setModifiedOn(now);
            adoptionPolicyRepo.save(entity);

            createAuditLog(tenantId, serviceId, "SERVICE_ADOPTION_POLICY", serviceId.toString(),
                    null, null, "UPDATE_ADOPTION_POLICY", oldPolicy, policy.name(),
                    (request.getRemarks() != null && !request.getRemarks().isBlank()) ? request.getRemarks() : "Adoption policy updated to " + policy.name(),
                    userId);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("serviceId", serviceId);
            resp.put("nonAdoptedSubmissionPolicy", policy.name());
            resp.put("remarks", request.getRemarks());
            resp.put("message", "Service adoption policy configured successfully.");
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            throw new SPRuntimeError(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error saving service adoption policy for serviceId={}", request.getServiceId(), ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getServiceAdoptionPolicy(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(serviceId, userSessionDetails);

            String tenantId = userSessionDetails.getTenantId();
            Optional<ServiceAdoptionPolicy> policyOpt = adoptionPolicyRepo
                    .findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId);

            String serviceName = "";
            List<ServiceDefinition> serviceDefs = serviceDefRepo.findByServiceId(serviceId);
            if (serviceDefs != null && !serviceDefs.isEmpty()) {
                serviceName = serviceDefs.get(0).getServiceName();
            }

            String policy = policyOpt.map(ServiceAdoptionPolicy::getNonAdoptedSubmissionPolicy)
                    .orElse(NonAdoptedSubmissionPolicy.BLOCK_NON_ADOPTED_SUBMISSION.name());
            String remarks = policyOpt.map(ServiceAdoptionPolicy::getRemarks).orElse(null);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("serviceId", serviceId);
            resp.put("serviceName", serviceName);
            resp.put("nonAdoptedSubmissionPolicy", policy);
            resp.put("remarks", remarks);
            return ResponseEntity.ok(resp);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error fetching service adoption policy for serviceId={}", serviceId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getPendingAdoptionSummary(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
            validateServiceAccess(serviceId, userSessionDetails);

            String tenantId = userSessionDetails.getTenantId();
            List<Object[]> rows = pendingRepo.findPendingSummaryByService(tenantId, serviceId);

            List<PendingAdoptionSummaryDTO> dtos = new ArrayList<>();
            for (Object[] row : rows) {
                Integer srvId = (Integer) row[0];
                Integer sduLocId = (Integer) row[1];
                Integer subLocId = (Integer) row[2];
                long count = (Long) row[3];
                Date oldest = (Date) row[4];
                Date latest = (Date) row[5];

                Optional<ServiceSubmissionLocationAdoption> adoptOpt = adoptionRepo
                        .findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
                                tenantId, srvId, sduLocId, subLocId);
                String subLocName = adoptOpt.map(ServiceSubmissionLocationAdoption::getSubmissionLocationName)
                        .orElse("Location-" + subLocId);

                dtos.add(new PendingAdoptionSummaryDTO(srvId, sduLocId, subLocId, subLocName, count, oldest, latest));
            }
            return ResponseEntity.ok(dtos);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Error fetching pending adoption summary for serviceId={}", serviceId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public long releasePendingApplicationsForLocation(String tenantId, Integer serviceId,
                                                      Integer sduLocationId, Integer submissionLocationId,
                                                      Long performedBy) {
        List<PendingAdoptionApplication> pendingList = pendingRepo
                .findByTenantIdAndServiceIdAndSubmissionLocationIdAndStatusAndDeletedFlagFalse(
                        tenantId, serviceId, submissionLocationId, PendingApplicationStatus.PENDING_ADOPTION.name());

        if (pendingList == null || pendingList.isEmpty()) {
            return 0;
        }

        Date now = new Date();
        long releasedCount = 0;
        for (PendingAdoptionApplication app : pendingList) {
            app.setStatus(PendingApplicationStatus.RELEASED_TO_WORKFLOW.name());
            app.setAdoptionDate(now);
            app.setReleasedDate(now);
            app.setModifiedBy(performedBy);
            app.setModifiedOn(now);
            pendingRepo.save(app);

            createAuditLog(tenantId, serviceId, "PENDING_APPLICATION", app.getApplicationId(),
                    sduLocationId, submissionLocationId, "RELEASE_PENDING",
                    PendingApplicationStatus.PENDING_ADOPTION.name(),
                    PendingApplicationStatus.RELEASED_TO_WORKFLOW.name(),
                    "Released to workflow on location adoption", performedBy);
            releasedCount++;
        }

        // Notify that applications have been released for normal processing
        notificationService.sendAdoptionReleasedNotification(tenantId, serviceId, sduLocationId, submissionLocationId, releasedCount);
        LOG.info("Released {} pending applications for serviceId={}, submissionLocationId={}",
                releasedCount, serviceId, submissionLocationId);
        return releasedCount;
    }

    private List<SubmissionLocationNodeDTO> resolveChildSubmissionLocations(
            Integer serviceId, Integer sduLocationId, Integer sduLevelId, String tenantId,
            Map<Integer, ServiceRolloutScope> scopeByLocation,
            Map<String, ServiceSubmissionLocationAdoption> adoptionMap) {

        List<SubmissionLocationNodeDTO> children = new ArrayList<>();

        // Check child rollout scopes where parentLocationId == sduLocationId
        List<ServiceRolloutScope> childScopes = rolloutScopeRepo
                .findByTenantIdAndServiceIdAndParentLocationIdAndDeletedFlagFalse(tenantId, serviceId, sduLocationId);

        if (childScopes != null && !childScopes.isEmpty()) {
            for (ServiceRolloutScope cs : childScopes) {
                SubmissionLocationNodeDTO dto = buildChildDto(cs.getLocationId(), cs.getLocationName(),
                        cs.getDeliveryUnitLevelId(), sduLocationId, tenantId, serviceId, cs.getScopeStatus(), adoptionMap);
                children.add(dto);
            }
        }

        // Check adoptions for this SDU
        List<ServiceSubmissionLocationAdoption> adoptions = adoptionRepo
                .findByTenantIdAndServiceIdAndSduLocationIdAndDeletedFlagFalse(tenantId, serviceId, sduLocationId);

        for (ServiceSubmissionLocationAdoption adopt : adoptions) {
            boolean alreadyPresent = children.stream().anyMatch(c -> Objects.equals(c.getSubmissionLocationId(), adopt.getSubmissionLocationId()));
            if (!alreadyPresent) {
                ServiceRolloutScope scope = scopeByLocation.get(adopt.getSubmissionLocationId());
                String scopeStatus = scope != null ? scope.getScopeStatus() : LocationRolloutScopeStatus.IN_SCOPE.name();
                SubmissionLocationNodeDTO dto = buildChildDto(adopt.getSubmissionLocationId(), adopt.getSubmissionLocationName(),
                        adopt.getSubmissionLevelId(), sduLocationId, tenantId, serviceId, scopeStatus, adoptionMap);
                children.add(dto);
            }
        }

        return children;
    }

    private SubmissionLocationNodeDTO buildChildDto(Integer subLocId, String subLocName, Integer subLevelId,
                                                    Integer sduLocationId, String tenantId, Integer serviceId,
                                                    String scopeStatus,
                                                    Map<String, ServiceSubmissionLocationAdoption> adoptionMap) {
        SubmissionLocationNodeDTO dto = new SubmissionLocationNodeDTO();
        dto.setSubmissionLocationId(subLocId);
        dto.setSubmissionLocationName(subLocName != null ? subLocName : "Location-" + subLocId);
        dto.setSubmissionLevelId(subLevelId);
        dto.setRolloutScopeStatus(scopeStatus);

        ServiceSubmissionLocationAdoption adoption = adoptionMap.get(sduLocationId + "_" + subLocId);
        String adoptStatus = adoption != null ? adoption.getAdoptionStatus() : LocationAdoptionStatus.NOT_ADOPTED.name();
        dto.setAdoptionStatus(adoptStatus);

        long pendingCount = pendingRepo.countByTenantIdAndServiceIdAndSubmissionLocationIdAndStatusAndDeletedFlagFalse(
                tenantId, serviceId, subLocId, PendingApplicationStatus.PENDING_ADOPTION.name());
        dto.setPendingApplicationCount(pendingCount);

        boolean inScope = LocationRolloutScopeStatus.IN_SCOPE.name().equalsIgnoreCase(scopeStatus);
        dto.setEligibleForAdoption(inScope && !LocationAdoptionStatus.ADOPTED.name().equalsIgnoreCase(adoptStatus));

        dto.setOperationalStatus(LocationAdoptionStatus.ADOPTED.name().equalsIgnoreCase(adoptStatus)
                ? LocationOperationalStatus.ACTIVE.name()
                : (LocationAdoptionStatus.SUSPENDED.name().equalsIgnoreCase(adoptStatus)
                        ? LocationOperationalStatus.SUSPENDED.name()
                        : LocationOperationalStatus.INACTIVE.name()));

        return dto;
    }

    private void validateServiceAccess(Integer serviceId, UserSessionDTO userSessionDetails) {
        if (serviceId == null || userSessionDetails == null || userSessionDetails.getTenantId() == null) {
            throw new SPRuntimeError("Invalid request or unauthorized session", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateDesignatedOfficerAuthorization(Integer serviceId, Integer sduLocationId, UserSessionDTO userSessionDetails) {
        if (Boolean.TRUE.equals(userSessionDetails.getTopLevelAdmin())) {
            return;
        }

        // Check if user has DO role or matching assignment
        boolean hasDoRole = userSessionDetails.getRoles() != null && userSessionDetails.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == ApplicationConstants.ROLE_DESIGNATED_OFFICER);

        List<ServiceDesignatedOfficerAssignment> assignments = doAssignmentRepo.findByTenantIdAndServiceId(
                userSessionDetails.getTenantId(), serviceId);

        boolean userAssignedAsDo = assignments != null && assignments.stream()
                .anyMatch(a -> Objects.equals(a.getUserId(), userSessionDetails.getUserID().longValue()));

        if (!hasDoRole && !userAssignedAsDo) {
            LOG.warn("User {} is not an authorized Designated Officer for serviceId={}", userSessionDetails.getUserID(), serviceId);
            throw new SPRuntimeError("Unauthorized: User is not an authorized Designated Officer for this service jurisdiction", HttpStatus.FORBIDDEN);
        }
    }

    private void createAuditLog(String tenantId, Integer serviceId, String entityType, String entityId,
                                Integer sduLocationId, Integer submissionLocationId, String action,
                                String oldState, String newState, String remarks, Long performedBy) {
        try {
            SduAdoptionAuditLog log = new SduAdoptionAuditLog();
            log.setTenantId(tenantId);
            log.setServiceId(serviceId);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setSduLocationId(sduLocationId);
            log.setSubmissionLocationId(submissionLocationId);
            log.setAction(action);
            log.setOldState(oldState);
            log.setNewState(newState);
            log.setRemarks(remarks);
            log.setPerformedBy(performedBy);
            log.setPerformedOn(new Date());
            auditLogRepo.save(log);
        } catch (Exception ex) {
            LOG.error("Failed to persist SDU adoption audit log for entityId={}", entityId, ex);
        }
    }

	public ResponseEntity<?> getServiceAdoptionPolicyList(UserSessionDTO userSessionDetails) {
		try {
			String tenantId = userSessionDetails.getTenantId();
			Long userId = userSessionDetails.getUserID();

			List<ServiceAdoptionPolicy> policies = adoptionPolicyRepo
					.findByTenantIdAndCreatedByAndDeletedFlagFalse(tenantId, userId);

			if (policies == null || policies.isEmpty()) {
				return ResponseEntity.ok(Collections.emptyList());
			}

			List<Integer> serviceIds = policies.stream().map(ServiceAdoptionPolicy::getServiceId)
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			Map<Integer, String> serviceNameMap = serviceIds.isEmpty() ? Collections.emptyMap()
					: serviceDefRepo.findByServiceIdIn(serviceIds).stream()
							.collect(Collectors.toMap(ServiceDefinition::getServiceId,
									ServiceDefinition::getServiceName, (existing, replacement) -> existing));

			List<Map<String, Object>> responseList = policies.stream().map(policyEntity -> {
				Map<String, Object> response = new LinkedHashMap<>();

				Integer serviceId = policyEntity.getServiceId();

				response.put("serviceId", serviceId);
				response.put("serviceName", serviceNameMap.getOrDefault(serviceId, ""));
				response.put("nonAdoptedSubmissionPolicy",
						policyEntity.getNonAdoptedSubmissionPolicy() != null
								? policyEntity.getNonAdoptedSubmissionPolicy()
								: NonAdoptedSubmissionPolicy.BLOCK_NON_ADOPTED_SUBMISSION.name());
				response.put("remarks", policyEntity.getRemarks());

				return response;
			}).collect(Collectors.toList());

			return ResponseEntity.ok(responseList);

		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			LOG.error("Error fetching service adoption policy list for tenantId={}", userSessionDetails.getTenantId(),
					ex);

			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getDepartmentServiceList(Integer status, UserSessionDTO userSessionDetails) {

        JSONObject responseJson = new JSONObject();
        List<ServiceListDetailsDTO> serviceListDetailsDTOs = new ArrayList<>();
        try {
            List<ServiceDefinition> serviceList =serviceDefinitionRepository.findByDepartmentId(userSessionDetails.getEntityId().longValue());
            if (serviceList.isEmpty()) {
                return ResponseEntity.ok(serviceListDetailsDTOs);
            }
            List<Integer> serviceIds = serviceList.stream().map(ServiceDefinition::getServiceId).collect(Collectors.toList());
            List<ServiceLog> serviceLogs =(status != null)? serviceLogRepository.findByServiceIdInAndServiceStatusAndTenantId(serviceIds, status,userSessionDetails.getTenantId())
                    :serviceLogRepository.findByServiceIdInAndTenantId(serviceIds,userSessionDetails.getTenantId());

            Map<Integer, ServiceLog> serviceLogMap = serviceLogs.stream().collect(Collectors.toMap(ServiceLog::getServiceId,sl -> sl,(a, b) -> a));

             serviceList = serviceList.stream().filter(sd -> serviceLogMap.containsKey(sd.getServiceId())).collect(Collectors.toList());
            for (ServiceDefinition service : serviceList) {
                ServiceLog serviceLog = serviceLogMap.get(service.getServiceId());
                ServiceListDetailsDTO dto = new ServiceListDetailsDTO();
                dto.setServiceId(service.getServiceId());
                dto.setServiceName(service.getServiceName());
                dto.setStatus(serviceLog != null && serviceLog.getServiceStatus() != null
                        ? ServiceStatus.fromCode(serviceLog.getServiceStatus()).name()
                        : null);
                serviceListDetailsDTOs.add(dto);
            }

            return ResponseEntity.ok(serviceListDetailsDTOs);

        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
