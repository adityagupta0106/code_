package com.serviceplus.tracking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.dto.TaskPropertyDetailsEvent;
import com.serviceplus.tracking.entity.TaskMaster;
import com.serviceplus.tracking.repository.TaskMasterRepository;

@Service
public class TaskMasterService {
	
    private static final Logger log = LoggerFactory.getLogger(TaskMasterService.class);

	private final TaskMasterRepository taskMasterRepository;
	
	public TaskMasterService(TaskMasterRepository taskMasterRepository) {
		this.taskMasterRepository = taskMasterRepository;
	}


	@Transactional
	public void addTaskProperty(TaskPropertyDetailsEvent event) {

	    if (event == null || event.getTasks() == null || event.getTasks().isEmpty()) {
	        return;
	    }

	    for (TaskPropertyDetailsEvent.TaskPropertyDetails task : event.getTasks()) {

	        TaskMaster taskMaster = taskMasterRepository
	                .findByBaseServiceIdAndTaskIdAndTenantId(
	                		event.getBaseServiceId(),
	                        task.getTaskId(),
	                        event.getTenantId()
	                )
	                .orElse(new TaskMaster());

	        taskMaster.setTaskId(task.getTaskId());
	        taskMaster.setServiceId(event.getServiceId());
	        taskMaster.setBaseServiceId(event.getBaseServiceId());
	        taskMaster.setTaskName(task.getTaskName());
	        taskMaster.setTaskProperty(task.getTaskProperty());
	        taskMaster.setTenantId(event.getTenantId());

	        taskMasterRepository.save(taskMaster);
	    }
	}


	@Transactional
	public void deleteTaskProperties(TaskPropertyDetailsEvent event) {

		if (event == null || event.getServiceId() == null || event.getTenantId() == null) {
			return;
		}

		log.info("Deleting task properties. ServiceId: {}, TenantId: {}", event.getServiceId(), event.getTenantId());

		taskMasterRepository.deleteByServiceIdAndTenantId(event.getServiceId(), event.getTenantId());

		log.info("Task properties deleted. ServiceId: {}, TenantId: {}", event.getServiceId(), event.getTenantId());
	}

}
