package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceDefinitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceDefinitionHistoryRepository extends JpaRepository<ServiceDefinitionHistory, Long> {

    List<ServiceDefinitionHistory> findByModificationId(Long modificationId);

    List<ServiceDefinitionHistory> findByServiceId(Integer serviceId);
}
