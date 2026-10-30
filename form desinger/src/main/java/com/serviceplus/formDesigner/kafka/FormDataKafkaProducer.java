package com.serviceplus.formDesigner.kafka;


import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.FormDataEventDTO;

@Service
public class FormDataKafkaProducer {

	private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String MVEL_TOPIC = "mvel-form-data-topic";
    private static final String DOCUMENT_TOPIC = "document-form-data-topic";
    private static final String NOTIFICATION_TOPIC = "notification-form-data-topic";
    private static final String INBOXSENTBOX_FILTER_TOPIC = "inboxSentboxFilter-form-data-topic";
	private static final String CORRELATION_SET_TOPIC = "correlationSet-form-data-topic";

    @Value("${kafka.publish.form.data.topic}")
    private String FORM_DATA_TOPIC;

    public FormDataKafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendCorrelationSet(FormDataEventDTO dto) {

    	try {
			String message = objectMapper.writeValueAsString(dto);

			kafkaTemplate.send(CORRELATION_SET_TOPIC, dto.getApplicationId() + "$"  + String.join(",", dto.getTaskIds()), message)
					.whenComplete((result, ex) -> {
						if (ex != null) {
							KafkaLogger.error("Kafka send failed for applicationId: {}", dto.getApplicationId(), ex);
						} else {
							KafkaLogger.info("Kafka send success for applicationId: {}", dto.getApplicationId());
						}
					});

		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize ApplicationDataEventDTO", e);
		}
    }
    
	public void sendInboxSentboxFilter(FormDataEventDTO dto) {

		try {
			String message = objectMapper.writeValueAsString(dto);

			kafkaTemplate.send(INBOXSENTBOX_FILTER_TOPIC, dto.getApplicationId() + "$" + String.join(",", dto.getTaskIds()), message)
					.whenComplete((result, ex) -> {
						if (ex != null) {
							KafkaLogger.error("Kafka send failed for applicationId: {}", dto.getApplicationId(), ex);
						} else {
							KafkaLogger.info("Kafka send success for applicationId: {}", dto.getApplicationId());
						}
					});

		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize ApplicationDataEventDTO", e);
		}
	}
    
	public void sendNotification(FormDataEventDTO dto) {
	    	
	    	try {
	    		String message = objectMapper.writeValueAsString(dto);
	    		
	    		kafkaTemplate.send(NOTIFICATION_TOPIC, dto.getApplicationId()+"$"+ String.join(",", dto.getTaskIds()), message)
			.whenComplete((result, ex) -> {
				if (ex != null) {
					KafkaLogger.error("Kafka send failed for applicationId: {}", dto.getApplicationId(), ex);
				} else {
					KafkaLogger.info("Kafka send success for applicationId: {}", dto.getApplicationId());
				}
			});
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize ApplicationDataEventDTO", e);
		}
	}
	
    public void sendMvel(FormDataEventDTO dto) {

        try {
            String message = objectMapper.writeValueAsString(dto);

            kafkaTemplate.send(MVEL_TOPIC, dto.getApplicationId()+"$"+ String.join(",", dto.getTaskIds()), message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                        	KafkaLogger.error("Kafka send failed for applicationId: {}", dto.getApplicationId(), ex);
                        } else {
                        	KafkaLogger.info("Kafka send success for applicationId: {}", dto.getApplicationId());
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize ApplicationDataEventDTO", e);
        }
    }
    public void sendDocument(FormDataEventDTO dto) {
    	
    	try {
    		String message = objectMapper.writeValueAsString(dto);
    		
    		kafkaTemplate.send(DOCUMENT_TOPIC, dto.getApplicationId()+"$"+ String.join(",", dto.getTaskIds()), message)
    		.whenComplete((result, ex) -> {
    			if (ex != null) {
    				KafkaLogger.error("Kafka send failed for applicationId: {}", dto.getApplicationId(), ex);
    			} else {
    				KafkaLogger.info("Kafka send success for applicationId: {}", dto.getApplicationId());
    			}
    		});
    		
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to serialize ApplicationDataEventDTO", e);
    	}
    }

    public void sendFormData(FormDataEventDTO dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(
                            FORM_DATA_TOPIC,
                            dto.getApplicationId() + "$" + UUID.randomUUID(),
                            message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            KafkaLogger.error(
                                    "Kafka send failed for applicationId: {}",
                                    dto.getApplicationId(),
                                    ex);
                        } else {
                            KafkaLogger.info(
                                    "Kafka send success for applicationId : {}",
                                    dto.getApplicationId());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to serialize FormDataEventDTO",
                    e);
        }
    }
}
