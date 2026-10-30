package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.TaskMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskMasterRepository extends JpaRepository<TaskMaster, Long> {

    Optional<TaskMaster> findByBaseServiceIdAndTaskIdAndTenantId(Integer baseServiceId, String taskId, String tenantId);

	void deleteByServiceIdAndTenantId(Integer serviceId, String tenantId);
}
