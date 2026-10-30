package com.serviceplus.metadata.correlationmapping.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.correlationmapping.dto.DataSetMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.dto.DeleteExternalDataMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataCombinationRequestDTO;
import com.serviceplus.metadata.correlationmapping.dto.ExternalDataMappingRequestDTO;
import com.serviceplus.metadata.correlationmapping.service.ExternalDataCombinationService;
import com.serviceplus.metadata.correlationmapping.service.ExternalDataMappingService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ExternalDataMappingController {

	private static final Logger LOGGER = LogManager.getLogger(ExternalDataMappingController.class);

	@Autowired
	private ExternalDataMappingService externalDataMappingService;
	@Autowired
	private ExternalDataCombinationService combinationService;

	@GetMapping("/dataset/approved")
	public ResponseEntity<?> getApprovedDataSet(HttpServletRequest request) {

		LOGGER.info("Controller : Get Approved DataSet - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Get Approved DataSet - Unauthorized access");

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return combinationService.getApprovedDataSet(userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.info("Controller : Get Approved DataSet - END");

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/dataset/combination")
	public ResponseEntity<?> saveOrEditCombination(@RequestBody @Valid ExternalDataCombinationRequestDTO requestDTO,
			HttpServletRequest request) {

		LOGGER.info("Controller : Save/Edit DataSet Combination - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return combinationService.saveOrEditCombination(requestDTO, userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.info("Controller : Save/Edit DataSet Combination - END");
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/dataset/combination")
	public ResponseEntity<?> getAllCombinations(HttpServletRequest request) {

		LOGGER.info("Controller : Get All DataSet Combinations - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return combinationService.getAllCombinations(userSessionDetails, true);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.info("Controller : Get All DataSet Combinations - END");
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/dataset/combination/all")
	public ResponseEntity<?> getAllEntityCombinations(HttpServletRequest request) {

		LOGGER.info("Controller : Get All Entity DataSet Combinations - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			return combinationService.getAllCombinations(userSessionDetails, false);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.info("Controller : Get All Entity DataSet Combinations - END");
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/datasets/mapping")
	public ResponseEntity<?> getAllDataSetForMapping(@RequestBody @Valid DataSetMappingRequestDTO requestDTO,
			HttpServletRequest request) {

		LOGGER.info("Controller : Get All Data Set For Mapping - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Get All Data Set For Mapping " + "- Unauthorized access attempt");

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			LOGGER.debug(
					"Controller : Get All Data Set For Mapping " + "- userId={}, tenantId={}, "
							+ "sources={}, serviceStatus={}",
					userSessionDetails.getUserID(), userSessionDetails.getTenantId(), requestDTO.getSources(),
					requestDTO.getServiceStatus());

			return externalDataMappingService.getAllDataSetForMapping(requestDTO.getSources(),
					requestDTO.getServiceStatus(), userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Controller : Get All Data Set For Mapping " + "- Unexpected exception", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/datasets")
	public ResponseEntity<?> getAllDataSets(HttpServletRequest request) {

		LOGGER.info("Controller : Get All External DataSets - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Get All External DataSets " + "- Unauthorized access attempt");

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			LOGGER.debug("Controller : Get All External DataSets " + "- userId={}", userSessionDetails.getUserID());

			return externalDataMappingService.getAllDataSets(userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Controller : Get All External DataSets " + "- Exception occurred ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/dataset")
	public ResponseEntity<?> save(@RequestBody @Valid ExternalDataMappingRequestDTO mappingRequestDTO,
			HttpServletRequest request) {

		LOGGER.info("Controller : Save/Edit External DataSet - START");

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Save/Edit External DataSet " + "- Unauthorized access attempt");

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			LOGGER.debug("Controller : Save/Edit External DataSet " + "- userId={}, payload={}",
					userSessionDetails.getUserID(), mappingRequestDTO);

			return externalDataMappingService.save(mappingRequestDTO, userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Controller : Save/Edit External DataSet " + "- Exception occurred | payload={}",
					mappingRequestDTO, ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/dataset/delete")
	public ResponseEntity<?> delete(@RequestBody @Valid DeleteExternalDataMappingRequestDTO requestDTO,
			HttpServletRequest request) {

		LOGGER.info("Controller : Delete External DataSet - START | mappingId={}",
				requestDTO != null ? requestDTO.getId() : null);

		try {

			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {

				LOGGER.warn("Controller : Delete External DataSet - Unauthorized access attempt | mappingId={}",
						requestDTO != null ? requestDTO.getId() : null);

				throw new SPRuntimeError("Unauthorized Access", HttpStatus.UNAUTHORIZED);
			}

			LOGGER.debug("Controller : Delete External DataSet - userId={}, mappingId={}",
					userSessionDetails.getUserID(), requestDTO != null ? requestDTO.getId() : null);

			return externalDataMappingService.delete(requestDTO, userSessionDetails);

		} catch (SPRuntimeError ex) {

			throw ex;

		} catch (Exception ex) {

			LOGGER.error("Controller : Delete External DataSet - Exception occurred | mappingId={}",
					requestDTO != null ? requestDTO.getId() : null, ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}