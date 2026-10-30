package com.serviceplus.metadata.departmentConfig.service;

import com.serviceplus.metadata.departmentConfig.config.BsaRegistrationProperties;
import com.serviceplus.metadata.departmentConfig.dto.*;
import com.serviceplus.metadata.departmentConfig.entity.BsaConfiguration;
import com.serviceplus.metadata.departmentConfig.entity.BsaRegistrationStatus;
import com.serviceplus.metadata.departmentConfig.repository.BsaConfigurationRepository;
import com.serviceplus.metadata.departmentConfig.util.BsaCryptoUtil;
import com.serviceplus.metadata.dto.UserSessionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service orchestrating BSA configuration and onboarding using the
 * BsaConfiguration table (schm_sp.bsa_configuration) from integrated plugins.
 * Credentials received from BSA registration API are persisted in plain text,
 * and email notification follows the publishMail pattern from FormSubscriptionService.
 */
@Service
public class BsaConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(BsaConfigurationService.class);

    private final BsaConfigurationRepository repository;
    private final BsaRegistrationClient registrationClient;
    private final BsaPdfGenerationService pdfService;
    private final BsaEmailService emailService;
    private final BsaRegistrationProperties properties;

    public BsaConfigurationService(BsaConfigurationRepository repository,
                                  BsaRegistrationClient registrationClient,
                                  BsaPdfGenerationService pdfService,
                                  BsaEmailService emailService,
                                  BsaRegistrationProperties properties) {
        this.repository = repository;
        this.registrationClient = registrationClient;
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.properties = properties;
    }

    /**
     * Resolves and returns the existing BSA configuration for the authenticated Department Admin.
     * Secrets (password, encryption key, IV) are masked before returning to the UI.
     */
    @Transactional(readOnly = true)
    public BsaConfigurationResponseDTO getConfiguration(UserSessionDTO userSession, Long requestedDepartmentId) {
        Long departmentId = resolveAndValidateDepartmentId(userSession, requestedDepartmentId);
        String tenantId = resolveTenantId(userSession);

        logger.info("Fetching BSA configuration for departmentId={}", departmentId);

        Optional<BsaConfiguration> existingOpt = repository.findByEntityIdAndActiveStatusTrueOrderByIdDesc(departmentId.intValue());
        BsaConfigurationResponseDTO response = new BsaConfigurationResponseDTO();
        response.setDepartmentId(departmentId);
        response.setTenantId(tenantId);

        if (existingOpt.isEmpty()) {
            response.setConfigured(false);
            response.setCanGenerate(true);
            response.setRegistrationStatus(BsaRegistrationStatus.NO_CONFIGURATION.name());
            response.setMessage("No BSA configuration found for department. Configuration generation is available.");
            return response;
        }

        BsaConfiguration config = existingOpt.get();
        response.setConfigured(true);
        response.setCanGenerate(false);
        response.setDepartmentName(config.getSchemeName());
        response.setServiceCode(config.getServiceCode());
        response.setStateCode(config.getsCode());
        response.setBsaUserId(config.getUserNameProd() != null ? config.getUserNameProd() : config.getUserName());
        response.setRegistrationStatus(BsaRegistrationStatus.REGISTERED.name());
        response.setStagingUrl(config.getStagingUrl());
        response.setProdUrl(config.getProdUrl());
        response.setAdminEmail(userSession != null ? userSession.getEmailId() : null);

        // Mask credentials before returning to UI
        if (config.getPassword() != null) {
            response.setMaskedPassword(BsaCryptoUtil.maskPassword(config.getPassword()));
        }
        if (config.getKeyProd() != null) {
            response.setMaskedEncryptionKey(BsaCryptoUtil.maskSecret(config.getKeyProd()));
        }
        if (config.getIvProd() != null) {
            response.setMaskedIv(BsaCryptoUtil.maskSecret(config.getIvProd()));
        }
        if (config.getKeyStaging() != null) {
            response.setMaskedKeyStaging(BsaCryptoUtil.maskSecret(config.getKeyStaging()));
        }
        if (config.getIvStaging() != null) {
            response.setMaskedIvStaging(BsaCryptoUtil.maskSecret(config.getIvStaging()));
        }

        response.setMessage("BSA configuration retrieved successfully in read-only mode.");
        return response;
    }

    /**
     * Generates credentials, calls external BSA / Mock registration API,
     * extracts bsa_staging_url, bsa_url, user_name, password, key_iv from the response,
     * persists them in plain text into schm_sp.bsa_configuration,
     * generates the certificate PDF, and dispatches email via publishMail logic.
     */
    public BsaGenerateConfigurationResponseDTO generateConfiguration(UserSessionDTO userSession) {
        Long departmentId = resolveAndValidateDepartmentId(userSession, null);
        String tenantId = resolveTenantId(userSession);

        logger.info("Initiating BSA configuration generation for baseServiceId/departmentId={}", departmentId);

        // 1. Check existing configuration to prevent duplicate registration
        Optional<BsaConfiguration> existingOpt = repository.findByEntityIdAndActiveStatusTrueOrderByIdDesc(departmentId.intValue());
        if (existingOpt.isPresent()) {
            BsaConfiguration existing = existingOpt.get();
            logger.warn("Department departmentId={} is already registered with ServiceCode={}",
                    departmentId, existing.getServiceCode());
            BsaGenerateConfigurationResponseDTO dupResponse = new BsaGenerateConfigurationResponseDTO();
            dupResponse.setSuccess(false);
            dupResponse.setDepartmentId(departmentId);
            dupResponse.setTenantId(tenantId);
            dupResponse.setServiceCode(existing.getServiceCode());
            dupResponse.setRegistrationStatus(BsaRegistrationStatus.REGISTERED.name());
            dupResponse.setMessage("Department is already registered with BSA. Service Code: " + existing.getServiceCode());
            return dupResponse;
        }

        // 2. Determine State Code
        int stateCode = determineStateCode(userSession, null);

        // Department / Scheme Name & Admin Email
        String deptName = (userSession != null && userSession.getLocationName() != null ? userSession.getLocationName() : "Department " + departmentId);

        String adminEmail = (userSession != null && userSession.getEmailId() != null && !userSession.getEmailId().trim().isEmpty()) ?
                userSession.getEmailId().trim() : "admin@sp.gov.in";

        // 3. Generate initial credentials & request ID for external call
        String plainPassword = BsaCryptoUtil.generateStrongPassword(16);
        String plainKey = properties.getEncryptionKey();
        String plainIv = properties.getEncryptionIv();
        String bsaUserId = "BSA_SP_" + departmentId + "_" + BsaCryptoUtil.generateRandomHex(4).toUpperCase();
        String requestId = UUID.randomUUID().toString();

        // 4. Call External BSA Registration Service / Mock API
        BsaRegistrationExternalRequestDTO extRequest = new BsaRegistrationExternalRequestDTO();
        extRequest.setRequestId(requestId);
        extRequest.setDepartmentId(departmentId);
        extRequest.setTenantId(tenantId);
        extRequest.setDepartmentName(deptName);
        extRequest.setStateCode(stateCode);
        extRequest.setBsaUserId(bsaUserId);
        extRequest.setPassword(plainPassword);
        extRequest.setEncryptionKey(plainKey);
        extRequest.setInitializationVector(plainIv);
        extRequest.setPublicKey(properties.getPublicKey());
        extRequest.setAdminEmail(adminEmail);
        extRequest.setEndpointVersion(properties.getRegistration().getEndpointVersion());

        BsaGenerateConfigurationResponseDTO response = new BsaGenerateConfigurationResponseDTO();
        response.setDepartmentId(departmentId);
        response.setTenantId(tenantId);
        response.setStateCode(stateCode);

        try {
            BsaRegistrationExternalResponseDTO extResponse = registrationClient.registerDepartment(extRequest);

            if (extResponse.isSuccess() && extResponse.getServiceCode() != null) {
                // 5. Extract bsa_staging_url, bsa_url, user_name, password, key_iv from API response
                String stagingUrl = extResponse.getBsaStagingUrl();
                String prodUrl = extResponse.getBsaUrl();
                String finalUserName = extResponse.getUserName() ;
                String finalPassword = extResponse.getPassword() ;
                String userNameStaging = extResponse.getBsaStagingUrl();
                String passwordStaging = extResponse.getPassword();
                

                // Extract key and iv from key_iv or key_prod / iv_prod
                String finalKey = null;
                String finalIv = null;
                if (extResponse.getKeyIv() != null && !extResponse.getKeyIv().isEmpty()) {
                    String keyIv = extResponse.getKeyIv();
                    if (keyIv.contains(":")) {
                        String[] parts = keyIv.split(":", 2);
                        finalKey = parts[0];
                        finalIv = parts.length > 1 ? parts[1] : "";
                    } else if (keyIv.length() >= 48) {
                        finalKey = keyIv.substring(0, 32);
                        finalIv = keyIv.substring(32, 48);
                    } else if (keyIv.length() >= 32) {
                        finalKey = keyIv.substring(0, 32);
                        finalIv = keyIv.substring(32);
                    }
                }
                if (finalKey == null || finalKey.isEmpty()) {
                    finalKey = extResponse.getKeyProd() != null ? extResponse.getKeyProd() : plainKey;
                }
                if (finalIv == null || finalIv.isEmpty()) {
                    finalIv = extResponse.getIvProd() != null ? extResponse.getIvProd() : plainIv;
                }

                // Extract key and iv of staging
                String finalKeyStaging = extResponse.getKeyStaging();
                String finalIvStaging = extResponse.getIvStaging();
                if ((finalKeyStaging == null || finalIvStaging == null) && extResponse.getKeyIv() != null) {
                    String keyIv = extResponse.getKeyIv();
                    if (keyIv.contains("#")) {
                        String[] parts = keyIv.split("#", 2);
                        if (finalKeyStaging == null && parts.length > 0) finalKeyStaging = parts[0];
                        if (finalIvStaging == null && parts.length > 1) finalIvStaging = parts[1];
                    } else if (keyIv.contains(":")) {
                        String[] parts = keyIv.split(":", 2);
                        if (finalKeyStaging == null && parts.length > 0) finalKeyStaging = parts[0];
                        if (finalIvStaging == null && parts.length > 1) finalIvStaging = parts[1];
                    }
                }
                if (finalKeyStaging == null) finalKeyStaging = plainKey;
                if (finalIvStaging == null) finalIvStaging = plainIv;

                // 6. Save in schm_sp.bsa_configuration table in PLAIN TEXT
                BsaConfiguration config = new BsaConfiguration();
                config.setEntityId(departmentId.intValue());
                config.setSchemeCode(extResponse.getServiceCode());
                config.setSchemeName(deptName);
                config.setUserName(bsaUserId);
                config.setPassword(plainPassword); 
                config.setsCode(stateCode);
                config.setServiceCode(extResponse.getServiceCode());
                config.setStagingUrl(stagingUrl);
                config.setProdUrl(prodUrl);
                config.setUserNameStaging(userNameStaging);
                config.setPasswordStaging(passwordStaging);
                config.setUserNameProd(finalUserName); 
                config.setPasswordProd(finalPassword); 
                config.setKeyProd(finalKey);           
                config.setIvProd(finalIv);             
                config.setKeyStaging(finalKeyStaging); 
                config.setIvStaging(finalIvStaging);   
                config.setActiveStatus(true);
                config.setConfigBy(userSession.getUserID());
                config.setConfigCreatedOn(new Date());
                config.setModifiedBy(userSession.getUserID());
                config.setModifiedOn(new Date());
                config.setTenantId(userSession.getTenantId());

                config = repository.save(config);
                logger.info("BSA configuration successfully saved in schm_sp.bsa_configuration with ID={}", config.getId());

                // 7. Generate Configuration PDF
                byte[] pdfBytes = null;
                boolean pdfSuccess = false;
                try {
                    pdfBytes = pdfService.generateConfigurationPdf(config, requestId, adminEmail, finalPassword, finalKey, finalIv);
                    pdfSuccess = true;
                } catch (Exception pdfEx) {
                    logger.error("PDF generation failed for baseServiceId={}", departmentId, pdfEx);
                }

                boolean emailSent = false;
                if (pdfBytes != null) {
                    try {
                        emailSent = emailService.sendConfigurationEmail(
                                tenantId,
                                adminEmail,
                                properties.getSupportDesk().getEmail(),
                                config.getServiceCode(),
                                finalUserName,
                                stateCode,
                                pdfBytes
                        );
                    } catch (Exception emailEx) {
                        logger.warn("Email notification failed for baseServiceId={}: {}", departmentId, emailEx.getMessage());
                    }
                }

                response.setSuccess(true);
                response.setServiceCode(config.getServiceCode());
                response.setBsaUserId(finalUserName);
                response.setRegistrationStatus(BsaRegistrationStatus.REGISTERED.name());
                response.setMaskedPassword(BsaCryptoUtil.maskPassword(finalPassword));
                response.setMaskedEncryptionKey(BsaCryptoUtil.maskSecret(finalKey));
                response.setMaskedIv(BsaCryptoUtil.maskSecret(finalIv));
                response.setMaskedKeyStaging(BsaCryptoUtil.maskSecret(finalKeyStaging));
                response.setMaskedIvStaging(BsaCryptoUtil.maskSecret(finalIvStaging));
                response.setPdfGenerated(pdfSuccess);
                response.setEmailDelivered(emailSent);
                response.setMessage("Department registered successfully with BSA. Service Code: " + config.getServiceCode());
                return response;

            } else {
                String errMsg = extResponse.getMessage() != null ? extResponse.getMessage() : "Registration failed at BSA Gateway";
                response.setSuccess(false);
                response.setRegistrationStatus(BsaRegistrationStatus.FAILED.name());
                response.setMessage(errMsg);
                return response;
            }

        } catch (Exception e) {
            logger.error("Exception during BSA external registration for departmentId={}", departmentId, e);
            response.setSuccess(false);
            response.setRegistrationStatus(BsaRegistrationStatus.FAILED.name());
            response.setMessage("BSA registration failed: " + e.getMessage());
            return response;
        }
    }

    /**
     * Returns the registration status and Service Code (if available).
     */
    @Transactional(readOnly = true)
    public BsaConfigurationStatusResponseDTO getStatus(UserSessionDTO userSession, Long requestedDepartmentId) {
        Long departmentId = resolveAndValidateDepartmentId(userSession, requestedDepartmentId);
        String tenantId = resolveTenantId(userSession);

        logger.info("Checking BSA configuration status for baseServiceId/departmentId={}", departmentId);

        Optional<BsaConfiguration> existingOpt = repository.findByEntityIdAndActiveStatusTrueOrderByIdDesc(departmentId.intValue());
        BsaConfigurationStatusResponseDTO response = new BsaConfigurationStatusResponseDTO();
        response.setDepartmentId(departmentId);
        response.setTenantId(tenantId);

        if (existingOpt.isEmpty()) {
            response.setHasConfiguration(false);
            response.setRegistrationStatus(BsaRegistrationStatus.NO_CONFIGURATION.name());
            response.setMessage("No configuration found for department");
            return response;
        }

        BsaConfiguration config = existingOpt.get();
        response.setHasConfiguration(true);
        response.setRegistrationStatus(BsaRegistrationStatus.REGISTERED.name());
        response.setServiceCode(config.getServiceCode());
        response.setLastUpdated(config.getModifiedOn());
        response.setMessage("Department is registered with BSA");

        return response;
    }

    /**
     * Resends the configuration document without regenerating credentials or re-registering at BSA.
     */
    @Transactional
    public BsaResendDocumentResponseDTO resendDocument(UserSessionDTO userSession, BsaResendDocumentRequestDTO request) {
        Long departmentId = resolveAndValidateDepartmentId(userSession, request.getDepartmentId());
        String tenantId = resolveTenantId(userSession);

        logger.info("Resending BSA configuration document for baseServiceId/departmentId={}", departmentId);

        Optional<BsaConfiguration> existingOpt = repository.findByEntityIdAndActiveStatusTrueOrderByIdDesc(departmentId.intValue());
        BsaResendDocumentResponseDTO response = new BsaResendDocumentResponseDTO();

        if (existingOpt.isEmpty() || !Boolean.TRUE.equals(existingOpt.get().getActiveStatus())) {
            response.setSuccess(false);
            response.setMessage("Cannot resend document: Department is not registered with BSA.");
            return response;
        }

        BsaConfiguration config = existingOpt.get();
        String adminEmail = userSession != null && userSession.getEmailId() != null ? userSession.getEmailId() : "admin@sp.gov.in";

        response.setServiceCode(config.getServiceCode());
        response.setAdminEmail(adminEmail);
        response.setSupportDeskEmail(properties.getSupportDesk().getEmail());

        try {
            // Read plain credentials from table
            byte[] pdfBytes = pdfService.generateConfigurationPdf(
                    config,
                    "RESEND-" + config.getId(),
                    adminEmail,
                    config.getPassword(),
                    config.getKeyProd(),
                    config.getIvProd()
            );

            boolean emailSent = emailService.sendConfigurationEmail(
                    tenantId,
                    adminEmail,
                    properties.getSupportDesk().getEmail(),
                    config.getServiceCode(),
                    config.getUserNameProd(),
                    config.getsCode(),
                    pdfBytes
            );

            response.setSuccess(emailSent);
            response.setSentAt(new Date());
            response.setMessage(emailSent ? "Document resent successfully to " + adminEmail :
                    "Document generated but email dispatch service was unavailable. Status recorded.");
            return response;

        } catch (Exception e) {
            logger.error("Failed to resend BSA document for departmentId={}", departmentId, e);
            response.setSuccess(false);
            response.setMessage("Failed to regenerate and resend document: " + e.getMessage());
            return response;
        }
    }

    /**
     * Resolves the department ID from session and validates authorization against requested department ID.
     */
    public Long resolveAndValidateDepartmentId(UserSessionDTO userSession, Long requestedDepartmentId) {
        if (userSession == null) {
            if (requestedDepartmentId != null) {
                return requestedDepartmentId;
            }
            throw new IllegalArgumentException("Invalid user session: Missing user details header.");
        }

        Long sessionDepartmentId = userSession.getEntityId() != null ? Long.valueOf(userSession.getEntityId()) : null;

        if (sessionDepartmentId == null) {
            if (requestedDepartmentId != null) {
                return requestedDepartmentId;
            }
            throw new IllegalArgumentException("Department ID could not be resolved from user session.");
        }

        if (Boolean.TRUE.equals(userSession.getTopLevelAdmin())) {
            return requestedDepartmentId != null ? requestedDepartmentId : sessionDepartmentId;
        }

        if (requestedDepartmentId != null && !requestedDepartmentId.equals(sessionDepartmentId)) {
            logger.warn("Access denied: User entityId={} attempted to access departmentId={}",
                    sessionDepartmentId, requestedDepartmentId);
            throw new SecurityException("Access denied: Authenticated user is not authorized for department " + requestedDepartmentId);
        }

        return sessionDepartmentId;
    }

    private String resolveTenantId(UserSessionDTO userSession) {
        if (userSession != null && userSession.getTenantId() != null && !userSession.getTenantId().trim().isEmpty()) {
            return userSession.getTenantId().trim();
        }
        return "SERVICEPLUS";
    }

    private int determineStateCode(UserSessionDTO userSession, BsaGenerateConfigurationRequestDTO request) {
        
        if (userSession != null) {
            if (userSession.getClcId() != null && userSession.getClcId() == 0) {
                return 99;
            }
            if (userSession.getClcId() != null && userSession.getClcId() > 0) {
                return userSession.getClcId();
            }
            
        }

        return 99;
    }
}
