package com.serviceplus.metadata.kafka.service;

import com.serviceplus.metadata.kafka.entity.KafkaOutboxEvent;
import com.serviceplus.metadata.kafka.repository.KafkaOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.serviceplus.metadata.utility.ApplicationConstants.OUTBOX_STATUS_PENDING;
import static com.serviceplus.metadata.utility.ApplicationConstants.OUTBOX_TOKEN_PREFIX;
import static com.serviceplus.metadata.utility.CommonUtil.entityToString;
import static com.serviceplus.metadata.utility.CommonUtil.generateUniqueToken;

@Service
public class KafkaOutboxService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaOutboxService.class);

    @Autowired
    private KafkaOutboxRepository repository;

    public void saveEvent(String topic, String eventKey, Object payload) {

        try {

            KafkaOutboxEvent event = new KafkaOutboxEvent();

            event.setId(generateUniqueToken(OUTBOX_TOKEN_PREFIX));
            event.setTopic(topic);
            event.setEventKey(eventKey);
            event.setPayload(entityToString(payload));
            event.setStatus(OUTBOX_STATUS_PENDING);
            event.setRetryCount(0);
            event.setCreatedAt(Instant.now());

            repository.save(event);

            logger.info("Kafka outbox event saved successfully for topic : {}", topic);

        } catch (Exception e) {

            logger.error("Unable to save kafka outbox event : {}", e.getMessage(), e);
        }
    }
}