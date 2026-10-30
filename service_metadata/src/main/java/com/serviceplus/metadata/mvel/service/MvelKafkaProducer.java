package com.serviceplus.metadata.mvel.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.mvel.dto.MvelFunctionEvent;

import static com.serviceplus.metadata.utility.CommonUtil.entityToString;

@Service
public class MvelKafkaProducer {

	private static final Logger kafkaLogger = LogManager.getLogger("mvelKafkaLogger");
    private static final String TOPIC = "mvel-function-topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishFreezeEvent(MvelFunctionEvent event) {
    	
        String key = String.valueOf(event.getServiceId());
        String message = entityToString(event);
        kafkaLogger.info("Publishing Freeze Event for topic {} with key {} and message {}",TOPIC,key,message);
        kafkaTemplate.send(TOPIC, key, message)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                	kafkaLogger.info("Publishing Freeze Event fails with exception {}",ex);
                } else {
                	kafkaLogger.info("Freeze Event published successfully for key {}",key);
                }
            });
    }

    public void publishUnfreezeEvent(MvelFunctionEvent event) {

        String key = String.valueOf(event.getServiceId());
        String message = entityToString(event);
        kafkaLogger.info("Publishing UnFreeze Event for topic {} with key {} and message {}",TOPIC,key,event);
        kafkaTemplate.send(TOPIC, key, message);
    }
}
