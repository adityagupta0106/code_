package com.serviceplus.tracking.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.serviceplus.tracking.dto.FilterKafkaMessage;
import com.serviceplus.tracking.service.ServiceFilterConfigService;

@Component
public class ServiceFilterConfigConsumer implements KafkaEventHandler<FilterKafkaMessage> {

	@Value("${kafka.topic.filter.configuration}")
	private String topic;

	private final ServiceFilterConfigService serviceFilterConfigService;

	public ServiceFilterConfigConsumer(ServiceFilterConfigService serviceFilterConfigService) {
		this.serviceFilterConfigService = serviceFilterConfigService;
	}

	@Override
	public String getTopic() {
		return this.topic;
	}

	@Override
	public Class<FilterKafkaMessage> getPayloadType() {
		return FilterKafkaMessage.class;
	}

	@Override
	public void process(FilterKafkaMessage event) {
		serviceFilterConfigService.persistMessage(event);
	}
}