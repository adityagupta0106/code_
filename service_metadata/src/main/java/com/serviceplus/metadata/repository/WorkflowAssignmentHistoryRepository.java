package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.WorkflowAssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowAssignmentHistoryRepository extends JpaRepository<WorkflowAssignmentHistory,Long> {

    Optional<WorkflowAssignmentHistory> findTopByHolderIdAndTenantIdOrderByUpdOnDesc(String holderId, String tenantId);

    List<WorkflowAssignmentHistory> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
