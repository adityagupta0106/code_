package com.serviceplus.metadata.esign.service.impl;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateJSONDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateRequestDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateResponseDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateStatusRequestDTO;
import com.serviceplus.metadata.esign.entity.EsignTemplate;
import com.serviceplus.metadata.esign.enums.EsignTemplateStatus;
import com.serviceplus.metadata.esign.repository.EsignTemplateRepository;
import com.serviceplus.metadata.esign.repository.ServiceEsignConfigurationRepository;
import com.serviceplus.metadata.esign.service.EsignTemplateService;
import com.serviceplus.metadata.esign.service.PfxCertificateStorageService;
import com.serviceplus.metadata.esign.service.PfxStorageResult;
import com.serviceplus.metadata.exception.SPRuntimeError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsignTemplateServiceImpl implements EsignTemplateService {

    private static final Logger log = LoggerFactory.getLogger("esignLogger");

    private final EsignTemplateRepository templateRepository;
    private final ServiceEsignConfigurationRepository serviceEsignRepository;
    private final PfxCertificateStorageService certificateStorageService;

    public EsignTemplateServiceImpl(EsignTemplateRepository templateRepository,
                                   ServiceEsignConfigurationRepository serviceEsignRepository,
                                   PfxCertificateStorageService certificateStorageService) {
        this.templateRepository = templateRepository;
        this.serviceEsignRepository = serviceEsignRepository;
        this.certificateStorageService = certificateStorageService;
    }

    @Override
    @Transactional
    public EsignTemplateResponseDTO createTemplate(EsignTemplateRequestDTO request, MultipartFile pfxFile, UserSessionDTO user) {
        validateUser(user);
        validateTemplateRequest(request);

        if (pfxFile == null || pfxFile.isEmpty()) {
            throw new SPRuntimeError("PFX certificate file is mandatory for template creation", HttpStatus.BAD_REQUEST);
        }

        String tenantId = user.getTenantId();
        if (templateRepository.existsByTenantIdAndTemplateNameIgnoreCase(tenantId, request.getTemplateName().getValue().toString())) {
            throw new SPRuntimeError("An eSign template with name '" + request.getTemplateName() + "' already exists in this tenant", HttpStatus.CONFLICT);
        }

        PfxStorageResult storageResult = certificateStorageService.storeCertificate(pfxFile, tenantId, request.getPfxPassword());

        LocalDateTime now = LocalDateTime.now();
        EsignTemplate entity = new EsignTemplate();
        entity.setTemplateName(request.getTemplateName().getValue().toString());
        entity.setTemplateName(request.getTemplateName());
        entity.setAspId(request.getAspId().trim());
        entity.setHasAppId(request.getHasAppId());
        entity.setAppId(Boolean.TRUE.equals(request.getHasAppId()) ? request.getAppId().trim() : null);
        entity.setPfxStorageReference(storageResult.getStorageReference());
        entity.setPfxFilename(storageResult.getOriginalFilename());
        entity.setPfxFileSize(storageResult.getFileSize());
        entity.setPfxFingerprint(storageResult.getSha256Fingerprint());
        entity.setCertificateExpiryDate(storageResult.getExpiryDate());
        entity.setCertificateSubjectDn(storageResult.getSubjectDn());
        entity.setCertificateIssuerDn(storageResult.getIssuerDn());
        entity.setPfxPassword(request.getPfxPassword());
        entity.setTenantId(tenantId);
        entity.setStatus(EsignTemplateStatus.INACTIVE);
        entity.setCreatedBy(user.getUserID());
        entity.setCreatedDate(now);
        entity.setUpdatedBy(user.getUserID());
        entity.setUpdatedDate(now);

        EsignTemplate saved = templateRepository.save(entity);
        log.info("eSign template created successfully. id={}, name={}, tenant={}", saved.getId(), saved.getTemplateName(), tenantId);

        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EsignTemplateResponseDTO> getTemplates(String status, UserSessionDTO user) {
        validateUser(user);
        String tenantId = user.getTenantId();

        List<EsignTemplate> templates;
        if (status != null && !status.trim().isEmpty()) {
            EsignTemplateStatus templateStatus = EsignTemplateStatus.fromString(status);
            templates = templateRepository.findByTenantIdAndStatus(tenantId, templateStatus);
        } else {
            templates = templateRepository.findByTenantId(tenantId);
        }

        return templates.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EsignTemplateResponseDTO getTemplateDetails(Long templateId, UserSessionDTO user) {
        validateUser(user);
        if (templateId == null) {
            throw new SPRuntimeError("Template ID parameter is mandatory", HttpStatus.BAD_REQUEST);
        }

        EsignTemplate template = templateRepository.findByIdAndTenantId(templateId, user.getTenantId())
                .orElseThrow(() -> new SPRuntimeError("eSign template not found with ID: " + templateId, HttpStatus.NOT_FOUND));

        return mapToResponseDTO(template);
    }

    @Override
    @Transactional
    public EsignTemplateResponseDTO updateTemplate(EsignTemplateRequestDTO request, MultipartFile pfxFile, UserSessionDTO user) {
        validateUser(user);
        if (request.getTemplateId() == null) {
            throw new SPRuntimeError("Template ID is mandatory for update", HttpStatus.BAD_REQUEST);
        }
        validateTemplateRequest(request);

        String tenantId = user.getTenantId();
        EsignTemplate existing = templateRepository.findByIdAndTenantId(request.getTemplateId(), tenantId)
                .orElseThrow(() -> new SPRuntimeError("eSign template not found with ID: " + request.getTemplateId(), HttpStatus.NOT_FOUND));

        if (templateRepository.existsByTenantIdAndTemplateNameIgnoreCaseAndIdNot(tenantId, request.getTemplateName().getValue().toString(), request.getTemplateId())) {
            throw new SPRuntimeError("Another eSign template with name '" + request.getTemplateName() + "' already exists", HttpStatus.CONFLICT);
        }

        existing.setTemplateName(request.getTemplateName().getValue().toString());
        existing.setAspId(request.getAspId().trim());
        existing.setHasAppId(request.getHasAppId());
        existing.setAppId(Boolean.TRUE.equals(request.getHasAppId()) ? request.getAppId().trim() : null);

        // If replacing PFX file
        if (pfxFile != null && !pfxFile.isEmpty()) {
            PfxStorageResult storageResult = certificateStorageService.storeCertificate(pfxFile, tenantId, request.getPfxPassword());
            existing.setPfxStorageReference(storageResult.getStorageReference());
            existing.setPfxFilename(storageResult.getOriginalFilename());
            existing.setPfxFileSize(storageResult.getFileSize());
            existing.setPfxFingerprint(storageResult.getSha256Fingerprint());
            existing.setCertificateExpiryDate(storageResult.getExpiryDate());
            existing.setCertificateSubjectDn(storageResult.getSubjectDn());
            existing.setCertificateIssuerDn(storageResult.getIssuerDn());
            if (storageResult.getEncryptedPassword() != null) {
                existing.setPfxPassword(storageResult.getEncryptedPassword());
            }
        } else if (request.getPfxPassword() != null && !request.getPfxPassword().trim().isEmpty()) {
            existing.setPfxPassword(request.getPfxPassword());
        }
        existing.setPfxPassword(request.getPfxPassword());
        existing.setUpdatedBy(user.getUserID());
        existing.setUpdatedDate(LocalDateTime.now());

        EsignTemplate updated = templateRepository.save(existing);
        log.info("eSign template updated successfully. id={}, name={}, tenant={}", updated.getId(), updated.getTemplateName(), tenantId);

        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public EsignTemplateResponseDTO updateTemplateStatus(EsignTemplateStatusRequestDTO request, UserSessionDTO user) {
        validateUser(user);
        if (request == null || request.getTemplateId() == null || request.getStatus() == null) {
            throw new SPRuntimeError("Template ID and status are mandatory", HttpStatus.BAD_REQUEST);
        }

        EsignTemplateStatus newStatus = EsignTemplateStatus.fromString(request.getStatus());
        String tenantId = user.getTenantId();

        EsignTemplate template = templateRepository.findByIdAndTenantId(request.getTemplateId(), tenantId)
                .orElseThrow(() -> new SPRuntimeError("eSign template not found with ID: " + request.getTemplateId(), HttpStatus.NOT_FOUND));

        if (newStatus == EsignTemplateStatus.INACTIVE && template.getStatus() == EsignTemplateStatus.ACTIVE) {
            // Guard against deactivating a template that is actively configured in a service
            boolean isActivelyUsed = serviceEsignRepository.existsByEsignTemplateIdAndEsignEnabledTrue(template.getId());
            if (isActivelyUsed) {
                throw new SPRuntimeError("Cannot deactivate eSign template because it is actively configured in one or more services. Please reconfigure those services first.", HttpStatus.CONFLICT);
            }
        }

        template.setStatus(newStatus);
        template.setUpdatedBy(user.getUserID());
        template.setUpdatedDate(LocalDateTime.now());

        EsignTemplate saved = templateRepository.save(template);
        log.info("eSign template status changed. id={}, newStatus={}, tenant={}", saved.getId(), newStatus, tenantId);

        return mapToResponseDTO(saved);
    }

    private void validateUser(UserSessionDTO user) {
        if (user == null || user.getTenantId() == null || user.getUserID() == null) {
            throw new SPRuntimeError("User session is invalid or unauthenticated", HttpStatus.UNAUTHORIZED);
        }
    }

    private void validateTemplateRequest(EsignTemplateRequestDTO request) {
        if (request == null) {
            throw new SPRuntimeError("Request body cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getTemplateName() == null || request.getTemplateName().getValue().toString().isEmpty()) {
            throw new SPRuntimeError("Template name is mandatory and cannot be blank", HttpStatus.BAD_REQUEST);
        }
        if (request.getAspId() == null || request.getAspId().trim().isEmpty()) {
            throw new SPRuntimeError("ASP ID is mandatory and cannot be blank", HttpStatus.BAD_REQUEST);
        }
        if (request.getHasAppId() == null) {
            throw new SPRuntimeError("hasAppId field is mandatory", HttpStatus.BAD_REQUEST);
        }
        if (Boolean.TRUE.equals(request.getHasAppId())) {
            if (request.getAppId() == null || request.getAppId().trim().isEmpty()) {
                throw new SPRuntimeError("App ID is mandatory when hasAppId is true", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private EsignTemplateResponseDTO mapToResponseDTO(EsignTemplate entity) {
        EsignTemplateResponseDTO dto = new EsignTemplateResponseDTO();
        dto.setTemplateId(entity.getId());
        dto.setTemplateName(entity.getTemplateName());
        dto.setStatus(entity.getStatus().name());
        EsignTemplateJSONDTO json = entity.getTemplateJson();
        if (json != null) {
            EsignTemplateJSONDTO safeJson = new EsignTemplateJSONDTO();
            safeJson.setTemplateName(json.getTemplateName());
            safeJson.setAspId(json.getAspId());
            safeJson.setHasAppId(json.getHasAppId());
            safeJson.setAppId(json.getAppId());
            safeJson.setPfxStorageReference(json.getPfxStorageReference());
            safeJson.setPfxFilename(json.getPfxFilename());
            safeJson.setPfxFileSize(json.getPfxFileSize());
            safeJson.setPfxFingerprint(json.getPfxFingerprint());
            safeJson.setCertificateExpiryDate(json.getCertificateExpiryDate());
            safeJson.setCertificateSubjectDn(json.getCertificateSubjectDn());
            safeJson.setCertificateIssuerDn(json.getCertificateIssuerDn());
            safeJson.setPfxPassword(json.getPfxPassword());
            dto.setTemplateJson(safeJson);
        }

        return dto;
    }
}
