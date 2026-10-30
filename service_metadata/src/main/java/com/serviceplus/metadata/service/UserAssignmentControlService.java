package com.serviceplus.metadata.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.ServiceJSONDTO;
import com.serviceplus.metadata.dto.ServiceProcessFlowDTO;
import com.serviceplus.metadata.dto.UserAssignmentValidationRequestDTO;
import com.serviceplus.metadata.dto.UserAssignmentValidationResponseDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDeliveryUnits;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.entity.ServiceRolloutScope;
import com.serviceplus.metadata.entity.ServiceSubmissionLocationAdoption;
import com.serviceplus.metadata.enums.LocationAdoptionStatus;
import com.serviceplus.metadata.enums.LocationRolloutScopeStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;
import com.serviceplus.metadata.repository.ServiceDeliveryUnitsRepository;
import com.serviceplus.metadata.repository.IServiceRolloutScopeRepository;
import com.serviceplus.metadata.repository.IServiceSubmissionLocationAdoptionRepository;

@Service
public class UserAssignmentControlService {

    private static final Logger LOG = LogManager.getLogger("UserAssignmentControlService");

    private final IServiceRolloutScopeRepository rolloutScopeRepo;
    private final IServiceSubmissionLocationAdoptionRepository adoptionRepo;
    private final ServiceDeliveryUnitsRepository sduRepo;
    private final IServiceMetadataRepository serviceMetadataRepo;

    @Autowired
    public UserAssignmentControlService(
            IServiceRolloutScopeRepository rolloutScopeRepo,
            IServiceSubmissionLocationAdoptionRepository adoptionRepo,
            ServiceDeliveryUnitsRepository sduRepo,
            @Autowired(required = false) IServiceMetadataRepository serviceMetadataRepo) {
        this.rolloutScopeRepo = rolloutScopeRepo;
        this.adoptionRepo = adoptionRepo;
        this.sduRepo = sduRepo;
        this.serviceMetadataRepo = serviceMetadataRepo;
    }

    public UserAssignmentControlService(
            IServiceRolloutScopeRepository rolloutScopeRepo,
            IServiceSubmissionLocationAdoptionRepository adoptionRepo,
            ServiceDeliveryUnitsRepository sduRepo) {
        this(rolloutScopeRepo, adoptionRepo, sduRepo, null);
    }

    public ResponseEntity<?> validateUserAssignmentEligibility(
            UserAssignmentValidationRequestDTO request, UserSessionDTO userSessionDetails) {
        try {
            Integer serviceId = request.getServiceId();
            Integer subLocId = request.getSubmissionLocationId();
            String tenantId = userSessionDetails.getTenantId();
            Long targetUserId = userSessionDetails.getUserID();

            if (request.getTargetUserId() != null) {
                targetUserId = request.getTargetUserId();
            }

            UserAssignmentValidationResponseDTO response = new UserAssignmentValidationResponseDTO();
            response.setServiceId(serviceId);
            response.setSubmissionLocationId(subLocId);
            response.setTargetUserId(targetUserId);


            // Case 1: Check if selected location is a Service Delivery Unit (SDU)
            Optional<ServiceDeliveryUnits> sduOpt = sduRepo
                    .findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId)
                    .stream()
                    .filter(s -> Objects.equals(s.getLocationId(), subLocId))
                    .findFirst();

            if (sduOpt.isPresent()) {
                // If selected location is SDU, only check activation status of delivery unit
                ServiceDeliveryUnits sdu = sduOpt.get();
                boolean isActive = sdu.getActivationStatus() != null && sdu.getActivationStatus() == 1;
                response.setSduLocationId(subLocId);
                if (isActive) {
                    response.setEligible(true);
                    response.setReason("Service Delivery Unit is active. User assignment permitted.");
                    response.setMessage("User assignment validation passed.");
                    return ResponseEntity.ok(response);
                } else {
                    response.setEligible(false);
                    response.setReason("User assignment prohibited: Service Delivery Unit " + sduOpt.get().getLocationName() + " is not operationally active.");
                    response.setMessage("User assignment rejected: Service Delivery Unit inactive.");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }

            // Case 2: Check if selected location is an actual submission location
            boolean isSubmission = isActualSubmissionLocation(serviceId, subLocId, tenantId);
            if (isSubmission) {
                // Check rollout scope (if scope not configured, default val is IN_SCOPE)
                Optional<ServiceRolloutScope> scopeOpt = rolloutScopeRepo
                        .findByTenantIdAndServiceIdAndLocationIdAndDeletedFlagFalse(tenantId, serviceId, subLocId);

                if (scopeOpt.isPresent() && LocationRolloutScopeStatus.OUT_OF_SCOPE.name().equalsIgnoreCase(scopeOpt.get().getScopeStatus())) {
                    response.setEligible(false);
                    response.setReason("User assignment prohibited: Service is OUT_OF_SCOPE at location " + scopeOpt.get().getLocationName());
                    response.setMessage("User assignment rejected: Location out of rollout scope.");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

                // Check adoption & suspended
                Optional<ServiceSubmissionLocationAdoption> adoptionOpt = adoptionRepo
                        .findByTenantIdAndServiceIdAndSubmissionLocationIdAndDeletedFlagFalse(tenantId, serviceId, subLocId);

                if (adoptionOpt.isEmpty()) {
                    response.setEligible(false);
                    response.setReason("User assignment prohibited: selected location has not been adopted by Designated Officer.");
                    response.setMessage("User assignment rejected: Location not adopted.");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

                ServiceSubmissionLocationAdoption adoption = adoptionOpt.get();
                Integer sduLocationId = adoption.getSduLocationId();
                response.setSduLocationId(sduLocationId);

                
                if (!LocationAdoptionStatus.ADOPTED.name().equalsIgnoreCase(adoption.getAdoptionStatus())) {
                    response.setEligible(false);
                    response.setReason("User assignment prohibited: Submission location (" + adoption.getSubmissionLocationName() + ") status is " + adoption.getAdoptionStatus());
                    response.setMessage("User assignment rejected: Location not adopted.");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

                // Check parent SDU operational status
                Optional<ServiceDeliveryUnits> parentSduOpt = sduRepo
                        .findByTenantIdAndServiceIdAndDeletedFlagFalse(tenantId, serviceId)
                        .stream()
                        .filter(s -> Objects.equals(s.getLocationId(), sduLocationId))
                        .findFirst();

                if (parentSduOpt.isEmpty() || parentSduOpt.get().getActivationStatus() == null || parentSduOpt.get().getActivationStatus() != 1) {
                    response.setEligible(false);
                    response.setReason("User assignment prohibited: Parent SDU " + sduLocationId + " is not operationally active.");
                    response.setMessage("User assignment rejected: Parent SDU inactive.");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

                // All prerequisites satisfied for submission location
                response.setEligible(true);
                response.setReason("Location is adopted and operational. Target user assignment permitted.");
                response.setMessage("User assignment validation passed.");
                return ResponseEntity.ok(response);
            }

            // Case 3: Selected location is neither a submission location nor a delivery unit
            response.setEligible(true);
            response.setReason("Selected location is neither a submission location nor a delivery unit. User assignment permitted.");
            response.setMessage("User assignment validation passed.");
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            LOG.error("Error validating user assignment eligibility", ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isActualSubmissionLocation(Integer serviceId, Integer subLocId, String tenantId) {
        if (serviceMetadataRepo != null) {
            try {
                ServiceMetadata metadata = serviceMetadataRepo.findByServiceIdAndTenantId(serviceId, tenantId);
                if (metadata != null && metadata.getMetadataJson() != null) {
                    ServiceJSONDTO serviceJson = metadata.getMetadataJson();
                    String applSubmissionTaskId = serviceJson.getApplSubmissionTaskId();

                    Set<String> nextTaskIds = new HashSet<>();
                    ServiceProcessFlowDTO processFlow = serviceJson.getProcessFlowMap();
                    if (processFlow != null && processFlow.getTaskRelation() != null && applSubmissionTaskId != null) {
                        ServiceProcessFlowDTO.TaskRelationDTO subRel = processFlow.getTaskRelation().get(applSubmissionTaskId);
                        if (subRel != null && subRel.getNextTask() != null) {
                            nextTaskIds.addAll(subRel.getNextTask());
                        }
                    }

                    if (!nextTaskIds.isEmpty() && serviceJson.getOfficeDetails() != null) {
                        boolean inNextTaskOffices = serviceJson.getOfficeDetails().stream()
                                .filter(o -> o != null && o.getTaskId() != null && nextTaskIds.contains(o.getTaskId()))
                                .filter(o -> o.getAllowedOffices() != null)
                                .flatMap(o -> o.getAllowedOffices().stream())
                                .anyMatch(u -> u != null && Objects.equals(u.getOrgUnitCode(), subLocId));
                        if (inNextTaskOffices) {
                            return true;
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Error checking submission office details via taskRelation for serviceId={}: {}", serviceId, ex.getMessage());
            }
        }

        return false;
    }

    public boolean isSubmissionLocation(Integer serviceId, Integer subLocId, String taskId, String tenantId) {
        return isActualSubmissionLocation(serviceId, subLocId, tenantId);
    }
}
