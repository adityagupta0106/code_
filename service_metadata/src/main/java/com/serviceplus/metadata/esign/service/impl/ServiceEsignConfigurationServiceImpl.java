package com.serviceplus.metadata.esign.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.serviceplus.metadata.dto.FormDTO;
import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.esign.dto.EsignAadhaarFieldOptionDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateResponseDTO;
import com.serviceplus.metadata.esign.dto.ServiceEsignConfigurationDTO;
import com.serviceplus.metadata.esign.dto.ServiceEsignToggleDTO;
import com.serviceplus.metadata.esign.entity.EsignTemplate;
import com.serviceplus.metadata.esign.entity.ServiceEsignConfiguration;
import com.serviceplus.metadata.esign.enums.EsignTemplateStatus;
import com.serviceplus.metadata.esign.repository.EsignTemplateRepository;
import com.serviceplus.metadata.esign.repository.ServiceEsignConfigurationRepository;
import com.serviceplus.metadata.esign.service.ServiceEsignConfigurationService;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.FormDesignerFeignClient;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import com.serviceplus.metadata.utility.CommonUtil;

@Service
public class ServiceEsignConfigurationServiceImpl implements ServiceEsignConfigurationService {

    private static final Logger log = LoggerFactory.getLogger("esignLogger");

    private final ServiceEsignConfigurationRepository serviceEsignRepository;
    private final EsignTemplateRepository templateRepository;
    private final IServiceLogRepository serviceLogRepository;
    private final IServiceTemplateRepository serviceTemplateRepository;
    private final FormDesignerFeignClient formDesignerFeignClient;

    public ServiceEsignConfigurationServiceImpl(
            ServiceEsignConfigurationRepository serviceEsignRepository,
            EsignTemplateRepository templateRepository,
            IServiceLogRepository serviceLogRepository,
            IServiceTemplateRepository serviceTemplateRepository,
            FormDesignerFeignClient formDesignerFeignClient) {
        this.serviceEsignRepository = serviceEsignRepository;
        this.templateRepository = templateRepository;
        this.serviceLogRepository = serviceLogRepository;
        this.serviceTemplateRepository = serviceTemplateRepository;
        this.formDesignerFeignClient = formDesignerFeignClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EsignTemplateResponseDTO> getEligibleTemplates(Integer serviceId, UserSessionDTO user) {
        validateUser(user);
        validateServiceAccess(serviceId, user);

        List<EsignTemplate> activeTemplates = templateRepository.findByTenantIdAndStatus(user.getTenantId(), EsignTemplateStatus.ACTIVE);
        return activeTemplates.stream().map(this::mapTemplateToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceEsignConfigurationDTO getConfiguration(Integer serviceId, UserSessionDTO user) {
        validateUser(user);
        validateServiceAccess(serviceId, user);

        ServiceEsignConfiguration config = serviceEsignRepository
                .findByServiceIdAndTenantId(serviceId, user.getTenantId())
                .orElse(null);

        if (config == null) {
            ServiceEsignConfigurationDTO defaultDto = new ServiceEsignConfigurationDTO();
            defaultDto.setServiceId(serviceId);
            defaultDto.setEsignEnabled(false);
            defaultDto.setPrintSignerName(false);
            defaultDto.setBiometricEnabled(false);
            defaultDto.setShareAadhaar(false);
            defaultDto.setVerifySignerName(false);
            return defaultDto;
        }

        return mapToDTO(config);
    }


	@Override
	@Transactional
	public ServiceEsignConfigurationDTO saveConfiguration(ServiceEsignConfigurationDTO request, UserSessionDTO user) {

		validateUser(user);

		if (request == null || request.getServiceId() == null) {
			throw new SPRuntimeError("Request body and service ID are mandatory", HttpStatus.BAD_REQUEST);
		}
		ServiceEsignConfigurationDTO inner = request.getServiceEsignJson();
		if (inner != null) {
			if (request.getEsignEnabled() == null) {
				request.setEsignEnabled(inner.getEsignEnabled());
			}
			if (request.getEsignTemplateId() == null) {
				request.setEsignTemplateId(inner.getEsignTemplateId());
			}
			if (request.getPrintSignerName() == null) {
				request.setPrintSignerName(inner.getPrintSignerName());
			}
			if (request.getBiometricEnabled() == null) {
				request.setBiometricEnabled(inner.getBiometricEnabled());
			}
			if (request.getSubmissionModes() == null || request.getSubmissionModes().isEmpty()) {
				request.setSubmissionModes(inner.getSubmissionModes());
			}
			if (request.getIdentifiedUserGroupTypes() == null || request.getIdentifiedUserGroupTypes().isEmpty()) {
				request.setIdentifiedUserGroupTypes(inner.getIdentifiedUserGroupTypes());
			}
			if (request.getBiometricTypes() == null || request.getBiometricTypes().isEmpty()) {
				request.setBiometricTypes(inner.getBiometricTypes());
			}
			if (request.getShareAadhaar() == null) {
				request.setShareAadhaar(inner.getShareAadhaar());
			}

			// Preserve complete LabelValue objects.
			if (request.getTaskId() == null) {
				request.setTaskId(inner.getTaskId());
			}

			if (request.getAttributeId() == null) {
				request.setAttributeId(inner.getAttributeId());
			}

			if (request.getVerifySignerName() == null) {
				request.setVerifySignerName(inner.getVerifySignerName());
			}
		}

		validateServiceAccess(request.getServiceId(), user);
		validateQuestionnaire(request, user);

		String tenantId = user.getTenantId();
		LocalDateTime now = LocalDateTime.now();

		ServiceEsignConfiguration entity = serviceEsignRepository
				.findByServiceIdAndTenantId(request.getServiceId(), tenantId).orElseGet(() -> {
					ServiceEsignConfiguration newEntity = new ServiceEsignConfiguration();

					newEntity.setServiceId(request.getServiceId());
					newEntity.setTenantId(tenantId);
					newEntity.setCreatedBy(user.getUserID());
					newEntity.setCreatedDate(now);

					return newEntity;
				});

		applyValidatedFields(entity, request, user, now);

		ServiceEsignConfiguration saved = serviceEsignRepository.save(entity);

		log.info("Service eSign configuration saved successfully. " + "serviceId={}, esignEnabled={}, tenant={}",
				saved.getServiceId(), saved.getEsignEnabled(), tenantId);

		return mapToDTO(saved);
	}


    @Override
    @Transactional
    public ServiceEsignConfigurationDTO updateConfiguration(ServiceEsignConfigurationDTO request, UserSessionDTO user) {
        return saveConfiguration(request, user);
    }

    @Override
    @Transactional
    public ServiceEsignConfigurationDTO toggleEsign(ServiceEsignToggleDTO request, UserSessionDTO user) {
        validateUser(user);
        if (request == null || request.getServiceId() == null || request.getEsignEnabled() == null) {
            throw new SPRuntimeError("Service ID and eSign enabled flag are mandatory", HttpStatus.BAD_REQUEST);
        }

        validateServiceAccess(request.getServiceId(), user);
        String tenantId = user.getTenantId();

        ServiceEsignConfiguration entity = serviceEsignRepository
                .findByServiceIdAndTenantId(request.getServiceId(), tenantId)
                .orElseThrow(() -> new SPRuntimeError("No eSign configuration found for service " + request.getServiceId() + ". Please configure eSign first.", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(request.getEsignEnabled())) {
            // Verify that the template is still valid and active
            if (entity.getEsignTemplateId() == null) {
                throw new SPRuntimeError("Cannot enable eSign: no eSign template is configured for this service", HttpStatus.BAD_REQUEST);
            }
            EsignTemplate template = templateRepository.findByIdAndTenantId(entity.getEsignTemplateId(), tenantId)
                    .orElseThrow(() -> new SPRuntimeError("Configured eSign template not found", HttpStatus.NOT_FOUND));
            if (template.getStatus() != EsignTemplateStatus.ACTIVE) {
                throw new SPRuntimeError("Cannot enable eSign: configured eSign template is currently INACTIVE", HttpStatus.BAD_REQUEST);
            }
        }

        entity.setEsignEnabled(request.getEsignEnabled());
        entity.setUpdatedBy(user.getUserID());
        entity.setUpdatedDate(LocalDateTime.now());

        ServiceEsignConfiguration saved = serviceEsignRepository.save(entity);
        log.info("Toggled eSign for serviceId={}, esignEnabled={}", saved.getServiceId(), saved.getEsignEnabled());

        return mapToDTO(saved);
    }

	@Override
	@Transactional(readOnly = true)
	public List<EsignAadhaarFieldOptionDTO> getEligibleAadhaarFields(Integer serviceId, UserSessionDTO user) {

		validateUser(user);
		validateServiceAccess(serviceId, user);

		List<ServiceTemplate> templates = serviceTemplateRepository.findByServiceId(serviceId);

		List<EsignAadhaarFieldOptionDTO> options = new ArrayList<>();

		if (templates == null || templates.isEmpty()) {
			return options;
		}

		for (ServiceTemplate template : templates) {

			if (template == null) {
				continue;
			}

			String taskId = template.getTaskId();
			String taskName = template.getTemplateName();
			String formId = template.getFormId();

			if (formDesignerFeignClient != null && formId != null && !formId.trim().isEmpty()) {

				try {
					ResponseEntity<?> response = formDesignerFeignClient.getAllAttributesByFormId(formId);

					if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

						FormDTO formDTO = null;
						Object responseBody = response.getBody();

						if (responseBody instanceof FormDTO) {
							formDTO = (FormDTO) responseBody;
						} else {
							formDTO = new Gson().fromJson(new Gson().toJson(responseBody), FormDTO.class);
						}

						if (formDTO != null && formDTO.getAttributes() != null && !formDTO.getAttributes().isEmpty()) {

							for (FormDTO.Attribute attr : formDTO.getAttributes()) {

								if (attr == null || attr.getAttributeId() == null) {
									continue;
								}

								String attributeId = attr.getAttributeId();

								String attributeLabel = attr.getAttributeLabel() != null
										&& !attr.getAttributeLabel().trim().isEmpty() ? attr.getAttributeLabel()
												: attributeId;

								String fieldId = CommonUtil.createAttributeId(taskId, template.getHolderId(),
										attributeId);

								String type = attr.getTypeId() != null ? String.valueOf(attr.getTypeId()) : "text";

								EsignAadhaarFieldOptionDTO option = new EsignAadhaarFieldOptionDTO();

								option.setTask(new LabelValue(taskName, taskId));
								option.setAttribute(new LabelValue(attributeLabel, fieldId));

								options.add(option);
							}
						}
					}

				} catch (Exception ex) {
					log.debug("Could not retrieve attributes from Form Designer for task {}: {}", taskId,
							ex.getMessage());
				}
			}

		}

		return options;
	}

	private void validateQuestionnaire(ServiceEsignConfigurationDTO request, UserSessionDTO user) {

		if (Boolean.FALSE.equals(request.getEsignEnabled())) {
			return;
		}

		// Rule 1: An active eSign template is mandatory.
		if (request.getEsignTemplateId() == null) {
			throw new SPRuntimeError("An eSign template must be selected when eSign is enabled",
					HttpStatus.BAD_REQUEST);
		}

		EsignTemplate template = templateRepository
				.findByIdAndTenantId(request.getEsignTemplateId(), user.getTenantId())
				.orElseThrow(() -> new SPRuntimeError("Selected eSign template with ID " + request.getEsignTemplateId()
						+ " not found or unauthorized", HttpStatus.NOT_FOUND));

		if (template.getStatus() != EsignTemplateStatus.ACTIVE) {
			throw new SPRuntimeError("Selected eSign template '" + template.getTemplateName()
					+ "' is INACTIVE. Only active templates can be assigned.", HttpStatus.BAD_REQUEST);
		}

		// Rules 2-4: Validate biometric configuration when enabled.
		if (Boolean.TRUE.equals(request.getBiometricEnabled())) {

			// Rule 2: Submission modes.
			List<LabelValue> submissionModes = request.getSubmissionModes();

			if (submissionModes == null || submissionModes.isEmpty()) {
				throw new SPRuntimeError("Submission modes are mandatory when biometric authentication is enabled",
						HttpStatus.BAD_REQUEST);
			}

			Set<String> validSubmissionModes = new HashSet<>();


			

			// Rule 4: Biometric authentication types.
			List<LabelValue> biometricTypes = request.getBiometricTypes();

			if (biometricTypes == null || biometricTypes.isEmpty()) {
				throw new SPRuntimeError("At least one biometric authentication type "
						+ "(FINGERPRINT, RETINA_SCANNING) is mandatory " + "when biometric authentication is enabled",
						HttpStatus.BAD_REQUEST);
			}
		}

		// Rule 5: Aadhaar sharing configuration.
		if (Boolean.TRUE.equals(request.getShareAadhaar())) {

			LabelValue taskId = request.getTaskId();
			LabelValue attributeId = request.getAttributeId();

			String selectedTaskId = getRequiredLabelValue(taskId, "Aadhaar task");

			String selectedAttributeId = getRequiredLabelValue(attributeId, "Aadhaar form field");

			// Validate that the selected task belongs to the service.
			List<ServiceTemplate> serviceTemplates = serviceTemplateRepository.findByServiceId(request.getServiceId());

			boolean taskExists = serviceTemplates != null && serviceTemplates.stream().filter(Objects::nonNull)
					.anyMatch(serviceTemplate -> Objects.equals(selectedTaskId, serviceTemplate.getTaskId()));

			if (!taskExists) {
				throw new SPRuntimeError("Selected Aadhaar task '" + selectedTaskId + "' does not belong to service "
						+ request.getServiceId(), HttpStatus.BAD_REQUEST);
			}
		}
	}

	private String getRequiredLabelValue(LabelValue item, String fieldName) {

		if (item == null || item.getValue() == null || item.getValue().toString().isBlank()) {
			throw new SPRuntimeError("A valid " + fieldName + " selection is mandatory", HttpStatus.BAD_REQUEST);
		}

		return item.getValue().toString().trim();
	}

	private void applyValidatedFields(ServiceEsignConfiguration entity, ServiceEsignConfigurationDTO request,
			UserSessionDTO user, LocalDateTime now) {

		boolean esignEnabled = Boolean.TRUE.equals(request.getEsignEnabled());
		entity.setEsignEnabled(esignEnabled);

		if (esignEnabled) {
			entity.setEsignTemplateId(request.getEsignTemplateId());
			entity.setPrintSignerName(Boolean.TRUE.equals(request.getPrintSignerName()));
			entity.setBiometricEnabled(Boolean.TRUE.equals(request.getBiometricEnabled()));

			if (Boolean.TRUE.equals(request.getBiometricEnabled())) {
				// Preserve label/value pairs in JSONB.
				entity.setSubmissionModes(request.getSubmissionModes());

				boolean identifiedUserSetSelected = request.getSubmissionModes() != null
						&& request.getSubmissionModes().stream().filter(Objects::nonNull)
								.anyMatch(mode -> mode.getValue() != null && "113".equalsIgnoreCase(mode.getValue().toString().trim()));

				if (identifiedUserSetSelected) {
					entity.setIdentifiedUserGroupTypes(request.getIdentifiedUserGroupTypes());
				} else {
					entity.setIdentifiedUserGroupTypes(null);
				}

				entity.setBiometricTypes(request.getBiometricTypes());
			} else {
				entity.setSubmissionModes(null);
				entity.setIdentifiedUserGroupTypes(null);
				entity.setBiometricTypes(null);
			}

			boolean shareAadhaar = Boolean.TRUE.equals(request.getShareAadhaar());
			entity.setShareAadhaar(shareAadhaar);

			if (shareAadhaar) {
				entity.setTaskId(request.getTaskId());
				entity.setAttributeId(request.getAttributeId());
			} else {
				entity.setTaskId(null);
				entity.setAttributeId(null);
			}

			entity.setVerifySignerName(Boolean.TRUE.equals(request.getVerifySignerName()));

		} else {
			// Clear active configuration when eSign is disabled.
			entity.setEsignTemplateId(request.getEsignTemplateId());
			entity.setPrintSignerName(Boolean.FALSE);
			entity.setBiometricEnabled(Boolean.FALSE);
			entity.setSubmissionModes(null);
			entity.setIdentifiedUserGroupTypes(null);
			entity.setBiometricTypes(null);
			entity.setShareAadhaar(Boolean.FALSE);
			entity.setTaskId(null);
			entity.setAttributeId(null);
			entity.setVerifySignerName(Boolean.FALSE);
		}

		entity.setUpdatedBy(user.getUserID());
		entity.setUpdatedDate(now);
	}

    private void validateServiceAccess(Integer serviceId, UserSessionDTO user) {
        if (serviceId == null) {
            throw new SPRuntimeError("Service ID parameter is mandatory", HttpStatus.BAD_REQUEST);
        }

        ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
        if (serviceLog != null && serviceLog.getTenantId() != null) {
            if (!serviceLog.getTenantId().equals(user.getTenantId())) {
                throw new SPRuntimeError("Access denied: Service " + serviceId + " belongs to a different tenant", HttpStatus.FORBIDDEN);
            }
        }
    }

    private void validateUser(UserSessionDTO user) {
        if (user == null || user.getTenantId() == null || user.getUserID() == null) {
            throw new SPRuntimeError("User session is invalid or unauthenticated", HttpStatus.UNAUTHORIZED);
        }
    }


	private ServiceEsignConfigurationDTO mapToDTO(ServiceEsignConfiguration entity) {
		ServiceEsignConfigurationDTO dto = new ServiceEsignConfigurationDTO();

		dto.setId(entity.getId());
		dto.setServiceId(entity.getServiceId());
		dto.setEsignEnabled(entity.getEsignEnabled());
		dto.setEsignTemplateId(entity.getEsignTemplateId());

		if (entity.getEsignTemplateId() != null) {
			templateRepository.findById(entity.getEsignTemplateId()).ifPresent(template -> {
				LabelValue templateName = new LabelValue();
				templateName.setLabel(template.getTemplateName());
				templateName.setValue(template.getId());
				dto.setEsignTemplateName(templateName);
			});
		}

		dto.setPrintSignerName(entity.getPrintSignerName());
		dto.setBiometricEnabled(entity.getBiometricEnabled());

		dto.setSubmissionModes(
				entity.getSubmissionModes() != null ? entity.getSubmissionModes() : Collections.emptyList());

		dto.setIdentifiedUserGroupTypes(
				entity.getIdentifiedUserGroupTypes() != null ? entity.getIdentifiedUserGroupTypes()
						: Collections.emptyList());

		dto.setBiometricTypes(
				entity.getBiometricTypes() != null ? entity.getBiometricTypes() : Collections.emptyList());

		dto.setShareAadhaar(entity.getShareAadhaar());
		dto.setTaskId(entity.getTaskId());
		dto.setAttributeId(entity.getAttributeId());
		dto.setVerifySignerName(entity.getVerifySignerName());

		dto.setServiceEsignJson(entity.getServiceEsignJson());

		return dto;
	}


    private EsignTemplateResponseDTO mapTemplateToDTO(EsignTemplate entity) {
        EsignTemplateResponseDTO dto = new EsignTemplateResponseDTO();
        dto.setTemplateId(entity.getId());
        dto.setTemplateName(entity.getTemplateName());   
        
        return dto;
    }
}
