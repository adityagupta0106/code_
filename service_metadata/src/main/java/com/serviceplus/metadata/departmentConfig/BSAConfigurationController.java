package com.serviceplus.metadata.departmentConfig;

import com.serviceplus.metadata.departmentConfig.dto.*;
import com.serviceplus.metadata.departmentConfig.service.BsaConfigurationService;
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
import java.util.Map;

/**
 * Controller exposing REST endpoints for Beneficiary Satyapan App (BSA)
 * department onboarding and configuration management in ServicePlus 2.0.
 */
@RestController
public class BSAConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(BSAConfigurationController.class);

    private final BsaConfigurationService bsaConfigurationService;

   
    public BSAConfigurationController(BsaConfigurationService bsaConfigurationService) {
        this.bsaConfigurationService = bsaConfigurationService;
    }

    /**
     * 1. Configuration retrieval API
     * GET /spgateway/bsa/configuration
     */
    @GetMapping("/bsa/configuration")
    public ResponseEntity<?> getConfiguration(@RequestParam(required = false) Long departmentId,
                                             HttpServletRequest request) {
        logger.info("Received request for BSA configuration retrieval, requested departmentId={}", departmentId);
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null && departmentId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaConfigurationResponseDTO response = bsaConfigurationService.getConfiguration(userSession, departmentId);
            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            logger.warn("Security exception in getConfiguration: {}", se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorEnvelope(HttpStatus.FORBIDDEN.value(), se.getMessage()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error in getConfiguration: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorEnvelope(HttpStatus.BAD_REQUEST.value(), iae.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error in getConfiguration", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred."));
        }
    }

    /**
     * 2. Generate and register configuration API
     * POST /spgateway/bsa/configuration/generate
     */
    @PostMapping("/bsa/configuration/generate")
    public ResponseEntity<?> generateConfiguration(
                                                  HttpServletRequest request) {
        
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaGenerateConfigurationResponseDTO response = bsaConfigurationService.generateConfiguration(userSession);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        } catch (SecurityException se) {
            logger.warn("Security exception in generateConfiguration: {}", se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorEnvelope(HttpStatus.FORBIDDEN.value(), se.getMessage()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error in generateConfiguration: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorEnvelope(HttpStatus.BAD_REQUEST.value(), iae.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error in generateConfiguration", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred during registration."));
        }
    }

    /**
     * 3. Configuration status API
     * GET /spgateway/bsa/configuration/status
     */
    @GetMapping("/bsa/configuration/status")
    public ResponseEntity<?> getStatus(@RequestParam(required = false) Long departmentId,
                                      HttpServletRequest request) {
        logger.info("Received request for BSA configuration status, requested departmentId={}", departmentId);
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null && departmentId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaConfigurationStatusResponseDTO response = bsaConfigurationService.getStatus(userSession, departmentId);
            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            logger.warn("Security exception in getStatus: {}", se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorEnvelope(HttpStatus.FORBIDDEN.value(), se.getMessage()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error in getStatus: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorEnvelope(HttpStatus.BAD_REQUEST.value(), iae.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error in getStatus", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred."));
        }
    }

    /**
     * 4. Resend document API
     * POST /spgateway/bsa/configuration/resend-document
     */
    @PostMapping("/bsa/configuration/resend-document")
    public ResponseEntity<?> resendDocument(@Valid @RequestBody BsaResendDocumentRequestDTO resendRequest,
                                           HttpServletRequest request) {
        logger.info("Received request for BSA document resend for departmentId={}", resendRequest.getDepartmentId());
        try {
            UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
            if (userSession == null && resendRequest.getDepartmentId() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorEnvelope(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Missing USER-DETAILS header."));
            }

            BsaResendDocumentResponseDTO response = bsaConfigurationService.resendDocument(userSession, resendRequest);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (SecurityException se) {
            logger.warn("Security exception in resendDocument: {}", se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorEnvelope(HttpStatus.FORBIDDEN.value(), se.getMessage()));
        } catch (IllegalArgumentException iae) {
            logger.warn("Validation error in resendDocument: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorEnvelope(HttpStatus.BAD_REQUEST.value(), iae.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error in resendDocument", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorEnvelope(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred."));
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
