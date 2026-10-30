package com.serviceplus.tracking.correlationapplication.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.tracking.correlationapplication.service.CorrelationSetFormDataService;
import com.serviceplus.tracking.dto.FormDataEvent;

@Service
public class CorrelationSetFormDataKafkaConsumer {

	private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");

	private final ObjectMapper objectMapper;

	private final CorrelationSetFormDataService correlationSetFormDataService;

	public CorrelationSetFormDataKafkaConsumer(ObjectMapper objectMapper,
			CorrelationSetFormDataService correlationSetFormDataService) {

		this.objectMapper = objectMapper;
		this.correlationSetFormDataService = correlationSetFormDataService;
	}

	@KafkaListener(topics = "${correlation.set.form-data.kafka.topic}", groupId = "${correlation.set.form-data.kafka.consumer.group-id}")
	public void consumeCorrelationSetFormData(String message) {

		KafkaLogger.info("Correlation Set Form Data Kafka message received: {}", message);

		try {

			FormDataEvent formDataDTO = objectMapper.readValue(message, FormDataEvent.class);

			correlationSetFormDataService.processFormData(formDataDTO);

		} catch (Exception e) {

			KafkaLogger.error("Failed to process correlation set form data", e);

			throw new RuntimeException("Failed to process correlation set form data", e);
		}
	}
}