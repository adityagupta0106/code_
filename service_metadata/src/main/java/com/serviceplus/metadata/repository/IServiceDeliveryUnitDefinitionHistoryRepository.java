package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceDeliveryUnitDefinitionHistoryRepository extends JpaRepository<ServiceDeliveryUnitDefinitionHistory, Long> {

    List<ServiceDeliveryUnitDefinitionHistory> findByModificationId(Long modificationId);

    List<ServiceDeliveryUnitDefinitionHistory> findByServiceId(Integer serviceId);
}
