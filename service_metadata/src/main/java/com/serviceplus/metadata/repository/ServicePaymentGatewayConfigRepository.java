package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServicePaymentGatewayConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePaymentGatewayConfigRepository extends JpaRepository<ServicePaymentGatewayConfig, Long> {

    List<ServicePaymentGatewayConfig> findByServiceIdAndTenantId(Integer serviceId, String tenantId);

    Optional<ServicePaymentGatewayConfig> findByServiceIdAndGatewayCodeAndTenantId(Integer serviceId, String gatewayCode, String tenantId);

    Optional<ServicePaymentGatewayConfig> findByServiceIdAndCreatedByAndGatewayCodeAndTenantId(Integer serviceId, Long userID, String gatewayCode, String tenantId);

    List<ServicePaymentGatewayConfig> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
