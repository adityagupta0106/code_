package com.serviceplus.metadata.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serviceplus.metadata.dto.PaymentGatewayConfigurationListResponse;
import com.serviceplus.metadata.dto.PaymentGatewayConfigurationResponse;
import com.serviceplus.metadata.dto.PaymentGatewayMappingRequest;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfig;
import com.serviceplus.metadata.entity.ServicePaymentGatewayConfigHistory;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.PaymentGatewayFeignClient;
import com.serviceplus.metadata.repository.ServicePaymentGatewayConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentGatewayMetadataService {

    private static final Logger log = LoggerFactory.getLogger("paymentGatewayLogger");

    private final ServicePaymentGatewayConfigRepository repository;
    private final PaymentGatewayFeignClient paymentGatewayFeignClient;
    private final ObjectMapper objectMapper;
    private final com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService;

    public PaymentGatewayMetadataService(ServicePaymentGatewayConfigRepository repository,
                                         PaymentGatewayFeignClient paymentGatewayFeignClient,
                                         ObjectMapper objectMapper,
                                         com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService) {

        this.repository = repository;
        this.paymentGatewayFeignClient = paymentGatewayFeignClient;
        this.objectMapper = objectMapper;
        this.serviceVersionTokenService = serviceVersionTokenService;
    }

    @Transactional(readOnly = true)
    public PaymentGatewayConfigurationResponse getConfiguration(Integer serviceId, String gatewayCode, UserSessionDTO user) {

        String tenantId = user.getTenantId();

        log.info("Fetching payment gateway configuration. serviceId={}, tenantId={}, gatewayCode={}", serviceId, tenantId, gatewayCode);

        ServicePaymentGatewayConfig config = repository.findByServiceIdAndCreatedByAndGatewayCodeAndTenantId(serviceId,user.getUserID(), gatewayCode, tenantId).orElse(null);

        JsonNode gatewayConfiguration = paymentGatewayFeignClient.getGatewayConfiguration(gatewayCode).getBody();

        if (gatewayConfiguration == null) {

            log.error("Payment gateway configuration not found. serviceId={}, tenantId={}, gatewayCode={}", serviceId, tenantId, gatewayCode);

            throw new SPRuntimeError("Payment gateway configuration not found", HttpStatus.NOT_FOUND);
        }

        PaymentGatewayConfigurationResponse response;
        if (config == null) {

            log.info("No payment gateway mapping found. Returning empty mappings. serviceId={}, tenantId={}, gatewayCode={}", serviceId, tenantId, gatewayCode);

            response = buildEmptyResponse(serviceId, gatewayCode, gatewayConfiguration);
        } else {
            response = buildResponse(config, gatewayConfiguration);
        }

        if (serviceVersionTokenService.isMinorModification(serviceId)) {
            ServicePaymentGatewayConfigHistory historyConfig = serviceVersionTokenService.getBaselinePaymentGatewayConfigHistory(serviceId, gatewayCode);
            if (historyConfig != null) {
                response.setHistoryEnabled(historyConfig.getEnabled());
                response.setOldEnabled(historyConfig.getEnabled());
                if (historyConfig.getMappingJson() != null) {
                    try {
                        JsonNode historyNode = objectMapper.readTree(historyConfig.getMappingJson());
                        response.setHistoryMappings(historyNode);
                        response.setOldMappings(historyNode);
                    } catch (Exception ignored) {}
                }
            }
        }

        return response;
    }

    private PaymentGatewayConfigurationResponse buildEmptyResponse(Integer serviceId, String gatewayCode, JsonNode gatewayConfiguration) {

        PaymentGatewayConfigurationResponse response = new PaymentGatewayConfigurationResponse();

        response.setServiceId(serviceId);
        response.setGatewayCode(gatewayCode);

        response.setGatewayConfiguration(mergeMappings(gatewayConfiguration, null));

        return response;
    }

    @Transactional
    public PaymentGatewayConfigurationResponse saveConfiguration(Integer serviceId, PaymentGatewayMappingRequest request, UserSessionDTO user) {

        String tenantId = user.getTenantId();

        log.info("Saving payment gateway configuration. serviceId={}, tenantId={}, gatewayCode={}", serviceId, tenantId, request.getGatewayCode());

        validateRequest(request);

        JsonNode gatewayConfiguration = paymentGatewayFeignClient
                                                .getGatewayConfiguration(request.getGatewayCode()).getBody();

        if (gatewayConfiguration == null) {

            throw new SPRuntimeError("Payment gateway configuration not found", HttpStatus.NOT_FOUND);
        }

        validateMappings(request.getMappings(), gatewayConfiguration);

        ServicePaymentGatewayConfig existingConfig = repository
                                            .findByServiceIdAndCreatedByAndGatewayCodeAndTenantId(serviceId,user.getUserID(), request.getGatewayCode(), tenantId)
                                            .orElse(null);

        boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
        ServicePaymentGatewayConfigHistory historyConfig = (isMinorVersion)
                ? serviceVersionTokenService.getBaselinePaymentGatewayConfigHistory(serviceId, request.getGatewayCode())
                : null;
        if (historyConfig != null && Boolean.TRUE.equals(historyConfig.getEnabled())
                && Boolean.FALSE.equals(request.getEnabled())
                && isMinorVersion) {
            throw new SPRuntimeError("Removing or disabling a payment gateway once mapped is not allowed in minor version modification", HttpStatus.BAD_REQUEST);
        }

        ServicePaymentGatewayConfig config = (existingConfig != null) ? existingConfig : new ServicePaymentGatewayConfig();

        config.setServiceId(serviceId);
        config.setTenantId(tenantId);
        config.setGatewayCode(request.getGatewayCode());

        config.setEnabled(request.getEnabled() == null ? Boolean.TRUE : request.getEnabled());

        try {

            ObjectNode storedConfiguration = buildStoredConfiguration(request);

            config.setMappingJson(objectMapper.writeValueAsString(storedConfiguration));

        } catch (Exception e) {

            log.error("Unable to serialize payment gateway configuration. serviceId={}, tenantId={}, gatewayCode={}", serviceId, tenantId, request.getGatewayCode(), e);

            throw new SPRuntimeError("Invalid payment gateway configuration", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();

        if (config.getCreatedAt() == null) {
            config.setCreatedAt(now);
            config.setCreatedBy(user.getUserID());
        }

        config.setUpdatedAt(now);

        repository.save(config);

        if (isMinorVersion) {
            serviceVersionTokenService.recordPaymentGatewayChangeLog(
                    serviceId,
                    request.getGatewayCode(),
                    existingConfig,
                    request,
                    user
            );
        }

        log.info("Payment gateway configuration saved successfully. serviceId={}, tenantId={}, gatewayCode={}", serviceId, tenantId, request.getGatewayCode());

        return buildResponse(config, gatewayConfiguration);
    }

    private ObjectNode buildStoredConfiguration(PaymentGatewayMappingRequest request) {

        ObjectNode storedConfiguration = objectMapper.createObjectNode();

        storedConfiguration.set("additionalData", request.getAdditionalData() == null ? objectMapper.createObjectNode() : objectMapper.valueToTree(request.getAdditionalData()));
        storedConfiguration.set("mappings", request.getMappings() == null ? objectMapper.createArrayNode() : objectMapper.valueToTree(request.getMappings()));

        return storedConfiguration;
    }

    private void validateRequest(PaymentGatewayMappingRequest request) {

        if (request == null) {
            throw new SPRuntimeError("Payment gateway request cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (request.getGatewayCode() == null || request.getGatewayCode().isBlank()) {
            throw new SPRuntimeError("Gateway code is required", HttpStatus.BAD_REQUEST);
        }

        if (request.getMappings() == null) {
            throw new SPRuntimeError("Payment gateway mappings are required", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateMappings(List<PaymentGatewayMappingRequest.PaymentGatewayNodeMapping> mappings, JsonNode gatewayConfiguration) {

        JsonNode modes = gatewayConfiguration.get("modes");

        if (modes == null || !modes.isObject()) {
            throw new SPRuntimeError("Invalid gateway configuration: modes not found", HttpStatus.BAD_REQUEST);
        }

        for (PaymentGatewayMappingRequest.PaymentGatewayNodeMapping mapping : mappings) {

            if (mapping.getMode() == null || mapping.getMode().isBlank()) {
                throw new SPRuntimeError("Mode is required", HttpStatus.BAD_REQUEST);
            }

            if (mapping.getNodeCode() == null || mapping.getNodeCode().isBlank()) {
                throw new SPRuntimeError("Node code is required", HttpStatus.BAD_REQUEST);
            }

            JsonNode modeConfiguration = modes.get(mapping.getMode());

            if (modeConfiguration == null) {
                throw new SPRuntimeError("Invalid gateway mode: " + mapping.getMode(), HttpStatus.BAD_REQUEST);
            }

            JsonNode nodes = modeConfiguration.get("nodes");

            if (nodes == null || !nodes.isArray()) {
                throw new SPRuntimeError("No nodes configured for gateway mode: " + mapping.getMode(), HttpStatus.BAD_REQUEST);
            }

            boolean nodeExists = false;

            for (JsonNode node : nodes) {

                if (mapping.getNodeCode().equals(node.path("nodeCode").asText())) {

                    nodeExists = true;
                    break;
                }
            }

            if (!nodeExists) {
                throw new SPRuntimeError("Invalid gateway node: " + mapping.getNodeCode() + " for mode: " + mapping.getMode(), HttpStatus.BAD_REQUEST);
            }
        }

        validateRequiredNodes(mappings, modes);
    }

    private void validateRequiredNodes(List<PaymentGatewayMappingRequest.PaymentGatewayNodeMapping> mappings, JsonNode modes) {

        mappings.stream().map(PaymentGatewayMappingRequest.PaymentGatewayNodeMapping::getMode).distinct().forEach(mode -> {

            JsonNode modeConfiguration = modes.get(mode);

            if (modeConfiguration == null) {
                return;
            }

            JsonNode nodes = modeConfiguration.get("nodes");

            if (nodes == null || !nodes.isArray()) {
                return;
            }

            for (JsonNode node : nodes) {

                boolean required = node.path("required").asBoolean(false);

                if (!required) {
                    continue;
                }

                String nodeCode = node.path("nodeCode").asText();

                boolean mapped = mappings.stream().anyMatch(mapping -> mode.equals(mapping.getMode()) && nodeCode.equals(mapping.getNodeCode()));

                if (!mapped) {
                    throw new SPRuntimeError("Required gateway node is not mapped: " + mode + "." + nodeCode, HttpStatus.BAD_REQUEST);
                }
            }
        });
    }

    private PaymentGatewayConfigurationResponse buildResponse(ServicePaymentGatewayConfig config, JsonNode gatewayConfiguration) {

        PaymentGatewayConfigurationResponse response = new PaymentGatewayConfigurationResponse();

        response.setServiceId(config.getServiceId());
        response.setGatewayCode(config.getGatewayCode());
        response.setGatewayConfiguration(mergeMappings(gatewayConfiguration, config.getMappingJson()));

        return response;
    }

    private JsonNode mergeMappings(JsonNode gatewayConfiguration, String mappingJson) {

        JsonNode configuration = gatewayConfiguration.deepCopy();

        JsonNode storedConfiguration;

        try {
            storedConfiguration = mappingJson == null ? objectMapper.createObjectNode() : objectMapper.readTree(mappingJson);

        } catch (Exception e) {

            log.error("Unable to read payment gateway configuration", e);
            throw new SPRuntimeError("Unable to read payment gateway configuration", HttpStatus.BAD_REQUEST);
        }

        JsonNode additionalData = storedConfiguration.get("additionalData");

        if (additionalData != null && !additionalData.isNull()) {
            ((ObjectNode) configuration).set("additionalData", additionalData.deepCopy());

        } else {
            ((ObjectNode) configuration).set("additionalData", objectMapper.createObjectNode());
        }

        JsonNode mappings = storedConfiguration.get("mappings");

        if (storedConfiguration.isArray()) {
            mappings = storedConfiguration;
        }

        if (mappings == null || !mappings.isArray()) {

            mappings = objectMapper.createArrayNode();
        }

        JsonNode modes = configuration.get("modes");

        if (modes == null || !modes.isObject()) {
            return configuration;
        }

        JsonNode finalMappings = mappings;
        modes.fields().forEachRemaining(modeEntry -> {

            String mode = modeEntry.getKey();

            JsonNode modeConfiguration = modeEntry.getValue();

            JsonNode nodes = modeConfiguration.get("nodes");

            if (nodes == null || !nodes.isArray()) {
                return;
            }

            for (JsonNode node : nodes) {

                String nodeCode = node.path("nodeCode").asText();

                ObjectNode nodeObject = (ObjectNode) node;

                JsonNode mapping = findMapping(finalMappings, mode, nodeCode);

                nodeObject.put("valueType", node.path("valueType").asText(""));

                if (node.has("optionMap")) {
                    nodeObject.set("optionMap", node.get("optionMap").deepCopy());
                }


                if (mapping != null) {
                    nodeObject.put("value", mapping.path("sourceValue").asText(""));

                } else {
                    nodeObject.put("value", "");
                }
            }
        });

        return configuration;
    }

    private JsonNode findMapping(JsonNode mappings, String mode, String nodeCode) {

        if (mappings == null || !mappings.isArray()) {

            return null;
        }

        for (JsonNode mapping : mappings) {

            if (mode.equals(mapping.path("mode").asText()) && nodeCode.equals(mapping.path("nodeCode").asText())) {
                return mapping;
            }
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<PaymentGatewayConfigurationListResponse> getConfigurations(Integer serviceId, UserSessionDTO user) {

        String tenantId = user.getTenantId();

        log.info("Fetching payment gateway configurations. serviceId={}, tenantId={}", serviceId, tenantId);

        List<ServicePaymentGatewayConfig> configurations = repository.findByServiceIdAndTenantId(serviceId, tenantId);

        return configurations.stream()
                .map(config -> {

                    String scope = null;

                    try {

                        if (config.getMappingJson() != null) {

                            JsonNode storedConfiguration = objectMapper.readTree(config.getMappingJson());
                            JsonNode additionalData = storedConfiguration.get("additionalData");

                            if (additionalData != null) {
                                scope = additionalData.path("applicabilityScope").asText(null);
                            }
                        }

                    } catch (Exception e) {

                        log.error(
                                "Unable to read additional payment gateway configuration. serviceId={}, gatewayCode={}",
                                serviceId,
                                config.getGatewayCode(),
                                e
                        );
                    }

                    return new PaymentGatewayConfigurationListResponse(config.getId(), config.getGatewayCode(), config.getGatewayCode(), scope, config.getEnabled());
                })
                .toList();
    }
}