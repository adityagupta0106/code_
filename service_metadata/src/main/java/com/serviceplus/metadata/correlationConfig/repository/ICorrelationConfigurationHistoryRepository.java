package com.serviceplus.metadata.correlationConfig.repository;

import com.serviceplus.metadata.correlationConfig.entiry.CorrelationConfigurationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICorrelationConfigurationHistoryRepository extends JpaRepository<CorrelationConfigurationHistory, Long> {

    List<CorrelationConfigurationHistory> findByModificationId(Long modificationId);

    List<CorrelationConfigurationHistory> findByServiceId(Integer serviceId);
}
