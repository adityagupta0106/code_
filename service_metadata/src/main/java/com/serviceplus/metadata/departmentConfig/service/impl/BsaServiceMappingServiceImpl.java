package com.serviceplus.metadata.departmentConfig.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.serviceplus.metadata.departmentConfig.dto.*;
import com.serviceplus.metadata.departmentConfig.entity.BsaConfiguration;
import com.serviceplus.metadata.departmentConfig.entity.BsaInOutAttributeMapping;
import com.serviceplus.metadata.departmentConfig.entity.BsaServiceMapping;
import com.serviceplus.metadata.departmentConfig.repository.BsaConfigurationRepository;
import com.serviceplus.metadata.departmentConfig.repository.BsaInOutAttributeMappingRepository;
import com.serviceplus.metadata.departmentConfig.repository.BsaServiceMappingRepository;
import com.serviceplus.metadata.departmentConfig.service.BsaServiceMappingService;
import com.serviceplus.metadata.departmentConfig.util.BsaCryptoUtil;
import com.serviceplus.metadata.dto.*;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.FormDesignerFeignClient;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BsaServiceMappingServiceImpl implements BsaServiceMappingService {

    private static final Logger log = LoggerFactory.getLogger(BsaServiceMappingServiceImpl.class);

    private final BsaServiceMappingRepository mappingRepository;
    private final BsaInOutAttributeMappingRepository attributeMappingRepository;
    private final BsaConfigurationRepository configurationRepository;
    private final IServiceLogRepository serviceLogRepository;
    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final IProcessFlowRepository processFlowRepository;
    private final IServiceTemplateRepository serviceTemplateRepository;
    private final FormDesignerFeignClient formDesignerFeignClient;
    private final ObjectMapper objectMapper;

    public BsaServiceMappingServiceImpl(BsaServiceMappingRepository mappingRepository,
                                        BsaInOutAttributeMappingRepository attributeMappingRepository,
                                        BsaConfigurationRepository configurationRepository,
                                        IServiceLogRepository serviceLogRepository,
                                        IServiceDefinitionRepository serviceDefinitionRepository,
                                        IProcessFlowRepository processFlowRepository,
                                        IServiceTemplateRepository serviceTemplateRepository,
                                        @Autowired(required = false) FormDesignerFeignClient formDesignerFeignClient,
                                        ObjectMapper objectMapper) {
        this.mappingRepository = mappingRepository;
        this.attributeMappingRepository = attributeMappingRepository;
        this.configurationRepository = configurationRepository;
        this.serviceLogRepository = serviceLogRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.processFlowRepository = processFlowRepository;
        this.serviceTemplateRepository = serviceTemplateRepository;
        this.formDesignerFeignClient = formDesignerFeignClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public BsaServiceMappingDTO getMapping(Integer serviceId, UserSessionDTO userSession) {
        validateUser(userSession);
        validateServiceAccess(serviceId, userSession);

        String tenantId = userSession.getTenantId();
        return buildBsaServiceMappingDTO(serviceId, tenantId);
    }

    @Override
    @Transactional
    public BsaServiceMappingDTO saveMapping(BsaServiceMappingRequestDTO request, UserSessionDTO userSession) {
        validateUser(userSession);
        if (request == null) {
            throw new SPRuntimeError("Request body cannot be null", HttpStatus.BAD_REQUEST);
        }
        Integer serviceId = request.getServiceId();
        Long bsaConfigId = request.getBsaConfigId();

        if (serviceId == null) {
            throw new SPRuntimeError("serviceId is required", HttpStatus.BAD_REQUEST);
        }
        if (bsaConfigId == null) {
            throw new SPRuntimeError("bsaConfigId is required", HttpStatus.BAD_REQUEST);
        }

        validateServiceAccess(serviceId, userSession);

        String tenantId = userSession.getTenantId();
        Date now = new Date();

        BsaConfiguration bsaConfig = configurationRepository.findById(bsaConfigId)
                .orElseThrow(() -> new SPRuntimeError("BSA Configuration not found with id: " + bsaConfigId, HttpStatus.NOT_FOUND));

        ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
        Integer baseServiceId = serviceLog != null ? serviceLog.getBaseServiceId() : serviceId / 10000;
        Integer versionNo = serviceLog != null && serviceLog.getVersionNo() != null ? serviceLog.getVersionNo() : 1;

        // Fetch or create BsaServiceMapping entity
        BsaServiceMapping mapping = mappingRepository.findByServiceIdAndTenantId(serviceId, tenantId)
                .orElseGet(() -> {
                    BsaServiceMapping m = new BsaServiceMapping();
                    m.setServiceId(serviceId);
                    m.setTenantId(tenantId);
                    m.setCreatedBy(userSession.getUserID());
                    m.setCreatedDate(now);
                    return m;
                });

        mapping.setBsaConfigId(bsaConfigId);
        mapping.setBaseServiceId(baseServiceId);
        mapping.setVersionNo(versionNo);
        mapping.setActiveStatus(request.getActiveStatus() != null ? request.getActiveStatus() : true);
        mapping.setUpdatedBy(userSession.getUserID());
        mapping.setUpdatedDate(now);

        // Build DTO to save definition in JSON
        BsaServiceMappingDTO mappingDto = new BsaServiceMappingDTO();
        mappingDto.setBsaConfigId(bsaConfigId);
        mappingDto.setBsaServiceName(bsaConfig.getSchemeName() != null ? bsaConfig.getSchemeName() : bsaConfig.getSchemeCode());
        mappingDto.setSchemeCode(bsaConfig.getSchemeCode());
        mappingDto.setSchemeName(bsaConfig.getSchemeName());
        mappingDto.setServiceCode(bsaConfig.getServiceCode());
        mappingDto.setServiceId(serviceId);

        List<ServiceDefinition> serviceDefs = serviceDefinitionRepository.findByServiceId(serviceId);
        ServiceDefinition serviceDef = (serviceDefs != null && !serviceDefs.isEmpty()) ? serviceDefs.get(0) : null;
        mappingDto.setServiceName(serviceDef != null ? serviceDef.getServiceName() : "Service " + serviceId);
        mappingDto.setBaseServiceId(baseServiceId);
        mappingDto.setVersionNo(versionNo);
        mappingDto.setMinorVersionNo(0);
        mappingDto.setTenantId(tenantId);
        mappingDto.setActiveStatus(mapping.getActiveStatus());

        List<BsaInOutAttributeMappingDTO> attrDtos = new ArrayList<>();
        if (request.getAttributeMappings() != null) {
            for (BsaInOutAttributeMappingDTO item : request.getAttributeMappings()) {
                item.setBsaConfigId(bsaConfigId);
                item.setServiceId(serviceId);
                item.setBaseServiceId(baseServiceId);
                item.setVersionNo(versionNo);
                item.setMinorVersion(0);
                item.setTenantId(tenantId);
                attrDtos.add(item);
            }
        }
        mappingDto.setAttributeMappings(attrDtos);

        // Serialize DTO to JSON
        try {
            mapping.setMappingJson(mappingDto);
        } catch (Exception e) {
            log.error("Failed to serialize BSA mapping definition to JSON", e);
            throw new SPRuntimeError("Failed to serialize BSA mapping definition: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BsaServiceMapping savedMapping = mappingRepository.save(mapping);
        mappingDto.setId(savedMapping.getId());
        mappingDto.setCreatedDate(savedMapping.getCreatedDate());
        mappingDto.setUpdatedDate(savedMapping.getUpdatedDate());

        log.info("Saved BSA service mapping successfully. serviceId={}, bsaConfigId={}, attrsCount={}",
                serviceId, bsaConfigId, attrDtos.size());
        return mappingDto;
    }

    @Override
    public List<BsaConfigurationResponseDTO> getAvailableConfigurations(UserSessionDTO userSession) {
        validateUser(userSession);
        String tenantId = userSession.getTenantId();

        List<BsaConfiguration> configs = configurationRepository.findAll();
        List<BsaConfigurationResponseDTO> result = new ArrayList<>();

        for (BsaConfiguration cfg : configs) {
            if (cfg.getTenantId() != null && !cfg.getTenantId().equals(tenantId)) {
                continue;
            }
            if (Boolean.FALSE.equals(cfg.getActiveStatus())) {
                continue;
            }

            BsaConfigurationResponseDTO dto = new BsaConfigurationResponseDTO();
            dto.setDepartmentId(cfg.getEntityId() != null ? cfg.getEntityId().longValue() : null);
            dto.setTenantId(cfg.getTenantId());
            dto.setDepartmentName(cfg.getSchemeName() != null ? cfg.getSchemeName() : "BSA Config " + cfg.getId());
            dto.setConfigured(true);
            dto.setCanGenerate(false);
            dto.setServiceCode(cfg.getServiceCode());
            dto.setStateCode(cfg.getsCode());
            dto.setBsaUserId(cfg.getUserNameProd() != null ? cfg.getUserNameProd() : cfg.getUserName());
            dto.setRegistrationStatus("REGISTERED");
            dto.setStagingUrl(cfg.getStagingUrl());
            dto.setProdUrl(cfg.getProdUrl());
            dto.setSchemeCode(cfg.getSchemeCode());
            dto.setSchemeName(cfg.getSchemeName());

            if (cfg.getPassword() != null) dto.setMaskedPassword(BsaCryptoUtil.maskPassword(cfg.getPassword()));
            if (cfg.getKeyProd() != null) dto.setMaskedEncryptionKey(BsaCryptoUtil.maskSecret(cfg.getKeyProd()));
            if (cfg.getIvProd() != null) dto.setMaskedIv(BsaCryptoUtil.maskSecret(cfg.getIvProd()));
            if (cfg.getKeyStaging() != null) dto.setMaskedKeyStaging(BsaCryptoUtil.maskSecret(cfg.getKeyStaging()));
            if (cfg.getIvStaging() != null) dto.setMaskedIvStaging(BsaCryptoUtil.maskSecret(cfg.getIvStaging()));

            result.add(dto);
        }

        return result;
    }

    @Override
    public BsaServiceTaskAttributeDTO getServiceTasksAndAttributes(Integer serviceId, UserSessionDTO userSession) {
        validateUser(userSession);
        validateServiceAccess(serviceId, userSession);

        BsaServiceTaskAttributeDTO response = new BsaServiceTaskAttributeDTO(serviceId);
        List<BsaServiceTaskAttributeDTO.TaskOption> tasks = new ArrayList<>();

        // 1. Try from ProcessFlow
        ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
        if (serviceProcessFlow != null && serviceProcessFlow.getProcessFlowJson() != null) {
            List<NodeDTO> nodes = serviceProcessFlow.getProcessFlowJson().getNodes();
            if (nodes != null) {
                for (NodeDTO node : nodes) {
                    if (node == null || node.getId() == null) continue;
                    String taskId = node.getId();
                    String taskName = (node.getData() != null && node.getData().getName() != null) ?
                            node.getData().getName() : taskId;
                    String formId = (node.getData() != null && node.getData().getFormDetail() != null) ?
                            node.getData().getFormDetail().getFormId() : null;

                    BsaServiceTaskAttributeDTO.TaskOption taskOption = new BsaServiceTaskAttributeDTO.TaskOption(taskId, taskName, formId);
                    taskOption.setAttributes(fetchAttributesForForm(formId, taskId));
                    tasks.add(taskOption);
                }
            }
        }

        // 2. If no tasks found from process flow, try ServiceTemplate
        if (tasks.isEmpty()) {
            List<ServiceTemplate> templates = serviceTemplateRepository.findByServiceId(serviceId);
            if (templates != null) {
                for (ServiceTemplate tmpl : templates) {
                    String taskId = tmpl.getTaskId() != null ? tmpl.getTaskId() : "task_" + tmpl.getId();
                    String taskName = tmpl.getTemplateName() != null ? tmpl.getTemplateName() : taskId;
                    String formId = tmpl.getFormId();

                    BsaServiceTaskAttributeDTO.TaskOption taskOption = new BsaServiceTaskAttributeDTO.TaskOption(taskId, taskName, formId);
                    taskOption.setAttributes(fetchAttributesForForm(formId, taskId));
                    tasks.add(taskOption);
                }
            }
        }

        response.setTasks(tasks);
        return response;
    }

    private List<BsaServiceTaskAttributeDTO.AttributeOption> fetchAttributesForForm(String formId, String taskId) {
        List<BsaServiceTaskAttributeDTO.AttributeOption> attributes = new ArrayList<>();
        if (formId == null || formId.isBlank() || formDesignerFeignClient == null) {
            return attributes;
        }

        try {
            ResponseEntity<?> response = formDesignerFeignClient.getAllAttributesByFormId(formId);
            if (response != null && response.getBody() != null) {
                String body = response.getBody().toString();
                JSONObject json = new JSONObject(body);
                if (json.has("attributes")) {
                    org.json.JSONArray arr = json.getJSONArray("attributes");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String attrId = obj.optString("attributeId", obj.optString("id", null));
                        String attrName = obj.optString("attributeName", obj.optString("name", attrId));
                        String attrType = obj.optString("type", "TEXT");
                        if (attrId != null) {
                            attributes.add(new BsaServiceTaskAttributeDTO.AttributeOption(attrId, attrName, attrType));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch attributes from form designer for formId: {}. Error: {}", formId, e.getMessage());
        }

        return attributes;
    }

    @Override
    public BsaServiceMappingDTO buildBsaServiceMappingDTO(Integer serviceId, String tenantId) {
        Optional<BsaServiceMapping> optMapping = mappingRepository.findByServiceIdAndTenantId(serviceId, tenantId);
        if (optMapping.isEmpty()) {
            optMapping = mappingRepository.findByServiceId(serviceId);
        }
        if (optMapping.isEmpty()) {
            return null;
        }

        BsaServiceMapping entity = optMapping.get();

        // 1. If mapping_json is present and valid, parse it
        if (entity.getMappingJson() != null) {
            try {
                BsaServiceMappingDTO dto = entity.getMappingJson();
                dto.setId(entity.getId());
                dto.setTenantId(entity.getTenantId());
                dto.setActiveStatus(entity.getActiveStatus());
                return dto;
            } catch (Exception e) {
                log.warn("Could not deserialize mappingJson for serviceId: {}, falling back to row-wise reconstitution", serviceId, e);
            }
        }

        // 2. Reconstitute from relational rows
        BsaServiceMappingDTO dto = new BsaServiceMappingDTO();
        dto.setId(entity.getId());
        dto.setServiceId(entity.getServiceId());
        dto.setBaseServiceId(entity.getBaseServiceId());
        dto.setBsaConfigId(entity.getBsaConfigId());
        dto.setVersionNo(entity.getVersionNo());
        dto.setMinorVersionNo(entity.getMinorVersionNo());
        dto.setTenantId(entity.getTenantId());
        dto.setActiveStatus(entity.getActiveStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());

        if (entity.getBsaConfigId() != null) {
            configurationRepository.findById(entity.getBsaConfigId()).ifPresent(cfg -> {
                dto.setBsaServiceName(cfg.getSchemeName() != null ? cfg.getSchemeName() : cfg.getSchemeCode());
                dto.setSchemeCode(cfg.getSchemeCode());
                dto.setSchemeName(cfg.getSchemeName());
                dto.setServiceCode(cfg.getServiceCode());
            });
        }

        List<ServiceDefinition> serviceDefs = serviceDefinitionRepository.findByServiceId(serviceId);
        ServiceDefinition serviceDef = (serviceDefs != null && !serviceDefs.isEmpty()) ? serviceDefs.get(0) : null;
        if (serviceDef != null) {
            dto.setServiceName(serviceDef.getServiceName());
        } else {
            dto.setServiceName("Service " + serviceId);
        }

        return dto;
    }

    @Override
    public BsaConfiguration findBsaConfigurationById(Long bsaConfigId) {
        if (bsaConfigId == null) return null;
        return configurationRepository.findById(bsaConfigId).orElse(null);
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
            throw new SPRuntimeError("User session is invalid or unauthenticated", HttpStatus.FORBIDDEN);
        }
    }
}
