package com.serviceplus.metadata.correlationmapping.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.serviceplus.metadata.correlationmapping.dto.ApprovedDataSetResponseDTO;
import com.serviceplus.metadata.correlationmapping.dto.CombinationSetDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataCombinationRequestDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataCombinationResponseDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.entity.ExternalDataCombination;
import com.serviceplus.metadata.correlationmapping.entity.ExternalDataMapping;
import com.serviceplus.metadata.correlationmapping.enums.ApprovalStatusCode;
import com.serviceplus.metadata.correlationmapping.repository.ExternalDataMappingRepository;
import com.serviceplus.metadata.correlationmapping.repository.IExternalDataCombinationRepository;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;

@Service
public class ExternalDataCombinationService {

	private static final Logger LOGGER = LogManager.getLogger(ExternalDataCombinationService.class);

	private final IExternalDataCombinationRepository combinationRepository;
	private final ExternalDataMappingRepository externalDataMappingRepository;

	public ExternalDataCombinationService(IExternalDataCombinationRepository combinationRepository,
			ExternalDataMappingRepository externalDataMappingRepository) {
		super();
		this.combinationRepository = combinationRepository;
		this.externalDataMappingRepository = externalDataMappingRepository;
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getApprovedDataSet(UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Get Approved DataSet - START");

		try {

			String tenantId = userSessionDetails.getTenantId();

			Integer entityId = userSessionDetails.getEntityId();

			Long crBy = userSessionDetails.getUserID();

			Optional<List<ExternalDataMapping>> mappingOptional = externalDataMappingRepository
					.findByTenantIdAndEntityIdAndCrBy(tenantId, entityId, crBy);

			if (mappingOptional.isEmpty() || mappingOptional.get().isEmpty()) {

				return ResponseEntity.ok(new ArrayList<ApprovedDataSetResponseDTO>());
			}

			List<ApprovedDataSetResponseDTO> response = new ArrayList<>();

			for (ExternalDataMapping mapping : mappingOptional.get()) {

				ExternalDataMappingRequestDTO dto = mapping.getDto();

				if (dto == null || dto.getDataSetSources() == null) {

					continue;
				}

				if (dto.getDataSetSources().getService() != null) {

					for (ExternalDataMappingRequestDTO.ServiceDataSetDTO service : dto.getDataSetSources()
							.getService()) {

						if (service == null) {
							continue;
						}

						if (!Objects.equals(service.getStatus(), ApprovalStatusCode.APPROVED.getCode())) {

							continue;
						}

						ApprovedDataSetResponseDTO result = new ApprovedDataSetResponseDTO();

						result.setServiceId(service.getServiceId());

						result.setBaseServiceId(service.getBaseServiceId());
						
						result.setServiceName(service.getServiceName());
						
						result.setSourceType(service.getSourceType());

						result.setStatus(ApprovalStatusCode.APPROVED.name());

						result.setRemark(service.getRemark());

						result.setApprovedSets(convertApprovedSets(service.getApprovedSets()));

						response.add(result);
					}
				}

				if (dto.getDataSetSources().getExternal() != null) {

					for (ExternalDataMappingRequestDTO.ExternalDataSetDTO external : dto.getDataSetSources()
							.getExternal()) {

						if (external == null) {
							continue;
						}

						if (!Objects.equals(external.getStatus(), ApprovalStatusCode.APPROVED.getCode())) {

							continue;
						}

						ApprovedDataSetResponseDTO result = new ApprovedDataSetResponseDTO();

						result.setSetId(external.getSetId());

						result.setSetName(external.getSetName());

						result.setSourceType(external.getSourceType());

						result.setStatus(ApprovalStatusCode.APPROVED.name());

						result.setRemark(external.getRemark());

						result.setApprovedSets(convertApprovedSets(external.getApprovedSets()));

						response.add(result);
					}
				}
			}

			LOGGER.info("Service : Get Approved DataSet - SUCCESS, count={}", response.size());

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError e) {

			throw e;

		} catch (Exception e) {

			LOGGER.error("Service : Get Approved DataSet - ERROR", e);

			throw new SPRuntimeError("Failed to get approved DataSet", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<ApprovedDataSetResponseDTO.ApprovedSetDTO> convertApprovedSets(
			List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets) {

		List<ApprovedDataSetResponseDTO.ApprovedSetDTO> response = new ArrayList<>();

		if (approvedSets == null || approvedSets.isEmpty()) {

			return response;
		}

		for (ExternalDataMappingRequestDTO.ApprovedSetDTO approvedSet : approvedSets) {

			if (approvedSet == null) {
				continue;
			}

			ApprovedDataSetResponseDTO.ApprovedSetDTO result = new ApprovedDataSetResponseDTO.ApprovedSetDTO();

			result.setSetId(approvedSet.getSetId());

			result.setSetName(approvedSet.getSetName());

			List<ApprovedDataSetResponseDTO.OutputSetDTO> outputSets = new ArrayList<>();

			if (approvedSet.getOutputSet() != null) {

				for (ExternalDataMappingRequestDTO.OutputSetDTO outputSet : approvedSet.getOutputSet()) {

					if (outputSet == null) {
						continue;
					}

					ApprovedDataSetResponseDTO.OutputSetDTO output = new ApprovedDataSetResponseDTO.OutputSetDTO();

					output.setId(outputSet.getId());

					output.setName(outputSet.getName());

					outputSets.add(output);
				}
			}

			result.setOutputSet(outputSets);

			response.add(result);
		}

		return response;
	}

	public ResponseEntity<?> saveOrEditCombination(ExternalDataCombinationRequestDTO requestDTO,
			UserSessionDTO userSessionDetails) {

		LOGGER.info("Service : Save/Edit DataSet Combination - START");

		try {

			validateCombinationRequest(requestDTO);

			validateSequences(requestDTO.getSets());

			for (CombinationSetDTO set : requestDTO.getSets()) {

				validateCombinationSet(set);
			}

			ExternalDataCombination combination;

			if (requestDTO.getId() == null) {

				LOGGER.info("Creating new DataSet Combination");

				combination = new ExternalDataCombination();

				combination.setCrBy(userSessionDetails.getUserID());

				combination.setTenantId(userSessionDetails.getTenantId());

				combination.setEntityId(userSessionDetails.getEntityId());

			} else {

				LOGGER.info("Editing DataSet Combination, id={}", requestDTO.getId());

				combination = combinationRepository
						.findByIdAndTenantIdAndEntityId(requestDTO.getId(), userSessionDetails.getTenantId(),
								userSessionDetails.getEntityId())
						.orElseThrow(() -> new SPRuntimeError("DataSet combination not found", HttpStatus.NOT_FOUND));

				combination.setUpBy(userSessionDetails.getUserID());
			}

			combination.setCombinationName(requestDTO.getCombinationName().trim());

			combination.setSearchOnDataFound(requestDTO.getSearchOnDataFound());

			combination.setSets(requestDTO.getSets());

			ExternalDataCombination saved = combinationRepository.save(combination);

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("message", requestDTO.getId() == null ? "DataSet combination created successfully"
					: "DataSet combination updated successfully");

			response.put("id", saved.getId());

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError e) {

			throw e;

		} catch (Exception e) {

			LOGGER.error("Service : Save/Edit DataSet Combination - ERROR", e);

			throw new SPRuntimeError("Failed to save DataSet combination", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validateCombinationRequest(ExternalDataCombinationRequestDTO requestDTO) {

		if (requestDTO == null) {

			throw new SPRuntimeError("Request can't be null", HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.hasText(requestDTO.getCombinationName())) {

			throw new SPRuntimeError("combinationName can't be blank", HttpStatus.BAD_REQUEST);
		}

		if (requestDTO.getSearchOnDataFound() == null) {

			throw new SPRuntimeError("searchOnDataFound can't be null", HttpStatus.BAD_REQUEST);
		}

		if (requestDTO.getSets() == null || requestDTO.getSets().isEmpty()) {

			throw new SPRuntimeError("At least one set is required", HttpStatus.BAD_REQUEST);
		}
	}

	private void validateSequences(List<CombinationSetDTO> sets) {

		Set<Integer> sequences = new HashSet<>();

		for (CombinationSetDTO set : sets) {

			if (set.getSequence() == null) {

				throw new SPRuntimeError("sequence can't be null", HttpStatus.BAD_REQUEST);
			}

			if (!sequences.add(set.getSequence())) {

				throw new SPRuntimeError("Duplicate sequence: " + set.getSequence(), HttpStatus.BAD_REQUEST);
			}
		}
	}

	private void validateCombinationSet(CombinationSetDTO set) {

		if (!"S".equalsIgnoreCase(set.getSourceType()) && !"E".equalsIgnoreCase(set.getSourceType())) {

			throw new SPRuntimeError("Invalid sourceType. Allowed values are S or E", HttpStatus.BAD_REQUEST);
		}

		if ("S".equalsIgnoreCase(set.getSourceType())) {

			if (set.getServiceId() == null) {

				throw new SPRuntimeError("serviceId is required for sourceType S", HttpStatus.BAD_REQUEST);
			}

			if (set.getBaseServiceId() == null) {

				throw new SPRuntimeError("baseServiceId is required for sourceType S", HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.hasText(set.getServiceName())) {

				throw new SPRuntimeError("serviceName is required for sourceType S", HttpStatus.BAD_REQUEST);
			}
		} else {

			if (set.getSetId() == null) {

				throw new SPRuntimeError("setId is required for sourceType E", HttpStatus.BAD_REQUEST);
			}

			if (!StringUtils.hasText(set.getSetName())) {

				throw new SPRuntimeError("setName is required for sourceType E", HttpStatus.BAD_REQUEST);
			}
		}

		if (set.getApprovedSets() == null) {
			set.setApprovedSets(new ArrayList<>());
		}
	}

	@Transactional(readOnly = true)
	public ResponseEntity<?> getAllCombinations(UserSessionDTO userSessionDetails, boolean flag) {

		LOGGER.info("Service : Get All DataSet Combinations - START");

		try {

			String tenantId = userSessionDetails.getTenantId();

			Integer entityId = userSessionDetails.getEntityId();

			Long crBy = userSessionDetails.getUserID();
			Optional<List<ExternalDataCombination>> combinationOptional;
			if (flag) {
				combinationOptional = combinationRepository.findByTenantIdAndEntityIdAndCrBy(tenantId, entityId, crBy);
			} else {
				combinationOptional = combinationRepository.findByTenantIdAndEntityId(tenantId, entityId);
			}

			if (combinationOptional.isEmpty() || combinationOptional.get().isEmpty()) {

				return ResponseEntity.ok(new ArrayList<ExternalDataCombinationResponseDTO>());
			}

			List<ExternalDataCombinationResponseDTO> response = new ArrayList<>();

			for (ExternalDataCombination combination : combinationOptional.get()) {

				ExternalDataCombinationResponseDTO dto = new ExternalDataCombinationResponseDTO();

				dto.setId(combination.getId());

				dto.setCombinationName(combination.getCombinationName());

				dto.setSearchOnDataFound(combination.getSearchOnDataFound());

				dto.setSets(combination.getSets() == null ? new ArrayList<>() : combination.getSets());

				response.add(dto);
			}

			LOGGER.info("Service : Get All DataSet Combinations - SUCCESS, count={}", response.size());

			return ResponseEntity.ok(response);

		} catch (SPRuntimeError e) {

			throw e;

		} catch (Exception e) {

			LOGGER.error("Service : Get All DataSet Combinations - ERROR", e);

			throw new SPRuntimeError("Failed to get DataSet combinations", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
