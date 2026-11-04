package com.serviceplus.metadata.catalogue.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueChatbotRequestDTO;
import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueConfigurationRequestDTO;
import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueConfigurationResponseDTO;
import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueQuestionnaireRequestDTO;
import com.serviceplus.metadata.catalogue.dto.ServiceDetailDTO;
import com.serviceplus.metadata.catalogue.entity.ServiceCatalogueConfiguration;
import com.serviceplus.metadata.catalogue.entity.ServiceCatalogueServiceMapping;
import com.serviceplus.metadata.catalogue.enums.ServiceCatalogueConfigurationStatus;
import com.serviceplus.metadata.catalogue.repository.ServiceCatalogueConfigurationRepository;
import com.serviceplus.metadata.catalogue.repository.ServiceCatalogueServiceMappingRepository;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMasterData;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.SpaAiChatbotFeignClient;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMasterDataRepository;

import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueDataDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.dto.TaskPaymentMappingDTO;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceChargeRepository;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionRepository;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO;
import com.serviceplus.metadata.dto.TaskPaymentMappingDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.entity.ServiceOutputFormat;
import com.serviceplus.metadata.catalogue.entity.ServiceCatalogueServiceMapping;
import com.serviceplus.metadata.repository.IServiceOutputFormatRepository;
import com.serviceplus.metadata.repository.IServiceChargeRepository;
import com.serviceplus.metadata.entity.ServiceChargeDetail;
import com.serviceplus.metadata.dto.ServiceChargeDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import com.serviceplus.metadata.feignClient.ProcessingFeignClient;

@Service
public class ServiceCatalogueService {

	private static final Logger serviceMetadataLogger = LogManager.getLogger("metadataCreationLogger");

	private final ServiceCatalogueConfigurationRepository configurationRepository;

	private final ServiceCatalogueServiceMappingRepository mappingRepository;

	private final IServiceDefinitionRepository serviceDefinitionRepository;

	private final IServiceLogRepository serviceLogRepository;

	private final IServiceMasterDataRepository masterDataRepository;

	private final ObjectMapper mapper;
	
	private final SpaAiChatbotFeignClient spaAiChatbotFeignClient;
	
	private final IProcessFlowRepository processFlowRepository;
	private final IServiceDeliveryUnitDefinitionRepository
    serviceDeliveryUnitDefinitionRepository;
	private final IServiceOutputFormatRepository serviceOutputFormatRepository;
	private final IServiceChargeRepository serviceChargeRepository;
	
	private final ProcessingFeignClient processingFeignClient;
	
	public ServiceCatalogueService(ServiceCatalogueConfigurationRepository configurationRepository,
			ServiceCatalogueServiceMappingRepository mappingRepository,
			IServiceDefinitionRepository serviceDefinitionRepository, IServiceLogRepository serviceLogRepository,
			IServiceMasterDataRepository masterDataRepository, ObjectMapper mapper, SpaAiChatbotFeignClient spaAiChatbotFeignClient,IProcessFlowRepository processFlowRepository,IServiceDeliveryUnitDefinitionRepository
	        serviceDeliveryUnitDefinitionRepository,IServiceOutputFormatRepository serviceOutputFormatRepository,IServiceChargeRepository serviceChargeRepository,ProcessingFeignClient processingFeignClient) {

		this.configurationRepository = configurationRepository;

		this.mappingRepository = mappingRepository;

		this.serviceDefinitionRepository = serviceDefinitionRepository;

		this.serviceLogRepository = serviceLogRepository;

		this.masterDataRepository = masterDataRepository;

		this.mapper = mapper;
		this.spaAiChatbotFeignClient = spaAiChatbotFeignClient;
		this.processFlowRepository = processFlowRepository;
		this.serviceDeliveryUnitDefinitionRepository =
		        serviceDeliveryUnitDefinitionRepository;
		this.serviceOutputFormatRepository = serviceOutputFormatRepository;
		this.serviceChargeRepository = serviceChargeRepository;
		this.processingFeignClient = processingFeignClient;
		
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getConfigurations(UserSessionDTO userSessionDetails, Integer status, String search) {

		try {
			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			List<ServiceCatalogueConfiguration> configurations;

			if (status != null) {
				configurations = configurationRepository
						.findByServiceDefinerIdAndStatusOrderByCreatedOnDesc(serviceDefinerId, status);
			} else {
				configurations = configurationRepository.findByServiceDefinerIdOrderByCreatedOnDesc(serviceDefinerId);
			}

			if (search != null && !search.trim().isEmpty()) {

				String searchText = search.trim().toLowerCase();

				configurations = configurations.stream()
						.filter(configuration -> contains(configuration.getName(), searchText)
								|| contains(configuration.getDescription(), searchText)
								|| contains(configuration.getConfigurationCode(), searchText))
						.collect(Collectors.toList());
			}

			List<ServiceCatalogueConfigurationResponseDTO> configurationList = new ArrayList<>();

			for (ServiceCatalogueConfiguration configuration : configurations) {

				List<ServiceCatalogueServiceMapping> mappings = mappingRepository
						.findByConfigurationId(configuration.getId());

				List<Integer> serviceIds = mappings.stream().map(mapping -> mapping.getServiceId().intValue())
						.collect(Collectors.toList());

				List<ServiceDetailDTO> serviceDetails = getServiceDetails(serviceIds, serviceDefinerId);

				ServiceCatalogueConfigurationResponseDTO response = buildConfigurationResponseDTO(configuration,serviceDetails,true);
				
				configurationList.add(response);
			}
			return ResponseEntity.ok(configurationList);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Get service catalogue configurations :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getServices(UserSessionDTO userSessionDetails, String search, Integer status) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			List<ServiceDefinition> serviceList = serviceDefinitionRepository.findByUserId(serviceDefinerId);

			if (serviceList == null || serviceList.isEmpty()) {
				return ResponseEntity.ok(new ArrayList<ServiceDetailDTO>());
			}

			List<Integer> serviceIds = serviceList.stream().map(ServiceDefinition::getServiceId)
					.filter(Objects::nonNull).collect(Collectors.toList());

			List<ServiceLog> serviceLogs;

			if (status != null) {

				serviceLogs = serviceLogRepository.findByServiceIdInAndServiceStatusAndTenantId(serviceIds, status,
						userSessionDetails.getTenantId());

			} else {

				serviceLogs = serviceLogRepository.findByServiceIdInAndTenantId(serviceIds,
						userSessionDetails.getTenantId());
			}

			Map<Integer, ServiceLog> serviceLogMap = serviceLogs.stream().collect(
					Collectors.toMap(ServiceLog::getServiceId, serviceLog -> serviceLog, (first, second) -> first));

			if (status != null) {

				serviceList = serviceList.stream().filter(service -> serviceLogMap.containsKey(service.getServiceId()))
						.collect(Collectors.toList());
			}

			if (search != null && !search.trim().isEmpty()) {

				String searchText = search.trim().toLowerCase();

				serviceList = serviceList.stream().filter(service -> {

					String serviceName = service.getDefinitionJson() != null ? service.getDefinitionJson().getName()
							: null;

					return contains(serviceName, searchText);
				}).collect(Collectors.toList());
			}

			List<Long> serviceIdList = serviceIds.stream().map(Integer::longValue).collect(Collectors.toList());

			List<ServiceCatalogueServiceMapping> mappings = mappingRepository.findByServiceIdIn(serviceIdList);

			Map<Long, ServiceCatalogueServiceMapping> mappingMap = mappings.stream().collect(Collectors
					.toMap(ServiceCatalogueServiceMapping::getServiceId, mapping -> mapping, (first, second) -> first));

			List<Long> configurationIds = mappings.stream().map(ServiceCatalogueServiceMapping::getConfigurationId)
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			Map<Long, ServiceCatalogueConfiguration> configurationMap = new HashMap<>();

			if (!configurationIds.isEmpty()) {

				List<ServiceCatalogueConfiguration> configurations = configurationRepository
						.findAllById(configurationIds);

				configurationMap = configurations.stream().collect(
						Collectors.toMap(ServiceCatalogueConfiguration::getId, configuration -> configuration));
			}

			List<ServiceDetailDTO> result = new ArrayList<>();

			for (ServiceDefinition service : serviceList) {

				ServiceLog serviceLog = serviceLogMap.get(service.getServiceId());

				ServiceDetailDTO dto = new ServiceDetailDTO();

				dto.setServiceId(service.getServiceId());

				if (service.getDefinitionJson() != null) {

					dto.setServiceName(service.getDefinitionJson().getName());

					dto.setDescription(service.getDefinitionJson().getDescription());

				} else {

					dto.setServiceName(null);
					dto.setDescription(null);
				}

				dto.setStatus(serviceLog != null && serviceLog.getServiceStatus() != null
						? ServiceStatus.fromCode(serviceLog.getServiceStatus()).name()
						: null);

				ServiceCatalogueServiceMapping mapping = mappingMap.get(service.getServiceId().longValue());

				if (mapping != null) {

					dto.setUsed(true);

					dto.setConfigurationId(mapping.getConfigurationId());

					ServiceCatalogueConfiguration configuration = configurationMap.get(mapping.getConfigurationId());

					if (configuration != null) {

						dto.setConfigurationName(configuration.getName());
					}

				} else {

					dto.setUsed(false);
					dto.setConfigurationId(null);
					dto.setConfigurationName(null);
				}

				result.add(dto);
			}

			return ResponseEntity.ok(result);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Get service catalogue services :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@Transactional
	public ResponseEntity<?> createConfiguration(UserSessionDTO userSessionDetails,
			ServiceCatalogueConfigurationRequestDTO request) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			validateConfigurationRequest(request);

			List<Integer> serviceIds = normalizeServiceIds(request.getServiceIds());

			validateServices(serviceIds, serviceDefinerId);

			validateServiceUniqueness(serviceIds);

			ServiceCatalogueConfiguration configuration = new ServiceCatalogueConfiguration();

			configuration.setConfigurationCode(generateConfigurationCode());

			configuration.setName(request.getName().trim());

			configuration.setDescription(request.getDescription());

			configuration.setServiceDefinerId(serviceDefinerId);

			configuration.setStatus(ServiceCatalogueConfigurationStatus.DRAFT.getValue());

			
			configuration.setData(createDefaultConfigurationData());

			configuration = configurationRepository.save(configuration);

			saveServiceMappings(configuration.getId(), serviceIds);

			JSONObject response = new JSONObject();
			response.put("configurationId", configuration.getId());
			response.put("message", "Configuration created successfully");
			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Create service catalogue configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

	@Transactional(readOnly = true)
	public ResponseEntity<?> getConfiguration(UserSessionDTO userSessionDetails, Long configurationId) {
		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			ServiceCatalogueConfiguration configuration = getConfigurationForOwner(configurationId, serviceDefinerId);

			List<ServiceCatalogueServiceMapping> mappings = mappingRepository.findByConfigurationId(configurationId);

			List<Integer> serviceIds = mappings.stream().map(mapping -> mapping.getServiceId().intValue())
					.collect(Collectors.toList());

			List<ServiceDetailDTO> serviceDetails = getServiceDetails(serviceIds, serviceDefinerId);

			ServiceCatalogueConfigurationResponseDTO response = buildConfigurationResponseDTO(configuration,serviceDetails,true);
			
			return ResponseEntity.ok(response);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Get service catalogue configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private List<ServiceDetailDTO> getServiceDetails(List<Integer> serviceIds, Long serviceDefinerId) {

		List<ServiceDetailDTO> serviceDetails = new ArrayList<>();
		if (serviceIds == null || serviceIds.isEmpty()) {
			return serviceDetails;
		}

		List<ServiceDefinition> serviceDefinitions = serviceDefinitionRepository.findByUserId(serviceDefinerId);
		if (serviceDefinitions == null || serviceDefinitions.isEmpty()) {

			return serviceDetails;
		}

		Map<Integer, ServiceDefinition> serviceDefinitionMap = serviceDefinitions.stream()
				.filter(service -> service.getServiceId() != null).collect(Collectors
						.toMap(ServiceDefinition::getServiceId, service -> service, (first, second) -> first));

		List<ServiceLog> serviceLogs = serviceLogRepository.findByServiceIdIn(serviceIds);

		Map<Integer, ServiceLog> serviceLogMap = serviceLogs.stream().filter(log -> log.getServiceId() != null)
				.collect(Collectors.toMap(ServiceLog::getServiceId, log -> log, (first, second) -> first));

		for (Integer serviceId : serviceIds) {

			ServiceDefinition serviceDefinition = serviceDefinitionMap.get(serviceId);

			if (serviceDefinition == null) {
				continue;
			}

			ServiceDetailDTO dto = new ServiceDetailDTO();
			
			dto.setBaseServiceId(serviceDefinition.getBaseServiceId());
			
			dto.setServiceId(serviceDefinition.getServiceId());

			if (serviceDefinition.getDefinitionJson() != null) {

				dto.setServiceName(serviceDefinition.getDefinitionJson().getName());

				dto.setDescription(serviceDefinition.getDefinitionJson().getDescription());
			}

			ServiceLog serviceLog = serviceLogMap.get(serviceId);

			if (serviceLog != null && serviceLog.getServiceStatus() != null) {

				dto.setStatus(ServiceStatus.fromCode(serviceLog.getServiceStatus()).name());
			}

			serviceDetails.add(dto);
		}

		return serviceDetails;
	}

	@Transactional
	public ResponseEntity<?> modifyConfiguration(UserSessionDTO userSessionDetails,
			ServiceCatalogueConfigurationRequestDTO request) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			if (request == null || request.getConfigurationId() == null) {
				throw new SPRuntimeError("Configuration id is required", HttpStatus.BAD_REQUEST);
			}

			ServiceCatalogueConfiguration configuration = getConfigurationForOwner(request.getConfigurationId(),
					serviceDefinerId);

			validateNotActive(configuration);

			if (request.getName() != null && !request.getName().trim().isEmpty()) {
				configuration.setName(request.getName().trim());
			}

			if (request.getDescription() != null) {
				configuration.setDescription(request.getDescription());
			}

			if (request.getServiceIds() != null) {

				List<Integer> serviceIds = normalizeServiceIds(request.getServiceIds());

				if (serviceIds.isEmpty()) {

					throw new SPRuntimeError("At least one service is required", HttpStatus.BAD_REQUEST);
				}

				validateServices(serviceIds, serviceDefinerId);

				validateServiceUniquenessForConfiguration(serviceIds, configuration.getId());

				mappingRepository.deleteByConfigurationId(configuration.getId());

				saveServiceMappings(configuration.getId(), serviceIds);
			}

			configuration = configurationRepository.save(configuration);

			JSONObject response = new JSONObject();

			response.put("configurationId", configuration.getId());

			response.put("message", "Configuration modified successfully");

			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Modify service catalogue configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Transactional(readOnly = true)
	public ResponseEntity<?> getQuestionnaire(UserSessionDTO userSessionDetails, Integer masterId) {

		try {

			if (masterId == null) {

				throw new SPRuntimeError("Master id is required", HttpStatus.BAD_REQUEST);
			}

			ServiceMasterData masterData = masterDataRepository.findById(masterId)
					.orElseThrow(() -> new SPRuntimeError("Questionnaire master not found", HttpStatus.NOT_FOUND));

			JSONObject response = new JSONObject();

			response.put("message", "Success");

			if (masterData.getData() != null) {

				response.put("data", new JSONObject(masterData.getData().toString()));

			} else {

				response.put("data", new JSONObject());
			}

			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Get beneficiary profile questionnaire :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

	@Transactional
	public ResponseEntity<?> saveQuestionnaire(UserSessionDTO userSessionDetails,
			ServiceCatalogueQuestionnaireRequestDTO request) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			if (request == null || request.getConfigurationId() == null) {

				throw new SPRuntimeError("Configuration id is required", HttpStatus.BAD_REQUEST);
			}

			ServiceCatalogueConfiguration configuration = getConfigurationForOwner(request.getConfigurationId(),
					serviceDefinerId);

			validateNotActive(configuration);

			if (Boolean.FALSE.equals(request.getEnabled())) {
				throw new SPRuntimeError("At least one beneficiary profile category must be defined",
						HttpStatus.BAD_REQUEST);
			}

			if (request.getMasterId() == null) {
				throw new SPRuntimeError("Master id is required when beneficiary profile is enabled",
						HttpStatus.BAD_REQUEST);
			}

			ServiceMasterData masterData = masterDataRepository.findById(request.getMasterId())
					.orElseThrow(() -> new SPRuntimeError("Questionnaire master not found", HttpStatus.NOT_FOUND));

			JsonNode answers = request.getAnswers();

			if (!hasAtLeastOneCategoryDefined(answers)) {
				throw new SPRuntimeError("At least one beneficiary profile category must be defined",
						HttpStatus.BAD_REQUEST);
			}

			ObjectNode configurationData;

			if (configuration.getData() != null && configuration.getData().isObject()) {
				configurationData = (ObjectNode) configuration.getData().deepCopy();
			} else {
				configurationData = mapper.createObjectNode();
			}

			ObjectNode beneficiaryProfile = mapper.createObjectNode();

			beneficiaryProfile.put("enabled", true);
			beneficiaryProfile.put("masterId", masterData.getMasterId());
			beneficiaryProfile.set("answers", answers);

			configurationData.set("beneficiaryProfile", beneficiaryProfile);

			configuration.setData(configurationData);

			configuration = configurationRepository.save(configuration);

			JSONObject response = new JSONObject();

			response.put("configurationId", configuration.getId());

			response.put("message", "Beneficiary profile configuration saved successfully");

			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Save beneficiary profile configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public ResponseEntity<?> activateConfiguration(UserSessionDTO userSessionDetails, Long configurationId) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			ServiceCatalogueConfiguration configuration = getConfigurationForOwner(configurationId, serviceDefinerId);

			ServiceCatalogueConfigurationStatus currentStatus = ServiceCatalogueConfigurationStatus
					.fromValue(configuration.getStatus());

			if (currentStatus == ServiceCatalogueConfigurationStatus.ACTIVE) {

				throw new SPRuntimeError("Configuration is already active", HttpStatus.BAD_REQUEST);
			}

			List<ServiceCatalogueServiceMapping> mappings = mappingRepository.findByConfigurationId(configurationId);

			if (mappings == null || mappings.isEmpty()) {

				throw new SPRuntimeError("At least one service must be mapped to the configuration",
						HttpStatus.BAD_REQUEST);
			}

			validateBeneficiaryProfileForActivation(configuration);

			configuration.setStatus(ServiceCatalogueConfigurationStatus.ACTIVE.getValue());

			configuration = configurationRepository.save(configuration);

			// Push configuration to chatbot
			pushConfigurationToChatbot(configuration, serviceDefinerId, "ACTIVATE");
			JSONObject response = new JSONObject();
			
			
			response.put("configurationId",configuration.getId());
			response.put("message", "Configuration activated successfully");


			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Activate service catalogue configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Transactional
	public ResponseEntity<?> deactivateConfiguration(UserSessionDTO userSessionDetails, Long configurationId) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			ServiceCatalogueConfiguration configuration = getConfigurationForOwner(configurationId, serviceDefinerId);

			ServiceCatalogueConfigurationStatus currentStatus = ServiceCatalogueConfigurationStatus
					.fromValue(configuration.getStatus());

			if (currentStatus != ServiceCatalogueConfigurationStatus.ACTIVE) {

				throw new SPRuntimeError("Only active configuration can be deactivated", HttpStatus.BAD_REQUEST);
			}
			
			configuration.setStatus(ServiceCatalogueConfigurationStatus.DEACTIVATED.getValue());

			configuration = configurationRepository.save(configuration);

			// Notify chatbot
			pushConfigurationToChatbot(configuration, serviceDefinerId, "DEACTIVATE");
			List<ServiceCatalogueServiceMapping> mappings = mappingRepository.findByConfigurationId(configurationId);

			JSONObject response = new JSONObject();
			response.put("configurationId", configuration.getId());
			response.put("message", "Configuration deactivated successfully");

			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Deactivate service catalogue configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Transactional
	public ResponseEntity<?> deleteConfiguration(UserSessionDTO userSessionDetails, Long configurationId) {

		try {

			Long serviceDefinerId = getServiceDefinerId(userSessionDetails);

			ServiceCatalogueConfiguration configuration = getConfigurationForOwner(configurationId, serviceDefinerId);

			ServiceCatalogueConfigurationStatus currentStatus = ServiceCatalogueConfigurationStatus
					.fromValue(configuration.getStatus());

			if (currentStatus == ServiceCatalogueConfigurationStatus.ACTIVE) {

				throw new SPRuntimeError("Active configuration cannot be deleted. Deactivate it first.",
						HttpStatus.BAD_REQUEST);
			}

			pushConfigurationToChatbot(configuration, serviceDefinerId, "DELETE");

			mappingRepository.deleteByConfigurationId(configurationId);

			configurationRepository.delete(configuration);

			JSONObject response = new JSONObject();

			response.put("configurationId", configuration.getId());
			response.put("message", "Configuration deleted successfully");

			return ResponseEntity.ok(response.toString());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error : Delete service catalogue configuration :", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private void validateServices(List<Integer> serviceIds, Long serviceDefinerId) {

		if (serviceIds == null || serviceIds.isEmpty()) {

			throw new SPRuntimeError("At least one service is required", HttpStatus.BAD_REQUEST);
		}

		List<ServiceDefinition> serviceDefinitions = serviceDefinitionRepository.findByUserId(serviceDefinerId);

		if (serviceDefinitions == null || serviceDefinitions.isEmpty()) {

			throw new SPRuntimeError("No services found for the service definer", HttpStatus.BAD_REQUEST);
		}

		Set<Integer> ownedServiceIds = serviceDefinitions.stream().map(ServiceDefinition::getServiceId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		for (Integer serviceId : serviceIds) {

			if (!ownedServiceIds.contains(serviceId)) {

				throw new SPRuntimeError(
						"Invalid service " + serviceId + ". Service does not belong to the current service definer.",
						HttpStatus.BAD_REQUEST);
			}
		}
	}

	

	private void validateServiceUniqueness(List<Integer> serviceIds) {

		for (Integer serviceId : serviceIds) {

			Optional<ServiceCatalogueServiceMapping> mapping = mappingRepository.findByServiceId(serviceId.longValue());

			if (mapping.isPresent()) {

				throw new SPRuntimeError("Service " + serviceId + " is already mapped to another configuration",
						HttpStatus.CONFLICT);
			}
		}
	}

	
	private void validateServiceUniquenessForConfiguration(List<Integer> serviceIds, Long configurationId) {

		for (Integer serviceId : serviceIds) {

			Optional<ServiceCatalogueServiceMapping> mapping = mappingRepository.findByServiceId(serviceId.longValue());

			if (mapping.isPresent()) {

				Long mappedConfigurationId = mapping.get().getConfigurationId();

				if (!configurationId.equals(mappedConfigurationId)) {

					throw new SPRuntimeError("Service " + serviceId + " is already mapped to another configuration",
							HttpStatus.CONFLICT);
				}
			}
		}
	}

	

	private void saveServiceMappings(Long configurationId, List<Integer> serviceIds) {

		if (serviceIds == null || serviceIds.isEmpty()) {

			return;
		}

		for (Integer serviceId : serviceIds) {

			ServiceCatalogueServiceMapping mapping = new ServiceCatalogueServiceMapping();

			mapping.setConfigurationId(configurationId);

			mapping.setServiceId(serviceId.longValue());

			mappingRepository.save(mapping);
		}
	}

	

	private ServiceCatalogueConfiguration getConfigurationForOwner(Long configurationId, Long serviceDefinerId) {

		if (configurationId == null) {
			throw new SPRuntimeError("Configuration id is required", HttpStatus.BAD_REQUEST);
		}
		
		return configurationRepository.findByIdAndServiceDefinerId(configurationId, serviceDefinerId)
				.orElseThrow(() -> new SPRuntimeError("Configuration not found", HttpStatus.NOT_FOUND));
	}



	private void validateNotActive(ServiceCatalogueConfiguration configuration) {

		ServiceCatalogueConfigurationStatus status = ServiceCatalogueConfigurationStatus
				.fromValue(configuration.getStatus());

		if (status == ServiceCatalogueConfigurationStatus.ACTIVE) {

			throw new SPRuntimeError("Active configuration cannot be modified", HttpStatus.BAD_REQUEST);
		}
	}

	

	private void validateBeneficiaryProfileForActivation(ServiceCatalogueConfiguration configuration) {

		JsonNode data = configuration.getData();

		if (data == null || !data.isObject()) {
			throw new SPRuntimeError("At least one beneficiary profile category must be defined",
					HttpStatus.BAD_REQUEST);
		}

		JsonNode beneficiaryProfile = data.get("beneficiaryProfile");

		if (beneficiaryProfile == null || !beneficiaryProfile.isObject()) {
			throw new SPRuntimeError("At least one beneficiary profile category must be defined",
					HttpStatus.BAD_REQUEST);
		}

		boolean enabled = beneficiaryProfile.path("enabled").asBoolean(false);

		if (!enabled) {
			throw new SPRuntimeError("At least one beneficiary profile category must be defined",
					HttpStatus.BAD_REQUEST);
		}

		JsonNode masterId = beneficiaryProfile.get("masterId");

		if (masterId == null || masterId.isNull()) {
			throw new SPRuntimeError("Master id is required for beneficiary profile", HttpStatus.BAD_REQUEST);
		}

		Integer questionnaireMasterId = masterId.asInt();

		boolean masterExists = masterDataRepository.existsById(questionnaireMasterId);

		if (!masterExists) {
			throw new SPRuntimeError("Beneficiary profile questionnaire master does not exist", HttpStatus.BAD_REQUEST);
		}

		JsonNode answers = beneficiaryProfile.get("answers");

		if (!hasAtLeastOneCategoryDefined(answers)) {
			throw new SPRuntimeError("At least one beneficiary profile category must be defined",
					HttpStatus.BAD_REQUEST);
		}
	}

	private boolean hasAtLeastOneCategoryDefined(JsonNode answers) {

		if (answers == null || !answers.isObject() || answers.isEmpty()) {
			return false;
		}

		for (Iterator<Map.Entry<String, JsonNode>> it = answers.fields(); it.hasNext();) {
			Map.Entry<String, JsonNode> entry = it.next();
			if (isCategoryDefined(entry.getValue())) {
				return true;
			}
		}

		return false;
	}

	private boolean isCategoryDefined(JsonNode node) {

		if (node == null || node.isNull()) {
			return false;
		}

		if (node.isTextual()) {
			return !node.asText().trim().isEmpty();
		}

		if (node.isNumber()) {
			return true;
		}

		if (node.isBoolean()) {
			return node.asBoolean();
		}

		if (node.isArray()) {
			if (node.isEmpty()) {
				return false;
			}
			for (JsonNode element : node) {
				if (isCategoryDefined(element)) {
					return true;
				}
			}
			return false;
		}

		if (node.isObject()) {
			if (node.isEmpty()) {
				return false;
			}
			for (Iterator<JsonNode> it = node.elements(); it.hasNext();) {
				if (isCategoryDefined(it.next())) {
					return true;
				}
			}
			return false;
		}

		return false;
	}

	private void validateConfigurationRequest(ServiceCatalogueConfigurationRequestDTO request) {

		if (request == null) {

			throw new SPRuntimeError("Configuration request is required", HttpStatus.BAD_REQUEST);
		}

		if (request.getName() == null || request.getName().trim().isEmpty()) {

			throw new SPRuntimeError("Configuration name is required", HttpStatus.BAD_REQUEST);
		}

		if (request.getServiceIds() == null || request.getServiceIds().isEmpty()) {

			throw new SPRuntimeError("At least one service is required", HttpStatus.BAD_REQUEST);
		}
	}

	private List<Integer> normalizeServiceIds(List<Integer> serviceIds) {

		if (serviceIds == null) {

			return Collections.emptyList();
		}

		return new ArrayList<>(
				new LinkedHashSet<>(serviceIds.stream().filter(Objects::nonNull).collect(Collectors.toList())));
	}

	private JsonNode createDefaultConfigurationData() {

		ObjectNode root = mapper.createObjectNode();

		ObjectNode beneficiaryProfile = mapper.createObjectNode();

		beneficiaryProfile.put("enabled", false);

		root.set("beneficiaryProfile", beneficiaryProfile);

		return root;
	}
	
	private void pushConfigurationToChatbot(ServiceCatalogueConfiguration configuration, Long serviceDefinerId,
			String action) {

		try {
			List<ServiceCatalogueServiceMapping> mappings = mappingRepository
					.findByConfigurationId(configuration.getId());

			List<Integer> serviceIds = mappings.stream().map(mapping -> mapping.getServiceId().intValue())
					.collect(Collectors.toList());

			List<ServiceDetailDTO> serviceDetails = getServiceDetails(serviceIds, serviceDefinerId);

			ServiceCatalogueChatbotRequestDTO request = new ServiceCatalogueChatbotRequestDTO();

			request.setStatus(ServiceCatalogueConfigurationStatus.fromValue(configuration.getStatus()).name());
			request.setServices(serviceDetails);

			if (configuration.getData() != null) {
				request.setData(mapper.readTree(configuration.getData().toString()));
			} else {
				request.setData(mapper.createObjectNode());
			}

			spaAiChatbotFeignClient.updateServiceCatalogueConfiguration(request);

		} catch (Exception ex) {

			serviceMetadataLogger.error("Error while pushing service catalogue configuration to SPAI Chatbot. "
					+ "configurationId: {}, action: {}", configuration.getId(), action, ex);
			ex.printStackTrace();
		}
	}


	private ServiceCatalogueConfigurationResponseDTO buildConfigurationResponseDTO(
			ServiceCatalogueConfiguration configuration, List<ServiceDetailDTO> serviceDetails, boolean includeData) {

		ServiceCatalogueConfigurationResponseDTO response = new ServiceCatalogueConfigurationResponseDTO();
		response.setConfigurationId(configuration.getId());
		response.setConfigurationCode(configuration.getConfigurationCode());
		response.setName(configuration.getName());
		response.setDescription(configuration.getDescription());
		response.setStatusName(ServiceCatalogueConfigurationStatus.fromValue(configuration.getStatus()).name());
		response.setServices(serviceDetails != null ? serviceDetails : new ArrayList<>());
		if (includeData) {
			response.setData(configuration.getData() != null ? configuration.getData() : mapper.createObjectNode());
		}
		return response;
	}

	private Long getServiceDefinerId(UserSessionDTO userSessionDetails) {

		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {

			throw new SPRuntimeError("Invalid user session", HttpStatus.BAD_REQUEST);
		}

		return userSessionDetails.getUserID();
	}

	private boolean contains(String value, String search) {

		return value != null && value.toLowerCase().contains(search);
	}

	private String generateConfigurationCode() {
		return "SC-" + System.currentTimeMillis();
	}
	
	
	
	
	    @Transactional(readOnly = true)
    public ServiceCatalogueDataDTO buildServiceCatalogueData(Integer serviceId) {
        if (serviceId == null) {
            throw new SPRuntimeError("Service id is required", HttpStatus.BAD_REQUEST);
        }

        ServiceDefinition sd = serviceDefinitionRepository.findById(serviceId)
                .orElseThrow(() -> new SPRuntimeError("Service not found", HttpStatus.NOT_FOUND));
        ServiceDefinitionDTO def = sd.getDefinitionJson();
        ServiceLog log = serviceLogRepository.findByServiceId(serviceId);

        // Workflow: submission roles + department/state labels
        ServiceProcessFlow flow = processFlowRepository.findByServiceId(serviceId);
        ProcessFlowDTO.NodeDTO start = flow == null || flow.getProcessFlowJson() == null
                || flow.getProcessFlowJson().getNodes() == null ? null
                : flow.getProcessFlowJson().getNodes().stream()
                    .filter(n -> n != null && "submission".equals(n.getType()))
                    .findFirst().orElse(null);

        List<String> modes = new ArrayList<>();
        String department = null;
        String state = null;
        if (start != null && start.getData() != null) {
            if (start.getData().getMode() != null) {
                modes = start.getData().getMode().stream()
                        .filter(m -> m != null && m.getLabel() != null)
                        .map(m -> m.getLabel()).distinct().toList();
            }
            if (start.getData().getEntities() != null) {
                for (ProcessFlowDTO.Entities entity : start.getData().getEntities()) {
                    if (entity == null) continue;
                    if (department == null && entity.getEntity() != null)
                        department = entity.getEntity().getLabel();
                    if (state == null && entity.getClcDetail() != null)
                        state = entity.getClcDetail().getLabel();
                    if (department != null && state != null) break;
                }
            }
        }

        // Fallback if no department/state is configured on the submission node
        if (department == null || state == null) {
            ServiceDeliveryUnitDefinition sdu = serviceDeliveryUnitDefinitionRepository.findByServiceId(serviceId);
            if (sdu != null && sdu.getServiceDeliveryUnits() != null) {
                for (ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON unit : sdu.getServiceDeliveryUnits()) {
                    if (unit == null || unit.getServiceDeliveryUnit() == null) continue;
                    var hierarchy = unit.getServiceDeliveryUnit();
                    if (department == null && hierarchy.getEntity() != null)
                        department = hierarchy.getEntity().getLabel();
                    if (state == null && hierarchy.getClcDetail() != null)
                        state = hierarchy.getClcDetail().getLabel();
                    if (department != null && state != null) break;
                }
            }
        }

        List<String> goals = def == null || def.getGoals() == null ? List.of()
                : def.getGoals().stream()
                    .filter(g -> g != null && g.getLabel() != null)
                    .map(g -> g.getLabel()).toList();

        // Catalogue configuration: all enabled beneficiary fields, not just five
        List<String> availedBy = new ArrayList<>();
        List<ServiceCatalogueDataDTO.Beneficiary> beneficiary = new ArrayList<>();
        Optional<ServiceCatalogueServiceMapping> mapping = mappingRepository.findByServiceId(serviceId.longValue());
        if (mapping.isPresent()) {
            JsonNode configData = configurationRepository.findDataByConfigurationId(
                    mapping.get().getConfigurationId());
            if (configData != null) {
                JsonNode profile = configData.path("beneficiaryProfile");
                if (profile.path("enabled").asBoolean(false)) {
                    JsonNode answers = profile.path("answers");
                    JsonNode applicant = answers.path("applicant_type");
                    if (applicant.path("enabled").asBoolean(false)) {
                        for (JsonNode v : applicant.path("who_can_apply")) {
                            if (v.isTextual() && !v.asText().isBlank()) availedBy.add(v.asText());
                        }
                    }
                    for (java.util.Iterator<Map.Entry<String, JsonNode>> categories = answers.fields(); categories.hasNext();) {
                        JsonNode category = categories.next().getValue();
                        if (!category.path("enabled").asBoolean(false)) continue;
                        for (java.util.Iterator<Map.Entry<String, JsonNode>> fields = category.fields(); fields.hasNext();) {
                            Map.Entry<String, JsonNode> f = fields.next();
                            if ("enabled".equals(f.getKey())) continue;
                            List<String> values = new ArrayList<>();
                            JsonNode value = f.getValue();
                            if (value.isArray()) {
                                value.forEach(v -> { if (v.isValueNode() && !v.asText().isBlank()) values.add(v.asText()); });
                            } else if (value.isObject()) {
                                if ("age_range".equals(f.getKey())
                                        && value.hasNonNull("min_age") && value.hasNonNull("max_age")) {
                                    values.add(value.path("min_age").asText() + "-"
                                            + value.path("max_age").asText() + " yrs");
                                } else if ("dob_range".equals(f.getKey())
                                        && value.hasNonNull("born_after") && value.hasNonNull("born_before")) {
                                    values.add(value.path("born_after").asText() + " to "
                                            + value.path("born_before").asText());
                                } else {
                                    value.fields().forEachRemaining(e -> {
                                        JsonNode v = e.getValue();
                                        if (v.isValueNode() && !v.isNull() && !v.asText().isBlank())
                                            values.add(e.getKey().replace('_', ' ') + ": " + v.asText());
                                    });
                                }
                            } else if (value.isValueNode() && !value.isNull() && !value.asText().isBlank()) {
                                values.add(value.asText());
                            }
                            if (!values.isEmpty()) {
                                String label = "who_can_apply".equals(f.getKey()) ? "Type of Beneficiary"
                                        : java.util.Arrays.stream(f.getKey().split("_"))
                                            .map(w -> w.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + w.substring(1))
                                            .collect(java.util.stream.Collectors.joining(" "));
                                ServiceCatalogueDataDTO.Beneficiary b = new ServiceCatalogueDataDTO.Beneficiary();
                                b.setAttribute(label);
                                b.setValues(values);
                                beneficiary.add(b);
                            }
                        }
                    }
                }
            }
        }

        String deliverable = null;
        List<ServiceOutputFormat> outputs = serviceOutputFormatRepository.findAllDocumentByServiceId(serviceId);
        if (outputs != null) {
            for (ServiceOutputFormat output : outputs) {
                if (output.getOutputFormatJson() != null
                        && output.getOutputFormatJson().getTemplateName() != null) {
                    deliverable = output.getOutputFormatJson().getTemplateName();
                    break;  // New contract supports a single deliverable string
                }
            }
        }

        // Same DTO pattern as the original code: instantiate + setters.
        ServiceCatalogueDataDTO response = new ServiceCatalogueDataDTO();

        ServiceCatalogueDataDTO.ServiceIdentity identity = new ServiceCatalogueDataDTO.ServiceIdentity();
        identity.setTenantId(log == null ? null : log.getTenantId());
        identity.setBaseServiceId(sd.getBaseServiceId());
        identity.setServiceId(sd.getServiceId());
        identity.setDepartmentId(sd.getDepartmentId());
        identity.setServiceName(def != null && def.getName() != null ? def.getName() : sd.getServiceName());
        identity.setDepartment(department);
        identity.setState(state);
        identity.setStateId(sd.getStateId() == null ? null : String.valueOf(sd.getStateId()));
        response.setServiceIdentity(identity);

        ServiceCatalogueDataDTO.ServiceDetails details = new ServiceCatalogueDataDTO.ServiceDetails();
        details.setDescription(def == null ? null : def.getDescription());
        details.setSgdGoals(goals);
        ServiceCatalogueDataDTO.HowToApply how = new ServiceCatalogueDataDTO.HowToApply();
        how.setMode(modes);
        details.setHowToApply(how);
        response.setServiceDetails(details);

        response.setAvailedBy(availedBy);
        response.setBeneficiary(beneficiary);
        response.setApplyModes(modes);

        // Business rule for submissionType not verified yet.
        response.setSubmission(new ServiceCatalogueDataDTO.Submission());

        ServiceCatalogueDataDTO.WhatYouReceive what = new ServiceCatalogueDataDTO.WhatYouReceive();
        what.setDeliverable(deliverable);
        what.setDeliveryModes(new ArrayList<>());
        response.setWhatYouReceive(what);

        // Documents: senior confirmation pending.
        ServiceCatalogueDataDTO.Documents documents = new ServiceCatalogueDataDTO.Documents();
        documents.setItems(new ArrayList<>());
        response.setDocuments(documents);

        return response;
    }
	
	@Transactional(readOnly = true)
	public Map<String, Object> getServiceCatalogue(
	        Integer state,
	        Integer dept,
	        int page,
	        int size,
	        String userDetails) {

	    if (page < 0 || size < 1 || size > 100) {
	        throw new SPRuntimeError(
	                "Invalid pagination parameters",
	                HttpStatus.BAD_REQUEST
	        );
	    }

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("serviceId").ascending()
	    );

	    Page<ServiceDefinition> result =
	            serviceDefinitionRepository.findCataloguePage(
	                    state,
	                    dept == null ? null : dept.longValue(),
	                    List.of(
	                            ServiceStatus.FROZEN.getCode(),
	                            ServiceStatus.LAUNCHED.getCode()
	                    ),
	                    pageable
	            );

	    Map<String, String> serviceKeys = new HashMap<>();

	    // Fetch service keys from existing Processing API
	    if (result.hasContent()
	            && userDetails != null
	            && !userDetails.isBlank()) {

	        List<Map<String, Object>> processingServices =
	                processingFeignClient.getServiceList(userDetails);

	        if (processingServices != null) {

	            System.out.println("Processing service count: "
	                    + processingServices.size());

	            System.out.println("Processing service IDs: "
	                    + processingServices.stream()
	                            .map(s -> s.get("serviceId"))
	                            .toList());
	            
	            System.out.println("Processing IDs: " + serviceKeys.keySet());

	            System.out.println("Missing service IDs: " +
	                result.getContent().stream()
	                    .map(sd -> String.valueOf(sd.getServiceId()))
	                    .filter(id -> !serviceKeys.containsKey(id))
	                    .toList());

	            for (Map<String, Object> service : processingServices) {

	                Object serviceId = service.get("serviceId");
	                Object serviceKey = service.get("serviceKey");

	                if (serviceId != null && serviceKey != null) {

	                    serviceKeys.put(
	                            serviceId.toString(),
	                            serviceKey.toString()
	                    );
	                }
	            }
	           
	            
	            System.out.println("Mapped serviceKey IDs: " + serviceKeys.keySet());

	            System.out.println("Missing service IDs: " +
	                    result.getContent().stream()
	                            .map(sd -> String.valueOf(sd.getServiceId()))
	                            .filter(id -> !serviceKeys.containsKey(id))
	                            .toList());
	        }
	    }

	    // Prepare catalogue data with serviceKey
	    List<Map<String, Object>> data = result.getContent()
	            .stream()
	            .map(sd -> {

	                Map<String, Object> item = toCatalogueItem(sd);

	                item.put(
	                        "serviceKey",
	                        serviceKeys.get(String.valueOf(sd.getServiceId()))
	                );

	                return item;
	            })
	            .toList();

	    // Pagination response
	    Map<String, Object> response = new LinkedHashMap<>();

	    response.put("data", data);
	    response.put("page", result.getNumber());
	    response.put("size", result.getSize());
	    response.put("totalRecords", result.getTotalElements());
	    response.put("totalPages", result.getTotalPages());
	    response.put("hasNext", result.hasNext());

	    return response;
	}
	
	private Map<String, Object> toCatalogueItem(ServiceDefinition sd) {

	    ServiceDefinitionDTO def = sd.getDefinitionJson();

	    Map<String, Object> item = new LinkedHashMap<>();

	    item.put(
	            "service",
	            def != null && def.getName() != null
	                    ? def.getName()
	                    : sd.getServiceName()
	    );

	    item.put("departmentId", sd.getDepartmentId());

	    item.put("department", null);

	    item.put(
	            "description",
	            def != null ? def.getDescription() : null
	    );

	    Map<String, Object> sla = null;

	    if (def != null && def.getServiceLevel() != null) {

	        var level = def.getServiceLevel();

	        sla = new LinkedHashMap<>();

	        sla.put("quantity", level.getQuantity());

	        String unit = level.getUnit() == null
	                ? null
	                : level.getUnit().getValue();

	        sla.put(
	                "unit",
	                "d".equalsIgnoreCase(unit) ? "days" :
	                "h".equalsIgnoreCase(unit) ? "hours" : unit
	        );
	    }

	    item.put("sla", sla);

	    item.put("fee", null);

	    item.put("serviceId", String.valueOf(sd.getServiceId()));

	    return item;
	}
	
}
