package com.serviceplus.metadata.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
	
	private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");
	
	public static final String RECEIVED_MESSAGE_KEY = "kafka_receivedMessageKey";
	
	@Autowired
	private KafkaService kafkaService;
	
    @KafkaListener(topics = "${kafka.update.credential.topic}" , groupId = "${kafka.update.credential.topic.grp}")
    public void getUserDetail(@Header(RECEIVED_MESSAGE_KEY) String key,String message) {
    	kafkaService.updateUserCredential(key,message);
    }
}
