package com.serviceplus.metadata.correlationmapping.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfiguration;
import com.serviceplus.metadata.correlationConfig.repository.ICorrelationConfigurationRepository;
import com.serviceplus.metadata.correlationmapping.dto.DeleteExternalDataMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingResponseDTO;
import com.serviceplus.metadata.correlationmapping.entity.ExternalDataMapping;
import com.serviceplus.metadata.correlationmapping.enums.ApprovalStatus;
import com.serviceplus.metadata.correlationmapping.enums.SourceType;
import com.serviceplus.metadata.correlationmapping.repository.ExternalDataMappingRepository;
import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IServiceLogRepository;

@Service
public class ExternalDataMappingService {

	private static final Logger LOGGER = LogManager.getLogger(ExternalDataMappingService.class);

	private final ExternalDataMappingRepository externalDataMappingRepository;

	private final ICorrelationConfigurationRepository correlationSetConfigRepository;

	private final IServiceLogRepository serviceLogRepository;

	public ExternalDataMappingService(ExternalDataMappingRepository externalDataMappingRepository,
			ICorrelationConfigurationRepository correlationSetConfigRepository,
			IServiceLogRepository serviceLogRepository) {

		this.externalDataMappingRepository = externalDataMappingRepository;

		this.correlationSetConfigRepository = correlationSetConfigRepository;

		this.serviceLogRepository = serviceLogRepository;
	}

	public ResponseEntity<?> getAllDataSetForMapping(List<String> sources, Integer serviceStatus,
			UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Get All Data Set For Mapping - START");

		try {

			if (userSessionDetails == null || userSessionDetails.getTenantId() == null) {

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			if (sources == null || sources.isEmpty()) {

				throw new SPRuntimeError("At least one source is required", HttpStatus.BAD_REQUEST);
			}

			Set<SourceType> sourceTypes;

			try {

				sourceTypes = sources.stream().filter(Objects::nonNull).map(String::trim)
						.filter(source -> !source.isEmpty()).map(SourceType::fromValue)
						.collect(Collectors.toCollection(LinkedHashSet::new));

			} catch (Exception ex) {

				throw new SPRuntimeError("Invalid source", HttpStatus.BAD_REQUEST);
			}

			if (sourceTypes.isEmpty()) {

				throw new SPRuntimeError("Invalid source", HttpStatus.BAD_REQUEST);
			}

			Set<Integer> allowedServiceStatuses = getAllowedServiceStatuses(serviceStatus);

			String tenantId = userSessionDetails.getTenantId();

			LOGGER.debug(
					"Service : Get All Data Set For Mapping - "
							+ "sourceTypes={}, allowedServiceStatuses={}, tenantId={}",
					sourceTypes, allowedServiceStatuses, tenantId);

			List<CorrelationConfiguration> correlationConfigurations = correlationSetConfigRepository
					.findByTenantId(tenantId).orElse(Collections.emptyList());

			List<Map<String, Object>> services = new ArrayList<>();

			List<Map<String, Object>> externalDataSets = new ArrayList<>();

			Set<Integer> addedServiceIds = new HashSet<>();

			for (CorrelationConfiguration configuration : correlationConfigurations) {

				if (configuration == null || configuration.getSetType() == null) {

					continue;
				}

				String setType = configuration.getSetType();

				if (sourceTypes.contains(SourceType.SERVICE) && SourceType.SERVICE.getValue().equals(setType)) {

					Integer serviceId = configuration.getServiceId();

					if (serviceId == null) {

						LOGGER.warn("Service : Get All Data Set For Mapping " + "- Missing serviceId for "
								+ "correlationConfigurationId={}", configuration.getId());

						continue;
					}

					if (addedServiceIds.contains(serviceId)) {
						continue;
					}

					ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

					if (serviceLog == null) {

						LOGGER.debug("Service : Get All Data Set For Mapping " + "- No ServiceLog found for "
								+ "serviceId={}", serviceId);

						continue;
					}

					Integer dbServiceStatus = serviceLog.getServiceStatus();

					if (!allowedServiceStatuses.contains(dbServiceStatus)) {

						continue;
					}

					Map<String, Object> service = new LinkedHashMap<>();

					service.put("serviceName", configuration.getServiceName());

					service.put("serviceId", serviceId);

					service.put("baseServiceId", configuration.getBaseServiceId());

					service.put("sourceType", SourceType.SERVICE.getValue());

					services.add(service);

					addedServiceIds.add(serviceId);
				}

				if (sourceTypes.contains(SourceType.EXTERNAL_DATA_SET)
						&& SourceType.EXTERNAL_DATA_SET.getValue().equals(setType)) {

					Map<String, Object> externalDataSet = new LinkedHashMap<>();

					externalDataSet.put("setName", configuration.getSetName());

					externalDataSet.put("setId", configuration.getId());

					externalDataSet.put("sourceType", SourceType.EXTERNAL_DATA_SET.getValue());

					externalDataSets.add(externalDataSet);
				}
			}

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("services", services);

			response.put("externalDataSets", externalDataSets);

			LOGGER.info("Service : Get All Data Set For Mapping - " + "SUCCESS | services={}, externalDataSets={}",
					services.size(), externalDataSets.size());

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Service : Get All Data Set For Mapping - " + "Exception | userId={}, tenantId={}",
					userSessionDetails != null ? userSessionDetails.getUserID() : null,
					userSessionDetails != null ? userSessionDetails.getTenantId() : null, ex);

			throw new SPRuntimeError("Something went wrong while fetching datasets", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Set<Integer> getAllowedServiceStatuses(Integer serviceStatus) {

		if (serviceStatus == null || serviceStatus == -1) {

			return Set.of(ServiceStatus.ACTIVATED.getCode(), ServiceStatus.LAUNCHED.getCode());
		}

		if (serviceStatus == ServiceStatus.ACTIVATED.getCode() || serviceStatus == ServiceStatus.LAUNCHED.getCode()) {

			return Set.of(serviceStatus);
		}

		throw new SPRuntimeError("Invalid serviceStatus. " + "Allowed values are 2 (ACTIVATED) " + "and 4 (LAUNCHED)",
				HttpStatus.BAD_REQUEST);
	}

	public ResponseEntity<?> getAllDataSets(UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Get All External DataSets - START");

		try {

			if (userSessionDetails == null || userSessionDetails.getTenantId() == null
					|| userSessionDetails.getEntityId() == null) {

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			String tenantId = userSessionDetails.getTenantId();
			Integer entityId = userSessionDetails.getEntityId();

			List<ExternalDataMapping> externalDataMappings = externalDataMappingRepository
					.findByTenantIdAndEntityIdAndCrBy(tenantId, entityId, userSessionDetails.getUserID())
					.orElse(Collections.emptyList());

			List<ExternalDataMappingResponseDTO> responseList = new ArrayList<>();

			for (ExternalDataMapping dataMapping : externalDataMappings) {

				if (dataMapping == null || dataMapping.getDto() == null) {
					continue;
				}

				ExternalDataMappingRequestDTO requestDTO = dataMapping.getDto();

				ExternalDataMappingResponseDTO responseDTO = new ExternalDataMappingResponseDTO();

				responseDTO.setId(dataMapping.getId());

				responseDTO.setSource(requestDTO.getSource());

				responseDTO.setServiceStatus(requestDTO.getServiceStatus());

				ExternalDataMappingResponseDTO.DataSetSourcesResponseDTO dataSetSourcesResponse = new ExternalDataMappingResponseDTO.DataSetSourcesResponseDTO();

				List<ExternalDataMappingResponseDTO.ServiceDataSetResponseDTO> serviceResponseList = new ArrayList<>();

				if (requestDTO.getDataSetSources() != null && requestDTO.getDataSetSources().getService() != null) {

					for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : requestDTO.getDataSetSources()
							.getService()) {

						if (service == null) {
							continue;
						}

						ExternalDataMappingResponseDTO.ServiceDataSetResponseDTO serviceResponse = new ExternalDataMappingResponseDTO.ServiceDataSetResponseDTO();

						serviceResponse.setServiceId(service.getServiceId());

						serviceResponse.setBaseServiceId(service.getBaseServiceId());

						serviceResponse.setServiceName(service.getServiceName());

						serviceResponse.setSourceType(service.getSourceType());

						if (service.getStatus() != null) {

							ApprovalStatus approvalStatus = ApprovalStatus.fromCode(service.getStatus());

							serviceResponse.setStatus(approvalStatus.getValue());
						}

						serviceResponse.setRemark(service.getRemark());

						List<ExternalDataMappingResponseDTO.ApprovedSetResponseDTO> approvedSetResponseList = new ArrayList<>();

						if (service.getApprovedSets() != null) {

							for (ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet : service.getApprovedSets()) {

								if (approvedSet == null) {
									continue;
								}

								ExternalDataMappingResponseDTO.ApprovedSetResponseDTO approvedSetResponse = new ExternalDataMappingResponseDTO.ApprovedSetResponseDTO();

								approvedSetResponse.setSetId(approvedSet.getSetId());

								approvedSetResponse.setSetName(approvedSet.getSetName());

								List<ExternalDataMappingResponseDTO.OutputSetResponseDTO> outputSetResponseList = new ArrayList<>();

								if (approvedSet.getOutputSet() != null) {

									for (ExternalDataMappingRequestDTO.OutputSetDTO outputSet : approvedSet
											.getOutputSet()) {

										if (outputSet == null) {
											continue;
										}

										ExternalDataMappingResponseDTO.OutputSetResponseDTO outputSetResponse = new ExternalDataMappingResponseDTO.OutputSetResponseDTO();

										outputSetResponse.setId(outputSet.getId());

										outputSetResponse.setName(outputSet.getName());

										outputSetResponseList.add(outputSetResponse);
									}
								}

								approvedSetResponse.setOutputSet(outputSetResponseList);

								approvedSetResponseList.add(approvedSetResponse);
							}
						}

						serviceResponse.setApprovedSets(approvedSetResponseList);

						serviceResponseList.add(serviceResponse);
					}
				}

				dataSetSourcesResponse.setService(serviceResponseList);

				List<ExternalDataMappingResponseDTO.ExternalDataSetResponseDTO> externalResponseList = new ArrayList<>();

				if (requestDTO.getDataSetSources() != null && requestDTO.getDataSetSources().getExternal() != null) {

					for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : requestDTO.getDataSetSources()
							.getExternal()) {

						if (external == null) {
							continue;
						}

						ExternalDataMappingResponseDTO.ExternalDataSetResponseDTO externalResponse = new ExternalDataMappingResponseDTO.ExternalDataSetResponseDTO();

						externalResponse.setSetId(external.getSetId());

						externalResponse.setSetName(external.getSetName());

						externalResponse.setSourceType(external.getSourceType());

						if (external.getStatus() != null) {

							ApprovalStatus approvalStatus = ApprovalStatus.fromCode(external.getStatus());

							externalResponse.setStatus(approvalStatus.getValue());
						}

						externalResponse.setRemark(external.getRemark());

						List<ExternalDataMappingResponseDTO.ApprovedSetResponseDTO> approvedSetResponseList = new ArrayList<>();

						if (external.getApprovedSets() != null) {

							for (ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet : external
									.getApprovedSets()) {

								if (approvedSet == null) {
									continue;
								}

								ExternalDataMappingResponseDTO.ApprovedSetResponseDTO approvedSetResponse = new ExternalDataMappingResponseDTO.ApprovedSetResponseDTO();

								approvedSetResponse.setSetId(approvedSet.getSetId());

								approvedSetResponse.setSetName(approvedSet.getSetName());

								List<ExternalDataMappingResponseDTO.OutputSetResponseDTO> outputSetResponseList = new ArrayList<>();

								if (approvedSet.getOutputSet() != null) {

									for (ExternalDataMappingRequestDTO.OutputSetDTO outputSet : approvedSet
											.getOutputSet()) {

										if (outputSet == null) {
											continue;
										}

										ExternalDataMappingResponseDTO.OutputSetResponseDTO outputSetResponse = new ExternalDataMappingResponseDTO.OutputSetResponseDTO();

										outputSetResponse.setId(outputSet.getId());

										outputSetResponse.setName(outputSet.getName());

										outputSetResponseList.add(outputSetResponse);
									}
								}

								approvedSetResponse.setOutputSet(outputSetResponseList);

								approvedSetResponseList.add(approvedSetResponse);
							}
						}

						externalResponse.setApprovedSets(approvedSetResponseList);

						externalResponseList.add(externalResponse);
					}
				}

				dataSetSourcesResponse.setExternal(externalResponseList);

				responseDTO.setDataSetSources(dataSetSourcesResponse);

				responseList.add(responseDTO);
			}

			LOGGER.info("Service : Get All External DataSets - END | userId={}, tenantId={}, entityId={}, count={}",
					userSessionDetails.getUserID(), tenantId, entityId, responseList.size());

			return ResponseEntity.ok(responseList);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Service : Get All External DataSets - Exception occurred | userId={}",
					userSessionDetails != null ? userSessionDetails.getUserID() : null, ex);

			throw new SPRuntimeError("Something went wrong while fetching datasets", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> save(ExternalDataMappingRequestDTO requestDTO, UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Save/Edit External Data Mapping - START");

		try {

			validate(requestDTO);
			validateAndSetDefaultStatus(requestDTO);

			ExternalDataMapping mapping;
			boolean isEdit = requestDTO.getId() != null;

			if (!isEdit) {

				LOGGER.debug(
						"Service : Save/Edit External Data Mapping - Creating new mapping | tenantId={}, entityId={}",
						userSessionDetails.getTenantId(), userSessionDetails.getEntityId());

				mapping = new ExternalDataMapping();

				mapping.setTenantId(userSessionDetails.getTenantId());

				mapping.setEntityId(userSessionDetails.getEntityId());

				mapping.setCrBy(userSessionDetails.getUserID());

				setAllDatasetStatus(requestDTO, ApprovalStatus.PENDING.getCode());

				clearAllApprovalDetails(requestDTO);

			}

			else {

				LOGGER.debug("Service : Save/Edit External Data Mapping - Editing mapping | id={}", requestDTO.getId());

				mapping = externalDataMappingRepository.findById(requestDTO.getId())
						.orElseThrow(() -> new SPRuntimeError("External Data Mapping not found", HttpStatus.NOT_FOUND));

				if (!Objects.equals(mapping.getTenantId(), userSessionDetails.getTenantId())
						|| !Objects.equals(mapping.getEntityId(), userSessionDetails.getEntityId())
						|| !Objects.equals(mapping.getCrBy(), userSessionDetails.getUserID())) {

					LOGGER.warn(
							"Service : Save/Edit External Data Mapping - Unauthorized edit | id={}, tenantId={}, entityId={}",
							requestDTO.getId(), userSessionDetails.getTenantId(), userSessionDetails.getEntityId());

					throw new SPRuntimeError("You are not authorized to edit this mapping", HttpStatus.FORBIDDEN);
				}

				ExternalDataMappingRequestDTO existingDTO = mapping.getDto();

				if (existingDTO == null) {
					throw new SPRuntimeError("Existing mapping data is invalid", HttpStatus.INTERNAL_SERVER_ERROR);
				}

				processServiceDataSetEdit(existingDTO, requestDTO);

				processExternalDataSetEdit(existingDTO, requestDTO);

				mapping.setUpBy(userSessionDetails.getUserID());
			}

			mapping.setDto(requestDTO);

			ExternalDataMapping saved = externalDataMappingRepository.save(mapping);

			LOGGER.info("Service : Save/Edit External Data Mapping - SUCCESS | id={}, operation={}", saved.getId(),
					isEdit ? "EDIT" : "CREATE");

			Map<String, Object> response = new HashMap<>();

			response.put("id", saved.getId());

			response.put("message",
					isEdit ? "External Data Mapping updated successfully" : "External Data Mapping saved successfully");

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Service : Save/Edit External Data Mapping - Exception", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void setAllDatasetStatus(ExternalDataMappingRequestDTO requestDTO, Integer status) {

		if (requestDTO == null || requestDTO.getDataSetSources() == null) {
			return;
		}

		if (requestDTO.getDataSetSources().getService() != null) {

			for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : requestDTO.getDataSetSources()
					.getService()) {
				service.setStatus(status);
			}
		}

		if (requestDTO.getDataSetSources().getExternal() != null) {

			for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : requestDTO.getDataSetSources()
					.getExternal()) {
				external.setStatus(status);
			}
		}
	}

	private void clearAllApprovalDetails(ExternalDataMappingRequestDTO requestDTO) {

		if (requestDTO == null || requestDTO.getDataSetSources() == null) {
			return;
		}

		if (requestDTO.getDataSetSources().getService() != null) {

			for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : requestDTO.getDataSetSources()
					.getService()) {

				service.setApprovedBy(null);
				service.setApprovedOn(null);
				service.setRemark("");
			}
		}

		if (requestDTO.getDataSetSources().getExternal() != null) {

			for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : requestDTO.getDataSetSources()
					.getExternal()) {

				external.setApprovedBy(null);
				external.setApprovedOn(null);
				external.setRemark("");
			}
		}
	}

	private void validate(ExternalDataMappingRequestDTO dataMappingRequestDTO) {

		if (dataMappingRequestDTO == null) {

			throw new SPRuntimeError("Request can't be null", HttpStatus.BAD_REQUEST);
		}

		if (dataMappingRequestDTO.getSource() == null || dataMappingRequestDTO.getSource().isEmpty()) {

			throw new SPRuntimeError("source can't be empty", HttpStatus.BAD_REQUEST);
		}

		List<String> sourceList = new ArrayList<>();

		for (LabelValue source : dataMappingRequestDTO.getSource()) {

			if (source == null || source.getValue() == null) {

				throw new SPRuntimeError("source value can't be null", HttpStatus.BAD_REQUEST);
			}

			String sourceValue;

			try {

				sourceValue = SourceType.fromValue(source.getValue().toString()).getValue();

			} catch (Exception ex) {

				throw new SPRuntimeError("Invalid source value: " + source.getValue(), HttpStatus.BAD_REQUEST);
			}

			if (sourceList.contains(sourceValue)) {

				throw new SPRuntimeError("Duplicate source value: " + sourceValue, HttpStatus.BAD_REQUEST);
			}

			sourceList.add(sourceValue);
		}

		if (dataMappingRequestDTO.getServiceStatus() == null) {

			throw new SPRuntimeError("serviceStatus can't be null", HttpStatus.BAD_REQUEST);
		}

		if (dataMappingRequestDTO.getServiceStatus().getValue() == null) {

			throw new SPRuntimeError("serviceStatus value can't be null", HttpStatus.BAD_REQUEST);
		}

		try {

			Integer serviceStatus = Integer.parseInt(dataMappingRequestDTO.getServiceStatus().getValue().toString());

			if(serviceStatus != -1)
				ServiceStatus.fromCode(serviceStatus);
			

		} catch (Exception ex) {

			throw new SPRuntimeError(
					"Invalid serviceStatus value: " + dataMappingRequestDTO.getServiceStatus().getValue(),
					HttpStatus.BAD_REQUEST);
		}

		ExternalDataMappingRequestDTO.DataSetSourcesDTO dataSetSources = dataMappingRequestDTO.getDataSetSources();

		if (dataSetSources == null) {

			throw new SPRuntimeError("dataSetSources can't be null", HttpStatus.BAD_REQUEST);
		}

		boolean hasServiceSource = sourceList.contains(SourceType.SERVICE.getValue());

		boolean hasExternalSource = sourceList.contains(SourceType.EXTERNAL_DATA_SET.getValue());

		boolean hasServiceData = dataSetSources.getService() != null && !dataSetSources.getService().isEmpty();

		boolean hasExternalData = dataSetSources.getExternal() != null && !dataSetSources.getExternal().isEmpty();

		if (hasServiceSource && !hasServiceData) {

			throw new SPRuntimeError("Service source is selected but " + "service dataSetSources is empty",
					HttpStatus.BAD_REQUEST);
		}

		if (hasExternalSource && !hasExternalData) {

			throw new SPRuntimeError("External source is selected but " + "external dataSetSources is empty",
					HttpStatus.BAD_REQUEST);
		}

		if (!hasServiceSource && hasServiceData) {

			throw new SPRuntimeError("Service data is provided but " + "SERVICE source is not selected",
					HttpStatus.BAD_REQUEST);
		}

		if (!hasExternalSource && hasExternalData) {

			throw new SPRuntimeError("External data is provided but " + "EXTERNAL_DATA_SET source is not selected",
					HttpStatus.BAD_REQUEST);
		}

		if (!hasServiceData && !hasExternalData) {

			throw new SPRuntimeError("At least one service or external dataset is required", HttpStatus.BAD_REQUEST);
		}

		if (hasServiceData) {

			for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : dataSetSources.getService()) {

				if (service == null) {

					throw new SPRuntimeError("Service dataSet can't be null", HttpStatus.BAD_REQUEST);
				}

				if (service.getSourceType() == null || service.getSourceType().trim().isEmpty()) {

					throw new SPRuntimeError("Service sourceType can't be null", HttpStatus.BAD_REQUEST);
				}

				SourceType sourceType;

				try {

					sourceType = SourceType.fromValue(service.getSourceType());

				} catch (Exception ex) {

					throw new SPRuntimeError("Invalid service sourceType: " + service.getSourceType(),
							HttpStatus.BAD_REQUEST);
				}

				if (!SourceType.SERVICE.equals(sourceType)) {

					throw new SPRuntimeError(
							"Invalid sourceType for service dataset. " + "Expected: " + SourceType.SERVICE.getValue(),
							HttpStatus.BAD_REQUEST);
				}

				Long serviceId = service.getServiceId();

				if (serviceId == null) {

					throw new SPRuntimeError("serviceId can't be null when " + "sourceType is SERVICE",
							HttpStatus.BAD_REQUEST);
				}


				Integer requestServiceId = serviceId.intValue();

				Long baseServiceId = service.getBaseServiceId();

				if (baseServiceId == null) {

					throw new SPRuntimeError("baseServiceId can't be null when " + "sourceType is SERVICE",
							HttpStatus.BAD_REQUEST);
				}

				if (baseServiceId > Integer.MAX_VALUE || baseServiceId < Integer.MIN_VALUE) {

					throw new SPRuntimeError("Invalid baseServiceId: " + baseServiceId, HttpStatus.BAD_REQUEST);
				}

				Integer requestBaseServiceId = baseServiceId.intValue();

				if (service.getServiceName() == null || service.getServiceName().trim().isEmpty()) {

					throw new SPRuntimeError("serviceName can't be empty when " + "sourceType is SERVICE",
							HttpStatus.BAD_REQUEST);
				}

				List<CorrelationConfiguration> configurations = correlationSetConfigRepository
						.findByServiceId(requestServiceId).orElse(Collections.emptyList());

				if (configurations.isEmpty()) {

					throw new SPRuntimeError(
							"CorrelationConfiguration not found " + "for serviceId: " + requestServiceId,
							HttpStatus.BAD_REQUEST);
				}

				boolean validConfiguration = configurations.stream().anyMatch(configuration ->

				Objects.equals(configuration.getServiceId(), requestServiceId)

						&& Objects.equals(configuration.getBaseServiceId(), requestBaseServiceId)

						&& Objects.equals(configuration.getServiceName(), service.getServiceName()));

				if (!validConfiguration) {

					throw new SPRuntimeError("Invalid service details for serviceId: " + requestServiceId,
							HttpStatus.BAD_REQUEST);
				}
			}
		}

		if (hasExternalData) {

			for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : dataSetSources.getExternal()) {

				if (external == null) {

					throw new SPRuntimeError("External dataSet can't be null", HttpStatus.BAD_REQUEST);
				}

				if (external.getSourceType() == null || external.getSourceType().trim().isEmpty()) {

					throw new SPRuntimeError("External sourceType can't be null", HttpStatus.BAD_REQUEST);
				}

				SourceType sourceType;

				try {

					sourceType = SourceType.fromValue(external.getSourceType());

				} catch (Exception ex) {

					throw new SPRuntimeError("Invalid external sourceType: " + external.getSourceType(),
							HttpStatus.BAD_REQUEST);
				}

				if (!SourceType.EXTERNAL_DATA_SET.equals(sourceType)) {

					throw new SPRuntimeError("Invalid sourceType for external dataset. " + "Expected: "
							+ SourceType.EXTERNAL_DATA_SET.getValue(), HttpStatus.BAD_REQUEST);
				}

				Long setId = external.getSetId();

				if (setId == null) {

					throw new SPRuntimeError("setId can't be null when sourceType " + "is EXTERNAL_DATA_SET",
							HttpStatus.BAD_REQUEST);
				}

				if (external.getSetName() == null || external.getSetName().trim().isEmpty()) {

					throw new SPRuntimeError("setName can't be empty when " + "sourceType is EXTERNAL_DATA_SET",
							HttpStatus.BAD_REQUEST);
				}

				if (setId > Integer.MAX_VALUE || setId < Integer.MIN_VALUE) {

					throw new SPRuntimeError("Invalid setId: " + setId, HttpStatus.BAD_REQUEST);
				}

				Integer requestSetId = setId.intValue();

				CorrelationConfiguration configuration = correlationSetConfigRepository.findById(requestSetId)
						.orElseThrow(() -> new SPRuntimeError(
								"CorrelationConfiguration " + "not found for setId: " + setId, HttpStatus.BAD_REQUEST));

				if (!SourceType.EXTERNAL_DATA_SET.getValue().equals(configuration.getSetType())) {

					throw new SPRuntimeError("Invalid external dataset setId: " + setId, HttpStatus.BAD_REQUEST);
				}

				if (!Objects.equals(configuration.getSetName(), external.getSetName())) {

					throw new SPRuntimeError("Invalid setName for setId: " + setId, HttpStatus.BAD_REQUEST);
				}
			}
		}
	}

	private void validateAndSetDefaultStatus(ExternalDataMappingRequestDTO requestDTO) {

		if (requestDTO == null || requestDTO.getDataSetSources() == null) {
			return;
		}

		if (requestDTO.getDataSetSources().getService() != null) {

			for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : requestDTO.getDataSetSources()
					.getService()) {

				if (service.getStatus() == null) {

					service.setStatus(ApprovalStatus.PENDING.getCode());

				} else {

					validateApprovalStatus(service.getStatus());
				}
			}
		}

		if (requestDTO.getDataSetSources().getExternal() != null) {

			for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : requestDTO.getDataSetSources()
					.getExternal()) {

				if (external.getStatus() == null) {

					external.setStatus(ApprovalStatus.PENDING.getCode());

				} else {

					validateApprovalStatus(external.getStatus());
				}
			}
		}
	}

	private void validateApprovalStatus(Integer status) {

		try {

			ApprovalStatus.fromCode(status);

		} catch (IllegalArgumentException ex) {

			throw new SPRuntimeError("Invalid approval status: " + status, HttpStatus.BAD_REQUEST);
		}
	}

	private void processServiceDataSetEdit(ExternalDataMappingRequestDTO existingDTO,
			ExternalDataMappingRequestDTO requestDTO) {

		Map<Long, ExternalDataMappingRequestDTO.ServiceDataSetDTO> existingServices = new HashMap<>();

		if (existingDTO.getDataSetSources() != null && existingDTO.getDataSetSources().getService() != null) {

			for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : existingDTO.getDataSetSources()
					.getService()) {

				if (service.getServiceId() != null) {

					existingServices.put(service.getServiceId(), service);
				}
			}
		}

		if (requestDTO.getDataSetSources() == null || requestDTO.getDataSetSources().getService() == null) {
			return;
		}

		for (ExternalDataMappingRequestDTO.ServiceDataSetDTO requestedService : requestDTO.getDataSetSources()
				.getService()) {

			ExternalDataMappingRequestDTO.ServiceDataSetDTO existingService = existingServices
					.get(requestedService.getServiceId());

			if (existingService == null) {

				requestedService.setStatus(ApprovalStatus.PENDING.getCode());

				requestedService.setApprovedBy(null);
				requestedService.setApprovedOn(null);
				requestedService.setRemark(null);

				continue;
			}

			boolean approvedSetsChanged = haveApprovedSetsChanged(existingService.getApprovedSets(),
					requestedService.getApprovedSets());

			if (approvedSetsChanged) {

				requestedService.setStatus(ApprovalStatus.PENDING.getCode());

				requestedService.setApprovedBy(null);
				requestedService.setApprovedOn(null);
				requestedService.setRemark(null);

			} else {

				requestedService.setStatus(existingService.getStatus());

				requestedService.setApprovedBy(existingService.getApprovedBy());

				requestedService.setApprovedOn(existingService.getApprovedOn());

				requestedService.setRemark(existingService.getRemark());
			}
		}
	}

	private boolean haveApprovedSetsChanged(List<ExternalDataMappingRequestDTO.ApprovedSetDTO> existingSets,
			List<ExternalDataMappingRequestDTO.ApprovedSetDTO> requestedSets) {

		Set<Long> existingSetIds = extractApprovedSetIds(existingSets);

		Set<Long> requestedSetIds = extractApprovedSetIds(requestedSets);

		return !existingSetIds.equals(requestedSetIds);
	}

	private Set<Long> extractApprovedSetIds(List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets) {

		Set<Long> setIds = new HashSet<>();

		if (approvedSets == null) {
			return setIds;
		}

		for (ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet : approvedSets) {

			if (approvedSet != null && approvedSet.getSetId() != null) {

				setIds.add(approvedSet.getSetId());
			}
		}

		return setIds;
	}

	private void processExternalDataSetEdit(ExternalDataMappingRequestDTO existingDTO,
			ExternalDataMappingRequestDTO requestDTO) {

		Map<Long, ExternalDataMappingRequestDTO.ExternalDataSetDTO> existingExternalDataSets = new HashMap<>();

		if (existingDTO.getDataSetSources() != null && existingDTO.getDataSetSources().getExternal() != null) {

			for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : existingDTO.getDataSetSources()
					.getExternal()) {

				if (external.getSetId() != null) {

					existingExternalDataSets.put(external.getSetId(), external);
				}
			}
		}

		if (requestDTO.getDataSetSources() == null || requestDTO.getDataSetSources().getExternal() == null) {
			return;
		}

		for (ExternalDataMappingRequestDTO.ExternalDataSetDTO requestedExternal : requestDTO.getDataSetSources()
				.getExternal()) {

			ExternalDataMappingRequestDTO.ExternalDataSetDTO existingExternal = existingExternalDataSets
					.get(requestedExternal.getSetId());

			if (existingExternal == null) {

				requestedExternal.setStatus(ApprovalStatus.PENDING.getCode());

				requestedExternal.setApprovedBy(null);
				requestedExternal.setApprovedOn(null);
				requestedExternal.setRemark("");

				continue;
			}

			boolean approvedSetsChanged = haveApprovedSetsChanged(existingExternal.getApprovedSets(),
					requestedExternal.getApprovedSets());

			if (approvedSetsChanged) {

				requestedExternal.setStatus(ApprovalStatus.PENDING.getCode());

				requestedExternal.setApprovedBy(null);
				requestedExternal.setApprovedOn(null);
				requestedExternal.setRemark("");

			} else {

				requestedExternal.setStatus(existingExternal.getStatus());

				requestedExternal.setApprovedBy(existingExternal.getApprovedBy());

				requestedExternal.setApprovedOn(existingExternal.getApprovedOn());

				requestedExternal.setRemark(existingExternal.getRemark());
			}
		}
	}

	public ResponseEntity<?> delete(DeleteExternalDataMappingRequestDTO requestDTO, UserSessionDTO userSessionDTO) {

		LOGGER.info("Service : Delete External DataSet - START | mappingId={}",
				requestDTO != null ? requestDTO.getId() : null);

		try {

			if (userSessionDTO == null || userSessionDTO.getTenantId() == null) {

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			if (requestDTO == null) {

				throw new SPRuntimeError("Request can't be null", HttpStatus.BAD_REQUEST);
			}

			if (requestDTO.getId() == null) {

				throw new SPRuntimeError("DataSet mapping id can't be null", HttpStatus.BAD_REQUEST);
			}

			if (requestDTO.getData() == null || requestDTO.getData().isEmpty()) {

				throw new SPRuntimeError("Data can't be empty", HttpStatus.BAD_REQUEST);
			}

			validateDeleteData(requestDTO);

			ExternalDataMapping externalDataMapping = externalDataMappingRepository.findById(requestDTO.getId())
					.orElseThrow(() -> new SPRuntimeError("DataSet mapping not found", HttpStatus.NOT_FOUND));

			if (!userSessionDTO.getTenantId().equals(externalDataMapping.getTenantId())) {

				throw new SPRuntimeError("DataSet mapping not found", HttpStatus.NOT_FOUND);
			}

			if (!Objects.equals(userSessionDTO.getEntityId(), externalDataMapping.getEntityId())) {

				throw new SPRuntimeError("DataSet mapping not found", HttpStatus.NOT_FOUND);
			}

			ExternalDataMappingRequestDTO mappingDTO = externalDataMapping.getDto();

			if (mappingDTO == null || mappingDTO.getDataSetSources() == null) {

				throw new SPRuntimeError("DataSet mapping data is invalid", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			deleteServices(mappingDTO, requestDTO.getData());

			deleteExternalDataSets(mappingDTO, requestDTO.getData());

			boolean hasServices = mappingDTO.getDataSetSources().getService() != null
					&& !mappingDTO.getDataSetSources().getService().isEmpty();

			boolean hasExternalDataSets = mappingDTO.getDataSetSources().getExternal() != null
					&& !mappingDTO.getDataSetSources().getExternal().isEmpty();

			if (!hasServices && !hasExternalDataSets) {

				externalDataMappingRepository.delete(externalDataMapping);

				LOGGER.info("Service : Delete External DataSet - Complete mapping deleted | mappingId={}",
						requestDTO.getId());

				return ResponseEntity.ok(Map.of("message", "Dataset mapping deleted successfully."));
			}

			externalDataMapping.setDto(mappingDTO);
			externalDataMapping.setUpBy(userSessionDTO.getUserID());

			externalDataMappingRepository.save(externalDataMapping);

			LOGGER.info("Service : Delete External DataSet - Selected data deleted | mappingId={}", requestDTO.getId());

			return ResponseEntity.ok(Map.of("message", "Selected dataset mapping deleted successfully."));

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Service : Delete External DataSet - Exception occurred | mappingId={}",
					requestDTO != null ? requestDTO.getId() : null, ex);

			throw new SPRuntimeError("Something went wrong while deleting dataset", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateDeleteData(DeleteExternalDataMappingRequestDTO requestDTO) {

		for (DeleteExternalDataMappingRequestDTO.DeleteDataDTO data : requestDTO.getData()) {

			if (data == null) {

				throw new SPRuntimeError("Delete data can't be null", HttpStatus.BAD_REQUEST);
			}

			if (data.getSourceType() == null || data.getSourceType().trim().isEmpty()) {

				throw new SPRuntimeError("sourceType can't be null or empty", HttpStatus.BAD_REQUEST);
			}

			SourceType sourceType;

			try {

				sourceType = SourceType.fromValue(data.getSourceType());

			} catch (IllegalArgumentException ex) {

				throw new SPRuntimeError("Invalid sourceType: " + data.getSourceType(), HttpStatus.BAD_REQUEST);
			}

			if (SourceType.SERVICE.equals(sourceType)) {

				if (data.getServiceId() == null) {

					throw new SPRuntimeError("serviceId is required for sourceType S", HttpStatus.BAD_REQUEST);
				}
			}

			else if (SourceType.EXTERNAL_DATA_SET.equals(sourceType)) {

				if (data.getSetId() == null) {

					throw new SPRuntimeError("setId is required for sourceType E", HttpStatus.BAD_REQUEST);
				}
			}
		}
	}

	private void deleteServices(ExternalDataMappingRequestDTO mappingDTO,
			List<DeleteExternalDataMappingRequestDTO.DeleteDataDTO> deleteData) {

		if (mappingDTO.getDataSetSources().getService() == null
				|| mappingDTO.getDataSetSources().getService().isEmpty()) {

			return;
		}

		Set<Long> serviceIdsToDelete = deleteData.stream().filter(Objects::nonNull)
				.filter(data -> SourceType.SERVICE.getValue().equalsIgnoreCase(data.getSourceType()))
				.map(DeleteExternalDataMappingRequestDTO.DeleteDataDTO::getServiceId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		if (serviceIdsToDelete.isEmpty()) {
			return;
		}

		mappingDTO.getDataSetSources().getService().removeIf(service -> service != null
				&& service.getServiceId() != null && serviceIdsToDelete.contains(service.getServiceId()));
	}

	private void deleteExternalDataSets(ExternalDataMappingRequestDTO mappingDTO,
			List<DeleteExternalDataMappingRequestDTO.DeleteDataDTO> deleteData) {

		if (mappingDTO.getDataSetSources().getExternal() == null
				|| mappingDTO.getDataSetSources().getExternal().isEmpty()) {

			return;
		}

		Set<Long> setIdsToDelete = deleteData.stream().filter(Objects::nonNull)
				.filter(data -> SourceType.EXTERNAL_DATA_SET.getValue().equalsIgnoreCase(data.getSourceType()))
				.map(DeleteExternalDataMappingRequestDTO.DeleteDataDTO::getSetId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		if (setIdsToDelete.isEmpty()) {
			return;
		}

		mappingDTO.getDataSetSources().getExternal().removeIf(external -> external != null
				&& external.getSetId() != null && setIdsToDelete.contains(external.getSetId()));
	}
	
}