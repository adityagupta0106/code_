package com.serviceplus.tracking.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.serviceplus.tracking.dto.FormDataEvent;
import com.serviceplus.tracking.service.ApplAttrKafkaService;

@Component
public class ApplAttrKafkaConsumer implements KafkaEventHandler<FormDataEvent> {

	@Value("${kafka.topic.inboxSentboxFilter}")
	private String topic;

	private final ApplAttrKafkaService applAttrKafkaService;

	public ApplAttrKafkaConsumer(ApplAttrKafkaService applAttrKafkaService) {
		this.applAttrKafkaService = applAttrKafkaService;
	}

	@Override
	public String getTopic() {
		return this.topic;
	}

	@Override
	public Class<FormDataEvent> getPayloadType() {
		return FormDataEvent.class;
	}

	@Override
	public void process(FormDataEvent event) {
		applAttrKafkaService.persistMessage(event);
	}

}
