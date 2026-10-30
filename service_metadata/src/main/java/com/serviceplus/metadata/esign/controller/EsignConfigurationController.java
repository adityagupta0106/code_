package com.serviceplus.metadata.esign.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.esign.dto.*;
import com.serviceplus.metadata.esign.service.EsignTemplateService;
import com.serviceplus.metadata.esign.service.ServiceEsignConfigurationService;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.ServiceMasterDataService;
import com.serviceplus.metadata.utility.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/esign")
public class EsignConfigurationController {

    private static final Logger log = LoggerFactory.getLogger("esignLogger");

    private final EsignTemplateService templateService;
    private final ServiceEsignConfigurationService serviceConfigurationService;
    private final ObjectMapper objectMapper;
    private final ServiceMasterDataService masterDataService;

    public EsignConfigurationController(
            EsignTemplateService templateService,
            ServiceEsignConfigurationService serviceConfigurationService,
            ObjectMapper objectMapper, ServiceMasterDataService masterDataService) {
        this.templateService = templateService;
        this.serviceConfigurationService = serviceConfigurationService;
        this.objectMapper = objectMapper;
		this.masterDataService = masterDataService;
    }

    // ---------------------------------------------------------
    // Module A: eSign Template Management APIs
    // ---------------------------------------------------------

    @PostMapping(value = "/templates", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<EsignTemplateResponseDTO> createTemplate(
            @RequestPart(value = "template", required = false) String templateJson,
            @RequestPart(value = "templateJson", required = false) String templateJsonPart,
            @RequestPart("pfxFile") MultipartFile pfxFile,
            HttpServletRequest request) {

        log.info("Incoming create eSign template request");
        UserSessionDTO user = getUserSession(request);

        String jsonPayload = templateJson != null && !templateJson.trim().isEmpty() ? templateJson : templateJsonPart;
        EsignTemplateRequestDTO requestDTO;
        if (jsonPayload != null && !jsonPayload.trim().isEmpty()) {
            try {
                requestDTO = objectMapper.readValue(jsonPayload, EsignTemplateRequestDTO.class);
            } catch (Exception e) {
                log.error("Failed to parse template JSON", e);
                throw new SPRuntimeError("Invalid template JSON payload: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
        	throw new SPRuntimeError("Invalid template JSON payload: " , HttpStatus.BAD_REQUEST);
        }
        log.info("PFX file is null: {}", pfxFile == null);

        if (pfxFile != null) {
            log.info("PFX filename: {}, size: {} bytes",
                    pfxFile.getOriginalFilename(),
                    pfxFile.getSize());
        }
        EsignTemplateResponseDTO response = templateService.createTemplate(requestDTO, pfxFile, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<EsignTemplateResponseDTO>> getTemplates(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        log.info("Fetching eSign templates. status={}", status);
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(templateService.getTemplates(status, user));
    }

    @GetMapping("/template-details")
    public ResponseEntity<EsignTemplateResponseDTO> getTemplateDetails(
            @RequestParam Long templateId,
            HttpServletRequest request) {

        log.info("Fetching eSign template details. templateId={}", templateId);
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(templateService.getTemplateDetails(templateId, user));
    }

    @PostMapping(value = "/templates/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EsignTemplateResponseDTO> updateTemplate(
            @RequestPart(value = "template", required = false) String templateJson,
            @RequestPart(value = "pfxFile", required = false) MultipartFile pfxFile,
            @RequestBody(required = false) EsignTemplateRequestDTO bodyRequest,
            HttpServletRequest request) {

        log.info("Incoming update eSign template request");
        UserSessionDTO user = getUserSession(request);

        EsignTemplateRequestDTO requestDTO = bodyRequest;
        if (requestDTO == null) {
            String jsonPayload = templateJson ;
            if (jsonPayload != null && !jsonPayload.trim().isEmpty()) {
                try {
                    requestDTO = objectMapper.readValue(jsonPayload, EsignTemplateRequestDTO.class);
                } catch (Exception e) {
                    throw new SPRuntimeError("Invalid template JSON payload: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            }else {
            	throw new SPRuntimeError("Invalid template JSON payload: ", HttpStatus.BAD_REQUEST);
            }
        }

        EsignTemplateResponseDTO response = templateService.updateTemplate(requestDTO, pfxFile, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/templates/status")
    public ResponseEntity<EsignTemplateResponseDTO> changeTemplateStatus(
            @RequestBody @Valid EsignTemplateStatusRequestDTO statusRequest,
            HttpServletRequest request) {

        log.info("Incoming change eSign template status request. templateId={}, status={}",
                statusRequest.getTemplateId(), statusRequest.getStatus());
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(templateService.updateTemplateStatus(statusRequest, user));
    }
    
    @GetMapping("/master-esign")
    public ResponseEntity<?> getEsignMaster() {
        return ResponseEntity.ok(masterDataService.getEsignMaster());
    }
    @GetMapping("/biometric-types")
    public ResponseEntity<?> getBiometricTypes() {
    	return ResponseEntity.ok(masterDataService.getBiometricTypes());
    }

    // ---------------------------------------------------------
    // Module B: Service-Level eSign Configuration APIs
    // ---------------------------------------------------------

    @GetMapping("/eligible-templates")
    public ResponseEntity<List<EsignTemplateResponseDTO>> getEligibleTemplates(
            @RequestParam Integer serviceId,
            HttpServletRequest request) {

        log.info("Fetching eligible eSign templates for serviceId={}", serviceId);
        validateServiceToken(request, serviceId);
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(serviceConfigurationService.getEligibleTemplates(serviceId, user));
    }

    @GetMapping("/configuration")
    public ResponseEntity<ServiceEsignConfigurationDTO> getServiceConfiguration(
            @RequestParam Integer serviceId,
            HttpServletRequest request) {

        log.info("Fetching service eSign configuration for serviceId={}", serviceId);
        validateServiceToken(request, serviceId);
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(serviceConfigurationService.getConfiguration(serviceId, user));
    }

    @PostMapping("/configuration")
    public ResponseEntity<ServiceEsignConfigurationDTO> saveServiceConfiguration(
            @RequestBody @Valid ServiceEsignConfigurationDTO configDTO,
            HttpServletRequest request) {

        log.info("Saving service eSign configuration for serviceId={}", configDTO.getServiceId());
        validateServiceToken(request, configDTO.getServiceId());
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(serviceConfigurationService.saveConfiguration(configDTO, user));
    }

    @PostMapping("/configuration/update")
    public ResponseEntity<ServiceEsignConfigurationDTO> updateServiceConfiguration(
            @RequestBody @Valid ServiceEsignConfigurationDTO configDTO,
            HttpServletRequest request) {

        log.info("Updating service eSign configuration for serviceId={}", configDTO.getServiceId());
        validateServiceToken(request, configDTO.getServiceId());
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(serviceConfigurationService.updateConfiguration(configDTO, user));
    }

    @PostMapping("/configuration/toggle")
    public ResponseEntity<ServiceEsignConfigurationDTO> toggleServiceEsign(
            @RequestBody @Valid ServiceEsignToggleDTO toggleDTO,
            HttpServletRequest request) {

        log.info("Toggling service eSign for serviceId={}, esignEnabled={}",
                toggleDTO.getServiceId(), toggleDTO.getEsignEnabled());
        validateServiceToken(request, toggleDTO.getServiceId());
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(serviceConfigurationService.toggleEsign(toggleDTO, user));
    }

    @GetMapping("/aadhaar-fields")
    public ResponseEntity<List<EsignAadhaarFieldOptionDTO>> getEligibleAadhaarFields(
            @RequestParam Integer serviceId,
            HttpServletRequest request) {

        log.info("Fetching eligible Aadhaar form fields for serviceId={}", serviceId);
        validateServiceToken(request, serviceId);
        UserSessionDTO user = getUserSession(request);
        return ResponseEntity.ok(serviceConfigurationService.getEligibleAadhaarFields(serviceId, user));
    }

   private void validateServiceToken(HttpServletRequest request, Integer serviceId) {
        if (serviceId != null) {
            JSONObject responseJson = new JSONObject();
            ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
            if (tokenValidationResult != null) {
                HttpStatus status = HttpStatus.resolve(tokenValidationResult.getStatusCode().value());
                String errorMsg = responseJson.optString("errorMessage", "Service token validation failed");
                throw new SPRuntimeError(errorMsg, status != null ? status : HttpStatus.BAD_REQUEST);
            }
        }
    }

    private UserSessionDTO getUserSession(HttpServletRequest request) {
        UserSessionDTO user = CommonUtil.getUserSessionDetails(request);
        if (user == null || user.getTenantId() == null || user.getUserID() == null) {
            throw new SPRuntimeError("Unauthorized: Missing or invalid USER-DETAILS ", HttpStatus.UNAUTHORIZED);
        }
        return user;
    }
}
