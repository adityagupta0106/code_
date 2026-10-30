package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.WorkflowAssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IWorkflowAssignmentHistoryRepository extends JpaRepository<WorkflowAssignmentHistory, Long> {

    List<WorkflowAssignmentHistory> findByServiceId(Integer serviceId);
}
