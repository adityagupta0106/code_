package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceChargeDetailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceChargeDetailHistoryRepository extends JpaRepository<ServiceChargeDetailHistory, Long> {

    List<ServiceChargeDetailHistory> findByModificationId(Long modificationId);

    List<ServiceChargeDetailHistory> findByServiceId(Integer serviceId);
}
