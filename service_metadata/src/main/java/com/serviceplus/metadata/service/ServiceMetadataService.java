package com.serviceplus.metadata.service;

import static com.serviceplus.metadata.enums.TaskType.APPLICANT_TASK;
import static com.serviceplus.metadata.utility.ApplicationConstants.GATEWAY_BEHAVIOUR_EXCLUSIVE_DIVERGENT;
import static com.serviceplus.metadata.utility.ApplicationConstants.GATEWAY_BEHAVIOUR_INCLUSIVE_DIVERGENT;
import static com.serviceplus.metadata.utility.ApplicationConstants.TYPE_GATEWAY;
import static com.serviceplus.metadata.utility.ApplicationConstants.TYPE_TASK;
import static com.serviceplus.metadata.utility.CommonUtil.createAttributeId;
import static com.serviceplus.metadata.utility.CommonUtil.entityToString;
import static com.serviceplus.metadata.utility.SnowflakeIdGenerator.createUniqueId;
import static java.util.Objects.isNull;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarApiConfiguration;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarConfiguration;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarNodeConfiguration;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarNodeValueConfiguration;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarPayloadConfiguration;
import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMapping;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarMappingType;
import com.serviceplus.metadata.aadhaarConfiguration.repository.AuaApiFieldMappingRepository;
import com.serviceplus.metadata.correlationConfig.dto.CorrelationFieldDTO;
import com.serviceplus.metadata.correlationConfig.dto.CorrelationSetDTO;
import com.serviceplus.metadata.correlationConfig.dto.IdentifierConfigure;
import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfiguration;
import com.serviceplus.metadata.correlationConfig.kafka.service.CorrelationSetKafkaProducer;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationRepository;
import com.serviceplus.metadata.correlationmapping.dto.CombinationSetDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingRequestDTO.ApprovedSetDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalServiceDataConfigurationDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalServiceDataConfigurationDTO.ApplicationStatusDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalServiceDataConfigurationDTO.AttributeMappingDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalServiceDataConfigurationDTO.ExternalServiceSetDTO;
import com.serviceplus.metadata.correlationmapping.dto.ServiceCombinationConfigurationDTO;
import com.serviceplus.metadata.correlationmapping.entity.ExternalDataCombination;
import com.serviceplus.metadata.correlationmapping.kafka.service.ServiceCombinationKafkaProducer;
import com.serviceplus.metadata.correlationmapping.repository.IExternalDataCombinationRepository;
import com.serviceplus.metadata.departmentConfig.dto.BsaConfigurationKafkaDTO;
import com.serviceplus.metadata.departmentConfig.dto.BsaServiceMappingDTO;
import com.serviceplus.metadata.departmentConfig.entity.BsaConfiguration;
import com.serviceplus.metadata.departmentConfig.kafka.BsaKafkaProducer;
import com.serviceplus.metadata.departmentConfig.service.BsaServiceMappingService;
import com.serviceplus.metadata.document.mapping.dto.DocMappingDTO;
import com.serviceplus.metadata.document.mapping.dto.TaskDocs;
import com.serviceplus.metadata.dto.ActivityMapDTO;
import com.serviceplus.metadata.dto.AttrMappingDetails;
import com.serviceplus.metadata.dto.AuaApiConfigurationDTO;
import com.serviceplus.metadata.dto.ChargeTemplateTaskDTO;
import com.serviceplus.metadata.dto.DesignationRequestDTO;
import com.serviceplus.metadata.dto.DocumentGenerationDetails;
import com.serviceplus.metadata.dto.EligibleServiceDTO;
import com.serviceplus.metadata.dto.Enclosure;
import com.serviceplus.metadata.dto.EscalationDetailsDTO;
import com.serviceplus.metadata.dto.ExternalAttributeMappingDTO;
import com.serviceplus.metadata.dto.ExternalClientFormAttributesMappingDTO;
import com.serviceplus.metadata.dto.FormBlankJsonDTO;
import com.serviceplus.metadata.dto.FormDTO;
import com.serviceplus.metadata.dto.FormDTO.Attribute;
import com.serviceplus.metadata.dto.HierarchyDetailsDTO;
import com.serviceplus.metadata.dto.HierarchyLabelValue;
import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.dto.LevelEntityRequestDTO;
import com.serviceplus.metadata.dto.LevelWiseUnitsDTO;
import com.serviceplus.metadata.dto.MvelDetailsDTO;
import com.serviceplus.metadata.dto.NotificationConfigurationKafkaDTO;
import com.serviceplus.metadata.dto.NotificationDetails;
import com.serviceplus.metadata.dto.OfficeDetailsDTO;
import com.serviceplus.metadata.dto.OfficeDetailsDTO.OfficeUnitData;
import com.serviceplus.metadata.dto.OutputFormatFreezeDTO;
import com.serviceplus.metadata.dto.PaymentConfigurationDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.ApplicationRouting;
import com.serviceplus.metadata.dto.ProcessFlowDTO.AssociatedTaskDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.DataDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.DataDTO.Action;
import com.serviceplus.metadata.dto.ProcessFlowDTO.DataDTO.AssociatedTaskReferenceDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.DataDTO.Escalation;
import com.serviceplus.metadata.dto.ProcessFlowDTO.EdgeDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.EditForm;
import com.serviceplus.metadata.dto.ProcessFlowDTO.EditForm.EditControl;
import com.serviceplus.metadata.dto.ProcessFlowDTO.EditForm.EditFormData;
import com.serviceplus.metadata.dto.ProcessFlowDTO.Entities;
import com.serviceplus.metadata.dto.ProcessFlowDTO.ExternalSystemDetail;
import com.serviceplus.metadata.dto.ProcessFlowDTO.FormDetail;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.Static;
import com.serviceplus.metadata.dto.ProcessFlowDTO.Timer;
import com.serviceplus.metadata.dto.ProcessFlowDTO.WebserviceDetails;
import com.serviceplus.metadata.dto.SduActivationRequestDTO;
import com.serviceplus.metadata.dto.ServiceActivationDTO;
import com.serviceplus.metadata.dto.ServiceAssignmentDTO;
import com.serviceplus.metadata.dto.ServiceChargeDTO;
import com.serviceplus.metadata.dto.ServiceChargeDTO.PaymentModeDTO;
import com.serviceplus.metadata.dto.ServiceChargeResDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO.LogoDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitMappingDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitResponseDTO;
import com.serviceplus.metadata.dto.ServiceFormAttributeDetailsDTO;
import com.serviceplus.metadata.dto.ServiceFormDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceJSONDTO;
import com.serviceplus.metadata.dto.ServiceListDetailsDTO;
import com.serviceplus.metadata.dto.ServiceOutputFormatDTO;
import com.serviceplus.metadata.dto.ServiceParameterDetailsDTO;
import com.serviceplus.metadata.dto.ServiceParameterDetailsDTO.TaskDetails;
import com.serviceplus.metadata.dto.ServiceProcessFlowDTO;
import com.serviceplus.metadata.dto.ServiceProcessFlowDTO.Data.EditPermissionData;
import com.serviceplus.metadata.dto.ServiceProcessFlowDTO.TaskRelationDTO;
import com.serviceplus.metadata.dto.ServiceTemplateDTO;
import com.serviceplus.metadata.dto.ServiceTemplateMappingDTO;
import com.serviceplus.metadata.dto.ServiceTrackingConfigDTO;
import com.serviceplus.metadata.dto.TaskAssignmentDTO;
import com.serviceplus.metadata.dto.TaskAssignmentHierarchyDTO;
import com.serviceplus.metadata.dto.TaskDTO;
import com.serviceplus.metadata.dto.TaskDTO.Task;
import com.serviceplus.metadata.dto.TaskPaymentMappingDTO;
import com.serviceplus.metadata.dto.TaskPropertyDetails;
import com.serviceplus.metadata.dto.TaskPropertyDetailsEvent;
import com.serviceplus.metadata.dto.TimerTaskDTO;
import com.serviceplus.metadata.dto.UsedAttributeListDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.dto.UserSessionDTO.Roles;
import com.serviceplus.metadata.dto.WebServiceTaskDTO;
import com.serviceplus.metadata.dto.WorkFlowDataDTO;
import com.serviceplus.metadata.dto.WorkFlowDataDTO.WorkFlowAction;
import com.serviceplus.metadata.dto.XSDDetail;
import com.serviceplus.metadata.entity.ServiceChargeDetail;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.entity.ServiceDeliveryUnits;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMasterData;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.entity.ServiceOutputFormat;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfig;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.entity.ServiceTrackingConfig;
import com.serviceplus.metadata.entity.SpeSequenceLastNo;
import com.serviceplus.metadata.entity.SystemVariableMaster;
import com.serviceplus.metadata.entity.WorkflowAssignment;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.enums.TaskType;
import com.serviceplus.metadata.esign.dto.EsignConfigurationKafkaDTO;
import com.serviceplus.metadata.esign.dto.ServiceEsignConfigurationDTO;
import com.serviceplus.metadata.esign.entity.ServiceEsignConfiguration;
import com.serviceplus.metadata.esign.kafka.service.EsignKafkaProducer;
import com.serviceplus.metadata.esign.repository.EsignTemplateRepository;
import com.serviceplus.metadata.esign.repository.ServiceEsignConfigurationRepository;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.FileMgmtFeignClient;
import com.serviceplus.metadata.feignClient.FormDesignerFeignClient;
import com.serviceplus.metadata.feignClient.LgdFeignClient;
import com.serviceplus.metadata.feignClient.TenantConfigurationFeignClient;
import com.serviceplus.metadata.formrecommendation.service.RecommendedFormKafkaProducer;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.FilterJSONDTO;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.FilterKafkaMessage;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.FilterKafkaMessage.FilterKafkaDTO;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.InboxSentBoxFilterDTO.FilterAttributeDTO;
import com.serviceplus.metadata.inboxSentboxFilterConfig.entity.InboxSentBoxFilter;
import com.serviceplus.metadata.inboxSentboxFilterConfig.enums.Status;
import com.serviceplus.metadata.inboxSentboxFilterConfig.repository.InboxSentBoxFilterRepository;
import com.serviceplus.metadata.kafka.KafkaProducer;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO.TriggerPoint;
import com.serviceplus.metadata.mvel.dto.MvelFunctionEvent;
import com.serviceplus.metadata.mvel.entity.MvelFunction;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionRepository;
import com.serviceplus.metadata.mvel.service.MvelKafkaProducer;
import com.serviceplus.metadata.notification.dto.NotificationConfigEvent;
import com.serviceplus.metadata.notification.dto.NotificationConfigEvent.NotificationProviderConfig;
import com.serviceplus.metadata.notification.dto.NotificationConfigEvent.NotificationTempleConfig;
import com.serviceplus.metadata.notification.dto.NotificationDTO.SandesConfig;
import com.serviceplus.metadata.notification.dto.NotificationDTO.SmsConfig;
import com.serviceplus.metadata.notification.dto.NotificationDTO.WhatsappConfig;
import com.serviceplus.metadata.notification.dto.SpecificAttributesDTO;
import com.serviceplus.metadata.notification.dto.SpecificAttributesReqDTO;
import com.serviceplus.metadata.notification.entity.NotificationConfiguration;
import com.serviceplus.metadata.notification.enums.NotificationType;
import com.serviceplus.metadata.notification.repository.INotificationRepository;
import com.serviceplus.metadata.notification.repository.NotificationProviderConfigRepository;
import com.serviceplus.metadata.notification.service.NotificationKafkaProducer;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.PatternConfig;
import com.serviceplus.metadata.pattern.entity.PatternConfiguration;
import com.serviceplus.metadata.pattern.entity.ServicePatternConfiguration;
import com.serviceplus.metadata.pattern.repository.PatternConfigurationRepository;
import com.serviceplus.metadata.pattern.repository.ServicePatternConfigurationRepository;
import com.serviceplus.metadata.pattern.service.PatternGenerationConfigService;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceChargeRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMasterDataRepository;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;
import com.serviceplus.metadata.repository.IServiceOutputFormatRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import com.serviceplus.metadata.repository.IServiceTrackingConfigRepository;
import com.serviceplus.metadata.repository.ISpeSequenceLastNoRepository;
import com.serviceplus.metadata.repository.ISystemVariableRepository;
import com.serviceplus.metadata.repository.IWorkFlowAssignmentRepository;
import com.serviceplus.metadata.repository.ServiceDeliveryUnitsRepository;
import com.serviceplus.metadata.repository.ServiceDesignatedOfficerAssignmentRepository;
import com.serviceplus.metadata.repository.ServicePaymentGatewayConfigRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.validation.Valid;

@Service
public class ServiceMetadataService {

    private static final Logger metadataCreationLogger = LogManager.getLogger("metadataCreationLogger");

    @Value("${kafka.topic.filter.configuration}")
    private String filterConfigurationTopic;
    
    @Value("${kafka.topic.notification-configuration}")
    private String notificationConfigurationTopic;
    
    @Value("${kafka.topic.task-property}")
    private String taskPropertyTopic;

    @Autowired
    private com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService;

    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final IServiceLogRepository serviceLogRepository;
    private final ISpeSequenceLastNoRepository speSequenceLastNoRepo;
    
    private final IServiceTemplateRepository serviceTemplateRepository;
    private final IServiceOutputFormatRepository serviceOutputFormatRepository;
    private final IProcessFlowRepository processFlowRepository;
    private final LgdFeignClient lgdFeignClient;
    private final FormDesignerFeignClient formFeignClient;
    private final IServiceChargeRepository serviceChargeRepository;
    private final IServiceMasterDataRepository masterDataRepository;
    private final IServiceMetadataRepository metadataRepository;
    private final IMvelFunctionRepository mvelFunctionRepository;
    private final MvelKafkaProducer mvelKafkaProducer;
    private final ISystemVariableRepository systemVariableRepository;
    private final FileMgmtFeignClient fileMgmtFeignClient;
    private final ObjectMapper mapper;
    private final IWorkFlowAssignmentRepository workFlowAssignmentRepository;
    private final RecommendedFormKafkaProducer formKafkaProducer;
    private final CorrelationSetKafkaProducer correlationSetKafkaProducer;
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final IServiceDeliveryUnitDefinitionRepository serviceDeliveryUnitRepo;
    private final ServiceDesignatedOfficerAssignmentRepository serviceDesignatedOfficerAssignmentRepository;
    private final UserService userService;
    private final ServiceDeliveryUnitsRepository serviceDeliveryUnitsRepository;
    private final INotificationRepository notificationRepository;
    private final NotificationProviderConfigRepository notificationProviderConfigRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaProducer kafkaProducer;
    private final InboxSentBoxFilterRepository inboxSentBoxFilterRepository;
    private final AuaApiFieldMappingRepository mappingRepository;
    private final TenantConfigurationFeignClient tenantConfigurationFeignClient;
    private final AuaApiFieldMappingRepository auaApiFieldMappingRepository;
    private final ICorrelationConfigurationRepository correlationConfigurationRepository;
    private final IExternalDataCombinationRepository combinationRepository;
    private final ServiceCombinationKafkaProducer serviceCombinationKafkaProducer;
    private final ServicePaymentGatewayConfigRepository servicePaymentGatewayConfigRepository;
    @Autowired(required = false)
    private SduAdoptionService sduAdoptionService;
    private final IServiceTrackingConfigRepository trackingConfigRepository;
    private final ServiceEsignConfigurationRepository serviceEsignRepository;
    private final EsignKafkaProducer esignKafkaProducer;
    @Autowired(required = false)
    private BsaServiceMappingService bsaServiceMappingService;
    
    private final ServicePatternConfigurationRepository configurationRepository;
    private final PatternConfigurationRepository patternConfigurationRepository;
    private final PatternGenerationConfigService generationConfigService;

    @Autowired(required = false)
    private BsaKafkaProducer bsaKafkaProducer;

    @Autowired(required = false)
    private EsignTemplateRepository esignTemplateRepository;

    public ServiceMetadataService(IServiceDefinitionRepository serviceDefinitionRepository, IServiceLogRepository serviceLogRepository, ISpeSequenceLastNoRepository speSequenceLastNoRepo, IServiceTemplateRepository serviceTemplateRepository, IServiceOutputFormatRepository serviceOutputFormatRepository, IProcessFlowRepository processFlowRepository, LgdFeignClient lgdFeignClient, FormDesignerFeignClient formFeignClient, IServiceChargeRepository serviceChargeRepository, IServiceMasterDataRepository masterDataRepository, IServiceMetadataRepository metadataRepository, IMvelFunctionRepository mvelFunctionRepository, MvelKafkaProducer mvelKafkaProducer, ISystemVariableRepository systemVariableRepository, FileMgmtFeignClient fileMgmtFeignClient, ObjectMapper mapper, IWorkFlowAssignmentRepository workFlowAssignmentRepository, RecommendedFormKafkaProducer formKafkaProducer, CorrelationSetKafkaProducer correlationSetKafkaProducer, NotificationKafkaProducer notificationKafkaProducer, IServiceDeliveryUnitDefinitionRepository serviceDeliveryUnitRepo, ServiceDesignatedOfficerAssignmentRepository serviceDesignatedOfficerAssignmentRepository, UserService userService, ServiceDeliveryUnitsRepository serviceDeliveryUnitsRepository, INotificationRepository notificationRepository, NotificationProviderConfigRepository notificationProviderConfigRepository, RedisTemplate<String, String> redisTemplate, KafkaProducer kafkaProducer, InboxSentBoxFilterRepository inboxSentBoxFilterRepository, AuaApiFieldMappingRepository mappingRepository, TenantConfigurationFeignClient tenantConfigurationFeignClient, AuaApiFieldMappingRepository auaApiFieldMappingRepository, ICorrelationConfigurationRepository correlationConfigurationRepository, IExternalDataCombinationRepository combinationRepository, ServiceCombinationKafkaProducer serviceCombinationKafkaProducer, ServicePaymentGatewayConfigRepository servicePaymentGatewayConfigRepository, ServiceEsignConfigurationRepository serviceEsignRepository, EsignKafkaProducer esignKafkaProducer,IServiceTrackingConfigRepository trackingConfigRepository, ServicePatternConfigurationRepository configurationRepository, PatternConfigurationRepository patternConfigurationRepository, PatternGenerationConfigService generationConfigService) {


        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.serviceLogRepository = serviceLogRepository;
        this.speSequenceLastNoRepo = speSequenceLastNoRepo;
        this.serviceTemplateRepository = serviceTemplateRepository;
        this.serviceOutputFormatRepository = serviceOutputFormatRepository;
        this.processFlowRepository = processFlowRepository;
        this.lgdFeignClient = lgdFeignClient;
        this.formFeignClient = formFeignClient;
        this.serviceChargeRepository = serviceChargeRepository;
        this.masterDataRepository = masterDataRepository;
        this.metadataRepository = metadataRepository;
        this.mvelFunctionRepository = mvelFunctionRepository;
        this.mvelKafkaProducer = mvelKafkaProducer;
        this.systemVariableRepository = systemVariableRepository;
        this.fileMgmtFeignClient = fileMgmtFeignClient;
        this.mapper = mapper;
        this.workFlowAssignmentRepository = workFlowAssignmentRepository;
        this.formKafkaProducer = formKafkaProducer;
        this.correlationSetKafkaProducer = correlationSetKafkaProducer;
        this.notificationKafkaProducer = notificationKafkaProducer;
        this.serviceDeliveryUnitRepo = serviceDeliveryUnitRepo;
        this.serviceDesignatedOfficerAssignmentRepository = serviceDesignatedOfficerAssignmentRepository;
        this.userService = userService;
        this.serviceDeliveryUnitsRepository = serviceDeliveryUnitsRepository;
        this.notificationRepository = notificationRepository;
        this.notificationProviderConfigRepository = notificationProviderConfigRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaProducer = kafkaProducer;
        this.inboxSentBoxFilterRepository = inboxSentBoxFilterRepository;
        this.mappingRepository = mappingRepository;
        this.tenantConfigurationFeignClient = tenantConfigurationFeignClient;
        this.auaApiFieldMappingRepository = auaApiFieldMappingRepository;
        this.correlationConfigurationRepository = correlationConfigurationRepository;
        this.combinationRepository = combinationRepository;
        this.serviceCombinationKafkaProducer = serviceCombinationKafkaProducer;
        this.servicePaymentGatewayConfigRepository = servicePaymentGatewayConfigRepository;
        this.trackingConfigRepository = trackingConfigRepository;
        this.serviceEsignRepository = serviceEsignRepository;
        this.esignKafkaProducer = esignKafkaProducer;
        this.configurationRepository = configurationRepository;
        this.patternConfigurationRepository = patternConfigurationRepository;
        this.generationConfigService = generationConfigService;
        

    }

    @Value("${file.upload-dir}")
    private String uploadDir;

	public ResponseEntity<?> getAllAllowActionOfTask(Integer serviceId, String taskId,
			UserSessionDTO userSessionDetails) {
		try {
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId,
					userSessionDetails.getUserID());
			if (serviceProcessFlow == null) {
				throw new SPRuntimeError("Service not found.", HttpStatus.BAD_REQUEST);
			}
			ProcessFlowDTO processFlowJson = serviceProcessFlow.getProcessFlowJson();
			if (processFlowJson == null) {
				throw new SPRuntimeError("Service Process Flow not found.", HttpStatus.BAD_REQUEST);
			}
			
			List<Map<String, String>> response = new ArrayList<>();
			for (NodeDTO node : processFlowJson.getNodes()) {
				if (taskId.equals(node.getId())) {
					DataDTO data = node.getData() != null ? node.getData() : new DataDTO();
					Map<String, Action> workflow = data.getWorkflow();
					if (workflow != null) {
						for (Map.Entry<String, Action> entry : workflow.entrySet()) {
							Action action = entry.getValue();
							if (Boolean.TRUE.equals(action.getEnabled())) {
								Map<String, String> actionMap = new HashMap<>();
								actionMap.put("label", action.getActionFormLabel());
								actionMap.put("value", entry.getKey());
								response.add(actionMap);
							}
						}
					}
					break;
				}
			}
			return ResponseEntity.ok(response);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				metadataCreationLogger.error("Error : Get all allow action of task :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
    
    public ResponseEntity<?> getAllTaskOfAttachedForms(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
                ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID());
                if(serviceProcessFlow  == null) {
                        throw new SPRuntimeError("Service not found.", HttpStatus.BAD_REQUEST);
                }
                ProcessFlowDTO processFlowJson = serviceProcessFlow.getProcessFlowJson();
                if(processFlowJson  == null) {
                        throw new SPRuntimeError("Service Process Flow not found.", HttpStatus.BAD_REQUEST);
                }
                List<Object> resData = new ArrayList<Object>();
                
                List<NodeDTO> nodes = processFlowJson.getNodes();
                for (NodeDTO node : nodes) {
                        DataDTO data = node.getData() != null ? node.getData() : new DataDTO();
                        FormDetail formDetail = data.getFormDetail();
                        if (formDetail != null) {
                                Map<String, Object> task = new HashMap<String, Object>();
                                task.put("taskId", node.getId());
                                task.put("taskName", data.getName());
                                task.put("taskType", node.getType());
                                task.put("formId", formDetail.getFormId());
                                task.put("formName", formDetail.getTemplateName());
                                task.put("holderId", formDetail.getHolderId());
                                resData.add(task);
                        }
                }
                return ResponseEntity.ok(resData);
        } catch (Exception ex) {
                ex.printStackTrace();
                if (ex instanceof SPRuntimeError) {
                        throw ex;
                } else {
                        metadataCreationLogger.error("Error : Get all get process flow tasks of attached forms :", ex);
                        throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }
}

public ResponseEntity<?> getAllDouments(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
                ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId,
                                userSessionDetails.getUserID());
                if (serviceProcessFlow == null) {
                        throw new SPRuntimeError("Service not found.", HttpStatus.BAD_REQUEST);
                }
                ProcessFlowDTO processFlowJson = serviceProcessFlow.getProcessFlowJson();
                if (processFlowJson == null) {
                        throw new SPRuntimeError("Service Process Flow not found.", HttpStatus.BAD_REQUEST);
                }
                List<NodeDTO> nodes = processFlowJson.getNodes();
                List<TaskDocs> resData = new ArrayList<TaskDocs>();
                for (NodeDTO node : nodes) {
                        DataDTO data = node.getData();
                        if (data == null)
                                continue;
                        List<DocMappingDTO> documentMapping = data.getDocumentMapping();
                        TaskDocs tasksDoc = new TaskDocs();
                        tasksDoc.setTaskId(node.getId());
                        tasksDoc.setTaskName(data.getName());
                        List<TaskDocs.Document> documents = new ArrayList<TaskDocs.Document>();
                        if(documentMapping != null) {
                        	for (DocMappingDTO docMappingDTO : documentMapping) {
                        		TaskDocs.Document document = new TaskDocs.Document();
                        		document.setReferenceId(docMappingDTO.getReferenceId());
                        		document.setDocumentName(docMappingDTO.getDocumentName());
                        		documents.add(document);
                        	}
                        }
                        tasksDoc.setDocuments(documents);
                        resData.add(tasksDoc);
                }
                
                return ResponseEntity.ok(resData);
        } catch (Exception ex) {
                ex.printStackTrace();
                if (ex instanceof SPRuntimeError) {
                        throw ex;
                } else {
                        metadataCreationLogger.error("Error : Get all attached documents :", ex);
                        throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }
}

public ResponseEntity<?> getProcessFlowFormsAttr(Integer serviceId, String taskId, String attrType, UserSessionDTO userSessionDetails) {
	try {
		ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId,
				userSessionDetails.getUserID());

		if (serviceProcessFlow == null) {
			throw new SPRuntimeError("Service not found.", HttpStatus.BAD_REQUEST);
		}

		ProcessFlowDTO processFlowJson = serviceProcessFlow.getProcessFlowJson();
		if (processFlowJson == null) {
			throw new SPRuntimeError("Service Process Flow not found.", HttpStatus.BAD_REQUEST);
		}

		List<NodeDTO> nodes = processFlowJson.getNodes();
		Set<String> formIds = new HashSet<>();
		FormDetail formDetail = null;

		for (NodeDTO node : nodes) {
			if (taskId.equals(node.getId())) {
				DataDTO data = node.getData() != null ? node.getData() : new DataDTO();
				formDetail = data.getFormDetail();
				if (formDetail != null) {
					formIds.add(formDetail.getFormId());
				}
				break;
			}
		}

		if (formDetail == null) {
			throw new SPRuntimeError("Form not found.", HttpStatus.BAD_REQUEST);
		}

		SpecificAttributesReqDTO attributesReqDTO = new SpecificAttributesReqDTO();
		Map<String, SpecificAttributesDTO> specificAttributesDTOs = new HashMap<>();

		ServiceParameterDetailsDTO responseDTO = new ServiceParameterDetailsDTO();
		List<ServiceParameterDetailsDTO.TemplateDetails> templateList = new ArrayList<>();

		if (!formIds.isEmpty()) {

			attributesReqDTO.setAttrType(attrType);
			attributesReqDTO.setFormIds(formIds);

			ResponseEntity<?> responseEntity = formFeignClient.getSpecificAttributes(attributesReqDTO);

			if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {

				Map<String, SpecificAttributesDTO> responseMap = mapper.convertValue(responseEntity.getBody(),
						new TypeReference<Map<String, SpecificAttributesDTO>>() {
						});

				for (Map.Entry<String, SpecificAttributesDTO> entry : responseMap.entrySet()) {

					String formId = entry.getKey();
					SpecificAttributesDTO dto = entry.getValue();

					if (specificAttributesDTOs.containsKey(formId)) {
						specificAttributesDTOs.get(formId).getAttributes().addAll(dto.getAttributes());
					} else {
						specificAttributesDTOs.put(formId, dto);
					}
				}
			}
			

			String formId = formDetail.getFormId();
			String holderId = formDetail.getHolderId();

			SpecificAttributesDTO specificAttributesDTO = specificAttributesDTOs.get(formId);

			ServiceParameterDetailsDTO.TemplateDetails templateDTO = new ServiceParameterDetailsDTO.TemplateDetails();
			List<ServiceParameterDetailsDTO.Attribute> attributeDTOList = new ArrayList<>();

			if (specificAttributesDTO != null && specificAttributesDTO.getAttributes() != null) {

				for (SpecificAttributesDTO.Attribute attr : specificAttributesDTO.getAttributes()) {

					ServiceParameterDetailsDTO.Attribute attributeDTO = new ServiceParameterDetailsDTO.Attribute();

					attributeDTO.setAttributeId(createAttributeId(taskId, holderId, attr.getAttributeId()));
					attributeDTO.setAttributeLabel(attr.getAttributeLabel());
					attributeDTO.setTypeId(attr.getTypeId());
					attributeDTO.setType(ApplicationConstants.ATTRIBUTE);

					attributeDTOList.add(attributeDTO);
				}
			}

			templateDTO.setTemplateName(formDetail.getTemplateName());
			templateDTO.setTaskId(taskId);
			templateDTO.setAttributeList(attributeDTOList);

			templateList.add(templateDTO);

			responseDTO.setServiceId(serviceId);
			responseDTO.setTemplateDetails(templateList);
		}

		return ResponseEntity.ok(responseDTO);

	} catch (Exception ex) {
		if (ex instanceof SPRuntimeError) {
			throw ex;
		} else {
			ex.printStackTrace();
			metadataCreationLogger.error("Error : Get all process flow forms with attributes :", ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

    public ResponseEntity<?> getAllVersionOfService(Integer serviceId, UserSessionDTO userSessionDetails) {
		try {
			List<Integer> serviceVersions = new ArrayList<Integer>();
			List<ServiceDefinition> serviceDefinitions = serviceDefinitionRepository.findByBaseServiceId(serviceId/10000).orElseThrow(() -> new SPRuntimeError("Service not found", HttpStatus.BAD_REQUEST));
			for (ServiceDefinition serviceDefinition : serviceDefinitions) {
				Integer versionNo = serviceDefinition.getVersionNo();
				serviceVersions.add(versionNo);
			}
			return ResponseEntity.ok(Map.of("versions",serviceVersions));
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				metadataCreationLogger.error("Error : Get all version of service :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
    
	public JSONObject saveServiceDefinition(ServiceDefinitionDTO dto, UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		try {
			responseJson.put("status", true);
			Integer serviceId = null;
			String logoPath=null;
			ServiceDefinition serviceDefinition = new ServiceDefinition();
			ServiceLog serviceLog = new ServiceLog();
			ServiceDefinitionDTO oldDefDto = null;
			if (dto.getServiceId() != null && !dto.getServiceId().equals(0)) {

				serviceId = dto.getServiceId();
				serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId,
						userSessionDetails.getUserID().longValue());
				serviceLog = serviceLogRepository.findByServiceId(serviceId);

				if (Objects.isNull(serviceDefinition)) {
					responseJson.put("errorCode", "400");
					responseJson.put("errorMessage", "Invalid Service");
					responseJson.put("status", false);
					return responseJson;
				}

				
				oldDefDto = serviceDefinition.getDefinitionJson();

				if (dto.getName() != null && !dto.getName().isBlank() && serviceDefinitionRepository
						.existsByServiceNameIgnoreCaseAndServiceIdNot(dto.getName(), serviceId)) {
					responseJson.put("errorCode", "400");
					responseJson.put("errorMessage", "Service name already exists");
					responseJson.put("status", false);
					return responseJson;
				}

				if (dto.getAbbreviation() != null && !dto.getAbbreviation().isBlank()
						&& serviceDefinitionRepository.existsByServiceAbbreviationAndServiceIdNot(dto.getAbbreviation(), serviceId)) {
					responseJson.put("errorCode", "400");
					responseJson.put("errorMessage", "Service abbreviation already exists");
					responseJson.put("status", false);
					return responseJson;
				}

				if (serviceLog.getcDate() != null) {
					serviceLog.setuDate(new Date());
				} else {
					serviceLog.setcDate(new Date());
				}
				serviceLog.setTablist(serviceLog.getTablist());

			} else {

				if (dto.getName() != null && !dto.getName().isBlank()
						&& serviceDefinitionRepository.existsByServiceNameIgnoreCase(dto.getName())) {
					responseJson.put("errorCode", "400");
					responseJson.put("errorMessage", "Service name already exists");
					responseJson.put("status", false);
					return responseJson;
				}
				if (dto.getAbbreviation() != null && !dto.getAbbreviation().isBlank()
						&& serviceDefinitionRepository.existsByServiceAbbreviation(dto.getAbbreviation())) {
					responseJson.put("errorCode", "400");
					responseJson.put("errorMessage", "Service abbreviation already exists");
					responseJson.put("status", false);
					return responseJson;
				}

				SpeSequenceLastNo speSequenceLastNo = speSequenceLastNoRepo.getSequenceLastNo(1);

				serviceId = Integer.valueOf(String.valueOf(speSequenceLastNo.getFnc_generate_seq()).concat("0001"));

				serviceLog.setTablist("1");
			}

			serviceDefinition.setServiceId(serviceId);

			serviceDefinition.setServiceName(dto.getName());
			serviceDefinition.setServiceAbbreviation(dto.getAbbreviation());

			if (dto.getLogo() != null) {

				logoPath=saveLogoFile(dto.getLogo());
			}

			serviceDefinition.setDefinitionJson(dto);
			serviceDefinition.setLogoPath(logoPath);
			serviceDefinition.setcDate(new Date());

			serviceDefinition.setBaseServiceId(serviceId / 10000);

			serviceDefinition.setVersionNo(serviceId % 10000);

			Integer minorVer = (serviceLog.getMinorVersionNo() != null) ? serviceLog.getMinorVersionNo() : 0;
			serviceDefinition.setMinorVersionNo(minorVer);

			serviceDefinition.setUserId(userSessionDetails.getUserID().longValue());

			serviceDefinition.setStateId(userSessionDetails.getClcId());

			serviceDefinition.setDepartmentId(userSessionDetails.getEntityId().longValue());

			serviceLog.setBaseServiceId(serviceId / 10000);

			serviceLog.setServiceId(serviceId);

			serviceLog.setServiceStatus(0);

			serviceLog.setLaunchFlag('N');

			serviceLog.setVersionNo(serviceId % 10000);

			serviceLog.setMinorVersionNo(minorVer);

			serviceLog.setTenantId(userSessionDetails.getTenantId());

			responseJson = saveServiceEntity(responseJson, serviceDefinition, serviceLog);

			boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
			if (isMinorVersion && Boolean.TRUE.equals(responseJson.optBoolean("status", true))) {
				serviceVersionTokenService.recordDefinitionChangeLog(
						serviceId,
						oldDefDto,
						dto,
						userSessionDetails
				);
			}

			return responseJson;

		} catch (Exception ex) {

			ex.printStackTrace();

			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			responseJson.put("status", false);

			return responseJson;
		}
	}
    @Transactional
    private JSONObject saveServiceEntity(JSONObject responseJson, ServiceDefinition serviceDefinition, ServiceLog serviceLog) {
        try {
            ServiceDefinition savedEntity = serviceDefinitionRepository.save(serviceDefinition);
            ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
            responseJson.put("serviceId", savedEntity.getServiceId());
            responseJson.put("message","Service definition saved successfully");
            responseJson.put("tabList",savedLogEntity.getTablist());
            return responseJson;
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            responseJson.put("status", false);
            return responseJson;
        }
    }
    
    private String saveLogoFile(LogoDTO logo) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String sanitizedName = sanitizeFileName(logo.getName());
            String uniqueFileName = new Date().getTime() + "_" + sanitizedName;
            Path filePath = uploadPath.resolve(uniqueFileName);

            String base64Data = logo.getBase64().split(",")[1];
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            Files.write(filePath, decodedBytes);

            return uploadPath+File.separator + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save logo file: " + e.getMessage(), e);
        }
    }

//  private void saveLogoFile(String uploadId, UserSessionDTO userSessionDetails) {
//  try {
//      List<String> uploadIds =new ArrayList<>();
//      uploadIds.add(uploadId);
//      ResponseEntity<CommitResponse> commitResponse = fileMgmtFeignClient.commitFiles(entityToString(userSessionDetails), new CommitRequest(uploadIds));
//      CommitResponse body = commitResponse.getBody();
//  } catch (Exception e) {
//      throw new RuntimeException("Failed to save logo file: " + e.getMessage(), e);
//  }
//}

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._\\-\\(\\)]", "_");
    }

    public ResponseEntity<?> getServiceDefinitionDetails(Integer serviceId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        List<String> uploadIds=new ArrayList<>();
        try {
            ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
            ServiceLog servicelog=serviceLogRepository.findByServiceId(serviceId);
            if (Objects.isNull(serviceDefinition)) {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Service not found");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }

            ServiceDefinitionDTO dto = serviceDefinition.getDefinitionJson();
            //String checksum=CommonUtil.generateChecksums(serviceId.toString(), 2);
            dto.setServiceId(serviceDefinition.getServiceId());
            dto.setTablist(servicelog.getTablist());
            if (serviceVersionTokenService.isMinorModification(serviceId)) {
                ServiceDefinitionDTO baseline = serviceVersionTokenService.getBaselineServiceDefinition(serviceId);
                if (baseline != null) {
                    dto.setHistoryDefinition(baseline);
                    dto.setOldDefinition(baseline);
                }
            }

            List<String> auaConfig = auaApiFieldMappingRepository.findConfigurationIdsByServiceIdOrderByLatest(serviceId);
            dto.setAuaConfig(auaConfig);

//          String uploadId=dto.getLogo();
//          dto.setLogo("");
//          if(uploadId!=null && !uploadId.isEmpty()) {
//              uploadIds.add(uploadId);
//              ResponseEntity<FileViewResponse> fileViewResp = fileMgmtFeignClient.filePreview(entityToString(userSessionDetails), new CommitRequest(uploadIds));
//              FileViewResponse body = fileViewResp.getBody();
//              if (isNull(body) || isNull(body.getResults())) {
//                  throw new SPRuntimeError("Failed to fetch file",HttpStatus.FAILED_DEPENDENCY);
//              }
//              Map<String, Data> results = body.getResults();
//              if (results.containsKey(uploadId)) {
//                  dto.setLogo(mapper.writeValueAsString(results.get(uploadId)));
//              }
//          }
          ServiceDefinitionDTO.LogoDTO logoDTO = new LogoDTO();
		    if (serviceDefinition.getLogoPath() != null) {
		        try {
		            Path logoPath = Paths.get(serviceDefinition.getLogoPath());
		            if (Files.exists(logoPath)) {
		            	
		                logoDTO.setName(logoPath.getFileName().toString());
		                
		                String extension = logoPath.getFileName().toString().toLowerCase();
		                String mimeType;
		                if (extension.endsWith(".png")) {
		                    mimeType = "image/png";
		                } else if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
		                    mimeType = "image/jpeg";
		                } else {
		                    throw new IOException("Unsupported logo file type: " + extension);
		                }
		                logoDTO.setType(mimeType);
		                long fileSize = Files.size(logoPath);
		                logoDTO.setSize(fileSize);
		                
		                byte[] fileBytes = Files.readAllBytes(logoPath);
		                String base64String = Base64.getEncoder().encodeToString(fileBytes);
		                logoDTO.setBase64("data:" + mimeType + ";base64," + base64String);
	
		            } 
		        } catch (IOException e) {
		           e.printStackTrace();
		        }
		    }
		    dto.setLogo(logoDTO);
            return ResponseEntity.ok().body(dto);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> getServiceList(Integer status, UserSessionDTO userSessionDetails) {

        JSONObject responseJson = new JSONObject();
        List<ServiceListDetailsDTO> serviceListDetailsDTOs = new ArrayList<>();
        try {
            List<ServiceDefinition> serviceList =serviceDefinitionRepository.findByUserId(userSessionDetails.getUserID().longValue());
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

    public ResponseEntity<?> getServiceTemplateList(Integer serviceId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        List<ServiceTemplateDTO> serviceTemplateDTOList = new ArrayList<ServiceTemplateDTO>();
        try {
            List<ServiceTemplate> serviceTemplateList = serviceTemplateRepository.findByServiceIdAndUserId(serviceId,userSessionDetails.getUserID().longValue());
            for(ServiceTemplate  serviceTemplate:serviceTemplateList) {
                ServiceTemplateDTO serviceTemplateDTO = new ServiceTemplateDTO();
                serviceTemplateDTO.setServiceId(serviceTemplate.getServiceId());
                serviceTemplateDTO.setTemplateId(serviceTemplate.getId());
                serviceTemplateDTO.setTaskId(serviceTemplate.getTaskId());
                serviceTemplateDTO.setTemplateName(serviceTemplate.getTemplateName());
                serviceTemplateDTOList.add(serviceTemplateDTO);
            }
            return ResponseEntity.ok().body(serviceTemplateDTOList);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public ResponseEntity<?> getTaskList(Integer serviceId, String taskId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        TaskDTO taskDto= new TaskDTO();
        List<TaskDTO.Task> taskList=new ArrayList<>();
        try {
            ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
            if(serviceProcessFlow!=null &&serviceProcessFlow.getProcessFlowJson()!=null) {
                taskDto.setServiceId(serviceId);
                List<com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO> nodes = serviceProcessFlow.getProcessFlowJson().getNodes();
                List<com.serviceplus.metadata.dto.ProcessFlowDTO.EdgeDTO> edges = serviceProcessFlow.getProcessFlowJson().getEdges();
                Map<String, NodeDTO> nodeMap = nodes.stream()
                        .collect(Collectors.toMap(NodeDTO::getId, Function.identity()));

                Map<String, List<String>> outgoingMap = edges.stream()
                        .collect(Collectors.groupingBy(
                                EdgeDTO::getSource,
                                Collectors.mapping(EdgeDTO::getTarget, Collectors.toList())
                        ));

                Map<String, List<String>> incomingMap = edges.stream()
                        .collect(Collectors.groupingBy(
                                EdgeDTO::getTarget,
                                Collectors.mapping(EdgeDTO::getSource, Collectors.toList())
                        ));
                List<NodeDTO> filteredNodes = nodes.stream()
                        .filter(node -> taskId == null || taskId.toString().equals(node.getId()))
                        .collect(Collectors.toList());
                for(com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO node:filteredNodes) {
                    TaskDTO.Task task=new Task();
                    task.setTaskId(node.getId());
                    task.setTaskName(node.getData()!=null ? node.getData().getName():"");
                    task.setTaskType(node.getType());
                    Set<String> nextTaskIds = new LinkedHashSet<>();
                    collectNextTasks(node.getId(), outgoingMap, nodeMap, new HashSet<>(), nextTaskIds);

                    List<TaskDTO.Task.TaskReference> nextTasks = nextTaskIds.stream()
                            .map(id -> {
                            	TaskDTO.Task.TaskReference ref = new TaskDTO.Task.TaskReference();
                                ref.setTaskId(id);

                                NodeDTO nextNode = nodeMap.get(id);
                                ref.setTaskName(nextNode != null && nextNode.getData() != null
                                        ? nextNode.getData().getName()
                                        : "");

                                return ref;
                            })
                            .collect(Collectors.toList());

                    task.setNextTasks(nextTasks);
                    Set<String> previousTaskIds = new LinkedHashSet<>();
                    collectPreviousTasks(node.getId(), incomingMap, nodeMap, new HashSet<>(), previousTaskIds);

                    List<TaskDTO.Task.TaskReference> prevTasks = previousTaskIds.stream()
                            .map(id -> {
                            	TaskDTO.Task.TaskReference ref = new TaskDTO.Task.TaskReference();
                                ref.setTaskId(id);

                                NodeDTO prevNode = nodeMap.get(id);
                                ref.setTaskName(prevNode != null && prevNode.getData() != null
                                        ? prevNode.getData().getName()
                                        : "");

                                return ref;
                            })
                            .collect(Collectors.toList());

                    task.setPrevTasks(prevTasks);
                    taskList.add(task);
                }
                taskDto.setTaskList(taskList);
            }
            return ResponseEntity.ok().body(taskDto);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public ResponseEntity<?> saveServiceTemplate(ServiceTemplateDTO serviceTemplateDTO, UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        try {
            String formId=serviceTemplateDTO.getFormId();
            String holderId=serviceTemplateDTO.getHolderId();
            Integer serviceId=serviceTemplateDTO.getServiceId();
            ServiceTemplate serviceTemplate=null;
            if(serviceTemplateDTO.getTemplateId()!=null) {
                serviceTemplate=serviceTemplateRepository.findByIdAndUserId(serviceTemplateDTO.getTemplateId(),userSessionDetails.getUserID().longValue());
                if(Objects.isNull(serviceTemplate)) {
                    responseJson.put("errorCode", "400");
                    responseJson.put("errorMessage", "Invalid Service");
                    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
                }
                serviceTemplate.setuDate(new Date());
                if(serviceTemplate.getInstantiableFlag().equalsIgnoreCase("N") && serviceTemplateDTO.getInstantiableFlag().equalsIgnoreCase("N")) {
                    serviceTemplateDTO.setInstantiableFlag(null);
                }else {
                    serviceTemplate.setInstantiableFlag(serviceTemplateDTO.getInstantiableFlag());
                }
            }else {
                serviceTemplate=new ServiceTemplate();
                serviceTemplate.setcDate(new Date());
                serviceTemplate.setInstantiableFlag(serviceTemplateDTO.getInstantiableFlag());
            }
            ServiceLog serviceLog=serviceLogRepository.findByServiceId(serviceId);
            if(Objects.isNull(serviceLog)) {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Invalid Service");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }
            serviceTemplate.setServiceId(serviceId);
            //serviceTemplate.setTemplateJson(serviceTemplateDTO);
            serviceTemplate.setVersionNo(serviceId%10000);
            serviceTemplate.setMinorVersion(0);
            serviceTemplate.setUserId(userSessionDetails.getUserID().longValue());
            serviceTemplate.setBaseServiceId(serviceId/10000);
            if(serviceTemplateDTO.getInstantiableFlag()!=null) {

                ServiceTemplateMappingDTO templateMappingDto=new ServiceTemplateMappingDTO();
                templateMappingDto.setCreatedBy(userSessionDetails.getUserID().longValue());
                templateMappingDto.setDepartmentId(userSessionDetails.getEntityId());
                templateMappingDto.setStateId(userSessionDetails.getClcId());
                templateMappingDto.setLocationId(userSessionDetails.getLocationId());
                templateMappingDto.setInstantiable(serviceTemplateDTO.getInstantiableFlag());
                templateMappingDto.setFormId(formId);


                ResponseEntity<Map<String, Object>> saveInstantiableForm = formFeignClient.saveInstantiableForm(templateMappingDto);
                if(saveInstantiableForm.getStatusCode().equals(HttpStatus.OK)) {
                    Map<String, Object> responseBody = saveInstantiableForm.getBody();
                    formId=(String) responseBody.get("formId");
                }
            }
            serviceTemplate.setFormId(formId);
            serviceTemplate.setHolderId(holderId);
            serviceTemplate.setTaskId(serviceTemplateDTO.getTaskId());
            responseJson=saveTemplate(responseJson, serviceTemplate, serviceLog);
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);


        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Transactional
    private JSONObject saveTemplate(JSONObject responseJson, ServiceTemplate serviceTemplate, ServiceLog serviceLog) {
        try {
            String tablist=serviceLog.getTablist();
            ServiceTemplate savedEntity = serviceTemplateRepository.save(serviceTemplate);
            if(!tablist.contains("6")) {
                serviceLog.setTablist(serviceLog.getTablist()+",6");
                ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
                tablist=savedLogEntity.getTablist();
            }
            responseJson.put("templateId", savedEntity.getId());
            responseJson.put("message", "Service Tempalte saved successfully");
            responseJson.put("tabList", tablist);
            return responseJson;
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return responseJson;
        }
    }
    public ResponseEntity<?> getServiceTemplate(Integer serviceId, Integer templateId,
                                                UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        ServiceTemplateDTO serviceTemplateDTO = new ServiceTemplateDTO();
        try {
            ServiceTemplate serviceTemplate=serviceTemplateRepository.findByIdAndServiceIdAndUserId(templateId,serviceId,userSessionDetails.getUserID().longValue());
            if(Objects.nonNull(serviceTemplate)) {
                serviceTemplateDTO.setTemplateId(serviceTemplate.getId());
                serviceTemplateDTO.setFormId(serviceTemplate.getFormId());
                serviceTemplateDTO.setTemplateName(serviceTemplate.getTemplateName());
            }
            return ResponseEntity.ok().body(serviceTemplateDTO);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public void deleteTemplate(Integer serviceId, String nodeId, UserSessionDTO userSessionDetails) {
        try {
            ServiceTemplate serviceTemplate=serviceTemplateRepository.findByServiceIdAndTaskIdAndUserId(serviceId,nodeId,userSessionDetails.getUserID().longValue());
            if(Objects.nonNull(serviceTemplate)) {
                serviceTemplateRepository.delete(serviceTemplate);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getServiceFormAttributeList(Integer serviceId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        List<ServiceFormAttributeDetailsDTO> serviceFormAttrDetailDTOList = new ArrayList<ServiceFormAttributeDetailsDTO>();
        try {

            List<ServiceTemplate> serviceTemplateList = serviceTemplateRepository.findByServiceIdAndUserId(serviceId,userSessionDetails.getUserID().longValue());
            for (ServiceTemplate serviceTemplate : serviceTemplateList) {
                ServiceFormAttributeDetailsDTO serviceFormAttrDetailDTO = new ServiceFormAttributeDetailsDTO();
                serviceFormAttrDetailDTO.setTemplateName(serviceTemplate.getTemplateName());
                serviceFormAttrDetailDTO.setTemplateId(serviceTemplate.getId());
                ResponseEntity<?> formDtoResponse = formFeignClient.getAllAttributesByFormId(serviceTemplate.getFormId());
                if (formDtoResponse.getStatusCode().equals(HttpStatus.OK)) {
                    Map<Object,Object> responseObj = (Map<Object, Object>) formDtoResponse.getBody();
                    JSONObject json=new JSONObject(responseObj);
                    FormDTO formDto= new Gson().fromJson(json.toString(), FormDTO.class);
                    serviceFormAttrDetailDTO.setTemplateName(serviceTemplate.getTemplateName());
                    formDto.setFormId(serviceTemplate.getFormId());
                    List<Attribute> attrList = formDto.getAttributes();
                    for(Attribute attr:attrList) {
                        String attrId=createAttributeId(serviceTemplate.getTaskId(),serviceTemplate.getHolderId(),attr.getAttributeId());
                        attr.setAttributeId(attrId);
                    }
                    serviceFormAttrDetailDTO.setAttributeList(attrList);
                }
                serviceFormAttrDetailDTOList.add(serviceFormAttrDetailDTO);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return ResponseEntity.ok().body(serviceFormAttrDetailDTOList);
    }
    public ResponseEntity<?> saveServiceOutputFormat(@Valid ServiceOutputFormatDTO serviceOutputFormatDTO,
                                                     UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        try {
            Integer serviceId=serviceOutputFormatDTO.getServiceId();
            Long outputFormatId=serviceOutputFormatDTO.getOutputFormatId();
            ServiceOutputFormat serviceOutputFormat=null;
            ServiceOutputFormatDTO oldOutputFormatDTO = null;
            if(outputFormatId!=null) {
                serviceOutputFormat=serviceOutputFormatRepository.findByIdAndUserId(outputFormatId,userSessionDetails.getUserID().longValue());
                if(Objects.isNull(serviceOutputFormat)) {
                    responseJson.put("errorCode", "400");
                    responseJson.put("errorMessage", "Invalid Service");
                    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
                }
                oldOutputFormatDTO = serviceOutputFormat.getOutputFormatJson();
                serviceOutputFormat.setUDate(new Date());
            }else {
                serviceOutputFormat=new ServiceOutputFormat();
                serviceOutputFormat.setCDate(new Date());
            }
            ServiceLog serviceLog=serviceLogRepository.findByServiceId(serviceId);
            if(Objects.isNull(serviceLog)) {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Invalid Service");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }
            serviceOutputFormat.setServiceId(serviceId);
            serviceOutputFormat.setOutputFormatJson(serviceOutputFormatDTO);
            serviceOutputFormat.setVersionNo(serviceId%10000);
            Integer minorVer = (serviceLog.getMinorVersionNo() != null) ? serviceLog.getMinorVersionNo() : 0;
            serviceOutputFormat.setMinorVersion(minorVer);
            serviceOutputFormat.setUserId(userSessionDetails.getUserID().longValue());
            serviceOutputFormat.setBaseServiceId(serviceId/10000);


            responseJson=saveOutputFormat(responseJson, serviceOutputFormat, serviceLog);

            boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
            if (isMinorVersion) {
                serviceVersionTokenService.recordOutputFormatChangeLog(
                        serviceId,
                        oldOutputFormatDTO == null ? "CREATE" : "UPDATE",
                        serviceOutputFormatDTO.getTemplateName(),
                        oldOutputFormatDTO,
                        serviceOutputFormatDTO,
                        userSessionDetails
                );
            }

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);


        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JSONObject saveOutputFormat(JSONObject responseJson, ServiceOutputFormat serviceOutputFormat,
                                        ServiceLog serviceLog) {
        try {
            String tablist = serviceLog.getTablist();
            ServiceOutputFormat savedEntity = serviceOutputFormatRepository.save(serviceOutputFormat);
            if (!tablist.contains("7")) {
                serviceLog.setTablist(serviceLog.getTablist() + ",7");
                ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
                tablist = savedLogEntity.getTablist();
            }
            responseJson.put("outputFormatId", savedEntity.getId());
            responseJson.put("message", "Output format saved successfully");
            responseJson.put("tabList", tablist);
            return responseJson;
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return responseJson;
        }
    }
    public ResponseEntity<?> getServiceOutputFormat(Integer serviceId, Long outputFormatId,
                                                    UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        ServiceOutputFormatDTO serviceOutputFormatDTO = null;
        try {
            ServiceOutputFormat serviceOutputFormat=serviceOutputFormatRepository.findByIdAndServiceIdAndUserId(outputFormatId,serviceId,userSessionDetails.getUserID().longValue());
            if(Objects.nonNull(serviceOutputFormat)) {
                serviceOutputFormatDTO=serviceOutputFormat.getOutputFormatJson();
                if (serviceOutputFormatDTO != null && serviceVersionTokenService.isMinorModification(serviceId)) {
                    ServiceOutputFormatDTO baseline = serviceVersionTokenService.getBaselineOutputFormat(serviceId, outputFormatId);
                    serviceOutputFormatDTO.setHistoryOutputFormat(baseline);
                    serviceOutputFormatDTO.setOldOutputFormat(baseline);
                }
            }
            return ResponseEntity.ok().body(serviceOutputFormatDTO);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public ResponseEntity<?> deleteOutputFormat(Integer serviceId, Long outputFormatId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        try {
            if (serviceVersionTokenService.isMinorModification(serviceId) && serviceVersionTokenService.isOutputFormatInHistory(serviceId, outputFormatId)) {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Deleting existing document templates is not allowed in minor version modification");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }

            ServiceOutputFormat serviceOutputFormat=serviceOutputFormatRepository.findByIdAndServiceIdAndUserId(outputFormatId,serviceId,userSessionDetails.getUserID().longValue());
            if(Objects.nonNull(serviceOutputFormat)) {
                String tName = serviceOutputFormat.getOutputFormatJson() != null ? serviceOutputFormat.getOutputFormatJson().getTemplateName() : "Output Format";
                ServiceOutputFormatDTO delDto = serviceOutputFormat.getOutputFormatJson();
                serviceOutputFormatRepository.delete(serviceOutputFormat);
                serviceVersionTokenService.recordOutputFormatChangeLog(
                        serviceId,
                        "DELETE",
                        tName,
                        delDto,
                        null,
                        userSessionDetails
                );
                responseJson.put("message", "Output Format deleted successfully.");
            }else {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Output Format does not exist.");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(responseJson.toString());
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public ResponseEntity<?> getServiceOutputFormatList(Integer serviceId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        List<ServiceOutputFormatDTO> serviceOutputFormatDTOList = new ArrayList<ServiceOutputFormatDTO>();
        try {
            List<ServiceOutputFormat> serviceOutputFormatList = serviceOutputFormatRepository.findByServiceIdAndUserId(serviceId,userSessionDetails.getUserID().longValue());
            for(ServiceOutputFormat  serviceOutputFormat:serviceOutputFormatList) {
                ServiceOutputFormatDTO serviceOutputFormatDTO = new ServiceOutputFormatDTO();
                serviceOutputFormatDTO.setServiceId(serviceOutputFormat.getServiceId());
                serviceOutputFormatDTO.setOutputFormatId(serviceOutputFormat.getId());
                serviceOutputFormatDTO.setDocumentType(serviceOutputFormat.getOutputFormatJson().getDocumentType());
                serviceOutputFormatDTO.setTemplateName(serviceOutputFormat.getOutputFormatJson().getTemplateName());
                serviceOutputFormatDTOList.add(serviceOutputFormatDTO);
            }
            return ResponseEntity.ok().body(serviceOutputFormatDTOList);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public ResponseEntity<?> getXSDAttributeList(String xsd) {
        JSONObject responseJson = new JSONObject();
        List<XSDDetail> xsdDetailList = new ArrayList<XSDDetail>();
        try {
            xsd = xsd.trim();
            String decodedXsd = URLDecoder.decode(xsd, StandardCharsets.UTF_8.toString());
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                try {
                    Document doc = docBuilder.parse(new InputSource(new StringReader(decodedXsd)));
                    NodeList elementList = doc.getElementsByTagName("xs:element");
                    NodeList attrList = doc.getElementsByTagName("xs:attribute");
                    for (int i = 0; i < elementList.getLength(); i++) {
                        boolean hasChild = false;
                        Element first = (Element) elementList.item(i);
                        if (first.hasAttributes()) {
                            XSDDetail xsdDetail = new XSDDetail();

                            String parentNodeNm = null;

                            Node pNode = first.getParentNode();
                            String pNodeNm = pNode.getNodeName();
                            if (pNodeNm.contains("sequence")) {
                                pNode = pNode.getParentNode();
                                pNodeNm = pNode.getNodeName();
                                if (pNodeNm.contains("complexType")) {
                                    pNode = pNode.getParentNode();
                                    pNodeNm = pNode.getNodeName();
                                    if (pNodeNm.contains("element")) {
                                        parentNodeNm = ((Element) pNode).getAttribute("name");
                                    }
                                }
                            }
                            parentNodeNm = parentNodeNm == null || parentNodeNm.equalsIgnoreCase("null") ? ""
                                    : parentNodeNm;
                            String elementName = first.getAttribute("name");
                            hasChild = first.hasChildNodes();
                            String nodeType = "E";

                            xsdDetail.setParent(parentNodeNm);
                            xsdDetail.setHasChild(hasChild);
                            xsdDetail.setNode(elementName);
                            if (hasChild) {
                                nodeType = "D";
                                xsdDetail.setAttrId("0");
                            }
                            xsdDetail.setType(nodeType);

                            xsdDetailList.add(xsdDetail);
                        }
                    }
                    for (int i = 0; i < attrList.getLength(); i++) {
                        Element first = (Element) attrList.item(i);
                        if (first.hasAttributes()) {
                            String attrName = first.getAttribute("name");
                            XSDDetail xsdDetail = new XSDDetail();
                            xsdDetail.setNode(attrName);
                            xsdDetailList.add(xsdDetail);
                        }
                    }

                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok().body(xsdDetailList);
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public ResponseEntity<?> getServiceAbbreviation(Integer serviceId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        try {
            ServiceMetadata serviceMetadata = metadataRepository.findById(serviceId).get();
            if (Objects.nonNull(serviceMetadata)) {
                responseJson.put("abbr", serviceMetadata.getMetadataJson().getServiceAbbrevation());
            } else {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Invalid service");
                new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(responseJson.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public ResponseEntity<?> getEligibleServiceList(UserSessionDTO userSessionDetails) {
        List<EligibleServiceDTO> eligibleServiceDTO=new ArrayList<EligibleServiceDTO>();
        try {
            List<Roles> userRoles = userSessionDetails.getRoles();
            Set<String> allowedSubmissionModes = userRoles.stream()
                    .map(role -> ApplicationConstants.ROLE_TO_SUBMISSION_MODE.get(role.getRoleId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (allowedSubmissionModes.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
			List<ServiceLog> serviceLogs = serviceLogRepository.findByServiceStatusInAndTenantId(
					List.of(ServiceStatus.ACTIVATED.getCode(), ServiceStatus.FROZEN.getCode()),
					userSessionDetails.getTenantId());
            if (serviceLogs.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            List<Integer> serviceIds = serviceLogs.stream()
                    .collect(Collectors.toMap(
                            ServiceLog::getBaseServiceId,
                            ServiceLog::getServiceId,
                            Integer::max))
                    .values().stream().toList();

            List<ServiceMetadata> serviceList = metadataRepository.findByTenantIdAndServiceIdIn(
                    userSessionDetails.getTenantId(),
                    serviceIds);

            for (ServiceMetadata serviceMeta : serviceList) {
                ServiceJSONDTO serviceJson = serviceMeta.getMetadataJson();
                if (serviceJson == null || serviceJson.getSubmissionModes() == null) {
                    continue;
                }

                boolean matches = serviceJson.getSubmissionModes().stream()
                        .anyMatch(mode -> mode.getValue() != null && allowedSubmissionModes.contains(mode.getValue().toString()));

                if (!matches) {
                    continue;
                }

                EligibleServiceDTO dto = new EligibleServiceDTO();
                dto.setBaseServiceId(serviceJson.getServiceId() / 10000);
                dto.setServiceId(serviceJson.getServiceId());
                dto.setServiceName(serviceJson.getServiceName());
                dto.setDepartmentName(serviceJson.getDepartmentName());
                dto.setStateName(serviceJson.getStateName());
                dto.setFormId(serviceJson.getApplFormId());
                dto.setTaskId(serviceJson.getApplSubmissionTaskId());
                dto.setTaskType("A");

                List<ActivityMapDTO> activities = serviceJson.getActivityMap().stream()
                        .filter(activity -> activity.getTaskId().equals(serviceJson.getApplSubmissionTaskId()))
                        .collect(Collectors.toList());

                List<OfficeDetailsDTO> locations = serviceJson.getOfficeDetails().stream()
                        .filter(location -> location.getTaskId().equals(serviceJson.getApplSubmissionTaskId()))
                        .collect(Collectors.toList());

                dto.setActivityMap(activities.isEmpty() ? new ActivityMapDTO() : activities.get(0));
                dto.setLocations(locations.isEmpty()
                        ? new ArrayList<>()
                        : locations.get(0).getAllowedOffices());

                eligibleServiceDTO.add(dto);
            }
            return ResponseEntity.ok(eligibleServiceDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching eligible services");
        }
    }
    
    public ResponseEntity<?> saveServiceCharge(ServiceChargeResDTO serviceChargeReqDTO, UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        try {
            Integer serviceId=serviceChargeReqDTO.getServiceId();
            Long chargeId=serviceChargeReqDTO.getChargeId();
            ServiceChargeDetail serviceCharge=null;
            ServiceChargeDTO oldChargeDTO = null;
            if(chargeId!=null) {
                serviceCharge=serviceChargeRepository.findByIdAndUserId(chargeId,userSessionDetails.getUserID().longValue());
                if(Objects.isNull(serviceCharge)) {
                    responseJson.put("errorCode", "400");
                    responseJson.put("errorMessage", "Invalid Service");
                    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
                }
                oldChargeDTO = serviceCharge.getServiceChargeJson();
                serviceCharge.setUDate(new Date());
            }else {
                serviceCharge=new ServiceChargeDetail();
                serviceCharge.setCDate(new Date());
            }

            ServiceChargeDTO serviceChargeDTO = copyToServiceChargeDTO(serviceChargeReqDTO);

            boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
            ServiceChargeDTO baselineCharge = (isMinorVersion && serviceChargeReqDTO.getChargeId() != null)
                    ? serviceVersionTokenService.getBaselineServiceCharge(serviceId, serviceChargeReqDTO.getChargeId())
                    : null;
            if (isMinorVersion && baselineCharge != null) {
                if (!Objects.equals(baselineCharge.getServiceUnitWise(), serviceChargeDTO.getServiceUnitWise())) {
                    responseJson.put("errorCode", "400");
                    responseJson.put("errorMessage", "Changing Service Unit-wise charge definition is not allowed in minor version modification");
                    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
                }
                if (!Objects.equals(baselineCharge.getTemplateName(), serviceChargeDTO.getTemplateName())) {
                    responseJson.put("errorCode", "400");
                    responseJson.put("errorMessage", "Modifying existing charge templates is not allowed in minor version modification");
                    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
                }
            }

            Set<String> taskIds = new HashSet<String>();
            if(serviceChargeReqDTO.getTasks() != null) {
				List<LabelValue> tasks = serviceChargeReqDTO.getTasks();
				for(LabelValue task : tasks) {
					taskIds.add(task.getValue().toString());
				}
			}

            ServiceLog serviceLog=serviceLogRepository.findByServiceId(serviceId);
            if(Objects.isNull(serviceLog)) {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Invalid Service");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }

            List<PaymentModeDTO> paymentModes = serviceChargeDTO.getPaymentModes();

            ServiceMasterData masterData = masterDataRepository.findByName("Payment Mode").orElseThrow(() -> new RuntimeException("Payment Mode  not found"));
            List<PaymentModeDTO> validatePaymentModes = new ArrayList<ServiceChargeDTO.PaymentModeDTO>();

            for(PaymentModeDTO paymentMode : paymentModes) {
            	if(paymentMode!=null) {
            		List<Map<String, Object>> dataList = mapper.readValue(masterData.getData(), new TypeReference<List<Map<String, Object>>>(){});

            		List<Map<String, Object>> filteredDataList = dataList.stream()
            				.filter(item -> {
            					Object value = item.get("value");
            					return value != null && value.toString().equals(paymentMode.getValue());
            				}).collect(Collectors.toList());
            		if(filteredDataList.isEmpty()) {
            			responseJson.put("errorCode", "400");
            			responseJson.put("errorMessage", "Invalid Payment Mode Data :"+paymentMode.getLabel());
            			return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            		}else {
            			paymentMode.setOnlineFlag((Boolean) filteredDataList.get(0).get("online"));
            			paymentMode.setLabel((String) filteredDataList.get(0).get("label"));
            			paymentMode.setValue((String) filteredDataList.get(0).get("value"));
            		}
            		validatePaymentModes.add(paymentMode);
            	}
            }

            boolean isDuplicateTemplateName = serviceChargeRepository.existsByServiceIdAndTemplateName(serviceId,serviceChargeDTO.getTemplateName(),serviceChargeDTO.getChargeId());
            if(isDuplicateTemplateName) {
            	responseJson.put("errorCode", "400");
    			responseJson.put("errorMessage", "Template Name is already present");
    			return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }

            serviceChargeDTO.setPaymentModes(validatePaymentModes);
            serviceCharge.setServiceChargeJson(serviceChargeDTO);
            serviceCharge.setServiceId(serviceId);
            serviceCharge.setServiceChargeJson(serviceChargeDTO);
            serviceCharge.setVersionNo(serviceId%10000);
            Integer minorVer = (serviceLog.getMinorVersionNo() != null) ? serviceLog.getMinorVersionNo() : 0;
            serviceCharge.setMinorVersion(minorVer);
            serviceCharge.setUserId(userSessionDetails.getUserID().longValue());
            serviceCharge.setBaseServiceId(serviceId/10000);


            responseJson=saveChargeDetail(responseJson, serviceCharge, serviceLog);

            Long dbChargetId = (Long)responseJson.get("outputFormatId");

            saveChargeDetailsOnTask(serviceId, dbChargetId, taskIds);

            if (isMinorVersion) {
                serviceVersionTokenService.recordServiceChargeChangeLog(
                        serviceId,
                        oldChargeDTO == null ? "CREATE" : "UPDATE",
                        serviceChargeDTO.getTemplateName(),
                        oldChargeDTO,
                        serviceChargeDTO,
                        userSessionDetails
                );
            }
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);


        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
	private ServiceChargeDTO copyToServiceChargeDTO(ServiceChargeResDTO source) {

		if (source == null) {
			return null;
		}

		ServiceChargeDTO target = new ServiceChargeDTO();

		// Simple fields
		BeanUtils.copyProperties(source, target);

		// Charge Type List
		if (source.getChargeTypeList() != null) {

			List<ServiceChargeDTO.ChargeTypeDTO> chargeTypeList = source.getChargeTypeList().stream().map(item -> {
				ServiceChargeDTO.ChargeTypeDTO targetItem = new ServiceChargeDTO.ChargeTypeDTO();

				targetItem.setName(item.getName());
				targetItem.setAmount(item.getAmount());

				return targetItem;
			}).collect(Collectors.toList());

			target.setChargeTypeList(chargeTypeList);
		}

		// Payment Modes
		if (source.getPaymentModes() != null) {

			List<ServiceChargeDTO.PaymentModeDTO> paymentModes = source.getPaymentModes().stream().map(item -> {
				ServiceChargeDTO.PaymentModeDTO targetItem = new ServiceChargeDTO.PaymentModeDTO();

				targetItem.setValue(item.getValue());
				targetItem.setLabel(item.getLabel());
				targetItem.setOnlineFlag(item.getOnlineFlag());

				return targetItem;
			}).collect(Collectors.toList());

			target.setPaymentModes(paymentModes);
		}

		// Closed User Type
		if (source.getClosedUserType() != null) {

			List<ServiceChargeDTO.ClosedUserTypeDTO> closedUserTypes = source.getClosedUserType().stream().map(item -> {
				ServiceChargeDTO.ClosedUserTypeDTO targetItem = new ServiceChargeDTO.ClosedUserTypeDTO();

				targetItem.setValue(item.getValue());
				targetItem.setLabel(item.getLabel());

				return targetItem;
			}).collect(Collectors.toList());

			target.setClosedUserType(closedUserTypes);
		}

		if (source.getChargeDefinedFor() != null) {
			target.setChargeDefinedFor(source.getChargeDefinedFor());
		}

		return target;
	}
    private void saveChargeDetailsOnTask(Integer serviceId, Long chargeId, Set<String> taskIds) {
    	ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
		if (serviceProcessFlow != null && serviceProcessFlow.getProcessFlowJson() != null
				&& serviceProcessFlow.getProcessFlowJson().getNodes() != null
				&& !serviceProcessFlow.getProcessFlowJson().getNodes().isEmpty()) {
			
			List<NodeDTO> nodes = serviceProcessFlow.getProcessFlowJson().getNodes();
			for (NodeDTO node : nodes) {

				TaskPaymentMappingDTO payment = node.getData().getPayment();
				boolean taskSelected = taskIds.contains(node.getId());

				if (payment == null) {

					if (taskSelected) {
						payment = new TaskPaymentMappingDTO();
						payment.setChargeId(chargeId);
						payment.setEnabled(true);
						payment.setSystemGeneratedAck(false);
						payment.setPaymentAckDoc(null);

						node.getData().setPayment(payment);
					}

				} else {

					Long processFlowChargeId = payment.getChargeId();

					if (Objects.equals(processFlowChargeId, chargeId) && !taskSelected) {
						node.getData().setPayment(null);

					} else if (taskSelected) {
						payment.setChargeId(chargeId);
						node.getData().setPayment(payment);
					}
				}
			}
			processFlowRepository.save(serviceProcessFlow);
		}
	}

	private JSONObject saveChargeDetail(JSONObject responseJson, ServiceChargeDetail serviceCharge,
                                        ServiceLog serviceLog) {
        try {
            String tablist = serviceLog.getTablist();
            ServiceChargeDetail savedEntity = serviceChargeRepository.save(serviceCharge);
            if (!tablist.contains("8")) {
                serviceLog.setTablist(serviceLog.getTablist() + ",8");
                ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
                tablist = savedLogEntity.getTablist();
            }
            responseJson.put("outputFormatId", savedEntity.getId());
            responseJson.put("message", "Payment receipt template saved successfully");
            responseJson.put("tabList", tablist);
            return responseJson;
        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return responseJson;
        }
    }
    public ResponseEntity<?> getServiceChargeDetail(Integer serviceId, Long chargeId, UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        ServiceChargeDTO serviceChargeDTO = null;
        try {
            ServiceChargeDetail serviceChargeDetail=serviceChargeRepository.findByIdAndServiceIdAndUserId(chargeId,serviceId,userSessionDetails.getUserID().longValue());
            if(Objects.nonNull(serviceChargeDetail)) {
                serviceChargeDTO=serviceChargeDetail.getServiceChargeJson();
            }
            if (serviceChargeDTO != null) {
                serviceChargeDTO.setChargeId(serviceChargeDetail.getId());
                if (serviceVersionTokenService.isMinorModification(serviceId)) {
                    ServiceChargeDTO baseline = serviceVersionTokenService.getBaselineServiceCharge(serviceId, chargeId);
                    serviceChargeDTO.setHistoryCharge(baseline);
                    serviceChargeDTO.setOldCharge(baseline);
                }
            }
            return ResponseEntity.ok().body(serviceChargeDTO);
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

	public ResponseEntity<?> getServiceChargeDetailList(Integer serviceId, UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		List<ServiceChargeResDTO> serviceChargeDTOList = new ArrayList<ServiceChargeResDTO>();
		try {
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
			Map<Long, List<LabelValue>> chargeTaskMap = new HashMap<>();

			if (serviceProcessFlow != null && serviceProcessFlow.getProcessFlowJson() != null
					&& serviceProcessFlow.getProcessFlowJson().getNodes() != null) {

				chargeTaskMap = serviceProcessFlow.getProcessFlowJson().getNodes().stream()
						.filter(node -> node.getData() != null).filter(node -> node.getData().getPayment() != null)
						.filter(node -> node.getData().getPayment().getChargeId() != null)
						.filter(node -> node.getData().getName() != null)
						.filter(node -> !node.getData().getName().trim().isEmpty()).collect(Collectors.groupingBy(
								node -> node.getData().getPayment().getChargeId(), Collectors.mapping(node -> {
									LabelValue labelValue = new LabelValue();
									labelValue.setLabel(node.getData().getName());
									labelValue.setValue(node.getId());
									return labelValue;
								}, Collectors.toList())));
			}

			List<ServiceChargeDetail> serviceChargeDetailList = serviceChargeRepository
					.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
			for (ServiceChargeDetail serviceChargeDetail : serviceChargeDetailList) {

				ServiceChargeResDTO serviceChargeDTO =
				mapToResponse(serviceChargeDetail.getServiceChargeJson());

				Long chargeId = serviceChargeDetail.getId();

				serviceChargeDTO.setChargeId(chargeId);

				List<LabelValue> tasks = chargeTaskMap.getOrDefault(chargeId, new ArrayList<LabelValue>());

				serviceChargeDTO.setTasks(tasks);

				serviceChargeDTOList.add(serviceChargeDTO);
			}
			return ResponseEntity.ok().body(serviceChargeDTOList);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	private ServiceChargeResDTO mapToResponse(ServiceChargeDTO source) {

		ServiceChargeResDTO target = new ServiceChargeResDTO();

		target.setServiceId(source.getServiceId());
		target.setTemplateName(source.getTemplateName());
		target.setChargeDefinedFor(source.getChargeDefinedFor());
		target.setServiceUnitWise(source.getServiceUnitWise());
		target.setDefaultCharge(source.getDefaultCharge());

		if (source.getChargeTypeList() != null) {
			List<ServiceChargeResDTO.ChargeTypeDTO> chargeTypes = source.getChargeTypeList().stream().map(item -> {
				ServiceChargeResDTO.ChargeTypeDTO dto = new ServiceChargeResDTO.ChargeTypeDTO();

				dto.setName(item.getName());
				dto.setAmount(item.getAmount());

				return dto;
			}).collect(Collectors.toList());

			target.setChargeTypeList(chargeTypes);
		}

		if (source.getPaymentModes() != null) {
			List<ServiceChargeResDTO.PaymentModeDTO> paymentModes = source.getPaymentModes().stream().map(item -> {
				ServiceChargeResDTO.PaymentModeDTO dto = new ServiceChargeResDTO.PaymentModeDTO();

				dto.setValue(item.getValue());
				dto.setLabel(item.getLabel());
				dto.setOnlineFlag(item.getOnlineFlag());

				return dto;
			}).collect(Collectors.toList());

			target.setPaymentModes(paymentModes);
		}

		if (source.getClosedUserType() != null) {
			List<ServiceChargeResDTO.ClosedUserTypeDTO> closedUserTypes = source.getClosedUserType().stream()
					.map(item -> {
						ServiceChargeResDTO.ClosedUserTypeDTO dto = new ServiceChargeResDTO.ClosedUserTypeDTO();

						dto.setValue(item.getValue());
						dto.setLabel(item.getLabel());

						return dto;
					}).collect(Collectors.toList());

			target.setClosedUserType(closedUserTypes);
		}

		return target;
	}
	
    public ResponseEntity<?> deleteChargeDetail(Integer serviceId, Long chargeId,UserSessionDTO userSessionDetails) {
        JSONObject responseJson=new JSONObject();
        try {
            if (serviceVersionTokenService.isMinorModification(serviceId) && serviceVersionTokenService.isServiceChargeInHistory(serviceId, chargeId)) {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Deleting existing charge templates is not allowed in minor version modification");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }

            ServiceChargeDetail serviceChargeDetail=serviceChargeRepository.findByIdAndServiceIdAndUserId(chargeId,serviceId,userSessionDetails.getUserID().longValue());
            if(Objects.nonNull(serviceChargeDetail)) {
            	List<String> taskNames = processFlowRepository.findTasksUsingChargeDetail(serviceId, serviceChargeDetail.getId());
            	if (!taskNames.isEmpty()) {
            	    String tasks = String.join(", ", taskNames);
            	    responseJson.put("errorCode", "400");
            	    responseJson.put("errorMessage", "This charge detail cannot be deleted because it is being used in the following task(s): "
            	        + tasks + ". Please remove the charge detail from the payment configuration before deleting it.");
            	    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            	}
                String tName = serviceChargeDetail.getServiceChargeJson() != null ? serviceChargeDetail.getServiceChargeJson().getTemplateName() : "Charge Template";
                ServiceChargeDTO delChargeDto = serviceChargeDetail.getServiceChargeJson();
                serviceChargeRepository.delete(serviceChargeDetail);
                serviceVersionTokenService.recordServiceChargeChangeLog(
                        serviceId,
                        "DELETE",
                        tName,
                        delChargeDto,
                        null,
                        userSessionDetails
                );
                responseJson.put("message", "Charge Detail deleted successfully.");
            }else {
                responseJson.put("errorCode", "400");
                responseJson.put("errorMessage", "Charge Detail does not exist.");
                return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok().body(responseJson.toString());
        }catch(Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    public ResponseEntity<?> freezeService(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
            ServiceLog serviceLog = validateAndGetServiceLog(serviceId);
            Set<String> mvelUsedAttrSet=new HashSet<String>();
            Set<String> documentUsedAttrSet=new HashSet<String>();
            ServiceDefinition serviceDefinition = serviceDefinitionRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service Definition not found"));
            ServiceDeliveryUnitDefinition serviceDeliveryUnitDefinition=serviceDeliveryUnitRepo.findByServiceId(serviceId);
            List<ServiceDeliveryUnitMappingDTO> serviceDeliveryUnitMappingDetails=new ArrayList<>();
            ServiceProcessFlow processFlow =validateAndGetProcessFlow(serviceId, userSessionDetails.getUserID());
            List<ServiceOutputFormat> serviceOutputFormat=serviceOutputFormatRepository.findByServiceId(serviceId);
            List<MvelFunction> mvelFunctionList=mvelFunctionRepository.findByServiceId(serviceId);
            List<NodeDTO> nodes = processFlow.getProcessFlowJson().getNodes();
            List<EdgeDTO> edges = processFlow.getProcessFlowJson().getEdges();
            Map<Object,List<OfficeDetailsDTO.OfficeUnitData>> entityLevelOfficeUnitMap=new HashMap<Object,List<OfficeDetailsDTO.OfficeUnitData>>();
            NodeDTO submissionNode = getSubmissionNode(nodes);

            List<AuaApiConfigurationDTO> auaApiConfigurations = buildAuaApiConfigurations(serviceId, userSessionDetails.getUserID(), userSessionDetails.getTenantId(),userSessionDetails);
            
            List<WorkFlowDataDTO> workFlowDetails = buildWorkFlowData(nodes);
            List<OfficeDetailsDTO> officeDetails = buildOfficeData(nodes,entityLevelOfficeUnitMap);
            Map<String, String> taskFormMapping = buildTaskFormMapping(nodes);
            ServiceProcessFlowDTO processFlowDTO = buildProcessFlowMap(serviceId,nodes,edges,officeDetails,taskFormMapping,userSessionDetails.getTenantId());
            List<ServiceProcessFlowDTO.AssociatedActivity> associatedActivities =buildAssociatedActivities(nodes,processFlow.getProcessFlowJson().getAssociatedTasks(),officeDetails,entityLevelOfficeUnitMap);
            processFlowDTO.setAssociatedActivities(associatedActivities);

            List<DocumentGenerationDetails> documentGenerationDetails = buildDocumentGenerationDetails(nodes);
            Map<String, ApplicationRouting> applicationRoutingMap =buildApplicationRoutingMap(nodes);
            List<NotificationConfiguration> notificationConfigurations = notificationRepository.findByTenantIdAndServiceId(userSessionDetails.getTenantId(), serviceId);
            List<NotificationDetails> notificationDetails = buildNotificationDetails(notificationConfigurations);

            Set<String> notificationUsedAttrSet=getAttributeUsedInNotification(notificationConfigurations);

            Set<String> inboxSentboxFiltersUsedAttrList = new HashSet<String>();
            List<InboxSentBoxFilter> inboxSentBoxFilters = inboxSentBoxFilterRepository.findByServiceIdAndStatus(serviceId.longValue(), Status.ACTIVE.getValue());
            if(!inboxSentBoxFilters.isEmpty()) {
            		buildInboxSentboxFilters(inboxSentBoxFilters, inboxSentboxFiltersUsedAttrList);
            		FilterKafkaMessage filterKafkaMessage = buildInboxSentBoxFilterConfigKafkaMessage(inboxSentBoxFilters);
            		metadataCreationLogger.info("Info : Send data to Kafka InboxSentBoxFilterConfigKafkaMessage, serviceId : {}", serviceId);
            		kafkaProducer.sendMessage(filterConfigurationTopic, String.valueOf(serviceId), filterKafkaMessage);
            		metadataCreationLogger.info("Info : Send Successfully data to Kafka InboxSentBoxFilterConfigKafkaMessage, serviceId : {}", serviceId);
            }
            
            List<ActivityMapDTO> activityFlowMap = buildActivityFlow(nodes,notificationConfigurations);
            documentUsedAttrSet= getAttributeListUsedInService(serviceOutputFormat);
            mvelUsedAttrSet=getAttributeUsedInMvel(mvelFunctionList);
            List<MvelDetailsDTO> mvelDetailsDTO = buildMvelDetails(mvelFunctionList);
            List<ExternalAttributeMappingDTO> externalAttributeMappingDTO =buildExternalSystem(nodes);
            List<OutputFormatFreezeDTO> outputFormatDetail=getOutputFormatDetails(serviceOutputFormat);
            List<WebServiceTaskDTO> webServiceTasks=buildWebServiceTasks(nodes);
            List<EscalationDetailsDTO> escalationDetails= buildEscalationDetails(nodes);
            List<TimerTaskDTO> timerTaskDetails=buildTimerTaskDTO(nodes);

            PaymentConfigurationDTO paymentConfiguration = buildPaymentConfiguration(serviceId, userSessionDetails, processFlowDTO);


            if (serviceDeliveryUnitDefinition != null) {
				List<ServiceDeliveryUnitJSON> serviceDeliveryUnits = serviceDeliveryUnitDefinition
						.getServiceDeliveryUnits();
				serviceDeliveryUnitMappingDetails = buildServiceDeliveryUnitMappingWithUser(serviceDeliveryUnits,
						entityLevelOfficeUnitMap);
			}
			updateOfficeLevelIdsWithServiceDeliveryUnit(officeDetails, serviceDeliveryUnitMappingDetails);
        
			Set<String> correlationAttributeIds = new HashSet<String>();
            List<CorrelationSetDTO> correlationDataSet = buildCorrelationDataSet(serviceId, correlationAttributeIds);
            
            if(!correlationDataSet.isEmpty()) {
            	correlationSetKafkaProducer.publishCorrelationSetDTO(correlationDataSet);
            }
            List<ServiceCombinationConfigurationDTO> externalDataSet = buildExternalDataSet(nodes, serviceId, serviceLog.getBaseServiceId());
            if(!externalDataSet.isEmpty()) {
            	serviceCombinationKafkaProducer.publishServiceCombination(externalDataSet);
            }

            UsedAttributeListDTO usedAttrList = buildUsedAttributeList(serviceId, notificationConfigurations, serviceOutputFormat, mvelFunctionList,paymentConfiguration);

            ServiceEsignConfigurationDTO esignConfiguration = buildServiceEsignConfigurationDTO(serviceId, userSessionDetails.getTenantId());
            BsaServiceMappingDTO bsaServiceMapping = buildBsaServiceMappingDTO(serviceId, userSessionDetails.getTenantId());
            
            ServiceMetadata metadata = metadataRepository.findById(serviceId)
                    .orElse(new ServiceMetadata(serviceLog));

            PatternGenerationConfigDTO patternGenerationConfig = generationConfigService.buildPatternGenerationConfig(serviceId.longValue(), userSessionDetails);
            usedAttrList.setDocumentUsedAttrList(patternGenerationConfig.getUsedAttributeList().getPatternGenerationUsedAttrList());
            
            updateMetadataJson(metadata,serviceDefinition,submissionNode,processFlowDTO,taskFormMapping,activityFlowMap,
                    officeDetails,documentUsedAttrSet,workFlowDetails,mvelDetailsDTO,mvelUsedAttrSet,
                    externalAttributeMappingDTO,outputFormatDetail,webServiceTasks,escalationDetails,timerTaskDetails,documentGenerationDetails,serviceDeliveryUnitMappingDetails, notificationDetails, notificationUsedAttrSet
                    , inboxSentboxFiltersUsedAttrList,auaApiConfigurations,applicationRoutingMap, correlationAttributeIds,paymentConfiguration,usedAttrList, esignConfiguration, bsaServiceMapping, patternGenerationConfig.getPatternConfigurations());

            serviceLog.setServiceStatus(1);
            serviceLogRepository.save(serviceLog);
            metadataRepository.save(metadata);
            List<NotificationConfigurationKafkaDTO> kafkaConfigurations = buildNotificationConfigurationKafkaDTOs(
					notificationConfigurations);

			if (!kafkaConfigurations.isEmpty()) {
				kafkaProducer.sendMessage(notificationConfigurationTopic, String.valueOf(serviceId),
						kafkaConfigurations);
			}
            RecommendedServiceFormKafkaDTO recommendedFormKafkaDTO = new RecommendedServiceFormKafkaDTO();
            recommendedFormKafkaDTO.setServiceId(serviceId);
            recommendedFormKafkaDTO.setServiceName(serviceDefinition.getDefinitionJson().getName());
            recommendedFormKafkaDTO.setServiceDesc(serviceDefinition.getDefinitionJson().getDescription());
            recommendedFormKafkaDTO.setForms(getFormIdAndName(nodes));
            recommendedFormKafkaDTO.setDepartmentId(metadata.getMetadataJson().getDepartmentId());
            recommendedFormKafkaDTO.setTenantId(metadata.getTenantId());
            formKafkaProducer.publishRecommendedForm(recommendedFormKafkaDTO);
            
            boolean hasDocEsign = documentGenerationDetails != null && documentGenerationDetails.stream().anyMatch(d -> d.getDocumentMapping() != null && d.getDocumentMapping().stream().anyMatch(m -> "true".equalsIgnoreCase(m.getEsignRequired())));
            if (esignConfiguration != null || hasDocEsign) {
                EsignConfigurationKafkaDTO esignKafkaMessage = new EsignConfigurationKafkaDTO();
                esignKafkaMessage.setServiceId(serviceId);
                esignKafkaMessage.setTenantId(userSessionDetails.getTenantId());
                esignKafkaMessage.setEsignConfiguration(esignConfiguration);

                if (esignConfiguration != null && esignConfiguration.getEsignTemplateId() != null && esignTemplateRepository != null) {
                    esignTemplateRepository.findById(esignConfiguration.getEsignTemplateId())
                            .ifPresent(tmpl -> esignKafkaMessage.setTemplate(tmpl.getTemplateJson()));
                }

                esignKafkaProducer.publishFreezeEvent(esignKafkaMessage);
            }

            if (bsaServiceMapping != null && bsaKafkaProducer != null) {
                BsaConfigurationKafkaDTO bsaKafkaMessage = new BsaConfigurationKafkaDTO();
                bsaKafkaMessage.setEventType("FREEZE");
                bsaKafkaMessage.setServiceId(serviceId);
                bsaKafkaMessage.setBaseServiceId(serviceLog.getBaseServiceId());
                bsaKafkaMessage.setTenantId(userSessionDetails.getTenantId());
                bsaKafkaMessage.setServiceMapping(bsaServiceMapping);
                bsaKafkaMessage.setAttributeMappings(bsaServiceMapping.getAttributeMappings());
                if (bsaServiceMappingService != null && bsaServiceMapping.getBsaConfigId() != null) {
                    BsaConfiguration bsaConfig = bsaServiceMappingService.findBsaConfigurationById(bsaServiceMapping.getBsaConfigId());
                    if (bsaConfig != null) {
                        BsaConfigurationKafkaDTO.BsaConfigPayload configPayload = new BsaConfigurationKafkaDTO.BsaConfigPayload();
                        configPayload.setId(bsaConfig.getId());
                        configPayload.setBaseServiceId(serviceLog.getBaseServiceId());
                        configPayload.setServiceId(serviceId);
                        configPayload.setSchemeCode(bsaConfig.getSchemeCode());
                        configPayload.setSchemeName(bsaConfig.getSchemeName());
                        configPayload.setUserName(bsaConfig.getUserName());
                        configPayload.setPassword(bsaConfig.getPassword());
                        configPayload.setStateCode(bsaConfig.getsCode());
                        configPayload.setServiceCode(bsaConfig.getServiceCode());
                        configPayload.setStagingUrl(bsaConfig.getStagingUrl());
                        configPayload.setProdUrl(bsaConfig.getProdUrl());
                        configPayload.setUserNameProd(bsaConfig.getUserNameProd());
                        configPayload.setPasswordProd(bsaConfig.getPasswordProd());
                        configPayload.setKeyProd(bsaConfig.getKeyProd());
                        configPayload.setIvProd(bsaConfig.getIvProd());
                        configPayload.setUserNameStaging(bsaConfig.getUserNameStaging());
                        configPayload.setPasswordStaging(bsaConfig.getPasswordStaging());
                        configPayload.setKeyStaging(bsaConfig.getKeyStaging());
                        configPayload.setIvStaging(bsaConfig.getIvStaging());
                        configPayload.setActiveStatus(bsaConfig.getActiveStatus());
                        configPayload.setAuthType(bsaConfig.getAuthType());
                        configPayload.setVersionNo(bsaConfig.getVersionNo());
                        configPayload.setMinorVersionNo(bsaConfig.getMinorVersion());
                        configPayload.setTenantId(bsaConfig.getTenantId());
                        bsaKafkaMessage.setBsaConfiguration(configPayload);
                    }
                }
                bsaKafkaProducer.publishFreezeEvent(bsaKafkaMessage);
            }
            return ResponseEntity.ok(Map.of("message", "Service frozen successfully"));

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error","Internal Server Error"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error"));
        }
    }

    private UsedAttributeListDTO buildUsedAttributeList(Integer serviceId, List<NotificationConfiguration> notificationConfigurations, List<ServiceOutputFormat> serviceOutputFormat, List<MvelFunction> mvelFunctionList, PaymentConfigurationDTO paymentConfiguration) {

        UsedAttributeListDTO usedAttrList = new UsedAttributeListDTO();

        Set<String> notificationUsedAttrSet = getAttributeUsedInNotification(notificationConfigurations);

        Set<String> documentUsedAttrSet = getAttributeListUsedInService(serviceOutputFormat);

        Set<String> mvelUsedAttrSet = getAttributeUsedInMvel(mvelFunctionList);

        Set<String> inboxSentboxFiltersUsedAttrList = new HashSet<>();

        List<InboxSentBoxFilter> inboxSentBoxFilters = inboxSentBoxFilterRepository.findByServiceIdAndStatus(serviceId.longValue(), Status.ACTIVE.getValue());

        if (!inboxSentBoxFilters.isEmpty()) {
            buildInboxSentboxFilters(inboxSentBoxFilters, inboxSentboxFiltersUsedAttrList);
            FilterKafkaMessage filterKafkaMessage = buildInboxSentBoxFilterConfigKafkaMessage(inboxSentBoxFilters);
            metadataCreationLogger.info("Info : Send data to Kafka InboxSentBoxFilterConfigKafkaMessage , serviceId : {}", serviceId);
            kafkaProducer.sendMessage(filterConfigurationTopic, String.valueOf(serviceId), filterKafkaMessage);
            metadataCreationLogger.info("Info : Send Successfully data to Kafka InboxSentBoxFilterConfigKafkaMessage, serviceId  : {}", serviceId);
        }

        Set<String> paymentUsedAttrSet = getAttributeUsedInPayment(paymentConfiguration);

        usedAttrList.setNotificationUsedAttrList(notificationUsedAttrSet);
        usedAttrList.setDocumentUsedAttrList(documentUsedAttrSet);
        usedAttrList.setMvelUsedAttrList(mvelUsedAttrSet);
        usedAttrList.setInboxSentboxFiltersUsedAttrList(inboxSentboxFiltersUsedAttrList);
        usedAttrList.setPaymentUsedAttrList(paymentUsedAttrSet);

        return usedAttrList;
    }

    private Set<String> getAttributeUsedInPayment(PaymentConfigurationDTO paymentConfiguration) {

        Set<String> paymentUsedAttrSet = new HashSet<>();

        if (paymentConfiguration == null || paymentConfiguration.getPaymentGatewayConfigurations() == null) {
            return paymentUsedAttrSet;
        }

        for (PaymentConfigurationDTO.PaymentGatewayConfiguration gatewayConfiguration : paymentConfiguration.getPaymentGatewayConfigurations()) {

            if (gatewayConfiguration == null || gatewayConfiguration.getMappings() == null) {
                continue;
            }

            for (PaymentConfigurationDTO.GatewayMapping mapping : gatewayConfiguration.getMappings()) {

                if (mapping == null) {
                    continue;
                }

                if ("DYNAMIC".equalsIgnoreCase(mapping.getSourceType()) && mapping.getSourceValue() != null && !mapping.getSourceValue().isBlank()) {
                    paymentUsedAttrSet.add(mapping.getSourceValue());
                }
            }
        }

        return paymentUsedAttrSet;
    }

    private List<AuaApiConfigurationDTO> buildAuaApiConfigurations(Integer serviceId, Long userId, String tenantId, UserSessionDTO userSessionDetails) {

        metadataCreationLogger.info("Starting AUA metadata creation. serviceId={}, userId={}, tenantId={}",
                serviceId, userId, tenantId);

        List<AuaApiConfigurationDTO> result = new ArrayList<>();

        List<AuaApiFieldMapping> mappings = mappingRepository.findByTenantIdAndServiceId(tenantId, serviceId);

        metadataCreationLogger.info("AUA service mappings fetched. serviceId={}, tenantId={}, count={}",
                serviceId, tenantId, mappings == null ? 0 : mappings.size());

        if (mappings == null || mappings.isEmpty()) {
            metadataCreationLogger.warn("No AUA field mappings found. serviceId={}, tenantId={}", serviceId, tenantId);
            return result;
        }

        String configurationId = mappings.stream()
                .map(AuaApiFieldMapping::getConfigurationId)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse(null);

        if (configurationId == null) {
            metadataCreationLogger.warn("No Aadhaar configurationId found in AUA field mappings. serviceId={}, tenantId={}", serviceId, tenantId);
            return result;
        }

        metadataCreationLogger.info("Aadhaar configurationId resolved. serviceId={}, configurationId={}", serviceId, configurationId);

        Map<String, AuaApiFieldMapping> mappingMap = buildMappingMap(mappings);

        ResponseEntity<AadhaarConfiguration> response = tenantConfigurationFeignClient.getAadhaarConfiguration(configurationId, entityToString(userSessionDetails));

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            metadataCreationLogger.warn("Aadhaar configuration not found. configurationId={}, serviceId={}", configurationId, serviceId);
            return result;
        }

        AadhaarConfiguration configuration = response.getBody();

        metadataCreationLogger.info("Aadhaar configuration resolved. configurationId={}, configurationName={}, auaName={}",
                configuration.getId(), configuration.getConfigurationName(), configuration.getAuaName());

        if (configuration.getApiConfigurations() == null || configuration.getApiConfigurations().isEmpty()) {

            metadataCreationLogger.warn("No Aadhaar API configurations found. configurationId={}", configurationId);
            return result;
        }

        for (AadhaarApiConfiguration api : configuration.getApiConfigurations()) {

            if (api == null || api.getApiType() == null) {
                continue;
            }

            String apiType = api.getApiType().name();

            metadataCreationLogger.info("Processing Aadhaar API. apiType={}, displayName={}", apiType, api.getDisplayName());

            AuaApiConfigurationDTO apiDTO = new AuaApiConfigurationDTO();

            apiDTO.setApiType(apiType);
            apiDTO.setEndpoint(api.getEndpoint());
            apiDTO.setXmlNamespace(api.getXmlNamespace());
            apiDTO.setSuffix(configuration.getTransactionIdConfiguration().getSuffix());
            apiDTO.setPrefix(configuration.getTransactionIdConfiguration().getPrefix());

            if (api.getRequestPayload() != null) {

                AuaApiConfigurationDTO.AuaPayloadDTO requestPayload = buildRequestPayload(api.getRequestPayload(), apiType, mappingMap);

                if (requestPayload.getNodes() != null && !requestPayload.getNodes().isEmpty()) {
                    apiDTO.setRequestPayload(requestPayload);
                }
            }

            if (api.getResponsePayload() != null) {

                AuaApiConfigurationDTO.AuaPayloadDTO responsePayload = buildResponsePayload(api.getResponsePayload(), apiType, mappingMap);

                if (responsePayload.getNodes() != null && !responsePayload.getNodes().isEmpty()) {
                    apiDTO.setResponsePayload(responsePayload);
                }
            }

            if (apiDTO.getRequestPayload() != null || apiDTO.getResponsePayload() != null) {
                result.add(apiDTO);
            } else {
                metadataCreationLogger.debug("Skipping Aadhaar API because no configured nodes are available. apiType={}", apiType);
            }
        }

        metadataCreationLogger.info("AUA metadata creation completed. serviceId={}, configurationId={}, apiCount={}", serviceId, configurationId, result.size());

        return result;
    }

    private Map<String, AuaApiFieldMapping> buildMappingMap(List<AuaApiFieldMapping> mappings) {

        if (mappings == null || mappings.isEmpty()) {
            return new HashMap<>();
        }

        return mappings.stream()
                .collect(Collectors.toMap(mapping ->
                        createMappingKey(mapping.getApiType(), mapping.getNodeCode()), Function.identity(), (first, second) -> second)
                );
    }

    private String createMappingKey(String apiType, String nodeCode) {
        return apiType.toLowerCase() + ":" + nodeCode.toLowerCase();
    }

    private AuaApiConfigurationDTO.AuaPayloadDTO buildRequestPayload(AadhaarPayloadConfiguration sourcePayload, String apiType, Map<String, AuaApiFieldMapping> mappingMap) {

        AuaApiConfigurationDTO.AuaPayloadDTO payload = new AuaApiConfigurationDTO.AuaPayloadDTO();

        if (sourcePayload.getNodes() == null || sourcePayload.getNodes().isEmpty()) {
            return payload;
        }

        List<AuaApiConfigurationDTO.AuaNodeDTO> nodes = new ArrayList<>();

        for (AadhaarNodeConfiguration sourceNode : sourcePayload.getNodes()) {

            if (sourceNode == null || sourceNode.getNodeCode() == null) {
                continue;
            }

            AadhaarMappingType mappingType = sourceNode.getMappingType();

            /*
             * NONE nodes are never required in processing metadata.
             */
            if (mappingType == AadhaarMappingType.NONE) {
                continue;
            }

            AuaApiConfigurationDTO.AuaNodeDTO node = new AuaApiConfigurationDTO.AuaNodeDTO();

            node.setNodeCode(sourceNode.getNodeCode());
            node.setNodeName(sourceNode.getDisplayName());
            node.setXpath(sourceNode.getXpath());
            node.setMappingType(mappingType);
            node.setEnableLogs(sourceNode.isEnableLogs());
            node.setMaskValue(sourceNode.isMaskValue());

            /*
             * Instance Admin configured value.
             */
            if (hasAdminConfiguration(sourceNode)) {

                applyAdminMapping(node, sourceNode);
                nodes.add(node);

                metadataCreationLogger.debug("Admin mapping applied. apiType={}, nodeCode={}, mappingType={}", apiType, sourceNode.getNodeCode(), mappingType);

                continue;
            }

            /*
             * SYSTEM_GENERATED and PLUGIN are completely
             * configured by Instance Admin.
             */
            if (mappingType == AadhaarMappingType.SYSTEM_GENERATED) {

                if (hasSystemGeneratedConfiguration(sourceNode)) {

                    applySystemGeneratedMapping(node, sourceNode);
                    nodes.add(node);
                }

                continue;
            }

            if (mappingType == AadhaarMappingType.PLUGIN) {

                if (hasPluginConfiguration(sourceNode)) {

                    applyPluginMapping(node, sourceNode);
                    nodes.add(node);
                }

                continue;
            }

            /*
             * DYNAMIC / STATIC without an Admin value:
             * check Service Definer mapping.
             */
            if (mappingType == AadhaarMappingType.DYNAMIC || mappingType == AadhaarMappingType.STATIC || mappingType == AadhaarMappingType.ADD_VALUE) {

                AuaApiFieldMapping mapping = mappingMap.get(createMappingKey(apiType, sourceNode.getNodeCode()));

                if (mapping == null) {
                    metadataCreationLogger.debug("Skipping unconfigured request node. apiType={}, nodeCode={}, mappingType={}", apiType, sourceNode.getNodeCode(), mappingType);

                    continue;
                }

                applyServiceMapping(node, mapping);
                nodes.add(node);
            }
        }

        payload.setNodes(nodes);
        return payload;
    }

    private boolean hasAdminConfiguration(AadhaarNodeConfiguration sourceNode) {

        if (sourceNode.getValueConfiguration() == null) {
            return false;
        }

        String value = sourceNode.getValueConfiguration().getValue();

        return value != null && !value.isBlank();
    }

    private void applyAdminMapping(AuaApiConfigurationDTO.AuaNodeDTO node, AadhaarNodeConfiguration sourceNode) {

        AadhaarNodeValueConfiguration valueConfiguration = sourceNode.getValueConfiguration();

        if (valueConfiguration == null) {
            return;
        }

        node.setDefaultValue(valueConfiguration.getValue());
    }

    private boolean hasSystemGeneratedConfiguration(AadhaarNodeConfiguration sourceNode) {

        return sourceNode.getValueConfiguration() != null
                && sourceNode.getValueConfiguration().getSystemVariable() != null
                && !sourceNode.getValueConfiguration().getSystemVariable().isBlank();
    }

    private void applySystemGeneratedMapping(AuaApiConfigurationDTO.AuaNodeDTO node, AadhaarNodeConfiguration sourceNode) {

        node.setSystemVariable(sourceNode.getValueConfiguration().getSystemVariable());
    }

    private boolean hasPluginConfiguration(AadhaarNodeConfiguration sourceNode) {

        return sourceNode.getValueConfiguration() != null && sourceNode.getValueConfiguration().getPluginMethodCode() != null && !sourceNode.getValueConfiguration().getPluginMethodCode().isBlank();
    }

    private void applyPluginMapping(AuaApiConfigurationDTO.AuaNodeDTO node, AadhaarNodeConfiguration sourceNode) {

        node.setPluginMethodCode(sourceNode.getValueConfiguration().getPluginMethodCode());
    }

    private void applyServiceMapping(AuaApiConfigurationDTO.AuaNodeDTO node, AuaApiFieldMapping mapping) {

        node.setSourcePath(mapping.getSourcePath());

        node.setTransformation(mapping.getTransformation());

        node.setDefaultValue(mapping.getDefaultValue());

        metadataCreationLogger.debug("Service mapping applied. nodeCode={}, sourceType={}", node.getNodeCode(), mapping.getSourceType());
    }

    private AuaApiConfigurationDTO.AuaPayloadDTO buildResponsePayload(AadhaarPayloadConfiguration sourcePayload, String apiType, Map<String, AuaApiFieldMapping> mappingMap) {

        AuaApiConfigurationDTO.AuaPayloadDTO payload = new AuaApiConfigurationDTO.AuaPayloadDTO();

        if (sourcePayload.getNodes() == null || sourcePayload.getNodes().isEmpty()) {
            return payload;
        }

        List<AuaApiConfigurationDTO.AuaNodeDTO> nodes = new ArrayList<>();

        for (AadhaarNodeConfiguration sourceNode : sourcePayload.getNodes()) {

            if (sourceNode == null || sourceNode.getNodeCode() == null) {
                continue;
            }

            AadhaarMappingType mappingType = sourceNode.getMappingType();

            /*
             * NONE is never included.
             */
            if (mappingType == AadhaarMappingType.NONE) {
                continue;
            }

            AuaApiConfigurationDTO.AuaNodeDTO node = new AuaApiConfigurationDTO.AuaNodeDTO();

            node.setNodeCode(sourceNode.getNodeCode());
            node.setNodeName(sourceNode.getDisplayName());
            node.setXpath(sourceNode.getXpath());
            node.setMappingType(mappingType);
            node.setEnableLogs(sourceNode.isEnableLogs());
            node.setMaskValue(sourceNode.isMaskValue());

            if (mappingType == AadhaarMappingType.RESULT) {

                if (!hasAdminConfiguration(sourceNode)) {

                    metadataCreationLogger.debug("Skipping unconfigured admin response node. apiType={}, nodeCode={}, mappingType={}",
                            apiType, sourceNode.getNodeCode(), mappingType);

                    continue;
                }

                applyAdminResponseMapping(node, sourceNode);

                nodes.add(node);

                continue;
            }

            if (mappingType == AadhaarMappingType.ERROR || mappingType == AadhaarMappingType.INFO) {

                if (sourceNode.getXpath() == null || sourceNode.getXpath().isBlank()) {

                    metadataCreationLogger.debug("Skipping admin response node without XPath. apiType={}, nodeCode={}, mappingType={}",
                            apiType, sourceNode.getNodeCode(), mappingType);

                    continue;
                }

                applyAdminResponseMapping(node, sourceNode);

                nodes.add(node);

                continue;
            }

            /*
             * DYNAMIC response mapping is configured
             * by Service Definer.
             */
            if (mappingType == AadhaarMappingType.DYNAMIC) {

                AuaApiFieldMapping mapping = mappingMap.get(createMappingKey(apiType, sourceNode.getNodeCode()));

                if (mapping == null) {

                    metadataCreationLogger.debug("Skipping unconfigured dynamic response node. apiType={}, nodeCode={}", apiType, sourceNode.getNodeCode());

                    continue;
                }

                applyResponseMapping(node, mapping);

                nodes.add(node);

                continue;
            }

            /*
             * STATIC response mapping can also be
             * Service Definer configured if required.
             */
            if (mappingType == AadhaarMappingType.STATIC) {

                AuaApiFieldMapping mapping = mappingMap.get(createMappingKey(apiType, sourceNode.getNodeCode()));

                if (mapping == null) {

                    metadataCreationLogger.debug("Skipping unconfigured static response node. apiType={}, nodeCode={}", apiType, sourceNode.getNodeCode());

                    continue;
                }

                applyResponseMapping(node, mapping);

                nodes.add(node);
            }
        }

        payload.setNodes(nodes);

        return payload;
    }

    private void applyAdminResponseMapping(AuaApiConfigurationDTO.AuaNodeDTO node, AadhaarNodeConfiguration sourceNode) {

        AadhaarMappingType mappingType = sourceNode.getMappingType();
        node.setMappingType(mappingType);

        if (mappingType == AadhaarMappingType.RESULT) {

            AadhaarNodeValueConfiguration valueConfiguration = sourceNode.getValueConfiguration();
            node.setResponseAttributeType(AadhaarMappingType.RESULT.name());
            node.setDesiredResponse(valueConfiguration.getValue());
        }

        if (mappingType == AadhaarMappingType.ERROR) {
            node.setResponseAttributeType(AadhaarMappingType.ERROR.name());
        }

        if (mappingType == AadhaarMappingType.INFO) {
            node.setResponseAttributeType(AadhaarMappingType.INFO.name());
        }

        metadataCreationLogger.debug("Admin response mapping applied. nodeCode={}, mappingType={}", sourceNode.getNodeCode(), mappingType);
    }

    private void applyResponseMapping(AuaApiConfigurationDTO.AuaNodeDTO node, AuaApiFieldMapping mapping) {

        node.setResponseAttributeType(mapping.getResponseAttributeType());

        node.setDesiredResponse(mapping.getDesiredResponse());

        metadataCreationLogger.debug("Service Definer response mapping applied. nodeCode={}, responseAttributeType={}", node.getNodeCode(), mapping.getResponseAttributeType());
    }


	private List<NotificationConfigurationKafkaDTO> buildNotificationConfigurationKafkaDTOs(
			List<NotificationConfiguration> configurations) {

		List<NotificationConfigurationKafkaDTO> result = new ArrayList<>();

		if (configurations == null || configurations.isEmpty()) {
			return result;
		}

		for (NotificationConfiguration configuration : configurations) {

			result.add(buildKafkaDTO(configuration));
		}

		return result;
	}
	
	private NotificationConfigurationKafkaDTO buildKafkaDTO(
	        NotificationConfiguration entity) {

	    NotificationConfigurationKafkaDTO dto =
	            new NotificationConfigurationKafkaDTO();

	    dto.setNotificationId(entity.getNotificationId());
	    dto.setBaseServiceId(entity.getBaseServiceId());
	    dto.setServiceId(entity.getServiceId());
	    dto.setNotificationName(entity.getNotificationName());
	    dto.setChannel(entity.getChannel());
	    dto.setTriggerPointId(entity.getTriggerPointId());
	    dto.setTaskId(entity.getTaskId());
	    dto.setConfigJson(entity.getConfigJson());

	    dto.setCreatedBy(entity.getCreatedBy());
	    dto.setModifiedBy(entity.getModifiedBy());
	    dto.setTenantId(entity.getTenantId());

	    return dto;
	}

    private void updateOfficeLevelIdsWithServiceDeliveryUnit(
            List<OfficeDetailsDTO> officeDetails,
            List<ServiceDeliveryUnitMappingDTO> serviceDeliveryUnitMappings) {
        if (officeDetails == null || officeDetails.isEmpty()
                || serviceDeliveryUnitMappings == null
                || serviceDeliveryUnitMappings.isEmpty()) {
            return;
        }
        for (ServiceDeliveryUnitMappingDTO mapping : serviceDeliveryUnitMappings) {
            Integer deliveryUnitLevelId = mapping.getDeliveryUnitLevelId();
            boolean exists = officeDetails.stream()
                    .anyMatch(officeDetail ->
                            officeDetail.getOfficeLevelIds() != null
                                    && officeDetail.getOfficeLevelIds().contains(deliveryUnitLevelId));
            if (!exists) {
                OfficeDetailsDTO officeDetail = new OfficeDetailsDTO();
                officeDetail.setTaskId("DO");
                Set<Integer> officeLevelIds = new HashSet<>();
                officeLevelIds.add(deliveryUnitLevelId);
                officeDetail.setOfficeLevelIds(officeLevelIds);
                officeDetail.setAllowedOffices(mapping.getOfficeUnits());
                officeDetails.add(officeDetail);
            }
        }
    }

	private List<ServiceDeliveryUnitMappingDTO> buildServiceDeliveryUnitMappingWithUser(
			List<ServiceDeliveryUnitJSON> serviceDeliveryUnits,
			Map<Object, List<OfficeUnitData>> entityLevelOfficeUnitMap) {
		List<ServiceDeliveryUnitMappingDTO> mappings = new ArrayList<>();
		if (serviceDeliveryUnits == null || serviceDeliveryUnits.isEmpty()) {
			return mappings;
		}
		for (ServiceDeliveryUnitJSON serviceDeliveryUnit : serviceDeliveryUnits) {
			ServiceDeliveryUnitMappingDTO mapping = new ServiceDeliveryUnitMappingDTO();
			// Delivery Unit
			if(serviceDeliveryUnit.getServiceDeliveryUnit()!=null &&serviceDeliveryUnit.getServiceDeliveryUnit().getValue()!=null) {
				mapping.setDeliveryUnitLevelId(
						Integer.parseInt(serviceDeliveryUnit.getServiceDeliveryUnit().getValue().toString()));
				mapping.setDeliveryUnitLevelName(serviceDeliveryUnit.getServiceDeliveryUnit().getLabel());
			}
			// User Role
			Integer userRoleId = Integer.parseInt(serviceDeliveryUnit.getUserRole().getValue().toString());
			mapping.setUserRoleId(userRoleId);
			if (userRoleId == 3) {
				mapping.setUserRole("Designated Officer");
				mapping.setDesignatedOfficerUserLevelId(
						Integer.parseInt(serviceDeliveryUnit.getUserLevel().getEntityLevel().getValue().toString()));
				mapping.setDesignatedOfficerUserLevelName(serviceDeliveryUnit.getUserLevel().getEntityLevel().getLabel());
			}
			mapping.setOfficeUnits(
					getOfficeUnits(serviceDeliveryUnit.getServiceDeliveryUnit(), entityLevelOfficeUnitMap));
			mappings.add(mapping);
		}
		return mappings;
	}

	private List<OfficeUnitData> getOfficeUnits(HierarchyLabelValue hierarchy,
			Map<Object, List<OfficeUnitData>> entityLevelOfficeUnitMap) {
		Entities entity = new Entities();
		entity.setEntity(hierarchy.getEntity());
		entity.setEntityLevel(hierarchy.getEntityLevel());
		entity.setClcDetail(hierarchy.getClcDetail());
		return getOfficeUnits(entity, entityLevelOfficeUnitMap);
	}
        
    private List<RecommendedServiceFormKafkaDTO.Form> getFormIdAndName(List<NodeDTO> nodes) {
        List<RecommendedServiceFormKafkaDTO.Form> formList = new ArrayList<RecommendedServiceFormKafkaDTO.Form>();

        nodes.forEach(node -> formList.add(new RecommendedServiceFormKafkaDTO.Form(
                node.getData().getFormDetail() != null ? node.getData().getFormDetail().getFormId() : null,
                node.getData().getFormDetail() != null ? node.getData().getFormDetail().getParentForm() : null
        )));

        return formList;
    }

    private List<OutputFormatFreezeDTO> getOutputFormatDetails(List<ServiceOutputFormat> serviceOutputFormatList) {

        List<OutputFormatFreezeDTO> outputFormatDetails = new ArrayList<>();

        for (ServiceOutputFormat serviceOutputFormat : serviceOutputFormatList) {

            if (serviceOutputFormat.getOutputFormatJson() == null) {
                continue;
            }

            ServiceOutputFormatDTO outputFormatJson =
                    serviceOutputFormat.getOutputFormatJson();

            OutputFormatFreezeDTO dto = new OutputFormatFreezeDTO();

            dto.setId(serviceOutputFormat.getId());
            dto.setLayout(outputFormatJson.getLayout());
            dto.setPageSize(outputFormatJson.getPageSize());
            dto.setUsedAttr(outputFormatJson.getUsedAttr());
            dto.setTemplateName(outputFormatJson.getTemplateName());
            dto.setDocumentType(outputFormatJson.getDocumentType());
            dto.setTemplateBodySrc(outputFormatJson.getTemplateBodySrc());
            dto.setWaterMark(outputFormatJson.getWaterMark());
            dto.setXsd(outputFormatJson.getXsd());
            dto.setXmlNodes(outputFormatJson.getXmlNodes());
            dto.setJsonNodes(outputFormatJson.getJsonNodes());
            dto.setRootType(outputFormatJson.getRootType());

            outputFormatDetails.add(dto);
        }

        return outputFormatDetails;
    }

    private List<ExternalAttributeMappingDTO> buildExternalSystem(List<NodeDTO> nodes) {

        List<ExternalAttributeMappingDTO> result = new ArrayList<>();

        for (NodeDTO node : nodes) {

            if (node == null || node.getData() == null
                    || node.getData().getExternalSystemDetail() == null
                    || node.getData().getExternalSystemDetail().isEmpty()) {
                continue;
            }

            for (ExternalSystemDetail externalDetail : node.getData().getExternalSystemDetail()) {

                ExternalAttributeMappingDTO dto = new ExternalAttributeMappingDTO();
                dto.setNodeId(node.getId());
                dto.setExternalSystemRegId(externalDetail.getExternalSystemRegId());

                if (externalDetail.getMappingData() != null
                        && !externalDetail.getMappingData().isEmpty()) {

                    List<AttrMappingDetails> mappingDetails = externalDetail.getMappingData()
                            .stream()
                            .map(mapping -> {
                                AttrMappingDetails attr = new AttrMappingDetails();
                                attr.setNodeRef(mapping.getNodeReference());
                                attr.setAttributeId(mapping.getUniqueId());
                                return attr;
                            })
                            .collect(Collectors.toList());

                    dto.setAttrMappingDetails(mappingDetails);
                }

                result.add(dto);
            }
        }

        return result;
    }
    private Set<String> getAttributeUsedInMvel(List<MvelFunction> mvelFunctionList) {
        Set<String> usedMvelAttribute=new HashSet<String>();
        if (mvelFunctionList == null || mvelFunctionList.isEmpty()) {
            return usedMvelAttribute;
        }
        for (MvelFunction mvelFunction : mvelFunctionList) {
            MvelFunctionDTO dto = mvelFunction.getMvelFunctionJson();
            if (dto.getParameters() != null && !dto.getParameters().isEmpty()) {
                for (MvelFunctionDTO.FunctionParameter p : dto.getParameters()) {
                    if(p.getParamType().equals(ApplicationConstants.ATTRIBUTE))
                        usedMvelAttribute.add(p.getParamId());
                }
            }
        }
        return usedMvelAttribute;
    }
    private List<MvelDetailsDTO> buildMvelDetails(List<MvelFunction> mvelFunctionList) {
        List<MvelDetailsDTO> mvelDetailList = new ArrayList<>();

        if (mvelFunctionList == null || mvelFunctionList.isEmpty()) {
            return mvelDetailList;
        }
        MvelFunctionEvent mvelFnEvent = new MvelFunctionEvent();
        List<MvelFunctionEvent.FunctionPayload> functions=new ArrayList<>();
        mvelFnEvent.setEventType("Freeze");

        if (!mvelFunctionList.isEmpty()) {
            mvelFnEvent.setServiceId(mvelFunctionList.get(0).getServiceId());
            mvelFnEvent.setVersionNo(mvelFunctionList.get(0).getVersionNo());
            mvelFnEvent.setMinorVersionNo(mvelFunctionList.get(0).getMinorVersionNo());
        }
        for (MvelFunction mvelFunction : mvelFunctionList) {
            MvelDetailsDTO mvelDetails = new MvelDetailsDTO();
            MvelFunctionDTO dto = mvelFunction.getMvelFunctionJson();
            MvelFunctionEvent.FunctionPayload fnPayload = new MvelFunctionEvent.FunctionPayload();
            fnPayload.setFunctionId(mvelFunction.getId());
            fnPayload.setFunctionName(dto.getFunctionName());
            fnPayload.setFunctionBody(dto.getFunctionBody());

            if (dto.getParameters() != null && !dto.getParameters().isEmpty()) {

                List<MvelFunctionEvent.FunctionPayload.ParameterPayload> paramList = new ArrayList<>();

                for (MvelFunctionDTO.FunctionParameter p : dto.getParameters()) {

                    MvelFunctionEvent.FunctionPayload.ParameterPayload param =
                            new MvelFunctionEvent.FunctionPayload.ParameterPayload();

                    param.setParamId(p.getParamId());
                    param.setParamType(p.getParamType());
                    paramList.add(param);
                }

                fnPayload.setParameters(paramList);
            }
            TriggerPoint triggerPoint = mvelFunction.getMvelFunctionJson().getTriggerPoint();

            if (triggerPoint.getValue() == null || triggerPoint.getNodeId() == null) {
                continue;
            }
            mvelDetails.setLabel(triggerPoint.getLabel());
            mvelDetails.setValue(triggerPoint.getValue());
            mvelDetails.setNodeId(triggerPoint.getNodeId());
            mvelDetails.setMvelId(mvelFunction.getId());
            mvelDetailList.add(mvelDetails);
            functions.add(fnPayload);
        }
        mvelFnEvent.setFunctions(functions);
        mvelKafkaProducer.publishFreezeEvent(mvelFnEvent);
        return mvelDetailList;
    }
    private List<WorkFlowDataDTO> buildWorkFlowData(List<NodeDTO> nodes) {
        List<WorkFlowDataDTO> workFlowDataList = new ArrayList<>();

        for (NodeDTO node : nodes) {
            if (!ApplicationConstants.GATEWAY_NODE_TYPE.equals(node.getType())) {
                WorkFlowDataDTO dto = new WorkFlowDataDTO();
                dto.setTaskId(node.getId());

                List<WorkFlowAction> actionList = new ArrayList<>();
                Map<String, Action> workflow = node.getData().getWorkflow();
                for (Map.Entry<String, Action> entry : workflow.entrySet()) {

                    Action action = entry.getValue();
                    if (Boolean.TRUE.equals(action.getEnabled())) {

                        WorkFlowAction actionDTO = new WorkFlowAction();
                        actionDTO.setActionKey(entry.getKey().split("_")[1]);
                        actionDTO.setActionLabel(action.getActionFormLabel());
                        actionDTO.setTrackLabel(action.getTrackLabel());
                        actionDTO.setTrackLabelOfficial(action.getHistoryLabel());
                        if (action.getRemarks() != null) {
                            actionDTO.setRemarksRequired(action.getRemarks().getMandatory());
                        }
                        actionDTO.setLogicalClosure(action.getLogicalClosure());
                        actionDTO.setCompleteClosure(action.getCompleteClosure());
                        actionList.add(actionDTO);
                    }
                }

                dto.setAllowedAction(actionList);
                workFlowDataList.add(dto);
            }
        }
        return workFlowDataList;
    }
    private Set<String> getAttributeListUsedInService(List<ServiceOutputFormat> serviceOutputFormatList) {
        Set<String> usedAttributeList= new HashSet<String>();
        for(ServiceOutputFormat serviceOutputFormat:serviceOutputFormatList) {
            if(serviceOutputFormat.getOutputFormatJson()!=null) {
                List<String> attributeIds = serviceOutputFormat.getOutputFormatJson().getUsedAttr()!=null && !serviceOutputFormat.getOutputFormatJson().getUsedAttr().isEmpty()?serviceOutputFormat.getOutputFormatJson().getUsedAttr().stream().map(attr -> attr.replaceFirst("^\\$attr_", "")).toList():new ArrayList<String>();
                usedAttributeList.addAll(attributeIds);
            }
        }
        return usedAttributeList;
    }
    private List<OfficeDetailsDTO> buildOfficeData(List<NodeDTO> nodes, Map<Object, List<OfficeDetailsDTO.OfficeUnitData>> entityLevelOfficeUnitMap) {
        List<OfficeDetailsDTO> officeList = new ArrayList<>();

        for (NodeDTO node : nodes) {
            if (!ApplicationConstants.GATEWAY_NODE_TYPE.equals(node.getType())) {
                OfficeDetailsDTO dto = new OfficeDetailsDTO();
                List<OfficeUnitData> officeUnitData=new ArrayList<>();
                Set<Integer> officeLevelIds=new HashSet<>();
                dto.setTaskId(node.getId());
                if (node.getData().getEntities() != null && !node.getData().getEntities().isEmpty()) {
                    for(Entities dept:node.getData().getEntities()) {
                        officeLevelIds.add(Integer.parseInt(dept.getEntityLevel().getValue().toString()));
                        officeUnitData.addAll(getOfficeUnits(dept, entityLevelOfficeUnitMap));
                    }
                    dto.setAllowedOffices(officeUnitData);
                    dto.setOfficeLevelIds(officeLevelIds);
                }
                officeList.add(dto);
            }
        }
        return officeList;
    }
    private ServiceLog validateAndGetServiceLog(Integer serviceId) {
        ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
        if (serviceLog == null) {
            throw new RuntimeException("Invalid Service");
        }
        return serviceLog;
    }

    private ServiceProcessFlow validateAndGetProcessFlow(Integer serviceId, Long userId) {
        ServiceProcessFlow flow = processFlowRepository.findByServiceIdAndUserId(serviceId, userId);

        if (flow == null || flow.getProcessFlowJson() == null) {
            throw new RuntimeException("Process flow does not exist for this service and user.");
        }
        return flow;
    }

    private NodeDTO getSubmissionNode(List<NodeDTO> nodes) {
        return nodes.stream()
                .filter(n -> ApplicationConstants.SUBMISSION_NODE_TYPE.equals(n.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Submission node not found in process flow"));
    }
    
    private void addAssociatedTaskOfficeDetails(List<OfficeDetailsDTO> officeDetails, AssociatedTaskDTO task,
			Map<Object, List<OfficeDetailsDTO.OfficeUnitData>> entityLevelOfficeUnitMap) {

		if (task == null || task.getOfficialIntimation() == null || task.getOfficialIntimation().getEntities() == null
				|| task.getOfficialIntimation().getEntities().isEmpty()) {
			return;
		}
		
		OfficeDetailsDTO officeDTO = null;

		for (OfficeDetailsDTO dto : officeDetails) {
			if (Objects.equals(dto.getTaskId(), task.getId())) {
				officeDTO = dto;
				break;
			}
		}

		if (officeDTO == null) {
			officeDTO = new OfficeDetailsDTO();
			officeDTO.setTaskId(task.getId());
			officeDTO.setAllowedOffices(new ArrayList<>());
			officeDTO.setOfficeLevelIds(new HashSet<>());
			officeDetails.add(officeDTO);
		}

		List<OfficeUnitData> offices = officeDTO.getAllowedOffices();
		Set<Integer> officeLevels = officeDTO.getOfficeLevelIds();

		for (Entities entity : task.getOfficialIntimation().getEntities()) {

			officeLevels.add(Integer.parseInt(entity.getEntityLevel().getValue().toString()));

			List<OfficeUnitData> unitData = getOfficeUnits(entity, entityLevelOfficeUnitMap);

			for (OfficeUnitData unit : unitData) {
				boolean exists = false;

				for (OfficeUnitData existing : offices) {
					if (Objects.equals(existing.getOrgUnitCode(), unit.getOrgUnitCode())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					offices.add(unit);
				}
			}
		}
	}

    private ServiceProcessFlowDTO buildProcessFlowMap(Integer serviceId, List<NodeDTO> nodes, List<EdgeDTO> edges, List<OfficeDetailsDTO> officeDetails, Map<String, String> taskFormMapping, String tenantId) {

        Map<String, List<String>> edgeMap = edges.stream()
                .collect(Collectors.groupingBy(
                        EdgeDTO::getSource,
                        Collectors.mapping(EdgeDTO::getTarget, Collectors.toList())
                ));

        Map<String, NodeDTO> nodeMap = nodes.stream()
                .collect(Collectors.toMap(NodeDTO::getId, Function.identity()));

        ServiceProcessFlowDTO serviceProcessFlowDTO = new ServiceProcessFlowDTO();
        serviceProcessFlowDTO.setServiceId(serviceId);

        List<ServiceProcessFlowDTO.Data> dataList = new ArrayList<>();
        List<TaskPropertyDetails> taskProperty = new ArrayList<TaskPropertyDetails>();
        for (NodeDTO node : nodes) {
            List<OfficeDetailsDTO> nodeOfficeDetails = officeDetails.stream().filter(off -> off.getTaskId().equals(node.getId())).collect(Collectors.toList());
            ServiceProcessFlowDTO.Data data = new ServiceProcessFlowDTO.Data();
            boolean bulkEnabled=false;
            boolean priority=false;
            boolean callbackEnabled=false;
            ServiceProcessFlowDTO.Data.Nodes dtoNode = new ServiceProcessFlowDTO.Data.Nodes();
            ServiceProcessFlowDTO.Data.NodeProperty nodeProperty = new ServiceProcessFlowDTO.Data.NodeProperty();
            
            dtoNode.setId(node.getId());
            dtoNode.setType(node.getType());
            dtoNode.setName(node.getData() != null ? node.getData().getName() : null);
            dtoNode.setFormId(taskFormMapping.get(node.getId()));
			dtoNode.setIsGlobal(node.getData() != null && node.getData().getFormDetail() != null
					&& node.getData().getFormDetail().getIsGlobal() != null
							? node.getData().getFormDetail().getIsGlobal()
							: false);
            dtoNode.setBehaviour(node.getData() != null && node.getData().getBehaviour() != null ? (String) node.getData().getBehaviour().getValue():null);
            dtoNode.setPayment(node.getData().getPayment());
            TaskType taskType = TaskType.resolve(node.getData());
            dtoNode.setTaskType(taskType != null ? taskType.getType() : null);
            if (node.getData() != null) {
                dtoNode.setKioskType(node.getData().getKioskType());
                dtoNode.setIdentifiedUserSetIds(node.getData().getIdentifiedUserSetIds());
            }

            populateApplicantConfiguration(dtoNode, node);
            
            callbackEnabled=node.getData().getAdvanced()!=null && 
            		node.getData().getAdvanced().getOfficial()!=null && 
            		node.getData().getAdvanced().getOfficial().isConditionalCallback();
            
            priority= node.getData().getAdvanced()!=null && 
            		node.getData().getAdvanced().getOfficial()!=null &&
            		node.getData().getAdvanced().getOfficial().isPriority();
            
            bulkEnabled=node.getData().getAdvanced()!=null && 
            		node.getData().getAdvanced().getOfficial()!=null &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing()!=null &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing().isValue() &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing().getData() !=null &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing().getData().getMultipleProcessingType()!=null &&
            		"bulk".equals(node.getData().getAdvanced().getOfficial().getMultiProcessing().getData().getMultipleProcessingType().getValue());
            
            nodeProperty.setBulkEnabled(bulkEnabled);
            nodeProperty.setCallbackEnabled(callbackEnabled);
            nodeProperty.setPriorityEnabled(priority);
            
            data.setNode(dtoNode);
            data.setNodeProperty(nodeProperty);
			TaskPropertyDetails task = new TaskPropertyDetails();

			task.setTaskId(node.getId());

			task.setTaskName(node.getData() != null ? node.getData().getName() : null);

			task.setTaskType(taskType != null ? taskType.getType() : null);

			Gson gson = new Gson();
			
			JsonObject taskPropertyJson = JsonParser.parseString(gson.toJson(nodeProperty)).getAsJsonObject();
			
			task.setTaskProperty(taskPropertyJson);
			
			taskProperty.add(task);
			
            List<String> targetNodeIds = edgeMap.getOrDefault(node.getId(), Collections.emptyList());

            List<ServiceProcessFlowDTO.Data.MappedTask> mappedTasks = targetNodeIds.stream()
                    .map(targetId -> {
                        ServiceProcessFlowDTO.Data.MappedTask mt = new ServiceProcessFlowDTO.Data.MappedTask();

                        NodeDTO targetNode = nodeMap.get(targetId);
                        if (targetNode != null) {
                            ServiceProcessFlowDTO.Data.Nodes mappedNode = new ServiceProcessFlowDTO.Data.Nodes();
                            mappedNode.setId(targetNode.getId());
                            mappedNode.setType(targetNode.getType());
                            mappedNode.setName(targetNode.getData() != null ? targetNode.getData().getName() : null);
                            mappedNode.setBehaviour(targetNode.getData() != null && targetNode.getData().getBehaviour() != null? (String) targetNode.getData().getBehaviour().getValue():null);
                            mappedNode.setFormId(taskFormMapping.get(targetNode.getId()));
                            TaskType mappedTaskType = TaskType.resolve(targetNode.getData());
                            mappedNode.setTaskType(mappedTaskType != null ? mappedTaskType.getType() : null);
                            if (targetNode.getData() != null) {
                                mappedNode.setKioskType(targetNode.getData().getKioskType());
                                mappedNode.setIdentifiedUserSetIds(targetNode.getData().getIdentifiedUserSetIds());
                            }

                            populateApplicantConfiguration(mappedNode, targetNode);

                            mt.setNode(mappedNode);
                        }

                        return mt;
                    })
                    .collect(Collectors.toList());

            data.setMappedTasks(mappedTasks);

            if (TYPE_TASK.equalsIgnoreCase(node.getType())) {
                data.setWorkflowElementData(
                        buildWorkflowElementData(
                                node,
                                mappedTasks,
                                edgeMap,
                                nodeMap,
                                officeDetails
                        )
                );
            }


            if(!nodeOfficeDetails.isEmpty()) {
                data.setAllowedOffices(nodeOfficeDetails.getFirst().getAllowedOffices());
            }
            dataList.add(data);
        }

        serviceProcessFlowDTO.setData(dataList);
        serviceProcessFlowDTO.setTaskRelation(buildTaskRelationMap(nodes, edges));
        TaskPropertyDetailsEvent event = new TaskPropertyDetailsEvent();

        event.setEventType("TASK_PROPERTY_DETAILS");
        event.setTenantId(tenantId);
        event.setServiceId(serviceId);
        event.setBaseServiceId(serviceId/10000);
        event.setTasks(taskProperty);

        kafkaProducer.sendMessage(taskPropertyTopic, String.valueOf(serviceId),event);
        return serviceProcessFlowDTO;
    }

    private void populateApplicantConfiguration(ServiceProcessFlowDTO.Data.Nodes targetNode, NodeDTO sourceNode) {

        if (sourceNode == null || sourceNode.getData() == null) {
            return;
        }

        TaskType taskType = TaskType.resolve(sourceNode.getData());

        if (taskType == null || !taskType.equals(APPLICANT_TASK)) {
            return;
        }

        if (sourceNode.getData().getAdvanced() == null || sourceNode.getData().getAdvanced().getApplicant() == null) {
            return;
        }

        targetNode.setApplicant(sourceNode.getData().getAdvanced().getApplicant());
    }

    private Map<String, TaskRelationDTO> buildTaskRelationMap(List<NodeDTO> nodes, List<EdgeDTO> edges) {

        Map<String, NodeDTO> nodeMap = nodes.stream()
                .collect(Collectors.toMap(NodeDTO::getId, Function.identity()));

        Map<String, List<String>> outgoingMap = edges.stream()
                .collect(Collectors.groupingBy(
                        EdgeDTO::getSource,
                        Collectors.mapping(EdgeDTO::getTarget, Collectors.toList())
                ));

        Map<String, List<String>> incomingMap = edges.stream()
                .collect(Collectors.groupingBy(
                        EdgeDTO::getTarget,
                        Collectors.mapping(EdgeDTO::getSource, Collectors.toList())
                ));

        Map<String, TaskRelationDTO> relationMap = new LinkedHashMap<>();

        for (NodeDTO node : nodes) {

            TaskRelationDTO dto = new TaskRelationDTO();

            Set<String> nextTask = new LinkedHashSet<>();
            collectNextTasks(node.getId(),outgoingMap,nodeMap,new HashSet<>(),nextTask);

            Set<String> previousTask = new LinkedHashSet<>();
            collectPreviousTasks(node.getId(),incomingMap,nodeMap,new HashSet<>(),previousTask);

            dto.setNextTask(new ArrayList<>(nextTask));
            dto.setPreviousTask(new ArrayList<>(previousTask));

            relationMap.put(node.getId(), dto);
        }

        return relationMap;
    }
	
	private void collectNextTasks(String nodeId, Map<String, List<String>> edgeMap, Map<String, NodeDTO> nodeMap,
			Set<String> visited, Set<String> result) {

		if (!visited.add(nodeId)) {
			return;
		}

		List<String> targets = edgeMap.get(nodeId);
		if(targets!=null && !targets.isEmpty()) {
			for (String target : targets) {
	
				NodeDTO node = nodeMap.get(target);
	
				if (node == null) {
					continue;
				}
	
				if (ApplicationConstants.GATEWAY_NODE_TYPE.equals(node.getType())) {
					collectNextTasks(target, edgeMap, nodeMap, visited, result);
				} else {
					result.add(target);
				}
			}
		}
	}

	private void collectPreviousTasks(String nodeId, Map<String, List<String>> incomingMap,
			Map<String, NodeDTO> nodeMap, Set<String> visited, Set<String> result) {

		if (!visited.add(nodeId)) {
			return;
		}

		List<String> sources = incomingMap.get(nodeId);
		if(sources!=null && !sources.isEmpty()) {
			for (String source : sources) {
	
				NodeDTO node = nodeMap.get(source);
	
				if (node == null) {
					continue;
				}
	
				if (ApplicationConstants.GATEWAY_NODE_TYPE.equals(node.getType())) {
					collectPreviousTasks(source, incomingMap, nodeMap, visited, result);
				} else {
					result.add(source);
				}
			}
		}
	}

    private ServiceProcessFlowDTO.Data.WorkflowElementData buildWorkflowElementData(
            NodeDTO currentNode,
            List<ServiceProcessFlowDTO.Data.MappedTask> mappedTasks,
            Map<String, List<String>> edgeMap,
            Map<String, NodeDTO> nodeMap,
            List<OfficeDetailsDTO> officeDetails) {

        ServiceProcessFlowDTO.Data.WorkflowElementData workflowElementData = new ServiceProcessFlowDTO.Data.WorkflowElementData();

        List<EditPermissionData> editPermissions = new ArrayList<ServiceProcessFlowDTO.Data.EditPermissionData>();
        
        // ---------------- Action Attributes ----------------

        List<ServiceProcessFlowDTO.Data.ActionAttribute> actionAttributes = new ArrayList<>();

        if (currentNode.getData() != null && currentNode.getData().getWorkflow() != null) {

            currentNode.getData().getWorkflow().forEach((actionKey, workflow) -> {

                if (Boolean.TRUE.equals(workflow.getEnabled())) {

                    ServiceProcessFlowDTO.Data.ActionAttribute action =
                            new ServiceProcessFlowDTO.Data.ActionAttribute();

                    action.setKey(actionKey.replace("action_", ""));
                    action.setLabel(workflow.getActionFormLabel());
                    action.setTrackLabel(workflow.getTrackLabel());
                    action.setLogicalClosure(workflow.getLogicalClosure());
                    action.setCompleteClosure(workflow.getCompleteClosure());

                    actionAttributes.add(action);
                }
            });
        }

        workflowElementData.setActionAttribute(actionAttributes);

        if (!mappedTasks.isEmpty()) {

            ServiceProcessFlowDTO.Data.Nodes nextNode = mappedTasks.getFirst().getNode();

            // ---------------- Gateway ----------------

            if (TYPE_GATEWAY.equalsIgnoreCase(nextNode.getType())) {

                String selectionType = null;

                if (GATEWAY_BEHAVIOUR_EXCLUSIVE_DIVERGENT.equalsIgnoreCase(nextNode.getBehaviour())) {
                    selectionType = "RADIO";
                } else if (GATEWAY_BEHAVIOUR_INCLUSIVE_DIVERGENT.equalsIgnoreCase(nextNode.getBehaviour())) {
                    selectionType = "CHECKBOX";
                }

                if (!isNull(selectionType)) {

                    ServiceProcessFlowDTO.Data.TaskAttribute taskAttribute =
                            new ServiceProcessFlowDTO.Data.TaskAttribute();

                    List<ServiceProcessFlowDTO.Data.TaskNode> taskNodes = new ArrayList<>();

                    ServiceProcessFlowDTO.Data.UserAttribute userAttribute =
                            new ServiceProcessFlowDTO.Data.UserAttribute();

                    List<ServiceProcessFlowDTO.Data.UserNode> userNodes = new ArrayList<>();

                    taskAttribute.setGatewayType(nextNode.getBehaviour());

                    List<String> gatewayTargets = edgeMap.getOrDefault(nextNode.getId(), Collections.emptyList());

                    for (String targetId : gatewayTargets) {

                        NodeDTO taskNode = nodeMap.get(targetId);

                        if (taskNode == null) {
                            continue;
                        }

                        boolean showTask = false;
                        boolean showUser = false;

                        if (taskNode.getData() != null
                                && taskNode.getData().getAdvanced() != null
                                && taskNode.getData().getAdvanced().getDisplayUserTask() != null
                                && taskNode.getData().getAdvanced().getDisplayUserTask().getData() != null) {

                            showTask = taskNode.getData()
                                    .getAdvanced()
                                    .getDisplayUserTask()
                                    .getData()
                                    .isTaskVisibilityOnPreviousTask();

                            showUser = taskNode.getData()
                                    .getAdvanced()
                                    .getDisplayUserTask()
                                    .getData()
                                    .isUserVisibilityOnPreviousTask();
                        }

                        // ---------------- Task ----------------

                        ServiceProcessFlowDTO.Data.TaskNode task = new ServiceProcessFlowDTO.Data.TaskNode();

                        task.setTaskId(taskNode.getId());
                        task.setTaskName(taskNode.getData().getName());
                        task.setShowSelection(showTask);

                        taskNodes.add(task);

                        // ---------------- User ----------------

                        ServiceProcessFlowDTO.Data.UserNode user = new ServiceProcessFlowDTO.Data.UserNode();

                        user.setTaskId(taskNode.getId());
                        user.setShowSelection(showUser);

                        userNodes.add(user);
                    }

                    taskAttribute.setSelectionType(selectionType);
                    taskAttribute.setTaskNodes(taskNodes);

                    userAttribute.setSelectionType("CHECKBOX");
                    userAttribute.setUserNodes(userNodes);

                    workflowElementData.setTaskAttribute(taskAttribute);
                    workflowElementData.setUserAttribute(userAttribute);
                }
            }

            // ---------------- Direct Task ----------------
            // No TaskAttribute required.
            // But UserAttribute can still be configured for a direct task.

            ServiceProcessFlowDTO.Data.UserAttribute userAttribute = new ServiceProcessFlowDTO.Data.UserAttribute();

            List<ServiceProcessFlowDTO.Data.UserNode> userNodes = new ArrayList<>();

            if (!TYPE_GATEWAY.equalsIgnoreCase(nextNode.getType())) {

                OfficeDetailsDTO officeDetail = officeDetails.stream()
                        .filter(o -> o.getTaskId() != null && o.getTaskId().equals(nextNode.getId()))
                        .findFirst()
                        .orElse(null);

                if (officeDetail != null && officeDetail.getAllowedOffices() != null && !officeDetail.getAllowedOffices().isEmpty()) {

                    for (OfficeDetailsDTO.OfficeUnitData office : officeDetail.getAllowedOffices()) {

                        ServiceProcessFlowDTO.Data.UserNode user = new ServiceProcessFlowDTO.Data.UserNode();

                        user.setTaskId(nextNode.getId());
                        user.setLocationId(office.getOrgUnitCode() != null ? office.getOrgUnitCode().longValue() : null);
                        user.setLocationName(office.getOrgUnitName());
                        user.setShowSelection(Boolean.TRUE);

                        userNodes.add(user);
                    }
                }
            }

            if (!userNodes.isEmpty()) {
                userAttribute.setSelectionType("CHECKBOX");
                userAttribute.setUserNodes(userNodes);
                workflowElementData.setUserAttribute(userAttribute);
            }
            
            if (!TYPE_GATEWAY.equalsIgnoreCase(nextNode.getType())) {
            	if(currentNode.getData().getEditPermission() != null) {
            		EditForm editPermissionProcessFlow = currentNode.getData().getEditPermission();
            		if(editPermissionProcessFlow.getValue()) {
            			if(editPermissionProcessFlow.getData() != null) {
            				List<EditFormData> datas = editPermissionProcessFlow.getData();
            				for(EditFormData editFormData : datas) {
            					if (editFormData.getControl() != null
            					        && editFormData.getControl().getValue() != null
            					        && Integer.parseInt(
            					                editFormData.getControl().getValue().toString()
            					           ) == EditControl.SPECIFIC_FIELDS.getValue()) {
            						EditPermissionData editPermissionData = new EditPermissionData();
            						editPermissionData.setSelectedFormId(editFormData.getSelectedForm().getValue().toString());
            						editPermissionData.setSelectedHolderId(editFormData.getSelectedHolderId());
            						editPermissionData.setSpecific(editFormData.getSpecific());
            						editPermissionData.setSortOrder(editFormData.getSortOrder());
            						editPermissionData.setLabel(editFormData.getSelectedForm().getLabel().toString());
            						editPermissionData.setModifyChoice(Integer.parseInt(editFormData.getModifyChoice().getValue().toString()));
            						editPermissionData.setPlacement(editPermissionProcessFlow.getPlacement().getValue().toString());
            						editPermissions.add(editPermissionData);
            					}
            				}
            			}
            			
            		}
            	}
            	workflowElementData.setEditPermission(editPermissions);
            }

        }

        boolean priority = false;
        boolean bulkEnabled = false;
        if (currentNode.getData() != null
                && currentNode.getData().getAdvanced() != null
                && currentNode.getData().getAdvanced().getOfficial() != null) {
            priority = currentNode.getData().getAdvanced().getOfficial().isPriority();
			bulkEnabled = currentNode.getData().getAdvanced() != null
					&& currentNode.getData().getAdvanced().getOfficial() != null
					&& currentNode.getData().getAdvanced().getOfficial().getMultiProcessing() != null
					&& currentNode.getData().getAdvanced().getOfficial().getMultiProcessing().isValue()
					&& currentNode.getData().getAdvanced().getOfficial().getMultiProcessing().getData() != null
					&& currentNode.getData().getAdvanced().getOfficial().getMultiProcessing().getData().getMultipleProcessingType() != null
					&& "bulk".equals(currentNode.getData().getAdvanced().getOfficial().getMultiProcessing().getData()
							.getMultipleProcessingType().getValue());

        }
        
        if(currentNode.getData().getFormDetail() != null) {
        	boolean isGlobal = currentNode.getData().getFormDetail().getIsGlobal() != null ? currentNode.getData().getFormDetail().getIsGlobal() : false;
        	workflowElementData.setIsGlobal(isGlobal);
        }
        
        workflowElementData.setPriorityEnabled(priority);
        workflowElementData.setBulkEnabled(bulkEnabled);
        
        return workflowElementData;
    }
    
    private Map<String, ApplicationRouting> buildApplicationRoutingMap(List<NodeDTO> nodes) {

        Map<String, ApplicationRouting> applicationRoutingMap = new HashMap<>();

        for (NodeDTO node : nodes) {

            if (node == null || node.getData() == null || node.getData().getApplicationRouting() == null) {
                continue;
            }
            applicationRoutingMap.put(node.getId(),node.getData().getApplicationRouting());
        }

        return applicationRoutingMap;
    }

    private List<DocumentGenerationDetails> buildDocumentGenerationDetails(List<NodeDTO> nodes) {

        metadataCreationLogger.info("========== Building Document Generation Metadata ==========");

        List<DocumentGenerationDetails> result = new ArrayList<>();

        if (nodes == null || nodes.isEmpty()) {
            metadataCreationLogger.info("No nodes found.");
            return result;
        }

        metadataCreationLogger.info("Total Nodes : {}", nodes.size());

        for (NodeDTO node : nodes) {

            metadataCreationLogger.info("---------------------------------------------");
            metadataCreationLogger.info("Processing Node : {} ({})", node.getId(), node.getType());

            // Only submission & task participate
            if (!(ApplicationConstants.TYPE_TASK.equalsIgnoreCase(node.getType())
                    || ApplicationConstants.SUBMISSION_NODE_TYPE.equalsIgnoreCase(node.getType()))) {

                metadataCreationLogger.info("Skipping node because type is {}", node.getType());
                continue;
            }

            DocumentGenerationDetails dto = new DocumentGenerationDetails();
            dto.setTaskId(node.getId());

            List<DocMappingDTO> mappings = new ArrayList<>();

            if (node.getData() == null) {

                metadataCreationLogger.info("Node data is null.");
                dto.setDocumentMapping(mappings);
                result.add(dto);
                continue;
            }

            if (node.getData().getDocumentMapping() == null
                    || node.getData().getDocumentMapping().isEmpty()) {

                metadataCreationLogger.info("No document mapping configured for {}", node.getId());

                dto.setDocumentMapping(mappings);
                result.add(dto);
                continue;
            }

            metadataCreationLogger.info("Found {} document mappings",
                    node.getData().getDocumentMapping().size());

            for (DocMappingDTO source : node.getData().getDocumentMapping()) {

                metadataCreationLogger.info("Document : {}", source.getDocumentName());

                DocMappingDTO target = new DocMappingDTO();

                target.setReferenceId(source.getReferenceId());
                target.setDocumentName(source.getDocumentName());
                target.setDocumentSource(source.getDocumentSource());

                //-----------------------------
                // Digital Signature
                //-----------------------------

                if (source.getDigitalSignatureRequired() != null) {

                    String ds = source.getDigitalSignatureRequired().trim().toLowerCase();

                    if ("yes".equals(ds)) {
                        target.setDigitalSignatureRequired("true");
                    } else if ("no".equals(ds)) {
                        target.setDigitalSignatureRequired("false");
                    } else {
                        target.setDigitalSignatureRequired("false");
                    }
                }
                
              //-----------------------------
                // eSign Configuration
                //-----------------------------

                if (source.getEsignRequired() != null) {
                    String es = source.getEsignRequired().trim().toLowerCase();
                    if ("yes".equals(es) || "true".equals(es)) {
                        target.setEsignRequired("true");
                    } else {
                        target.setEsignRequired("false");
                    }
                } else if (source.getEsignEnabled() != null) {
                    target.setEsignRequired(source.getEsignEnabled() ? "true" : "false");
                }
                target.setEsignPlacementMode(source.getEsignPlacementMode());
                target.setEsignPositionVariable(source.getEsignPositionVariable());

                target.setViewPermission(source.getViewPermission());
                target.setOfficialViewTasks(source.getOfficialViewTasks());

                target.setIsFileUploadOptional(source.getIsFileUploadOptional());
                target.setIsDraftDocumentRequired(source.getIsDraftDocumentRequired());

                target.setMergeMode(source.getMergeMode());

                //-----------------------------
                // Actions
                //-----------------------------

                if (source.getAction() != null) {

                    List<DocMappingDTO.LabelValueDTO> actions = getLabelValueDTOS(source);

                    target.setAction(actions);

                    metadataCreationLogger.info("Mapped {} actions", actions.size());
                }

                //-----------------------------
                // Linked Task
                //-----------------------------

                if (source.getLinkDocumentFromTask() != null) {

                    DocMappingDTO.LabelValueDTO copy = new DocMappingDTO.LabelValueDTO();

                    copy.setLabel(source.getLinkDocumentFromTask().getLabel());
                    copy.setValue(source.getLinkDocumentFromTask().getValue());

                    target.setLinkDocumentFromTask(copy);
                }

                //-----------------------------
                // Linked Document
                //-----------------------------

                if (source.getLinkedDocument() != null) {

                    DocMappingDTO.LabelValueDTO copy = new DocMappingDTO.LabelValueDTO();

                    copy.setLabel(source.getLinkedDocument().getLabel());
                    copy.setValue(source.getLinkedDocument().getValue());

                    target.setLinkedDocument(copy);
                }

                //-----------------------------
                // System Generated
                //-----------------------------

                if (source.getSystemGeneratedDocument() != null) {

                    DocMappingDTO.SystemGeneratedDocumentDTO copy = new DocMappingDTO.SystemGeneratedDocumentDTO();

                    copy.setLabel(source.getSystemGeneratedDocument().getLabel());
                    copy.setValue(source.getSystemGeneratedDocument().getValue());

                    target.setSystemGeneratedDocument(copy);
                }

                //-----------------------------
                // File Types
                //-----------------------------

                if (source.getFileType() != null) {

                    List<DocMappingDTO.LabelValueDTO> fileTypes = getValueDTOS(source);
                    target.setFileType(fileTypes);
                }

                //-----------------------------
                // Merge Order
                //-----------------------------

                if (source.getMergeOrder() != null) {

                    List<DocMappingDTO.MergeOrderDTO> mergeOrders = getMergeOrderDTOS(source);
                    target.setMergeOrder(mergeOrders);
                }

                mappings.add(target);
            }

            dto.setDocumentMapping(mappings);

            result.add(dto);

            metadataCreationLogger.info("Added metadata for task {} with {} documents",
                    node.getId(),
                    mappings.size());
        }

        metadataCreationLogger.info("============================================");
        metadataCreationLogger.info("Total Document Generation Entries : {}", result.size());

        return result;
    }

    private static List<DocMappingDTO.MergeOrderDTO> getMergeOrderDTOS(DocMappingDTO source) {
        List<DocMappingDTO.MergeOrderDTO> mergeOrders = new ArrayList<>();

        for (DocMappingDTO.MergeOrderDTO order : source.getMergeOrder()) {

            DocMappingDTO.MergeOrderDTO copy = new DocMappingDTO.MergeOrderDTO();

            copy.setId(order.getId());
            copy.setLabel(order.getLabel());
            copy.setSortOrder(order.getSortOrder());

            mergeOrders.add(copy);
        }
        return mergeOrders;
    }

    private static List<DocMappingDTO.LabelValueDTO> getValueDTOS(DocMappingDTO source) {
        List<DocMappingDTO.LabelValueDTO> fileTypes = new ArrayList<>();

        for (DocMappingDTO.LabelValueDTO file : source.getFileType()) {

            DocMappingDTO.LabelValueDTO copy = new DocMappingDTO.LabelValueDTO();

            copy.setLabel(file.getLabel());
            copy.setValue(file.getValue());

            fileTypes.add(copy);
        }
        return fileTypes;
    }

    private static List<DocMappingDTO.LabelValueDTO> getLabelValueDTOS(DocMappingDTO source) {
        List<DocMappingDTO.LabelValueDTO> actions = new ArrayList<>();

        for (DocMappingDTO.LabelValueDTO action : source.getAction()) {

            DocMappingDTO.LabelValueDTO copy = new DocMappingDTO.LabelValueDTO();

            copy.setLabel(action.getLabel());
            copy.setValue(action.getValue().replace("action_", ""));

            actions.add(copy);
        }
        return actions;
    }

    private List<ServiceProcessFlowDTO.AssociatedActivity> buildAssociatedActivities(
            List<NodeDTO> nodes,
            List<AssociatedTaskDTO> associatedTasks,
            List<OfficeDetailsDTO> officeDetails,
            Map<Object, List<OfficeDetailsDTO.OfficeUnitData>> entityLevelOfficeUnitMap) {

        if (nodes == null || associatedTasks == null) {
            return Collections.emptyList();
        }

        Map<String, AssociatedTaskDTO> taskMap = associatedTasks.stream()
                .collect(Collectors.toMap(AssociatedTaskDTO::getId, Function.identity()));

        List<ServiceProcessFlowDTO.AssociatedActivity> activityList = new ArrayList<>();

        for (NodeDTO node : nodes) {
            if (node.getData() == null || node.getData().getAssociatedTasks() == null) {
                continue;
            }
			for (AssociatedTaskReferenceDTO ref : node.getData().getAssociatedTasks()) {
				AssociatedTaskDTO task = taskMap.get(ref.getAssociatedTaskId());
				if (task == null) {
					continue;
				}
				ServiceProcessFlowDTO.AssociatedActivity activity = new ServiceProcessFlowDTO.AssociatedActivity();
				activity.setId(task.getId());

				activity.setType(
						task.getAssociatedTaskType() != null ? task.getAssociatedTaskType().getValue().toString()
								: null);
				activity.setSourceTaskId(node.getId());
				activity.setTriggerPoint(ref.getTriggerPoint());
				activity.setTriggerOnAction(ref.getTriggerOnAction());
				activity.setExecutionTasks(ref.getExecutionTasks());
				activity.setFormDetail(ref.getFormDetail());

				WebserviceDetails master = task.getWebserviceDetails();

				if (master != null) {
				    WebserviceDetails details = new WebserviceDetails();

				    details.setWsCall(master.getWsCall());
				    details.setEntities(master.getEntities());
				    details.setWsSelected(master.getWsSelected());
				    details.setApplicationStatus(master.getApplicationStatus());

				    // node-specific
				    details.setDefinition(ref.getApiDefinition());

				    activity.setWebServiceDetails(details);
				}
			
				if (task.getOfficialIntimation() != null) {

					ServiceProcessFlowDTO.OfficialIntimation official = new ServiceProcessFlowDTO.OfficialIntimation();

					official.setAllowApplicationView(task.getOfficialIntimation().getAllowApplicationView());

					official.setAllowHistoryView(task.getOfficialIntimation().getAllowHistoryView());

					official.setAutoClear(task.getOfficialIntimation().getAutoClear());

					List<OfficeUnitData> offices = new ArrayList<>();

					for (Entities entity : task.getOfficialIntimation().getEntities()) {
						offices.addAll(getOfficeUnits(entity, entityLevelOfficeUnitMap));
					}

					official.setAllowedOffices(offices);

					activity.setOfficialIntimation(official);
				}

				addAssociatedTaskOfficeDetails(officeDetails, task, entityLevelOfficeUnitMap);

				activityList.add(activity);
			}
        }

        return activityList;
    }
    private List<OfficeUnitData> getOfficeUnits(Entities entity,Map<Object, List<OfficeDetailsDTO.OfficeUnitData>> entityLevelOfficeUnitMap) {

        Object levelId = entity.getEntityLevel().getValue();

        List<OfficeUnitData> officeUnits = entityLevelOfficeUnitMap.get(levelId);
        if (officeUnits != null && !officeUnits.isEmpty()) {
            return officeUnits;
        }

        LevelEntityRequestDTO request = new LevelEntityRequestDTO();
        request.setLevelCode(Integer.parseInt(levelId.toString()));
        request.setEntityCode(Integer.parseInt(entity.getEntity().getValue().toString()));
        request.setClc(entity.getClcDetail() != null ? Long.parseLong(entity.getClcDetail().getValue().toString()) : null);
        request.setParentUnitCode(null);
        request.setUnitId(null);
        request.setLocationCode(null);

        List<LevelWiseUnitsDTO> units = lgdFeignClient.getLevelWiseUnits(request);

        officeUnits = new ArrayList<>();

        if (units != null) {
            for (LevelWiseUnitsDTO unit : units) {

                OfficeUnitData office = new OfficeUnitData();
                office.setOrgUnitCode(unit.getEntityUnitCode().intValue());
                office.setOrgUnitName(unit.getEntityUnitName());

                officeUnits.add(office);
            }
        }
        entityLevelOfficeUnitMap.put(levelId, officeUnits);

        return officeUnits;
    }
    private Map<String, String> buildTaskFormMapping(List<NodeDTO> nodes) {
        Map<String, String> mapping = new HashMap<>();
        for (NodeDTO node : nodes) {
            if (node.getData().getFormDetail() != null) {
                mapping.put(node.getId(), node.getData().getFormDetail().getFormId());
            }
            if (node.getData().getAssociatedTasks() != null) {
                for (AssociatedTaskReferenceDTO associatedTask : node.getData().getAssociatedTasks()) {
                    if (associatedTask.getFormDetail() != null) {
                        mapping.put(associatedTask.getAssociatedTaskId(),associatedTask.getFormDetail().getFormId());
                    }
                }
            }
        }
        return mapping;
    }

    private List<ActivityMapDTO> buildActivityFlow(List<NodeDTO> nodes,List<NotificationConfiguration> notificationConfigurations) {

		List<ActivityMapDTO> activityFlowList = new ArrayList<>();
		boolean bulkEnabled=false;
		for (NodeDTO node : nodes) {

			if (ApplicationConstants.GATEWAY_NODE_TYPE.equals(node.getType())) {
				continue;
			}

			List<ActivityMapDTO.ActivityData> activityDataList = new ArrayList<>();

			List<NotificationConfiguration> formSubmissionNotifications = new ArrayList<>();

			List<NotificationConfiguration> taskCompletionNotifications = new ArrayList<>();

			if (notificationConfigurations != null) {

				for (NotificationConfiguration notification : notificationConfigurations) {

					if (notification.getTaskId() == null || !notification.getTaskId().equals(node.getId())
							|| notification.getTriggerPointId() == null) {
						continue;
					}

					Integer triggerPointId;

					try {
						triggerPointId = Integer.valueOf(notification.getTriggerPointId());
					} catch (NumberFormatException ex) {
						continue;
					}

					if (triggerPointId == 3 || triggerPointId == 4) {

						formSubmissionNotifications.add(notification);

					} else if (triggerPointId == 1) {

						taskCompletionNotifications.add(notification);
					}
				}
			}
			bulkEnabled=node.getData().getAdvanced()!=null && 
            		node.getData().getAdvanced().getOfficial()!=null &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing()!=null &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing().isValue() &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing().getData() !=null &&
            		node.getData().getAdvanced().getOfficial().getMultiProcessing().getData().getMultipleProcessingType()!=null &&
            		"bulk".equals(node.getData().getAdvanced().getOfficial().getMultiProcessing().getData().getMultipleProcessingType().getValue());

			
			ActivityMapDTO.ActivityData fsActivity = new ActivityMapDTO.ActivityData();

			if(bulkEnabled) {
				fsActivity.setActivityType("BULKFS");
				fsActivity.setActivityName(getActivityName("BULKFS"));
			}else {
				fsActivity.setActivityType("FS");
				fsActivity.setActivityName(getActivityName("FS"));
			}

			activityDataList.add(fsActivity);

			for (NotificationConfiguration notification : formSubmissionNotifications) {

				addNotificationActivity(activityDataList, notification);
			}

			/*
			 * Document Generation
			 */
			List<DocMappingDTO> documentMappings = node.getData().getDocumentMapping();

			if (documentMappings != null && !documentMappings.isEmpty()) {

				ActivityMapDTO.ActivityData data = new ActivityMapDTO.ActivityData();

				data.setActivityType("DG");
				data.setActivityName(getActivityName("DG"));
				data.setUserSubmissionRequired(requiresDocumentSubmission(node, documentMappings));

				activityDataList.add(data);
			}

            if (node.getData().getPayment() != null && node.getData().getPayment().getEnabled()) {

                ActivityMapDTO.ActivityData paymentActivity = new ActivityMapDTO.ActivityData();

                paymentActivity.setActivityType("PAY");
                paymentActivity.setActivityName(getActivityName("PAY"));
                activityDataList.add(paymentActivity);
            }


			for (NotificationConfiguration notification : taskCompletionNotifications) {
				addNotificationActivity(activityDataList, notification);
			}

			for (int i = 0; i < activityDataList.size(); i++) {

				ActivityMapDTO.ActivityData data = activityDataList.get(i);

				data.setIndex(i);
				data.setIsLast(i == activityDataList.size() - 1);
			}

			ActivityMapDTO dto = new ActivityMapDTO();
			dto.setTaskId(node.getId());
			dto.setData(activityDataList);

			activityFlowList.add(dto);
		}

		return activityFlowList;
	}
	private String getActivityName(String code) {

        return switch (code) {
            case "PDE" -> "Predefined Enclosure";
            case "FS"  -> "Form Submit";
            case "DG"  -> "Document Generation";
            case "ES"  -> "Enclosure Submit";
            case "NG"  -> "Notification Genration";
            case "BULKFS" -> "Bulk Form Submit";
            case "PAY" -> "Payment";
            default    -> "Unknown";
        };
    }
	
	private void addNotificationActivity(List<ActivityMapDTO.ActivityData> activityDataList,
			NotificationConfiguration notification) {

		ActivityMapDTO.ActivityData data = new ActivityMapDTO.ActivityData();

		data.setActivityType("NG");
		data.setActivityName(getActivityName("NG"));
		data.setActivityConfigId(notification.getNotificationId());
		data.setUserSubmissionRequired(false);

		activityDataList.add(data);
	}

    private boolean requiresDocumentSubmission(NodeDTO node,List<DocMappingDTO> mappings) {

        if (ApplicationConstants.SUBMISSION_NODE_TYPE.equals(node.getType())) {
            return false;
        }

        if (node.getData().getTaskType() == null || node.getData().getHumanTask() == null) {
            return false;
        }

        if ((Integer) node.getData().getTaskType().getValue() != 1) {
            return false;
        }

        return (Integer) node.getData().getHumanTask().getValue() == 1;
    }

    private List<EscalationDetailsDTO> buildEscalationDetails(List<NodeDTO> nodes) {

        List<EscalationDetailsDTO> escalationDetails = new ArrayList<>();

        for (NodeDTO node : nodes) {

            if (node.getData() == null) {
                continue;
            }

            TaskType taskType = TaskType.resolve(node.getData());

            if (taskType != TaskType.OFFICIAL_TASK
                    && taskType != TaskType.WEB_SERVICE_TASK
                    && taskType != APPLICANT_TASK) {
                continue;
            }

            Escalation escalation = node.getData().getEscalation();
			if (escalation != null &&  escalation.getEnabled() != null && escalation.getEnabled()) {
				EscalationDetailsDTO dto = new EscalationDetailsDTO();
				dto.setTaskId(node.getId());
				dto.setTaskName(node.getData().getName());

				dto.setSlaPeriod(escalation.getSlaPeriod());
				dto.setEscalationPeriod(escalation.getEscalationPeriod());
				dto.setMvelExpression(escalation.getMvelExpression());

				if (escalation.getDefaultActions() != null) {
					dto.setDefaultActions(escalation.getDefaultActions().stream().map(LabelValue::getValue)
							.map(String::valueOf).toList());
				}

				escalationDetails.add(dto);
			}
        }

        return escalationDetails;
    }
    
    private List<TimerTaskDTO> buildTimerTaskDTO(List<NodeDTO> nodes) {

        List<TimerTaskDTO> timerTaskList = new ArrayList<>();

        
        if (nodes == null || nodes.isEmpty()) {
            return timerTaskList;
        }

        for (NodeDTO nodeDTO : nodes) {
        	
        	if (!ApplicationConstants.TASK.equalsIgnoreCase(nodeDTO.getType())) {
	            continue;
	        }
        	
            if (nodeDTO.getData() == null
                    || nodeDTO.getData().getAdvanced() == null
                    || nodeDTO.getData().getAdvanced().getTimer() == null) {
                continue;
            }
            TaskType taskType = TaskType.resolve(nodeDTO.getData());

            if (taskType != TaskType.TIMER_TASK) {
                continue;
            }
            Timer timer = nodeDTO.getData().getAdvanced().getTimer();

            if (timer.getTaskExecustionPeriod() == null
                    || timer.getTaskExecustionPeriod().getBehaviour() == null) {
                continue;
            }

            TimerTaskDTO dto = new TimerTaskDTO();

            dto.setTaskId(nodeDTO.getId());

            Integer behaviour = (Integer)timer.getTaskExecustionPeriod()
                                     .getBehaviour()
                                     .getValue();

            dto.setBehaviour(behaviour);

            // Static Behaviour
            if (Integer.valueOf(2).equals(behaviour)
                    && timer.getTaskExecustionPeriod().getStaticValue() != null) {

                Static staticValue = timer.getTaskExecustionPeriod().getStaticValue();

                dto.setExecutionPeriod(staticValue.getTaskExecutionPeriod());

                if (staticValue.getUnit() != null) {
                    dto.setExecutionPeriodUnit(staticValue.getUnit().getLabel());
                }
            }

            timerTaskList.add(dto);
        }

        return timerTaskList;
    }

	private List<NotificationDetails> buildNotificationDetails(
			List<NotificationConfiguration> notificationConfigurations) {
		List<NotificationDetails> notificationDetails = new ArrayList<>();
		NotificationConfigEvent event = new NotificationConfigEvent();
		Map<Long, NotificationTempleConfig> notificationTempleConfigMap = new HashMap<Long, NotificationConfigEvent.NotificationTempleConfig>();
		Map<Long, NotificationProviderConfig> notificationProviderMap = new HashMap<Long, NotificationConfigEvent.NotificationProviderConfig>();

		for (NotificationConfiguration notifConfig : notificationConfigurations) {
			NotificationDetails detail = new NotificationDetails();
			detail.setNotificationId(notifConfig.getNotificationId());
			detail.setTriggerPointId(notifConfig.getTriggerPointId());
			detail.setTaskId(notifConfig.getTaskId());
			notificationDetails.add(detail);

			NotificationTempleConfig notificationTempleConfig = new NotificationTempleConfig();
			notificationTempleConfig.setTaskId(notifConfig.getTaskId());
			notificationTempleConfig.setNotificationId(notifConfig.getNotificationId());
			notificationTempleConfig.setChannel(NotificationType.fromValue(notifConfig.getChannel()));

			NotificationProviderConfig notificationProviderConfig = new NotificationProviderConfig();
			notificationProviderConfig.setType(NotificationType.fromValue(notifConfig.getChannel()));

			Long notificationProviderId = 0L;
			switch (NotificationType.fromValue(notifConfig.getChannel())) {
			case EMAIL:
				notificationTempleConfig.setEmailConfig(notifConfig.getConfigJson().getEmailConfig());
				break;
			case SMS:
				notificationTempleConfig.setSmsConfig(notifConfig.getConfigJson().getSmsConfig());
				if (notifConfig.getConfigJson().getSmsConfig() != null) {
					SmsConfig smsConfig = notifConfig.getConfigJson().getSmsConfig();
					notificationProviderId = smsConfig.getSmsConfigId();
				}
				break;
			case SANDES:
				notificationTempleConfig.setSandesConfig(notifConfig.getConfigJson().getSandesConfig());
				if (notifConfig.getConfigJson().getSandesConfig() != null) {
					SandesConfig sandesConfig = notifConfig.getConfigJson().getSandesConfig();
					notificationProviderId = sandesConfig.getSandesConfigId();
				}
				break;
			case WHATSAPP:
				notificationTempleConfig.setWhatsappConfig(notifConfig.getConfigJson().getWhatsappConfig());
				if (notifConfig.getConfigJson().getWhatsappConfig() != null) {
					WhatsappConfig whatsappConfig = notifConfig.getConfigJson().getWhatsappConfig();
					notificationProviderId = whatsappConfig.getWhatsappConfigId();

				}
				break;
			}
			notificationTempleConfigMap.put(notificationTempleConfig.getNotificationId(), notificationTempleConfig);
			if (!Objects.equals(notificationProviderId, 0L) && !isNull(notificationProviderId)) {
				if (!notificationProviderMap.containsKey(notificationProviderId)) {
					com.serviceplus.metadata.notification.entity.NotificationProviderConfig providerConfig = notificationProviderConfigRepository
							.findById(notificationProviderId).orElse(null);
					if (providerConfig != null) {
						notificationProviderConfig.setId(providerConfig.getId());
						notificationProviderConfig.setType(NotificationType.fromValue(providerConfig.getType()));
						notificationProviderConfig.setConfigDTO(providerConfig.getConfigDTO());
						notificationProviderMap.put(notificationProviderId, notificationProviderConfig);
					}
				}
			}
		}

		if (!notificationConfigurations.isEmpty()) {
			event.setServiceId(notificationConfigurations.get(0).getServiceId());
		}

		event.setEventType("Freeze");
		event.setNotificationProviderConfigMap(notificationProviderMap);
		event.setNotificationTempleConfigMap(notificationTempleConfigMap);

		notificationKafkaProducer.publishFreezeEvent(event);
		return notificationDetails;
	}

	private void buildInboxSentboxFilters(List<InboxSentBoxFilter> inboxSentBoxFilters, Set<String> usedAttributeIds) {

		for (InboxSentBoxFilter inboxSentBoxFilter : inboxSentBoxFilters) {

			FilterJSONDTO filterJSON = inboxSentBoxFilter.getFilterJSON();
			if (filterJSON == null) {
				continue;
			}

			List<LabelValue> tasks = inboxSentBoxFilter.getTasks();

			if (tasks == null || tasks.isEmpty()) {
				continue;
			}
			addUsedAttributeIds(filterJSON.getFilterInput(), usedAttributeIds);
			addUsedAttributeIds(filterJSON.getFilterOutput(), usedAttributeIds);

			for (LabelValue task : tasks) {

				if (task == null || task.getValue() == null) {
					continue;
				}

				String taskId = task.getValue().toString();

			}
		}
	}
	
	private void addUsedAttributeIds(List<FilterAttributeDTO> filterAttributes, Set<String> usedAttributeIds) {

		if (filterAttributes == null) {
			return;
		}
		for (FilterAttributeDTO attribute : filterAttributes) {
			if (attribute != null && !Boolean.TRUE.equals(attribute.getIsSystemVar()) && attribute.getId() != null) {
				usedAttributeIds.add(attribute.getId());
			}
		}
	}
	
	private FilterKafkaMessage buildInboxSentBoxFilterConfigKafkaMessage(List<InboxSentBoxFilter> inboxSentBoxFilters) {

		FilterKafkaMessage kafkaMessage = new FilterKafkaMessage();

		List<FilterKafkaDTO> configurations = new ArrayList<>();

		for (InboxSentBoxFilter filter : inboxSentBoxFilters) {

			if (filter == null) {
				continue;
			}

			if (filter.getTasks() == null || filter.getTasks().isEmpty()) {
				continue;
			}

			for (LabelValue task : filter.getTasks()) {

				if (task == null || task.getValue() == null) {
					continue;
				}

				String taskId = String.valueOf(task.getValue());

				if (filter.getFilterJSON() != null && filter.getFilterJSON().getFilterInput() != null) {

					for (FilterAttributeDTO attribute : filter.getFilterJSON().getFilterInput()) {

						if (attribute == null || attribute.getId() == null) {
							continue;
						}

						FilterKafkaDTO dto = buildKafkaDTO(filter, taskId, attribute, 'I');

						configurations.add(dto);
					}
				}
				if (filter.getFilterJSON() != null && filter.getFilterJSON().getFilterOutput() != null) {

					for (FilterAttributeDTO attribute : filter.getFilterJSON().getFilterOutput()) {

						if (attribute == null || attribute.getId() == null) {
							continue;
						}

						FilterKafkaDTO dto = buildKafkaDTO(filter, taskId, attribute, 'O');

						configurations.add(dto);
					}
				}
			}
		}

		kafkaMessage.setConfigurations(configurations);

		return kafkaMessage;
	}

	private FilterKafkaDTO buildKafkaDTO(InboxSentBoxFilter filter, String taskId, FilterAttributeDTO attribute,
			Character attrType) {

		FilterKafkaDTO dto = new FilterKafkaDTO();

		dto.setFilterId(filter.getId());
		dto.setBaseServiceId(filter.getBaseServiceId());
		dto.setServiceId(filter.getServiceId());
		dto.setTaskId(taskId);
		dto.setFilterType(filter.getFilterType());
		dto.setTenantId(filter.getTenantId());

		dto.setAttrType(attrType);
		dto.setAttrId(attribute.getId());
		dto.setAttrLabel(attribute.getAttrLabel());
		dto.setAttrFormId(attribute.getFormId());
		dto.setAttrOrder(attribute.getSortOrder());

		dto.setFilterCondition(filter.getCondition());

		return dto;
	}
	
	private Set<String> getAttributeUsedInNotification(List<NotificationConfiguration> notificationConfigurations) {
		Set<String> notificationUsedAttrSet = new HashSet<>();
		for (NotificationConfiguration notifConfig : notificationConfigurations) {
			if (notifConfig == null || notifConfig.getConfigJson() == null) {
				continue;
			}
			Set<String> usedAttr = null;
			switch (NotificationType.fromValue(notifConfig.getChannel())) {
			case EMAIL:
				if (notifConfig.getConfigJson().getEmailConfig() != null) {
					usedAttr = notifConfig.getConfigJson().getEmailConfig().getUsedAttr();
				}
				break;
			case SMS:
				if (notifConfig.getConfigJson().getSmsConfig() != null) {
					usedAttr = notifConfig.getConfigJson().getSmsConfig().getUsedAttr();
				}
				break;
			case SANDES:
				if (notifConfig.getConfigJson().getSandesConfig() != null) {
					usedAttr = notifConfig.getConfigJson().getSandesConfig().getUsedAttr();
				}
				break;
			case WHATSAPP:
				if (notifConfig.getConfigJson().getWhatsappConfig() != null) {
					usedAttr = notifConfig.getConfigJson().getWhatsappConfig().getUsedAttr();
				}
				break;
			}
			if (usedAttr != null) {
				notificationUsedAttrSet.addAll(usedAttr);
			}
		}
		return notificationUsedAttrSet;
	}

	private void updateMetadataJson(ServiceMetadata metadata, ServiceDefinition serviceDefinition, NodeDTO submissionNode,
            ServiceProcessFlowDTO processFlowDTO, Map<String, String> taskFormMapping, List<ActivityMapDTO> activityFlowMap,
            List<OfficeDetailsDTO> officeDetails, Set<String> documentUsedAttrSet, List<WorkFlowDataDTO> workFlowDetails,
            List<MvelDetailsDTO> mvelDetailsDTO, Set<String> mvelUsedAttrSet, List<ExternalAttributeMappingDTO> externalAttributeMappingDTO,
            List<OutputFormatFreezeDTO> outputFormatDetail, List<WebServiceTaskDTO> webServiceTasks, List<EscalationDetailsDTO> escalationDetails, List<TimerTaskDTO> timerTaskDetails, List<DocumentGenerationDetails> documentGenerationDetails,
            List<ServiceDeliveryUnitMappingDTO> serviceDeliveryUnitMappingWithUser, List<NotificationDetails> notificationDetails, Set<String> notificationUsedAttrSet,
            Set<String> inboxSentboxFiltersUsedAttrList,List<AuaApiConfigurationDTO> auaApiConfigurations,Map<String, ApplicationRouting> applicationRoutingMap,
                                    Set<String> correlationAttributeIds, PaymentConfigurationDTO paymentConfiguration, UsedAttributeListDTO usedAttrList, ServiceEsignConfigurationDTO esignConfiguration,
                                    BsaServiceMappingDTO bsaServiceMapping, List<PatternConfig> patternConfigurations) {

        ServiceJSONDTO serviceJson = metadata.getMetadataJson();
        if (serviceJson == null) {
            serviceJson = new ServiceJSONDTO();
        }

        serviceJson.setServiceId(serviceDefinition.getServiceId());
        serviceJson.setServiceName(serviceDefinition.getDefinitionJson().getName());
        serviceJson.setStateId(serviceDefinition.getStateId());
        serviceJson.setDepartmentId(serviceDefinition.getDepartmentId());
        serviceJson.setServiceAbbrevation(serviceDefinition.getDefinitionJson().getAbbreviation());
        
        ServiceDefinitionDTO.ServiceLevelDTO serviceLevel = serviceDefinition.getDefinitionJson().getServiceLevel();

        if (serviceLevel == null || serviceLevel.getType() == null|| serviceLevel.getType().getValue() == null) {

            // No Service Level
            serviceJson.setSla(null);
            serviceJson.setSlaUnit(null);

        } else if ("sameDay".equalsIgnoreCase(serviceLevel.getType().getValue())) {
            // Same Day
            serviceJson.setSla(0);
            serviceJson.setSlaUnit(null);

        } else {
            // Custom
            if (serviceLevel.getQuantity() != null && !serviceLevel.getQuantity().isBlank()) {
                serviceJson.setSla(Integer.parseInt(serviceLevel.getQuantity().trim()));

            } else {
                serviceJson.setSla(null);
            }
            if (serviceLevel.getUnit() != null) {
                serviceJson.setSlaUnit(serviceLevel.getUnit().getValue());

            } else {
                serviceJson.setSlaUnit(null);
            }
        }

        serviceJson.setSubmissionModes(submissionNode.getData().getMode());
        if (submissionNode.getData() != null) {
            serviceJson.setSubmissionKioskTypes(submissionNode.getData().getKioskType());
            serviceJson.setKioskType(submissionNode.getData().getKioskType());
            serviceJson.setSubmissionIdentifiedUserSetIds(submissionNode.getData().getIdentifiedUserSetIds());
        }
        serviceJson.setPredefinedEnclosures(submissionNode.getData().getPredefined());
        serviceJson.setEnclosures(submissionNode.getData().getEnclosures());
        serviceJson.setSubmissionLimitData(submissionNode.getData().getLimitSubmission());
        serviceJson.setApplFormId(submissionNode.getData().getFormDetail().getFormId());
        serviceJson.setApplSubmissionTaskId(submissionNode.getId());

        serviceJson.setProcessFlowMap(processFlowDTO);
        serviceJson.setTaskFormMapping(taskFormMapping);
        serviceJson.setActivityMap(activityFlowMap);
        serviceJson.setDocumentGenerationDetails(documentGenerationDetails);
        serviceJson.setOfficeDetails(officeDetails);
        serviceJson.setDocumentUsedAttrList(documentUsedAttrSet);
        serviceJson.setMvelUsedAttrList(mvelUsedAttrSet);
        serviceJson.setWorkFlowDetails(workFlowDetails);
        serviceJson.setMvelDetailsDTO(mvelDetailsDTO);
        serviceJson.setExternalAttributeMapping(externalAttributeMappingDTO);
        serviceJson.setOutputFormatDetails(outputFormatDetail);
        serviceJson.setWebServiceTasks(webServiceTasks);
        serviceJson.setEscalationDetails(escalationDetails);
        serviceJson.setTimerTaskDetails(timerTaskDetails);
        serviceJson.setServiceDeliveryUnitMappingDetails(serviceDeliveryUnitMappingWithUser);
        serviceJson.setNotificationDetails(notificationDetails);
        serviceJson.setNotificationUsedAttrList(notificationUsedAttrSet);
        serviceJson.setInboxSentboxFiltersUsedAttrList(inboxSentboxFiltersUsedAttrList);
        serviceJson.setAuaApiConfigurations(auaApiConfigurations);
        serviceJson.setApplicationRoutingMap(applicationRoutingMap);
        serviceJson.setCorrelationAttributeIds(correlationAttributeIds);
        serviceJson.setPaymentConfiguration(paymentConfiguration);
        serviceJson.setUsedAttributeList(usedAttrList);
        serviceJson.setEsignConfiguration(esignConfiguration);
        serviceJson.setBsaServiceMapping(bsaServiceMapping);
        serviceJson.setPaymentConfiguration(paymentConfiguration);
        metadata.setMetadataJson(serviceJson);
        saveWorkflowAssignments(metadata);
        saveServiceDeliveryUnits(metadata);

    }
	private ServiceEsignConfigurationDTO buildServiceEsignConfigurationDTO(
	        Integer serviceId, String tenantId) {

	    if (serviceEsignRepository == null) {
	        return null;
	    }

	    Optional<ServiceEsignConfiguration> optConfig =
	            serviceEsignRepository.findByServiceIdAndTenantId(serviceId, tenantId);

	    if (optConfig.isEmpty()) {
	        optConfig = serviceEsignRepository.findByServiceId(serviceId);
	    }

	    if (optConfig.isEmpty()
	            || !Boolean.TRUE.equals(optConfig.get().getEsignEnabled())) {
	        return null;
	    }

	    ServiceEsignConfiguration config = optConfig.get();

	    ServiceEsignConfigurationDTO dto = new ServiceEsignConfigurationDTO();

	    dto.setId(config.getId());
	    dto.setServiceId(config.getServiceId());
	    dto.setEsignEnabled(config.getEsignEnabled());
	    dto.setEsignTemplateId(config.getEsignTemplateId());

	    dto.setPrintSignerName(config.getPrintSignerName());
	    dto.setBiometricEnabled(config.getBiometricEnabled());

	    dto.setSubmissionModes(config.getSubmissionModes());
	    dto.setIdentifiedUserGroupTypes(config.getIdentifiedUserGroupTypes());
	    dto.setBiometricTypes(config.getBiometricTypes());

	    dto.setShareAadhaar(config.getShareAadhaar());

	    dto.setTaskId(config.getTaskId());
	    dto.setAttributeId(config.getAttributeId());

	    dto.setVerifySignerName(config.getVerifySignerName());

	    return dto;
	}

    private BsaServiceMappingDTO buildBsaServiceMappingDTO(Integer serviceId, String tenantId) {
        if (bsaServiceMappingService == null) {
            return null;
        }
        try {
            return bsaServiceMappingService.buildBsaServiceMappingDTO(serviceId, tenantId);
        } catch (Exception e) {
            metadataCreationLogger.error("Error building BSA service mapping DTO for serviceId: {}, tenantId: {}", serviceId, tenantId, e);
            return null;
        }
    }
    private List<WebServiceTaskDTO> buildWebServiceTasks(List<NodeDTO> nodes) {

	    List<WebServiceTaskDTO> taskList = new ArrayList<>();

	    for (NodeDTO node : nodes) {

	        if (!ApplicationConstants.TASK.equalsIgnoreCase(node.getType())) {
	            continue;
	        }
	        if (node.getData() == null) {
	            continue;
	        }
	        if (node.getData().getWebserviceDetails() == null) {
	            continue;
	        }
	        WebServiceTaskDTO dto = new WebServiceTaskDTO();
	        dto.setTaskId(node.getId());
	        dto.setTaskName(node.getData().getName());
	        dto.setWebserviceDetails(node.getData().getWebserviceDetails());

	        taskList.add(dto);
	    }

	    return taskList;
	}

    public void saveWorkflowAssignments(ServiceMetadata serviceMetadata) {

        List<OfficeDetailsDTO> officeDetails = serviceMetadata.getMetadataJson().getOfficeDetails();
        List<LabelValue> submissionModes = serviceMetadata.getMetadataJson().getSubmissionModes();
        
        Set<Integer> submissionModeSet = submissionModes == null
                ? Collections.emptySet()
                : submissionModes.stream()
                        .map(mode -> Integer.valueOf(mode.getValue().toString()))
                        .collect(Collectors.toSet());

        boolean skipApplicantTask = !submissionModeSet.isEmpty()
                && submissionModeSet.stream().allMatch(mode ->
                        ApplicationConstants.SUBMISSION_MODE_APPLICANT.equals(mode)
                                || ApplicationConstants.SUBMISSION_MODE_CLOSED_USER_GROUP.equals(mode));
        
        if (officeDetails == null || officeDetails.isEmpty()) {
            return;
        }

        Date now = new Date();

        List<WorkflowAssignment> assignments = new ArrayList<>();

        for (OfficeDetailsDTO officeDetail : officeDetails) {
        	if (skipApplicantTask && ApplicationConstants.SUBMISSION_NODE_ID.equals(officeDetail.getTaskId())) {
                continue;
            }
            List<OfficeDetailsDTO.OfficeUnitData> allowedOffices = mapper.convertValue(officeDetail.getAllowedOffices(),new TypeReference<List<OfficeDetailsDTO.OfficeUnitData>>() {});
            if(allowedOffices==null) {
                continue;
            }
            
            for (OfficeDetailsDTO.OfficeUnitData office : allowedOffices) {
                List<WorkflowAssignment> assignmentList = workFlowAssignmentRepository.findByServiceIdAndTaskIdAndLocationId(serviceMetadata.getServiceId(), officeDetail.getTaskId(),
                        office.getOrgUnitCode().toString());
                if (assignmentList.isEmpty()) {
                    WorkflowAssignment assignment = new WorkflowAssignment();
                    assignment.setHolderId(ApplicationConstants.WORKFLOW_ASSIGNMENT_TOKEN_PREFIX + createUniqueId());
                    assignment.setCrtOn(now);
                    assignment.setAssignedBy(0l);
                    assignment.setBaseServiceId(serviceMetadata.getBaseServiceId());
                    assignment.setServiceId(serviceMetadata.getServiceId());
                    assignment.setTaskId(officeDetail.getTaskId());
                    assignment.setLocationId(office.getOrgUnitCode() != null ? office.getOrgUnitCode().toString() : null);
                    assignment.setVersionNo(serviceMetadata.getVersionNo());
                    assignment.setTenantId(serviceMetadata.getTenantId());
                    assignments.add(assignment);
                }
            }
        }
        List<WorkflowAssignment> saveAll = workFlowAssignmentRepository.saveAll(assignments);
    }
    
	private void saveServiceDeliveryUnits(ServiceMetadata serviceMetadata) {
		List<ServiceDeliveryUnitMappingDTO> mappings = serviceMetadata.getMetadataJson()
				.getServiceDeliveryUnitMappingDetails();
		if (mappings == null || mappings.isEmpty()) {
			return;
		}
		Date now = new Date();
		List<ServiceDeliveryUnits> unitsToSave = new ArrayList<>();
		for (ServiceDeliveryUnitMappingDTO mapping : mappings) {
			if (mapping.getOfficeUnits() == null || mapping.getOfficeUnits().isEmpty()) {
				continue;
			}
			for (OfficeDetailsDTO.OfficeUnitData office : mapping.getOfficeUnits()) {
				Optional<ServiceDeliveryUnits> existing = serviceDeliveryUnitsRepository
						.findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
								serviceMetadata.getTenantId(), serviceMetadata.getServiceId(),
								mapping.getDeliveryUnitLevelId(), office.getOrgUnitCode());
				if (existing.isPresent()) {
					continue;
				}
				ServiceDeliveryUnits entity = new ServiceDeliveryUnits();
				entity.setTenantId(serviceMetadata.getTenantId());
				entity.setServiceId(serviceMetadata.getServiceId());
				entity.setBaseServiceId(serviceMetadata.getBaseServiceId());
				entity.setDeliveryUnitLevelId(mapping.getDeliveryUnitLevelId());
				entity.setLocationId(office.getOrgUnitCode());
				entity.setLocationName(office.getOrgUnitName());
				entity.setActivationStatus(0);
				entity.setCreatedBy(0L);
				entity.setCreatedOn(now);
				entity.setDeletedFlag(false);
				unitsToSave.add(entity);
			}
		}
		if (!unitsToSave.isEmpty()) {
			serviceDeliveryUnitsRepository.saveAll(unitsToSave);
		}
	}
    
    public ResponseEntity<?> serviceParameterList(Integer serviceId, Long moduleId, UserSessionDTO userSessionDetails) {

        JSONObject responseJson = new JSONObject();

        ServiceParameterDetailsDTO responseDTO = new ServiceParameterDetailsDTO();

        List<ServiceParameterDetailsDTO.TemplateDetails> templateList = new ArrayList<>();
        List<ServiceParameterDetailsDTO.TaskDetails> taskList = new ArrayList<>();
        List<ServiceParameterDetailsDTO.EnclosureDetails> enclosureDetails = new ArrayList<>();
        List<ServiceParameterDetailsDTO.SystemAttribute> systemAttributeDetails = new ArrayList<>();

        try {
            responseDTO.setServiceId(serviceId);
            ServiceProcessFlow serviceProcessFlow =processFlowRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
            if (serviceProcessFlow != null && serviceProcessFlow.getProcessFlowJson() != null) {
                List<NodeDTO> nodes =serviceProcessFlow.getProcessFlowJson().getNodes();
                for (NodeDTO node : nodes) {
                    TaskDetails task = new TaskDetails();
                    List<Enclosure> enclosureList=node.getData()!=null && node.getData().getEnclosures()!=null? new ArrayList(node.getData().getEnclosures()):new ArrayList<>();
                    task.setTaskId(node.getId());
                    task.setTaskName(node.getData() != null ? node.getData().getName() : "");
                    task.setType(ApplicationConstants.TASK);
                    taskList.add(task);
                    if(!enclosureList.isEmpty()) {
                        for(Enclosure enclosure:enclosureList) {
                            ServiceParameterDetailsDTO.EnclosureDetails enclosureDetail = new ServiceParameterDetailsDTO.EnclosureDetails();
                            enclosureDetail.setEnclosureId(node.getId()+"_"+enclosure.getEnclosureType().getValue());
                            enclosureDetail.setEnclosureLabel(enclosure.getEnclosureType().getLabel());
                            enclosureDetail.setType(ApplicationConstants.ENCLOSURE);
                            enclosureDetails.add(enclosureDetail);
                        }
                    }

                }
            }
            responseDTO.setTaskDetails(taskList);
            responseDTO.setEnclosureDetails(enclosureDetails);
            List<SystemVariableMaster> systemVariableList=systemVariableRepository.findByModuleIdOrModuleIdIsNull(moduleId);
            for(SystemVariableMaster systemVariable:systemVariableList) {
                ServiceParameterDetailsDTO.SystemAttribute sysAttrDetail = new ServiceParameterDetailsDTO.SystemAttribute();
                sysAttrDetail.setAttributeId(systemVariable.getAttributeId());
                sysAttrDetail.setAttributeLabel(systemVariable.getAttributeLabel());
                sysAttrDetail.setType(ApplicationConstants.SYSTEM_ATTRIBUTE);
                systemAttributeDetails.add(sysAttrDetail);
            }
            responseDTO.setSystemAttributes(systemAttributeDetails);
            List<ServiceTemplate> serviceTemplateList =serviceTemplateRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
            for (ServiceTemplate serviceTemplate : serviceTemplateList) {

                ServiceParameterDetailsDTO.TemplateDetails templateDTO =new ServiceParameterDetailsDTO.TemplateDetails();

                templateDTO.setTemplateName(serviceTemplate.getTemplateName());
                templateDTO.setTaskId(serviceTemplate.getTaskId());
                templateDTO.setFormId(serviceTemplate.getFormId());
                templateDTO.setHolderId(serviceTemplate.getHolderId());
                try {
	                ResponseEntity<?> formDtoResponse =formFeignClient.getAllAttributesByFormId(serviceTemplate.getFormId());
	
	                if (formDtoResponse.getStatusCode().equals(HttpStatus.OK)) {
	                    Map<Object, Object> responseObj = (Map<Object, Object>) formDtoResponse.getBody();
	                    JSONObject json = new JSONObject(responseObj);
	                    FormDTO formDto = new Gson().fromJson(json.toString(), FormDTO.class);
	                    List<Attribute> attrList = formDto.getAttributes();
	                    List<ServiceParameterDetailsDTO.Attribute> attributeDTOList = new ArrayList<>();
	                    for (Attribute attr : attrList) {
	                        ServiceParameterDetailsDTO.Attribute attributeDTO =new ServiceParameterDetailsDTO.Attribute();
	
	                        String attrId = createAttributeId(serviceTemplate.getTaskId(),serviceTemplate.getHolderId(),attr.getAttributeId());
	                        attributeDTO.setAttributeId(attrId);
	                        attributeDTO.setAttributeLabel(attr.getAttributeLabel());
	                        attributeDTO.setTypeId(attr.getTypeId());
	                        attributeDTO.setRequired(attr.getRequired());
	                        attributeDTO.setType(ApplicationConstants.ATTRIBUTE);
	                        attributeDTOList.add(attributeDTO);
	                    }
	                    templateDTO.setAttributeList(attributeDTOList);
	                }
	                templateList.add(templateDTO);
                }catch (Exception e) {
					e.printStackTrace();
				}
            }
            
            List<ServicePatternConfiguration> servicePatternConfigurations = configurationRepository.findAllByServiceIdAndTenantId(serviceId.longValue(), userSessionDetails.getTenantId());
            List<ServiceParameterDetailsDTO.PatternAttribute> patternAttributeDetails = new ArrayList<>();
            for(ServicePatternConfiguration configuration : servicePatternConfigurations) {
            	Long patternId = configuration.getConfigJson().getPatternId();
            	PatternConfiguration patternConfiguration =  patternConfigurationRepository.findById(patternId).get();
            	if(patternConfiguration != null) {
            		String patternName = patternConfiguration.getConfigJson().getPatternName();
            		 ServiceParameterDetailsDTO.PatternAttribute dto = new ServiceParameterDetailsDTO.PatternAttribute();
                     dto.setAttributeId(ApplicationConstants.PATTERN_ATTRIBUTE_DELIMITER+patternId.toString());
                     dto.setAttributeLabel(patternName);
                     dto.setType(ApplicationConstants.PATTERN_ATTRIBUTE);
                     patternAttributeDetails.add(dto);
            	}
            }
            responseDTO.setPatternAttributes(patternAttributeDetails);
            
            responseDTO.setTemplateDetails(templateList);
        } catch (Exception ex) {
            ex.printStackTrace();

            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(responseDTO);
    }
    public ResponseEntity<?> unfreeze(Integer serviceId, UserSessionDTO userSessionDetails) {
        try {
            ServiceDefinition serviceDefinition = serviceDefinitionRepository.findById(serviceId).orElseThrow(() -> new SPRuntimeError("Service not found", HttpStatus.BAD_REQUEST));
            ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
            ServiceMetadata serviceMetadata = metadataRepository.findByServiceIdAndTenantId(serviceId,userSessionDetails.getTenantId());

            if(serviceLog== null) {
                new SPRuntimeError("Service not found", HttpStatus.BAD_REQUEST);
            }

            if (!serviceLog.getTenantId().equals(userSessionDetails.getTenantId())
                    || !serviceDefinition.getDepartmentId().equals(userSessionDetails.getEntityId().longValue())
                    || !serviceDefinition.getUserId().equals(userSessionDetails.getUserID().longValue())) {
                new SPRuntimeError("Unauthorized access", HttpStatus.BAD_REQUEST);
            }

            if(serviceLog.getServiceStatus().equals(ServiceStatus.LAUNCHED.getCode())){
                new SPRuntimeError("Service is in online mode. You can't unfreeze it.", HttpStatus.BAD_REQUEST);
            }

            Date date = new Date();
            serviceDefinition.setuDate(date);

            serviceLog.setServiceStatus(ServiceStatus.DEFINED.getCode());
            serviceLog.setuDate(date);
            if(!Objects.isNull(serviceMetadata)) {
                metadataRepository.delete(serviceMetadata);
            }
            String redisKey = ApplicationConstants.REDIS_KEY_OF_SERVICEMETADATA + serviceId;
            evictRedisCache(redisKey);
            serviceDefinitionRepository.save(serviceDefinition);
            serviceLogRepository.save(serviceLog);
            MvelFunctionEvent mvelFnEvent=new MvelFunctionEvent();
            mvelFnEvent.setEventType("Unfreeze");
            mvelFnEvent.setServiceId(serviceId);
            mvelKafkaProducer.publishUnfreezeEvent(mvelFnEvent);
            
            NotificationConfigEvent notifEvent = new NotificationConfigEvent();
            notifEvent.setServiceId(serviceId);
            notifEvent.setEventType("Unfreeze");
            notificationKafkaProducer.publishFreezeEvent(notifEvent);
            
            TaskPropertyDetailsEvent taskPropertyEvent = new TaskPropertyDetailsEvent();

            taskPropertyEvent.setEventType("Unfreeze");
            taskPropertyEvent.setTenantId(userSessionDetails.getTenantId());
            taskPropertyEvent.setServiceId(serviceId);
            taskPropertyEvent.setBaseServiceId(serviceId / 10000);

			kafkaProducer.sendMessage(taskPropertyTopic, String.valueOf(serviceId), taskPropertyEvent);
			esignKafkaProducer.publishUnfreezeEvent(serviceId, userSessionDetails.getTenantId());
            if (bsaKafkaProducer != null) {
                bsaKafkaProducer.publishUnfreezeEvent(serviceId, userSessionDetails.getTenantId());
            }
            
            return ResponseEntity.ok(Map.of("message", "Service unfrozen successfully"));
        } catch (Exception ex) {
            if (ex instanceof SPRuntimeError) {
                throw ex;
            } else {
                ex.printStackTrace();
                throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
    public void evictRedisCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


    public ServiceFormDefinitionDTO getServiceFormDefinition(Integer baseServiceId) {
        try {
            ServiceFormDefinitionDTO serviceFormDefinition = new ServiceFormDefinitionDTO();
            ServiceMetadata serviceMetadata = metadataRepository
                    .findTopByBaseServiceIdOrderByServiceIdDesc(baseServiceId).orElse(null);
            if (serviceMetadata == null) {
                return null;
            }
            serviceFormDefinition.setServiceId(serviceMetadata.getServiceId());
            serviceFormDefinition.setTaskId(serviceMetadata.getMetadataJson().getApplSubmissionTaskId());
            serviceFormDefinition.setFormId(serviceMetadata.getMetadataJson().getApplFormId());
            serviceFormDefinition.setFormBlankJsonDTO(new FormBlankJsonDTO());// blank json?
            return serviceFormDefinition;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public ResponseEntity<?> getUserAssignmentServiceList(UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        try {
        	List<ServiceAssignmentDTO> eligibleServices = new ArrayList<>();
			List<Integer> serviceIds = serviceLogRepository.findServiceIdByServiceStatusNotInAndTenantId(
					List.of(ServiceStatus.LAUNCHED.getCode(), ServiceStatus.DEFINED.getCode()),
					userSessionDetails.getTenantId());

			if (serviceIds == null || serviceIds.isEmpty()) {
				return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
			}

			List<ServiceMetadata> serviceList = metadataRepository.findByServiceIdIn(serviceIds);

			if (serviceList == null || serviceList.isEmpty()) {
				return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
			}
            Integer userOfficeLevelId = userSessionDetails.getEntityLevelId();
            eligibleServices = serviceList.stream()
                    .filter(service -> isServiceMappedToOfficeLevel(service, userOfficeLevelId))
                    .map(service -> new ServiceAssignmentDTO(
                            service.getServiceId(),
                            service.getMetadataJson().getServiceName()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(eligibleServices, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isServiceMappedToOfficeLevel(ServiceMetadata service, Integer officeLevelId) {
        if (service == null || service.getMetadataJson() == null|| service.getMetadataJson().getOfficeDetails() == null) {
            return false;
        }
        for (OfficeDetailsDTO officeDetail : service.getMetadataJson().getOfficeDetails()) {
            if (officeDetail.getOfficeLevelIds() != null && officeDetail.getOfficeLevelIds().contains(officeLevelId)) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<?> getUserAssignmentTaskList(Integer serviceId, Integer officeLevelId,UserSessionDTO userSessionDetails) {
        JSONObject responseJson = new JSONObject();
        try {
        	Optional<ServiceLog> serviceLog = serviceLogRepository.findByServiceIdAndServiceStatusNotInAndTenantId(serviceId,
					List.of(ServiceStatus.LAUNCHED.getCode(), ServiceStatus.DEFINED.getCode()),
					userSessionDetails.getTenantId());

			if (serviceLog.isEmpty()) {
				return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
			}
            ServiceMetadata serviceMetadata = metadataRepository.findByServiceIdAndTenantId(serviceId,userSessionDetails.getTenantId());
            if (serviceMetadata == null || serviceMetadata.getMetadataJson() == null) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }

            List<OfficeDetailsDTO> officeDetails = serviceMetadata.getMetadataJson().getOfficeDetails();
            if (officeDetails == null || officeDetails.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }
            List<LabelValue> submissionModes = serviceMetadata.getMetadataJson().getSubmissionModes();

            Set<Integer> submissionModeSet = submissionModes == null
                    ? Collections.emptySet()
                    : submissionModes.stream()
                            .map(mode -> Integer.valueOf(mode.getValue().toString()))
                            .collect(Collectors.toSet());

            boolean skipSubmissionTask = !submissionModeSet.isEmpty()
                    && submissionModeSet.stream().allMatch(mode ->
                            ApplicationConstants.SUBMISSION_MODE_APPLICANT.equals(mode)
                                    || ApplicationConstants.SUBMISSION_MODE_CLOSED_USER_GROUP.equals(mode));
            
            List<TaskAssignmentDTO> taskList = officeDetails.stream()
                    .filter(officeDetail -> officeDetail.getOfficeLevelIds() != null
                            && officeDetail.getOfficeLevelIds().contains(officeLevelId))
                    .filter(officeDetail -> !(skipSubmissionTask
                                    && ApplicationConstants.SUBMISSION_NODE_ID.equals(officeDetail.getTaskId())))
                    .map(officeDetail -> new TaskAssignmentDTO(officeDetail.getTaskId(),
                            getTaskName(serviceMetadata, officeDetail.getTaskId())))
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.toMap(TaskAssignmentDTO::getTaskId, Function.identity(),
                                            (existing, replacement) -> existing),
                                    map -> new ArrayList<>(map.values())));

            return new ResponseEntity<>(taskList, HttpStatus.OK);

        } catch (Exception ex) {

            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");

            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getTaskName(ServiceMetadata serviceMetadata, String taskId) {

        ServiceProcessFlowDTO processFlow = serviceMetadata.getMetadataJson().getProcessFlowMap();

        if (processFlow == null || processFlow.getData() == null) {
            return taskId;
        }

        return processFlow.getData().stream()
                .filter(data -> data.getNode() != null && taskId.equals(data.getNode().getId()))
                .map(data -> data.getNode().getName()).findFirst().orElse(taskId);
    }

    public ResponseEntity<?> getOutputFormatDetails(Integer serviceId, Long outputFormatId) {

        JSONObject responseJson = new JSONObject();

        try {

            Optional<ServiceMetadata> serviceMetadataOpt = metadataRepository.findById(serviceId);

            if (serviceMetadataOpt.isEmpty()) {

                responseJson.put("errorCode", "404");
                responseJson.put("errorMessage", "Service metadata not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseJson.toString());
            }

            ServiceMetadata serviceMetadata = serviceMetadataOpt.get();

            List<OutputFormatFreezeDTO> outputFormatDetails = serviceMetadata.getMetadataJson().getOutputFormatDetails();

            if (outputFormatDetails == null || outputFormatDetails.isEmpty()) {

                responseJson.put("errorCode", "404");
                responseJson.put("errorMessage", "No output format details found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseJson.toString());
            }

            OutputFormatFreezeDTO outputFormat = outputFormatDetails.stream().filter(item -> outputFormatId.equals(item.getId())).findFirst().orElse(null);

            if (outputFormat == null) {

                responseJson.put("errorCode", "404");
                responseJson.put("errorMessage", "Output format not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseJson.toString());
            }

            return ResponseEntity.ok(outputFormat);

        } catch (Exception ex) {
            ex.printStackTrace();
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseJson.toString());
        }
    }
    
	public ResponseEntity<?> getServiceDeliveryUnitList(Integer serviceId, Integer officeLevelId,
			UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		try {
			Optional<ServiceLog> serviceLog = serviceLogRepository.findByServiceIdAndServiceStatusNotInAndTenantId(serviceId,
					List.of(ServiceStatus.LAUNCHED.getCode(), ServiceStatus.DEFINED.getCode()),
					userSessionDetails.getTenantId());

			if (serviceLog.isEmpty()) {
				return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
			}
			ServiceMetadata serviceMetadata = metadataRepository.findByServiceIdAndTenantId(serviceId,
					userSessionDetails.getTenantId());
			if (serviceMetadata == null || serviceMetadata.getMetadataJson() == null) {
				return ResponseEntity.ok(Collections.emptyList());
			}
			//Entity check required ??
			
			List<ServiceDeliveryUnitMappingDTO> mappings = serviceMetadata.getMetadataJson()
					.getServiceDeliveryUnitMappingDetails();
			if (mappings == null || mappings.isEmpty()) {
				return ResponseEntity.ok(Collections.emptyList());
			}
			List<ServiceDeliveryUnitResponseDTO> response = mappings.stream()
					.filter(mapping -> officeLevelId.equals(mapping.getDesignatedOfficerUserLevelId())).map(mapping -> {
						ServiceDeliveryUnitResponseDTO dto = new ServiceDeliveryUnitResponseDTO();
						dto.setRoleId(mapping.getUserRoleId());
						dto.setUserRole(mapping.getUserRole());
						dto.setDeliveryUnitLevelId(mapping.getDeliveryUnitLevelId());
						dto.setDeliveryUnitLevelName(mapping.getDeliveryUnitLevelName());
						dto.setDesignatedOfficerUserLevelId(mapping.getDesignatedOfficerUserLevelId());
						dto.setDesignatedOfficerUserLevelName(mapping.getDesignatedOfficerUserLevelName());
						serviceDesignatedOfficerAssignmentRepository
								.findFirstByTenantIdAndServiceIdAndDeliveryUnitLevelId(userSessionDetails.getTenantId(),
										serviceId, mapping.getDeliveryUnitLevelId())
								.ifPresent(entity -> {
									dto.setAssignedUserId(entity.getUserId());
									dto.setAssignedUserName(userService.getUserName(entity.getUserId()));
									dto.setAssignedDesignationId(entity.getDesignationId());
									DesignationRequestDTO requestDto=new DesignationRequestDTO();
									requestDto.setDesignationCode(entity.getDesignationId());
									dto.setDesignationName(userService.getDesignationName(requestDto));
								});
						return dto;
					}).collect(Collectors.toList());
			return ResponseEntity.ok(response);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public ResponseEntity<?> activateService(ServiceActivationDTO request, UserSessionDTO userSessionDetail) {
		JSONObject response = new JSONObject();
		try {
			if (sduAdoptionService != null) {
				if (request.getActivationStatus() == 1) {
					return sduAdoptionService.activateSdu(
							new SduActivationRequestDTO(request.getServiceId(), request.getDeliveryUnitLevelId(), request.getLocationIds(), "Service activated"),
							userSessionDetail);
				} else {
					return sduAdoptionService.deactivateSdu(
							new SduActivationRequestDTO(request.getServiceId(), request.getDeliveryUnitLevelId(), request.getLocationIds(), "Service deactivated"), userSessionDetail);
				}
			}
			Optional<ServiceLog> serviceLog = serviceLogRepository.findByServiceIdAndServiceStatusNotInAndTenantId(request.getServiceId(),
					List.of(ServiceStatus.LAUNCHED.getCode(), ServiceStatus.DEFINED.getCode()),
					userSessionDetail.getTenantId());

			if (!serviceLog.isPresent()) {
				return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
			}
			Date now = new Date();
			for (Integer locationId : request.getLocationIds()) {
				Optional<ServiceDeliveryUnits> optional = serviceDeliveryUnitsRepository
						.findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationIdAndDeletedFlagFalse(
								userSessionDetail.getTenantId(), request.getServiceId(),
								request.getDeliveryUnitLevelId(), locationId);
				ServiceDeliveryUnits entity;
				if (optional.isPresent()) {
					entity = optional.get();
					entity.setActivationStatus(request.getActivationStatus());
					entity.setModifiedBy(userSessionDetail.getUserID().longValue());
					entity.setModifiedOn(now);
					if (request.getActivationStatus() == 1) {
						entity.setActivatedBy(userSessionDetail.getUserID().longValue());
						entity.setActivatedOn(now);
					} else {
						entity.setDeactivatedBy(userSessionDetail.getUserID().longValue());
						entity.setDeactivatedOn(now);
					}
				} else {
					entity = new ServiceDeliveryUnits();
					entity.setTenantId(userSessionDetail.getTenantId());
					entity.setServiceId(request.getServiceId());
					entity.setBaseServiceId(request.getServiceId()/10000);
					entity.setDeliveryUnitLevelId(request.getDeliveryUnitLevelId());
					entity.setLocationId(locationId);
					entity.setActivationStatus(request.getActivationStatus());
					entity.setCreatedBy(userSessionDetail.getUserID().longValue());
					entity.setCreatedOn(now);
					if (request.getActivationStatus() == 1) {
						entity.setActivatedBy(userSessionDetail.getUserID().longValue());
						entity.setActivatedOn(now);
					} else {
						entity.setDeactivatedBy(userSessionDetail.getUserID().longValue());
						entity.setDeactivatedOn(now);
					}
					entity.setDeletedFlag(false);
				}
				serviceDeliveryUnitsRepository.save(entity);
				
			}
			boolean anyActiveDeliveryUnit = serviceDeliveryUnitsRepository
					.existsByTenantIdAndServiceIdAndActivationStatusAndDeletedFlagFalse(
							userSessionDetail.getTenantId(), request.getServiceId(), 1);

			if (anyActiveDeliveryUnit) {
				serviceLog.get().setServiceStatus(ServiceStatus.ACTIVATED.getCode());
			} else {
				serviceLog.get().setServiceStatus(ServiceStatus.FROZEN.getCode());
			}
			serviceLog.get().setuDate(new Date());

	        serviceLogRepository.save(serviceLog.get());
			response.put("message", "Service activation updated successfully.");
			return ResponseEntity.ok(response.toString());
		} catch (Exception ex) {
			response.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public ResponseEntity<?> moveToProduction(Integer serviceId, UserSessionDTO userSessionDetails) {

	    try {
	    	Date currentDate = new Date();
	    	int updated = serviceLogRepository.moveToProduction(serviceId,userSessionDetails.getUserID().longValue(),currentDate,userSessionDetails.getTenantId());

	    	if (updated == 0) {
	    	    return ResponseEntity.badRequest()
	    	            .body("Service not found or not for ready to launch.");
	    	}
	        return ResponseEntity.ok("Service moved to production successfully.");

	    } catch (Exception ex) {
	    	ex.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Internal Server Error");
	    }
	}
	
	public ServiceMetadata rebuildServiceMetaData(ServiceMetadata metadata, ServiceProcessFlow processFlow) {
		ServiceJSONDTO serviceJson = metadata.getMetadataJson();

		List<NodeDTO> nodes = processFlow.getProcessFlowJson().getNodes();
		List<EdgeDTO> edges = processFlow.getProcessFlowJson().getEdges();
		NodeDTO submissionNode = getSubmissionNode(nodes);
		Map<Object, List<OfficeDetailsDTO.OfficeUnitData>> entityLevelOfficeUnitMap = new HashMap<Object, List<OfficeDetailsDTO.OfficeUnitData>>();
		List<OfficeDetailsDTO> officeDetails = buildOfficeData(nodes, entityLevelOfficeUnitMap);
		Map<String, String> taskFormMapping = buildTaskFormMapping(nodes);

		ServiceProcessFlowDTO processFlowDTO = buildProcessFlowMap(metadata.getServiceId(), nodes, edges, officeDetails, taskFormMapping,metadata.getTenantId());
		List<ServiceProcessFlowDTO.AssociatedActivity> associatedActivities = buildAssociatedActivities(nodes, processFlow.getProcessFlowJson().getAssociatedTasks(), officeDetails, entityLevelOfficeUnitMap);

		processFlowDTO.setAssociatedActivities(associatedActivities);
		serviceJson.setTaskFormMapping(taskFormMapping);
		serviceJson.setApplFormId(submissionNode.getData().getFormDetail().getFormId());
		serviceJson.setProcessFlowMap(processFlowDTO);

		return metadata;
	}

	public void rebuildServiceMetaDataForInboxSentBoxFilter(Integer serviceId, FilterJSONDTO filterJSONDTO) {

		metadataRepository.findById(serviceId).ifPresent(serviceMetadata -> {
			if(serviceMetadata.getMetadataJson().getInboxSentboxFiltersUsedAttrList() != null && !serviceMetadata.getMetadataJson().getInboxSentboxFiltersUsedAttrList().isEmpty()){
				Set<String> usedAttributeIds = serviceMetadata.getMetadataJson().getInboxSentboxFiltersUsedAttrList();
				List<FilterAttributeDTO> filterInputList = filterJSONDTO.getFilterInput();
				for(FilterAttributeDTO filterAttribute : filterInputList) {
					usedAttributeIds.remove(filterAttribute.getId());
				}
				List<FilterAttributeDTO> filterOutputList = filterJSONDTO.getFilterOutput();
				for(FilterAttributeDTO filterAttribute : filterOutputList) {
					usedAttributeIds.remove(filterAttribute.getId());
				}
				serviceMetadata.getMetadataJson().setInboxSentboxFiltersUsedAttrList(usedAttributeIds);
				metadataRepository.save(serviceMetadata);
				evictRedisCache(ApplicationConstants.REDIS_KEY_OF_SERVICEMETADATA + serviceMetadata.getServiceId());
			}

		});
	}
	public Boolean isExternalServiceMapped(Integer serviceId, String clientId) {

		ServiceMetadata serviceMetadata = metadataRepository.findById(serviceId).orElse(null);

		if (serviceMetadata == null || serviceMetadata.getMetadataJson() == null) {
			return false;
		}

		List<ExternalAttributeMappingDTO> mappings = serviceMetadata.getMetadataJson().getExternalAttributeMapping();

		if (mappings == null || mappings.isEmpty()) {
			return false;
		}

		return mappings.stream().anyMatch(mapping -> clientId.equals(mapping.getExternalSystemRegId().toString()));
	}

	public List<ExternalClientFormAttributesMappingDTO> getExternalFormAttributes(Integer serviceId) {

		ServiceMetadata metadata = metadataRepository.findById(serviceId).orElse(null);

		if (metadata == null || metadata.getMetadataJson() == null
				|| metadata.getMetadataJson().getExternalAttributeMapping() == null) {
			return Collections.emptyList();
		}

		return metadata.getMetadataJson().getExternalAttributeMapping().stream()
				.filter(mapping -> mapping.getAttrMappingDetails() != null)
				.flatMap(mapping -> mapping.getAttrMappingDetails().stream()
						.map(attr -> toExternalFormAttributesDTO(mapping, attr, serviceId)))
				.collect(Collectors.toList());
	}

	private ExternalClientFormAttributesMappingDTO toExternalFormAttributesDTO(ExternalAttributeMappingDTO mapping,
			AttrMappingDetails attr, Integer serviceId) {

		ExternalClientFormAttributesMappingDTO dto = new ExternalClientFormAttributesMappingDTO();

		dto.setClientMappingId(mapping.getExternalSystemRegId());
		dto.setNodeName(mapping.getNodeId());
		dto.setNodeReference(attr.getNodeRef());
		dto.setAttributeId(attr.getAttributeId());
		dto.setServiceId(serviceId);

		return dto;
	}

	public ResponseEntity<?> serviceRoutingParameterList(Integer serviceId, String typeIds,
			UserSessionDTO userSessionDetails) {

		JSONObject responseJson = new JSONObject();

		Optional<ServiceLog> serviceLog = serviceLogRepository.findByServiceIdAndServiceStatusAndTenantId(serviceId,
				ServiceStatus.DEFINED.getCode(), userSessionDetails.getTenantId());

		if (serviceLog.isEmpty()) {
			responseJson.put("errorCode", "400");
			responseJson.put("errorMessage", "Bad Request");

			return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}

		ServiceParameterDetailsDTO responseDTO = new ServiceParameterDetailsDTO();

		List<ServiceParameterDetailsDTO.TemplateDetails> templateList = new ArrayList<>();

		try {
			responseDTO.setServiceId(serviceId);
			List<ServiceTemplate> serviceTemplateList = serviceTemplateRepository.findByServiceIdAndUserId(serviceId,
					userSessionDetails.getUserID().longValue());			
			Set<String> formIds = new HashSet<>();
			for (ServiceTemplate serviceTemplate : serviceTemplateList) {
				if (serviceTemplate.getFormId() != null) {
					formIds.add(serviceTemplate.getFormId());
				}
			}
			Map<String, SpecificAttributesDTO> specificAttributesDTOs = new HashMap<>();

			if (!formIds.isEmpty() && typeIds != null && !typeIds.trim().isEmpty()) {
				SpecificAttributesReqDTO attributesReqDTO = new SpecificAttributesReqDTO();
				attributesReqDTO.setAttrType(typeIds);
				attributesReqDTO.setFormIds(formIds);
				ResponseEntity<?> responseEntity = formFeignClient.getSpecificAttributes(attributesReqDTO);
				if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
					Map<String, SpecificAttributesDTO> responseMap = mapper.convertValue(responseEntity.getBody(),
							new TypeReference<Map<String, SpecificAttributesDTO>>() {
							});

					specificAttributesDTOs.putAll(responseMap);
				}
			}

			for (ServiceTemplate serviceTemplate : serviceTemplateList) {

				ServiceParameterDetailsDTO.TemplateDetails templateDTO = new ServiceParameterDetailsDTO.TemplateDetails();

				String formId = serviceTemplate.getFormId();
				String taskId = serviceTemplate.getTaskId();
				String holderId = serviceTemplate.getHolderId();

				templateDTO.setTemplateName(serviceTemplate.getTemplateName());
				templateDTO.setTaskId(serviceTemplate.getTaskId());
				templateDTO.setFormId(formId);
				templateDTO.setHolderId(serviceTemplate.getHolderId());

				List<ServiceParameterDetailsDTO.Attribute> attributeDTOList = new ArrayList<>();

				SpecificAttributesDTO specificAttributesDTO = specificAttributesDTOs.get(formId);
				if (specificAttributesDTO != null && specificAttributesDTO.getAttributes() != null) {
					for (SpecificAttributesDTO.Attribute attr : specificAttributesDTO.getAttributes()) {
						ServiceParameterDetailsDTO.Attribute attributeDTO = new ServiceParameterDetailsDTO.Attribute();
						attributeDTO.setAttributeId(createAttributeId(taskId, holderId, attr.getAttributeId()));
						attributeDTO.setAttributeLabel(attr.getAttributeLabel());
						attributeDTO.setTypeId(attr.getTypeId());
						attributeDTO.setType(ApplicationConstants.ATTRIBUTE);
						attributeDTO.setHierarchyLevel(attr.getHierachyLevel());
						attributeDTOList.add(attributeDTO);
					}
				}
				templateDTO.setAttributeList(attributeDTOList);
				templateList.add(templateDTO);
			}
			responseDTO.setTemplateDetails(templateList);

		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.ok(responseDTO);
	}
	
	private List<CorrelationSetDTO> buildCorrelationDataSet(Integer serviceId, Set<String> attributeIds) {

		List<CorrelationConfiguration> configurations = correlationConfigurationRepository.findByServiceId(serviceId).orElse(new ArrayList<>());

		List<CorrelationSetDTO> correlationSetDTOs = new ArrayList<>();

		for (CorrelationConfiguration configuration : configurations) {

			if (configuration == null || configuration.getConfig() == null
					|| configuration.getConfig().getServiceIdentifierConfig() == null) {
				continue;
			}

			IdentifierConfigure identifierConfigure = configuration.getConfig().getServiceIdentifierConfig();

			CorrelationSetDTO correlationSetDTO = new CorrelationSetDTO();

			correlationSetDTO.setServiceId(
					configuration.getServiceId() != null ? configuration.getServiceId().longValue() : null);

			correlationSetDTO.setBaseServiceId(
					configuration.getBaseServiceId() != null ? configuration.getBaseServiceId().longValue() : null);

			correlationSetDTO.setSetId(configuration.getId() != null ? configuration.getId().longValue() : null);

			correlationSetDTO.setSecurityKey(configuration.getSecurityKey());
			correlationSetDTO.setTenantId(configuration.getTenantId());
			correlationSetDTO.setEntityId(configuration.getEntityId());
			correlationSetDTO.setSourceType(configuration.getSetType());
			correlationSetDTO.setTenantId(configuration.getTenantId());
			correlationSetDTO.setEntityId(configuration.getEntityId());

			if (identifierConfigure.getInputAttributes() != null) {

				List<CorrelationSetDTO.CorrelationAttributeDTO> inputAttributes = new ArrayList<>();

				for (CorrelationFieldDTO field : identifierConfigure.getInputAttributes()) {

					if (field == null) {
						continue;
					}

					CorrelationSetDTO.CorrelationAttributeDTO attribute = new CorrelationSetDTO.CorrelationAttributeDTO();

					attribute.setSortOrder(field.getSortOrder());
					attribute.setAttributeId(field.getAttributeId());
					attribute.setTaskId(field.getTaskId());
					if (field.getAttributeSource() != null) {
						attribute.setAttributeSource(field.getAttributeSource().name());
					}

					inputAttributes.add(attribute);
					if (field.getAttributeId() != null) {
	                    attributeIds.add(field.getAttributeId());
	                }
				}

				correlationSetDTO.setInputAttributes(inputAttributes);
			}

			if (identifierConfigure.getOutputAttributes() != null) {

				List<CorrelationSetDTO.OutputAttributeDTO> outputAttributes = new ArrayList<>();

				for (IdentifierConfigure.OutputAttributes output : identifierConfigure.getOutputAttributes()) {

					if (output == null) {
						continue;
					}

					CorrelationSetDTO.OutputAttributeDTO outputAttribute = new CorrelationSetDTO.OutputAttributeDTO();

					outputAttribute.setOutputSetid(output.getOutputSetid());

					List<CorrelationSetDTO.OutputAttributeItemDTO> attributes = new ArrayList<>();

					if (output.getAttributes() != null) {

						for (CorrelationFieldDTO field : output.getAttributes()) {

							if (field == null) {
								continue;
							}

							CorrelationSetDTO.OutputAttributeItemDTO attribute = new CorrelationSetDTO.OutputAttributeItemDTO();

							attribute.setAttributeId(field.getAttributeId());
							attribute.setTaskId(field.getTaskId());
							if (field.getAttributeSource() != null) {
								attribute.setAttributeSource(field.getAttributeSource().name());
							}

							attributes.add(attribute);
							if (field.getAttributeId() != null) {
			                    attributeIds.add(field.getAttributeId());
			                }
						}
					}

					outputAttribute.setAttributes(attributes);

					outputAttributes.add(outputAttribute);
				}

				correlationSetDTO.setOutputAttributes(outputAttributes);
			}

			if (identifierConfigure.getSortingAttributes() != null) {

				List<CorrelationSetDTO.SortingAttributeDTO> sortingAttributes = new ArrayList<>();

				for (CorrelationFieldDTO field : identifierConfigure.getSortingAttributes()) {

					if (field == null) {
						continue;
					}

					CorrelationSetDTO.SortingAttributeDTO sortingAttribute = new CorrelationSetDTO.SortingAttributeDTO();

					sortingAttribute.setSortOrder(field.getSortOrder());
					sortingAttribute.setAttributeId(field.getAttributeId());
					sortingAttribute.setSortSequence(field.getSortSequence());
					sortingAttributes.add(sortingAttribute);
				}

				correlationSetDTO.setSortingAttributes(sortingAttributes);
			}

			correlationSetDTOs.add(correlationSetDTO);
		}

		return correlationSetDTOs;
	}
	
	private List<ServiceCombinationConfigurationDTO> buildExternalDataSet(List<NodeDTO> nodes, Integer serviceId,
			Integer baseServiceId) {

		List<ServiceCombinationConfigurationDTO> result = new ArrayList<>();

		if (nodes == null || nodes.isEmpty()) {
			return result;
		}

		for (NodeDTO node : nodes) {

			if (node == null || node.getData() == null) {
				continue;
			}

			ExternalServiceDataConfigurationDTO externalDataSet = node.getData().getExternalDataSet();

			if (externalDataSet == null || externalDataSet.getCombonation() == null
					|| externalDataSet.getCombonation().getValue() == null) {
				continue;
			}

			Long combinationId;

			try {
				combinationId = Long.valueOf(externalDataSet.getCombonation().getValue().toString());
			} catch (NumberFormatException ex) {

				metadataCreationLogger.warn("Invalid combinationId={} for nodeId={}", externalDataSet.getCombonation().getValue(),
						node.getId());

				continue;
			}

			ExternalDataCombination externalDataCombination = combinationRepository.findById(combinationId)
					.orElse(null);

			if (externalDataCombination == null) {

				metadataCreationLogger.warn("External data combination not found for combinationId={}, nodeId={}", combinationId,
						node.getId());

				continue;
			}

			ServiceCombinationConfigurationDTO configurationDTO = new ServiceCombinationConfigurationDTO();

			configurationDTO.setServiceId(serviceId != null ? serviceId.longValue() : null);

			configurationDTO.setBaseServiceId(baseServiceId != null ? baseServiceId.longValue() : null);

			configurationDTO.setTaskId(node.getId());

			configurationDTO.setCombinationId(externalDataCombination.getId());

			configurationDTO.setSearchAfterDataFound(externalDataCombination.getSearchOnDataFound());

			configurationDTO.setTenantId(externalDataCombination.getTenantId());
			
			configurationDTO.setEntityId(externalDataCombination.getEntityId());
			
			List<CombinationSetDTO> combinationSets = externalDataCombination.getSets();

			if (combinationSets == null) {
				combinationSets = new ArrayList<>();
			}

			List<ExternalServiceSetDTO> externalSets = externalDataSet.getSets();

			if (externalSets == null || externalSets.isEmpty()) {
				result.add(configurationDTO);
				continue;
			}

			List<ServiceCombinationConfigurationDTO.CombinationSetDTO> outputSets = new ArrayList<>();

			for (ExternalServiceSetDTO externalSet : externalSets) {

				if (externalSet == null || externalSet.getInputSet() == null
						|| externalSet.getInputSet().getValue() == null) {
					continue;
				}

				Long inputSetId;

				try {
					inputSetId = Long.valueOf(externalSet.getInputSet().getValue().toString());
				} catch (NumberFormatException ex) {

					metadataCreationLogger.warn("Invalid inputSetId={} for nodeId={}", externalSet.getInputSet().getValue(),
							node.getId());

					continue;
				}

				CorrelationConfiguration correlationConfiguration = correlationConfigurationRepository
						.findById(inputSetId.intValue()).orElse(null);

				if (correlationConfiguration == null) {

					metadataCreationLogger.warn("Correlation configuration not found for setId={}, serviceId={}, nodeId={}", inputSetId,
							serviceId, node.getId());

					continue;
				}

				ServiceCombinationConfigurationDTO.CombinationSetDTO setDTO = new ServiceCombinationConfigurationDTO.CombinationSetDTO();

				setDTO.setSourceType(correlationConfiguration.getSetType());

				setDTO.setServiceId(correlationConfiguration.getServiceId() != null
						? correlationConfiguration.getServiceId().longValue()
						: null);

				setDTO.setBaseServiceId(correlationConfiguration.getBaseServiceId() != null
						? correlationConfiguration.getBaseServiceId().longValue()
						: null);

				setDTO.setSetId(
						correlationConfiguration.getId() != null ? correlationConfiguration.getId().longValue() : null);

				if (externalSet.getOutputSet() != null && externalSet.getOutputSet().getValue() != null) {

					setDTO.setOutputSetId(externalSet.getOutputSet().getValue().toString());
				}
				Integer priority = 1;

				for (CombinationSetDTO combinationSet : combinationSets) {

					if (combinationSet == null ) {
						continue;
					}

					List<ApprovedSetDTO> approvedSets = combinationSet.getApprovedSets();
					if (approvedSets == null ) {
						continue;
					}
					
					for(ApprovedSetDTO approvedSet : approvedSets) {
						if (Objects.equals(inputSetId, approvedSet.getSetId())) {
							priority = combinationSet.getSequence();
							break;
						}
					}
				}

				setDTO.setPriority(priority);

				List<ServiceCombinationConfigurationDTO.AttributeMappingDTO> inputAttributes = new ArrayList<>();

				if (externalSet.getInputAttributes() != null) {

					for (AttributeMappingDTO inputAttribute : externalSet.getInputAttributes()) {

						if (inputAttribute == null) {
							continue;
						}

						ServiceCombinationConfigurationDTO.AttributeMappingDTO attributeDTO = new ServiceCombinationConfigurationDTO.AttributeMappingDTO();

						attributeDTO.setMappedAttributeId(inputAttribute.getMappedAttributeId());

						attributeDTO.setAttributeId(inputAttribute.getAttributeId());

						inputAttributes.add(attributeDTO);
					}
				}

				setDTO.setInputAttributes(inputAttributes);

				List<ServiceCombinationConfigurationDTO.AttributeMappingDTO> outputAttributes = new ArrayList<>();

				if (externalSet.getOutputAttributes() != null) {

					for (AttributeMappingDTO outputAttribute : externalSet.getOutputAttributes()) {

						if (outputAttribute == null) {
							continue;
						}

						ServiceCombinationConfigurationDTO.AttributeMappingDTO attributeDTO = new ServiceCombinationConfigurationDTO.AttributeMappingDTO();

						attributeDTO.setMappedAttributeId(outputAttribute.getMappedAttributeId());

						attributeDTO.setAttributeId(outputAttribute.getAttributeId());

						outputAttributes.add(attributeDTO);
					}
				}

				setDTO.setOutputAttributes(outputAttributes);

				ApplicationStatusDTO applicationStatus = externalSet.getApplicationStatus();

				if (applicationStatus != null) {

					ServiceCombinationConfigurationDTO.ApplicationStatusDTO applicationStatusDTO = new ServiceCombinationConfigurationDTO.ApplicationStatusDTO();

					applicationStatusDTO.setValue(applicationStatus.getValue());

					applicationStatusDTO.setTaskId(applicationStatus.getTaskId());

					setDTO.setApplicationStatus(applicationStatusDTO);
				}

				outputSets.add(setDTO);
			}

			configurationDTO.setSets(outputSets);

			result.add(configurationDTO);
		}

		return result;
	}

	public ResponseEntity<?> getLevlsOfTask(Integer serviceId, UserSessionDTO userSessionDetails) {
		
		Optional<ServiceLog> serviceLog = serviceLogRepository.findByServiceIdAndServiceStatusNotInAndTenantId(
				serviceId, List.of(ServiceStatus.LAUNCHED.getCode(), ServiceStatus.DEFINED.getCode()),
				userSessionDetails.getTenantId());

		if (serviceLog.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		
		ServiceMetadata serviceMetadata = metadataRepository.findByServiceIdAndTenantId(serviceId,
				userSessionDetails.getTenantId());
		if (serviceMetadata == null || serviceMetadata.getMetadataJson() == null) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}

		List<OfficeDetailsDTO> officeDetails = serviceMetadata.getMetadataJson().getOfficeDetails();
		if (officeDetails == null || officeDetails.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		List<LabelValue> submissionModes = serviceMetadata.getMetadataJson().getSubmissionModes();

		Set<Integer> submissionModeSet = submissionModes == null ? Collections.emptySet()
				: submissionModes.stream().map(mode -> Integer.valueOf(mode.getValue().toString()))
						.collect(Collectors.toSet());

		boolean skipSubmissionTask = !submissionModeSet.isEmpty() && submissionModeSet.stream()
				.allMatch(mode -> ApplicationConstants.SUBMISSION_MODE_APPLICANT.equals(mode)
						|| ApplicationConstants.SUBMISSION_MODE_CLOSED_USER_GROUP.equals(mode)
						|| ApplicationConstants.SUBMISSION_MODE_WEBSERVICE.equals(mode));

		ResponseEntity<List<HierarchyDetailsDTO>> hierarchyResponse = lgdFeignClient.getLevelLowerHierarchy(
				userSessionDetails.getEntityLevelId(), userSessionDetails.getEntityId(),
				userSessionDetails.getClcId() != null ? userSessionDetails.getClcId().longValue() : null);

		List<HierarchyDetailsDTO> hierarchyList = hierarchyResponse.getBody() != null ? hierarchyResponse.getBody()
				: Collections.emptyList();

		List<TaskAssignmentHierarchyDTO> taskList = officeDetails.stream()
				.filter(officeDetail -> officeDetail.getOfficeLevelIds() != null
						&& officeDetail.getOfficeLevelIds().contains(userSessionDetails.getEntityLevelId()))
				.filter(officeDetail -> !(skipSubmissionTask
						&& ApplicationConstants.SUBMISSION_NODE_ID.equals(officeDetail.getTaskId())))
				.flatMap(officeDetail -> {

					String taskId = officeDetail.getTaskId();
					String taskName = getTaskName(serviceMetadata, taskId);

					return hierarchyList.stream()
							.filter(hierarchy -> officeDetail.getOfficeLevelIds().contains(hierarchy.getLevelCode()))
							.map(hierarchy -> new TaskAssignmentHierarchyDTO(taskId, taskName, hierarchy.getLevelCode(),
									hierarchy.getLevelSpecificName()));
				}).collect(Collectors.toList());

		return ResponseEntity.ok(taskList);
	}

	public ResponseEntity<?> getChargeTask(Integer serviceId, Long chargeId,
			UserSessionDTO userSessionDetails) {

		try {
			metadataCreationLogger.info("Fetching charge tasks. serviceId={}, chargeId={}", serviceId, chargeId);
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);

			if (serviceProcessFlow == null) {
				metadataCreationLogger.warn("Service process flow not found. serviceId={}", serviceId);
				throw new SPRuntimeError("Service not found.", HttpStatus.BAD_REQUEST);
			}

			ProcessFlowDTO processFlowJson = serviceProcessFlow.getProcessFlowJson();

			if (processFlowJson == null) {
				metadataCreationLogger.warn("Process flow JSON not found. serviceId={}", serviceId);
				throw new SPRuntimeError("Service Process Flow not found.", HttpStatus.BAD_REQUEST);
			}

			if (chargeId != null) {
				metadataCreationLogger.debug("Validating chargeId={}. serviceId={}", chargeId, serviceId);
				ServiceChargeDetail serviceCharge = serviceChargeRepository.findById(chargeId)
						.orElseThrow(() -> new SPRuntimeError("Invalid Charge Detail Id", HttpStatus.BAD_REQUEST));

				chargeId = serviceCharge.getId();
			}

			List<NodeDTO> nodes = processFlowJson.getNodes();

			if (nodes == null || nodes.isEmpty()) {
				metadataCreationLogger.info("No process flow nodes found. serviceId={}", serviceId);
				return ResponseEntity.ok(Collections.emptyList());
			}

			List<ChargeTemplateTaskDTO> chargeTemplateTaskDTOs = new LinkedList<>();

			for (NodeDTO node : nodes) {

				DataDTO data = node.getData();

				String taskId = node.getId();
				String taskName = data.getName();

				TaskPaymentMappingDTO payment = data.getPayment();

				boolean paymentNotConfigured = payment == null || payment.getChargeId() == null;

				if (chargeId == null) {
					if (paymentNotConfigured) {
						metadataCreationLogger.debug("Task available for charge mapping. taskId={}, taskName={}",
								taskId, taskName);
						chargeTemplateTaskDTOs.add(new ChargeTemplateTaskDTO(taskId, taskName, false));
					}
				} else {
					if (paymentNotConfigured) {
						metadataCreationLogger.debug(
								"Unconfigured task available for chargeId={}. taskId={}, taskName={}", chargeId, taskId,
								taskName);
						chargeTemplateTaskDTOs.add(new ChargeTemplateTaskDTO(taskId, taskName, false));
					} else if (payment.getChargeId().equals(chargeId)) {
						metadataCreationLogger.debug(
								"Task already mapped to charge. taskId={}, taskName={}, chargeId={}", taskId, taskName,
								chargeId);

						chargeTemplateTaskDTOs.add(new ChargeTemplateTaskDTO(taskId, taskName, true));
					}
				}
			}

			metadataCreationLogger.info(
					"Charge task processing completed. serviceId={}, chargeId={}, availableTasks={}", serviceId,
					chargeId, chargeTemplateTaskDTOs.size());
			return ResponseEntity.ok(chargeTemplateTaskDTOs);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			ex.printStackTrace();
			metadataCreationLogger.error("Error while processing charge tasks. serviceId={}, chargeId={}", serviceId,
					chargeId, ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


    private PaymentConfigurationDTO buildPaymentConfiguration(Integer serviceId,
                                                              UserSessionDTO userSessionDetails,
                                                              ServiceProcessFlowDTO processFlowDTO) {

        ObjectMapper objectMapper = new ObjectMapper();

        PaymentConfigurationDTO paymentConfiguration = new PaymentConfigurationDTO();

        // =========================================================
        // 1. CHARGE CONFIGURATION
        // =========================================================

        List<ServiceChargeDetail> serviceChargesDetails =
                serviceChargeRepository.findByServiceIdAndUserId(
                        serviceId,
                        userSessionDetails.getUserID()
                );

        List<PaymentConfigurationDTO.PaymentCharge> paymentCharges =
                new ArrayList<>();

        if (serviceChargesDetails != null && !serviceChargesDetails.isEmpty()) {

            for (ServiceChargeDetail chargeDetail : serviceChargesDetails) {

                if (chargeDetail == null
                        || chargeDetail.getServiceChargeJson() == null) {
                    continue;
                }

                ServiceChargeDTO charge =
                        chargeDetail.getServiceChargeJson();

                PaymentConfigurationDTO.PaymentCharge paymentCharge =
                        new PaymentConfigurationDTO.PaymentCharge();

                /*
                 * The actual charge-detail ID comes from the
                 * service_charge_detail table.
                 *
                 * Do NOT depend on charge.getChargeDetailId()
                 * because the JSON may not contain it.
                 */
                paymentCharge.setChargeDetailId(
                        chargeDetail.getId()
                );

                paymentCharge.setTemplateName(
                        charge.getTemplateName()
                );

                paymentCharge.setDefaultCharge(
                        charge.getDefaultCharge()
                );

                paymentCharge.setServiceUnitWise(
                        charge.getServiceUnitWise()
                );


                // =====================================================
                // PAYMENT MODES
                // =====================================================

                if (charge.getPaymentModes() != null) {

                    List<PaymentConfigurationDTO.PaymentMode> paymentModes =
                            new ArrayList<>();

                    for (ServiceChargeDTO.PaymentModeDTO paymentMode
                            : charge.getPaymentModes()) {

                        if (paymentMode == null) {
                            continue;
                        }

                        PaymentConfigurationDTO.PaymentMode mode =
                                new PaymentConfigurationDTO.PaymentMode();

                        mode.setLabel(
                                paymentMode.getLabel()
                        );

                        mode.setValue(
                                paymentMode.getValue()
                        );

                        mode.setOnlineFlag(
                                paymentMode.getOnlineFlag()
                        );

                        paymentModes.add(mode);
                    }

                    paymentCharge.setPaymentModes(paymentModes);
                }


                // =====================================================
                // CHARGE TYPES
                // =====================================================

                if (charge.getChargeTypeList() != null) {

                    List<PaymentConfigurationDTO.ChargeType> chargeTypes =
                            new ArrayList<>();

                    for (ServiceChargeDTO.ChargeTypeDTO chargeType
                            : charge.getChargeTypeList()) {

                        if (chargeType == null) {
                            continue;
                        }

                        PaymentConfigurationDTO.ChargeType type =
                                new PaymentConfigurationDTO.ChargeType();

                        type.setName(
                                chargeType.getName()
                        );

                        type.setAmount(
                                chargeType.getAmount()
                        );

                        chargeTypes.add(type);
                    }

                    paymentCharge.setChargeTypeList(chargeTypes);
                }


                // =====================================================
                // CHARGE DEFINED FOR
                // =====================================================

                if (charge.getChargeDefinedFor() != null) {

                    List<PaymentConfigurationDTO.LabelValue>
                            chargeDefinedFor = new ArrayList<>();

                    for (LabelValue value
                            : charge.getChargeDefinedFor()) {

                        if (value == null) {
                            continue;
                        }

                        PaymentConfigurationDTO.LabelValue labelValue =
                                new PaymentConfigurationDTO.LabelValue();

                        labelValue.setLabel(
                                value.getLabel()
                        );

                        labelValue.setValue(
                                value.getValue() != null
                                        ? value.getValue().toString()
                                        : null
                        );

                        chargeDefinedFor.add(labelValue);
                    }

                    paymentCharge.setChargeDefinedFor(
                            chargeDefinedFor
                    );
                }


                // =====================================================
                // CLOSED USER TYPE
                // =====================================================

                if (charge.getClosedUserType() != null) {

                    List<PaymentConfigurationDTO.ClosedUserType>
                            closedUserTypes = new ArrayList<>();

                    for (ServiceChargeDTO.ClosedUserTypeDTO closedUserType
                            : charge.getClosedUserType()) {

                        if (closedUserType == null) {
                            continue;
                        }

                        PaymentConfigurationDTO.ClosedUserType userType =
                                new PaymentConfigurationDTO.ClosedUserType();

                        userType.setValue(
                                closedUserType.getValue()
                        );

                        userType.setLabel(
                                closedUserType.getLabel()
                        );

                        closedUserTypes.add(userType);
                    }

                    paymentCharge.setClosedUserType(
                            closedUserTypes
                    );
                }

                paymentCharges.add(paymentCharge);
            }
        }

        paymentConfiguration.setPaymentCharges(paymentCharges);


        // =========================================================
        // 2. PAYMENT GATEWAY CONFIGURATION
        // =========================================================

        List<ServicePaymentGatewayConfig> gatewayConfigurations = servicePaymentGatewayConfigRepository.findByServiceIdAndTenantId(serviceId, userSessionDetails.getTenantId());

        List<PaymentConfigurationDTO.PaymentGatewayConfiguration> paymentGatewayConfigurations = new ArrayList<>();

        if (gatewayConfigurations != null && !gatewayConfigurations.isEmpty()) {

            for (ServicePaymentGatewayConfig config : gatewayConfigurations) {

                if (config == null || config.getMappingJson() == null || config.getMappingJson().isBlank()) {
                    continue;
                }

                try {

                    JsonNode mappingJson = objectMapper.readTree(config.getMappingJson());

                    PaymentConfigurationDTO.PaymentGatewayConfiguration gatewayConfiguration = new PaymentConfigurationDTO.PaymentGatewayConfiguration();

                    gatewayConfiguration.setGatewayCode(config.getGatewayCode());

                    gatewayConfiguration.setEnabled(config.getEnabled());


                    // -----------------------------
                    // Additional Data
                    // -----------------------------

                    JsonNode additionalDataNode = mappingJson.get("additionalData");

                    if (additionalDataNode != null && !additionalDataNode.isNull()) {

                        PaymentConfigurationDTO.AdditionalData additionalData = new PaymentConfigurationDTO.AdditionalData();

                        additionalData.setApplicabilityScope(additionalDataNode.path("applicabilityScope").asText(null));

                        if (additionalDataNode.has("enableRevalidation")) {
                            additionalData.setEnableRevalidation(additionalDataNode.get("enableRevalidation").asBoolean());
                        }

                        if (additionalDataNode.has("refundAllowed")) {
                            additionalData.setRefundAllowed(additionalDataNode.get("refundAllowed").asBoolean());
                        }

                        JsonNode revalidationModesNode = additionalDataNode.get("revalidationModes");

                        if (revalidationModesNode != null && revalidationModesNode.isArray()) {

                            additionalData.setRevalidationModes(objectMapper.convertValue(revalidationModesNode, List.class));
                        }

                        JsonNode revalidationPeriodsNode = additionalDataNode.get("revalidationPeriods");

                        if (revalidationPeriodsNode != null && revalidationPeriodsNode.isObject()) {

                            additionalData.setRevalidationPeriods(objectMapper.convertValue(revalidationPeriodsNode, Map.class));
                        }

                        gatewayConfiguration.setAdditionalData(additionalData);
                    }


                    // -----------------------------
                    // Gateway Mappings
                    // -----------------------------

                    JsonNode mappingsNode = mappingJson.get("mappings");

                    if (mappingsNode != null && mappingsNode.isArray()) {

                        List<PaymentConfigurationDTO.GatewayMapping> mappings = new ArrayList<>();

                        for (JsonNode mappingNode : mappingsNode) {

                            PaymentConfigurationDTO.GatewayMapping mapping = new PaymentConfigurationDTO.GatewayMapping();

                            mapping.setMode(mappingNode.path("mode").asText(null));

                            mapping.setNodeCode(mappingNode.path("nodeCode").asText(null));

                            mapping.setSourceType(mappingNode.path("sourceType").asText(null));

                            mapping.setSourceValue(mappingNode.path("sourceValue").asText(null));

                            mappings.add(mapping);
                        }

                        gatewayConfiguration.setMappings(mappings);
                    }

                    paymentGatewayConfigurations.add(gatewayConfiguration);

                } catch (Exception e) {

                    metadataCreationLogger.error("Unable to build payment gateway configuration. serviceId={}, gatewayCode={}", serviceId, config.getGatewayCode(), e);

                    throw new SPRuntimeError("Unable to build payment gateway configuration", HttpStatus.BAD_REQUEST);
                }
            }
        }

        paymentConfiguration.setPaymentGatewayConfigurations(paymentGatewayConfigurations);


        // =========================================================
        // 3. TASK PAYMENT MAPPING FROM PROCESS FLOW
        // =========================================================

        List<PaymentConfigurationDTO.TaskPaymentMapping> taskPaymentMappings = new ArrayList<>();

        if (processFlowDTO != null && processFlowDTO.getData() != null) {

            for (ServiceProcessFlowDTO.Data data : processFlowDTO.getData()) {

                if (data == null || data.getNode() == null) {
                    continue;
                }

                if (data.getNode().getPayment() == null) {
                    continue;
                }

                PaymentConfigurationDTO.TaskPaymentMapping taskPaymentMapping = new PaymentConfigurationDTO.TaskPaymentMapping();

                taskPaymentMapping.setTaskId(data.getNode().getId());

                taskPaymentMapping.setPayment(data.getNode().getPayment());

                taskPaymentMappings.add(taskPaymentMapping);
            }
        }

        paymentConfiguration.setTaskPaymentMappings(taskPaymentMappings);

        return paymentConfiguration;
    }


	public ResponseEntity<?> getEligibleTrackingAttributes(Integer serviceId, UserSessionDTO userSessionDetails) {
		metadataCreationLogger.info("Fetching eligible tracking attributes for serviceId: {}",serviceId);
	if (serviceId == null || serviceId <= 0) {
	    throw new SPRuntimeError("Invalid Service ID",HttpStatus.BAD_REQUEST);
	}
	requireTrackingService(serviceId, userSessionDetails);
	ServiceTemplate submissionTemplate =serviceTemplateRepository.findByServiceIdAndTaskId(serviceId,ApplicationConstants.SUBMISSION_NODE_ID);
	if (submissionTemplate == null) {
	    throw new SPRuntimeError("Submission form not found for service ID: " + serviceId,HttpStatus.NOT_FOUND);
	}
	String formId = submissionTemplate.getFormId();

	if (formId == null || formId.isBlank()) {
	    throw new SPRuntimeError("Form ID not found for submission node of service ID: " + serviceId,HttpStatus.NOT_FOUND);
	}
	FormDTO formDto;
	try {
	    ResponseEntity<?> result =formFeignClient.getAllAttributesByFormId(formId);

	    if (result == null|| !result.getStatusCode().is2xxSuccessful()|| !(result.getBody() instanceof Map<?, ?> body)|| !(body.get("attributes") instanceof List<?>)) {
	        throw new IllegalArgumentException("Invalid Form Designer response");
	    }
	    formDto = new Gson().fromJson(new Gson().toJson(result.getBody()),FormDTO.class);

	    if (formDto == null || formDto.getAttributes() == null) {
	        throw new IllegalArgumentException("Invalid Form Designer attributes");
	        }
	} catch (Exception ex) {
	    metadataCreationLogger.warn("Unable to fetch tracking form attributes for serviceId: {}",serviceId,ex);

	    throw new SPRuntimeError("Unable to fetch attributes from Form Designer",HttpStatus.BAD_GATEWAY);
	}
	List<FormDTO.Attribute> attributes =formDto.getAttributes();

	Set<Integer> inputTypes =Set.of(1, 2, 3, 4, 6, 7, 8, 40, 46);
	List<Map<String, Object>> mobileAttributes =new ArrayList<>();
	List<Map<String, Object>> emailAttributes =new ArrayList<>();
	List<Map<String, Object>> formAttributes =new ArrayList<>();
	for (FormDTO.Attribute attr : attributes) {
		

	    if (attr == null|| attr.getAttributeId() == null|| attr.getAttributeId().isBlank()|| !Boolean.TRUE.equals(attr.getRequired())) {
	        continue;
	    }
	    Integer typeId = attr.getTypeId();

	    if (typeId == null || !inputTypes.contains(typeId)) {
	        continue;
	    }
	    String rawLabel =attr.getAttributeLabel() != null? attr.getAttributeLabel().trim(): "";
	    String lowerLabel =rawLabel.toLowerCase(Locale.ROOT);
	    Map<String, Object> attrMap =new LinkedHashMap<>();
	    attrMap.put("attributeId",attr.getAttributeId());
	    attrMap.put("attributeLabel",rawLabel);
	    attrMap.put("typeId",typeId);

	    boolean isEmail =typeId == 3;

	    boolean isMobile =!isEmail&& (typeId == 1 || typeId == 2)&& lowerLabel.matches(".*\\b(mobile|phone|cell)\\b.*");

	    if (isEmail) {
	        emailAttributes.add(attrMap);
	    } else if (isMobile) {
	        mobileAttributes.add(attrMap);
	    } else {
	        formAttributes.add(attrMap);
	    }
	}
	Map<String, Object> response =new LinkedHashMap<>();
	response.put("serviceId",serviceId);
	response.put("formId",formId);
	response.put("formName",formDto.getFormName());
	response.put("mobileAttributes",mobileAttributes);
	response.put("emailAttributes",emailAttributes);
	response.put("formAttributes",formAttributes);

	return ResponseEntity.ok(response);
	}

    private ServiceLog requireTrackingService(Integer serviceId, UserSessionDTO session) {
        if (serviceId == null || serviceId <= 0) {
            throw new SPRuntimeError("Invalid Service ID", HttpStatus.BAD_REQUEST);
        }
        if (session == null || session.getUserID() == null || session.getTenantId() == null) {
            throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
        }
        ServiceLog log = serviceLogRepository.findByServiceId(serviceId);
        if (log == null || !session.getTenantId().equals(log.getTenantId())) {
            throw new SPRuntimeError("Service not found", HttpStatus.NOT_FOUND);
        }
        return log;
    }

    private Set<String> trackingAttributeIds(Map<?, ?> eligible, String key) {
        Object value = eligible.get(key);
        if (!(value instanceof List<?> fields)) {
            throw new SPRuntimeError("Unable to resolve tracking attributes", HttpStatus.BAD_GATEWAY);
        }
        Set<String> ids = new HashSet<>();
        for (Object field : fields) {
            if (!(field instanceof Map<?, ?> attribute) || !(attribute.get("attributeId") instanceof String id)) {
                throw new SPRuntimeError("Unable to resolve tracking attributes", HttpStatus.BAD_GATEWAY);
            }
            ids.add(id);
        }
        return ids;
    }

    private static final String INVALID_TRACKING_CONFIG = "Invalid tracking configuration";

    private void validateTrackingAttribute(String id, Set<String> eligible, String name) {
        if (id == null || id.isBlank() || !eligible.contains(id)) {
            throw new SPRuntimeError(INVALID_TRACKING_CONFIG, HttpStatus.BAD_REQUEST);
        }
    }
    
    
    private void validateAndNormalizeTrackingConfig(Integer serviceId,ServiceTrackingConfigDTO dto,UserSessionDTO session) {

        Set<String> allowed = Set.of("MOBILE_OTP","EMAIL_OTP","FORM_ATTRIBUTES");

        if (dto == null|| dto.getTrackingTypes() == null|| dto.getTrackingTypes().isEmpty()|| dto.getTrackingTypes().stream().anyMatch(type -> type == null || !allowed.contains(type))
                || new HashSet<>(dto.getTrackingTypes()).size() != dto.getTrackingTypes().size()) {

            throw new SPRuntimeError(INVALID_TRACKING_CONFIG,HttpStatus.BAD_REQUEST);
        }

        Object body = getEligibleTrackingAttributes(serviceId,session).getBody();
        if (!(body instanceof Map<?, ?> eligible)) {
            throw new SPRuntimeError("Unable to resolve tracking attributes",HttpStatus.BAD_GATEWAY);
        }
        Set<String> mobileIds =trackingAttributeIds(eligible,"mobileAttributes");
        Set<String> emailIds =trackingAttributeIds(eligible,"emailAttributes");
        Set<String> formIds =trackingAttributeIds(eligible,"formAttributes");

        if (dto.getTrackingTypes().contains("MOBILE_OTP")) {
            validateTrackingAttribute(dto.getMobileAttributeId(),mobileIds,"mobile");

        } else {
            dto.setMobileAttributeId(null);
        }

            if (dto.getTrackingTypes().contains("EMAIL_OTP")) {
            validateTrackingAttribute(dto.getEmailAttributeId(),emailIds,"email");

        } else {

            dto.setEmailAttributeId(null);
        }

        if (dto.getTrackingTypes().contains("FORM_ATTRIBUTES")) {

            List<String> ids =
                    dto.getSelectedAttributeIds();

            if (ids == null|| ids.size() < 3|| ids.size() > 5|| new HashSet<>(ids).size() != ids.size()) {

                throw new SPRuntimeError(INVALID_TRACKING_CONFIG,HttpStatus.BAD_REQUEST);
            }

            for (String id : ids) {

                validateTrackingAttribute(id,formIds,"form");
            }
        } else {
            dto.setSelectedAttributeIds(Collections.emptyList());
        }

        if (dto.getDisplayActionTakenDate() == null) {
            dto.setDisplayActionTakenDate(false);
        }
    }

    private String trackingTabs(String tablist, boolean include) {
        List<String> tabs = new ArrayList<>();
        boolean found = false;
        if (tablist != null && !tablist.isBlank()) {
            for (String token : tablist.split(",")) {
                if ("30".equals(token.trim())) {
                    if (include && !found) {
                        tabs.add("30");
                        found = true;
                    }
                } else {
                    tabs.add(token);
                }
            }
        }
        if (include && !found) {
            tabs.add("30");
        }
        return String.join(",", tabs);
    }

    @Transactional
public ResponseEntity<?> saveTrackingConfig(
        Integer serviceId,
        ServiceTrackingConfigDTO dto,
        UserSessionDTO session) {

    ServiceLog log = requireTrackingService(serviceId, session);

    validateAndNormalizeTrackingConfig(serviceId, dto, session);

    ServiceTrackingConfig config =trackingConfigRepository.findByServiceId(serviceId.longValue());

    if (config == null) {
        config = new ServiceTrackingConfig();
        config.setServiceId(serviceId.longValue());
        config.setBaseServiceId(log.getBaseServiceId().longValue());
        config.setCreatedBy(session.getUserID());
        config.setCreatedDate(java.time.LocalDateTime.now());
    } else {
        config.setUpdatedBy(session.getUserID());
        config.setUpdatedDate(java.time.LocalDateTime.now());
    }
    config.setTrackingConfigJson(dto);

    trackingConfigRepository.save(config);

    log.setTablist(trackingTabs(log.getTablist(), true));
    serviceLogRepository.save(log);

    return ResponseEntity.ok(Map.of(
            "serviceId", serviceId,
            "message", "Tracking configuration saved successfully",
            "tabList", log.getTablist()
    ));
}

    public ResponseEntity<?> getTrackingConfig(Integer serviceId, UserSessionDTO session) {
        requireTrackingService(serviceId, session);
        ServiceTrackingConfig config = trackingConfigRepository.findByServiceId(serviceId.longValue());
        if (config == null) {
            throw new SPRuntimeError("Tracking configuration not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(config.getTrackingConfigJson());
    }

    @Transactional
    public ResponseEntity<?> deleteTrackingConfig(Integer serviceId, UserSessionDTO session) {
        ServiceLog log = requireTrackingService(serviceId, session);
        ServiceTrackingConfig config = trackingConfigRepository.findByServiceId(serviceId.longValue());
        if (config == null) {
            throw new SPRuntimeError("Tracking configuration not found", HttpStatus.NOT_FOUND);
        }
        trackingConfigRepository.delete(config);
        log.setTablist(trackingTabs(log.getTablist(), false));
        serviceLogRepository.save(log);
        return ResponseEntity.ok(Map.of("serviceId", serviceId, "message", "Tracking configuration deleted successfully",
                "tabList", log.getTablist()));
    }
    
    
    public List<Map<String, Object>> getConfiguredServices(String tenantId){
    	List <Object[]> rows = trackingConfigRepository.findConfiguredServices(tenantId);
    	List <Map<String , Object>> result = new ArrayList<>();
    	for( Object[]row : rows)
    	{
    		Map <String , Object> services = new HashMap<>();
    		services.put("serviceId",((Number) row[0]).longValue());
    		services.put("serviceName", ((String) row[1]));
    		result.add(services);
        }
    	return result;
    }
    
    public Map<String, Object> getPublicTrackingConfig( Long serviceId, String tenantId) {
        // 1. Validate input
        if (serviceId == null || serviceId <= 0 || serviceId > Integer.MAX_VALUE || tenantId == null || tenantId.isBlank()) {
            throw new SPRuntimeError( "Invalid tracking configuration request", HttpStatus.BAD_REQUEST);
        }
        // 2. Verify service belongs to tenant
        ServiceLog serviceLog = serviceLogRepository.findByServiceId(Math.toIntExact(serviceId));
        if (serviceLog == null || !tenantId.equals(serviceLog.getTenantId())) {
            throw new SPRuntimeError( "Tracking configuration not found",HttpStatus.NOT_FOUND);
        }
        // 3. Fetch saved configuration
        ServiceTrackingConfig config =trackingConfigRepository.findByServiceId(serviceId);
        if (config == null || config.getTrackingConfigJson() == null) {
            throw new SPRuntimeError("Tracking configuration not found",HttpStatus.NOT_FOUND);
        }
        // 4. Get saved JSON
        ServiceTrackingConfigDTO savedConfig =config.getTrackingConfigJson();
        List<String> types = savedConfig.getTrackingTypes();
        if (types == null || types.isEmpty()) {
            throw new SPRuntimeError("Invalid saved tracking configuration",HttpStatus.CONFLICT);
        }
        // 5. Fetch Form Designer attributes
        List<FormDTO.Attribute> allAttributes =getTrackingFormAttributes(serviceId);
        // 6. Prepare response
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("serviceId", serviceId);
        result.put("trackingTypes", types);
        result.put("displayActionTakenDate",
                Boolean.TRUE.equals(savedConfig.getDisplayActionTakenDate()));
        // 7. Mobile OTP
        if (types.contains("MOBILE_OTP")) {
            List<Map<String, Object>> mobile =filterSelectedAttributes(allAttributes,Collections.singletonList(savedConfig.getMobileAttributeId()));
            result.put("mobileAttribute", mobile.get(0));
        } else {
            result.put("mobileAttribute", null);
        }
        // 8. Email OTP
        if (types.contains("EMAIL_OTP")) {
            List<Map<String, Object>> email =filterSelectedAttributes(allAttributes,Collections.singletonList(savedConfig.getEmailAttributeId()));
            result.put("emailAttribute", email.get(0));
        } else {
            result.put("emailAttribute", null);
        }
        // 9. Form Attributes
        if (types.contains("FORM_ATTRIBUTES")) {
            List<Map<String, Object>> selectedFields =filterSelectedAttributes(allAttributes,savedConfig.getSelectedAttributeIds());
            result.put("formAttributes", selectedFields);
        } else {
            result.put("formAttributes", Collections.emptyList());
        }
        return result;
    }

    private List<Map<String, Object>> filterSelectedAttributes( List<FormDTO.Attribute> attributes, List<String> selectedIds) {
        if (attributes == null || selectedIds == null) 
        {
            throw new SPRuntimeError( "Tracking attributes are unavailable", HttpStatus.CONFLICT);
        }
        List<Map<String, Object>> result = new ArrayList<>();

        for (String id : selectedIds)
        {
            if (id == null || id.isBlank()) 
            {
                throw new SPRuntimeError( "Invalid configured attribute ID", HttpStatus.CONFLICT);
            }
            boolean found = false;
            for (FormDTO.Attribute attr : attributes) 
            {
                if (attr != null && id.equals(attr.getAttributeId())) {
                    Map<String, Object> fields = new HashMap<>();
                    fields.put("attributeId", attr.getAttributeId());
                    fields.put("attributeLabel", attr.getAttributeLabel());
                    fields.put("typeId", attr.getTypeId());
                    result.add(fields);

                    found = true;
                    break;
                }
            }
            if (!found) 
            {
                throw new SPRuntimeError( "Configured tracking attribute not found", HttpStatus.CONFLICT);
            }
        }
        return result;
    }
    
    private List<FormDTO.Attribute> getTrackingFormAttributes( Long serviceId) {    	
    	int id = Math.toIntExact(serviceId);
    	ServiceTemplate template = serviceTemplateRepository.findByServiceIdAndTaskId(id,ApplicationConstants.SUBMISSION_NODE_ID);

    	if(template == null || template.getFormId() == null || template.getFormId().isBlank())
    	{
    		throw new SPRuntimeError( "Submission form not found", HttpStatus.NOT_FOUND);
    	}
    	ResponseEntity<?> response = formFeignClient.getAllAttributesByFormId(template.getFormId());
    	if (response == null ||!response.getStatusCode().is2xxSuccessful() ||!(response.getBody() instanceof Map<?, ?> body) ||!(body.get("attributes") instanceof List<?>)) {

    	    throw new SPRuntimeError("Invalid Form Designer response",HttpStatus.BAD_GATEWAY);
    	}
    	FormDTO formDto = new Gson().fromJson(new Gson().toJson(response.getBody()),FormDTO.class);

    	if (formDto == null || formDto.getAttributes() == null) {
    	    throw new SPRuntimeError("Form attributes unavailable",HttpStatus.BAD_GATEWAY);
    	}
    	return formDto.getAttributes();
    }
    

}
