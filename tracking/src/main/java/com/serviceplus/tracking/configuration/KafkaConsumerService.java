package com.serviceplus.tracking.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.service.InboxConsumerService;

import jakarta.annotation.PostConstruct;

@Service
public class KafkaConsumerService {
	
	public static final String RECEIVED_MESSAGE_KEY = "kafka_receivedMessageKey";
	public static final String RECEIVED_MESSAGE_TOPIC = "kafka_receivedTopic";
	
	@Value("#{'${communicate.topics}'.split(',')}")
    private List<String> topics;
	
	@Autowired
	private InboxConsumerService inboxConsumerService;
	
	
	@KafkaListener(topics = "#{'${communicate.topics}'.split(',')}", groupId = "inbox-group")
    public void listen(@Header(RECEIVED_MESSAGE_KEY) String key, String message,
                       @Header(RECEIVED_MESSAGE_TOPIC) String topic) {
       inboxConsumerService.updateUserInboxFromKafka(key, message, topic);
    }

    @PostConstruct
    public void showTopics() {
        System.out.println("Kafka Listeners initialized for topics: " + topics);
    }
}


