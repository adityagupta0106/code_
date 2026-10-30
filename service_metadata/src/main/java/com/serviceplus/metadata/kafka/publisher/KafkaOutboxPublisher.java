package com.serviceplus.metadata.kafka.publisher;

import com.serviceplus.metadata.kafka.entity.KafkaOutboxEvent;
import com.serviceplus.metadata.kafka.repository.KafkaOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static com.serviceplus.metadata.utility.ApplicationConstants.OUTBOX_STATUS_PENDING;
import static com.serviceplus.metadata.utility.ApplicationConstants.OUTBOX_STATUS_PROCESSED;

@Component
public class KafkaOutboxPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaOutboxPublisher.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaOutboxRepository repository;

    @Scheduled(fixedDelayString = "${kafka.outbox.publish.delay-ms}")
    public void publishPendingEvents() {

        List<KafkaOutboxEvent> events = repository.findTop100ByStatusOrderByCreatedAtAsc(OUTBOX_STATUS_PENDING);

        if (events.isEmpty()) {
            return;
        }

        logger.info("Publishing {} kafka outbox events", events.size());

        for (KafkaOutboxEvent event : events) {

            try {

                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload());

                event.setStatus(OUTBOX_STATUS_PROCESSED);
                event.setProcessedAt(Instant.now());
                repository.save(event);

                logger.info("Kafka outbox event published successfully with id : {}", event.getId());

            } catch (Exception e) {

                event.setRetryCount(event.getRetryCount() + 1);
                repository.save(event);

                logger.error("Unable to publish kafka outbox event id : {} error : {}", event.getId(), e.getMessage(), e);
            }
        }
    }
}
