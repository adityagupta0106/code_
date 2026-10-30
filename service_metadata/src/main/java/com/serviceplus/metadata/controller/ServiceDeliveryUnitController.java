package com.serviceplus.metadata.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.AdoptSubmissionLocationsRequestDTO;
import com.serviceplus.metadata.dto.LocationSuspensionRequestDTO;
import com.serviceplus.metadata.dto.RolloutScopeUpsertDTO;
import com.serviceplus.metadata.dto.SduActivationRequestDTO;
import com.serviceplus.metadata.dto.ServiceAdoptionPolicyDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.SduAdoptionService;
import com.serviceplus.metadata.service.ServiceDeliveryUnitService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/sdu")
@RestController
public class ServiceDeliveryUnitController {

	private final ServiceDeliveryUnitService deliveryUnitService;
	private final SduAdoptionService sduAdoptionService;

	private static final Logger LOG = LogManager.getLogger("ServiceDeliveryUnitController");

	@Autowired
	public ServiceDeliveryUnitController(ServiceDeliveryUnitService deliveryUnitService,
										 SduAdoptionService sduAdoptionService) {
		this.deliveryUnitService = deliveryUnitService;
		this.sduAdoptionService = sduAdoptionService;
	}

	@GetMapping("/submission-level")
	public ResponseEntity<?> getSubmissionLocation(@RequestParam Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return deliveryUnitService.getSubmissionLocation(serviceId, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Get Submission Location for service id {}, {}", serviceId, ex);
				ex.printStackTrace();
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/get")
	public ResponseEntity<?> getServiceCoverage(@RequestParam Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return deliveryUnitService.getServiceDeliveryUnit(serviceId, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Get Service Coverage for service id {}, {}", serviceId, ex);
				ex.printStackTrace();
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/upsert")
	public ResponseEntity<?> saveServiceDeliveryUnit(@Valid @RequestBody ServiceDeliveryUnitDTO serviceDeliveryUnitDTO,
			HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request,
					serviceDeliveryUnitDTO.getServiceId(), responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return deliveryUnitService.saveServiceDeliveryUnit(serviceDeliveryUnitDTO, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Save Service Coverage for service id {}, {}", serviceDeliveryUnitDTO.getServiceId(), ex);
				ex.printStackTrace();
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/hierarchy")
	public ResponseEntity<?> getHierarchy(@RequestParam Integer serviceId,
										  @RequestParam(required = false) Integer sduLocationId,
										  HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.getHierarchyView(serviceId, sduLocationId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Get hierarchy view error for service id {}", serviceId, ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/sdu-configuration-status")
	public ResponseEntity<?> getSduConfigurationStatus(@RequestParam Integer serviceId,
													   @RequestParam Integer sduLocationId,
													   HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.getSduConfigurationStatus(serviceId, sduLocationId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Get SDU configuration status error for service id {}, sdu location id {}", serviceId, sduLocationId, ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/related-units")
	public ResponseEntity<?> getRelatedUnits(@RequestParam(required=false) Integer serviceId,
											 @RequestParam(required=false) Integer sourceLevelCode,
											 @RequestParam(required=false) Long sourceUnitCode,
											 @RequestParam Integer destLevelCode,
											 HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			if(sourceLevelCode==null)
				sourceLevelCode=userSessionDetails.getEntityLevelId();
			
			if(sourceUnitCode==null)
				sourceUnitCode=userSessionDetails.getLocationId().longValue();
			
			return sduAdoptionService.getRelatedUnitsFromLgd(serviceId, sourceLevelCode, sourceUnitCode, destLevelCode, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Get related units error for sourceLevelCode={}, sourceUnitCode={}, destLevelCode={}",
					sourceLevelCode, sourceUnitCode, destLevelCode, ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/adopt-submission-locations")
	public ResponseEntity<?> adoptSubmissionLocations(@Valid @RequestBody AdoptSubmissionLocationsRequestDTO requestDTO,
													  HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.adoptSubmissionLocations(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Adopt submission locations error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/activate-sdu")
	public ResponseEntity<?> activateSdu(@Valid @RequestBody SduActivationRequestDTO requestDTO,
										 HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.activateSdu(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Activate SDU error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/deactivate-sdu")
	public ResponseEntity<?> deactivateSdu(@RequestBody SduActivationRequestDTO requestDTO,
										   HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.deactivateSdu(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Deactivate SDU error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/suspend-submission-location")
	public ResponseEntity<?> suspendSubmissionLocation(@Valid @RequestBody LocationSuspensionRequestDTO requestDTO,
													   HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.suspendSubmissionLocation(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Suspend submission location error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/reactivate-submission-location")
	public ResponseEntity<?> reactivateSubmissionLocation(@Valid @RequestBody LocationSuspensionRequestDTO requestDTO,
														 HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.reactivateSubmissionLocation(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Reactivate submission location error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/unadopt-submission-locations")
	public ResponseEntity<?> unadoptSubmissionLocations(@Valid @RequestBody AdoptSubmissionLocationsRequestDTO requestDTO,
														HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.unadoptSubmissionLocations(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Unadopt submission locations error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/pending-adoption-summary")
	public ResponseEntity<?> getPendingAdoptionSummary(@RequestParam Integer serviceId,
													   HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.getPendingAdoptionSummary(serviceId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Get pending adoption summary error for service id {}", serviceId, ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/rollout-scope/upsert")
	public ResponseEntity<?> upsertRolloutScope(@Valid @RequestBody RolloutScopeUpsertDTO requestDTO,
												HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.upsertRolloutScope(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Upsert rollout scope error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/rollout-scope/get")
	public ResponseEntity<?> getRolloutScope(@RequestParam Integer serviceId,
											HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.getRolloutScope(serviceId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Get rollout scope error for service id {}", serviceId, ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/adoption-policy/save")
	public ResponseEntity<?> saveServiceAdoptionPolicy(@Valid @RequestBody ServiceAdoptionPolicyDTO requestDTO,
													   HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.saveServiceAdoptionPolicy(requestDTO, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Save service adoption policy error for service id {}", requestDTO.getServiceId(), ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/adoption-policy/get")
	public ResponseEntity<?> getServiceAdoptionPolicy(@RequestParam Integer serviceId,
													 HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.getServiceAdoptionPolicy(serviceId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			LOG.error("Get service adoption policy error for service id {}", serviceId, ex);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping("/adoption-policy/list")
	public ResponseEntity<?> getServiceAdoptionPolicy(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return sduAdoptionService.getServiceAdoptionPolicyList(userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
