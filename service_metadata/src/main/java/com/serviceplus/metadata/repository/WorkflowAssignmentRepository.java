package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.WorkflowAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowAssignmentRepository extends JpaRepository<WorkflowAssignment, String> {

    Optional<WorkflowAssignment> findByServiceIdAndTaskIdAndLocationIdAndUserIdAndTenantId(
            Integer serviceId,
            String taskId,
            String locationId,
            Long userId,
            String tenantId);

    Optional<WorkflowAssignment> findByServiceIdAndTaskIdAndLocationIdAndTenantId(Integer serviceId,
                                                                              String taskId,
                                                                              String locationId,
                                                                              String tenantId);

    boolean existsByServiceIdAndTaskIdAndLocationIdAndUserIdAndTenantId(Integer serviceId, String taskId, String locationId, Integer id, String tenantId);

    List<WorkflowAssignment> findAllByServiceIdAndTaskIdAndLocationIdAndTenantId(Integer serviceId, String taskId, String locationId, String tenantId);

    List<WorkflowAssignment> findByUserIdAndLocationIdAndTenantId(Long userId, String locationId, String tenantId);

    List<WorkflowAssignment> findAllByServiceIdAndTaskIdAndTenantId(Integer serviceId, String taskId, String tenantId);

    List<WorkflowAssignment> findAllByServiceIdAndTenantId(Integer serviceId, String tenantId);

    List<WorkflowAssignment> findAllByServiceIdAndLocationIdAndTenantId(Integer serviceId, String string, String tenantId);

    List<WorkflowAssignment> findAllByServiceIdAndTaskIdAndLocationId(Integer serviceId, String taskId, String locationId);

    Optional<WorkflowAssignment> findByHolderIdAndTenantId(String holderId,String tenantId);

    long countByUserIdAndTenantId(Long userId, String tenantId);

    long countByUserId(Long userId);

    List<WorkflowAssignment> findByUserIdAndLocationId(Long userID, String string);

    Optional<WorkflowAssignment> findByServiceIdAndTaskIdAndLocationIdAndUserId(Integer serviceId, String taskId, String string, Long userID);

    List<WorkflowAssignment> findAllByServiceId(Integer serviceId);

    List<WorkflowAssignment> findByServiceId(Integer serviceId);

    List<WorkflowAssignment> findAllByServiceIdAndUserIdIsNotNull(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
