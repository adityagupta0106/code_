package com.serviceplus.metadata.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.GuestEligibilityDTO;
import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.dto.ServiceJSONDTO;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;

@RestController
public class GuestEligibilityController {

    private final IServiceDefinitionRepository serviceDefinitionRepository;
    private final IServiceLogRepository serviceLogRepository;
    private final IServiceMetadataRepository serviceMetadataRepository;

    private final String internalSecret;
    private final Set<String> onlineModeValues;

    public GuestEligibilityController(
            IServiceDefinitionRepository serviceDefinitionRepository,
            IServiceLogRepository serviceLogRepository,
            IServiceMetadataRepository serviceMetadataRepository,
            @Value("${guest.internal-secret:}") String internalSecret,
            @Value("${guest.online-mode-values:112}") String onlineModeValues) {

        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.serviceLogRepository = serviceLogRepository;
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.internalSecret = internalSecret;

        this.onlineModeValues = Arrays.stream(onlineModeValues.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
    }

    @GetMapping("/internal/guest/eligibility")
    public ResponseEntity<?> getGuestEligibility(
            @RequestParam Integer serviceId,
            @RequestHeader(
                    value = "X-Guest-Internal-Key",
                    required = false
            ) String suppliedSecret) {

        /*
         * Guest functionality disabled if internal secret
         * is missing or weak.
         */
        if (internalSecret == null
                || internalSecret.isBlank()
                || internalSecret.length() < 32) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }

        /*
         * Validate Processing -> Metadata internal request.
         */
        if (!secretMatches(internalSecret, suppliedSecret)) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        if (serviceId == null) {
            return ResponseEntity.badRequest().build();
        }

        /*
         * 1. Read only registerToAvail directly from JSONB.
         *
         * Do not load complete ServiceDefinition because older DB JSON
         * may contain fields not present in current ServiceDefinitionDTO.
         */
        String registerToAvail =
                serviceDefinitionRepository
                        .findRegisterToAvailByServiceId(serviceId);

        /*
         * Guest application allowed only when:
         * Register to avail service = N
         */
        
        
        if (!"Y".equalsIgnoreCase(registerToAvail)
                && !"N".equalsIgnoreCase(registerToAvail)) {
            return forbidden();
        }

        boolean registrationRequired =
                "Y".equalsIgnoreCase(registerToAvail);

        /*
         * 2. Service must be launched.
         */
        ServiceLog serviceLog =
                serviceLogRepository.findByServiceId(serviceId);

        if (serviceLog == null) {
            return forbidden();
        }

        if (!Objects.equals(
                serviceLog.getServiceStatus(),
                ServiceStatus.LAUNCHED.getCode())) {

            return forbidden();
        }

        if (!Character.valueOf('Y')
                .equals(serviceLog.getLaunchFlag())) {

            return forbidden();
        }

        if (serviceLog.getTenantId() == null
                || serviceLog.getTenantId().isBlank()) {

            return forbidden();
        }

        /*
         * 3. Read runtime metadata from Mongo.
         */
        ServiceMetadata metadata =
                serviceMetadataRepository
                        .findByServiceIdAndTenantId(
                                serviceId,
                                serviceLog.getTenantId());

        if (metadata == null
                || metadata.getMetadataJson() == null) {

            return forbidden();
        }

        /*
         * Tenant consistency check.
         */
        if (!Objects.equals(
                metadata.getTenantId(),
                serviceLog.getTenantId())) {

            return forbidden();
        }

        /*
         * Base service consistency check.
         */
        if (!Objects.equals(
                metadata.getBaseServiceId(),
                serviceLog.getBaseServiceId())) {

            return forbidden();
        }

        ServiceJSONDTO serviceJson =
                metadata.getMetadataJson();

        /*
         * 4. Online submission mode must contain 112.
         */
        boolean onlineSubmissionAllowed =
                serviceJson.getSubmissionModes() != null
                && serviceJson.getSubmissionModes()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(LabelValue::getValue)
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .map(String::trim)
                        .anyMatch(onlineModeValues::contains);

        if (!onlineSubmissionAllowed) {
            return forbidden();
        }

        /*
         * 5. Applicant task must exist.
         */
        if (isBlank(serviceJson.getApplSubmissionTaskId())) {
            return forbidden();
        }

        /*
         * Applicant form must exist.
         */
        if (isBlank(serviceJson.getApplFormId())) {
            return forbidden();
        }

        GuestEligibilityDTO response =
                new GuestEligibilityDTO(
                        serviceLog.getServiceId(),
                        serviceLog.getBaseServiceId(),
                        serviceLog.getTenantId(),
                        serviceJson.getStateId(),
                        serviceJson.getApplSubmissionTaskId(),
                        serviceJson.getApplFormId(),
                        registrationRequired);

        return ResponseEntity.ok(response);
    }

    private static boolean secretMatches(
            String expected,
            String supplied) {

        if (expected == null || supplied == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static ResponseEntity<Void> forbidden() {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}