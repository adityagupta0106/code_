package com.serviceplus.metadata.aadhaarConfiguration.repository;

import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuaApiFieldMappingRepository extends JpaRepository<AuaApiFieldMapping, Long> {

    Optional<AuaApiFieldMapping> findByTenantIdAndServiceIdAndConfigurationIdAndApiTypeAndNodeCode(String tenantId, Integer serviceId, String configurationId, String apiType, String nodeCode);

    Optional<AuaApiFieldMapping> findByTenantIdAndServiceIdAndConfigurationIdAndApiTypeAndNodeCodeAndMessageType(String tenantId, Integer serviceId, String configurationId, String apiType, String nodeCode, String name);

    List<AuaApiFieldMapping> findByTenantIdAndServiceIdAndConfigurationIdAndApiType(String tenantId, Integer serviceId, String configurationId, String apiType);

    List<AuaApiFieldMapping> findByTenantIdAndServiceIdAndConfigurationId(String tenantId, Integer serviceId, String configurationId);

    List<AuaApiFieldMapping> findByTenantIdAndServiceId(String tenantId, Integer serviceId);

    @Query("""
            SELECT a.configurationId
            FROM AuaApiFieldMapping a
            WHERE a.serviceId = :serviceId
            GROUP BY a.configurationId
            ORDER BY MAX(a.id) DESC
        """)
    List<String> findConfigurationIdsByServiceIdOrderByLatest(@Param("serviceId") Integer serviceId);

    List<AuaApiFieldMapping> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
