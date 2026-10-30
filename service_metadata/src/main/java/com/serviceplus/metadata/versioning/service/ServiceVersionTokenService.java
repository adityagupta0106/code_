package com.serviceplus.metadata.versioning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO;
import com.serviceplus.metadata.dto.TaskPaymentMappingDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.enums.TaskType;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.aadhaarConfiguration.dto.SaveAuaApiMappingRequest;
import com.serviceplus.metadata.correlationConfig.dto.ServiceCorrelationRequestDTO;
import com.serviceplus.metadata.document.mapping.dto.DocMappingDTO;
import com.serviceplus.metadata.dto.PaymentGatewayMappingRequest;
import com.serviceplus.metadata.dto.ServiceChargeDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceOutputFormatDTO;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfig;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;
import com.serviceplus.metadata.versioning.dto.ChangeItemDTO;
import com.serviceplus.metadata.versioning.dto.ServiceVersionChangesResponse;
import com.serviceplus.metadata.versioning.entity.ServiceVersionChange;
import com.serviceplus.metadata.versioning.entity.ServiceVersionChangeLog;
import com.serviceplus.metadata.versioning.enums.ModificationStatus;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.enums.VersionType;
import com.serviceplus.metadata.versioning.repository.ServiceVersionChangeLogRepository;
import com.serviceplus.metadata.versioning.repository.ServiceVersionChangeRepository;
import com.serviceplus.metadata.entity.ProcessFlowHistory;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinitionHistory;
import com.serviceplus.metadata.entity.ServiceDefinitionHistory;
import com.serviceplus.metadata.entity.ServiceOutputFormatHistory;
import com.serviceplus.metadata.entity.ServiceChargeDetailHistory;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfigHistory;
import com.serviceplus.metadata.mvel.entity.MvelFunctionHistory;
import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMappingHistory;
import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfigurationHistory;
import com.serviceplus.metadata.repository.IProcessFlowHistoryRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionHistoryRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionHistoryRepository;
import com.serviceplus.metadata.repository.IServiceOutputFormatHistoryRepository;
import com.serviceplus.metadata.repository.IServiceChargeDetailHistoryRepository;
import com.serviceplus.metadata.repository.IServicePaymentGatewayConfigHistoryRepository;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionHistoryRepository;
import com.serviceplus.metadata.aadhaarConfiguration.repository.IAuaApiFieldMappingHistoryRepository;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ServiceVersionTokenService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVersionTokenService.class);

    private final ServiceVersionChangeRepository versionChangeRepository;
    private final ServiceVersionChangeLogRepository changeLogRepository;
    private final IServiceLogRepository serviceLogRepository;
    private final ObjectMapper objectMapper;

    private final IProcessFlowHistoryRepository processFlowHistoryRepository;
    private final IServiceDeliveryUnitDefinitionHistoryRepository serviceDeliveryUnitDefinitionHistoryRepository;
    private final IServiceDefinitionHistoryRepository serviceDefinitionHistoryRepository;
    private final IServiceOutputFormatHistoryRepository serviceOutputFormatHistoryRepository;
    private final IServiceChargeDetailHistoryRepository serviceChargeDetailHistoryRepository;
    private final IServicePaymentGatewayConfigHistoryRepository servicePaymentGatewayConfigHistoryRepository;
    private final IMvelFunctionHistoryRepository mvelFunctionHistoryRepository;
    private final IAuaApiFieldMappingHistoryRepository auaApiFieldMappingHistoryRepository;
    private final ICorrelationConfigurationHistoryRepository correlationConfigurationHistoryRepository;

    public ServiceVersionTokenService(
            ServiceVersionChangeRepository versionChangeRepository,
            ServiceVersionChangeLogRepository changeLogRepository,
            IServiceLogRepository serviceLogRepository,
            ObjectMapper objectMapper,
            IProcessFlowHistoryRepository processFlowHistoryRepository,
            IServiceDeliveryUnitDefinitionHistoryRepository serviceDeliveryUnitDefinitionHistoryRepository,
            IServiceDefinitionHistoryRepository serviceDefinitionHistoryRepository,
            IServiceOutputFormatHistoryRepository serviceOutputFormatHistoryRepository,
            IServiceChargeDetailHistoryRepository serviceChargeDetailHistoryRepository,
            IServicePaymentGatewayConfigHistoryRepository servicePaymentGatewayConfigHistoryRepository,
            IMvelFunctionHistoryRepository mvelFunctionHistoryRepository,
            IAuaApiFieldMappingHistoryRepository auaApiFieldMappingHistoryRepository,
            ICorrelationConfigurationHistoryRepository correlationConfigurationHistoryRepository) {
        this.versionChangeRepository = versionChangeRepository;
        this.changeLogRepository = changeLogRepository;
        this.serviceLogRepository = serviceLogRepository;
        this.objectMapper = objectMapper;
        this.processFlowHistoryRepository = processFlowHistoryRepository;
        this.serviceDeliveryUnitDefinitionHistoryRepository = serviceDeliveryUnitDefinitionHistoryRepository;
        this.serviceDefinitionHistoryRepository = serviceDefinitionHistoryRepository;
        this.serviceOutputFormatHistoryRepository = serviceOutputFormatHistoryRepository;
        this.serviceChargeDetailHistoryRepository = serviceChargeDetailHistoryRepository;
        this.servicePaymentGatewayConfigHistoryRepository = servicePaymentGatewayConfigHistoryRepository;
        this.mvelFunctionHistoryRepository = mvelFunctionHistoryRepository;
        this.auaApiFieldMappingHistoryRepository = auaApiFieldMappingHistoryRepository;
        this.correlationConfigurationHistoryRepository = correlationConfigurationHistoryRepository;
    }

    /**
     * Retrieves baseline ProcessFlowDTO from history table for active minor modification.
     */
    public ProcessFlowDTO getBaselineProcessFlow(Integer serviceId) {
        if (serviceId == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<ProcessFlowHistory> list = processFlowHistoryRepository.findByModificationId(modId);
            if (list != null && !list.isEmpty()) {
                return list.getFirst().getProcessFlowJson();
            }
        }
        List<ProcessFlowHistory> list = processFlowHistoryRepository.findByServiceId(serviceId);
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .filter(h -> h.getProcessFlowJson() != null)
                    .max(Comparator.comparing(ProcessFlowHistory::getArchivedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(ProcessFlowHistory::getProcessFlowJson)
                    .orElse(null);
        }
        return null;
    }

    /**
     * Retrieves baseline ServiceDeliveryUnitDefinition from history table for active minor modification.
     */
    public ServiceDeliveryUnitDefinition getBaselineDeliveryUnitDefinition(Integer serviceId) {
        if (serviceId == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<ServiceDeliveryUnitDefinitionHistory> list = serviceDeliveryUnitDefinitionHistoryRepository.findByModificationId(modId);
            if (list != null && !list.isEmpty()) {
                ServiceDeliveryUnitDefinitionHistory h = list.get(0);
                ServiceDeliveryUnitDefinition def = new ServiceDeliveryUnitDefinition();
                def.setServiceId(h.getServiceId());
                def.setServiceDeliveryUnits(h.getServiceDeliveryUnits());
                def.setSource(h.getSource());
                return def;
            }
        }
        List<ServiceDeliveryUnitDefinitionHistory> list = serviceDeliveryUnitDefinitionHistoryRepository.findByServiceId(serviceId);
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .filter(h -> h.getServiceDeliveryUnits() != null)
                    .max(Comparator.comparing(ServiceDeliveryUnitDefinitionHistory::getArchivedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(h -> {
                        ServiceDeliveryUnitDefinition def = new ServiceDeliveryUnitDefinition();
                        def.setServiceId(h.getServiceId());
                        def.setServiceDeliveryUnits(h.getServiceDeliveryUnits());
                        def.setSource(h.getSource());
                        return def;
                    })
                    .orElse(null);
        }
        return null;
    }

    /**
     * Retrieves baseline ServiceDefinitionDTO from history table for active minor modification.
     */
    public ServiceDefinitionDTO getBaselineServiceDefinition(Integer serviceId) {
        if (serviceId == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<ServiceDefinitionHistory> list = serviceDefinitionHistoryRepository.findByModificationId(modId);
            if (list != null && !list.isEmpty()) {
                return list.get(0).getDefinitionJson();
            }
        }
        List<ServiceDefinitionHistory> list = serviceDefinitionHistoryRepository.findByServiceId(serviceId);
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .filter(h -> h.getDefinitionJson() != null)
                    .max(Comparator.comparing(ServiceDefinitionHistory::getArchivedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(ServiceDefinitionHistory::getDefinitionJson)
                    .orElse(null);
        }
        return null;
    }

    /**
     * Retrieves baseline ServiceOutputFormatDTO from history table for active minor modification.
     */
    public ServiceOutputFormatDTO getBaselineOutputFormat(Integer serviceId, Long outputFormatId) {
        if (serviceId == null || outputFormatId == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<ServiceOutputFormatHistory> list = serviceOutputFormatHistoryRepository.findByModificationId(modId);
            if (list != null) {
                for (ServiceOutputFormatHistory h : list) {
                    if (outputFormatId.equals(h.getId())) {
                        return h.getOutputFormatJson();
                    }
                }
            }
        }
        List<ServiceOutputFormatHistory> list = serviceOutputFormatHistoryRepository.findByServiceId(serviceId);
        if (list != null) {
            for (ServiceOutputFormatHistory h : list) {
                if (outputFormatId.equals(h.getId())) {
                    return h.getOutputFormatJson();
                }
            }
        }
        return null;
    }

    public boolean isOutputFormatInHistory(Integer serviceId, Long outputFormatId) {
        return getBaselineOutputFormat(serviceId, outputFormatId) != null;
    }

    /**
     * Retrieves baseline ServiceChargeDTO from history table for active minor modification.
     */
    public ServiceChargeDTO getBaselineServiceCharge(Integer serviceId, Long chargeId) {
        if (serviceId == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<ServiceChargeDetailHistory> list = serviceChargeDetailHistoryRepository.findByModificationId(modId);
            if (list != null) {
                for (ServiceChargeDetailHistory h : list) {
                    if (chargeId == null || chargeId.equals(h.getId())) {
                        return h.getServiceChargeJson();
                    }
                }
            }
        }
        List<ServiceChargeDetailHistory> list = serviceChargeDetailHistoryRepository.findByServiceId(serviceId);
        if (list != null) {
            for (ServiceChargeDetailHistory h : list) {
                if (chargeId == null || chargeId.equals(h.getId())) {
                    return h.getServiceChargeJson();
                }
            }
        }
        return null;
    }

    public boolean isServiceChargeInHistory(Integer serviceId, Long chargeId) {
        return getBaselineServiceCharge(serviceId, chargeId) != null;
    }

    /**
     * Retrieves baseline ServicePaymentGatewayConfigHistory from history table.
     */
    public ServicePaymentGatewayConfigHistory getBaselinePaymentGatewayConfigHistory(Integer serviceId, String gatewayCode) {
        if (serviceId == null || gatewayCode == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<ServicePaymentGatewayConfigHistory> list = servicePaymentGatewayConfigHistoryRepository.findByModificationId(modId);
            if (list != null) {
                for (ServicePaymentGatewayConfigHistory h : list) {
                    if (gatewayCode.equalsIgnoreCase(h.getGatewayCode())) {
                        return h;
                    }
                }
            }
        }
        List<ServicePaymentGatewayConfigHistory> list = servicePaymentGatewayConfigHistoryRepository.findByServiceId(serviceId);
        if (list != null) {
            for (ServicePaymentGatewayConfigHistory h : list) {
                if (gatewayCode.equalsIgnoreCase(h.getGatewayCode())) {
                    return h;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves baseline MvelFunctionDTO from history table for active minor modification.
     */
    public MvelFunctionDTO getBaselineMvelFunction(Integer serviceId, Long functionId) {
        if (serviceId == null || functionId == null) {
            return null;
        }
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            Long modId = activeOpt.get().getId();
            List<MvelFunctionHistory> list = mvelFunctionHistoryRepository.findByModificationId(modId);
            if (list != null) {
                for (MvelFunctionHistory h : list) {
                    if (functionId.equals(h.getId())) {
                        return h.getMvelFunctionJson();
                    }
                }
            }
        }
        List<MvelFunctionHistory> list = mvelFunctionHistoryRepository.findByServiceId(serviceId);
        if (list != null) {
            for (MvelFunctionHistory h : list) {
                if (functionId.equals(h.getId())) {
                    return h.getMvelFunctionJson();
                }
            }
        }
        return null;
    }

    public boolean isMvelFunctionInHistory(Integer serviceId, Long functionId) {
        return getBaselineMvelFunction(serviceId, functionId) != null;
    }

    /**
     * Finds active modification for a serviceId (matching targetServiceId).
     */
    public Optional<ServiceVersionChange> findActiveModification(Integer serviceId) {
        if (serviceId == null) {
            return Optional.empty();
        }
        return versionChangeRepository.findByTargetServiceIdAndStatus(serviceId, ModificationStatus.IN_PROGRESS);
    }

    /**
     * Checks if the service is currently under an active MINOR version modification.
     */
    public boolean isMinorModification(Integer serviceId) {
        return findActiveModification(serviceId)
                .map(c -> c.getRequestedVersionType() == VersionType.MINOR)
                .orElse(false);
    }

    /**
     * Validates that the requested module/component is allowed to be edited during MINOR modification.
     */
    public void validateComponentAllowed(Integer serviceId, ServiceComponent component) {
        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
        if (activeOpt.isPresent()) {
            ServiceVersionChange change = activeOpt.get();
            if (change.getRequestedVersionType() == VersionType.MINOR) {
                if (!ServiceVersionServiceImpl.MINOR_ALLOWED_COMPONENTS.contains(component)) {
                    logger.warn("Component {} is forbidden for minor modification on serviceId={}", component, serviceId);
                    throw new SPRuntimeError("Module/Component '" + component + "' is not allowed to be edited during minor version modification", HttpStatus.FORBIDDEN);
                }
            }
        }
    }

    /**
     * Builds ProcessFlowRestrictions metadata to attach to ProcessFlowDTO if state is minor.
     */
    public ProcessFlowDTO.ProcessFlowRestrictions buildProcessFlowRestrictions(Integer serviceId, ProcessFlowDTO processFlowDTO) {
        if (!isMinorModification(serviceId)) {
            return null;
        }

        Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);

        ProcessFlowDTO.ProcessFlowRestrictions restrictions = new ProcessFlowDTO.ProcessFlowRestrictions();
        restrictions.setMinorVersion(true);
        restrictions.setAllowDeleteTask(false);
        restrictions.setAllowChangeTaskType(false);
        restrictions.setAllowDeleteSDU(false);
        restrictions.setAllowChangeFormMapping(false);
        restrictions.setAllowChangeBunchingRules(false);
        restrictions.setAllowModifyDbtTasks(false);
        activeOpt.ifPresent(serviceVersionChange -> restrictions.setModificationToken(serviceVersionChange.getModificationToken()));

        // Baseline must strictly come from history snapshot
        ProcessFlowDTO baselineDto = getBaselineProcessFlow(serviceId);
        if (baselineDto == null) {
            baselineDto = processFlowDTO;
        }

        if (baselineDto != null && baselineDto.getNodes() != null) {
            List<String> taskIds = baselineDto.getNodes().stream()
                    .filter(node -> !ApplicationConstants.SUBMISSION_NODE_TYPE.equalsIgnoreCase(node.getType()))
                    .map(ProcessFlowDTO.NodeDTO::getId)
                    .collect(Collectors.toList());
            restrictions.setRestrictedTaskIds(taskIds);
        } else {
            restrictions.setRestrictedTaskIds(Collections.emptyList());
        }

        restrictions.setHistoryProcessFlow(baselineDto);
        restrictions.setOldProcessFlow(baselineDto);

        restrictions.setMessages(List.of(
                "Deleting existing tasks is restricted in minor version modification",
                "Modifying task properties or changing task type is restricted in minor version modification",
                "Deleting SDU / entities from tasks is restricted in minor version modification",
                "Removing forms or changing form mapping for previously configured tasks is restricted in minor version modification",
                "Enabling bunching or modifying bunching rules is restricted in minor version modification",
                "Modifying DBT tasks is restricted in minor version modification",
                "Disabling existing mode of submission is restricted in minor version modification",
                "Configuring 'Reject enclosures' or 'Upload rejected enclosures' is restricted in minor version modification"
        ));

        return restrictions;
    }

    /**
     * Enforces minute-level validations for ProcessFlowDTO when state is minor using baseline history snapshot.
     */
    public void validateProcessFlowMinorVersion(Integer serviceId, ProcessFlowDTO newDto, ProcessFlowDTO fallbackSavedDto) {
        ProcessFlowDTO baseline = getBaselineProcessFlow(serviceId);
        if (baseline == null) {
            baseline = fallbackSavedDto;
        }
        validateProcessFlowMinorVersion(newDto, baseline);
    }

    /**
     * Enforces minute-level validations for ProcessFlowDTO when state is minor according to Live Service Changes specification:
     * 1. Restrict deleting any existing task
     * 2. Restrict modifying task properties like type of task
     * 3. Restrict changing the task type
     * 4. Restrict deleting SDU
     * 5. Restrict removing forms or changing form mapping for previously configured tasks
     * 6. Restrict enabling bunching or modifying bunching rules in task which was previously configured
     * 7. Restrict modifying anything if task type is DBT
     * 8. Restrict disabling existing mode of submission
     * 9. Restrict configuring 'Reject enclosures' and 'Upload rejected enclosures'
     */
    public void validateProcessFlowMinorVersion(ProcessFlowDTO newDto, ProcessFlowDTO savedDto) {
        if (savedDto == null || savedDto.getNodes() == null || savedDto.getNodes().isEmpty()) {
            return;
        }

        Map<String, ProcessFlowDTO.NodeDTO> oldNodeMap = savedDto.getNodes().stream()
                .collect(Collectors.toMap(ProcessFlowDTO.NodeDTO::getId, Function.identity(), (a, b) -> a));

        Map<String, ProcessFlowDTO.NodeDTO> newNodeMap = (newDto != null && newDto.getNodes() != null) ?
                newDto.getNodes().stream().collect(Collectors.toMap(ProcessFlowDTO.NodeDTO::getId, Function.identity(), (a, b) -> a)) :
                Collections.emptyMap();

        // 1. Restrict deleting any existing task
        for (ProcessFlowDTO.NodeDTO oldNode : oldNodeMap.values()) {
            if (!newNodeMap.containsKey(oldNode.getId())) {
                String taskName = (oldNode.getData() != null && oldNode.getData().getName() != null) ?
                        oldNode.getData().getName() : oldNode.getId();
                logger.warn("Minor version violation: attempt to delete existing task '{}' (ID: {})", taskName, oldNode.getId());
                throw new SPRuntimeError("Deleting existing task '" + taskName + "' (ID: " + oldNode.getId() + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
            }
        }

        // Validate each existing node against minor version restrictions
        for (Map.Entry<String, ProcessFlowDTO.NodeDTO> entry : oldNodeMap.entrySet()) {
            String nodeId = entry.getKey();
            ProcessFlowDTO.NodeDTO oldNode = entry.getValue();
            ProcessFlowDTO.NodeDTO newNode = newNodeMap.get(nodeId);

            String taskName = (oldNode.getData() != null && oldNode.getData().getName() != null) ?
                    oldNode.getData().getName() : nodeId;

            // 7. Restrict changing anything if task type is DBT
            if (isDbtTask(oldNode)) {
                if (!Objects.equals(oldNode.getType(), newNode.getType()) || !isNodeDataEqual(oldNode.getData(), newNode.getData())) {
                    logger.warn("Minor version violation: attempt to modify DBT task '{}' (ID: {})", taskName, nodeId);
                    throw new SPRuntimeError("Task '" + taskName + "' (ID: " + nodeId + ") is a DBT task. Modifying DBT tasks is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                }
                // DBT task is completely immutable, skip further property checks
                continue;
            }

            // 8. Restrict disabling existing mode of submission on submission node
            if (ApplicationConstants.SUBMISSION_NODE_TYPE.equalsIgnoreCase(oldNode.getType()) || "submission".equalsIgnoreCase(nodeId)) {
                if (oldNode.getData() != null && oldNode.getData().getMode() != null && !oldNode.getData().getMode().isEmpty()) {
                    List<LabelValue> oldModes = oldNode.getData().getMode();
                    List<LabelValue> newModes = (newNode.getData() != null && newNode.getData().getMode() != null) ?
                            newNode.getData().getMode() : Collections.emptyList();

                    Set<String> newModeValues = newModes.stream()
                            .filter(m -> m.getValue() != null)
                            .map(m -> String.valueOf(m.getValue()))
                            .collect(Collectors.toSet());

                    for (LabelValue om : oldModes) {
                        if (om.getValue() != null && !newModeValues.contains(String.valueOf(om.getValue()))) {
                            String modeLabel = om.getLabel() != null ? om.getLabel() : String.valueOf(om.getValue());
                            logger.warn("Minor version violation: attempt to disable existing mode of submission '{}'", modeLabel);
                            throw new SPRuntimeError("Disabling existing mode of submission '" + modeLabel + "' is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }

            // 2 & 3. Check node type
            if (!Objects.equals(oldNode.getType(), newNode.getType())) {
                logger.warn("Minor version violation: attempt to change node type for task '{}' (ID: {}) from {} to {}",
                        taskName, nodeId, oldNode.getType(), newNode.getType());
                throw new SPRuntimeError("Changing node type for task '" + taskName + "' (ID: " + nodeId + ") from '" + oldNode.getType() + "' to '" + newNode.getType() + "' is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
            }

            if (oldNode.getData() != null && newNode.getData() != null) {
                // Check taskType
                Object oldTaskType = (oldNode.getData().getTaskType() != null) ? oldNode.getData().getTaskType().getValue() : null;
                Object newTaskType = (newNode.getData().getTaskType() != null) ? newNode.getData().getTaskType().getValue() : null;
                if (!Objects.equals(oldTaskType, newTaskType)) {
                    logger.warn("Minor version violation: attempt to change taskType for task '{}'", taskName);
                    throw new SPRuntimeError("Changing task type for task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                }

                // Check humanTask
                Object oldHt = (oldNode.getData().getHumanTask() != null) ? oldNode.getData().getHumanTask().getValue() : null;
                Object newHt = (newNode.getData().getHumanTask() != null) ? newNode.getData().getHumanTask().getValue() : null;
                if (!Objects.equals(oldHt, newHt)) {
                    logger.warn("Minor version violation: attempt to modify humanTask property for task '{}'", taskName);
                    throw new SPRuntimeError("Modifying human task property for task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                }

                // Check systemTask
                Object oldSt = (oldNode.getData().getSystemTask() != null) ? oldNode.getData().getSystemTask().getValue() : null;
                Object newSt = (newNode.getData().getSystemTask() != null) ? newNode.getData().getSystemTask().getValue() : null;
                if (!Objects.equals(oldSt, newSt)) {
                    logger.warn("Minor version violation: attempt to modify systemTask property for task '{}'", taskName);
                    throw new SPRuntimeError("Modifying system task property for task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                }

                // Check templateTask
                Object oldTt = (oldNode.getData().getTemplateTask() != null) ? oldNode.getData().getTemplateTask().getValue() : null;
                Object newTt = (newNode.getData().getTemplateTask() != null) ? newNode.getData().getTemplateTask().getValue() : null;
                if (!Objects.equals(oldTt, newTt)) {
                    logger.warn("Minor version violation: attempt to modify templateTask property for task '{}'", taskName);
                    throw new SPRuntimeError("Modifying template task property for task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                }

                // 4. Restrict deleting SDU / entities from an existing task
                List<ProcessFlowDTO.Entities> oldEntities = oldNode.getData().getEntities();
                if (oldEntities != null && !oldEntities.isEmpty()) {
                    List<ProcessFlowDTO.Entities> newEntities = newNode.getData().getEntities();
                    if (newEntities == null || newEntities.isEmpty()) {
                        logger.warn("Minor version violation: attempt to remove all SDUs/entities from task '{}'", taskName);
                        throw new SPRuntimeError("Deleting SDU / entities from task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                    }

                    Set<String> newEntityKeys = newEntities.stream()
                            .map(this::getEntityKey)
                            .collect(Collectors.toSet());

                    for (ProcessFlowDTO.Entities oldEnt : oldEntities) {
                        String oldKey = getEntityKey(oldEnt);
                        if (!newEntityKeys.contains(oldKey)) {
                            String entityLabel = (oldEnt.getEntity() != null && oldEnt.getEntity().getLabel() != null) ?
                                    oldEnt.getEntity().getLabel() : oldKey;
                            logger.warn("Minor version violation: attempt to remove SDU entity '{}' from task '{}'", entityLabel, taskName);
                            throw new SPRuntimeError("Deleting existing SDU / entity '" + entityLabel + "' from task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                // 5. Restrict removing forms or changing formId mapping for previously configured tasks
                ProcessFlowDTO.FormDetail oldFormDetail = oldNode.getData().getFormDetail();
                ProcessFlowDTO.FormDetail newFormDetail = newNode.getData().getFormDetail();
                if (oldFormDetail != null && oldFormDetail.getFormId() != null && !oldFormDetail.getFormId().trim().isEmpty()) {
                    String oldFormId = oldFormDetail.getFormId().trim();
                    String newFormId = (newFormDetail != null && newFormDetail.getFormId() != null) ? newFormDetail.getFormId().trim() : null;
                    if (!Objects.equals(oldFormId, newFormId)) {
                        logger.warn("Minor version violation: attempt to change formId mapping for task '{}' (ID: {}) from '{}' to '{}'",
                                taskName, nodeId, oldFormId, newFormId);
                        throw new SPRuntimeError("Changing form mapping for task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                    }
                }

                // 6. Restrict enabling bunching or modifying bunching rules in task which was previously configured
                ProcessFlowDTO.Advanced oldAdv = oldNode.getData().getAdvanced();
                ProcessFlowDTO.Advanced newAdv = newNode.getData().getAdvanced();
                if (oldAdv != null) {
                    // Check direct multiProcessing
                    ProcessFlowDTO.MultiProcessing oldMp = oldAdv.getMultiProcessing();
                    ProcessFlowDTO.MultiProcessing newMp = (newAdv != null) ? newAdv.getMultiProcessing() : null;
                    validateBunchingRule(oldMp, newMp, taskName, nodeId);

                    // Check official multiProcessing
                    ProcessFlowDTO.MultiProcessing oldOffMp = (oldAdv.getOfficial() != null) ? oldAdv.getOfficial().getMultiProcessing() : null;
                    ProcessFlowDTO.MultiProcessing newOffMp = (newAdv != null && newAdv.getOfficial() != null) ? newAdv.getOfficial().getMultiProcessing() : null;
                    validateBunchingRule(oldOffMp, newOffMp, taskName, nodeId);

                    // 9. Restrict configuring 'Reject enclosures'
                    boolean oldReject = (oldAdv.getOfficial() != null) && oldAdv.getOfficial().isRejectEnclosure();
                    boolean newReject = (newAdv != null && newAdv.getOfficial() != null) && newAdv.getOfficial().isRejectEnclosure();
                    if (!oldReject && newReject) {
                        logger.warn("Minor version violation: attempt to enable 'Reject enclosures' on task '{}'", taskName);
                        throw new SPRuntimeError("Configuring 'Reject enclosures' on task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                    }

                    // 9. Restrict configuring 'Upload rejected enclosures'
                    boolean oldUploadReject = (oldAdv.getApplicant() != null) && oldAdv.getApplicant().isUploadRejectedEnclosures();
                    boolean newUploadReject = (newAdv != null && newAdv.getApplicant() != null) && newAdv.getApplicant().isUploadRejectedEnclosures();
                    if (!oldUploadReject && newUploadReject) {
                        logger.warn("Minor version violation: attempt to enable 'Upload rejected enclosures' on task '{}'", taskName);
                        throw new SPRuntimeError("Configuring 'Upload rejected enclosures' on task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                    }
                } else if (newAdv != null) {
                    // oldAdv was null, check if bunching was enabled newly
                    if (isBunchingConfigured(newAdv.getMultiProcessing()) ||
                            (newAdv.getOfficial() != null && isBunchingConfigured(newAdv.getOfficial().getMultiProcessing()))) {
                        throw new SPRuntimeError("Enabling bunching on task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
    }

    private void validateBunchingRule(ProcessFlowDTO.MultiProcessing oldMp, ProcessFlowDTO.MultiProcessing newMp, String taskName, String nodeId) {
        boolean oldBunching = isBunchingConfigured(oldMp);
        boolean newBunching = isBunchingConfigured(newMp);

        if (!oldBunching && newBunching) {
            logger.warn("Minor version violation: attempt to enable bunching on task '{}'", taskName);
            throw new SPRuntimeError("Enabling bunching on task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
        }

        if (oldBunching && isBunchingModified(oldMp, newMp)) {
            logger.warn("Minor version violation: attempt to modify bunching rules for task '{}'", taskName);
            throw new SPRuntimeError("Modifying bunching rules for task '" + taskName + "' (ID: " + nodeId + ") is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Checks if task is a DBT task.
     */
    public boolean isDbtTask(ProcessFlowDTO.NodeDTO node) {
        if (node == null) {
            return false;
        }

        if (node.getData() != null) {
            TaskType resolved = TaskType.resolve(node.getData());
            return resolved == TaskType.DBT;
        }

        return false;
    }

    private boolean isNodeDataEqual(ProcessFlowDTO.DataDTO d1, ProcessFlowDTO.DataDTO d2) {
        if (d1 == d2) return true;
        if (d1 == null || d2 == null) return false;
        try {
            JsonNode tree1 = objectMapper.valueToTree(d1);
            JsonNode tree2 = objectMapper.valueToTree(d2);
            return Objects.equals(tree1, tree2);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBunchingConfigured(ProcessFlowDTO.MultiProcessing mp) {
        if (mp == null) {
            return false;
        }
        if (mp.isValue()) {
            return true;
        }
        if (mp.getData() != null) {
            return mp.getData().getMultipleProcessingType() != null
                    &&  mp.getData().getMultipleProcessingType().getValue().equals("bulk");
        }
        return false;
    }

    private boolean isBunchingModified(ProcessFlowDTO.MultiProcessing oldMp, ProcessFlowDTO.MultiProcessing newMp) {
        if (!isBunchingConfigured(oldMp)) {
            return false;
        }
        if (newMp == null || !newMp.isValue()) {
            return true;
        }
        if (oldMp.getData() != null) {
            if (newMp.getData() == null) {
                return true;
            }
            Object oldBunch = oldMp.getData().getBunchProcessingType() != null ? oldMp.getData().getBunchProcessingType().getValue() : null;
            Object newBunch = newMp.getData().getBunchProcessingType() != null ? newMp.getData().getBunchProcessingType().getValue() : null;
            if (!Objects.equals(oldBunch, newBunch)) {
                return true;
            }
            Object oldMult = oldMp.getData().getMultipleProcessingType() != null ? oldMp.getData().getMultipleProcessingType().getValue() : null;
            Object newMult = newMp.getData().getMultipleProcessingType() != null ? newMp.getData().getMultipleProcessingType().getValue() : null;
            if (!Objects.equals(oldMult, newMult)) {
                return true;
            }
            if (oldMp.getData().isUngroupBunchedApplicationPrevious() != newMp.getData().isUngroupBunchedApplicationPrevious()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that existing SDUs defined in ServiceDeliveryUnitDefinition are not deleted during minor modification using baseline history snapshot.
     */
    public void validateDeliveryUnitsMinorVersion(Integer serviceId,
                                                  ServiceDeliveryUnitDTO newDto,
                                                  ServiceDeliveryUnitDefinition fallbackDef) {
        ServiceDeliveryUnitDefinition baseline = getBaselineDeliveryUnitDefinition(serviceId);
        if (baseline == null) {
            baseline = fallbackDef;
        }
        validateDeliveryUnitsMinorVersion(newDto, baseline);
    }

    /**
     * Validates that existing SDUs defined in ServiceDeliveryUnitDefinition are not deleted during minor modification.
     */
    public void validateDeliveryUnitsMinorVersion(ServiceDeliveryUnitDTO newDto,
                                                  ServiceDeliveryUnitDefinition existingDef) {
        if (existingDef == null || existingDef.getServiceDeliveryUnits() == null || existingDef.getServiceDeliveryUnits().isEmpty()) {
            return;
        }

        List<ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON> oldUnits = existingDef.getServiceDeliveryUnits();
        List<ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON> newUnits = (newDto != null && newDto.getServiceDeliveryUnits() != null) ?
                newDto.getServiceDeliveryUnits() : Collections.emptyList();

        Set<String> newUnitKeys = newUnits.stream()
                .map(this::getDeliveryUnitKey)
                .collect(Collectors.toSet());

        for (ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON oldUnit : oldUnits) {
            String oldKey = getDeliveryUnitKey(oldUnit);
            if (!newUnitKeys.contains(oldKey)) {
                String unitLabel = (oldUnit.getServiceDeliveryUnit() != null && oldUnit.getServiceDeliveryUnit().getLabel() != null) ?
                        oldUnit.getServiceDeliveryUnit().getLabel() : oldKey;
                logger.warn("Minor version violation: attempt to delete existing Service Delivery Unit '{}'", unitLabel);
                throw new SPRuntimeError("Deleting existing Service Delivery Unit '" + unitLabel + "' is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private String getDeliveryUnitKey(ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON u) {
        if (u == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (u.getUserRole() != null && u.getUserRole().getValue() != null) {
            sb.append(u.getUserRole().getValue()).append(":");
        }
        if (u.getServiceDeliveryUnit() != null && u.getServiceDeliveryUnit().getValue() != null) {
            sb.append(u.getServiceDeliveryUnit().getValue()).append(":");
        }
        if (u.getUserLevel() != null && u.getUserLevel().getValue() != null) {
            sb.append(u.getUserLevel().getValue()).append(":");
        }
        if (u.getDesignation() != null && u.getDesignation().getValue() != null) {
            sb.append(u.getDesignation().getValue());
        }
        return sb.toString();
    }

    private String getEntityKey(ProcessFlowDTO.Entities e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (e.getType() != null) {
            sb.append(e.getType()).append(":");
        }
        if (e.getEntity() != null && e.getEntity().getValue() != null) {
            sb.append(e.getEntity().getValue()).append(":");
        }
        if (e.getEntityLevel() != null && e.getEntityLevel().getValue() != null) {
            sb.append(e.getEntityLevel().getValue()).append(":");
        }
        if (e.getClcDetail() != null && e.getClcDetail().getValue() != null) {
            sb.append(e.getClcDetail().getValue()).append(":");
        }
        if (e.getCategory() != null && e.getCategory().getValue() != null) {
            sb.append(e.getCategory().getValue());
        }
        return sb.toString();
    }

    /**
     * Records detailed Process Flow changes into service_version_change_log with "old was this, new is this" structure.
     */
    public void recordProcessFlowChangeLog(Integer serviceId, ProcessFlowDTO fallbackOldDto, ProcessFlowDTO newDto, UserSessionDTO userSession) {
        if (!isMinorModification(serviceId)) {
            return;
        }
        try {
            Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
            ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

            ProcessFlowDTO baseline = getBaselineProcessFlow(serviceId);
            ProcessFlowDTO oldDto = (baseline != null) ? baseline : fallbackOldDto;

            List<ChangeItemDTO> changeItems = computeProcessFlowDiff(oldDto, newDto);
            if (changeItems.isEmpty()) {
                logger.info("No detectable differences in process flow for serviceId={}", serviceId);
                return;
            }

            List<String> summaries = changeItems.stream()
                    .map(ChangeItemDTO::getDescription)
                    .collect(Collectors.toList());

            ServiceVersionChangeLog changeLog = new ServiceVersionChangeLog();
            changeLog.setServiceId(serviceId);
            changeLog.setBaseServiceId(serviceId / 10000);
            changeLog.setComponent(ServiceComponent.PROCESS_FLOW);
            changeLog.setAction(oldDto == null ? "CREATE" : "UPDATE");
            changeLog.setChangeSummary(String.join("\n", summaries));
            changeLog.setPreviousConfig(oldDto != null ? objectMapper.writeValueAsString(oldDto) : null);
            changeLog.setCurrentConfig(newDto != null ? objectMapper.writeValueAsString(newDto) : null);
            changeLog.setDiffDetails(objectMapper.writeValueAsString(changeItems));
            changeLog.setCreatedAt(new Date());

            if (userSession != null) {
                changeLog.setUserId(userSession.getUserID());
                changeLog.setTenantId(userSession.getTenantId());
            }

            if (activeOpt.isPresent()) {
                ServiceVersionChange change = activeOpt.get();
                changeLog.setVersionChangeId(change.getId());
                changeLog.setModificationToken(change.getModificationToken());
                changeLog.setVersionNo(change.getTargetVersionNo());
                changeLog.setMinorVersionNo(change.getTargetMinorVersionNo());
            } else if (serviceLog != null) {
                changeLog.setVersionNo(serviceLog.getVersionNo());
                changeLog.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
            } else {
                changeLog.setVersionNo(serviceId % 10000);
                changeLog.setMinorVersionNo(0);
            }

            changeLogRepository.save(changeLog);
            logger.info("Recorded Process Flow change log for serviceId={}, changeCount={}", serviceId, changeItems.size());
        } catch (Exception ex) {
            logger.error("Error recording Process Flow change log for serviceId={}", serviceId, ex);
        }
    }

    /**
     * Records detailed Service Delivery Unit changes into service_version_change_log.
     */
    public void recordDeliveryUnitChangeLog(Integer serviceId,
                                            List<ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON> fallbackOldUnits,
                                            List<ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON> newUnits,
                                            String fallbackOldSource,
                                            String newSource,
                                            UserSessionDTO userSession) {
        try {
            Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
            ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

            ServiceDeliveryUnitDefinition baseline = getBaselineDeliveryUnitDefinition(serviceId);
            List<ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON> oldUnits = (baseline != null && baseline.getServiceDeliveryUnits() != null)
                    ? baseline.getServiceDeliveryUnits() : fallbackOldUnits;
            String oldSource = (baseline != null && baseline.getSource() != null)
                    ? baseline.getSource() : fallbackOldSource;

            List<ChangeItemDTO> changeItems = new ArrayList<>();

            if (!Objects.equals(oldSource, newSource)) {
                changeItems.add(new ChangeItemDTO(
                        "Service Delivery Unit",
                        "SDU Source",
                        "Source",
                        oldSource,
                        newSource,
                        "SDU source changed from '" + oldSource + "' to '" + newSource + "'"
                ));
            }

            Set<String> oldKeys = (oldUnits != null) ? oldUnits.stream().map(this::getDeliveryUnitKey).collect(Collectors.toSet()) : Collections.emptySet();

            if (newUnits != null) {
                for (ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON nu : newUnits) {
                    String k = getDeliveryUnitKey(nu);
                    if (!oldKeys.contains(k)) {
                        String label = (nu.getServiceDeliveryUnit() != null) ? nu.getServiceDeliveryUnit().getLabel() : k;
                        changeItems.add(new ChangeItemDTO(
                                "Service Delivery Unit",
                                "Delivery Unit Mapping",
                                "SDU Added",
                                null,
                                label,
                                "Added Service Delivery Unit: " + label
                        ));
                    }
                }
            }

            if (changeItems.isEmpty()) {
                return;
            }

            List<String> summaries = changeItems.stream()
                    .map(ChangeItemDTO::getDescription)
                    .collect(Collectors.toList());

            ServiceVersionChangeLog changeLog = new ServiceVersionChangeLog();
            changeLog.setServiceId(serviceId);
            changeLog.setBaseServiceId(serviceId / 10000);
            changeLog.setComponent(ServiceComponent.DELIVERY_UNIT);
            changeLog.setAction("UPDATE");
            changeLog.setChangeSummary(String.join("\n", summaries));
            changeLog.setPreviousConfig(oldUnits != null ? objectMapper.writeValueAsString(oldUnits) : null);
            changeLog.setCurrentConfig(newUnits != null ? objectMapper.writeValueAsString(newUnits) : null);
            changeLog.setDiffDetails(objectMapper.writeValueAsString(changeItems));
            changeLog.setCreatedAt(new Date());

            if (userSession != null) {
                changeLog.setUserId(userSession.getUserID());
                changeLog.setTenantId(userSession.getTenantId());
            }

            if (activeOpt.isPresent()) {
                ServiceVersionChange change = activeOpt.get();
                changeLog.setVersionChangeId(change.getId());
                changeLog.setModificationToken(change.getModificationToken());
                changeLog.setVersionNo(change.getTargetVersionNo());
                changeLog.setMinorVersionNo(change.getTargetMinorVersionNo());
            } else if (serviceLog != null) {
                changeLog.setVersionNo(serviceLog.getVersionNo());
                changeLog.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
            } else {
                changeLog.setVersionNo(serviceId % 10000);
                changeLog.setMinorVersionNo(0);
            }

            changeLogRepository.save(changeLog);
            logger.info("Recorded Delivery Unit change log for serviceId={}", serviceId);
        } catch (Exception ex) {
            logger.error("Error recording Delivery Unit change log for serviceId={}", serviceId, ex);
        }
    }

    /**
     * Records MVEL Function changes into service_version_change_log.
     */
    public void recordMvelChangeLog(Integer serviceId, String action, String functionName, MvelFunctionDTO oldRequest, MvelFunctionDTO newRequest, UserSessionDTO userSession) {
        try {
            Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
            ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

            List<ChangeItemDTO> changeItems = new ArrayList<>();
            String description = "MVEL Function '" + functionName + "' " + ("CREATE".equalsIgnoreCase(action) ? "created" : "updated");
            changeItems.add(new ChangeItemDTO(
                    "Rules & MVEL",
                    "MVEL Function: " + functionName,
                    "Function Body",
                    oldRequest != null ? oldRequest.getFunctionBody() : null,
                    newRequest != null ? newRequest.getFunctionBody() : null,
                    description
            ));

            ServiceVersionChangeLog changeLog = new ServiceVersionChangeLog();
            changeLog.setServiceId(serviceId);
            changeLog.setBaseServiceId(serviceId / 10000);
            changeLog.setComponent(ServiceComponent.MVEL_FUNCTION);
            changeLog.setAction(action);
            changeLog.setChangeSummary(description);
            changeLog.setPreviousConfig(oldRequest != null ? objectMapper.writeValueAsString(oldRequest) : null);
            changeLog.setCurrentConfig(newRequest != null ? objectMapper.writeValueAsString(newRequest) : null);
            changeLog.setDiffDetails(objectMapper.writeValueAsString(changeItems));
            changeLog.setCreatedAt(new Date());

            if (userSession != null) {
                changeLog.setUserId(userSession.getUserID());
                changeLog.setTenantId(userSession.getTenantId());
            }

            if (activeOpt.isPresent()) {
                ServiceVersionChange change = activeOpt.get();
                changeLog.setVersionChangeId(change.getId());
                changeLog.setModificationToken(change.getModificationToken());
                changeLog.setVersionNo(change.getTargetVersionNo());
                changeLog.setMinorVersionNo(change.getTargetMinorVersionNo());
            } else if (serviceLog != null) {
                changeLog.setVersionNo(serviceLog.getVersionNo());
                changeLog.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
            } else {
                changeLog.setVersionNo(serviceId % 10000);
                changeLog.setMinorVersionNo(0);
            }

            changeLogRepository.save(changeLog);
            logger.info("Recorded MVEL Function change log for serviceId={}, function={}", serviceId, functionName);
        } catch (Exception ex) {
            logger.error("Error recording MVEL Function change log for serviceId={}", serviceId, ex);
        }
    }

    /**
     * Common helper to build and persist ServiceVersionChangeLog.
     */
    private void saveChangeLog(Integer serviceId,
                               ServiceComponent component,
                               String action,
                               String summary,
                               Object prevConfig,
                               Object currConfig,
                               List<ChangeItemDTO> changeItems,
                               UserSessionDTO userSession) {
        if (!isMinorModification(serviceId)) {
            return;
        }
        try {
            Optional<ServiceVersionChange> activeOpt = findActiveModification(serviceId);
            ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

            ServiceVersionChangeLog changeLog = new ServiceVersionChangeLog();
            changeLog.setServiceId(serviceId);
            changeLog.setBaseServiceId(serviceId != null ? serviceId / 10000 : null);
            changeLog.setComponent(component);
            changeLog.setAction(action);
            changeLog.setChangeSummary(summary);
            changeLog.setPreviousConfig(prevConfig != null ? (prevConfig instanceof String ? (String) prevConfig : objectMapper.writeValueAsString(prevConfig)) : null);
            changeLog.setCurrentConfig(currConfig != null ? (currConfig instanceof String ? (String) currConfig : objectMapper.writeValueAsString(currConfig)) : null);
            changeLog.setDiffDetails(changeItems != null && !changeItems.isEmpty() ? objectMapper.writeValueAsString(changeItems) : null);
            changeLog.setCreatedAt(new Date());

            if (userSession != null) {
                changeLog.setUserId(userSession.getUserID());
                changeLog.setTenantId(userSession.getTenantId());
            }

            if (activeOpt.isPresent()) {
                ServiceVersionChange change = activeOpt.get();
                changeLog.setVersionChangeId(change.getId());
                changeLog.setModificationToken(change.getModificationToken());
                changeLog.setVersionNo(change.getTargetVersionNo());
                changeLog.setMinorVersionNo(change.getTargetMinorVersionNo());
            } else if (serviceLog != null) {
                changeLog.setVersionNo(serviceLog.getVersionNo());
                changeLog.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
            } else if (serviceId != null) {
                changeLog.setVersionNo(serviceId % 10000);
                changeLog.setMinorVersionNo(0);
            }

            changeLogRepository.save(changeLog);
            logger.info("Saved change log for serviceId={}, component={}, action={}, changesCount={}",
                    serviceId, component, action, changeItems != null ? changeItems.size() : 0);
        } catch (Exception ex) {
            logger.error("Error saving change log for serviceId={}, component={}", serviceId, component, ex);
        }
    }

    /**
     * Records Service Definition changes into service_version_change_log.
     */
    public void recordDefinitionChangeLog(Integer serviceId, ServiceDefinitionDTO fallbackOldDto, ServiceDefinitionDTO newDto, UserSessionDTO userSession) {
        ServiceDefinitionDTO baseline = getBaselineServiceDefinition(serviceId);
        ServiceDefinitionDTO oldDto = (baseline != null) ? baseline : fallbackOldDto;

        List<ChangeItemDTO> changeItems = new ArrayList<>();
        String category = "Service Definition";
        String item = "General & Service Details";

        if (oldDto == null) {
            changeItems.add(new ChangeItemDTO(category, item, "Service Definition", null, newDto != null ? newDto.getName() : null, "Created initial service definition"));
        } else if (newDto != null) {
            if (!Objects.equals(oldDto.getName(), newDto.getName())) {
                changeItems.add(new ChangeItemDTO(category, item, "Service Name", oldDto.getName(), newDto.getName(), "Service Name changed from '" + oldDto.getName() + "' to '" + newDto.getName() + "'"));
            }
            if (!Objects.equals(oldDto.getAbbreviation(), newDto.getAbbreviation())) {
                changeItems.add(new ChangeItemDTO(category, item, "Abbreviation", oldDto.getAbbreviation(), newDto.getAbbreviation(), "Abbreviation changed from '" + oldDto.getAbbreviation() + "' to '" + newDto.getAbbreviation() + "'"));
            }
            if (!Objects.equals(oldDto.getDescription(), newDto.getDescription())) {
                changeItems.add(new ChangeItemDTO(category, item, "Description", oldDto.getDescription(), newDto.getDescription(), "Description updated"));
            }
            String oldCat = (oldDto.getCategory() != null) ? oldDto.getCategory().getLabel() : null;
            String newCat = (newDto.getCategory() != null) ? newDto.getCategory().getLabel() : null;
            if (!Objects.equals(oldCat, newCat)) {
                changeItems.add(new ChangeItemDTO(category, item, "Service Category", oldCat, newCat, "Category changed from '" + oldCat + "' to '" + newCat + "'"));
            }
            String oldType = (oldDto.getType() != null) ? oldDto.getType().getLabel() : null;
            String newType = (newDto.getType() != null) ? newDto.getType().getLabel() : null;
            if (!Objects.equals(oldType, newType)) {
                changeItems.add(new ChangeItemDTO(category, item, "Type Of Service", oldType, newType, "Type Of Service changed from '" + oldType + "' to '" + newType + "'"));
            }
//            if (!Objects.equals(oldDto.getRegisterToAvail(), newDto.getRegisterToAvail())) {
//                changeItems.add(new ChangeItemDTO(category, item, "Register to Avail", oldDto.getRegisterToAvail(), newDto.getRegisterToAvail(), "Register to avail changed from " + oldDto.getRegisterToAvail() + " to " + newDto.getRegisterToAvail()));
//            }
            if (!Objects.equals(oldDto.getServiceDeliveryAct(), newDto.getServiceDeliveryAct())) {
                changeItems.add(new ChangeItemDTO(category, item, "Service Delivery Act", oldDto.getServiceDeliveryAct(), newDto.getServiceDeliveryAct(), "Service Delivery Act changed from " + oldDto.getServiceDeliveryAct() + " to " + newDto.getServiceDeliveryAct()));
            }
            if (!Objects.equals(oldDto.getFifo(), newDto.getFifo())) {
                changeItems.add(new ChangeItemDTO(category, item, "FIFO", oldDto.getFifo(), newDto.getFifo(), "FIFO changed from " + oldDto.getFifo() + " to " + newDto.getFifo()));
            }
            if (!Objects.equals(oldDto.geteKYC(), newDto.geteKYC())) {
                changeItems.add(new ChangeItemDTO(category, item, "eKYC", oldDto.geteKYC(), newDto.geteKYC(), "eKYC changed from " + oldDto.geteKYC() + " to " + newDto.geteKYC()));
            }
            if (!Objects.equals(oldDto.getFeedback(), newDto.getFeedback())) {
                changeItems.add(new ChangeItemDTO(category, item, "Feedback", oldDto.getFeedback(), newDto.getFeedback(), "Feedback changed from " + oldDto.getFeedback() + " to " + newDto.getFeedback()));
            }
            if (!Objects.equals(oldDto.getAutoAppeal(), newDto.getAutoAppeal())) {
                changeItems.add(new ChangeItemDTO(category, item, "Auto Appeal", oldDto.getAutoAppeal(), newDto.getAutoAppeal(), "Auto Appeal changed from " + oldDto.getAutoAppeal() + " to " + newDto.getAutoAppeal()));
            }
            String oldKw = (oldDto.getKeywords() != null) ? String.join(", ", oldDto.getKeywords()) : null;
            String newKw = (newDto.getKeywords() != null) ? String.join(", ", newDto.getKeywords()) : null;
            if (!Objects.equals(oldKw, newKw)) {
                changeItems.add(new ChangeItemDTO(category, item, "Keywords", oldKw, newKw, "Keywords changed from [" + oldKw + "] to [" + newKw + "]"));
            }
            if (!Objects.equals(oldDto.getLogo(), newDto.getLogo())) {
                changeItems.add(new ChangeItemDTO(category, item, "Logo", oldDto.getLogo(), newDto.getLogo(), "Service logo updated"));
            }
        }

        if (changeItems.isEmpty()) {
            return;
        }

        String summary = changeItems.stream().map(ChangeItemDTO::getDescription).collect(Collectors.joining("\n"));
        saveChangeLog(serviceId, ServiceComponent.SERVICE_DEFINITION, oldDto == null ? "CREATE" : "UPDATE", summary, oldDto, newDto, changeItems, userSession);
    }

    /**
     * Records Output Format changes into service_version_change_log.
     */
    public void recordOutputFormatChangeLog(Integer serviceId, String action, String templateName, ServiceOutputFormatDTO fallbackOldDto, ServiceOutputFormatDTO newDto, UserSessionDTO userSession) {
        Long outputFormatId = (newDto != null) ? newDto.getOutputFormatId() : (fallbackOldDto != null ? fallbackOldDto.getOutputFormatId() : null);
        ServiceOutputFormatDTO baseline = (outputFormatId != null) ? getBaselineOutputFormat(serviceId, outputFormatId) : null;
        ServiceOutputFormatDTO oldDto = (baseline != null) ? baseline : fallbackOldDto;

        List<ChangeItemDTO> changeItems = new ArrayList<>();
        String category = "Output Format";
        String item = "Template: " + (templateName != null ? templateName : (newDto != null ? newDto.getTemplateName() : (oldDto != null ? oldDto.getTemplateName() : "Output Format")));

        if ("DELETE".equalsIgnoreCase(action)) {
            changeItems.add(new ChangeItemDTO(category, item, "Document Template", templateName, null, "Deleted document template: " + templateName));
        } else if (oldDto == null) {
            changeItems.add(new ChangeItemDTO(category, item, "Document Template", null, templateName, "Created new document template: " + templateName));
        } else if (newDto != null) {
            if (!Objects.equals(oldDto.getTemplateName(), newDto.getTemplateName())) {
                changeItems.add(new ChangeItemDTO(category, item, "Template Name", oldDto.getTemplateName(), newDto.getTemplateName(), "Template name changed from '" + oldDto.getTemplateName() + "' to '" + newDto.getTemplateName() + "'"));
            }
            if (!Objects.equals(oldDto.getDocumentType(), newDto.getDocumentType())) {
                changeItems.add(new ChangeItemDTO(category, item, "Document Type", oldDto.getDocumentType(), newDto.getDocumentType(), "Document type changed from '" + oldDto.getDocumentType() + "' to '" + newDto.getDocumentType() + "'"));
            }
            if (!Objects.equals(oldDto.getLayout(), newDto.getLayout())) {
                changeItems.add(new ChangeItemDTO(category, item, "Layout", oldDto.getLayout(), newDto.getLayout(), "Layout changed from " + oldDto.getLayout() + " to " + newDto.getLayout()));
            }
            if (!Objects.equals(oldDto.getPageSize(), newDto.getPageSize())) {
                changeItems.add(new ChangeItemDTO(category, item, "Page Size", oldDto.getPageSize(), newDto.getPageSize(), "Page size changed from '" + oldDto.getPageSize() + "' to '" + newDto.getPageSize() + "'"));
            }
            if (!Objects.equals(oldDto.getWaterMark(), newDto.getWaterMark())) {
                changeItems.add(new ChangeItemDTO(category, item, "Watermark", oldDto.getWaterMark(), newDto.getWaterMark(), "Watermark changed from '" + oldDto.getWaterMark() + "' to '" + newDto.getWaterMark() + "'"));
            }
            if (!Objects.equals(oldDto.getTemplateBodySrc(), newDto.getTemplateBodySrc())) {
                changeItems.add(new ChangeItemDTO(category, item, "Template Body", "Old Content", "Updated Content", "Template body source content modified"));
            }
            if (!Objects.equals(oldDto.getUsedAttr(), newDto.getUsedAttr())) {
                changeItems.add(new ChangeItemDTO(category, item, "Used Attributes", oldDto.getUsedAttr(), newDto.getUsedAttr(), "Used attributes mapping updated"));
            }
            if (!Objects.equals(oldDto.getNotificationType(), newDto.getNotificationType())) {
                changeItems.add(new ChangeItemDTO(category, item, "Notification Type", oldDto.getNotificationType(), newDto.getNotificationType(), "Notification type changed"));
            }
            if (!Objects.equals(oldDto.getMailSubject(), newDto.getMailSubject())) {
                changeItems.add(new ChangeItemDTO(category, item, "Mail Subject", oldDto.getMailSubject(), newDto.getMailSubject(), "Mail subject changed"));
            }
        }

        if (changeItems.isEmpty()) {
            changeItems.add(new ChangeItemDTO(category, item, "Document Template", null, null, "Updated document template configuration"));
        }

        String summary = changeItems.stream().map(ChangeItemDTO::getDescription).collect(Collectors.joining("\n"));
        saveChangeLog(serviceId, ServiceComponent.OUTPUT_FORMAT, action, summary, oldDto, newDto, changeItems, userSession);
    }

    /**
     * Records Service Charge changes into service_version_change_log.
     */
    public void recordServiceChargeChangeLog(Integer serviceId, String action, String templateName, ServiceChargeDTO fallbackOldDto, ServiceChargeDTO newDto, UserSessionDTO userSession) {
        Long chargeId = (newDto != null) ? newDto.getChargeId() : (fallbackOldDto != null ? fallbackOldDto.getChargeId() : null);
        ServiceChargeDTO baseline = (chargeId != null) ? getBaselineServiceCharge(serviceId, chargeId) : null;
        ServiceChargeDTO oldDto = (baseline != null) ? baseline : fallbackOldDto;

        List<ChangeItemDTO> changeItems = new ArrayList<>();
        String category = "Payment Receipt Configuration";
        String item = "Charge Template: " + (templateName != null ? templateName : (newDto != null ? newDto.getTemplateName() : (oldDto != null ? oldDto.getTemplateName() : "Charges")));

        if (oldDto == null) {
            changeItems.add(new ChangeItemDTO(category, item, "Charge Template", null, templateName, "Created new charge template: " + templateName));
        } else if (newDto != null) {
            if (!Objects.equals(oldDto.getTemplateName(), newDto.getTemplateName())) {
                changeItems.add(new ChangeItemDTO(category, item, "Template Name", oldDto.getTemplateName(), newDto.getTemplateName(), "Template name changed from '" + oldDto.getTemplateName() + "' to '" + newDto.getTemplateName() + "'"));
            }
            if (!Objects.equals(oldDto.getServiceUnitWise(), newDto.getServiceUnitWise())) {
                changeItems.add(new ChangeItemDTO(category, item, "Service Unit Wise", oldDto.getServiceUnitWise(), newDto.getServiceUnitWise(), "Service Unit-wise charge definition changed from " + oldDto.getServiceUnitWise() + " to " + newDto.getServiceUnitWise()));
            }
            if (!Objects.equals(oldDto.getDefaultCharge(), newDto.getDefaultCharge())) {
                changeItems.add(new ChangeItemDTO(category, item, "Default Charge", oldDto.getDefaultCharge(), newDto.getDefaultCharge(), "Default charge changed from " + oldDto.getDefaultCharge() + " to " + newDto.getDefaultCharge()));
            }
            if (!Objects.equals(oldDto.getPaymentModes(), newDto.getPaymentModes())) {
                changeItems.add(new ChangeItemDTO(category, item, "Payment Modes", oldDto.getPaymentModes(), newDto.getPaymentModes(), "Payment modes updated"));
            }
            if (!Objects.equals(oldDto.getChargeDefinedFor(), newDto.getChargeDefinedFor())) {
                changeItems.add(new ChangeItemDTO(category, item, "Charge Defined For", oldDto.getChargeDefinedFor(), newDto.getChargeDefinedFor(), "Charge definition targets updated"));
            }
            if (!Objects.equals(oldDto.getChargeTypeList(), newDto.getChargeTypeList())) {
                changeItems.add(new ChangeItemDTO(category, item, "Charge Type List", oldDto.getChargeTypeList(), newDto.getChargeTypeList(), "Charge types details modified"));
            }
        }

        if (changeItems.isEmpty()) {
            changeItems.add(new ChangeItemDTO(category, item, "Charge Template", null, null, "Updated charge template configuration"));
        }

        String summary = changeItems.stream().map(ChangeItemDTO::getDescription).collect(Collectors.joining("\n"));
        saveChangeLog(serviceId, ServiceComponent.SERVICE_CHARGE, action, summary, oldDto, newDto, changeItems, userSession);
    }

    /**
     * Records Payment Gateway changes into service_version_change_log.
     */
    public void recordPaymentGatewayChangeLog(Integer serviceId, String gatewayCode, ServicePaymentGatewayConfig oldConfig, PaymentGatewayMappingRequest newRequest, UserSessionDTO userSession) {
        List<ChangeItemDTO> changeItems = new ArrayList<>();
        String category = "Payment Receipt Configuration";
        String item = "Payment Gateway: " + gatewayCode;

        Boolean oldEnabled = (oldConfig != null) ? oldConfig.getEnabled() : null;
        Boolean newEnabled = (newRequest != null) ? newRequest.getEnabled() : null;

        if (oldConfig == null) {
            changeItems.add(new ChangeItemDTO(category, item, "Payment Gateway", null, gatewayCode, "Mapped new payment gateway: " + gatewayCode));
        } else {
            if (!Objects.equals(oldEnabled, newEnabled)) {
                changeItems.add(new ChangeItemDTO(category, item, "Status", oldEnabled, newEnabled, "Payment gateway " + gatewayCode + " status changed to " + (Boolean.TRUE.equals(newEnabled) ? "Enabled" : "Disabled")));
            }
            if (newRequest != null && newRequest.getMappings() != null) {
                changeItems.add(new ChangeItemDTO(category, item, "Gateway Mappings", null, newRequest.getMappings(), "Updated payment gateway parameters/mappings for " + gatewayCode));
            }
        }

        String summary = changeItems.stream().map(ChangeItemDTO::getDescription).collect(Collectors.joining("\n"));
        saveChangeLog(serviceId, ServiceComponent.PAYMENT_GATEWAY_CONFIG, oldConfig == null ? "CREATE" : "UPDATE", summary, oldConfig != null ? oldConfig.getMappingJson() : null, newRequest, changeItems, userSession);
    }

    /**
     * Records Aadhaar / AUA Mapping changes into service_version_change_log.
     */
    public void recordAuaMappingChangeLog(Integer serviceId, String configurationId, String apiType, SaveAuaApiMappingRequest request, UserSessionDTO userSession) {
        List<ChangeItemDTO> changeItems = new ArrayList<>();
        String category = "Additional Configuration";
        String item = "Aadhaar Config (" + apiType + ")";

        int reqCount = (request != null && request.getRequestMappings() != null) ? request.getRequestMappings().size() : 0;
        int resCount = (request != null && request.getResponseMappings() != null) ? request.getResponseMappings().size() : 0;

        changeItems.add(new ChangeItemDTO(
                category,
                item,
                "Aadhaar Field Mappings",
                null,
                "Request: " + reqCount + ", Response: " + resCount,
                "Configured Aadhaar " + apiType + " mappings (configurationId=" + configurationId + ", " + reqCount + " request fields, " + resCount + " response fields)"
        ));

        String summary = "Updated Aadhaar " + apiType + " configuration";
        saveChangeLog(serviceId, ServiceComponent.AUA_MAPPING, "UPDATE", summary, null, request, changeItems, userSession);
    }

    /**
     * Records Correlation Configuration changes into service_version_change_log.
     */
    public void recordCorrelationChangeLog(Integer serviceId, String action, String setName, ServiceCorrelationRequestDTO oldDto, ServiceCorrelationRequestDTO newDto, UserSessionDTO userSession) {
        List<ChangeItemDTO> changeItems = new ArrayList<>();
        String category = "Correlation Identifier";
        String item = "Correlation Set: " + (setName != null ? setName : (newDto != null ? newDto.getSetName() : (oldDto != null ? oldDto.getSetName() : "Correlation")));

        if ("DELETE".equalsIgnoreCase(action)) {
            changeItems.add(new ChangeItemDTO(category, item, "Correlation Set", setName, null, "Deleted correlation set: " + setName));
        } else if (oldDto == null) {
            changeItems.add(new ChangeItemDTO(category, item, "Correlation Set", null, setName, "Created new correlation set: " + setName));
        } else if (newDto != null) {
            if (!Objects.equals(oldDto.getSetName(), newDto.getSetName())) {
                changeItems.add(new ChangeItemDTO(category, item, "Set Name", oldDto.getSetName(), newDto.getSetName(), "Correlation set name changed from '" + oldDto.getSetName() + "' to '" + newDto.getSetName() + "'"));
            }
            if (!Objects.equals(oldDto.getSecurityKey(), newDto.getSecurityKey())) {
                changeItems.add(new ChangeItemDTO(category, item, "Security Key", oldDto.getSecurityKey(), newDto.getSecurityKey(), "Correlation security key updated"));
            }
            if (!Objects.equals(oldDto.getIdentifierConfigure(), newDto.getIdentifierConfigure())) {
                changeItems.add(new ChangeItemDTO(category, item, "Identifier Configuration", oldDto.getIdentifierConfigure(), newDto.getIdentifierConfigure(), "Correlation attributes configuration modified"));
            }
        }

        if (changeItems.isEmpty()) {
            changeItems.add(new ChangeItemDTO(category, item, "Correlation Set", null, null, "Updated correlation identifier configuration"));
        }

        String summary = changeItems.stream().map(ChangeItemDTO::getDescription).collect(Collectors.joining("\n"));
        saveChangeLog(serviceId, ServiceComponent.CORRELATION_CONFIG, action, summary, oldDto, newDto, changeItems, userSession);
    }

    /**
     * Computes field-by-field differences ("old was this, new is this") between two ProcessFlowDTO objects.
     */
    private List<ChangeItemDTO> computeProcessFlowDiff(ProcessFlowDTO oldDto, ProcessFlowDTO newDto) {
        List<ChangeItemDTO> changeItems = new ArrayList<>();

        if (oldDto == null) {
            int nodeCount = (newDto != null && newDto.getNodes() != null) ? newDto.getNodes().size() : 0;
            changeItems.add(new ChangeItemDTO(
                    "Process Flow",
                    "Process Flow Definition",
                    "Initial Creation",
                    null,
                    nodeCount + " tasks configured",
                    "Created initial process flow with " + nodeCount + " tasks."
            ));
            return changeItems;
        }

        Map<String, ProcessFlowDTO.NodeDTO> oldNodeMap = (oldDto.getNodes() != null) ?
                oldDto.getNodes().stream().collect(Collectors.toMap(ProcessFlowDTO.NodeDTO::getId, Function.identity(), (a, b) -> a)) :
                Collections.emptyMap();

        Map<String, ProcessFlowDTO.NodeDTO> newNodeMap = (newDto != null && newDto.getNodes() != null) ?
                newDto.getNodes().stream().collect(Collectors.toMap(ProcessFlowDTO.NodeDTO::getId, Function.identity(), (a, b) -> a)) :
                Collections.emptyMap();

        // 1. Check newly added tasks
        for (Map.Entry<String, ProcessFlowDTO.NodeDTO> entry : newNodeMap.entrySet()) {
            String nodeId = entry.getKey();
            ProcessFlowDTO.NodeDTO newNode = entry.getValue();

            if (!oldNodeMap.containsKey(nodeId)) {
                String taskName = (newNode.getData() != null && newNode.getData().getName() != null) ?
                        newNode.getData().getName() : nodeId;
                String type = String.valueOf(newNode.getType());
                changeItems.add(new ChangeItemDTO(
                        "Task",
                        "Task: " + taskName + " (ID: " + nodeId + ")",
                        "New Task Added",
                        null,
                        "Type: " + type + ", Name: " + taskName,
                        "Added new task '" + taskName + "' (ID: " + nodeId + ", Type: " + type + ")"
                ));

                if (newNode.getData() != null && newNode.getData().getDocumentMapping() != null && !newNode.getData().getDocumentMapping().isEmpty()) {
                    compareDocumentMappings(Collections.emptyList(), newNode.getData().getDocumentMapping(), "Task: " + taskName + " (ID: " + nodeId + ")", taskName, changeItems);
                }
            }
        }

        // 2. Check changes on existing nodes
        for (Map.Entry<String, ProcessFlowDTO.NodeDTO> entry : oldNodeMap.entrySet()) {
            String nodeId = entry.getKey();
            ProcessFlowDTO.NodeDTO oldNode = entry.getValue();
            ProcessFlowDTO.NodeDTO newNode = newNodeMap.get(nodeId);

            if (newNode == null) {
                continue;
            }

            String taskName = (newNode.getData() != null && newNode.getData().getName() != null) ?
                    newNode.getData().getName() :
                    ((oldNode.getData() != null && oldNode.getData().getName() != null) ? oldNode.getData().getName() : nodeId);

            String itemLabel = "Task: " + taskName + " (ID: " + nodeId + ")";

            // Submission node checks
            if (ApplicationConstants.SUBMISSION_NODE_TYPE.equalsIgnoreCase(oldNode.getType()) || "submission".equalsIgnoreCase(nodeId)) {
                itemLabel = "Service Initiation Task (Submission)";
                compareSubmissionNode(oldNode.getData(), newNode.getData(), itemLabel, changeItems);
            }

            if (oldNode.getData() != null && newNode.getData() != null) {
                // Name
                String oldName = oldNode.getData().getName();
                String newName = newNode.getData().getName();
                if (!Objects.equals(oldName, newName)) {
                    changeItems.add(new ChangeItemDTO(
                            "Task Configuration",
                            itemLabel,
                            "Task Name",
                            oldName,
                            newName,
                            "Task '" + taskName + "' name changed from '" + oldName + "' to '" + newName + "'"
                    ));
                }

                // Description
                String oldDesc = oldNode.getData().getDescription();
                String newDesc = newNode.getData().getDescription();
                if (!Objects.equals(oldDesc, newDesc)) {
                    changeItems.add(new ChangeItemDTO(
                            "Task Configuration",
                            itemLabel,
                            "Description",
                            oldDesc,
                            newDesc,
                            "Task '" + taskName + "' description changed from '" + oldDesc + "' to '" + newDesc + "'"
                    ));
                }

                // DEO Submission location containment
                if (oldNode.getData().isDeoSubmissionContainedWithinLocation() != newNode.getData().isDeoSubmissionContainedWithinLocation()) {
                    changeItems.add(new ChangeItemDTO(
                            "Task Configuration",
                            itemLabel,
                            "DEO Location Containment",
                            oldNode.getData().isDeoSubmissionContainedWithinLocation(),
                            newNode.getData().isDeoSubmissionContainedWithinLocation(),
                            "Task '" + taskName + "': DEO submission contained within location changed to " + (newNode.getData().isDeoSubmissionContainedWithinLocation() ? "Enabled" : "Disabled")
                    ));
                }

                // Form mapping (adding form on newly added task / template changes / subscription mode)
                ProcessFlowDTO.FormDetail oldFd = oldNode.getData().getFormDetail();
                ProcessFlowDTO.FormDetail newFd = newNode.getData().getFormDetail();
                String oldFid = oldFd != null ? oldFd.getFormId() : null;
                String newFid = newFd != null ? newFd.getFormId() : null;
                String oldTpl = oldFd != null ? oldFd.getTemplateName() : null;
                String newTpl = newFd != null ? newFd.getTemplateName() : null;
                if (!Objects.equals(oldFid, newFid) || !Objects.equals(oldTpl, newTpl)) {
                    String oldFormDesc = (oldTpl != null ? oldTpl : "None") + (oldFid != null ? " (ID: " + oldFid + ")" : "");
                    String newFormDesc = (newTpl != null ? newTpl : "None") + (newFid != null ? " (ID: " + newFid + ")" : "");
                    changeItems.add(new ChangeItemDTO(
                            "Actions & UI",
                            itemLabel,
                            "Form Mapping",
                            oldFormDesc,
                            newFormDesc,
                            "Task '" + taskName + "' form mapping changed from '" + oldFormDesc + "' to '" + newFormDesc + "'"
                    ));
                }
                if (oldFd != null && newFd != null) {
                    if (!Objects.equals(oldFd.getSubscriptionMode(), newFd.getSubscriptionMode())) {
                        changeItems.add(new ChangeItemDTO(
                                "Actions & UI",
                                itemLabel,
                                "Form Subscription Mode",
                                oldFd.getSubscriptionMode(),
                                newFd.getSubscriptionMode(),
                                "Task '" + taskName + "': Form subscription mode changed from '" + oldFd.getSubscriptionMode() + "' to '" + newFd.getSubscriptionMode() + "'"
                        ));
                    }
                    if (!Objects.equals(oldFd.getIsSubscribed(), newFd.getIsSubscribed())) {
                        changeItems.add(new ChangeItemDTO(
                                "Actions & UI",
                                itemLabel,
                                "Form Subscription Status",
                                oldFd.getIsSubscribed(),
                                newFd.getIsSubscribed(),
                                "Task '" + taskName + "': Form subscription status changed to " + (Boolean.TRUE.equals(newFd.getIsSubscribed()) ? "Subscribed" : "Unsubscribed")
                        ));
                    }
                }

                // SDU Entities: Added, Removed, and Designation changes
                List<ProcessFlowDTO.Entities> oldEntities = oldNode.getData().getEntities();
                List<ProcessFlowDTO.Entities> newEntities = newNode.getData().getEntities();
                Map<String, ProcessFlowDTO.Entities> oldEntityMap = (oldEntities != null) ?
                        oldEntities.stream().filter(Objects::nonNull).collect(Collectors.toMap(this::getEntityKey, Function.identity(), (a, b) -> a)) : Collections.emptyMap();
                Map<String, ProcessFlowDTO.Entities> newEntityMap = (newEntities != null) ?
                        newEntities.stream().filter(Objects::nonNull).collect(Collectors.toMap(this::getEntityKey, Function.identity(), (a, b) -> a)) : Collections.emptyMap();

                // SDU Entities Added
                for (Map.Entry<String, ProcessFlowDTO.Entities> entEntry : newEntityMap.entrySet()) {
                    if (!oldEntityMap.containsKey(entEntry.getKey())) {
                        ProcessFlowDTO.Entities ne = entEntry.getValue();
                        String entityLabel = (ne.getEntity() != null && ne.getEntity().getLabel() != null) ? ne.getEntity().getLabel() : entEntry.getKey();
                        changeItems.add(new ChangeItemDTO(
                                "Task Configuration",
                                itemLabel,
                                "SDU Entity Added",
                                null,
                                entityLabel,
                                "Task '" + taskName + "' added SDU: " + entityLabel
                        ));
                    }
                }

                // SDU Entities Removed
                for (Map.Entry<String, ProcessFlowDTO.Entities> entEntry : oldEntityMap.entrySet()) {
                    if (!newEntityMap.containsKey(entEntry.getKey())) {
                        ProcessFlowDTO.Entities oe = entEntry.getValue();
                        String entityLabel = (oe.getEntity() != null && oe.getEntity().getLabel() != null) ? oe.getEntity().getLabel() : entEntry.getKey();
                        changeItems.add(new ChangeItemDTO(
                                "Task Configuration",
                                itemLabel,
                                "SDU Entity Removed",
                                entityLabel,
                                null,
                                "Task '" + taskName + "' removed SDU: " + entityLabel
                        ));
                    }
                }

                // SDU Designation changes on matched entities
                for (Map.Entry<String, ProcessFlowDTO.Entities> entEntry : oldEntityMap.entrySet()) {
                    if (newEntityMap.containsKey(entEntry.getKey())) {
                        ProcessFlowDTO.Entities oe = entEntry.getValue();
                        ProcessFlowDTO.Entities ne = newEntityMap.get(entEntry.getKey());
                        String oldDesigs = formatDesignations(oe.getDesignations());
                        String newDesigs = formatDesignations(ne.getDesignations());
                        if (!Objects.equals(oldDesigs, newDesigs)) {
                            String entityLabel = (ne.getEntity() != null && ne.getEntity().getLabel() != null) ? ne.getEntity().getLabel() : entEntry.getKey();
                            changeItems.add(new ChangeItemDTO(
                                    "Task Configuration",
                                    itemLabel,
                                    "SDU Designation ('" + entityLabel + "')",
                                    oldDesigs,
                                    newDesigs,
                                    "Task '" + taskName + "': SDU '" + entityLabel + "' designations changed from [" + oldDesigs + "] to [" + newDesigs + "]"
                            ));
                        }
                    }
                }

                // Document Mappings (templates attached to actions, permissions, etc.)
                compareDocumentMappings(oldNode.getData().getDocumentMapping(), newNode.getData().getDocumentMapping(), itemLabel, taskName, changeItems);

                // Actions & Workflow labels
                Map<String, ProcessFlowDTO.DataDTO.Action> oldWf = oldNode.getData().getWorkflow();
                Map<String, ProcessFlowDTO.DataDTO.Action> newWf = newNode.getData().getWorkflow();
                if (oldWf != null || newWf != null) {
                    Set<String> allActionKeys = new LinkedHashSet<>();
                    if (oldWf != null) allActionKeys.addAll(oldWf.keySet());
                    if (newWf != null) allActionKeys.addAll(newWf.keySet());

                    for (String actionKey : allActionKeys) {
                        ProcessFlowDTO.DataDTO.Action oldAct = (oldWf != null) ? oldWf.get(actionKey) : null;
                        ProcessFlowDTO.DataDTO.Action newAct = (newWf != null) ? newWf.get(actionKey) : null;

                        if (oldAct == null && newAct != null) {
                            String actDisplayName = getActionDisplayName(actionKey, newAct);
                            changeItems.add(new ChangeItemDTO(
                                    "Actions & UI",
                                    itemLabel,
                                    "Action Added ('" + actionKey + "')",
                                    null,
                                    actDisplayName,
                                    "Task '" + taskName + "': Added workflow action '" + actDisplayName + "'"
                            ));
                        } else if (oldAct != null && newAct == null) {
                            String actDisplayName = getActionDisplayName(actionKey, oldAct);
                            changeItems.add(new ChangeItemDTO(
                                    "Actions & UI",
                                    itemLabel,
                                    "Action Removed ('" + actionKey + "')",
                                    actDisplayName,
                                    null,
                                    "Task '" + taskName + "': Removed workflow action '" + actDisplayName + "'"
                            ));
                        } else if (oldAct != null && newAct != null) {
                            String actDisplayName = getActionDisplayName(actionKey, newAct != null ? newAct : oldAct);

                            // Action status (enabled / disabled)
                            if (!Objects.equals(oldAct.getEnabled(), newAct.getEnabled())) {
                                String status = Boolean.TRUE.equals(newAct.getEnabled()) ? "Enabled" : "Disabled";
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Action Status ('" + actionKey + "')",
                                        oldAct.getEnabled(),
                                        newAct.getEnabled(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' was " + status
                                ));
                            }

                            if (!Objects.equals(oldAct.getActionFormLabel(), newAct.getActionFormLabel())) {
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Action Form Label ('" + actionKey + "')",
                                        oldAct.getActionFormLabel(),
                                        newAct.getActionFormLabel(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' form label updated from '" + oldAct.getActionFormLabel() + "' to '" + newAct.getActionFormLabel() + "'"
                                ));
                            }
                            if (!Objects.equals(oldAct.getHistoryLabel(), newAct.getHistoryLabel())) {
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "History Label ('" + actionKey + "')",
                                        oldAct.getHistoryLabel(),
                                        newAct.getHistoryLabel(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' history label updated from '" + oldAct.getHistoryLabel() + "' to '" + newAct.getHistoryLabel() + "'"
                                ));
                            }
                            if (!Objects.equals(oldAct.getTrackLabel(), newAct.getTrackLabel())) {
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Track Label ('" + actionKey + "')",
                                        oldAct.getTrackLabel(),
                                        newAct.getTrackLabel(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' track label updated from '" + oldAct.getTrackLabel() + "' to '" + newAct.getTrackLabel() + "'"
                                ));
                            }
                            if (!Objects.equals(oldAct.getCompleteClosure(), newAct.getCompleteClosure())) {
                                String status = Boolean.TRUE.equals(newAct.getCompleteClosure()) ? "Enabled" : "Disabled";
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Complete Closure ('" + actionKey + "')",
                                        oldAct.getCompleteClosure(),
                                        newAct.getCompleteClosure(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' complete closure changed to " + status
                                ));
                            }
                            if (!Objects.equals(oldAct.getLogicalClosure(), newAct.getLogicalClosure())) {
                                String status = Boolean.TRUE.equals(newAct.getLogicalClosure()) ? "Enabled" : "Disabled";
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Logical Closure ('" + actionKey + "')",
                                        oldAct.getLogicalClosure(),
                                        newAct.getLogicalClosure(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' logical closure changed to " + status
                                ));
                            }
                            if (!Objects.equals(oldAct.getIsSubscribed(), newAct.getIsSubscribed())) {
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Action Subscription ('" + actionKey + "')",
                                        oldAct.getIsSubscribed(),
                                        newAct.getIsSubscribed(),
                                        "Task '" + taskName + "': Action '" + actDisplayName + "' subscription updated to " + (Boolean.TRUE.equals(newAct.getIsSubscribed()) ? "Subscribed" : "Unsubscribed")
                                ));
                            }
                            if (!isRemarksEqual(oldAct.getRemarks(), newAct.getRemarks())) {
                                String oldRemarksStr = formatRemarks(oldAct.getRemarks());
                                String newRemarksStr = formatRemarks(newAct.getRemarks());
                                changeItems.add(new ChangeItemDTO(
                                        "Actions & UI",
                                        itemLabel,
                                        "Remarks Behaviour ('" + actionKey + "')",
                                        oldRemarksStr,
                                        newRemarksStr,
                                        "Task '" + taskName + "': Remarks behaviour for action '" + actDisplayName + "' changed from [" + oldRemarksStr + "] to [" + newRemarksStr + "]"
                                ));
                            }
                        }
                    }
                }

                // Payment configuration
                if (oldNode.getData().getPayment() != null || newNode.getData().getPayment() != null) {
                    TaskPaymentMappingDTO oldP = oldNode.getData().getPayment();
                    TaskPaymentMappingDTO newP = newNode.getData().getPayment();
                    Boolean oldEn = oldP != null ? oldP.getEnabled() : null;
                    Boolean newEn = newP != null ? newP.getEnabled() : null;
                    if (!Objects.equals(oldEn, newEn)) {
                        changeItems.add(new ChangeItemDTO(
                                "Payment Configuration",
                                itemLabel,
                                "Payment Status",
                                oldEn,
                                newEn,
                                "Task '" + taskName + "': Payment status changed from " + (Boolean.TRUE.equals(oldEn) ? "Enabled" : "Disabled") + " to " + (Boolean.TRUE.equals(newEn) ? "Enabled" : "Disabled")
                        ));
                    }
                    Long oldCharge = (oldP != null) ? oldP.getChargeId() : null;
                    Long newCharge = (newP != null) ? newP.getChargeId() : null;
                    if (!Objects.equals(oldCharge, newCharge)) {
                        changeItems.add(new ChangeItemDTO(
                                "Payment Configuration",
                                itemLabel,
                                "Charge ID",
                                oldCharge,
                                newCharge,
                                "Task '" + taskName + "': Payment charge template ID changed from " + oldCharge + " to " + newCharge
                        ));
                    }
                    Boolean oldSysAck = (oldP != null) ? oldP.getSystemGeneratedAck() : null;
                    Boolean newSysAck = (newP != null) ? newP.getSystemGeneratedAck() : null;
                    if (!Objects.equals(oldSysAck, newSysAck)) {
                        changeItems.add(new ChangeItemDTO(
                                "Payment Configuration",
                                itemLabel,
                                "System Generated Receipt",
                                oldSysAck,
                                newSysAck,
                                "Task '" + taskName + "': System generated payment receipt changed to " + (Boolean.TRUE.equals(newSysAck) ? "Enabled" : "Disabled")
                        ));
                    }
                    String oldAckDoc = (oldP != null && oldP.getPaymentAckDoc() != null) ? oldP.getPaymentAckDoc().getLabel() : null;
                    String newAckDoc = (newP != null && newP.getPaymentAckDoc() != null) ? newP.getPaymentAckDoc().getLabel() : null;
                    if (!Objects.equals(oldAckDoc, newAckDoc)) {
                        changeItems.add(new ChangeItemDTO(
                                "Payment Configuration",
                                itemLabel,
                                "Payment Receipt Document",
                                oldAckDoc,
                                newAckDoc,
                                "Task '" + taskName + "': Payment receipt document template changed from '" + oldAckDoc + "' to '" + newAckDoc + "'"
                        ));
                    }
                }

                // Functional Controls: Official, Applicant, Timer, ServiceLevel, DisplayUserTask
                ProcessFlowDTO.Advanced oldAdv = oldNode.getData().getAdvanced();
                ProcessFlowDTO.Advanced newAdv = newNode.getData().getAdvanced();
                compareFunctionalControls(oldAdv, newAdv, itemLabel, taskName, changeItems);

                // Task-level Escalation
                compareEscalation(oldNode.getData().getEscalation(), newNode.getData().getEscalation(), itemLabel, taskName, changeItems);

                // Task-level Application Routing
                compareApplicationRouting(oldNode.getData().getApplicationRouting(), newNode.getData().getApplicationRouting(), itemLabel, taskName, changeItems);

                // Task-level DBT
                compareDbt(oldAdv != null ? oldAdv.getDbt() : null, newAdv != null ? newAdv.getDbt() : null, itemLabel, taskName, changeItems);

                // Task-level Associated Tasks
                compareAssociatedTaskReferences(oldNode.getData().getAssociatedTasks(), newNode.getData().getAssociatedTasks(), itemLabel, taskName, changeItems);
            }
        }

        // 3. Root Associated Tasks
        compareRootAssociatedTasks(oldDto.getAssociatedTasks(), newDto.getAssociatedTasks(), changeItems);

        // 4. Check Edges (Process Flow Sequence)
        compareEdges(oldDto.getEdges(), newDto.getEdges(), changeItems);

        return changeItems;
    }

    private void compareSubmissionNode(ProcessFlowDTO.DataDTO oldData, ProcessFlowDTO.DataDTO newData, String itemLabel, List<ChangeItemDTO> changeItems) {
        if (oldData == null || newData == null) {
            return;
        }

        // Added & Removed modes of submission
        Set<String> oldModeVals = (oldData.getMode() != null) ?
                oldData.getMode().stream().filter(m -> m != null && m.getValue() != null).map(m -> String.valueOf(m.getValue())).collect(Collectors.toSet()) : Collections.emptySet();
        Set<String> newModeVals = (newData.getMode() != null) ?
                newData.getMode().stream().filter(m -> m != null && m.getValue() != null).map(m -> String.valueOf(m.getValue())).collect(Collectors.toSet()) : Collections.emptySet();

        if (newData.getMode() != null) {
            for (LabelValue nm : newData.getMode()) {
                if (nm != null && nm.getValue() != null && !oldModeVals.contains(String.valueOf(nm.getValue()))) {
                    changeItems.add(new ChangeItemDTO(
                            "Service Definition & Process Flow",
                            itemLabel,
                            "Mode of Submission Added",
                            null,
                            nm.getLabel(),
                            "Service Initiation: Added Mode of Submission: " + nm.getLabel()
                    ));
                }
            }
        }
        if (oldData.getMode() != null) {
            for (LabelValue om : oldData.getMode()) {
                if (om != null && om.getValue() != null && !newModeVals.contains(String.valueOf(om.getValue()))) {
                    changeItems.add(new ChangeItemDTO(
                            "Service Definition & Process Flow",
                            itemLabel,
                            "Mode of Submission Removed",
                            om.getLabel(),
                            null,
                            "Service Initiation: Removed Mode of Submission: " + om.getLabel()
                    ));
                }
            }
        }

        // Kiosk types
        int oldKioskCount = (oldData.getKioskType() != null) ? oldData.getKioskType().size() : 0;
        int newKioskCount = (newData.getKioskType() != null) ? newData.getKioskType().size() : 0;
        if (!Objects.equals(oldData.getKioskType(), newData.getKioskType())) {
            changeItems.add(new ChangeItemDTO(
                    "Service Definition & Process Flow",
                    itemLabel,
                    "Kiosk Configuration",
                    oldKioskCount + " kiosk types",
                    newKioskCount + " kiosk types",
                    "Service Initiation: Kiosk configuration updated (" + newKioskCount + " configured)"
            ));
        }

        // Enclosures required
        if (!Objects.equals(oldData.getEnclosuresRequired(), newData.getEnclosuresRequired())) {
            changeItems.add(new ChangeItemDTO(
                    "Service Definition & Process Flow",
                    itemLabel,
                    "Enclosures Required",
                    oldData.getEnclosuresRequired(),
                    newData.getEnclosuresRequired(),
                    "Service Initiation: Enclosures required changed from '" + oldData.getEnclosuresRequired() + "' to '" + newData.getEnclosuresRequired() + "'"
            ));
        }

        // Enclosures list
        if (!Objects.equals(oldData.getEnclosures(), newData.getEnclosures())) {
            int oldEncCount = (oldData.getEnclosures() != null) ? oldData.getEnclosures().size() : 0;
            int newEncCount = (newData.getEnclosures() != null) ? newData.getEnclosures().size() : 0;
            changeItems.add(new ChangeItemDTO(
                    "Service Definition & Process Flow",
                    itemLabel,
                    "Enclosures List",
                    oldEncCount + " enclosures",
                    newEncCount + " enclosures",
                    "Service Initiation: Enclosures list modified (" + newEncCount + " configured)"
            ));
        }

        // Predefined enclosures
        if (!Objects.equals(oldData.getPredefined(), newData.getPredefined())) {
            int oldPreCount = (oldData.getPredefined() != null) ? oldData.getPredefined().size() : 0;
            int newPreCount = (newData.getPredefined() != null) ? newData.getPredefined().size() : 0;
            changeItems.add(new ChangeItemDTO(
                    "Service Definition & Process Flow",
                    itemLabel,
                    "Predefined Enclosures",
                    oldPreCount + " predefined",
                    newPreCount + " predefined",
                    "Service Initiation: Predefined enclosures configuration updated (" + newPreCount + " configured)"
            ));
        }

        // Auto-delete
        if (oldData.getAutodelete() != null && newData.getAutodelete() != null) {
            if (oldData.getAutodelete().isBehaviour() != newData.getAutodelete().isBehaviour()) {
                changeItems.add(new ChangeItemDTO(
                        "Application Handling Rules",
                        itemLabel,
                        "Auto-Deletion Behaviour",
                        oldData.getAutodelete().isBehaviour(),
                        newData.getAutodelete().isBehaviour(),
                        "Service Initiation: Auto-deletion behaviour changed from " + oldData.getAutodelete().isBehaviour() + " to " + newData.getAutodelete().isBehaviour()
                ));
            }
            if (!Objects.equals(oldData.getAutodelete().getData(), newData.getAutodelete().getData())) {
                changeItems.add(new ChangeItemDTO(
                        "Application Handling Rules",
                        itemLabel,
                        "Auto-Deletion Rules",
                        oldData.getAutodelete().getData(),
                        newData.getAutodelete().getData(),
                        "Service Initiation: Auto-deletion period/rules configuration updated"
                ));
            }
        }

        // Limit submission
        if (oldData.getLimitSubmission() != null && newData.getLimitSubmission() != null) {
            if (oldData.getLimitSubmission().isBehaviour() != newData.getLimitSubmission().isBehaviour()) {
                changeItems.add(new ChangeItemDTO(
                        "Application Handling Rules",
                        itemLabel,
                        "Submission Limit Behaviour",
                        oldData.getLimitSubmission().isBehaviour(),
                        newData.getLimitSubmission().isBehaviour(),
                        "Service Initiation: Application submission limit behaviour changed from " + oldData.getLimitSubmission().isBehaviour() + " to " + newData.getLimitSubmission().isBehaviour()
                ));
            }
            if (!Objects.equals(oldData.getLimitSubmission().getData(), newData.getLimitSubmission().getData())) {
                changeItems.add(new ChangeItemDTO(
                        "Application Handling Rules",
                        itemLabel,
                        "Submission Limit Rules",
                        oldData.getLimitSubmission().getData(),
                        newData.getLimitSubmission().getData(),
                        "Service Initiation: Application submission limit rules configuration updated"
                ));
            }
        }
    }

    private void compareDocumentMappings(List<DocMappingDTO> oldDocs,
                                         List<DocMappingDTO> newDocs,
                                         String itemLabel,
                                         String taskName,
                                         List<ChangeItemDTO> changeItems) {
        List<DocMappingDTO> oldList = (oldDocs != null) ? new ArrayList<>(oldDocs) : Collections.emptyList();
        List<DocMappingDTO> newList = (newDocs != null) ? new ArrayList<>(newDocs) : Collections.emptyList();

        Set<DocMappingDTO> matchedOld = Collections.newSetFromMap(new IdentityHashMap<>());
        Set<DocMappingDTO> matchedNew = Collections.newSetFromMap(new IdentityHashMap<>());
        List<Map.Entry<DocMappingDTO, DocMappingDTO>> matchedPairs = new ArrayList<>();

        // Pass 1: match by referenceId (if non-blank)
        for (DocMappingDTO nd : newList) {
            if (nd != null && nd.getReferenceId() != null && !nd.getReferenceId().isBlank()) {
                for (DocMappingDTO od : oldList) {
                    if (od != null && !matchedOld.contains(od) && od.getReferenceId() != null && !od.getReferenceId().isBlank() &&
                            Objects.equals(nd.getReferenceId().trim(), od.getReferenceId().trim())) {
                        matchedPairs.add(new AbstractMap.SimpleEntry<>(od, nd));
                        matchedOld.add(od);
                        matchedNew.add(nd);
                        break;
                    }
                }
            }
        }

        // Pass 2: match remaining by documentName (if non-blank)
        for (DocMappingDTO nd : newList) {
            if (nd != null && !matchedNew.contains(nd) && nd.getDocumentName() != null && !nd.getDocumentName().isBlank()) {
                for (DocMappingDTO od : oldList) {
                    if (od != null && !matchedOld.contains(od) && od.getDocumentName() != null && !od.getDocumentName().isBlank() &&
                            Objects.equals(nd.getDocumentName().trim(), od.getDocumentName().trim())) {
                        matchedPairs.add(new AbstractMap.SimpleEntry<>(od, nd));
                        matchedOld.add(od);
                        matchedNew.add(nd);
                        break;
                    }
                }
            }
        }

        // Pass 3: match remaining by systemGeneratedDocument value (template id)
        for (DocMappingDTO nd : newList) {
            if (nd != null && !matchedNew.contains(nd) && nd.getSystemGeneratedDocument() != null && nd.getSystemGeneratedDocument().getValue() != null) {
                for (DocMappingDTO od : oldList) {
                    if (od != null && !matchedOld.contains(od) && od.getSystemGeneratedDocument() != null &&
                            Objects.equals(nd.getSystemGeneratedDocument().getValue(), od.getSystemGeneratedDocument().getValue())) {
                        matchedPairs.add(new AbstractMap.SimpleEntry<>(od, nd));
                        matchedOld.add(od);
                        matchedNew.add(nd);
                        break;
                    }
                }
            }
        }

        // Pass 4: match remaining positionally if same size
        if (oldList.size() == newList.size()) {
            for (int i = 0; i < newList.size(); i++) {
                DocMappingDTO nd = newList.get(i);
                DocMappingDTO od = oldList.get(i);
                if (nd != null && od != null && !matchedNew.contains(nd) && !matchedOld.contains(od)) {
                    matchedPairs.add(new AbstractMap.SimpleEntry<>(od, nd));
                    matchedOld.add(od);
                    matchedNew.add(nd);
                }
            }
        }

        // 1. Newly added documents (all unmatched in newList)
        for (DocMappingDTO newDoc : newList) {
            if (newDoc != null && !matchedNew.contains(newDoc)) {
                String docName = (newDoc.getDocumentName() != null && !newDoc.getDocumentName().isBlank()) ?
                        newDoc.getDocumentName() : (newDoc.getReferenceId() != null ? newDoc.getReferenceId() : "Document");
                String tplLabel = (newDoc.getSystemGeneratedDocument() != null && newDoc.getSystemGeneratedDocument().getLabel() != null) ?
                        newDoc.getSystemGeneratedDocument().getLabel() : null;
                String actionsStr = formatDocActions(newDoc.getAction());
                String sourcesStr = (newDoc.getDocumentSource() != null && !newDoc.getDocumentSource().isEmpty()) ?
                        String.join(", ", newDoc.getDocumentSource()) : null;

                StringBuilder details = new StringBuilder();
                details.append("Task '").append(taskName).append("': Added document mapping '").append(docName).append("'");
                List<String> meta = new ArrayList<>();
                if (tplLabel != null) meta.add("Output Template: '" + tplLabel + "'");
                if (actionsStr != null && !actionsStr.isEmpty()) meta.add("Attached to Action: " + actionsStr);
                if (sourcesStr != null) meta.add("Source: [" + sourcesStr + "]");
                if (newDoc.getDigitalSignatureRequired() != null) meta.add("Digital Signature: '" + newDoc.getDigitalSignatureRequired() + "'");
                if (newDoc.getViewPermission() != null && !newDoc.getViewPermission().isEmpty()) meta.add("View Permission: " + newDoc.getViewPermission());
                if (!meta.isEmpty()) {
                    details.append(" (").append(String.join(", ", meta)).append(")");
                }

                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Document Added ('" + docName + "')",
                        null,
                        docName,
                        details.toString()
                ));
            }
        }

        // 2. Removed documents (all unmatched in oldList)
        for (DocMappingDTO oldDoc : oldList) {
            if (oldDoc != null && !matchedOld.contains(oldDoc)) {
                String docName = (oldDoc.getDocumentName() != null && !oldDoc.getDocumentName().isBlank()) ?
                        oldDoc.getDocumentName() : (oldDoc.getReferenceId() != null ? oldDoc.getReferenceId() : "Document");
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Document Removed ('" + docName + "')",
                        docName,
                        null,
                        "Task '" + taskName + "': Removed document mapping '" + docName + "'"
                ));
            }
        }

        // 3. Modified documents (compare all matched pairs)
        for (Map.Entry<DocMappingDTO, DocMappingDTO> pair : matchedPairs) {
            DocMappingDTO oldDoc = pair.getKey();
            DocMappingDTO newDoc = pair.getValue();
            String docName = (newDoc.getDocumentName() != null && !newDoc.getDocumentName().isBlank()) ?
                    newDoc.getDocumentName() : (oldDoc.getDocumentName() != null ? oldDoc.getDocumentName() : "Document");

            if (!Objects.equals(oldDoc.getDocumentName(), newDoc.getDocumentName())) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Document Name",
                        oldDoc.getDocumentName(),
                        newDoc.getDocumentName(),
                        "Task '" + taskName + "': Document name changed from '" + oldDoc.getDocumentName() + "' to '" + newDoc.getDocumentName() + "'"
                ));
            }

            String oldTpl = (oldDoc.getSystemGeneratedDocument() != null) ? oldDoc.getSystemGeneratedDocument().getLabel() : null;
            String newTpl = (newDoc.getSystemGeneratedDocument() != null) ? newDoc.getSystemGeneratedDocument().getLabel() : null;
            if (!Objects.equals(oldTpl, newTpl)) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Output Template ('" + docName + "')",
                        oldTpl,
                        newTpl,
                        "Task '" + taskName + "': Document '" + docName + "' output template changed from '" + oldTpl + "' to '" + newTpl + "'"
                ));
            }

            String oldActs = formatDocActions(oldDoc.getAction());
            String newActs = formatDocActions(newDoc.getAction());
            if (!Objects.equals(oldActs, newActs)) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Attached Actions ('" + docName + "')",
                        oldActs,
                        newActs,
                        "Task '" + taskName + "': Document '" + docName + "' attached actions changed from [" + oldActs + "] to [" + newActs + "]"
                ));
            }

            if (!Objects.equals(oldDoc.getDigitalSignatureRequired(), newDoc.getDigitalSignatureRequired())) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Digital Signature ('" + docName + "')",
                        oldDoc.getDigitalSignatureRequired(),
                        newDoc.getDigitalSignatureRequired(),
                        "Task '" + taskName + "': Document '" + docName + "' digital signature requirement changed from '" + oldDoc.getDigitalSignatureRequired() + "' to '" + newDoc.getDigitalSignatureRequired() + "'"
                ));
            }

            String oldPerms = (oldDoc.getViewPermission() != null) ? String.join(", ", oldDoc.getViewPermission()) : "";
            String newPerms = (newDoc.getViewPermission() != null) ? String.join(", ", newDoc.getViewPermission()) : "";
            if (!Objects.equals(oldPerms, newPerms)) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "View Permission ('" + docName + "')",
                        oldPerms,
                        newPerms,
                        "Task '" + taskName + "': Document '" + docName + "' view permission changed from [" + oldPerms + "] to [" + newPerms + "]"
                ));
            }

            String oldSrc = (oldDoc.getDocumentSource() != null) ? String.join(", ", oldDoc.getDocumentSource()) : "";
            String newSrc = (newDoc.getDocumentSource() != null) ? String.join(", ", newDoc.getDocumentSource()) : "";
            if (!Objects.equals(oldSrc, newSrc)) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Document Source ('" + docName + "')",
                        oldSrc,
                        newSrc,
                        "Task '" + taskName + "': Document '" + docName + "' source changed from [" + oldSrc + "] to [" + newSrc + "]"
                ));
            }

            if (!Objects.equals(oldDoc.getIsFileUploadOptional(), newDoc.getIsFileUploadOptional())) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "File Upload Optional ('" + docName + "')",
                        oldDoc.getIsFileUploadOptional(),
                        newDoc.getIsFileUploadOptional(),
                        "Task '" + taskName + "': Document '" + docName + "' file upload optional changed to " + newDoc.getIsFileUploadOptional()
                ));
            }

            if (!Objects.equals(oldDoc.getIsDraftDocumentRequired(), newDoc.getIsDraftDocumentRequired())) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Draft Document Required ('" + docName + "')",
                        oldDoc.getIsDraftDocumentRequired(),
                        newDoc.getIsDraftDocumentRequired(),
                        "Task '" + taskName + "': Document '" + docName + "' draft document required changed to " + newDoc.getIsDraftDocumentRequired()
                ));
            }

            if (!Objects.equals(oldDoc.getMergeMode(), newDoc.getMergeMode())) {
                changeItems.add(new ChangeItemDTO(
                        "Document Mapping",
                        itemLabel,
                        "Merge Mode ('" + docName + "')",
                        oldDoc.getMergeMode(),
                        newDoc.getMergeMode(),
                        "Task '" + taskName + "': Document '" + docName + "' merge mode changed from '" + oldDoc.getMergeMode() + "' to '" + newDoc.getMergeMode() + "'"
                ));
            }
        }
    }

    private String formatDocActions(List<DocMappingDTO.LabelValueDTO> actions) {
        if (actions == null || actions.isEmpty()) return "";
        return actions.stream()
                .filter(Objects::nonNull)
                .map(a -> (a.getLabel() != null ? a.getLabel() : "") + (a.getValue() != null ? " (" + a.getValue() + ")" : ""))
                .collect(Collectors.joining(", "));
    }

    private String formatDesignations(List<ProcessFlowDTO.Designation> desigs) {
        if (desigs == null || desigs.isEmpty()) return "None";
        return desigs.stream()
                .filter(d -> d != null && d.getDesignation() != null && d.getDesignation().getLabel() != null)
                .map(d -> d.getDesignation().getLabel())
                .collect(Collectors.joining(", "));
    }

    private boolean isRemarksEqual(ProcessFlowDTO.DataDTO.Remarks r1, ProcessFlowDTO.DataDTO.Remarks r2) {
        if (r1 == null && r2 == null) {
            return true;
        }
        boolean app1 = r1 != null && Boolean.TRUE.equals(r1.getApplicant());
        boolean app2 = r2 != null && Boolean.TRUE.equals(r2.getApplicant());
        boolean off1 = r1 != null && Boolean.TRUE.equals(r1.getOfficial());
        boolean off2 = r2 != null && Boolean.TRUE.equals(r2.getOfficial());
        boolean man1 = r1 != null && Boolean.TRUE.equals(r1.getMandatory());
        boolean man2 = r2 != null && Boolean.TRUE.equals(r2.getMandatory());
        return app1 == app2 && off1 == off2 && man1 == man2;
    }

    private String formatRemarks(ProcessFlowDTO.DataDTO.Remarks r) {
        if (r == null) {
            return "Disabled";
        }
        List<String> active = new ArrayList<>();
        if (Boolean.TRUE.equals(r.getApplicant())) {
            active.add("Applicant Remarks");
        }
        if (Boolean.TRUE.equals(r.getOfficial())) {
            active.add("Official Remarks");
        }
        if (Boolean.TRUE.equals(r.getMandatory())) {
            active.add("Mandatory");
        }
        return active.isEmpty() ? "Disabled" : String.join(", ", active);
    }

    private String getActionDisplayName(String actionKey, ProcessFlowDTO.DataDTO.Action act) {
        if (act != null && act.getActionFormLabel() != null && !act.getActionFormLabel().isBlank()) {
            return act.getActionFormLabel() + " (" + actionKey + ")";
        }
        return actionKey;
    }

    private void compareFunctionalControls(ProcessFlowDTO.Advanced oldAdv, ProcessFlowDTO.Advanced newAdv, String itemLabel, String taskName, List<ChangeItemDTO> changeItems) {
        if (oldAdv == null && newAdv == null) {
            return;
        }

        // Official controls
        if (oldAdv != null && newAdv != null && oldAdv.getOfficial() != null && newAdv.getOfficial() != null) {
            ProcessFlowDTO.Official oldOff = oldAdv.getOfficial();
            ProcessFlowDTO.Official newOff = newAdv.getOfficial();

            if (oldOff.isAdditionalEnclosures() != newOff.isAdditionalEnclosures()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Ask for Additional Documents",
                        oldOff.isAdditionalEnclosures(),
                        newOff.isAdditionalEnclosures(),
                        "Task '" + taskName + "': Official permission to ask for additional documents changed to " + (newOff.isAdditionalEnclosures() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isPrint() != newOff.isPrint()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Print Processing History",
                        oldOff.isPrint(),
                        newOff.isPrint(),
                        "Task '" + taskName + "': Official permission to print application processing history changed to " + (newOff.isPrint() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isPriority() != newOff.isPriority()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Set Application Priority",
                        oldOff.isPriority(),
                        newOff.isPriority(),
                        "Task '" + taskName + "': Official permission to set application priority changed to " + (newOff.isPriority() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isConditionalCallback() != newOff.isConditionalCallback()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Conditional Callback",
                        oldOff.isConditionalCallback(),
                        newOff.isConditionalCallback(),
                        "Task '" + taskName + "': Conditional callback changed to " + (newOff.isConditionalCallback() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isOverride() != newOff.isOverride()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Override Automatic Office Selection",
                        oldOff.isOverride(),
                        newOff.isOverride(),
                        "Task '" + taskName + "': Override automatic office selection changed to " + (newOff.isOverride() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isReassign() != newOff.isReassign()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Reassign Permission",
                        oldOff.isReassign(),
                        newOff.isReassign(),
                        "Task '" + taskName + "': Official reassign permission changed to " + (newOff.isReassign() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isEscalate() != newOff.isEscalate()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Escalate Permission",
                        oldOff.isEscalate(),
                        newOff.isEscalate(),
                        "Task '" + taskName + "': Escalate permission changed to " + (newOff.isEscalate() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isVerify() != newOff.isVerify()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Verify Permission",
                        oldOff.isVerify(),
                        newOff.isVerify(),
                        "Task '" + taskName + "': Verify permission changed to " + (newOff.isVerify() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isCertificate() != newOff.isCertificate()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Generate Certificate Permission",
                        oldOff.isCertificate(),
                        newOff.isCertificate(),
                        "Task '" + taskName + "': Generate certificate permission changed to " + (newOff.isCertificate() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isAllowRevision() != newOff.isAllowRevision()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Allow Revision Permission",
                        oldOff.isAllowRevision(),
                        newOff.isAllowRevision(),
                        "Task '" + taskName + "': Allow revision permission changed to " + (newOff.isAllowRevision() ? "Enabled" : "Disabled")
                ));
            }
            if (oldOff.isRejectEnclosure() != newOff.isRejectEnclosure()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Reject Enclosure Permission",
                        oldOff.isRejectEnclosure(),
                        newOff.isRejectEnclosure(),
                        "Task '" + taskName + "': Official reject enclosure permission changed to " + (newOff.isRejectEnclosure() ? "Enabled" : "Disabled")
                ));
            }

            // Applicant Edit Permission
            if (oldOff.getApplicantEditPermission() != null || newOff.getApplicantEditPermission() != null) {
                ProcessFlowDTO.ApplicantEditPermission oldAep = oldOff.getApplicantEditPermission();
                ProcessFlowDTO.ApplicantEditPermission newAep = newOff.getApplicantEditPermission();
                boolean oldVal = oldAep != null && oldAep.isValue();
                boolean newVal = newAep != null && newAep.isValue();
                if (oldVal != newVal) {
                    changeItems.add(new ChangeItemDTO(
                            "Task-Level Functional Controls",
                            itemLabel,
                            "Applicant Edit Permission",
                            oldVal,
                            newVal,
                            "Task '" + taskName + "': Permission for applicant to edit fields changed to " + (newVal ? "Enabled" : "Disabled")
                    ));
                }
                String oldCtrl = (oldAep != null && oldAep.getControl() != null) ? oldAep.getControl().getLabel() : null;
                String newCtrl = (newAep != null && newAep.getControl() != null) ? newAep.getControl().getLabel() : null;
                if (!Objects.equals(oldCtrl, newCtrl)) {
                    changeItems.add(new ChangeItemDTO(
                            "Task-Level Functional Controls",
                            itemLabel,
                            "Applicant Edit Control Type",
                            oldCtrl,
                            newCtrl,
                            "Task '" + taskName + "': Applicant edit control changed from '" + oldCtrl + "' to '" + newCtrl + "'"
                    ));
                }
                String oldSpecs = (oldAep != null && oldAep.getSpecific() != null) ?
                        oldAep.getSpecific().stream().filter(Objects::nonNull).map(LabelValue::getLabel).filter(Objects::nonNull).collect(Collectors.joining(", ")) : null;
                String newSpecs = (newAep != null && newAep.getSpecific() != null) ?
                        newAep.getSpecific().stream().filter(Objects::nonNull).map(LabelValue::getLabel).filter(Objects::nonNull).collect(Collectors.joining(", ")) : null;
                if (!Objects.equals(oldSpecs, newSpecs)) {
                    changeItems.add(new ChangeItemDTO(
                            "Task-Level Functional Controls",
                            itemLabel,
                            "Applicant Editable Fields",
                            oldSpecs,
                            newSpecs,
                            "Task '" + taskName + "': Applicant editable specific fields changed from [" + oldSpecs + "] to [" + newSpecs + "]"
                    ));
                }
            }

            // Official MultiProcessing / Bunching
            compareMultiProcessing(oldOff.getMultiProcessing(), newOff.getMultiProcessing(), itemLabel, taskName, "Official Bunching / Multiple Processing", changeItems);

            // Predefined Processing
            if (oldOff.getPredefinedProcessing() != null || newOff.getPredefinedProcessing() != null) {
                ProcessFlowDTO.PredefinedProcessing oldPp = oldOff.getPredefinedProcessing();
                ProcessFlowDTO.PredefinedProcessing newPp = newOff.getPredefinedProcessing();
                boolean oldSingle = oldPp != null && oldPp.isSingleProcessingAllowed();
                boolean newSingle = newPp != null && newPp.isSingleProcessingAllowed();
                if (oldSingle != newSingle) {
                    changeItems.add(new ChangeItemDTO(
                            "Task-Level Functional Controls",
                            itemLabel,
                            "Single Processing Allowed",
                            oldSingle,
                            newSingle,
                            "Task '" + taskName + "': Single processing allowed changed to " + (newSingle ? "Enabled" : "Disabled")
                    ));
                }
            }
        }

        // Applicant controls
        if (oldAdv != null && newAdv != null && oldAdv.getApplicant() != null && newAdv.getApplicant() != null) {
            ProcessFlowDTO.Applicant oldApp = oldAdv.getApplicant();
            ProcessFlowDTO.Applicant newApp = newAdv.getApplicant();

            if (oldApp.isSubmissionToSameOfficial() != newApp.isSubmissionToSameOfficial()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Submit Back to Same Official",
                        oldApp.isSubmissionToSameOfficial(),
                        newApp.isSubmissionToSameOfficial(),
                        "Task '" + taskName + "': Submit back to same official permission changed to " + (newApp.isSubmissionToSameOfficial() ? "Enabled" : "Disabled")
                ));
            }
            if (oldApp.isDeoSubmit() != newApp.isDeoSubmit()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "DEO Submission",
                        oldApp.isDeoSubmit(),
                        newApp.isDeoSubmit(),
                        "Task '" + taskName + "': DEO submission permission changed to " + (newApp.isDeoSubmit() ? "Enabled" : "Disabled")
                ));
            }
            if (oldApp.isUploadRejectedEnclosures() != newApp.isUploadRejectedEnclosures()) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Upload Rejected Enclosures",
                        oldApp.isUploadRejectedEnclosures(),
                        newApp.isUploadRejectedEnclosures(),
                        "Task '" + taskName + "': Upload rejected enclosures permission changed to " + (newApp.isUploadRejectedEnclosures() ? "Enabled" : "Disabled")
                ));
            }
        }

        // Timer controls
        if (oldAdv != null && newAdv != null && oldAdv.getTimer() != null && newAdv.getTimer() != null) {
            Boolean oldTrack = oldAdv.getTimer().getShowInTrack();
            Boolean newTrack = newAdv.getTimer().getShowInTrack();
            if (!Objects.equals(oldTrack, newTrack)) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Track Activity in Timer",
                        oldTrack,
                        newTrack,
                        "Task '" + taskName + "': Track activity in timer tasks changed to " + (Boolean.TRUE.equals(newTrack) ? "Enabled" : "Disabled")
                ));
            }

            if (oldAdv.getTimer().getTaskExecustionPeriod() != null || newAdv.getTimer().getTaskExecustionPeriod() != null) {
                ProcessFlowDTO.TaskExecutionPeriod oldTep = oldAdv.getTimer().getTaskExecustionPeriod();
                ProcessFlowDTO.TaskExecutionPeriod newTep = newAdv.getTimer().getTaskExecustionPeriod();
                if (oldTep != null && newTep != null && oldTep.getStaticValue() != null && newTep.getStaticValue() != null) {
                    Integer oldPeriod = oldTep.getStaticValue().getTaskExecutionPeriod();
                    Integer newPeriod = newTep.getStaticValue().getTaskExecutionPeriod();
                    String oldUnit = (oldTep.getStaticValue().getUnit() != null) ? oldTep.getStaticValue().getUnit().getLabel() : "days";
                    String newUnit = (newTep.getStaticValue().getUnit() != null) ? newTep.getStaticValue().getUnit().getLabel() : "days";
                    if (!Objects.equals(oldPeriod, newPeriod) || !Objects.equals(oldUnit, newUnit)) {
                        changeItems.add(new ChangeItemDTO(
                                "Task-Level Functional Controls",
                                itemLabel,
                                "Task Execution Period",
                                oldPeriod + " " + oldUnit,
                                newPeriod + " " + newUnit,
                                "Task '" + taskName + "': Task execution period changed from " + oldPeriod + " " + oldUnit + " to " + newPeriod + " " + newUnit
                        ));
                    }
                }
            }

            // Timer MultiProcessing / Bunching
            compareMultiProcessing(oldAdv.getTimer().getMultiProcessing(), newAdv.getTimer().getMultiProcessing(), itemLabel, taskName, "Timer Bunching / Multiple Processing", changeItems);
        }

        // Display User Task
        if (oldAdv != null && newAdv != null && (oldAdv.getDisplayUserTask() != null || newAdv.getDisplayUserTask() != null)) {
            ProcessFlowDTO.DisplayUserTask oldDut = oldAdv.getDisplayUserTask();
            ProcessFlowDTO.DisplayUserTask newDut = newAdv.getDisplayUserTask();
            boolean oldVal = oldDut != null && oldDut.isValue();
            boolean newVal = newDut != null && newDut.isValue();
            if (oldVal != newVal) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Display User Task",
                        oldVal,
                        newVal,
                        "Task '" + taskName + "': Display user task changed to " + (newVal ? "Enabled" : "Disabled")
                ));
            }
        }

        // Service Level
        if (oldAdv != null && newAdv != null && (oldAdv.getServiceLevel() != null || newAdv.getServiceLevel() != null)) {
            ProcessFlowDTO.ServiceLevel oldSl = oldAdv.getServiceLevel();
            ProcessFlowDTO.ServiceLevel newSl = newAdv.getServiceLevel();
            boolean oldVal = oldSl != null && oldSl.isValue();
            boolean newVal = newSl != null && newSl.isValue();
            if (oldVal != newVal) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Service Level SLA",
                        oldVal,
                        newVal,
                        "Task '" + taskName + "': Task SLA service level changed to " + (newVal ? "Enabled" : "Disabled")
                ));
            }
            String oldDays = oldSl != null ? oldSl.getDays() : null;
            String newDays = newSl != null ? newSl.getDays() : null;
            if (!Objects.equals(oldDays, newDays)) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        "Service Level Days",
                        oldDays,
                        newDays,
                        "Task '" + taskName + "': Service level days changed from '" + oldDays + "' to '" + newDays + "'"
                ));
            }
        }
    }

    private void compareMultiProcessing(ProcessFlowDTO.MultiProcessing oldMp,
                                        ProcessFlowDTO.MultiProcessing newMp,
                                        String itemLabel,
                                        String taskName,
                                        String scopeLabel,
                                        List<ChangeItemDTO> changeItems) {
        if (oldMp == null && newMp == null) {
            return;
        }
        boolean oldVal = oldMp != null && oldMp.isValue();
        boolean newVal = newMp != null && newMp.isValue();
        if (oldVal != newVal) {
            changeItems.add(new ChangeItemDTO(
                    "Task-Level Functional Controls",
                    itemLabel,
                    scopeLabel + " Status",
                    oldVal,
                    newVal,
                    "Task '" + taskName + "': " + scopeLabel + " status changed to " + (newVal ? "Enabled" : "Disabled")
            ));
        }
        ProcessFlowDTO.MultiProcessingData oldData = oldMp != null ? oldMp.getData() : null;
        ProcessFlowDTO.MultiProcessingData newData = newMp != null ? newMp.getData() : null;
        if (oldData != null || newData != null) {
            String oldMultiType = (oldData != null && oldData.getMultipleProcessingType() != null) ? oldData.getMultipleProcessingType().getLabel() : null;
            String newMultiType = (newData != null && newData.getMultipleProcessingType() != null) ? newData.getMultipleProcessingType().getLabel() : null;
            if (!Objects.equals(oldMultiType, newMultiType)) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        scopeLabel + " Multiple Processing Type",
                        oldMultiType,
                        newMultiType,
                        "Task '" + taskName + "': " + scopeLabel + " type changed from '" + oldMultiType + "' to '" + newMultiType + "'"
                ));
            }

            String oldBunchType = (oldData != null && oldData.getBunchProcessingType() != null) ? oldData.getBunchProcessingType().getLabel() : null;
            String newBunchType = (newData != null && newData.getBunchProcessingType() != null) ? newData.getBunchProcessingType().getLabel() : null;
            if (!Objects.equals(oldBunchType, newBunchType)) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        scopeLabel + " Bunch Processing Type",
                        oldBunchType,
                        newBunchType,
                        "Task '" + taskName + "': " + scopeLabel + " bunch type changed from '" + oldBunchType + "' to '" + newBunchType + "'"
                ));
            }

            boolean oldUngroup = oldData != null && oldData.isUngroupBunchedApplicationPrevious();
            boolean newUngroup = newData != null && newData.isUngroupBunchedApplicationPrevious();
            if (oldUngroup != newUngroup) {
                changeItems.add(new ChangeItemDTO(
                        "Task-Level Functional Controls",
                        itemLabel,
                        scopeLabel + " Ungroup Previous Applications",
                        oldUngroup,
                        newUngroup,
                        "Task '" + taskName + "': Ungroup bunched application on previous task changed to " + (newUngroup ? "Enabled" : "Disabled")
                ));
            }
        }
    }

    private void compareApplicationRouting(ProcessFlowDTO.ApplicationRouting oldAr,
                                           ProcessFlowDTO.ApplicationRouting newAr,
                                           String itemLabel,
                                           String taskName,
                                           List<ChangeItemDTO> changeItems) {
        if (oldAr == null && newAr == null) return;
        Boolean oldEn = oldAr != null ? oldAr.getEnabled() : null;
        Boolean newEn = newAr != null ? newAr.getEnabled() : null;
        if (!Objects.equals(oldEn, newEn)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Application Routing Status",
                    oldEn,
                    newEn,
                    "Task '" + taskName + "': Application routing changed to " + (Boolean.TRUE.equals(newEn) ? "Enabled" : "Disabled")
            ));
        }
        String oldMode = oldAr != null ? oldAr.getRoutingMode() : null;
        String newMode = newAr != null ? newAr.getRoutingMode() : null;
        if (!Objects.equals(oldMode, newMode)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Application Routing Mode",
                    oldMode,
                    newMode,
                    "Task '" + taskName + "': Application routing mode changed from '" + oldMode + "' to '" + newMode + "'"
            ));
        }
        int oldCombCount = (oldAr != null && oldAr.getCombinations() != null) ? oldAr.getCombinations().size() : 0;
        int newCombCount = (newAr != null && newAr.getCombinations() != null) ? newAr.getCombinations().size() : 0;
        if (oldCombCount != newCombCount) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Routing Combinations Count",
                    oldCombCount,
                    newCombCount,
                    "Task '" + taskName + "': Routing combinations count changed from " + oldCombCount + " to " + newCombCount
            ));
        }
    }

    private void compareEscalation(ProcessFlowDTO.DataDTO.Escalation oldEsc,
                                   ProcessFlowDTO.DataDTO.Escalation newEsc,
                                   String itemLabel,
                                   String taskName,
                                   List<ChangeItemDTO> changeItems) {
        if (oldEsc == null && newEsc == null) return;
        Boolean oldEn = oldEsc != null ? oldEsc.getEnabled() : null;
        Boolean newEn = newEsc != null ? newEsc.getEnabled() : null;
        if (!Objects.equals(oldEn, newEn)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Escalation Status",
                    oldEn,
                    newEn,
                    "Task '" + taskName + "': Escalation status changed to " + (Boolean.TRUE.equals(newEn) ? "Enabled" : "Disabled")
            ));
        }
        String oldSla = (oldEsc != null && oldEsc.getSlaPeriod() != null) ?
                (oldEsc.getSlaPeriod().getDuration() + " " + (oldEsc.getSlaPeriod().getUnit() != null ? oldEsc.getSlaPeriod().getUnit().getLabel() : "")) : null;
        String newSla = (newEsc != null && newEsc.getSlaPeriod() != null) ?
                (newEsc.getSlaPeriod().getDuration() + " " + (newEsc.getSlaPeriod().getUnit() != null ? newEsc.getSlaPeriod().getUnit().getLabel() : "")) : null;
        if (!Objects.equals(oldSla, newSla)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Escalation SLA Period",
                    oldSla,
                    newSla,
                    "Task '" + taskName + "': Escalation SLA period changed from '" + oldSla + "' to '" + newSla + "'"
            ));
        }
        String oldEscPeriod = (oldEsc != null && oldEsc.getEscalationPeriod() != null) ?
                (oldEsc.getEscalationPeriod().getDuration() + " " + (oldEsc.getEscalationPeriod().getUnit() != null ? oldEsc.getEscalationPeriod().getUnit().getLabel() : "")) : null;
        String newEscPeriod = (newEsc != null && newEsc.getEscalationPeriod() != null) ?
                (newEsc.getEscalationPeriod().getDuration() + " " + (newEsc.getEscalationPeriod().getUnit() != null ? newEsc.getEscalationPeriod().getUnit().getLabel() : "")) : null;
        if (!Objects.equals(oldEscPeriod, newEscPeriod)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Escalation Period",
                    oldEscPeriod,
                    newEscPeriod,
                    "Task '" + taskName + "': Escalation period changed from '" + oldEscPeriod + "' to '" + newEscPeriod + "'"
            ));
        }
        String oldMvel = oldEsc != null ? oldEsc.getMvelExpression() : null;
        String newMvel = newEsc != null ? newEsc.getMvelExpression() : null;
        if (!Objects.equals(oldMvel, newMvel)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Escalation MVEL Expression",
                    oldMvel,
                    newMvel,
                    "Task '" + taskName + "': Escalation MVEL expression updated"
            ));
        }
    }

    private void compareDbt(ProcessFlowDTO.Advanced.DBT oldDbt,
                            ProcessFlowDTO.Advanced.DBT newDbt,
                            String itemLabel,
                            String taskName,
                            List<ChangeItemDTO> changeItems) {
        if (oldDbt == null && newDbt == null) return;
        String oldType = oldDbt != null ? oldDbt.getType() : null;
        String newType = newDbt != null ? newDbt.getType() : null;
        if (!Objects.equals(oldType, newType)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "DBT Type",
                    oldType,
                    newType,
                    "Task '" + taskName + "': DBT type changed from '" + oldType + "' to '" + newType + "'"
            ));
        }
        String oldGen = oldDbt != null ? oldDbt.getXmlGenerationTask() : null;
        String newGen = newDbt != null ? newDbt.getXmlGenerationTask() : null;
        if (!Objects.equals(oldGen, newGen)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "DBT XML Generation Task",
                    oldGen,
                    newGen,
                    "Task '" + taskName + "': DBT XML generation task changed from '" + oldGen + "' to '" + newGen + "'"
            ));
        }
        String oldTpl = oldDbt != null ? oldDbt.getXmlTemplate() : null;
        String newTpl = newDbt != null ? newDbt.getXmlTemplate() : null;
        if (!Objects.equals(oldTpl, newTpl)) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "DBT XML Template",
                    oldTpl,
                    newTpl,
                    "Task '" + taskName + "': DBT XML template changed from '" + oldTpl + "' to '" + newTpl + "'"
            ));
        }
    }

    private void compareAssociatedTaskReferences(List<ProcessFlowDTO.DataDTO.AssociatedTaskReferenceDTO> oldAssoc,
                                                List<ProcessFlowDTO.DataDTO.AssociatedTaskReferenceDTO> newAssoc,
                                                String itemLabel,
                                                String taskName,
                                                List<ChangeItemDTO> changeItems) {
        if (oldAssoc == null && newAssoc == null) return;
        int oldCount = oldAssoc != null ? oldAssoc.size() : 0;
        int newCount = newAssoc != null ? newAssoc.size() : 0;
        if (oldCount != newCount) {
            changeItems.add(new ChangeItemDTO(
                    "Task Configuration",
                    itemLabel,
                    "Associated Tasks Count",
                    oldCount,
                    newCount,
                    "Task '" + taskName + "': Associated tasks count changed from " + oldCount + " to " + newCount
            ));
        }
    }

    private void compareRootAssociatedTasks(List<ProcessFlowDTO.AssociatedTaskDTO> oldAssoc,
                                            List<ProcessFlowDTO.AssociatedTaskDTO> newAssoc,
                                            List<ChangeItemDTO> changeItems) {
        if (oldAssoc == null && newAssoc == null) return;
        int oldCount = oldAssoc != null ? oldAssoc.size() : 0;
        int newCount = newAssoc != null ? newAssoc.size() : 0;
        if (oldCount != newCount) {
            changeItems.add(new ChangeItemDTO(
                    "Process Flow",
                    "Process Flow Definition",
                    "Associated Tasks Count",
                    oldCount + " tasks",
                    newCount + " tasks",
                    "Process flow associated tasks count changed from " + oldCount + " to " + newCount
            ));
        }
    }

    private void compareEdges(List<ProcessFlowDTO.EdgeDTO> oldEdges, List<ProcessFlowDTO.EdgeDTO> newEdges, List<ChangeItemDTO> changeItems) {
        if (oldEdges == null && newEdges == null) {
            return;
        }

        Set<String> oldEdgeKeys = (oldEdges != null) ? oldEdges.stream().filter(Objects::nonNull).map(e -> e.getSource() + " -> " + e.getTarget()).collect(Collectors.toSet()) : Collections.emptySet();
        Set<String> newEdgeKeys = (newEdges != null) ? newEdges.stream().filter(Objects::nonNull).map(e -> e.getSource() + " -> " + e.getTarget()).collect(Collectors.toSet()) : Collections.emptySet();

        for (String nk : newEdgeKeys) {
            if (!oldEdgeKeys.contains(nk)) {
                changeItems.add(new ChangeItemDTO(
                        "Process Flow Sequence",
                        "Sequence Transition",
                        "Flow Connection Added",
                        null,
                        nk,
                        "Existing process flow sequence updated: added transition " + nk
                ));
            }
        }

        for (String ok : oldEdgeKeys) {
            if (!newEdgeKeys.contains(ok)) {
                changeItems.add(new ChangeItemDTO(
                        "Process Flow Sequence",
                        "Sequence Transition",
                        "Flow Connection Removed",
                        ok,
                        null,
                        "Existing process flow sequence updated: removed transition " + ok
                ));
            }
        }
    }

    /**
     * Gets all recorded change logs for a service formatted with "old was this, new is this".
     */
    public ServiceVersionChangesResponse getDetailedChanges(Integer serviceId) {
        ServiceVersionChangesResponse response = new ServiceVersionChangesResponse();
        response.setServiceId(serviceId);
        response.setBaseServiceId(serviceId != null ? serviceId / 10000 : null);

        ServiceLog serviceLog = (serviceId != null) ? serviceLogRepository.findByServiceId(serviceId) : null;
        if (serviceLog != null) {
            response.setVersionNo(serviceLog.getVersionNo());
            response.setMinorVersionNo(serviceLog.getMinorVersionNo());
        }

        List<ServiceVersionChangeLog> logs = (serviceId != null) ?
                changeLogRepository.findByServiceIdOrderByCreatedAtDesc(serviceId) : Collections.emptyList();

        List<ServiceVersionChangesResponse.ChangeLogEntryDTO> entryList = new ArrayList<>();
        int totalChangeCount = 0;

        for (ServiceVersionChangeLog log : logs) {
            ServiceVersionChangesResponse.ChangeLogEntryDTO entry = new ServiceVersionChangesResponse.ChangeLogEntryDTO();
            entry.setId(log.getId());
            entry.setVersionChangeId(log.getVersionChangeId());
            entry.setModificationToken(log.getModificationToken());
            entry.setComponent(log.getComponent());
            entry.setAction(log.getAction());
            entry.setChangeSummary(log.getChangeSummary());
            entry.setUserId(log.getUserId());
            entry.setTenantId(log.getTenantId());
            entry.setCreatedAt(log.getCreatedAt());

            // Parse diff_details into structured ChangeItemDTO list ("old was this, new is this")
            if (log.getDiffDetails() != null && !log.getDiffDetails().isBlank()) {
                try {
                    List<ChangeItemDTO> items = objectMapper.readValue(log.getDiffDetails(), new TypeReference<List<ChangeItemDTO>>() {});
                    entry.setChangeItems(items);
                    totalChangeCount += (items != null ? items.size() : 0);
                } catch (Exception ex) {
                    logger.warn("Could not parse diffDetails for changeLog id={}: {}", log.getId(), ex.getMessage());
                }
            }

            // Parse previous and current configs into json nodes
            if (log.getPreviousConfig() != null && !log.getPreviousConfig().isBlank()) {
                try {
                    entry.setPreviousConfig(objectMapper.readTree(log.getPreviousConfig()));
                } catch (Exception ignored) {
                    entry.setPreviousConfig(log.getPreviousConfig());
                }
            }
            if (log.getCurrentConfig() != null && !log.getCurrentConfig().isBlank()) {
                try {
                    entry.setCurrentConfig(objectMapper.readTree(log.getCurrentConfig()));
                } catch (Exception ignored) {
                    entry.setCurrentConfig(log.getCurrentConfig());
                }
            }

            entryList.add(entry);
        }

        response.setTotalChanges(totalChangeCount);
        response.setChangeLogs(entryList);

        return response;
    }

    /**
     * Legacy list query method.
     */
    public List<ServiceVersionChangeLog> getChangeLogs(Integer serviceId) {
        if (serviceId == null) {
            return Collections.emptyList();
        }
        return changeLogRepository.findByServiceIdOrderByCreatedAtDesc(serviceId);
    }
}
