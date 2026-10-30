package com.serviceplus.metadata.versioning.service;

import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMapping;
import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMappingHistory;
import com.serviceplus.metadata.aadhaarConfiguration.repository.AuaApiFieldMappingRepository;
import com.serviceplus.metadata.aadhaarConfiguration.repository.IAuaApiFieldMappingHistoryRepository;
import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfiguration;
import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfigurationHistory;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationHistoryRepository;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationRepository;
import com.serviceplus.metadata.entity.ProcessFlowHistory;
import com.serviceplus.metadata.entity.ServiceChargeDetail;
import com.serviceplus.metadata.entity.ServiceChargeDetailHistory;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceDefinitionHistory;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinitionHistory;
import com.serviceplus.metadata.entity.ServiceDeliveryUnits;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitsHistory;
import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignment;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceLogHistory;
import com.serviceplus.metadata.entity.ServiceOutputFormat;
import com.serviceplus.metadata.entity.ServiceOutputFormatHistory;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfig;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfigHistory;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.entity.ServiceTemplateHistory;
import com.serviceplus.metadata.entity.WorkflowAssignment;
import com.serviceplus.metadata.entity.WorkflowAssignmentHistory;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.inboxSentboxFilterConfig.entity.InboxSentBoxFilter;
import com.serviceplus.metadata.inboxSentboxFilterConfig.entity.ServiceFilterHistory;
import com.serviceplus.metadata.inboxSentboxFilterConfig.repository.IServiceFilterHistoryRepository;
import com.serviceplus.metadata.inboxSentboxFilterConfig.repository.InboxSentBoxFilterRepository;
import com.serviceplus.metadata.mvel.entity.MvelEventMapping;
import com.serviceplus.metadata.mvel.entity.MvelEventMappingHistory;
import com.serviceplus.metadata.mvel.entity.MvelFunction;
import com.serviceplus.metadata.mvel.entity.MvelFunctionHistory;
import com.serviceplus.metadata.mvel.repository.IMvelEventMappingHistoryRepository;
import com.serviceplus.metadata.mvel.repository.IMvelEventMappingRepository;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionHistoryRepository;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionRepository;
import com.serviceplus.metadata.notification.entity.NotificationConfiguration;
import com.serviceplus.metadata.notification.entity.NotificationConfigurationHistory;
import com.serviceplus.metadata.notification.repository.INotificationConfigurationHistoryRepository;
import com.serviceplus.metadata.notification.repository.INotificationRepository;
import com.serviceplus.metadata.repository.IProcessFlowHistoryRepository;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceChargeDetailHistoryRepository;
import com.serviceplus.metadata.repository.IServiceChargeRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionHistoryRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionHistoryRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitsHistoryRepository;
import com.serviceplus.metadata.repository.IServiceLogHistoryRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceOutputFormatHistoryRepository;
import com.serviceplus.metadata.repository.IServiceOutputFormatRepository;
import com.serviceplus.metadata.repository.IServicePaymentGatewayConfigHistoryRepository;
import com.serviceplus.metadata.repository.IServiceTemplateHistoryRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import com.serviceplus.metadata.repository.ServiceDeliveryUnitsRepository;
import com.serviceplus.metadata.repository.ServiceDesignatedOfficerAssignmentRepository;
import com.serviceplus.metadata.repository.ServicePaymentGatewayConfigRepository;
import com.serviceplus.metadata.repository.WorkflowAssignmentHistoryRepository;
import com.serviceplus.metadata.repository.WorkflowAssignmentRepository;
import com.serviceplus.metadata.utility.EntityCopyUtil;
import com.serviceplus.metadata.versioning.entity.ServiceVersionChange;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.enums.VersionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.serviceplus.metadata.utility.ApplicationConstants.WORKFLOW_ASSIGNMENT_TOKEN_PREFIX;
import static com.serviceplus.metadata.utility.CommonUtil.generateUniqueToken;

@Service
public class ServiceVersionHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVersionHistoryService.class);

    private final IServiceLogHistoryRepository serviceLogHistoryRepository;
    private final IServiceDefinitionHistoryRepository serviceDefinitionHistoryRepository;
    private final IServiceDeliveryUnitDefinitionHistoryRepository serviceDeliveryUnitDefinitionHistoryRepository;
    private final IProcessFlowHistoryRepository processFlowHistoryRepository;
    private final IServiceOutputFormatHistoryRepository serviceOutputFormatHistoryRepository;
    private final IMvelFunctionHistoryRepository mvelFunctionHistoryRepository;
    private final INotificationConfigurationHistoryRepository notificationConfigurationHistoryRepository;
    private final IServiceFilterHistoryRepository serviceFilterHistoryRepository;
    private final IAuaApiFieldMappingHistoryRepository auaApiFieldMappingHistoryRepository;
    private final IServiceChargeDetailHistoryRepository serviceChargeDetailHistoryRepository;
    private final IServiceDeliveryUnitsHistoryRepository serviceDeliveryUnitsHistoryRepository;
    private final IServicePaymentGatewayConfigHistoryRepository servicePaymentGatewayConfigHistoryRepository;
    private final IServiceTemplateHistoryRepository serviceTemplateHistoryRepository;
    private final IMvelEventMappingHistoryRepository mvelEventMappingHistoryRepository;
    private final ICorrelationConfigurationHistoryRepository correlationConfigurationHistoryRepository;

    private final IServiceLogRepository serviceLogRepository;
    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final IServiceDeliveryUnitDefinitionRepository serviceDeliveryUnitDefinitionRepository;
    private final IProcessFlowRepository processFlowRepository;
    private final IServiceOutputFormatRepository serviceOutputFormatRepository;
    private final IMvelFunctionRepository mvelFunctionRepository;
    private final INotificationRepository notificationRepository;
    private final InboxSentBoxFilterRepository inboxSentBoxFilterRepository;
    private final AuaApiFieldMappingRepository auaApiFieldMappingRepository;
    private final IServiceChargeRepository serviceChargeRepository;
    private final ServiceDeliveryUnitsRepository serviceDeliveryUnitsRepository;
    private final ServicePaymentGatewayConfigRepository servicePaymentGatewayConfigRepository;
    private final IServiceTemplateRepository serviceTemplateRepository;
    private final IMvelEventMappingRepository mvelEventMappingRepository;
    private final ICorrelationConfigurationRepository correlationConfigurationRepository;

    private final ServiceDesignatedOfficerAssignmentRepository designatedOfficerAssignmentRepository;
    private final WorkflowAssignmentRepository workflowAssignmentRepository;
    private final WorkflowAssignmentHistoryRepository workflowAssignmentHistoryRepository;

    public ServiceVersionHistoryService(
            IServiceLogHistoryRepository serviceLogHistoryRepository,
            IServiceDefinitionHistoryRepository serviceDefinitionHistoryRepository,
            IServiceDeliveryUnitDefinitionHistoryRepository serviceDeliveryUnitDefinitionHistoryRepository,
            IProcessFlowHistoryRepository processFlowHistoryRepository,
            IServiceOutputFormatHistoryRepository serviceOutputFormatHistoryRepository,
            IMvelFunctionHistoryRepository mvelFunctionHistoryRepository,
            INotificationConfigurationHistoryRepository notificationConfigurationHistoryRepository,
            IServiceFilterHistoryRepository serviceFilterHistoryRepository,
            IAuaApiFieldMappingHistoryRepository auaApiFieldMappingHistoryRepository,
            IServiceChargeDetailHistoryRepository serviceChargeDetailHistoryRepository,
            IServiceDeliveryUnitsHistoryRepository serviceDeliveryUnitsHistoryRepository,
            IServicePaymentGatewayConfigHistoryRepository servicePaymentGatewayConfigHistoryRepository,
            IServiceTemplateHistoryRepository serviceTemplateHistoryRepository,
            IMvelEventMappingHistoryRepository mvelEventMappingHistoryRepository,
            ICorrelationConfigurationHistoryRepository correlationConfigurationHistoryRepository,
            IServiceLogRepository serviceLogRepository,
            IServiceDefinitionRepository serviceDefinitionRepository,
            IServiceDeliveryUnitDefinitionRepository serviceDeliveryUnitDefinitionRepository,
            IProcessFlowRepository processFlowRepository,
            IServiceOutputFormatRepository serviceOutputFormatRepository,
            IMvelFunctionRepository mvelFunctionRepository,
            INotificationRepository notificationRepository,
            InboxSentBoxFilterRepository inboxSentBoxFilterRepository,
            AuaApiFieldMappingRepository auaApiFieldMappingRepository,
            IServiceChargeRepository serviceChargeRepository,
            ServiceDeliveryUnitsRepository serviceDeliveryUnitsRepository,
            ServicePaymentGatewayConfigRepository servicePaymentGatewayConfigRepository,
            IServiceTemplateRepository serviceTemplateRepository,
            IMvelEventMappingRepository mvelEventMappingRepository,
            ICorrelationConfigurationRepository correlationConfigurationRepository,
            ServiceDesignatedOfficerAssignmentRepository designatedOfficerAssignmentRepository,
            WorkflowAssignmentRepository workflowAssignmentRepository,
            WorkflowAssignmentHistoryRepository workflowAssignmentHistoryRepository) {

        this.serviceLogHistoryRepository = serviceLogHistoryRepository;
        this.serviceDefinitionHistoryRepository = serviceDefinitionHistoryRepository;
        this.serviceDeliveryUnitDefinitionHistoryRepository = serviceDeliveryUnitDefinitionHistoryRepository;
        this.processFlowHistoryRepository = processFlowHistoryRepository;
        this.serviceOutputFormatHistoryRepository = serviceOutputFormatHistoryRepository;
        this.mvelFunctionHistoryRepository = mvelFunctionHistoryRepository;
        this.notificationConfigurationHistoryRepository = notificationConfigurationHistoryRepository;
        this.serviceFilterHistoryRepository = serviceFilterHistoryRepository;
        this.auaApiFieldMappingHistoryRepository = auaApiFieldMappingHistoryRepository;
        this.serviceChargeDetailHistoryRepository = serviceChargeDetailHistoryRepository;
        this.serviceDeliveryUnitsHistoryRepository = serviceDeliveryUnitsHistoryRepository;
        this.servicePaymentGatewayConfigHistoryRepository = servicePaymentGatewayConfigHistoryRepository;
        this.serviceTemplateHistoryRepository = serviceTemplateHistoryRepository;
        this.mvelEventMappingHistoryRepository = mvelEventMappingHistoryRepository;
        this.correlationConfigurationHistoryRepository = correlationConfigurationHistoryRepository;

        this.serviceLogRepository = serviceLogRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.serviceDeliveryUnitDefinitionRepository = serviceDeliveryUnitDefinitionRepository;
        this.processFlowRepository = processFlowRepository;
        this.serviceOutputFormatRepository = serviceOutputFormatRepository;
        this.mvelFunctionRepository = mvelFunctionRepository;
        this.notificationRepository = notificationRepository;
        this.inboxSentBoxFilterRepository = inboxSentBoxFilterRepository;
        this.auaApiFieldMappingRepository = auaApiFieldMappingRepository;
        this.serviceChargeRepository = serviceChargeRepository;
        this.serviceDeliveryUnitsRepository = serviceDeliveryUnitsRepository;
        this.servicePaymentGatewayConfigRepository = servicePaymentGatewayConfigRepository;
        this.serviceTemplateRepository = serviceTemplateRepository;
        this.mvelEventMappingRepository = mvelEventMappingRepository;
        this.correlationConfigurationRepository = correlationConfigurationRepository;

        this.designatedOfficerAssignmentRepository = designatedOfficerAssignmentRepository;
        this.workflowAssignmentRepository = workflowAssignmentRepository;
        this.workflowAssignmentHistoryRepository = workflowAssignmentHistoryRepository;
    }

    /*
     * ============================================================
     * CREATE HISTORY SNAPSHOT
     * ============================================================
     */

    @Transactional
    public void createHistorySnapshot(Integer serviceId, ServiceVersionChange change) {
        logger.info("Creating complete history snapshot. serviceId={}, modificationId={}", serviceId, change.getId());

        createServiceLogHistory(serviceId, change);
        createServiceDefinitionHistory(serviceId, change);
        createDeliveryUnitHistory(serviceId, change);
        createProcessFlowHistory(serviceId, change);
        createOutputFormatHistory(serviceId, change);
        createMvelFunctionHistory(serviceId, change);
        createNotificationHistory(serviceId, change);
        createFilterHistory(serviceId, change);
        createAuaMappingHistory(serviceId, change);
        createServiceChargeHistory(serviceId, change);
        createDeliveryUnitsHistory(serviceId, change);
        createPaymentGatewayConfigHistory(serviceId, change);
        createTemplateHistory(serviceId, change);
        createMvelEventMappingHistory(serviceId, change);
        createCorrelationConfigHistory(serviceId, change);

        logger.info("History snapshot created successfully. serviceId={}, modificationId={}", serviceId, change.getId());
    }

    private void createServiceLogHistory(Integer serviceId, ServiceVersionChange change) {
        ServiceLog source = serviceLogRepository.findByServiceId(serviceId);
        if (source != null) {
            ServiceLogHistory history = EntityCopyUtil.copy(source, ServiceLogHistory.class, "historyId");
            setHistoryMetadata(history, change);
            serviceLogHistoryRepository.save(history);
        }
    }

    private void createServiceDefinitionHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServiceDefinition> sources = serviceDefinitionRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (ServiceDefinition source : sources) {
                ServiceDefinitionHistory history = EntityCopyUtil.copy(source, ServiceDefinitionHistory.class, "historyId");
                setHistoryMetadata(history, change);
                serviceDefinitionHistoryRepository.save(history);
            }
        }
    }

    private void createDeliveryUnitHistory(Integer serviceId, ServiceVersionChange change) {
        ServiceDeliveryUnitDefinition source = serviceDeliveryUnitDefinitionRepository.findByServiceId(serviceId);
        if (source != null) {
            ServiceDeliveryUnitDefinitionHistory history = EntityCopyUtil.copy(source, ServiceDeliveryUnitDefinitionHistory.class, "historyId");
            setHistoryMetadata(history, change);
            serviceDeliveryUnitDefinitionHistoryRepository.save(history);
        }
    }

    private void createProcessFlowHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServiceProcessFlow> sources = processFlowRepository.findAllByServiceId(serviceId);
        if (sources != null) {
            for (ServiceProcessFlow source : sources) {
                ProcessFlowHistory history = EntityCopyUtil.copy(source, ProcessFlowHistory.class, "historyId");
                setHistoryMetadata(history, change);
                processFlowHistoryRepository.save(history);
            }
        }
    }

    private void createOutputFormatHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServiceOutputFormat> sources = serviceOutputFormatRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (ServiceOutputFormat source : sources) {
                ServiceOutputFormatHistory history = EntityCopyUtil.copy(source, ServiceOutputFormatHistory.class, "historyId");
                setHistoryMetadata(history, change);
                serviceOutputFormatHistoryRepository.save(history);
            }
        }
    }

    private void createMvelFunctionHistory(Integer serviceId, ServiceVersionChange change) {
        List<MvelFunction> sources = mvelFunctionRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (MvelFunction source : sources) {
                MvelFunctionHistory history = EntityCopyUtil.copy(source, MvelFunctionHistory.class, "historyId");
                setHistoryMetadata(history, change);
                mvelFunctionHistoryRepository.save(history);
            }
        }
    }

    private void createNotificationHistory(Integer serviceId, ServiceVersionChange change) {
        List<NotificationConfiguration> sources = notificationRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (NotificationConfiguration source : sources) {
                NotificationConfigurationHistory history = EntityCopyUtil.copy(source, NotificationConfigurationHistory.class, "historyId");
                setHistoryMetadata(history, change);
                notificationConfigurationHistoryRepository.save(history);
            }
        }
    }

    private void createFilterHistory(Integer serviceId, ServiceVersionChange change) {
        List<InboxSentBoxFilter> sources = inboxSentBoxFilterRepository.findByServiceId(serviceId.longValue());
        if (sources != null) {
            for (InboxSentBoxFilter source : sources) {
                ServiceFilterHistory history = EntityCopyUtil.copy(source, ServiceFilterHistory.class, "historyId");
                setHistoryMetadata(history, change);
                serviceFilterHistoryRepository.save(history);
            }
        }
    }

    private void createAuaMappingHistory(Integer serviceId, ServiceVersionChange change) {
        List<AuaApiFieldMapping> sources = auaApiFieldMappingRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (AuaApiFieldMapping source : sources) {
                AuaApiFieldMappingHistory history = EntityCopyUtil.copy(source, AuaApiFieldMappingHistory.class, "historyId");
                setHistoryMetadata(history, change);
                auaApiFieldMappingHistoryRepository.save(history);
            }
        }
    }

    private void createServiceChargeHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServiceChargeDetail> sources = serviceChargeRepository.findAllByServiceId(serviceId);
        if (sources != null) {
            for (ServiceChargeDetail source : sources) {
                ServiceChargeDetailHistory history = EntityCopyUtil.copy(source, ServiceChargeDetailHistory.class, "historyId");
                setHistoryMetadata(history, change);
                serviceChargeDetailHistoryRepository.save(history);
            }
        }
    }

    private void createDeliveryUnitsHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServiceDeliveryUnits> sources = serviceDeliveryUnitsRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (ServiceDeliveryUnits source : sources) {
                ServiceDeliveryUnitsHistory history = EntityCopyUtil.copy(source, ServiceDeliveryUnitsHistory.class, "historyId");
                setHistoryMetadata(history, change);
                serviceDeliveryUnitsHistoryRepository.save(history);
            }
        }
    }

    private void createPaymentGatewayConfigHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServicePaymentGatewayConfig> sources = servicePaymentGatewayConfigRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (ServicePaymentGatewayConfig source : sources) {
                ServicePaymentGatewayConfigHistory history = EntityCopyUtil.copy(source, ServicePaymentGatewayConfigHistory.class, "historyId");
                setHistoryMetadata(history, change);
                servicePaymentGatewayConfigHistoryRepository.save(history);
            }
        }
    }

    private void createTemplateHistory(Integer serviceId, ServiceVersionChange change) {
        List<ServiceTemplate> sources = serviceTemplateRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (ServiceTemplate source : sources) {
                ServiceTemplateHistory history = EntityCopyUtil.copy(source, ServiceTemplateHistory.class, "historyId");
                setHistoryMetadata(history, change);
                serviceTemplateHistoryRepository.save(history);
            }
        }
    }

    private void createMvelEventMappingHistory(Integer serviceId, ServiceVersionChange change) {
        List<MvelEventMapping> sources = mvelEventMappingRepository.findByServiceId(serviceId);
        if (sources != null) {
            for (MvelEventMapping source : sources) {
                MvelEventMappingHistory history = EntityCopyUtil.copy(source, MvelEventMappingHistory.class, "historyId");
                setHistoryMetadata(history, change);
                mvelEventMappingHistoryRepository.save(history);
            }
        }
    }

    private void createCorrelationConfigHistory(Integer serviceId, ServiceVersionChange change) {
        List<CorrelationConfiguration> sources = correlationConfigurationRepository.findByServiceId(serviceId).orElse(Collections.emptyList());
        if (!sources.isEmpty()) {
            for (CorrelationConfiguration source : sources) {
                CorrelationConfigurationHistory history = EntityCopyUtil.copy(source, CorrelationConfigurationHistory.class, "historyId");
                setHistoryMetadata(history, change);
                correlationConfigurationHistoryRepository.save(history);
            }
        }
    }

    private void setHistoryMetadata(Object history, ServiceVersionChange change) {
        EntityCopyUtil.setIfPresent(history, "modificationId", change.getId());
        EntityCopyUtil.setIfPresent(history, "archivedAt", new Date());

        Date cDate = (Date) EntityCopyUtil.getIfPresent(history, "cDate");
        if (cDate == null) {
            cDate = (Date) EntityCopyUtil.getIfPresent(history, "crDate");
        }
        if (cDate == null) {
            cDate = (Date) EntityCopyUtil.getIfPresent(history, "creationDate");
        }
        if (cDate == null) {
            cDate = (Date) EntityCopyUtil.getIfPresent(history, "createdOn");
        }
        if (cDate == null) {
            cDate = new Date();
        }

        if (EntityCopyUtil.hasProperty(history, "uDate") && EntityCopyUtil.getIfPresent(history, "uDate") == null) {
            EntityCopyUtil.setIfPresent(history, "uDate", cDate);
        }
        if (EntityCopyUtil.hasProperty(history, "updateDate") && EntityCopyUtil.getIfPresent(history, "updateDate") == null) {
            EntityCopyUtil.setIfPresent(history, "updateDate", cDate);
        }
        if (EntityCopyUtil.hasProperty(history, "upDate") && EntityCopyUtil.getIfPresent(history, "upDate") == null) {
            EntityCopyUtil.setIfPresent(history, "upDate", cDate);
        }
        if (EntityCopyUtil.hasProperty(history, "updatedAt") && EntityCopyUtil.getIfPresent(history, "updatedAt") == null) {
            EntityCopyUtil.setIfPresent(history, "updatedAt", LocalDateTime.now());
        }
        if (EntityCopyUtil.hasProperty(history, "modifiedOn") && EntityCopyUtil.getIfPresent(history, "modifiedOn") == null) {
            EntityCopyUtil.setIfPresent(history, "modifiedOn", cDate);
        }
        if (EntityCopyUtil.hasProperty(history, "updOn") && EntityCopyUtil.getIfPresent(history, "updOn") == null) {
            EntityCopyUtil.setIfPresent(history, "updOn", cDate);
        }
    }

    /*
     * ============================================================
     * MINOR VERSION PREPARATION
     * ============================================================
     */

    @Transactional
    public void updateMinorVersion(Integer serviceId, Integer targetMinorVersion) {
        logger.info("Updating minor version. serviceId={}, targetMinorVersion={}", serviceId, targetMinorVersion);

        ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
        if (serviceLog != null) {
            serviceLog.setMinorVersionNo(targetMinorVersion);
            serviceLog.setServiceStatus(ServiceStatus.MODIFICATION.getCode());
            serviceLogRepository.save(serviceLog);
        }

        List<ServiceDefinition> definitions = serviceDefinitionRepository.findByServiceId(serviceId);
        if (definitions != null) {
            for (ServiceDefinition item : definitions) {
                item.setMinorVersionNo(targetMinorVersion);
                serviceDefinitionRepository.save(item);
            }
        }

        ServiceDeliveryUnitDefinition deliveryUnit = serviceDeliveryUnitDefinitionRepository.findByServiceId(serviceId);
        if (deliveryUnit != null) {
            deliveryUnit.setMinorVersionNo(targetMinorVersion);
            serviceDeliveryUnitDefinitionRepository.save(deliveryUnit);
        }

        List<ServiceProcessFlow> processFlows = processFlowRepository.findAllByServiceId(serviceId);
        if (processFlows != null) {
            for (ServiceProcessFlow item : processFlows) {
                item.setMinorVersionNo(targetMinorVersion);
                processFlowRepository.save(item);
            }
        }

        List<ServiceOutputFormat> outputFormats = serviceOutputFormatRepository.findByServiceId(serviceId);
        if (outputFormats != null) {
            for (ServiceOutputFormat item : outputFormats) {
                item.setMinorVersion(targetMinorVersion);
                serviceOutputFormatRepository.save(item);
            }
        }

        List<MvelFunction> mvelFunctions = mvelFunctionRepository.findByServiceId(serviceId);
        if (mvelFunctions != null) {
            for (MvelFunction item : mvelFunctions) {
                item.setMinorVersionNo(targetMinorVersion);
                mvelFunctionRepository.save(item);
            }
        }

        List<ServiceChargeDetail> charges = serviceChargeRepository.findAllByServiceId(serviceId);
        if (charges != null) {
            for (ServiceChargeDetail item : charges) {
                item.setMinorVersion(targetMinorVersion);
                serviceChargeRepository.save(item);
            }
        }

        List<ServiceTemplate> templates = serviceTemplateRepository.findByServiceId(serviceId);
        if (templates != null) {
            for (ServiceTemplate item : templates) {
                item.setMinorVersion(targetMinorVersion);
                serviceTemplateRepository.save(item);
            }
        }

        logger.info("Minor version updated successfully. serviceId={}, minorVersion={}", serviceId, targetMinorVersion);
    }

    /*
     * ============================================================
     * COMMON COMPONENT COPYING LOGIC (MAJOR VERSION & COPY SERVICE)
     * ============================================================
     */

    public static final Set<ServiceComponent> DEFAULT_COPY_SERVICE_EXCLUSIONS = Set.of(
            ServiceComponent.NOTIFICATION,
            ServiceComponent.AUA_MAPPING,
            ServiceComponent.DESIGNATED_OFFICER_ASSIGNMENT,
            ServiceComponent.WORKFLOW_ASSIGNMENT
    );

    @Transactional
    public void copyServiceComponents(
            Integer sourceServiceId,
            Integer targetServiceId,
            Integer targetMinorVersion,
            Integer baseServiceIdOverride,
            String serviceNameOverride,
            String serviceAbbreviationOverride,
            Long userIdOverride,
            String tenantIdOverride
    ) {
        copyServiceComponents(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride,
                serviceNameOverride, serviceAbbreviationOverride, userIdOverride, tenantIdOverride, Collections.emptySet());
    }

    /**
     * Unified service copying method used by both Major Versioning and Copy Service feature.
     * Selectively copies components based on excludedComponents filter.
     */
    @Transactional
    public void copyServiceComponents(
            Integer sourceServiceId,
            Integer targetServiceId,
            Integer targetMinorVersion,
            Integer baseServiceIdOverride,
            String serviceNameOverride,
            String serviceAbbreviationOverride,
            Long userIdOverride,
            String tenantIdOverride,
            Set<ServiceComponent> excludedComponents
    ) {
        if (excludedComponents == null) {
            excludedComponents = Collections.emptySet();
        }

        logger.info("Executing copyServiceComponents from sourceServiceId={} to targetServiceId={}, targetMinorVersion={}, excludedComponents={}",
                sourceServiceId, targetServiceId, targetMinorVersion, excludedComponents);

        copyServiceLog(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride, tenantIdOverride);

        if (!excludedComponents.contains(ServiceComponent.SERVICE_DEFINITION)) {
            copyServiceDefinition(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride, serviceNameOverride, serviceAbbreviationOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.DELIVERY_UNIT)) {
            copyDeliveryUnit(sourceServiceId, targetServiceId, targetMinorVersion, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.PROCESS_FLOW)) {
            copyProcessFlow(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.OUTPUT_FORMAT)) {
            copyOutputFormat(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.MVEL_FUNCTION)) {
            copyMvelFunctions(sourceServiceId, targetServiceId, targetMinorVersion, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.NOTIFICATION)) {
            copyNotifications(sourceServiceId, targetServiceId, baseServiceIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.FILTER)) {
            copyFilters(sourceServiceId, targetServiceId, baseServiceIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.AUA_MAPPING)) {
            copyAuaMappings(sourceServiceId, targetServiceId, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.SERVICE_CHARGE)) {
            copyServiceCharges(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.DELIVERY_UNITS)) {
            copyDeliveryUnits(sourceServiceId, targetServiceId, baseServiceIdOverride, tenantIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.PAYMENT_GATEWAY_CONFIG)) {
            copyPaymentGatewayConfigs(sourceServiceId, targetServiceId, tenantIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.TEMPLATE)) {
            copyTemplates(sourceServiceId, targetServiceId, targetMinorVersion, baseServiceIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.MVEL_EVENT_MAPPING)) {
            copyMvelEventMappings(sourceServiceId, targetServiceId, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.CORRELATION_CONFIG)) {
            copyCorrelationConfigs(sourceServiceId, targetServiceId, baseServiceIdOverride, serviceNameOverride, tenantIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.DESIGNATED_OFFICER_ASSIGNMENT)) {
            copyDesignatedOfficerAssignments(sourceServiceId, targetServiceId, baseServiceIdOverride, tenantIdOverride, userIdOverride);
        }
        if (!excludedComponents.contains(ServiceComponent.WORKFLOW_ASSIGNMENT)) {
            copyWorkflowAssignments(sourceServiceId, targetServiceId, baseServiceIdOverride, tenantIdOverride, userIdOverride);
        }

        logger.info("All selected service components successfully copied from {} to {}", sourceServiceId, targetServiceId);
    }

    /**
     * Used by Major Version creation (increments serviceId, minorVersion=0, maintains baseServiceId/names).
     * By default, copies all components.
     */
    @Transactional
    public void createMajorVersion(Integer oldServiceId, Integer newServiceId, Integer targetMinorVersion) {
        createMajorVersion(oldServiceId, newServiceId, targetMinorVersion, null, null);
    }

    @Transactional
    public void createMajorVersion(Integer oldServiceId, Integer newServiceId, Integer targetMinorVersion, Long userId, String tenantId) {
        logger.info("Creating major version data copy. oldServiceId={}, newServiceId={}, targetMinorVersion={}",
                oldServiceId, newServiceId, targetMinorVersion);
        copyServiceComponents(oldServiceId, newServiceId, targetMinorVersion, null, null, null, userId, tenantId, Collections.emptySet());
        deleteCurrentVersion(oldServiceId);
    }

    /**
     * Used by Copy Service feature (brand new service, minorVersion=0, new baseServiceId, optional new name/abbrev).
     * Applies default component restrictions (e.g. omits SMS config, Aadhaar config) unless overridden.
     */
    @Transactional
    public void copyServiceData(Integer sourceServiceId, Integer newServiceId, Integer newBaseServiceId, String newServiceName, String newServiceAbbreviation, Long userId, String tenantId) {
        copyServiceData(sourceServiceId, newServiceId, newBaseServiceId, newServiceName, newServiceAbbreviation, userId, tenantId, DEFAULT_COPY_SERVICE_EXCLUSIONS);
    }

    @Transactional
    public void copyServiceData(Integer sourceServiceId, Integer newServiceId, Integer newBaseServiceId, String newServiceName, String newServiceAbbreviation, Long userId, String tenantId, Set<ServiceComponent> excludedComponents) {
        Set<ServiceComponent> effectiveExclusions = (excludedComponents != null) ? excludedComponents : DEFAULT_COPY_SERVICE_EXCLUSIONS;
        logger.info("Executing copyServiceData from sourceServiceId={} to newServiceId={}, exclusions={}", sourceServiceId, newServiceId, effectiveExclusions);
        copyServiceComponents(sourceServiceId, newServiceId, 0, newBaseServiceId, newServiceName, newServiceAbbreviation, userId, tenantId, effectiveExclusions);
    }
    private void copyServiceLog(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Integer baseServiceIdOverride, String tenantIdOverride) {
        ServiceLog source = serviceLogRepository.findByServiceId(sourceServiceId);
        if (source == null) {
            throw new RuntimeException("Service log not found for serviceId: " + sourceServiceId);
        }

        Map<String, Object> overrides = new HashMap<>();
        overrides.put("serviceId", targetServiceId);
        overrides.put("minorVersionNo", minorVersion != null ? minorVersion : 0);
        overrides.put("serviceStatus", 0); // new service status
        overrides.put("launchedBy", null);
        overrides.put("launchApprovedBy", null);
        overrides.put("launchedDate", null);
        overrides.put("cDate", new Date());
        overrides.put("uDate", new Date());

        if (baseServiceIdOverride != null) {
            overrides.put("baseServiceId", baseServiceIdOverride);
            overrides.put("versionNo", 1);
        }
        if (tenantIdOverride != null) {
            overrides.put("tenantId", tenantIdOverride);
        }

        ServiceLog target = EntityCopyUtil.copy(
                source,
                ServiceLog.class,
                overrides,
                "serviceId"
        );
        serviceLogRepository.save(target);
    }

    private void copyServiceDefinition(
            Integer sourceServiceId,
            Integer targetServiceId,
            Integer minorVersion,
            Integer baseServiceIdOverride,
            String serviceNameOverride,
            String serviceAbbreviationOverride,
            Long userIdOverride
    ) {
        List<ServiceDefinition> sources = serviceDefinitionRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceDefinition sourceDef : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("minorVersionNo", minorVersion != null ? minorVersion : 0);
                overrides.put("cDate", new Date());
                overrides.put("uDate", new Date());

                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                    overrides.put("versionNo", 1);
                }
                if (serviceNameOverride != null && !serviceNameOverride.isBlank()) {
                    overrides.put("serviceName", serviceNameOverride);
                }
                if (serviceAbbreviationOverride != null && !serviceAbbreviationOverride.isBlank()) {
                    overrides.put("serviceAbbreviation", serviceAbbreviationOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("userId", userIdOverride);
                }

                ServiceDefinition targetDef = EntityCopyUtil.copy(
                        sourceDef,
                        ServiceDefinition.class,
                        overrides,
                        "serviceId"
                );
                serviceDefinitionRepository.save(targetDef);
            }
        }
    }

    private void copyDeliveryUnit(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Long userIdOverride) {
        ServiceDeliveryUnitDefinition source = serviceDeliveryUnitDefinitionRepository.findByServiceId(sourceServiceId);
        if (source != null) {
            Map<String, Object> overrides = new HashMap<>();
            overrides.put("serviceId", targetServiceId);
            overrides.put("minorVersionNo", minorVersion != null ? minorVersion : 0);
            overrides.put("creationDate", new Date());
            overrides.put("updateDate", new Date());
            if (userIdOverride != null) {
                overrides.put("userId", userIdOverride);
            }

            ServiceDeliveryUnitDefinition target = EntityCopyUtil.copy(
                    source,
                    ServiceDeliveryUnitDefinition.class,
                    overrides,
                    "serviceId"
            );
            serviceDeliveryUnitDefinitionRepository.save(target);
        }
    }

    private void copyProcessFlow(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Integer baseServiceIdOverride, Long userIdOverride) {
        List<ServiceProcessFlow> sources = processFlowRepository.findAllByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceProcessFlow source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("minorVersionNo", minorVersion != null ? minorVersion : 0);
                overrides.put("cDate", new Date());
                overrides.put("uDate", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("userId", userIdOverride);
                }

                ServiceProcessFlow target = EntityCopyUtil.copy(
                        source,
                        ServiceProcessFlow.class,
                        overrides,
                        "id", "serviceId"
                );
                processFlowRepository.save(target);
            }
        }
    }

    private void copyOutputFormat(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Integer baseServiceIdOverride, Long userIdOverride) {
        List<ServiceOutputFormat> sources = serviceOutputFormatRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceOutputFormat source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("minorVersion", minorVersion != null ? minorVersion : 0);
                overrides.put("cDate", new Date());
                overrides.put("uDate", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("userId", userIdOverride);
                }

                ServiceOutputFormat target = EntityCopyUtil.copy(
                        source,
                        ServiceOutputFormat.class,
                        overrides,
                        "id", "serviceId"
                );
                serviceOutputFormatRepository.save(target);
            }
        }
    }

    private void copyMvelFunctions(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Long userIdOverride) {
        List<MvelFunction> sources = mvelFunctionRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (MvelFunction source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("minorVersionNo", minorVersion != null ? minorVersion : 0);
                overrides.put("createdOn", new Date());
                overrides.put("modifiedOn", new Date());
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                    overrides.put("modifiedBy", userIdOverride);
                }

                MvelFunction target = EntityCopyUtil.copy(
                        source,
                        MvelFunction.class,
                        overrides,
                        "id", "serviceId"
                );
                mvelFunctionRepository.save(target);
            }
        }
    }

    private void copyNotifications(Integer sourceServiceId, Integer targetServiceId, Integer baseServiceIdOverride, Long userIdOverride) {
        List<NotificationConfiguration> sources = notificationRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (NotificationConfiguration source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("createdOn", new Date());
                overrides.put("modifiedOn", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                    overrides.put("modifiedBy", userIdOverride);
                }

                NotificationConfiguration target = EntityCopyUtil.copy(
                        source,
                        NotificationConfiguration.class,
                        overrides,
                        "notificationId", "serviceId"
                );
                notificationRepository.save(target);
            }
        }
    }

    private void copyFilters(Integer sourceServiceId, Integer targetServiceId, Integer baseServiceIdOverride) {
        List<InboxSentBoxFilter> sources = inboxSentBoxFilterRepository.findByServiceId(sourceServiceId.longValue());
        if (sources != null) {
            for (InboxSentBoxFilter source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId.longValue());
                overrides.put("crDate", new Date());
                overrides.put("upDate", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride.longValue());
                }

                InboxSentBoxFilter target = EntityCopyUtil.copy(
                        source,
                        InboxSentBoxFilter.class,
                        overrides,
                        "id", "serviceId"
                );
                inboxSentBoxFilterRepository.save(target);
            }
        }
    }

    private void copyAuaMappings(Integer sourceServiceId, Integer targetServiceId, Long userIdOverride) {
        List<AuaApiFieldMapping> sources = auaApiFieldMappingRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (AuaApiFieldMapping source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("createdAt", java.time.OffsetDateTime.now());
                overrides.put("updatedAt", java.time.OffsetDateTime.now());
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                    overrides.put("updatedBy", userIdOverride);
                }

                AuaApiFieldMapping target = EntityCopyUtil.copy(
                        source,
                        AuaApiFieldMapping.class,
                        overrides,
                        "id", "serviceId"
                );
                auaApiFieldMappingRepository.save(target);
            }
        }
    }

    private void copyServiceCharges(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Integer baseServiceIdOverride, Long userIdOverride) {
        List<ServiceChargeDetail> sources = serviceChargeRepository.findAllByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceChargeDetail source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("minorVersion", minorVersion != null ? minorVersion : 0);
                overrides.put("cDate", new Date());
                overrides.put("uDate", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("userId", userIdOverride);
                }

                ServiceChargeDetail target = EntityCopyUtil.copy(
                        source,
                        ServiceChargeDetail.class,
                        overrides,
                        "id", "serviceId"
                );
                serviceChargeRepository.save(target);
            }
        }
    }

    private void copyDeliveryUnits(Integer sourceServiceId, Integer targetServiceId, Integer baseServiceIdOverride, String tenantIdOverride, Long userIdOverride) {
        List<ServiceDeliveryUnits> sources = serviceDeliveryUnitsRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceDeliveryUnits source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("createdOn", new Date());
                overrides.put("modifiedOn", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (tenantIdOverride != null) {
                    overrides.put("tenantId", tenantIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                    overrides.put("modifiedBy", userIdOverride);
                }

                ServiceDeliveryUnits target = EntityCopyUtil.copy(
                        source,
                        ServiceDeliveryUnits.class,
                        overrides,
                        "id", "serviceId"
                );
                serviceDeliveryUnitsRepository.save(target);
            }
        }
    }

    private void copyPaymentGatewayConfigs(Integer sourceServiceId, Integer targetServiceId, String tenantIdOverride, Long userIdOverride) {
        List<ServicePaymentGatewayConfig> sources = servicePaymentGatewayConfigRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServicePaymentGatewayConfig source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("createdAt", LocalDateTime.now());
                overrides.put("updatedAt", LocalDateTime.now());
                if (tenantIdOverride != null) {
                    overrides.put("tenantId", tenantIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                }

                ServicePaymentGatewayConfig target = EntityCopyUtil.copy(
                        source,
                        ServicePaymentGatewayConfig.class,
                        overrides,
                        "id", "serviceId"
                );
                servicePaymentGatewayConfigRepository.save(target);
            }
        }
    }

    private void copyTemplates(Integer sourceServiceId, Integer targetServiceId, Integer minorVersion, Integer baseServiceIdOverride, Long userIdOverride) {
        List<ServiceTemplate> sources = serviceTemplateRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceTemplate source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("minorVersion", minorVersion != null ? minorVersion : 0);
                overrides.put("cDate", new Date());
                overrides.put("uDate", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                    overrides.put("versionNo", 1);
                }
                if (userIdOverride != null) {
                    overrides.put("userId", userIdOverride);
                }

                ServiceTemplate target = EntityCopyUtil.copy(
                        source,
                        ServiceTemplate.class,
                        overrides,
                        "id", "serviceId"
                );
                serviceTemplateRepository.save(target);
            }
        }
    }

    private void copyMvelEventMappings(Integer sourceServiceId, Integer targetServiceId, Long userIdOverride) {
        List<MvelEventMapping> sources = mvelEventMappingRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (MvelEventMapping source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("createdOn", new Date());
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                }

                MvelEventMapping target = EntityCopyUtil.copy(
                        source,
                        MvelEventMapping.class,
                        overrides,
                        "eventId", "serviceId"
                );
                mvelEventMappingRepository.save(target);
            }
        }
    }

    private void copyCorrelationConfigs(Integer sourceServiceId, Integer targetServiceId, Integer baseServiceIdOverride, String serviceNameOverride, String tenantIdOverride, Long userIdOverride) {
        List<CorrelationConfiguration> sources = correlationConfigurationRepository.findByServiceId(sourceServiceId).orElse(Collections.emptyList());
        if (!sources.isEmpty()) {
            for (CorrelationConfiguration source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("crDate", new Date());
                overrides.put("upDate", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (serviceNameOverride != null && !serviceNameOverride.isBlank()) {
                    overrides.put("serviceName", serviceNameOverride);
                }
                if (tenantIdOverride != null) {
                    overrides.put("tenantId", tenantIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("crBy", userIdOverride);
                    overrides.put("upBy", userIdOverride);
                }

                CorrelationConfiguration target = EntityCopyUtil.copy(
                        source,
                        CorrelationConfiguration.class,
                        overrides,
                        "id", "serviceId"
                );
                correlationConfigurationRepository.save(target);
            }
        }
    }

    private void copyDesignatedOfficerAssignments(Integer sourceServiceId, Integer targetServiceId, Integer baseServiceIdOverride, String tenantIdOverride, Long userIdOverride) {
        List<ServiceDesignatedOfficerAssignment> sources = designatedOfficerAssignmentRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (ServiceDesignatedOfficerAssignment source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("createdOn", new Date());
                overrides.put("modifiedOn", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                }
                if (tenantIdOverride != null) {
                    overrides.put("tenantId", tenantIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("createdBy", userIdOverride);
                    overrides.put("modifiedBy", userIdOverride);
                }

                ServiceDesignatedOfficerAssignment target = EntityCopyUtil.copy(
                        source,
                        ServiceDesignatedOfficerAssignment.class,
                        overrides,
                        "assignmentId", "serviceId"
                );
                designatedOfficerAssignmentRepository.save(target);
            }
        }
    }

    private void copyWorkflowAssignments(Integer sourceServiceId, Integer targetServiceId, Integer baseServiceIdOverride, String tenantIdOverride, Long userIdOverride) {
        List<WorkflowAssignment> sources = workflowAssignmentRepository.findByServiceId(sourceServiceId);
        if (sources != null) {
            for (WorkflowAssignment source : sources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("holderId", generateUniqueToken(WORKFLOW_ASSIGNMENT_TOKEN_PREFIX));
                overrides.put("serviceId", targetServiceId);
                overrides.put("crtOn", new Date());
                overrides.put("updOn", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                    overrides.put("versionNo", 1);
                }
                if (tenantIdOverride != null) {
                    overrides.put("tenantId", tenantIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("assignedBy", userIdOverride);
                }

                WorkflowAssignment target = EntityCopyUtil.copy(
                        source,
                        WorkflowAssignment.class,
                        overrides,
                        "holderId", "serviceId"
                );
                workflowAssignmentRepository.save(target);
            }
        }

        List<WorkflowAssignmentHistory> historySources = workflowAssignmentHistoryRepository.findByServiceId(sourceServiceId);
        if (historySources != null) {
            for (WorkflowAssignmentHistory source : historySources) {
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("serviceId", targetServiceId);
                overrides.put("crtOn", new Date());
                overrides.put("updOn", new Date());
                if (baseServiceIdOverride != null) {
                    overrides.put("baseServiceId", baseServiceIdOverride);
                    overrides.put("versionNo", 1);
                }
                if (tenantIdOverride != null) {
                    overrides.put("tenantId", tenantIdOverride);
                }
                if (userIdOverride != null) {
                    overrides.put("assignedBy", userIdOverride);
                }

                WorkflowAssignmentHistory target = EntityCopyUtil.copy(
                        source,
                        WorkflowAssignmentHistory.class,
                        overrides,
                        "id", "serviceId"
                );
                workflowAssignmentHistoryRepository.save(target);
            }
        }
    }

    /*
     * ============================================================
     * RESTORE & ROLLBACK
     * ============================================================
     */

    @Transactional
    public void restoreHistorySnapshot(ServiceVersionChange change) {
        logger.info("Restoring modification. modificationId={}, type={}", change.getId(), change.getRequestedVersionType());

        if (change.getRequestedVersionType() == VersionType.MAJOR) {
            restoreMajorVersion(change);
        } else {
            restoreMinorVersion(change);
        }
    }

    private void restoreMajorVersion(ServiceVersionChange change) {
        Integer targetServiceId = change.getTargetServiceId();
        if (targetServiceId == null) {
            throw new RuntimeException("Target serviceId is missing for major version rollback");
        }
        deleteCurrentVersion(targetServiceId);
        restoreMinorVersion(change);
        logger.info("Major version reverted completely. targetServiceId={}, restoredServiceId={}",
                targetServiceId, change.getServiceId());
    }

    private void restoreMinorVersion(ServiceVersionChange change) {
        Integer serviceId = change.getServiceId();

        restoreServiceLogHistory(change);
        restoreServiceDefinitionHistory(change);
        restoreDeliveryUnitHistory(change);
        restoreProcessFlowHistory(change);
        restoreOutputFormatHistory(change);
        restoreMvelFunctionHistory(change);
        restoreNotificationHistory(change);
        restoreFilterHistory(change);
        restoreAuaMappingHistory(change);
        restoreServiceChargeHistory(change);
        restoreDeliveryUnitsHistory(change);
        restorePaymentGatewayConfigHistory(change);
        restoreTemplateHistory(change);
        restoreMvelEventMappingHistory(change);
        restoreCorrelationConfigHistory(change);

        logger.info("Minor version reverted. serviceId={}, restoredMinorVersion={}",
                serviceId, change.getCurrentMinorVersionNo());
    }

    private void restoreServiceLogHistory(ServiceVersionChange change) {
        List<ServiceLogHistory> history = serviceLogHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceLogRepository.deleteByServiceId(change.getServiceId());
            for (ServiceLogHistory item : history) {
                ServiceLog target = EntityCopyUtil.copy(item, ServiceLog.class, "historyId", "modificationId", "archivedAt");
                serviceLogRepository.save(target);
            }
        }
    }

    private void restoreServiceDefinitionHistory(ServiceVersionChange change) {
        List<ServiceDefinitionHistory> history = serviceDefinitionHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceDefinitionRepository.deleteByServiceId(change.getServiceId());
            for (ServiceDefinitionHistory item : history) {
                ServiceDefinition target = EntityCopyUtil.copy(item, ServiceDefinition.class, "historyId", "modificationId", "archivedAt");
                serviceDefinitionRepository.save(target);
            }
        }
    }

    private void restoreDeliveryUnitHistory(ServiceVersionChange change) {
        List<ServiceDeliveryUnitDefinitionHistory> history = serviceDeliveryUnitDefinitionHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceDeliveryUnitDefinitionRepository.deleteByServiceId(change.getServiceId());
            for (ServiceDeliveryUnitDefinitionHistory item : history) {
                ServiceDeliveryUnitDefinition target = EntityCopyUtil.copy(item, ServiceDeliveryUnitDefinition.class, "historyId", "modificationId", "archivedAt");
                serviceDeliveryUnitDefinitionRepository.save(target);
            }
        }
    }

    private void restoreProcessFlowHistory(ServiceVersionChange change) {
        List<ProcessFlowHistory> history = processFlowHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            processFlowRepository.deleteByServiceId(change.getServiceId());
            for (ProcessFlowHistory item : history) {
                ServiceProcessFlow target = EntityCopyUtil.copy(item, ServiceProcessFlow.class, "historyId", "modificationId", "archivedAt", "id");
                processFlowRepository.save(target);
            }
        }
    }

    private void restoreOutputFormatHistory(ServiceVersionChange change) {
        List<ServiceOutputFormatHistory> history = serviceOutputFormatHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceOutputFormatRepository.deleteByServiceId(change.getServiceId());
            for (ServiceOutputFormatHistory item : history) {
                ServiceOutputFormat target = EntityCopyUtil.copy(item, ServiceOutputFormat.class, "historyId", "modificationId", "archivedAt", "id");
                serviceOutputFormatRepository.save(target);
            }
        }
    }

    private void restoreMvelFunctionHistory(ServiceVersionChange change) {
        List<MvelFunctionHistory> history = mvelFunctionHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            mvelFunctionRepository.deleteByServiceId(change.getServiceId());
            for (MvelFunctionHistory item : history) {
                MvelFunction target = EntityCopyUtil.copy(item, MvelFunction.class, "historyId", "modificationId", "archivedAt", "id");
                mvelFunctionRepository.save(target);
            }
        }
    }

    private void restoreNotificationHistory(ServiceVersionChange change) {
        List<NotificationConfigurationHistory> history = notificationConfigurationHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            notificationRepository.deleteByServiceId(change.getServiceId());
            for (NotificationConfigurationHistory item : history) {
                NotificationConfiguration target = EntityCopyUtil.copy(item, NotificationConfiguration.class, "historyId", "modificationId", "archivedAt", "notificationId");
                notificationRepository.save(target);
            }
        }
    }

    private void restoreFilterHistory(ServiceVersionChange change) {
        List<ServiceFilterHistory> history = serviceFilterHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            inboxSentBoxFilterRepository.deleteByServiceId(change.getServiceId().longValue());
            for (ServiceFilterHistory item : history) {
                InboxSentBoxFilter target = EntityCopyUtil.copy(item, InboxSentBoxFilter.class, "historyId", "modificationId", "archivedAt", "id");
                inboxSentBoxFilterRepository.save(target);
            }
        }
    }

    private void restoreAuaMappingHistory(ServiceVersionChange change) {
        List<AuaApiFieldMappingHistory> history = auaApiFieldMappingHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            auaApiFieldMappingRepository.deleteByServiceId(change.getServiceId());
            for (AuaApiFieldMappingHistory item : history) {
                AuaApiFieldMapping target = EntityCopyUtil.copy(item, AuaApiFieldMapping.class, "historyId", "modificationId", "archivedAt", "id");
                auaApiFieldMappingRepository.save(target);
            }
        }
    }

    private void restoreServiceChargeHistory(ServiceVersionChange change) {
        List<ServiceChargeDetailHistory> history = serviceChargeDetailHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceChargeRepository.deleteByServiceId(change.getServiceId());
            for (ServiceChargeDetailHistory item : history) {
                ServiceChargeDetail target = EntityCopyUtil.copy(item, ServiceChargeDetail.class, "historyId", "modificationId", "archivedAt", "id");
                serviceChargeRepository.save(target);
            }
        }
    }

    private void restoreDeliveryUnitsHistory(ServiceVersionChange change) {
        List<ServiceDeliveryUnitsHistory> history = serviceDeliveryUnitsHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceDeliveryUnitsRepository.deleteByServiceId(change.getServiceId());
            for (ServiceDeliveryUnitsHistory item : history) {
                ServiceDeliveryUnits target = EntityCopyUtil.copy(item, ServiceDeliveryUnits.class, "historyId", "modificationId", "archivedAt", "id");
                serviceDeliveryUnitsRepository.save(target);
            }
        }
    }

    private void restorePaymentGatewayConfigHistory(ServiceVersionChange change) {
        List<ServicePaymentGatewayConfigHistory> history = servicePaymentGatewayConfigHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            servicePaymentGatewayConfigRepository.deleteByServiceId(change.getServiceId());
            for (ServicePaymentGatewayConfigHistory item : history) {
                ServicePaymentGatewayConfig target = EntityCopyUtil.copy(item, ServicePaymentGatewayConfig.class, "historyId", "modificationId", "archivedAt", "id");
                servicePaymentGatewayConfigRepository.save(target);
            }
        }
    }

    private void restoreTemplateHistory(ServiceVersionChange change) {
        List<ServiceTemplateHistory> history = serviceTemplateHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            serviceTemplateRepository.deleteByServiceId(change.getServiceId());
            for (ServiceTemplateHistory item : history) {
                ServiceTemplate target = EntityCopyUtil.copy(item, ServiceTemplate.class, "historyId", "modificationId", "archivedAt", "id");
                serviceTemplateRepository.save(target);
            }
        }
    }

    private void restoreMvelEventMappingHistory(ServiceVersionChange change) {
        List<MvelEventMappingHistory> history = mvelEventMappingHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            mvelEventMappingRepository.deleteByServiceId(change.getServiceId());
            for (MvelEventMappingHistory item : history) {
                MvelEventMapping target = EntityCopyUtil.copy(item, MvelEventMapping.class, "historyId", "modificationId", "archivedAt", "eventId");
                mvelEventMappingRepository.save(target);
            }
        }
    }

    private void restoreCorrelationConfigHistory(ServiceVersionChange change) {
        List<CorrelationConfigurationHistory> history = correlationConfigurationHistoryRepository.findByModificationId(change.getId());
        if (history != null && !history.isEmpty()) {
            correlationConfigurationRepository.deleteByServiceId(change.getServiceId());
            for (CorrelationConfigurationHistory item : history) {
                CorrelationConfiguration target = EntityCopyUtil.copy(item, CorrelationConfiguration.class, "historyId", "modificationId", "archivedAt", "id");
                correlationConfigurationRepository.save(target);
            }
        }
    }

    /*
     * ============================================================
     * DELETE TARGET CURRENT VERSION (FOR MAJOR CANCEL)
     * ============================================================
     */

    private void deleteCurrentVersion(Integer serviceId) {
        logger.info("Deleting target major version data. serviceId={}", serviceId);

        workflowAssignmentHistoryRepository.deleteByServiceId(serviceId);
        workflowAssignmentRepository.deleteByServiceId(serviceId);
        designatedOfficerAssignmentRepository.deleteByServiceId(serviceId);
        correlationConfigurationRepository.deleteByServiceId(serviceId);
        mvelEventMappingRepository.deleteByServiceId(serviceId);
        serviceTemplateRepository.deleteByServiceId(serviceId);
        servicePaymentGatewayConfigRepository.deleteByServiceId(serviceId);
        serviceDeliveryUnitsRepository.deleteByServiceId(serviceId);
        serviceChargeRepository.deleteByServiceId(serviceId);
        auaApiFieldMappingRepository.deleteByServiceId(serviceId);
        inboxSentBoxFilterRepository.deleteByServiceId(serviceId.longValue());
        notificationRepository.deleteByServiceId(serviceId);
        mvelFunctionRepository.deleteByServiceId(serviceId);
        serviceOutputFormatRepository.deleteByServiceId(serviceId);
        processFlowRepository.deleteByServiceId(serviceId);
        serviceDeliveryUnitDefinitionRepository.deleteByServiceId(serviceId);
        serviceDefinitionRepository.deleteByServiceId(serviceId);
        serviceLogRepository.deleteByServiceId(serviceId);

        logger.info("Target major version data deleted successfully. serviceId={}", serviceId);
    }
}