package com.serviceplus.metadata.correlationmapping.approval.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.serviceplus.metadata.correlationConfig.approval.dto.ApprovalDetailsRequestDTO;
import com.serviceplus.metadata.correlationConfig.approval.dto.ApprovalDetailsResponseDTO;
import com.serviceplus.metadata.correlationConfig.approval.dto.ExternalDataMappingApprovalRequestDTO;
import com.serviceplus.metadata.correlationConfig.approval.dto.ExternalDataMappingApprovalResponseDTO;
import com.serviceplus.metadata.correlationConfig.dto.ConfigureDTO;
import com.serviceplus.metadata.correlationConfig.dto.CorrelationFieldDTO;
import com.serviceplus.metadata.correlationConfig.dto.IdentifierConfigure;
import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfiguration;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationRepository;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.entity.ExternalDataMapping;
import com.serviceplus.metadata.correlationmapping.enums.ApprovalStatus;
import com.serviceplus.metadata.correlationmapping.enums.ApprovalStatusCode;
import com.serviceplus.metadata.correlationmapping.enums.SourceType;
import com.serviceplus.metadata.correlationmapping.repository.ExternalDataMappingRepository;
import com.serviceplus.metadata.dto.UserProfile;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class ExternalDataMappingApprovalService {
	private static final Logger LOGGER = LogManager.getLogger(ExternalDataMappingApprovalService.class);

	private final ExternalDataMappingRepository externalDataMappingRepository;

	private final ICorrelationConfigurationRepository correlationSetConfigRepository;
	private final UserService userService;

	Map<Long, UserProfile> usersMap = new HashMap<Long, UserProfile>();

	public ExternalDataMappingApprovalService(ExternalDataMappingRepository externalDataMappingRepository,
			ICorrelationConfigurationRepository correlationSetConfigRepository, UserService userService) {
		super();
		this.externalDataMappingRepository = externalDataMappingRepository;
		this.correlationSetConfigRepository = correlationSetConfigRepository;
		this.userService = userService;
	}

	public ResponseEntity<?> getDataSetForApproval(UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Get Data Set For Approval - START");

		try {

			String tenantId = userSessionDetails.getTenantId();

			Integer entityId = userSessionDetails.getEntityId();

			List<ExternalDataMapping> mappings = externalDataMappingRepository
					.findByTenantIdAndEntityId(tenantId, entityId).orElse(Collections.emptyList());

			if (mappings.isEmpty()) {
				return ResponseEntity.ok(Collections.emptyList());
			}

			List<CorrelationConfiguration> correlationConfigurations = correlationSetConfigRepository
					.findByTenantId(tenantId).orElse(Collections.emptyList());

			List<ExternalDataMappingApprovalResponseDTO> response = new ArrayList<>();

			for (ExternalDataMapping mapping : mappings) {

				ExternalDataMappingRequestDTO mappingDTO = mapping.getDto();

				if (mappingDTO == null || mappingDTO.getDataSetSources() == null) {
					continue;
				}

				if (mappingDTO.getDataSetSources().getService() != null && !mappingDTO.getDataSetSources().getService().isEmpty()) {

					for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : mappingDTO.getDataSetSources()
							.getService()) {

						if (service == null) {
							continue;
						}

						if (!isPending(service.getStatus())) {
							continue;
						}

						ExternalDataMappingApprovalResponseDTO serviceResponse = prepareServiceApprovalResponse(mapping,
								service, correlationConfigurations);

						if (serviceResponse != null) {
							response.add(serviceResponse);
						}
					}
				}

				else if (mappingDTO.getDataSetSources().getExternal() != null && !mappingDTO.getDataSetSources().getExternal().isEmpty()) {

					for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : mappingDTO.getDataSetSources()
							.getExternal()) {

						if (external == null) {
							continue;
						}

						if (!isPending(external.getStatus())) {
							continue;
						}

						ExternalDataMappingApprovalResponseDTO externalResponse = prepareExternalApprovalResponse(
								mapping, external, correlationConfigurations);

						if (externalResponse != null) {
							response.add(externalResponse);
						}
					}
				}
			}

			LOGGER.info("Service : Get Data Set For Approval - END | count={}", response.size());

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Service : Get Data Set For Approval - Exception occurred", ex);

			throw new SPRuntimeError("Something went wrong while fetching approval data",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ExternalDataMappingApprovalResponseDTO prepareServiceApprovalResponse(ExternalDataMapping mapping,
			ExternalDataMappingRequestDTO.ServiceDataSetDTO service,
			List<CorrelationConfiguration> correlationConfigurations) {

		if (service.getServiceId() == null) {
			return null;
		}

		Integer serviceId;

		try {

			serviceId = Math.toIntExact(service.getServiceId());

		} catch (ArithmeticException ex) {

			LOGGER.warn("Invalid serviceId={} for mappingId={}", service.getServiceId(), mapping.getId());

			return null;
		}

		List<CorrelationConfiguration> serviceSets = correlationConfigurations.stream().filter(Objects::nonNull)
				.filter(config -> config.getServiceId() != null)
				.filter(config -> config.getServiceId().equals(serviceId))
				.filter(config -> SourceType.SERVICE.getValue().equalsIgnoreCase(config.getSetType()))
				.collect(Collectors.toList());

		if (serviceSets.isEmpty()) {
			return null;
		}

		ExternalDataMappingApprovalResponseDTO response = new ExternalDataMappingApprovalResponseDTO();

		response.setId(mapping.getId());

		response.setSourceType(service.getSourceType());

		response.setServiceId(service.getServiceId());

		response.setBaseServiceId(service.getBaseServiceId());

		response.setServiceName(service.getServiceName());

		
		response.setReqUserId(mapping.getCrBy());

		response.setReqUserName(getRequestUserName(mapping.getCrBy(), usersMap));

		response.setReqUserDepartmentName(getUserDepartmentName(service.getApprovedBy()));

		response.setSetId(null);
		response.setSetName(null);

		for (CorrelationConfiguration configuration : serviceSets) {

			ExternalDataMappingApprovalResponseDTO.SetApprovalDTO setResponse = prepareSetApprovalResponse(
					configuration, service.getApprovedSets());

			response.getSets().add(setResponse);
		}

		return response;
	}

	private ExternalDataMappingApprovalResponseDTO prepareExternalApprovalResponse(ExternalDataMapping mapping,
			ExternalDataMappingRequestDTO.ExternalDataSetDTO external,
			List<CorrelationConfiguration> correlationConfigurations) {

		if (external.getSetId() == null) {
			return null;
		}

		Integer setId;

		try {

			setId = Math.toIntExact(external.getSetId());

		} catch (ArithmeticException ex) {

			LOGGER.warn("Invalid setId={} for mappingId={}", external.getSetId(), mapping.getId());

			return null;
		}

		Optional<CorrelationConfiguration> configurationOptional = correlationConfigurations.stream()
				.filter(Objects::nonNull).filter(config -> config.getId() != null)
				.filter(config -> config.getId().equals(setId))
				.filter(config -> SourceType.EXTERNAL_DATA_SET.getValue().equalsIgnoreCase(config.getSetType()))
				.findFirst();

		if (!configurationOptional.isPresent()) {
			return null;
		}

		CorrelationConfiguration configuration = configurationOptional.get();

		ExternalDataMappingApprovalResponseDTO response = new ExternalDataMappingApprovalResponseDTO();

		response.setId(mapping.getId());

		response.setSourceType(external.getSourceType());

		response.setServiceId(null);

		response.setBaseServiceId(null);

		response.setServiceName(null);

		response.setReqUserName(getRequestUserName(mapping.getCrBy(), usersMap));

		response.setReqUserName(getRequestUserName(mapping.getCrBy(), usersMap));

		response.setReqUserId(mapping.getCrBy());

		response.setSetId(external.getSetId());

		response.setSetName(external.getSetName());

		ExternalDataMappingApprovalResponseDTO.SetApprovalDTO setResponse = prepareSetApprovalResponse(configuration,
				external.getApprovedSets());

		response.getSets().add(setResponse);

		return response;
	}

	private ExternalDataMappingApprovalResponseDTO.SetApprovalDTO prepareSetApprovalResponse(
			CorrelationConfiguration configuration, List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets) {

		ExternalDataMappingApprovalResponseDTO.SetApprovalDTO setResponse = new ExternalDataMappingApprovalResponseDTO.SetApprovalDTO();

		setResponse.setSetId(configuration.getId() != null ? configuration.getId().longValue() : null);

		setResponse.setSetName(configuration.getSetName());

		ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet = findApprovedSet(configuration.getId(), approvedSets);

		setResponse.setApproved(approvedSet != null);

		if (configuration.getConfig() == null || configuration.getConfig().getServiceIdentifierConfig() == null
				|| configuration.getConfig().getServiceIdentifierConfig().getOutputAttributes() == null) {

			return setResponse;
		}

		List<IdentifierConfigure.OutputAttributes> outputAttributes = configuration.getConfig()
				.getServiceIdentifierConfig().getOutputAttributes();

		for (IdentifierConfigure.OutputAttributes outputAttribute : outputAttributes) {

			if (outputAttribute == null) {
				continue;
			}

			ExternalDataMappingApprovalResponseDTO.OutputSetApprovalDTO outputResponse = new ExternalDataMappingApprovalResponseDTO.OutputSetApprovalDTO();

			outputResponse.setId(outputAttribute.getOutputSetid());

			outputResponse.setName(outputAttribute.getName());

			outputResponse.setApproved(isOutputSetApproved(outputAttribute.getOutputSetid(), approvedSet));

			setResponse.getOutputSet().add(outputResponse);
		}

		return setResponse;
	}

	private ExternalDataMappingRequestDTO.ApprovedSetDTO findApprovedSet(Integer setId,
			List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets) {

		if (setId == null || approvedSets == null || approvedSets.isEmpty()) {

			return null;
		}

		for (ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet : approvedSets) {

			if (approvedSet == null || approvedSet.getSetId() == null) {
				continue;
			}

			if (approvedSet.getSetId().equals(setId.longValue())) {

				return approvedSet;
			}
		}

		return null;
	}

	private boolean isOutputSetApproved(String outputSetId, ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet) {

		if (outputSetId == null || approvedSet == null || approvedSet.getOutputSet() == null) {

			return false;
		}

		for (ExternalDataMappingRequestDTO.OutputSetDTO outputSet : approvedSet.getOutputSet()) {

			if (outputSet == null || outputSet.getId() == null) {
				continue;
			}

			if (outputSetId.equals(outputSet.getId())) {

				return true;
			}
		}

		return false;
	}

	private boolean isPending(Integer status) {
		return status != null && status.equals(ApprovalStatus.PENDING.getCode());
	}

	private String getRequestUserName(Long userId, Map<Long, UserProfile> usersMap) {

		if (userId == null) {
			return null;
		}

		UserProfile userProfile;

		if (usersMap.containsKey(userId)) {

			userProfile = usersMap.get(userId);

		} else {

			userProfile = userService.fetchUserProfile(userId);

			if (userProfile == null) {

				LOGGER.info("User detail not found, userId :{}", userId);

				return null;
			}

			usersMap.put(userId, userProfile);
		}

		if (userProfile.getData() == null || userProfile.getData().getProfile() == null) {

			return null;
		}

		return userProfile.getData().getProfile().getU_userName();
	}

	public String getUserDepartmentName(Long userId) {
		return "";

	}

	public ResponseEntity<?> getApprovalDetails(ApprovalDetailsRequestDTO requestDTO,
			UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Get Approval Details - START");

		try {

			if (userSessionDetails == null || userSessionDetails.getTenantId() == null
					|| userSessionDetails.getEntityId() == null) {

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			validateApprovalDetailsRequest(requestDTO);

			String source = requestDTO.getSource().trim();

			List<CorrelationConfiguration> configurations = correlationSetConfigRepository
					.findByTenantId(userSessionDetails.getTenantId()).orElse(Collections.emptyList());

			if (configurations.isEmpty()) {

				return ResponseEntity.ok(Collections.emptyList());
			}

			if (SourceType.SERVICE.getValue().equalsIgnoreCase(source)) {

				return ResponseEntity.ok(getServiceApprovalDetails(requestDTO, configurations));
			}

			if (SourceType.EXTERNAL_DATA_SET.getValue().equalsIgnoreCase(source)) {

				return ResponseEntity.ok(getExternalApprovalDetails(requestDTO, configurations));
			}

			throw new SPRuntimeError("Invalid sourceType: " + source, HttpStatus.BAD_REQUEST);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Service : Get Approval Details - Exception occurred", ex);

			throw new SPRuntimeError("Something went wrong while fetching approval details",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateApprovalDetailsRequest(ApprovalDetailsRequestDTO requestDTO) {

		if (requestDTO == null) {

			throw new SPRuntimeError("Request can't be null", HttpStatus.BAD_REQUEST);
		}

		if (requestDTO.getSource() == null || requestDTO.getSource().trim().isEmpty()) {

			throw new SPRuntimeError("source can't be null or empty", HttpStatus.BAD_REQUEST);
		}

		String source = requestDTO.getSource().trim();

		if (SourceType.SERVICE.getValue().equalsIgnoreCase(source)) {

			if (requestDTO.getServiceId() == null) {

				throw new SPRuntimeError("serviceId is required for source S", HttpStatus.BAD_REQUEST);
			}

		} else if (SourceType.EXTERNAL_DATA_SET.getValue().equalsIgnoreCase(source)) {

			if (requestDTO.getSetId() == null) {

				throw new SPRuntimeError("setId is required for source E", HttpStatus.BAD_REQUEST);
			}

		} else {

			throw new SPRuntimeError("Invalid source: " + source, HttpStatus.BAD_REQUEST);
		}

		if (requestDTO.getSets() == null || requestDTO.getSets().isEmpty()) {

			throw new SPRuntimeError("sets can't be empty", HttpStatus.BAD_REQUEST);
		}

		for (ApprovalDetailsRequestDTO.ApprovalSetRequestDTO set : requestDTO.getSets()) {

			if (set == null || set.getSetId() == null) {

				throw new SPRuntimeError("setId can't be null", HttpStatus.BAD_REQUEST);
			}
		}
	}

	private List<ApprovalDetailsResponseDTO> getServiceApprovalDetails(ApprovalDetailsRequestDTO requestDTO,
			List<CorrelationConfiguration> configurations) {

		Integer serviceId;

		try {

			serviceId = Math.toIntExact(requestDTO.getServiceId());

		} catch (ArithmeticException ex) {

			throw new SPRuntimeError("Invalid serviceId: " + requestDTO.getServiceId(), HttpStatus.BAD_REQUEST);
		}

		Set<Long> requestedSetIds = requestDTO.getSets().stream().filter(Objects::nonNull)
				.map(ApprovalDetailsRequestDTO.ApprovalSetRequestDTO::getSetId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<CorrelationConfiguration> matchingConfigurations = configurations.stream().filter(Objects::nonNull)
				.filter(config -> config.getServiceId() != null)
				.filter(config -> config.getServiceId().equals(serviceId))
				.filter(config -> SourceType.SERVICE.getValue().equalsIgnoreCase(config.getSetType()))
				.filter(config -> config.getId() != null)
				.filter(config -> requestedSetIds.contains(config.getId().longValue())).collect(Collectors.toList());

		return buildApprovalDetailsResponse(requestDTO, matchingConfigurations);
	}

	private List<ApprovalDetailsResponseDTO> getExternalApprovalDetails(ApprovalDetailsRequestDTO requestDTO,
			List<CorrelationConfiguration> configurations) {

		Set<Long> requestedSetIds = requestDTO.getSets().stream().filter(Objects::nonNull)
				.map(ApprovalDetailsRequestDTO.ApprovalSetRequestDTO::getSetId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<CorrelationConfiguration> matchingConfigurations = configurations.stream().filter(Objects::nonNull)
				.filter(config -> config.getId() != null)
				.filter(config -> requestedSetIds.contains(config.getId().longValue()))
				.filter(config -> SourceType.EXTERNAL_DATA_SET.getValue().equalsIgnoreCase(config.getSetType()))
				.collect(Collectors.toList());

		return buildApprovalDetailsResponse(requestDTO, matchingConfigurations);
	}

	private List<ApprovalDetailsResponseDTO> buildApprovalDetailsResponse(ApprovalDetailsRequestDTO requestDTO,
			List<CorrelationConfiguration> configurations) {

		List<ApprovalDetailsResponseDTO> response = new ArrayList<>();

		Map<Long, ApprovalDetailsRequestDTO.ApprovalSetRequestDTO> requestedSetsMap = requestDTO.getSets().stream()
				.filter(Objects::nonNull).filter(set -> set.getSetId() != null)
				.collect(Collectors.toMap(ApprovalDetailsRequestDTO.ApprovalSetRequestDTO::getSetId,
						Function.identity(), (first, second) -> first));

		for (CorrelationConfiguration configuration : configurations) {

			if (configuration == null || configuration.getId() == null) {
				continue;
			}

			Long setId = configuration.getId().longValue();

			ApprovalDetailsRequestDTO.ApprovalSetRequestDTO requestedSet = requestedSetsMap.get(setId);

			if (requestedSet == null) {
				continue;
			}

			ApprovalDetailsResponseDTO dto = new ApprovalDetailsResponseDTO();

			dto.setSetId(setId);

			dto.setSetName(configuration.getSetName());

			prepareInputAttributes(configuration, dto);

			prepareOutputAttributes(configuration, requestedSet, dto);

			response.add(dto);
		}

		return response;
	}

	private void prepareInputAttributes(CorrelationConfiguration configuration, ApprovalDetailsResponseDTO response) {

		if (configuration.getConfig() == null || configuration.getConfig().getServiceIdentifierConfig() == null
				|| configuration.getConfig().getServiceIdentifierConfig().getInputAttributes() == null) {

			return;
		}

		List<CorrelationFieldDTO> inputAttributes = configuration.getConfig().getServiceIdentifierConfig()
				.getInputAttributes();

		for (CorrelationFieldDTO field : inputAttributes) {

			if (field == null) {
				continue;
			}

			String label = field.getAttributeLabel();

			if (label != null && !label.trim().isEmpty()) {

				response.getInputAttributes().add(label);
			}
		}
	}

	private void prepareOutputAttributes(CorrelationConfiguration configuration,
			ApprovalDetailsRequestDTO.ApprovalSetRequestDTO requestedSet, ApprovalDetailsResponseDTO response) {

		if (configuration.getConfig() == null || configuration.getConfig().getServiceIdentifierConfig() == null
				|| configuration.getConfig().getServiceIdentifierConfig().getOutputAttributes() == null) {

			return;
		}

		List<IdentifierConfigure.OutputAttributes> outputAttributes = configuration.getConfig()
				.getServiceIdentifierConfig().getOutputAttributes();

		Set<String> requestedOutputSetIds = requestedSet.getOutputSetIds().stream().filter(Objects::nonNull)
				.filter(id -> !id.trim().isEmpty()).collect(Collectors.toSet());

		boolean returnAll = requestedOutputSetIds.isEmpty();

		for (IdentifierConfigure.OutputAttributes outputAttribute : outputAttributes) {

			if (outputAttribute == null) {
				continue;
			}

			String outputSetId = outputAttribute.getOutputSetid();

			if (!returnAll && !requestedOutputSetIds.contains(outputSetId)) {

				continue;
			}

			ApprovalDetailsResponseDTO.OutputAttributeDTO outputResponse = new ApprovalDetailsResponseDTO.OutputAttributeDTO();

			outputResponse.setName(outputAttribute.getName());

			outputResponse.setOutputSetid(outputSetId);

			if (outputAttribute.getAttributes() != null) {

				for (CorrelationFieldDTO field : outputAttribute.getAttributes()) {

					if (field == null) {
						continue;
					}

					String attributeLabel = field.getAttributeLabel();

					if (attributeLabel != null && !attributeLabel.trim().isEmpty()) {

						outputResponse.getAttributes().add(attributeLabel);
					}
				}
			}

			response.getOutputAttributes().add(outputResponse);
		}
	}

	@Transactional
	public ResponseEntity<?> approveDataSet(ExternalDataMappingApprovalRequestDTO requestDTO,
			UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Dataset Approval - START");

		try {

			if (requestDTO == null) {
				throw new SPRuntimeError("Request can't be null", HttpStatus.BAD_REQUEST);
			}

			String source = requestDTO.getSource();

			if (!"S".equalsIgnoreCase(source) && !"E".equalsIgnoreCase(source)) {

				throw new SPRuntimeError("Invalid source. Allowed values are S or E", HttpStatus.BAD_REQUEST);
			}

			if (requestDTO.getStatus() == null || (requestDTO.getStatus() != ApprovalStatus.APPROVED.getCode()
					&& requestDTO.getStatus() != ApprovalStatus.REJECTED.getCode())) {

				throw new SPRuntimeError("Invalid status. Allowed values are 1 (APPROVED) or 2 (REJECTED)",
						HttpStatus.BAD_REQUEST);
			}

			if (requestDTO.getSets() == null || requestDTO.getSets().isEmpty()) {

				throw new SPRuntimeError("At least one set is required", HttpStatus.BAD_REQUEST);
			}

			if ("S".equalsIgnoreCase(source)) {

				if (requestDTO.getServiceId() == null) {
					throw new SPRuntimeError("serviceId is required for source S", HttpStatus.BAD_REQUEST);
				}

			} else {

				if (requestDTO.getSetId() == null) {
					throw new SPRuntimeError("setId is required for source E", HttpStatus.BAD_REQUEST);
				}
			}

			Optional<List<ExternalDataMapping>> mappingOptional = externalDataMappingRepository
					.findByTenantIdAndEntityId(userSessionDetails.getTenantId(), userSessionDetails.getEntityId());

			if (mappingOptional.isEmpty() || mappingOptional.get().isEmpty()) {

				throw new SPRuntimeError("DataSet mapping not found", HttpStatus.NOT_FOUND);
			}

			ExternalDataMapping mapping = mappingOptional.get().stream()
					.filter(m -> Objects.equals(m.getCrBy(), requestDTO.getReqUserId()))
					.filter(m -> isPendingDataSet(m.getDto(), requestDTO)).findFirst()
					.orElseThrow(() -> new SPRuntimeError("Pending DataSet mapping not found", HttpStatus.NOT_FOUND));


			ExternalDataMappingRequestDTO storedDTO = mapping.getDto();

			if (storedDTO == null || storedDTO.getDataSetSources() == null) {

				throw new SPRuntimeError("Invalid DataSet mapping data", HttpStatus.BAD_REQUEST);
			}


			if ("S".equalsIgnoreCase(source)) {
				validateServiceApproval(requestDTO, storedDTO, mapping, userSessionDetails);
			} else {
				validateExternalApproval(requestDTO, storedDTO, mapping, userSessionDetails);
			}

			replaceApprovedSets(requestDTO, storedDTO);

			if ("S".equalsIgnoreCase(source)) {

				ExternalDataMappingRequestDTO.ServiceDataSetDTO serviceDataSet = findServiceDataSet(storedDTO,
						requestDTO.getServiceId());

				if (serviceDataSet == null) {
					throw new SPRuntimeError("Service mapping not found", HttpStatus.NOT_FOUND);
				}

				serviceDataSet.setStatus(requestDTO.getStatus());
				serviceDataSet.setRemark(requestDTO.getRemark() == null ? "" : requestDTO.getRemark());

				if (requestDTO.getStatus() == ApprovalStatus.APPROVED.getCode()) {

					serviceDataSet.setApprovedBy(userSessionDetails.getUserID());

					serviceDataSet.setApprovedOn(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

				} else {

					serviceDataSet.setApprovedBy(null);
					serviceDataSet.setApprovedOn(null);
				}

			} else {

				ExternalDataMappingRequestDTO.ExternalDataSetDTO externalDataSet = findExternalDataSet(storedDTO,
						requestDTO.getSetId());

				if (externalDataSet == null) {
					throw new SPRuntimeError("External DataSet mapping not found", HttpStatus.NOT_FOUND);
				}

				externalDataSet.setStatus(requestDTO.getStatus());
				externalDataSet.setRemark(requestDTO.getRemark() == null ? "" : requestDTO.getRemark());

				if (requestDTO.getStatus() == ApprovalStatus.APPROVED.getCode()) {

					externalDataSet.setApprovedBy(userSessionDetails.getUserID());

					externalDataSet.setApprovedOn(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

				} else {

					externalDataSet.setApprovedBy(null);
					externalDataSet.setApprovedOn(null);
				}
			}

			mapping.setUpBy(userSessionDetails.getUserID());
			mapping.setUpDate(new Date());

			externalDataMappingRepository.save(mapping);

			LOGGER.info("Service : Dataset Approval - SUCCESS, mappingId={}, source={}, status={}", mapping.getId(), source, requestDTO.getStatus());

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("message",
					requestDTO.getStatus() == ApprovalStatus.APPROVED.getCode() ? "DataSet approved successfully"
							: "DataSet rejected successfully");

			response.put("id", mapping.getId());
			ApprovalStatusCode status = ApprovalStatusCode.fromValue(requestDTO.getStatus());
			response.put("status", status);

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError e) {
			throw e;

		} catch (Exception e) {

			LOGGER.error("Service : Dataset Approval - ERROR", e);

			throw new SPRuntimeError("Failed to process DataSet approval", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateServiceApproval(ExternalDataMappingApprovalRequestDTO requestDTO,
			ExternalDataMappingRequestDTO storedDTO, ExternalDataMapping mapping, UserSessionDTO userSessionDetails) {

		Long serviceId = requestDTO.getServiceId();

		ExternalDataMappingRequestDTO.ServiceDataSetDTO storedService = findServiceDataSet(storedDTO, serviceId);

		if (storedService == null) {
			throw new SPRuntimeError("Service mapping not found for serviceId: " + serviceId, HttpStatus.BAD_REQUEST);
		}

		if (!Objects.equals(mapping.getCrBy(), requestDTO.getReqUserId())) {
			throw new SPRuntimeError("Requester does not match mapping creator", HttpStatus.FORBIDDEN);
		}

		List<CorrelationConfiguration> configurations = correlationSetConfigRepository
				.findByTenantId(userSessionDetails.getTenantId()).orElse(Collections.emptyList());

		List<CorrelationConfiguration> serviceConfigs = configurations.stream().filter(Objects::nonNull)
				.filter(c -> Objects.equals(c.getServiceId(), serviceId.intValue()))
				.filter(c -> "S".equalsIgnoreCase(c.getSetType()))
				.filter(c -> Objects.equals(c.getCrBy(), requestDTO.getReqUserId())).collect(Collectors.toList());

		if (serviceConfigs.isEmpty()) {
			throw new SPRuntimeError("No correlation configuration found for serviceId: " + serviceId,HttpStatus.BAD_REQUEST);
		}

		for (ExternalDataMappingApprovalRequestDTO.ApprovalSetDTO requestedSet : requestDTO.getSets()) {

			if (requestedSet == null || requestedSet.getSetId() == null) {

				throw new SPRuntimeError("Invalid setId", HttpStatus.BAD_REQUEST);
			}

			CorrelationConfiguration config = serviceConfigs.stream()
					.filter(c -> Objects.equals(c.getId(), requestedSet.getSetId().intValue())).findFirst()
					.orElse(null);

			if (config == null) {

				throw new SPRuntimeError("Correlation set not found or requester is not owner. " + "serviceId="
						+ serviceId + ", setId=" + requestedSet.getSetId(), HttpStatus.BAD_REQUEST);
			}

			if (StringUtils.hasText(requestedSet.getSetName()) && !Objects.equals(requestedSet.getSetName(), config.getSetName())) {
				throw new SPRuntimeError("Invalid setName for setId: " + requestedSet.getSetId(), HttpStatus.BAD_REQUEST);
			}

			validateOutputSets(config, requestedSet);
		}
	}

	private void validateExternalApproval(ExternalDataMappingApprovalRequestDTO requestDTO,
			ExternalDataMappingRequestDTO storedDTO, ExternalDataMapping mapping, UserSessionDTO userSessionDetails) {

		Long externalSetId = requestDTO.getSetId();

		ExternalDataMappingRequestDTO.ExternalDataSetDTO storedExternal = findExternalDataSet(storedDTO, externalSetId);

		if (storedExternal == null) {

			throw new SPRuntimeError("External DataSet not found for setId: " + externalSetId, HttpStatus.BAD_REQUEST);
		}

		if (!Objects.equals(mapping.getCrBy(), requestDTO.getReqUserId())) {

			throw new SPRuntimeError("Requester does not match mapping creator", HttpStatus.FORBIDDEN);
		}

		List<CorrelationConfiguration> configurations = correlationSetConfigRepository
				.findByTenantId(userSessionDetails.getTenantId()).orElse(Collections.emptyList());

		for (ExternalDataMappingApprovalRequestDTO.ApprovalSetDTO requestedSet : requestDTO.getSets()) {

			if (requestedSet == null || requestedSet.getSetId() == null) {

				throw new SPRuntimeError("Invalid setId", HttpStatus.BAD_REQUEST);
			}

			CorrelationConfiguration config = configurations.stream().filter(Objects::nonNull)
					.filter(c -> Objects.equals(c.getId(), requestedSet.getSetId().intValue()))
					.filter(c -> "E".equalsIgnoreCase(c.getSetType()))
					.filter(c -> Objects.equals(c.getCrBy(), requestDTO.getReqUserId())).findFirst().orElse(null);

			if (config == null) {

				throw new SPRuntimeError(
						"Correlation set not found or requester is not owner. " + "setId=" + requestedSet.getSetId(),
						HttpStatus.BAD_REQUEST);
			}

			if (StringUtils.hasText(requestedSet.getSetName())
					&& !Objects.equals(requestedSet.getSetName(), config.getSetName())) {

				throw new SPRuntimeError("Invalid setName for setId: " + requestedSet.getSetId(),
						HttpStatus.BAD_REQUEST);
			}

			validateOutputSets(config, requestedSet);
		}
	}

	private void validateOutputSets(CorrelationConfiguration config,
			ExternalDataMappingApprovalRequestDTO.ApprovalSetDTO requestedSet) {

		ConfigureDTO configureDTO = config.getConfig();

		if (configureDTO == null || configureDTO.getServiceIdentifierConfig() == null) {

			throw new SPRuntimeError("Invalid correlation configuration for setId: " + config.getId(),
					HttpStatus.BAD_REQUEST);
		}

		IdentifierConfigure identifierConfig = configureDTO.getServiceIdentifierConfig();

		List<IdentifierConfigure.OutputAttributes> outputAttributes = identifierConfig.getOutputAttributes();

		if (outputAttributes == null) {
			outputAttributes = Collections.emptyList();
		}

		Set<String> validOutputSetIds = outputAttributes.stream().filter(Objects::nonNull)
				.map(IdentifierConfigure.OutputAttributes::getOutputSetid).filter(StringUtils::hasText)
				.collect(Collectors.toSet());

		List<ExternalDataMappingApprovalRequestDTO.OutputSetDTO> requestedOutputSets = requestedSet.getOutputSet();

		if (requestedOutputSets == null) {
			return;
		}

		Set<String> requestedIds = new HashSet<>();

		for (ExternalDataMappingApprovalRequestDTO.OutputSetDTO outputSet : requestedOutputSets) {

			if (outputSet == null || !StringUtils.hasText(outputSet.getId())) {

				throw new SPRuntimeError("Invalid outputSet id for correlation setId: " + requestedSet.getSetId(),
						HttpStatus.BAD_REQUEST);
			}

			if (!requestedIds.add(outputSet.getId())) {
				throw new SPRuntimeError("Duplicate outputSetId: " + outputSet.getId(), HttpStatus.BAD_REQUEST);
			}

			if (!validOutputSetIds.contains(outputSet.getId())) {

				throw new SPRuntimeError(
						"Invalid outputSetId: " + outputSet.getId() + " for setId: " + requestedSet.getSetId(),
						HttpStatus.BAD_REQUEST);
			}

			if (StringUtils.hasText(outputSet.getName())) {

				boolean validName = outputAttributes.stream().filter(Objects::nonNull)
						.anyMatch(output -> Objects.equals(output.getOutputSetid(), outputSet.getId())
								&& Objects.equals(output.getName(), outputSet.getName()));

				if (!validName) {

					throw new SPRuntimeError("Invalid outputSet name for outputSetId: " + outputSet.getId(),
							HttpStatus.BAD_REQUEST);
				}
			}
		}
	}

	private void replaceApprovedSets(ExternalDataMappingApprovalRequestDTO requestDTO,
			ExternalDataMappingRequestDTO storedDTO) {

		if ("S".equalsIgnoreCase(requestDTO.getSource())) {

			ExternalDataMappingRequestDTO.ServiceDataSetDTO serviceDataSet = findServiceDataSet(storedDTO,
					requestDTO.getServiceId());

			if (serviceDataSet == null) {
				throw new SPRuntimeError("Service mapping not found", HttpStatus.NOT_FOUND);
			}

			List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets = convertApprovedSets(requestDTO.getSets());

			serviceDataSet.setApprovedSets(approvedSets);

		} else {

			ExternalDataMappingRequestDTO.ExternalDataSetDTO externalDataSet = findExternalDataSet(storedDTO,
					requestDTO.getSetId());

			if (externalDataSet == null) {
				throw new SPRuntimeError("External DataSet mapping not found", HttpStatus.NOT_FOUND);
			}

			List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets = convertApprovedSets(requestDTO.getSets());

			externalDataSet.setApprovedSets(approvedSets);
		}
	}

	private List<ExternalDataMappingRequestDTO.ApprovedSetDTO> convertApprovedSets(
			List<ExternalDataMappingApprovalRequestDTO.ApprovalSetDTO> requestSets) {

		List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets = new ArrayList<>();

		if (requestSets == null) {
			return approvedSets;
		}

		for (ExternalDataMappingApprovalRequestDTO.ApprovalSetDTO requestSet : requestSets) {

			ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet = new ExternalDataMappingRequestDTO.ApprovedSetDTO();

			approvedSet.setSetId(requestSet.getSetId());

			approvedSet.setSetName(requestSet.getSetName());

			List<ExternalDataMappingRequestDTO.OutputSetDTO> outputSets = new ArrayList<>();

			if (requestSet.getOutputSet() != null) {

				for (ExternalDataMappingApprovalRequestDTO.OutputSetDTO requestOutput : requestSet.getOutputSet()) {

					ExternalDataMappingRequestDTO.OutputSetDTO outputSet = new ExternalDataMappingRequestDTO.OutputSetDTO();

					outputSet.setId(requestOutput.getId());

					outputSet.setName(requestOutput.getName());

					outputSets.add(outputSet);
				}
			}

			approvedSet.setOutputSet(outputSets);

			approvedSets.add(approvedSet);
		}

		return approvedSets;
	}

	private ExternalDataMappingRequestDTO.ServiceDataSetDTO findServiceDataSet(ExternalDataMappingRequestDTO dto,
			Long serviceId) {

		if (dto == null || dto.getDataSetSources() == null || dto.getDataSetSources().getService() == null) {

			return null;
		}

		return dto.getDataSetSources().getService().stream().filter(Objects::nonNull)
				.filter(service -> Objects.equals(service.getServiceId(), serviceId)).findFirst().orElse(null);
	}

	private ExternalDataMappingRequestDTO.ExternalDataSetDTO findExternalDataSet(ExternalDataMappingRequestDTO dto,
			Long setId) {

		if (dto == null || dto.getDataSetSources() == null || dto.getDataSetSources().getExternal() == null) {

			return null;
		}

		return dto.getDataSetSources().getExternal().stream().filter(Objects::nonNull)
				.filter(dataSet -> Objects.equals(dataSet.getSetId(), setId)).findFirst().orElse(null);
	}

	private boolean isPendingDataSet(ExternalDataMappingRequestDTO dto,
			ExternalDataMappingApprovalRequestDTO requestDTO) {

		if (dto == null || dto.getDataSetSources() == null) {
			return false;
		}

		if ("S".equalsIgnoreCase(requestDTO.getSource())) {

			if (requestDTO.getServiceId() == null || dto.getDataSetSources().getService() == null) {
				return false;
			}

			return dto.getDataSetSources().getService().stream().filter(Objects::nonNull)
					.anyMatch(service -> Objects.equals(service.getServiceId(), requestDTO.getServiceId())
							&& Objects.equals(service.getStatus(), ApprovalStatus.PENDING.getCode()));
		}

		if ("E".equalsIgnoreCase(requestDTO.getSource())) {

			if (requestDTO.getSetId() == null || dto.getDataSetSources().getExternal() == null) {
				return false;
			}

			return dto.getDataSetSources().getExternal().stream().filter(Objects::nonNull)
					.anyMatch(dataSet -> Objects.equals(dataSet.getSetId(), requestDTO.getSetId())
							&& Objects.equals(dataSet.getStatus(), ApprovalStatus.PENDING.getCode()));
		}

		return false;
	}
}
