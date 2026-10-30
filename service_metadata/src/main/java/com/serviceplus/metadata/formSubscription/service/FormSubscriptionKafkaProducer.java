package com.serviceplus.metadata.formSubscription.service;

import static com.serviceplus.metadata.utility.CommonUtil.entityToString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.NotificationRequestDTO;
import com.serviceplus.metadata.formSubscription.dto.EmailSMSNotificationDto;

@Service
public class FormSubscriptionKafkaProducer {
	private static final Logger FORM_SUBSCRIPTION = LogManager.getLogger("FormSubscription");

    private static final String TOPIC = "kafka.topic.instant-notification";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishFreezeEvent(EmailSMSNotificationDto event) {
    	
        String key = String.valueOf(event.getServiceId());
        String message = entityToString(event);
        FORM_SUBSCRIPTION.info("Publishing Form Subscription Email Event for topic {} with key {} and message {}",TOPIC,key,message);
        kafkaTemplate.send(TOPIC, key, message)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                	FORM_SUBSCRIPTION.info("Publishing Form Subscription Email Event fails with exception {}",ex);
                } else {
                	FORM_SUBSCRIPTION.info("Form Subscription Email Event published successfully for key {}",key);
                }
            });
    }
    public void publishFormNotificationEvent(NotificationRequestDTO event) {
    	
    	String key = String.valueOf(event.getCorrelationId());
    	String message = entityToString(event);
    	FORM_SUBSCRIPTION.info("Publishing Form Subscription Email Event for topic {} with key {} and message {}",TOPIC,key,message);
    	kafkaTemplate.send(TOPIC, key, message)
    	.whenComplete((result, ex) -> {
    		if (ex != null) {
    			FORM_SUBSCRIPTION.info("Publishing Form Subscription Email Event fails with exception {}",ex);
    		} else {
    			FORM_SUBSCRIPTION.info("Form Subscription Email Event published successfully for key {}",key);
    		}
    	});
    }
}
