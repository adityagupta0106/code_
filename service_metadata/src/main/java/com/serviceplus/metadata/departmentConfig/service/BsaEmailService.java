package com.serviceplus.metadata.departmentConfig.service;

import com.serviceplus.metadata.departmentConfig.config.BsaRegistrationProperties;
import com.serviceplus.metadata.dto.AttachmentDTO;
import com.serviceplus.metadata.dto.ConfigurationDTO;
import com.serviceplus.metadata.dto.NotificationRequestDTO;
import com.serviceplus.metadata.feignClient.TenantConfigurationFeignClient;
import com.serviceplus.metadata.formSubscription.service.FormSubscriptionKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.serviceplus.metadata.utility.SnowflakeIdGenerator.createUniqueId;

/**
 * Service for sending BSA configuration documents via email,
 * implemented following the publishMail logic from FormSubscriptionService.
 */
@Service
public class BsaEmailService {

    private static final Logger logger = LoggerFactory.getLogger(BsaEmailService.class);

    private final FormSubscriptionKafkaProducer kafkaProducer;
    private final ObjectProvider<TenantConfigurationFeignClient> tenantConfigurationFeignClientProvider;
    private final BsaRegistrationProperties properties;

    public BsaEmailService(FormSubscriptionKafkaProducer kafkaProducer,
                           ObjectProvider<TenantConfigurationFeignClient> tenantConfigurationFeignClientProvider,
                           BsaRegistrationProperties properties) {
        this.kafkaProducer = kafkaProducer;
        this.tenantConfigurationFeignClientProvider = tenantConfigurationFeignClientProvider;
        this.properties = properties;
    }

    /**
     * Dispatches the BSA registration email with attached certificate PDF using
     * the NotificationRequestDTO and FormSubscriptionKafkaProducer pattern.
     */
    public boolean sendConfigurationEmail(String tenantId,
                                          String adminEmail,
                                          String supportDeskEmail,
                                          String serviceCode,
                                          String bsaUserId,
                                          Integer stateCode,
                                          byte[] pdfBytes) {
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            logger.warn("No registered email found for Department Admin. BSA email delivery skipped.");
            return false;
        }

        String targetSupportDeskEmail = supportDeskEmail != null && !supportDeskEmail.trim().isEmpty() ?
                supportDeskEmail.trim() : properties.getSupportDesk().getEmail();

        logger.info("Initiating BSA notification dispatch for adminEmail={}, supportDeskEmail={}, serviceCode={}",
                adminEmail, targetSupportDeskEmail, serviceCode);

        try {
            Map<String, Object> configuration = new HashMap<>();
            String provider = "";

            // 1. Resolve tenant mail configuration (following FormSubscriptionService pattern)
            TenantConfigurationFeignClient feignClient = tenantConfigurationFeignClientProvider.getIfAvailable();
            if (feignClient != null && tenantId != null && !tenantId.isBlank()) {
                try {
                    ResponseEntity<ConfigurationDTO> response = feignClient.getConfig(tenantId, "MAIL,SMS");
                    if (response != null && response.getBody() != null) {
                        ConfigurationDTO configDTO = response.getBody();
                        Map<String, Object> data = configDTO.getData();
                        if (data != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> mailConfig = (Map<String, Object>) data.get("MAIL");
                            if (mailConfig != null) {
                                configuration = mailConfig;
                                provider = (String) configuration.getOrDefault("provider", "");
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Could not fetch tenant mail configuration for tenantId={}: {}. Proceeding with defaults.",
                            tenantId, ex.getMessage());
                }
            }

            // 2. Build NotificationRequestDTO
            NotificationRequestDTO notificationDto = new NotificationRequestDTO();
            notificationDto.setTenantId(tenantId != null ? tenantId : "SERVICEPLUS");
            notificationDto.setChannel("EMAIL");
            notificationDto.setProvider(provider);
            notificationDto.setConfiguration(configuration);
            notificationDto.setCorrelationId(createUniqueId());

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("to", List.of(adminEmail.trim()));
            if (targetSupportDeskEmail != null && !targetSupportDeskEmail.isBlank() && !targetSupportDeskEmail.equalsIgnoreCase(adminEmail.trim())) {
                recipient.put("cc", List.of(targetSupportDeskEmail));
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("subject", "ServicePlus 2.0 - BSA Department Registration Certificate [Service Code: " +
                    (serviceCode != null ? serviceCode : "CONFIDENTIAL") + "]");
            payload.put("body", buildEmailBody(serviceCode, bsaUserId, stateCode));

            if (pdfBytes != null && pdfBytes.length > 0) {
                AttachmentDTO attachment = new AttachmentDTO();
                attachment.setFileName("BSA_Integration_Certificate.pdf");
                attachment.setContentType("application/pdf");
                attachment.setBase64Content(Base64.getEncoder().encodeToString(pdfBytes));
                notificationDto.setAttachments(List.of(attachment));
            }

            notificationDto.setRecipient(recipient);
            notificationDto.setPayload(payload);

            // 3. Publish to Kafka instant-notification topic (following FormSubscriptionService pattern)
            kafkaProducer.publishFormNotificationEvent(notificationDto);
            logger.info("BSA registration email notification published successfully for adminEmail={}", adminEmail);
            return true;

        } catch (Exception ex) {
            logger.error("Failed to publish BSA registration email notification event for adminEmail={}: {}",
                    adminEmail, ex.getMessage());
            return false;
        }
    }

    private String buildEmailBody(String serviceCode, String bsaUserId, Integer stateCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Dear Department Administrator,</p>");
        sb.append("<p>Your department has been successfully registered on the <strong>Beneficiary Satyapan App (BSA)</strong> platform.</p>");
        sb.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse; font-family: sans-serif; font-size: 13px;'>");
        sb.append("<tr><td><strong>Service Code</strong></td><td>").append(serviceCode != null ? serviceCode : "N/A").append("</td></tr>");
        sb.append("<tr><td><strong>BSA User ID</strong></td><td>").append(bsaUserId != null ? bsaUserId : "N/A").append("</td></tr>");
        sb.append("<tr><td><strong>State Code</strong></td><td>").append(stateCode != null ? stateCode : 99).append("</td></tr>");
        sb.append("<tr><td><strong>Registration Status</strong></td><td>REGISTERED</td></tr>");
        sb.append("</table>");
        sb.append("<p>The official integration certificate containing your security configuration is attached to this email.</p>");
        sb.append("<p><strong>Important Security Notice:</strong> Please import these credentials immediately into your secure key vault or HSM. Do not forward or share this document over insecure communication channels.</p>");
        sb.append("<br/><p>Regards,<br/><strong>ServicePlus 2.0 Security Team</strong></p>");
        return sb.toString();
    }
}
