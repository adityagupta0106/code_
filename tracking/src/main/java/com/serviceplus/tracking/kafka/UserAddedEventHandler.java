package com.serviceplus.tracking.kafka;

import com.serviceplus.tracking.dto.WorkflowAssignmentKafkaEvent;
import com.serviceplus.tracking.service.WorkflowInboxRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserAddedEventHandler implements KafkaEventHandler<WorkflowAssignmentKafkaEvent> {

    @Value("${kafka.topic.workflow-user-added}")
    private String topic;

    @Autowired
    private WorkflowInboxRedisService redisService;

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public Class<WorkflowAssignmentKafkaEvent> getPayloadType() {
        return WorkflowAssignmentKafkaEvent.class;
    }

    @Override
    public void process(WorkflowAssignmentKafkaEvent event) {
        redisService.addTokenForUsers(event);
    }
}
