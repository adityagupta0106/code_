package com.serviceplus.tracking.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class GenericKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GenericKafkaConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, KafkaEventHandler<?>> handlerMap = new HashMap<>();

    @Autowired
    public GenericKafkaConsumer(List<KafkaEventHandler<?>> handlers) {

        for (KafkaEventHandler<?> handler : handlers) {
            handlerMap.put(handler.getTopic(), handler);
        }
    }

    @KafkaListener(
            topics = "#{'${kafka.consumer.topics}'.split(',')}",
            groupId = "${kafka.consumer.group-id}"
    )
    public void consume(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {

            logger.info("Kafka message received for topic : {} message : {}", topic, message);

            KafkaEventHandler<?> handler = handlerMap.get(topic);

            if (isNull(handler)) {
                logger.error("No handler configured for topic : {}", topic);
                return;
            }

            Object event = objectMapper.readValue(message, handler.getPayloadType());
            processEvent(handler, event);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to process kafka message for topic : {} error : {}", topic, e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void processEvent(KafkaEventHandler<?> handler, Object event) {
        ((KafkaEventHandler<T>) handler).process((T) event);
    }
}
