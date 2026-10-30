package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceProcessFlowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceProcessFlowHistoryRepository extends JpaRepository<ServiceProcessFlowHistory, Long> {

    List<ServiceProcessFlowHistory> findByModificationId(Long modificationId);

    List<ServiceProcessFlowHistory> findByServiceId(Integer serviceId);
}
