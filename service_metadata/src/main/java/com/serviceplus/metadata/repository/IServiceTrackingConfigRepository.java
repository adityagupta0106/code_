package com.serviceplus.metadata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.serviceplus.metadata.entity.ServiceTrackingConfig;

@Repository
public interface IServiceTrackingConfigRepository extends JpaRepository<ServiceTrackingConfig, Long> {

    ServiceTrackingConfig findByServiceId(Long serviceId);
    
    @Query(value = """
            SELECT
                tc.service_id AS "serviceId",
                sd.service_name AS "serviceName"
            FROM schm_sp.tracking_configuration tc
            JOIN schm_sp.service_definition sd
                ON tc.service_id = sd.service_id
            JOIN schm_sp.service_log sl
                ON tc.service_id = sl.service_id
            WHERE sl.tenant_id = :tenantId
            ORDER BY sd.service_name
            """, nativeQuery = true)
    List<Object[]>  findConfiguredServices( @Param("tenantId") String tenantId);
}