package com.serviceplus.metadata.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.kafka.entity.KafkaOutboxEvent;
import com.serviceplus.metadata.kafka.repository.KafkaOutboxRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

@Service
public class SduNotificationService {

    private static final Logger LOG = LogManager.getLogger("SduNotificationService");
    public static final String NOTIFICATION_TOPIC = "sdu-adoption-notifications";

    private final KafkaOutboxRepository kafkaOutboxRepository;

    @Autowired
    public SduNotificationService(KafkaOutboxRepository kafkaOutboxRepository) {
        this.kafkaOutboxRepository = kafkaOutboxRepository;
    }

    public void sendFirstPendingApplicationNotification(String tenantId, Integer serviceId,
                                                        Integer sduLocationId, Integer submissionLocationId,
                                                        String applicationId, Long designatedOfficerId) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("eventType", "FIRST_PENDING_ADOPTION_APPLICATION");
            payload.put("tenantId", tenantId);
            payload.put("serviceId", serviceId);
            payload.put("sduLocationId", sduLocationId);
            payload.put("submissionLocationId", submissionLocationId);
            payload.put("applicationId", applicationId);
            payload.put("designatedOfficerId", designatedOfficerId);
            payload.put("message", "First pending-adoption application received for location " + submissionLocationId
                    + " under SDU " + sduLocationId + ". Please review and adopt the submission location.");
            payload.put("timestamp", new Date().getTime());

            saveOutboxEvent("SDU_FIRST_PENDING_" + serviceId + "_" + submissionLocationId, payload.toString());
            LOG.info("First pending adoption notification event queued for serviceId={}, submissionLocationId={}",
                    serviceId, submissionLocationId);
        } catch (Exception ex) {
            LOG.error("Failed to queue first pending notification for serviceId={}, locationId={}",
                    serviceId, submissionLocationId, ex);
        }
    }

    public void sendPendingApplicationsReminder(String tenantId, Integer serviceId,
                                                Integer sduLocationId, Integer submissionLocationId,
                                                long pendingCount, Long designatedOfficerId) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("eventType", "PENDING_ADOPTION_REMINDER");
            payload.put("tenantId", tenantId);
            payload.put("serviceId", serviceId);
            payload.put("sduLocationId", sduLocationId);
            payload.put("submissionLocationId", submissionLocationId);
            payload.put("pendingCount", pendingCount);
            payload.put("designatedOfficerId", designatedOfficerId);
            payload.put("message", "Reminder: " + pendingCount + " applications are pending adoption for location "
                    + submissionLocationId + " under SDU " + sduLocationId);
            payload.put("timestamp", new Date().getTime());

            saveOutboxEvent("SDU_REMINDER_" + serviceId + "_" + submissionLocationId, payload.toString());
            LOG.info("Pending adoption reminder queued for serviceId={}, submissionLocationId={}, count={}",
                    serviceId, submissionLocationId, pendingCount);
        } catch (Exception ex) {
            LOG.error("Failed to queue reminder for serviceId={}, locationId={}", serviceId, submissionLocationId, ex);
        }
    }

    public void sendAdoptionReleasedNotification(String tenantId, Integer serviceId,
                                                 Integer sduLocationId, Integer submissionLocationId,
                                                 long releasedCount) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("eventType", "PENDING_APPLICATIONS_RELEASED");
            payload.put("tenantId", tenantId);
            payload.put("serviceId", serviceId);
            payload.put("sduLocationId", sduLocationId);
            payload.put("submissionLocationId", submissionLocationId);
            payload.put("releasedCount", releasedCount);
            payload.put("message", "Submission location " + submissionLocationId
                    + " has been adopted and is operational. " + releasedCount
                    + " pending applications released for workflow processing.");
            payload.put("timestamp", new Date().getTime());

            saveOutboxEvent("SDU_RELEASED_" + serviceId + "_" + submissionLocationId, payload.toString());
            LOG.info("Adoption release notification queued for serviceId={}, locationId={}, count={}",
                    serviceId, submissionLocationId, releasedCount);
        } catch (Exception ex) {
            LOG.error("Failed to queue adoption release notification for serviceId={}, locationId={}",
                    serviceId, submissionLocationId, ex);
        }
    }

    private void saveOutboxEvent(String eventKey, String payload) {
        KafkaOutboxEvent event = new KafkaOutboxEvent();
        event.setId(UUID.randomUUID().toString());
        event.setTopic(NOTIFICATION_TOPIC);
        event.setEventKey(eventKey);
        event.setPayload(payload);
        event.setStatus(ApplicationConstants.OUTBOX_STATUS_PENDING);
        event.setRetryCount(0);
        event.setCreatedAt(Instant.now());
        kafkaOutboxRepository.save(event);
    }
}
