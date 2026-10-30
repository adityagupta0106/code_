package com.serviceplus.metadata.notification.service;

import static com.serviceplus.metadata.utility.CommonUtil.entityToString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.notification.dto.NotificationConfigEvent;

@Service
public class NotificationKafkaProducer {
	private static final Logger kafkaLogger = LogManager.getLogger("notificationKafkaLogger");
	private static final String TOPIC = "notification-configuration-topic";

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	public void publishFreezeEvent(NotificationConfigEvent event) {

		String key = String.valueOf(event.getServiceId());
		String message = entityToString(event);
		kafkaLogger.info("Publishing Freeze Event for topic {} with key {} and message {}", TOPIC, key, message);
		kafkaTemplate.send(TOPIC, key, message).whenComplete((result, ex) -> {
			if (ex != null) {
				kafkaLogger.info("Publishing Freeze Event fails with exception {}", ex);
			} else {
				kafkaLogger.info("Freeze Event published successfully for key {}", key);
			}
		});
	}

	public void publishUnfreezeEvent(NotificationConfigEvent event) {

		String key = String.valueOf(event.getServiceId());
		String message = entityToString(event);
		kafkaLogger.info("Publishing UnFreeze Event for topic {} with key {} and message {}", TOPIC, key, event);
		kafkaTemplate.send(TOPIC, key, message);
	}

}
