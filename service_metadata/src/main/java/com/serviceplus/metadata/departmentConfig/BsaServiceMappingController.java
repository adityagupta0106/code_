package com.serviceplus.metadata.departmentConfig;

import com.serviceplus.metadata.departmentConfig.dto.*;
import com.serviceplus.metadata.departmentConfig.service.BsaServiceMappingService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.utility.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller exposing REST endpoints for mapping BSA Configuration to Service
 * and configuring Task & Attribute In/Out mappings with node reference.
 */
@RestController
public class BsaServiceMappingController {

    private static final Logger logger = LoggerFactory.getLogger(BsaServiceMappingController.class);

    private final BsaServiceMappingService bsaServiceMappingService;

    public BsaServiceMappingController(BsaServiceMappingService bsaServiceMappingService) {
        this.bsaServiceMappingService = bsaServiceMappingService;
    }

    /**
     * 1. Get BSA Service Mapping for a specific service
     * GET /spgateway/bsa/service-mapping?serviceId=...
     */
    @GetMapping("/bsa/service-mapping")
    public ResponseEntity<?> getServiceMapping(@RequestParam Integer serviceId,
                                              HttpServletRequest request) {
        logger.info("Received request for BSA service mapping retrieval, serviceId={}", serviceId);
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaServiceMappingDTO response = bsaServiceMappingService.getMapping(serviceId, userSession);
            if (response == null) {
                return ResponseEntity.ok(Map.of(
                        "serviceId", serviceId,
                        "configured", false,
                        "message", "No BSA service mapping found for this service."
                ));
            }
            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            logger.warn("Security exception in getServiceMapping: {}", se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorEnvelope(HttpStatus.FORBIDDEN.value(), se.getMessage()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error in getServiceMapping: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorEnvelope(HttpStatus.BAD_REQUEST.value(), iae.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error in getServiceMapping", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred: " + ex.getMessage()));
        }
    }

    /**
     * 2. Save / Update BSA Service Mapping and Attribute In/Out mappings
     * POST /spgateway/bsa/service-mapping
     */
    @PostMapping("/bsa/service-mapping")
    public ResponseEntity<?> saveServiceMapping(@Valid @RequestBody BsaServiceMappingRequestDTO mappingRequest,
                                               HttpServletRequest request) {
        logger.info("Received request to save BSA service mapping for serviceId={}, bsaConfigId={}",
                mappingRequest.getServiceId(), mappingRequest.getBsaConfigId());
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaServiceMappingDTO response = bsaServiceMappingService.saveMapping(mappingRequest, userSession);
            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            logger.warn("Security exception in saveServiceMapping: {}", se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorEnvelope(HttpStatus.FORBIDDEN.value(), se.getMessage()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error in saveServiceMapping: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorEnvelope(HttpStatus.BAD_REQUEST.value(), iae.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error in saveServiceMapping", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred: " + ex.getMessage()));
        }
    }

    /**
     * 3. Get available BSA configurations to map to a service
     * GET /spgateway/bsa/service-mapping/available-configurations
     */
    @GetMapping("/bsa/service-mapping/available-configurations")
    public ResponseEntity<?> getAvailableConfigurations(HttpServletRequest request) {
        logger.info("Received request for available BSA configurations");
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            List<BsaConfigurationResponseDTO> response = bsaServiceMappingService.getAvailableConfigurations(userSession);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Internal error in getAvailableConfigurations", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred: " + ex.getMessage()));
        }
    }

    /**
     * 4. Get eligible tasks and attributes of a service for BSA mapping dropdown
     * GET /spgateway/bsa/service-mapping/tasks-attributes?serviceId=...
     */
    @GetMapping("/bsa/service-mapping/tasks-attributes")
    public ResponseEntity<?> getTasksAndAttributes(@RequestParam Integer serviceId,
                                                  HttpServletRequest request) {
        logger.info("Received request for tasks and attributes for serviceId={}", serviceId);
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaServiceTaskAttributeDTO response = bsaServiceMappingService.getServiceTasksAndAttributes(serviceId, userSession);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Internal error in getTasksAndAttributes", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred: " + ex.getMessage()));
        }
    }

    private Map<String, Object> buildErrorEnvelope(int statusCode, String message) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", statusCode);
        errorMap.put("error", message);
        errorMap.put("timestamp", System.currentTimeMillis());
        return errorMap;
    }
}
