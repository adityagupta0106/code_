package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceLogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceLogHistoryRepository extends JpaRepository<ServiceLogHistory, Long> {

    List<ServiceLogHistory> findByModificationId(Long modificationId);

    List<ServiceLogHistory> findByServiceId(Integer serviceId);
}
