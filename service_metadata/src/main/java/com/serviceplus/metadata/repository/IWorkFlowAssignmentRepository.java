package com.serviceplus.metadata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceplus.metadata.entity.WorkflowAssignment;

public interface IWorkFlowAssignmentRepository extends JpaRepository<WorkflowAssignment, String>{

	List<WorkflowAssignment> findByServiceIdAndTaskIdAndLocationId(Integer serviceId, String taskId, String locationId);

}
