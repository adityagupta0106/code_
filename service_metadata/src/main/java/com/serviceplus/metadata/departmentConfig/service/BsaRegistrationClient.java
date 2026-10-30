package com.serviceplus.metadata.departmentConfig.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.departmentConfig.client.BsaEsbFeignClient;
import com.serviceplus.metadata.departmentConfig.config.BsaRegistrationProperties;
import com.serviceplus.metadata.departmentConfig.dto.BsaRegistrationExternalRequestDTO;
import com.serviceplus.metadata.departmentConfig.dto.BsaRegistrationExternalResponseDTO;
import com.serviceplus.metadata.departmentConfig.dto.IntegrationRequest;
import com.serviceplus.metadata.departmentConfig.dto.IntegrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class BsaRegistrationClient {

    private static final Logger log = LoggerFactory.getLogger(BsaRegistrationClient.class);

    private final RestTemplate restTemplate;
    private final BsaRegistrationProperties properties;
    private final ObjectProvider<BsaEsbFeignClient> esbFeignClientProvider;
    private final ObjectMapper objectMapper;

    public BsaRegistrationClient(RestTemplateBuilder restTemplateBuilder,
                                 BsaRegistrationProperties properties,
                                 ObjectProvider<BsaEsbFeignClient> esbFeignClientProvider) {
        this.properties = properties;
        this.esbFeignClientProvider = esbFeignClientProvider;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(properties.getRegistration().getConnectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(properties.getRegistration().getReadTimeoutMs()))
                .build();
    }

    /**
     * Registers the department with BSA.
     * In ServicePlus 2.0 microservice architecture, external calls go through ESB.
     */
    public BsaRegistrationExternalResponseDTO registerDepartment(BsaRegistrationExternalRequestDTO request) {
            return callViaEsb(request);
    }

    private BsaRegistrationExternalResponseDTO callViaEsb(BsaRegistrationExternalRequestDTO request) {
        String targetUrl = properties.getRegistration().getUrl();
        log.info("Routing external BSA Registration call via ESB (provider: {}, url: {}) for departmentId: {}, requestId: {}",
                properties.getEsb().getProvider(), targetUrl, request.getDepartmentId(), request.getRequestId());

        IntegrationRequest esbRequest = new IntegrationRequest();
        esbRequest.setProvider("BSA");
        esbRequest.setOperation("REGISTER");
        esbRequest.setMethod("POST");
        esbRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
        esbRequest.setProtocol("REST");
        esbRequest.setUrl(targetUrl);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        if (properties.getRegistration().getApiKey() != null && !properties.getRegistration().getApiKey().isBlank()) {
            headers.put("X-API-KEY", properties.getRegistration().getApiKey());
            headers.put("Authorization", "Bearer " + properties.getRegistration().getApiKey());
        }
        headers.put("X-Request-Id", request.getRequestId());
        esbRequest.setHeaders(headers);

        try {
            esbRequest.setBody(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.warn("Failed to serialize request to JSON string, passing object: {}", e.getMessage());
            esbRequest.setBody(request);
        }

        IntegrationResponse esbResponse = null;

        // 1. Try Feign client to ESB
        BsaEsbFeignClient feignClient = esbFeignClientProvider.getIfAvailable();
        if (feignClient != null) {
            try {
                log.info("Invoking BSA registration via ESB Feign Client (operation: {}, departmentId: {})",
                        esbRequest.getOperation(), request.getDepartmentId());
                esbResponse = feignClient.invoke(esbRequest);
            } catch (Exception ex) {
                log.warn("ESB Feign client invocation failed: {}. Trying ESB HTTP invoke URL.", ex.getMessage());
            }
        }

        // 2. Fallback to direct HTTP call to ESB invoke-url
        if (esbResponse == null) {
            try {
                String esbInvokeUrl = properties.getEsb().getInvokeUrl();
                log.info("Invoking BSA registration via ESB RestTemplate at: {} for departmentId: {}",
                        esbInvokeUrl, request.getDepartmentId());

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<IntegrationRequest> entity = new HttpEntity<>(esbRequest, httpHeaders);

                ResponseEntity<IntegrationResponse> responseEntity =
                        restTemplate.postForEntity(esbInvokeUrl, entity, IntegrationResponse.class);
                esbResponse = responseEntity.getBody();
            } catch (Exception ex) {
                log.error("RestTemplate call to ESB invoke URL failed: {}", ex.getMessage());
            }
        }

        if (esbResponse != null && esbResponse.getBody() != null) {
            return convertToExternalResponse(esbResponse.getBody(), esbResponse.isSuccess(), esbResponse.getStatusCode());
        }

        log.error("ESB invocation failed or returned empty response for departmentId: {}.", request.getDepartmentId());
        BsaRegistrationExternalResponseDTO failResp = new BsaRegistrationExternalResponseDTO();
        failResp.setSuccess(false);
        failResp.setMessage("ESB invocation failed: " + (esbResponse != null && esbResponse.getMessage() != null ? esbResponse.getMessage() : "No response from ESB"));
        failResp.setErrorCode("ESB_ERROR");
        return failResp;
    }

    private BsaRegistrationExternalResponseDTO convertToExternalResponse(Object body, boolean success, int statusCode) {
        if (body instanceof BsaRegistrationExternalResponseDTO dto) {
            return dto;
        }
        try {
            if (body instanceof String jsonStr) {
                return objectMapper.readValue(jsonStr, BsaRegistrationExternalResponseDTO.class);
            }
            return objectMapper.convertValue(body, BsaRegistrationExternalResponseDTO.class);
        } catch (Exception ex) {
            log.error("Failed to parse ESB response body into BsaRegistrationExternalResponseDTO: {}", ex.getMessage());
            BsaRegistrationExternalResponseDTO err = new BsaRegistrationExternalResponseDTO();
            err.setSuccess(success);
            err.setErrorCode(String.valueOf(statusCode));
            err.setMessage("Failed to deserialize BSA response from ESB");
            return err;
        }
    }

//    private BsaRegistrationExternalResponseDTO callDirect(BsaRegistrationExternalRequestDTO request) {
//        String targetUrl = properties.getRegistration().getUrl();
//        log.info("Calling direct external BSA Registration API at: {} for departmentId: {}, requestId: {}",
//                targetUrl, request.getDepartmentId(), request.getRequestId());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        if (properties.getRegistration().getApiKey() != null && !properties.getRegistration().getApiKey().isBlank()) {
//            headers.set("X-API-KEY", properties.getRegistration().getApiKey());
//            headers.set("Authorization", "Bearer " + properties.getRegistration().getApiKey());
//        }
//        headers.set("X-Request-Id", request.getRequestId());
//
//        HttpEntity<BsaRegistrationExternalRequestDTO> entity = new HttpEntity<>(request, headers);
//
//        try {
//            ResponseEntity<BsaRegistrationExternalResponseDTO> response = restTemplate.postForEntity(
//                    targetUrl, entity, BsaRegistrationExternalResponseDTO.class);
//
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                log.info("BSA registration succeeded remotely for departmentId: {}, serviceCode: {}",
//                        request.getDepartmentId(), response.getBody().getServiceCode());
//                return response.getBody();
//            } else {
//                log.warn("BSA registration API returned non-2xx status: {} for departmentId: {}",
//                        response.getStatusCode(), request.getDepartmentId());
//                BsaRegistrationExternalResponseDTO failResp = new BsaRegistrationExternalResponseDTO();
//                failResp.setSuccess(false);
//                failResp.setMessage("BSA API returned unexpected HTTP status: " + response.getStatusCode());
//                failResp.setErrorCode(String.valueOf(response.getStatusCode().value()));
//                return failResp;
//            }
//
//        } catch (HttpClientErrorException ex) {
//            log.error("Client error during BSA registration. HTTP Status: {}, Response: {}",
//                    ex.getStatusCode(), ex.getResponseBodyAsString());
//            BsaRegistrationExternalResponseDTO failResp = new BsaRegistrationExternalResponseDTO();
//            failResp.setSuccess(false);
//            failResp.setMessage("BSA registration failed (Client Error): " + ex.getStatusText());
//            failResp.setErrorCode(String.valueOf(ex.getStatusCode().value()));
//            return failResp;
//
//        } catch (HttpServerErrorException ex) {
//            log.error("Server error from BSA registration API. HTTP Status: {}, Response: {}",
//                    ex.getStatusCode(), ex.getResponseBodyAsString());
//            BsaRegistrationExternalResponseDTO failResp = new BsaRegistrationExternalResponseDTO();
//            failResp.setSuccess(false);
//            failResp.setMessage("BSA registration service temporarily unavailable: " + ex.getStatusText());
//            failResp.setErrorCode(String.valueOf(ex.getStatusCode().value()));
//            return failResp;
//
//        } catch (ResourceAccessException ex) {
//            log.error("Network timeout or connection refused while connecting to BSA registration API at: {}",
//                    targetUrl, ex);
//            BsaRegistrationExternalResponseDTO failResp = new BsaRegistrationExternalResponseDTO();
//            failResp.setSuccess(false);
//            failResp.setMessage("Connection timeout or unreachable BSA endpoint");
//            failResp.setErrorCode("BSA_TIMEOUT");
//            return failResp;
//
//        } catch (Exception ex) {
//            log.error("Unexpected error calling BSA registration API", ex);
//            BsaRegistrationExternalResponseDTO failResp = new BsaRegistrationExternalResponseDTO();
//            failResp.setSuccess(false);
//            failResp.setMessage("Unexpected error connecting to BSA: " + ex.getMessage());
//            failResp.setErrorCode("BSA_INTERNAL_ERROR");
//            return failResp;
//        }
//    }
}
