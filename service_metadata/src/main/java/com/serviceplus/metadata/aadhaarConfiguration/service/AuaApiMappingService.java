package com.serviceplus.metadata.aadhaarConfiguration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.aadhaarConfiguration.dto.*;
import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMapping;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarMappingType;
import com.serviceplus.metadata.aadhaarConfiguration.model.AuaResponseAttributeType;
import com.serviceplus.metadata.aadhaarConfiguration.model.MessageType;
import com.serviceplus.metadata.aadhaarConfiguration.repository.AuaApiFieldMappingRepository;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.TenantConfigurationFeignClient;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.serviceplus.metadata.utility.CommonUtil.entityToString;
import static com.serviceplus.metadata.utility.CommonUtil.getUserSessionDetails;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.hasText;

@Service
public class AuaApiMappingService {

    private static final Logger log = LoggerFactory.getLogger("auaMapping");

    private final AuaApiFieldMappingRepository mappingRepository;
    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final TenantConfigurationFeignClient tenantConfigurationFeignClient;
    private final ObjectMapper objectMapper;
    private final com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService;

    public AuaApiMappingService(AuaApiFieldMappingRepository mappingRepository,
                               IServiceDefinitionRepository serviceDefinitionRepository,
                               TenantConfigurationFeignClient tenantConfigurationFeignClient,
                               ObjectMapper objectMapper,
                               com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService) {

        this.mappingRepository = mappingRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.tenantConfigurationFeignClient = tenantConfigurationFeignClient;
        this.objectMapper = objectMapper;
        this.serviceVersionTokenService = serviceVersionTokenService;
    }

    @Transactional
    public void saveMappings(Integer serviceId, String configurationId, SaveAuaApiMappingRequest request, HttpServletRequest httpRequest) {

        UserSessionDTO user = requireNonNull(getUserSessionDetails(httpRequest), "User session not found");

        String tenantId = user.getTenantId();

        log.info("Saving Aadhaar mappings. serviceId={}, configurationId={}, userId={}", serviceId, configurationId, user.getUserID());

        validateServiceAccess(serviceId, user);

        validateSaveRequest(configurationId, request);

        String apiType = request.getApiType().trim().toUpperCase();

        log.info(
                "Aadhaar mapping request received. serviceId={}, configurationId={}, apiType={}, requestMappings={}, responseMappings={}",
                serviceId,
                configurationId,
                apiType,
                getSize(request.getRequestMappings()),
                getSize(request.getResponseMappings())
        );

        AadhaarConfiguration configuration = getAadhaarConfiguration(configurationId, serviceId, apiType, user);

        AadhaarApiConfiguration apiConfiguration = findApiConfiguration(configuration, apiType);

        validateRequestMappings(apiConfiguration, request.getRequestMappings());

        validateResponseMappings(apiConfiguration, request.getResponseMappings());

        OffsetDateTime now = OffsetDateTime.now();

        saveRequestMappings(tenantId, serviceId, configurationId, apiType, request.getRequestMappings(), user, now);

        saveResponseMappings(tenantId, serviceId, configurationId, apiType, request.getResponseMappings(), user, now);

        boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
        if (isMinorVersion) {
            serviceVersionTokenService.recordAuaMappingChangeLog(
                    serviceId,
                    configurationId,
                    apiType,
                    request,
                    user
            );
        }

        log.info(
                "Aadhaar mappings saved successfully. serviceId={}, configurationId={}, apiType={}, requestMappings={}, responseMappings={}, userId={}",
                serviceId,
                configurationId,
                apiType,
                getSize(request.getRequestMappings()),
                getSize(request.getResponseMappings()),
                user.getUserID()
        );
    }

    private AadhaarConfiguration getAadhaarConfiguration(String configurationId, Integer serviceId, String apiType, UserSessionDTO user) {


        try {

            log.info("Fetching Aadhaar configuration from Instance Config Registry. configurationId={}, serviceId={}, apiType={}", 
                    configurationId, serviceId, apiType);

            ResponseEntity<AadhaarConfiguration> response = tenantConfigurationFeignClient.getAadhaarConfiguration(configurationId, entityToString(user));

            if (response == null) {
                log.warn("Empty response received from Instance Config Registry. configurationId={}", configurationId);
                throw new SPRuntimeError("Unable to fetch Aadhaar configuration", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {

                log.warn("Instance Config Registry returned non-success status. configurationId={}, status={}", configurationId, response.getStatusCode());
                throw new SPRuntimeError("Unable to fetch Aadhaar configuration", HttpStatus.BAD_REQUEST);
            }

            AadhaarConfiguration configuration = response.getBody();

            if (configuration == null) {

                log.warn("Aadhaar configuration not found. configurationId={}, serviceId={}, apiType={}", configurationId, serviceId, apiType);
                throw new SPRuntimeError("Aadhaar configuration not found", HttpStatus.BAD_REQUEST);
            }

            log.info("Aadhaar configuration fetched successfully. configurationId={}, apiCount={}", configurationId, getSize(configuration.getApiConfigurations()));

            return configuration;

        } catch (SPRuntimeError e) {

            throw e;

        } catch (Exception e) {

            log.error("Failed to fetch Aadhaar configuration. configurationId={}, serviceId={}, apiType={}", configurationId, serviceId, apiType, e);

            throw new SPRuntimeError("Unable to fetch Aadhaar configuration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> buildUserSessionMap(UserSessionDTO user) {

        Map<String, Object> userSession = new HashMap<>();

        userSession.put("userId", user.getUserID());

        userSession.put("tenantId", user.getTenantId());

        return userSession;
    }

    private void validateServiceAccess(Integer serviceId, UserSessionDTO user) {

        if (serviceId == null) {

            log.warn("Service ID missing while saving Aadhaar mapping. userId={}", user.getUserID());
            throw new SPRuntimeError("Service ID is required", HttpStatus.BAD_REQUEST);
        }

        ServiceDefinition service = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId, user.getUserID());

        if (isNull(service)) {

            log.warn("Unauthorized service access for Aadhaar mapping. serviceId={}, userId={}, tenantId={}", serviceId, user.getUserID(), user.getTenantId());
            throw new SPRuntimeError("You are not allowed to access this service", HttpStatus.UNAUTHORIZED);
        }

        log.info("Service access validated. serviceId={}, userId={}", serviceId, user.getUserID());
    }

    private void validateSaveRequest(String configurationId, SaveAuaApiMappingRequest request) {

        if (!hasText(configurationId)) {

            log.warn("Configuration ID missing while saving Aadhaar mapping");
            throw new SPRuntimeError("Configuration ID is required", HttpStatus.BAD_REQUEST);
        }

        if (request == null) {

            log.warn("Aadhaar mapping request is null. configurationId={}", configurationId);
            throw new SPRuntimeError("Mapping request is required", HttpStatus.BAD_REQUEST);
        }

        if (!hasText(request.getApiType())) {

            log.warn("API type missing in Aadhaar mapping request. configurationId={}", configurationId);
            throw new SPRuntimeError("API type is required", HttpStatus.BAD_REQUEST);
        }

        boolean noRequestMappings = request.getRequestMappings() == null || request.getRequestMappings().isEmpty();
        boolean noResponseMappings = request.getResponseMappings() == null || request.getResponseMappings().isEmpty();

        if (noRequestMappings && noResponseMappings) {

            log.warn("No Aadhaar request or response mappings supplied. configurationId={}, apiType={}", configurationId, request.getApiType());
            throw new SPRuntimeError("At least one mapping is required", HttpStatus.BAD_REQUEST);
        }
    }

    private AadhaarApiConfiguration findApiConfiguration(AadhaarConfiguration configuration, String apiType) {

        if (configuration.getApiConfigurations() == null || configuration.getApiConfigurations().isEmpty()) {

            log.warn("No API configurations found in Aadhaar configuration. configurationId={}", configuration.getId());
            throw new SPRuntimeError("No API configurations found for Aadhaar configuration", HttpStatus.BAD_REQUEST);
        }

        return configuration.getApiConfigurations()
                .stream()
                .filter(api ->
                        api.getApiType() != null
                                && api.getApiType()
                                .name()
                                .equalsIgnoreCase(apiType))
                .findFirst()
                .orElseThrow(() -> {

                    log.warn("API type not found in Aadhaar configuration. configurationId={}, apiType={}", configuration.getId(), apiType);
                    return new SPRuntimeError("API type not found in Aadhaar configuration: " + apiType, HttpStatus.BAD_REQUEST);
                });
    }

    private void validateRequestMappings(AadhaarApiConfiguration apiConfiguration, List<SaveAuaFieldMappingRequest> mappings) {

        if (mappings == null || mappings.isEmpty()) {
            return;
        }

        if (apiConfiguration.getRequestPayload() == null || apiConfiguration.getRequestPayload().getNodes() == null 
                || apiConfiguration.getRequestPayload().getNodes().isEmpty()) {

            log.warn("Request nodes not configured. apiType={}", apiConfiguration.getApiType());
            throw new SPRuntimeError("Request nodes are not configured for API: " + apiConfiguration.getApiType(), HttpStatus.BAD_REQUEST);
        }

        Set<String> nodeCodes = new HashSet<>();

        for (SaveAuaFieldMappingRequest item : mappings) {

            if (!hasText(item.getNodeCode())) {

                log.warn("Request node code missing. apiType={}", apiConfiguration.getApiType());
                throw new SPRuntimeError("Request node code is required", HttpStatus.BAD_REQUEST);
            }

            String nodeCode = item.getNodeCode().trim();

            if (!nodeCodes.add(nodeCode.toLowerCase())) {

                log.warn("Duplicate request node mapping. apiType={}, nodeCode={}", apiConfiguration.getApiType(), nodeCode);
                throw new SPRuntimeError("Duplicate request mapping for node: " + nodeCode, HttpStatus.BAD_REQUEST);
            }

            AadhaarNodeConfiguration node = findNode(apiConfiguration.getRequestPayload().getNodes(), nodeCode, "Request");

            validateRequestMapping(item, node);
        }
    }

    private void validateRequestMapping(SaveAuaFieldMappingRequest item, AadhaarNodeConfiguration node) {

        String nodeCode = node.getNodeCode();

        if (node.getMappingType() != AadhaarMappingType.DYNAMIC && node.getMappingType() != AadhaarMappingType.STATIC) {

            log.warn("Attempt to configure non-configurable node. nodeCode={}, mappingType={}", nodeCode, node.getMappingType());
            throw new SPRuntimeError("Node is not configurable by Service Definer: " + nodeCode, HttpStatus.BAD_REQUEST);
        }

        if (!hasText(item.getSourceType())) {

            log.warn("Source type missing. nodeCode={}, mappingType={}", nodeCode, node.getMappingType());
            throw new SPRuntimeError("Source type is required for node: " + nodeCode, HttpStatus.BAD_REQUEST);
        }

        AadhaarMappingType sourceType;

        try {

            sourceType = AadhaarMappingType.valueOf(item.getSourceType().trim().toUpperCase());

        } catch (IllegalArgumentException ex) {

            log.warn("Invalid source type. nodeCode={}, sourceType={}", nodeCode, item.getSourceType());
            throw new SPRuntimeError("Invalid source type: " + item.getSourceType() + " for node: " + nodeCode, HttpStatus.BAD_REQUEST);
        }

        if (sourceType == AadhaarMappingType.NONE || sourceType == AadhaarMappingType.SYSTEM_GENERATED) {
            throw new SPRuntimeError("Invalid source type for Service Definer node: " + nodeCode, HttpStatus.BAD_REQUEST);
        }

        
        if (node.getMappingType() == AadhaarMappingType.DYNAMIC) {

            if (sourceType != AadhaarMappingType.DYNAMIC) {

                log.warn("Invalid source type for DYNAMIC node. nodeCode={}, sourceType={}", nodeCode, sourceType);
                throw new SPRuntimeError("Node " + nodeCode + " requires DYNAMIC mapping", HttpStatus.BAD_REQUEST);
            }

            if (!hasText(item.getSourcePath())) {
                throw new SPRuntimeError("Source path is required for DYNAMIC node: " + nodeCode, HttpStatus.BAD_REQUEST);
            }

            if (hasText(item.getDefaultValue())) {
                throw new SPRuntimeError("Default value must be empty for DYNAMIC node: " + nodeCode, HttpStatus.BAD_REQUEST);
            }
        }

        if (node.getMappingType() == AadhaarMappingType.STATIC) {

            if (sourceType != AadhaarMappingType.STATIC) {

                log.warn("Invalid source type for STATIC node. nodeCode={}, sourceType={}", nodeCode, sourceType);
                throw new SPRuntimeError("Node " + nodeCode + " requires STATIC mapping", HttpStatus.BAD_REQUEST);
            }

            if (hasText(item.getSourcePath())) {
                throw new SPRuntimeError("Source path must be empty for STATIC node: " + nodeCode, HttpStatus.BAD_REQUEST);
            }

            if (!hasText(item.getDefaultValue())) {
                throw new SPRuntimeError("Value is required for STATIC node: " + nodeCode, HttpStatus.BAD_REQUEST);
            }
        }

        log.info("Request mapping validated. nodeCode={}, mappingType={}, sourceType={}", nodeCode, node.getMappingType(), sourceType);
    }

    private void validateResponseMappings(AadhaarApiConfiguration apiConfiguration, List<SaveAuaResponseMappingRequest> mappings) {

        if (mappings == null || mappings.isEmpty()) {
            return;
        }

        if (apiConfiguration.getResponsePayload() == null || apiConfiguration.getResponsePayload().getNodes() == null 
                || apiConfiguration.getResponsePayload().getNodes().isEmpty()) {

            log.warn("Response nodes not configured. apiType={}", apiConfiguration.getApiType());
            throw new SPRuntimeError("Response nodes are not configured for API: " + apiConfiguration.getApiType(), HttpStatus.BAD_REQUEST);
        }

        Set<String> nodeCodes = new HashSet<>();

        for (SaveAuaResponseMappingRequest item : mappings) {

            if (!hasText(item.getNodeCode())) {
                throw new SPRuntimeError("Response node code is required", HttpStatus.BAD_REQUEST);
            }

            String nodeCode = item.getNodeCode().trim();

            if (!nodeCodes.add(nodeCode.toLowerCase())) {

                log.warn("Duplicate response node mapping. apiType={}, nodeCode={}", apiConfiguration.getApiType(), nodeCode);
                throw new SPRuntimeError("Duplicate response mapping for node: " + nodeCode, HttpStatus.BAD_REQUEST);
            }

            findNode(apiConfiguration.getResponsePayload().getNodes(), nodeCode, "Response");

            validateResponseMapping(item);
        }
    }

    private void validateResponseMapping(SaveAuaResponseMappingRequest item) {

        if (!hasText(item.getResponseAttributeType())) {
            throw new SPRuntimeError("Response attribute type is required for node: " + item.getNodeCode(), HttpStatus.BAD_REQUEST);
        }

        AuaResponseAttributeType attributeType;

        try {

            attributeType = AuaResponseAttributeType.valueOf(item.getResponseAttributeType().trim().toUpperCase());

        } catch (IllegalArgumentException ex) {

            log.warn("Invalid response attribute type. nodeCode={}, attributeType={}", item.getNodeCode(), item.getResponseAttributeType());
            throw new SPRuntimeError("Invalid response attribute type: " + item.getResponseAttributeType(), HttpStatus.BAD_REQUEST);
        }

        log.info("Response mapping validated. nodeCode={}, attributeType={}", item.getNodeCode(), attributeType);
    }

    private AadhaarNodeConfiguration findNode(List<AadhaarNodeConfiguration> nodes, String nodeCode, String direction) {

        return nodes
                .stream()
                .filter(node ->
                        node.getNodeCode() != null
                                && node.getNodeCode()
                                .equalsIgnoreCase(nodeCode))
                .findFirst()
                .orElseThrow(() -> {

                    log.warn("{} node not found in Aadhaar configuration. nodeCode={}", direction, nodeCode);
                    return new SPRuntimeError(direction + " node not found in Aadhaar configuration: " + nodeCode, HttpStatus.BAD_REQUEST);
                });
    }

    private void saveRequestMappings(String tenantId, Integer serviceId, String configurationId, 
                                     String apiType, List<SaveAuaFieldMappingRequest> mappings, UserSessionDTO user, OffsetDateTime now) {

        if (mappings == null || mappings.isEmpty()) {
            return;
        }

        for (SaveAuaFieldMappingRequest item : mappings) {

            String nodeCode = item.getNodeCode().trim();

            AuaApiFieldMapping mapping =
                    mappingRepository
                            .findByTenantIdAndServiceIdAndConfigurationIdAndApiTypeAndNodeCodeAndMessageType(
                                    tenantId,
                                    serviceId,
                                    configurationId,
                                    apiType,
                                    nodeCode,
                                    MessageType.REQUEST.name()
                            )
                            .orElseGet(
                                    AuaApiFieldMapping::new
                            );

            boolean isNew = mapping.getId() == null;

            mapping.setTenantId(tenantId);
            mapping.setServiceId(serviceId);
            mapping.setConfigurationId(configurationId);
            mapping.setApiType(apiType);
            mapping.setNodeCode(nodeCode);
            mapping.setMessageType(MessageType.REQUEST.name());

            mapping.setSourceType(normalize(item.getSourceType()));
            mapping.setSourcePath(item.getSourcePath());
            mapping.setTransformation(item.getTransformation());
            mapping.setDefaultValue(item.getDefaultValue());
            mapping.setResponseAttributeType(null);
            mapping.setDesiredResponse(null);

            if (isNew) {
                mapping.setCreatedAt(now);
                mapping.setCreatedBy(user.getUserID());
            }

            mapping.setUpdatedAt(now);
            mapping.setUpdatedBy(user.getUserID());

            mappingRepository.save(mapping);

            log.info(
                    "Aadhaar request mapping {}. serviceId={}, configurationId={}, apiType={}, nodeCode={}, mappingId={}",
                    isNew ? "created" : "updated",
                    serviceId,
                    configurationId,
                    apiType,
                    nodeCode,
                    mapping.getId()
            );
        }
    }

    private void saveResponseMappings(String tenantId, Integer serviceId, String configurationId, String apiType, 
                                      List<SaveAuaResponseMappingRequest> mappings, UserSessionDTO user, OffsetDateTime now) {

        if (mappings == null || mappings.isEmpty()) {
            return;
        }

        for (SaveAuaResponseMappingRequest item : mappings) {

            String nodeCode = item.getNodeCode().trim();

            AuaApiFieldMapping mapping =
                    mappingRepository
                            .findByTenantIdAndServiceIdAndConfigurationIdAndApiTypeAndNodeCodeAndMessageType(
                                    tenantId,
                                    serviceId,
                                    configurationId,
                                    apiType,
                                    nodeCode,
                                    MessageType.RESPONSE.name()
                            )
                            .orElseGet(
                                    AuaApiFieldMapping::new
                            );

            boolean isNew = mapping.getId() == null;

            mapping.setTenantId(tenantId);
            mapping.setServiceId(serviceId);
            mapping.setConfigurationId(configurationId);
            mapping.setApiType(apiType);
            mapping.setNodeCode(nodeCode);
            mapping.setMessageType(MessageType.RESPONSE.name());

            mapping.setSourceType(normalize(item.getResponseAttributeType()));
            mapping.setSourcePath(null);
            mapping.setTransformation(null);
            mapping.setDefaultValue(null);

            mapping.setResponseAttributeType(item.getResponseAttributeType().trim().toUpperCase());

            mapping.setDesiredResponse(item.getDesiredResponse());

            if (isNew) {
                mapping.setCreatedAt(now);
                mapping.setCreatedBy(user.getUserID());
            }

            mapping.setUpdatedAt(now);
            mapping.setUpdatedBy(user.getUserID());

            mappingRepository.save(mapping);

            log.info(
                    "Aadhaar response mapping {}. serviceId={}, configurationId={}, apiType={}, nodeCode={}, mappingId={}, attributeType={}",
                    isNew ? "created" : "updated",
                    serviceId,
                    configurationId,
                    apiType,
                    nodeCode,
                    mapping.getId(),
                    item.getResponseAttributeType()
            );
        }
    }

    private String normalize(String value) {

        if (!hasText(value)) {
            return value;
        }

        return value.trim().toUpperCase();
    }

    private int getSize(List<?> list) {
        return list == null ? 0 : list.size();
    }

    @Transactional(readOnly = true)
    public AadhaarDefinerConfigurationResponse getDefinerConfiguration(Integer serviceId, String configurationId, UserSessionDTO user) {

        log.info("Fetching Aadhaar definer configuration. serviceId={}, configurationId={}, userId={}", serviceId, configurationId, user.getUserID());

        validateServiceAccess(serviceId, user);

        AadhaarDefinerConfigurationResponse configuration = getConfigurationFromInstanceRegistry(configurationId, serviceId, user);

        if (configuration == null) {

            log.warn("Aadhaar definer configuration not found. serviceId={}, configurationId={}", serviceId, configurationId);
            throw new SPRuntimeError("Aadhaar configuration not found", HttpStatus.BAD_REQUEST);
        }

        enrichSavedMappings(configuration, serviceId, user.getTenantId());

        log.info("Aadhaar definer configuration returned successfully. serviceId={}, configurationId={}", serviceId, configurationId);

        return configuration;
    }

    private AadhaarDefinerConfigurationResponse getConfigurationFromInstanceRegistry(String configurationId,
                                                                                     Integer serviceId,
                                                                                     UserSessionDTO user) {

        try {
            log.debug("Calling Instance Config Registry. configurationId={}, serviceId={}", configurationId, serviceId);

            ResponseEntity<AadhaarDefinerConfigurationResponse> response = tenantConfigurationFeignClient
                                                                                .getAadhaarDefinerConfiguration(configurationId, entityToString(user));

            if (response == null) {
                throw new SPRuntimeError("Unable to fetch Aadhaar configuration", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {

                log.warn("Instance Config Registry returned unsuccessful status. configurationId={}, status={}", configurationId, response.getStatusCode());
                throw new SPRuntimeError("Unable to fetch Aadhaar configuration", HttpStatus.BAD_REQUEST);
            }

            return response.getBody();

        }catch (Exception e) {

            log.error("Failed to fetch Aadhaar configuration. configurationId={}, serviceId={}", configurationId, serviceId, e);
            throw new SPRuntimeError("Unable to fetch Aadhaar configuration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void enrichSavedMappings(AadhaarDefinerConfigurationResponse configuration, Integer serviceId, String tenantId) {

        if (configuration.getApiConfigurations() == null || configuration.getApiConfigurations().isEmpty()) {

            log.debug("No Aadhaar API configurations to enrich. configurationId={}", configuration.getConfigurationId());
            return;
        }

        for (AadhaarDefinerConfigurationResponse.AadhaarDefinerApiDTO api : configuration.getApiConfigurations()) {

            if (api.getApiType() == null) {
                continue;
            }

            String apiType = api.getApiType().name();

            List<AuaApiFieldMapping> savedMappings = mappingRepository
                    .findByTenantIdAndServiceIdAndConfigurationIdAndApiType(tenantId, serviceId, configuration.getConfigurationId(), apiType);

            log.debug("Fetched saved Aadhaar mappings. serviceId={}, configurationId={}, apiType={}, count={}",
                    serviceId, configuration.getConfigurationId(), apiType, savedMappings.size());

            Map<String, AuaApiFieldMapping> mappingMap = createMappingMap(savedMappings);

            enrichRequestPayload(api.getRequestPayload(), mappingMap);

            enrichResponsePayload(api.getResponsePayload(), mappingMap);
        }
    }

    private Map<String, AuaApiFieldMapping> createMappingMap(List<AuaApiFieldMapping> mappings) {

        Map<String, AuaApiFieldMapping> mappingMap = new HashMap<>();

        for (AuaApiFieldMapping mapping : mappings) {

            if (!hasText(mapping.getNodeCode()) || !hasText(mapping.getMessageType())) {
                continue;
            }

            String key = mapping.getMessageType().toUpperCase() + ":" + mapping.getNodeCode().toLowerCase();

            mappingMap.put(key, mapping);
        }

        return mappingMap;
    }

    private void enrichRequestPayload(AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO payload, Map<String, AuaApiFieldMapping> mappingMap) {

        if (payload == null || payload.getNodes() == null || payload.getNodes().isEmpty()) {
            return;
        }

        for (AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO node : payload.getNodes()) {

            if (!hasText(node.getCode())) {
                continue;
            }

            if (node.getMappingType() != AadhaarMappingType.DYNAMIC && node.getMappingType() != AadhaarMappingType.STATIC) {
                continue;
            }

            String key = MessageType.REQUEST.name() + ":" + node.getCode().toLowerCase();

            AuaApiFieldMapping savedMapping = mappingMap.get(key);

            if (savedMapping != null) {

                node.setMapping(createDefinerMapping(savedMapping, node));
                log.debug("Attached saved request mapping. nodeCode={}, mappingId={}", node.getCode(), savedMapping.getId());
            }
        }
    }

    private void enrichResponsePayload(AadhaarDefinerConfigurationResponse.AadhaarDefinerPayloadDTO payload, Map<String, AuaApiFieldMapping> mappingMap) {

        if (payload == null || payload.getNodes() == null || payload.getNodes().isEmpty()) {
            return;
        }

        for (AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO node : payload.getNodes()) {

            if (!hasText(node.getCode())) {
                continue;
            }

            if (node.getMappingType() == AadhaarMappingType.NONE) {
                continue;
            }

            String key = MessageType.RESPONSE.name() + ":" + node.getCode().toLowerCase();

            AuaApiFieldMapping savedMapping = mappingMap.get(key);

            if (savedMapping != null) {

                node.setMapping(createDefinerMapping(savedMapping, node));
                log.debug("Attached saved response mapping. nodeCode={}, mappingId={}", node.getCode(), savedMapping.getId());
            }
        }
    }

    private AadhaarDefinerConfigurationResponse.AadhaarDefinerMappingDTO createDefinerMapping(AuaApiFieldMapping mapping,
                                                                                              AadhaarDefinerConfigurationResponse.AadhaarDefinerNodeDTO node) {

        AadhaarDefinerConfigurationResponse.AadhaarDefinerMappingDTO dto = new AadhaarDefinerConfigurationResponse.AadhaarDefinerMappingDTO();

        dto.setFieldCode(node.getCode());
        dto.setFieldName(node.getName());

        if (MessageType.REQUEST.name().equalsIgnoreCase(mapping.getMessageType())) {

            if (AadhaarMappingType.DYNAMIC.name().equalsIgnoreCase(mapping.getSourceType())) {
                dto.setValue(mapping.getSourcePath());

            } else if (AadhaarMappingType.STATIC.name().equalsIgnoreCase(mapping.getSourceType())) {
                dto.setValue(mapping.getDefaultValue());

            } else {
                dto.setValue(mapping.getDefaultValue());
            }

        } else if (MessageType.RESPONSE.name().equalsIgnoreCase(mapping.getMessageType())) {

            dto.setValue(mapping.getResponseAttributeType());
            dto.setExpectedValue(mapping.getDesiredResponse());
        }

        return dto;
    }

}