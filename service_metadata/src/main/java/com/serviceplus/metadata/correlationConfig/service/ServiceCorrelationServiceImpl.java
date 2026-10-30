package com.serviceplus.metadata.correlationConfig.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.correlationConfig.dto.ConfigureDTO;
import com.serviceplus.metadata.correlationConfig.dto.CorrelationPrimaryDTO;
import com.serviceplus.metadata.correlationConfig.dto.ServiceCorrelationRequestDTO;
import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfiguration;
import com.serviceplus.metadata.correlationConfig.enums.SetType;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationRepository;
import com.serviceplus.metadata.correlationmapping.dto.CorrelationConfigurationResponse;
import com.serviceplus.metadata.correlationmapping.repository.ExternalDataMappingRepository;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;

import jakarta.transaction.Transactional;

@Service
public class ServiceCorrelationServiceImpl implements ServiceCorrelationService {

	private static final Logger LOGGER = LogManager.getLogger(ServiceCorrelationServiceImpl.class);
	private final IProcessFlowRepository processFlowRepository;
	private  final IServiceDefinitionRepository serviceDefinitionRepository;
	private final ICorrelationConfigurationRepository configurationRepository;
	private final ExternalDataMappingRepository externalDataMappingRepository;
	private final com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService;

	public ServiceCorrelationServiceImpl(IProcessFlowRepository processFlowRepository,
			ICorrelationConfigurationRepository configurationRepository, IServiceDefinitionRepository serviceDefinitionRepository,
			ExternalDataMappingRepository externalDataMappingRepository,
			com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService) {
		super();
		this.processFlowRepository = processFlowRepository;
		this.configurationRepository = configurationRepository;
		this.serviceDefinitionRepository = serviceDefinitionRepository;
		this.externalDataMappingRepository = externalDataMappingRepository;
		this.serviceVersionTokenService = serviceVersionTokenService;
	}

	@Override
	@Transactional
	public ResponseEntity<?> save(ServiceCorrelationRequestDTO requestDTO, UserSessionDTO userSessionDetails) {

		try {

			LOGGER.info("Service : Request received to save/update service correlation configuration");
			Integer serviceId = requestDTO.getServiceId();
			
			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
			if(serviceDefinition == null) {				
				throw new SPRuntimeError("Service Not Defined.", HttpStatus.BAD_REQUEST);
			}
			
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
			if (serviceProcessFlow == null) {
				throw new SPRuntimeError("Service Process Flow Not Defined.", HttpStatus.BAD_REQUEST);
			}
			
			int attrSize = requestDTO.getIdentifierConfigure().getInputAttributes().size();
			if(attrSize < 1) {
				throw new SPRuntimeError( "Minimum Attributes Size is 1.", HttpStatus.BAD_REQUEST);
			}else if(attrSize > 5) {
				throw new SPRuntimeError("Maximum Attributes Size is 5.", HttpStatus.BAD_REQUEST);
			}
			CorrelationConfiguration correlationConfiguration = configurationRepository.findByServiceIdAndSetName(serviceId, requestDTO.getSetName()).orElse(null);
			if(correlationConfiguration != null && !requestDTO.getSetId().equals(correlationConfiguration.getId())) {
				throw new SPRuntimeError( "Set name is alread present into the service.", HttpStatus.BAD_REQUEST);
			}
			CorrelationConfiguration configuration;
			ConfigureDTO config;
			ServiceCorrelationRequestDTO oldDto = null;
			if (requestDTO.getSetId() == null) {
				configuration = new CorrelationConfiguration();
				configuration.setServiceId(serviceProcessFlow.getServiceId());
				configuration.setBaseServiceId(serviceProcessFlow.getBaseServiceId());
				configuration.setServiceName(serviceDefinition.getServiceName());

				configuration.setSetType((SetType.SERVICE.getValue()));
				configuration.setEntityId(userSessionDetails.getEntityId());
				configuration.setEntityName(userSessionDetails.getEntityLevelName());
				configuration.setTenantId(userSessionDetails.getTenantId());
				configuration.setCrBy(userSessionDetails.getUserID());
				
				config = new ConfigureDTO();

			} else {
				configuration = configurationRepository.findById(requestDTO.getSetId()).orElseThrow(() -> new SPRuntimeError("Data Not Found", HttpStatus.BAD_REQUEST));
				config = configuration.getConfig();
				oldDto = new ServiceCorrelationRequestDTO();
				oldDto.setSetId(configuration.getId());
				oldDto.setServiceId(configuration.getServiceId());
				oldDto.setSetName(configuration.getSetName());
				oldDto.setSecurityKey(configuration.getSecurityKey());
				if (config != null) {
					oldDto.setIdentifierConfigure(config.getServiceIdentifierConfig());
				}
			}

			config.setServiceIdentifierConfig(requestDTO.getIdentifierConfigure());
			configuration.setConfig(config);
			
			configuration.setUpBy(userSessionDetails.getUserID());
			configuration.setSetName(requestDTO.getSetName());
			configuration.setSecurityKey(requestDTO.getSecurityKey());

			config.setServiceIdentifierConfig(requestDTO.getIdentifierConfigure());
			
			configuration = configurationRepository.save(configuration);

			boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
			if (isMinorVersion) {
				serviceVersionTokenService.recordCorrelationChangeLog(
						serviceId,
						oldDto == null ? "CREATE" : "UPDATE",
						configuration.getSetName(),
						oldDto,
						requestDTO,
						userSessionDetails
				);
			}

			return ResponseEntity.ok(Map.of("setId", configuration.getId(), "message", "Correlation Identifier saved successfully."));

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Service : Error while saving/updating service correlation configuration", ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<?> delete(Integer id, UserSessionDTO userSessionDetails) {

		try {
			LOGGER.info("Service : Request received to delete service correlation configuration, id: {}", id);

			CorrelationConfiguration configuration = configurationRepository.findById(id).orElseThrow(() -> new SPRuntimeError("Data Not Found", HttpStatus.BAD_REQUEST));
			Integer dataSetId = configuration.getId();
			boolean isDataSetUsed = externalDataMappingRepository.existsByDataSetId(dataSetId);

            if (isDataSetUsed) {
                LOGGER.warn("Cannot delete correlation configuration. dataSetId={} is used in external data mapping.", dataSetId);
                throw new SPRuntimeError( "This data set is already used in External Data Mapping and cannot be deleted.", HttpStatus.BAD_REQUEST);
            }
	            
			Integer sId = configuration.getServiceId();
			String sName = configuration.getSetName();
			configurationRepository.delete(configuration);

			boolean isMinorVersion = serviceVersionTokenService.isMinorModification(sId);
			if (isMinorVersion) {
				serviceVersionTokenService.recordCorrelationChangeLog(
						sId,
						"DELETE",
						sName,
						null,
						null,
						userSessionDetails
				);
			}

			return ResponseEntity.ok(Map.of("message", "Correlation Identifier deleted successfully."));

		} catch (Exception ex) {

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			LOGGER.error("Service : Error while deleting service correlation configuration, id: {}", id, ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getAll(UserSessionDTO userSessionDetails,Integer serviceId, int page, int size) {

		try {

			LOGGER.info(
					"Service : Request received to fetch all service correlation configurations, tenantId: {}, page: {}, size: {}",
					userSessionDetails.getTenantId(), page, size);

			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

			Page<CorrelationConfiguration> configurationPage = configurationRepository
					.findSetNamesByTenantIdAndSetType(userSessionDetails.getTenantId(),"S", serviceId, pageable);

			List<CorrelationConfigurationResponse> responseList = configurationPage.getContent().stream()
					.map(configuration -> {

						CorrelationConfigurationResponse response = new CorrelationConfigurationResponse();

						response.setId(configuration.getId());
						response.setSetName(configuration.getSetName());
						response.setSetType("SERVICE");
						response.setServiceName(configuration.getServiceName());

						return response;

					}).toList();

			Map<String, Object> response = new HashMap<>();

			response.put("content", responseList);
			response.put("page", configurationPage.getNumber());
			response.put("size", configurationPage.getSize());
			response.put("totalElements", configurationPage.getTotalElements());
			response.put("totalPages", configurationPage.getTotalPages());
			response.put("first", configurationPage.isFirst());
			response.put("last", configurationPage.isLast());

			LOGGER.info("Service : Successfully fetched {} service correlation configurations for tenantId: {}",
					configurationPage.getNumberOfElements(), userSessionDetails.getTenantId());

			return ResponseEntity.ok(response);

		} catch (Exception ex) {

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			LOGGER.error("Service : Error while fetching all service correlation configurations for tenantId: {}",
					userSessionDetails.getTenantId(), ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getById(Integer id, UserSessionDTO userSessionDetails) {

		try {

			LOGGER.info("Service : Request received to fetch service correlation configuration, id: {}", id);

			CorrelationConfiguration configuration = configurationRepository.findById(id).orElseThrow(() -> new SPRuntimeError("Invalid Id", HttpStatus.BAD_REQUEST));

			ServiceCorrelationRequestDTO response = new ServiceCorrelationRequestDTO();

			response.setSetId(configuration.getId());
			response.setSetName(configuration.getSetName());
			response.setSecurityKey(configuration.getSecurityKey());
			response.setServiceId(configuration.getServiceId());

			ConfigureDTO config = configuration.getConfig();

			if (config != null) {
				response.setIdentifierConfigure(config.getServiceIdentifierConfig());
			}

			LOGGER.info("Service : Successfully fetched service correlation configuration, id: {}", id);

			return ResponseEntity.ok(response);

		} catch (Exception ex) {

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			LOGGER.error("Service : Error while fetching service correlation configuration, id: {}", id, ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getAllBasedOnClcAndTenant(UserSessionDTO userSessionDetails, int page, int size) {
		try {
			LOGGER.info("Controller : Request received to fetch all service correlation configurations based on Clc and Tenant");
			List<CorrelationConfiguration> correlationConfigurations = configurationRepository.findByEntityIdAndTenantId(userSessionDetails.getEntityId(), userSessionDetails.getTenantId()).orElse(new ArrayList<CorrelationConfiguration>());
			List<CorrelationPrimaryDTO> primaryDTOs = new ArrayList<CorrelationPrimaryDTO>();
			for(CorrelationConfiguration correlationConfiguration :correlationConfigurations) {
				CorrelationPrimaryDTO dto = new CorrelationPrimaryDTO(
						correlationConfiguration.getId(),correlationConfiguration.getSetName(),
						correlationConfiguration.getServiceName(), correlationConfiguration.getSetType(),
						correlationConfiguration.getEntityName());
				primaryDTOs.add(dto);
			}
			return ResponseEntity.ok().body(primaryDTOs);
		} catch (Exception ex) {
			
			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}
			
			LOGGER.error("Service : Error while fetching all service correlation configurations based on Clc and Tenant", ex);
			
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}