package com.serviceplus.metadata.versioning.service;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.SpeSequenceLastNo;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.ISpeSequenceLastNoRepository;
import com.serviceplus.metadata.versioning.dto.StartModificationResponse;
import com.serviceplus.metadata.versioning.entity.ServiceVersionChange;
import com.serviceplus.metadata.versioning.enums.ModificationStatus;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.enums.VersionType;
import com.serviceplus.metadata.versioning.repository.ServiceVersionChangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ServiceVersionServiceImpl implements ServiceVersionService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVersionServiceImpl.class);

    private final IServiceLogRepository serviceLogRepository;
    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final ISpeSequenceLastNoRepository speSequenceLastNoRepo;
    private final ServiceVersionChangeRepository versionChangeRepository;
    private final ServiceVersionHistoryService historyService;

    // Components allowed for MINOR modification (PROCESS_FLOW allowed with restrictions; assignments not allowed)
    public static final Set<ServiceComponent> MINOR_ALLOWED_COMPONENTS = EnumSet.of(
            ServiceComponent.SERVICE_DEFINITION,
            ServiceComponent.DELIVERY_UNIT,
            ServiceComponent.PROCESS_FLOW,
            ServiceComponent.OUTPUT_FORMAT,
            ServiceComponent.MVEL_FUNCTION,
            ServiceComponent.NOTIFICATION,
            ServiceComponent.FILTER,
            ServiceComponent.AUA_MAPPING,
            ServiceComponent.SERVICE_CHARGE,
            ServiceComponent.DELIVERY_UNITS,
            ServiceComponent.PAYMENT_GATEWAY_CONFIG,
            ServiceComponent.TEMPLATE,
            ServiceComponent.MVEL_EVENT_MAPPING,
            ServiceComponent.CORRELATION_CONFIG
    );

    public ServiceVersionServiceImpl(
            IServiceLogRepository serviceLogRepository,
            IServiceDefinitionRepository serviceDefinitionRepository,
            ISpeSequenceLastNoRepository speSequenceLastNoRepo,
            ServiceVersionChangeRepository versionChangeRepository,
            ServiceVersionHistoryService historyService) {
        this.serviceLogRepository = serviceLogRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.speSequenceLastNoRepo = speSequenceLastNoRepo;
        this.versionChangeRepository = versionChangeRepository;
        this.historyService = historyService;
    }

    @Override
    @Transactional
    public StartModificationResponse startModification(Integer serviceId, VersionType versionType, UserSessionDTO userSessionDetails) {
        logger.info("Starting {} modification for serviceId={}", versionType, serviceId);

        validateRequest(serviceId, versionType, userSessionDetails);

        ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
        if (serviceLog == null) {
            throw new SPRuntimeError("Service not found for serviceId: " + serviceId, HttpStatus.BAD_REQUEST);
        }

        validateOnlineService(serviceLog);
        validateTenant(serviceLog, userSessionDetails);
        validateExistingModification(serviceId);

        Integer currentMinorVersion = getCurrentMinorVersion(serviceLog);
        Integer targetServiceId;
        int targetMinorVersion;

        if (versionType == VersionType.MINOR) {
            targetServiceId = serviceId;
            targetMinorVersion = currentMinorVersion + 1;
        } else {
            targetServiceId = serviceId + 1;
            targetMinorVersion = 0;
            validateTargetServiceIdAvailable(targetServiceId);
        }

        String modificationToken = generateModificationToken();

        ServiceVersionChange change = new ServiceVersionChange();
        change.setServiceId(serviceId);
        change.setBaseServiceId(serviceLog.getBaseServiceId());
        change.setTenantId(serviceLog.getTenantId());
        change.setUserId(userSessionDetails.getUserID());
        change.setCurrentVersionNo(serviceLog.getVersionNo());
        change.setCurrentMinorVersionNo(currentMinorVersion);
        change.setRequestedVersionType(versionType);
        change.setTargetServiceId(targetServiceId);
        change.setTargetVersionNo(serviceLog.getVersionNo());
        change.setTargetMinorVersionNo(targetMinorVersion);
        change.setModificationToken(modificationToken);
        change.setStatus(ModificationStatus.IN_PROGRESS);
        change.setCreatedAt(new Date());

        change = versionChangeRepository.save(change);

        // 1. Create complete history snapshot first
        historyService.createHistorySnapshot(serviceId, change);

        // 2. Prepare target version data
        if (versionType == VersionType.MINOR) {
            historyService.updateMinorVersion(serviceId, targetMinorVersion);
        } else {
            historyService.createMajorVersion(serviceId, targetServiceId, targetMinorVersion, userSessionDetails.getUserID(), userSessionDetails.getTenantId());
        }

        logger.info("Modification started successfully. modificationId={}, serviceId={}, targetServiceId={}, targetMinorVersion={}",
                change.getId(), serviceId, targetServiceId, targetMinorVersion);

        return buildResponse(change);
    }

    private void validateRequest(Integer serviceId, VersionType versionType, UserSessionDTO userSessionDetails) {
        if (serviceId == null) {
            throw new SPRuntimeError("Service ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (versionType == null) {
            throw new SPRuntimeError("Version type cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (userSessionDetails == null) {
            throw new SPRuntimeError("User session details cannot be null", HttpStatus.UNAUTHORIZED);
        }
    }

    private void validateOnlineService(ServiceLog serviceLog) {
        if (serviceLog.getServiceStatus() == null || serviceLog.getServiceStatus() != ServiceStatus.LAUNCHED.getCode()) {
            throw new SPRuntimeError("Only online services can be modified", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateTenant(ServiceLog serviceLog, UserSessionDTO userSessionDetails) {
        if (serviceLog.getTenantId() == null || userSessionDetails.getTenantId() == null) {
            throw new SPRuntimeError("Tenant information is missing", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(serviceLog.getTenantId(), userSessionDetails.getTenantId())) {
            throw new SPRuntimeError("Service does not belong to current tenant", HttpStatus.FORBIDDEN);
        }
    }

    private void validateExistingModification(Integer serviceId) {
        Optional<ServiceVersionChange> existing = versionChangeRepository.findByServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS);
        if (existing.isPresent()) {
            throw new SPRuntimeError("Modification already in progress for serviceId: " + serviceId, HttpStatus.CONFLICT);
        }
    }

    private Integer getCurrentMinorVersion(ServiceLog serviceLog) {
        return (serviceLog.getMinorVersionNo() != null) ? serviceLog.getMinorVersionNo() : 0;
    }

    private void validateTargetServiceIdAvailable(Integer targetServiceId) {
        ServiceLog targetService = serviceLogRepository.findByServiceId(targetServiceId);
        if (targetService != null) {
            throw new SPRuntimeError("Cannot create major version. Target serviceId " + targetServiceId + " already exists", HttpStatus.CONFLICT);
        }
    }

    private String generateModificationToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private StartModificationResponse buildResponse(ServiceVersionChange change) {
        StartModificationResponse response = new StartModificationResponse();
        response.setModificationId(change.getId());
        response.setModificationToken(change.getModificationToken());
        response.setServiceId(change.getServiceId());
        response.setBaseServiceId(change.getBaseServiceId());
        response.setVersionType(change.getRequestedVersionType());
        response.setCurrentMinorVersion(change.getCurrentMinorVersionNo());
        response.setTargetServiceId(change.getTargetServiceId());
        response.setTargetMinorVersion(change.getTargetMinorVersionNo());
        response.setStatus(change.getStatus().name());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public void validateModification(String modificationToken, ServiceComponent component, UserSessionDTO userSessionDetails) {
        if (modificationToken == null || modificationToken.isBlank()) {
            throw new SPRuntimeError("Modification token is required", HttpStatus.BAD_REQUEST);
        }
        if (component == null) {
            throw new SPRuntimeError("Service component is required", HttpStatus.BAD_REQUEST);
        }

        ServiceVersionChange change = getActiveModification(modificationToken);
        validateModificationOwner(change, userSessionDetails);

        // For MAJOR version, it is a brand new service: no component restrictions apply.
        // For MINOR version, check which functionalities are allowed.
        if (change.getRequestedVersionType() == VersionType.MINOR) {
            if (!MINOR_ALLOWED_COMPONENTS.contains(component)) {
                throw new SPRuntimeError("Modification of " + component + " is not allowed for MINOR version modification", HttpStatus.FORBIDDEN);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceVersionChange> findActiveModification(Integer serviceId) {
        if (serviceId == null) {
            return Optional.empty();
        }
        return versionChangeRepository.findByTargetServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMinorModification(Integer serviceId) {
        return findActiveModification(serviceId)
                .map(c -> c.getRequestedVersionType() == VersionType.MINOR)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateComponentAllowed(Integer serviceId, ServiceComponent component) {
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            ServiceVersionChange change = activeOpt.get();
            if (change.getRequestedVersionType() == VersionType.MINOR) {
                if (!MINOR_ALLOWED_COMPONENTS.contains(component)) {
                    throw new SPRuntimeError("Modification of " + component + " is not allowed for MINOR version modification", HttpStatus.FORBIDDEN);
                }
            }
        }
    }

    private ServiceVersionChange getActiveModification(String modificationToken) {
        ServiceVersionChange change = versionChangeRepository.findByModificationToken(modificationToken)
                .orElseThrow(() -> new SPRuntimeError("Invalid modification token", HttpStatus.NOT_FOUND));

        if (change.getStatus() != ModificationStatus.IN_PROGRESS) {
            throw new SPRuntimeError("Modification is no longer active", HttpStatus.BAD_REQUEST);
        }
        return change;
    }

    private void validateModificationOwner(ServiceVersionChange change, UserSessionDTO userSessionDetails) {
        if (userSessionDetails == null) {
            throw new SPRuntimeError("User session details are required", HttpStatus.UNAUTHORIZED);
        }
        if (!Objects.equals(change.getUserId(), userSessionDetails.getUserID())) {
            throw new SPRuntimeError("User is not authorized for this modification", HttpStatus.FORBIDDEN);
        }
        if (!Objects.equals(change.getTenantId(), userSessionDetails.getTenantId())) {
            throw new SPRuntimeError("Tenant mismatch for modification", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public void cancelModification(String modificationToken, Integer serviceId, UserSessionDTO userSessionDetails) {
        ServiceVersionChange change = null;

        if (modificationToken != null && !modificationToken.isBlank()) {
            change = versionChangeRepository.findByModificationToken(modificationToken).orElse(null);
        }

        if (change == null && serviceId != null) {
            change = versionChangeRepository.findByServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS)
                    .orElseGet(() -> versionChangeRepository.findByTargetServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS)
                            .orElse(null));
        }

        if (change == null) {
            throw new SPRuntimeError("No active modification found to cancel", HttpStatus.NOT_FOUND);
        }

        validateModificationOwner(change, userSessionDetails);

        if (change.getStatus() != ModificationStatus.IN_PROGRESS) {
            throw new SPRuntimeError("Modification is not in progress", HttpStatus.BAD_REQUEST);
        }

        historyService.restoreHistorySnapshot(change);

        change.setStatus(ModificationStatus.CANCELLED);
        change.setUpdatedAt(new Date());
        versionChangeRepository.save(change);

        logger.info("Modification cancelled successfully. modificationId={}, serviceId={}", change.getId(), change.getServiceId());
    }

    @Override
    @Transactional
    public void completeModificationFreeze(Integer serviceId) {
        Optional<ServiceVersionChange> activeChange = versionChangeRepository.findByServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS);
        if (activeChange.isEmpty()) {
            activeChange = versionChangeRepository.findByTargetServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS);
        }

        activeChange.ifPresent(change -> {
            change.setStatus(ModificationStatus.FROZEN);
            change.setUpdatedAt(new Date());
            versionChangeRepository.save(change);
            logger.info("Modification status transitioned to FROZEN for serviceId={}, changeId={}", serviceId, change.getId());
        });
    }

    /**
     * Copy Service Feature:
     * Generates a brand new base service ID via SpeSequenceLastNo sequence.
     * Copies all tables without taking a history snapshot (fresh draft service entry).
     */
    @Override
    @Transactional
    public Map<String, Object> copyService(Integer sourceServiceId, String newServiceName, String newServiceAbbreviation, UserSessionDTO userSessionDetails) {
        return copyService(sourceServiceId, newServiceName, newServiceAbbreviation, ServiceVersionHistoryService.DEFAULT_COPY_SERVICE_EXCLUSIONS, userSessionDetails);
    }

    @Override
    @Transactional
    public Map<String, Object> copyService(Integer sourceServiceId, String newServiceName, String newServiceAbbreviation, Set<ServiceComponent> excludedComponents, UserSessionDTO userSessionDetails) {
        logger.info("Copying service from sourceServiceId={}, excludedComponents={}", sourceServiceId, excludedComponents);

        if (sourceServiceId == null) {
            throw new SPRuntimeError("Source service ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (userSessionDetails == null) {
            throw new SPRuntimeError("User session details cannot be null", HttpStatus.UNAUTHORIZED);
        }

        ServiceLog sourceLog = serviceLogRepository.findByServiceId(sourceServiceId);
        if (sourceLog == null) {
            throw new SPRuntimeError("Source service not found: " + sourceServiceId, HttpStatus.NOT_FOUND);
        }

        validateTenant(sourceLog, userSessionDetails);

        if (newServiceName != null && !newServiceName.isBlank() && serviceDefinitionRepository.existsByServiceNameIgnoreCase(newServiceName)) {
            throw new SPRuntimeError("Service name already exists", HttpStatus.BAD_REQUEST);
        }

        if (newServiceAbbreviation != null && !newServiceAbbreviation.isBlank() && serviceDefinitionRepository.existsByServiceAbbreviation(newServiceAbbreviation)) {
            throw new SPRuntimeError("Service abbreviation already exists", HttpStatus.BAD_REQUEST);
        }

        // Generate new serviceId via sequence (e.g. seq + "0001")
        SpeSequenceLastNo speSequenceLastNo = speSequenceLastNoRepo.getSequenceLastNo(1);
        if (speSequenceLastNo == null || speSequenceLastNo.getFnc_generate_seq() == null) {
            throw new SPRuntimeError("Failed to generate new service sequence", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Integer newServiceId = Integer.valueOf(String.valueOf(speSequenceLastNo.getFnc_generate_seq()).concat("0001"));
        Integer newBaseServiceId = newServiceId / 10000;

        Set<ServiceComponent> effectiveExclusions = (excludedComponents != null)
                ? excludedComponents
                : ServiceVersionHistoryService.DEFAULT_COPY_SERVICE_EXCLUSIONS;

        historyService.copyServiceData(
                sourceServiceId,
                newServiceId,
                newBaseServiceId,
                newServiceName,
                newServiceAbbreviation,
                userSessionDetails.getUserID(),
                userSessionDetails.getTenantId(),
                effectiveExclusions
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("sourceServiceId", sourceServiceId);
        response.put("newServiceId", newServiceId);
        response.put("newBaseServiceId", newBaseServiceId);
        response.put("excludedComponents", effectiveExclusions);
        response.put("message", "Service copied successfully");

        logger.info("Service copied successfully. sourceServiceId={}, newServiceId={}, exclusions={}", sourceServiceId, newServiceId, effectiveExclusions);
        return response;
    }
}