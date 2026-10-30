package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServicePaymentGatewayConfigHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServicePaymentGatewayConfigHistoryRepository extends JpaRepository<ServicePaymentGatewayConfigHistory, Long> {

    List<ServicePaymentGatewayConfigHistory> findByModificationId(Long modificationId);

    List<ServicePaymentGatewayConfigHistory> findByServiceId(Integer serviceId);
}
