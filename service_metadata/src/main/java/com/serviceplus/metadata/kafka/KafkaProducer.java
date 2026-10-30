package com.serviceplus.metadata.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import static com.serviceplus.metadata.utility.CommonUtil.entityToString;

@Service
public class KafkaProducer {

    private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String key, Object payload) {

        try {
            String message = entityToString(payload);

            KafkaLogger.info("Sending kafka message to topic {} with key {}", topic, key);

            kafkaTemplate.send(topic, key, message).whenComplete((result, ex) -> {

                if (ex != null) {
                    KafkaLogger.error("Kafka publish failed for topic {} error {}", topic, ex.getMessage(), ex);
                } else {
                    KafkaLogger.info("Kafka publish success for topic {} offset {}", topic, result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            KafkaLogger.error("Unable to publish kafka message for topic {} error {}", topic, e.getMessage(), e);
        }
    }
}