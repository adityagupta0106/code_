package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceDeliveryUnitsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceDeliveryUnitsHistoryRepository extends JpaRepository<ServiceDeliveryUnitsHistory, Long> {

    List<ServiceDeliveryUnitsHistory> findByModificationId(Long modificationId);

    List<ServiceDeliveryUnitsHistory> findByServiceId(Integer serviceId);
}
