package com.serviceplus.metadata.kafka;

import com.serviceplus.metadata.kafka.dto.WorkflowAssignmentKafkaEvent;
import com.serviceplus.metadata.kafka.service.KafkaOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.serviceplus.metadata.utility.CommonUtil.serviceIdToBaseServiceId;

@Service
public class WorkflowAssignmentKafkaProducer {

    @Value("${kafka-topic-workflow-user-added}")
    private String USER_ADDED_TOPIC;

    @Value("${kafka-topic-workflow-user-removed}")
    private String USER_REMOVED_TOPIC;

    @Autowired
    private KafkaOutboxService kafkaOutboxService;

    public void publishUserAddedEvent(String tokenKey, Integer serviceId, String taskId, String locationId, String tenantId, List<Long> userIds) {

        WorkflowAssignmentKafkaEvent event = new WorkflowAssignmentKafkaEvent();

        event.setTokenKey(tokenKey);
        event.setServiceId(serviceId);
        event.setTaskId(taskId);
        event.setLocationId(locationId);
        event.setTenantId(tenantId);
        event.setUserIds(userIds);
        event.setEventType("USER_ADDED");

        kafkaOutboxService.saveEvent(USER_ADDED_TOPIC, tokenKey, event);
    }

    public void publishUserRemovedEvent(String tokenKey, Integer serviceId, String taskId, String locationId, String tenantId, List<Long> userIds) {

        WorkflowAssignmentKafkaEvent event = new WorkflowAssignmentKafkaEvent();

        event.setTokenKey(tokenKey);
        event.setServiceId(serviceId);
        event.setTaskId(taskId);
        event.setLocationId(locationId);
        event.setTenantId(tenantId);
        event.setUserIds(userIds);
        event.setEventType("USER_REMOVED");
        event.setBaseServiceId(serviceIdToBaseServiceId(serviceId));

        kafkaOutboxService.saveEvent(USER_REMOVED_TOPIC, tokenKey, event);
    }
}
