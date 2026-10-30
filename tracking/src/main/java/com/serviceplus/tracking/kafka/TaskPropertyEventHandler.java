package com.serviceplus.tracking.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.dto.TaskPropertyDetailsEvent;
import com.serviceplus.tracking.service.TaskMasterService;

@Service
public class TaskPropertyEventHandler implements KafkaEventHandler<TaskPropertyDetailsEvent> {

	@Value("${kafka.topic.task-property}")
    private String topic;
	
	@Autowired
	private TaskMasterService taskMasterService;
	
	@Override
	public String getTopic() {
		return this.topic;
	}

	@Override
	public Class<TaskPropertyDetailsEvent> getPayloadType() {
		return TaskPropertyDetailsEvent.class;
	}

	@Override
	public void process(TaskPropertyDetailsEvent event) {
		if ("Unfreeze".equalsIgnoreCase(event.getEventType())) {

	        taskMasterService.deleteTaskProperties(event);

	        return;
	    }
		taskMasterService.addTaskProperty(event);
	}

}
