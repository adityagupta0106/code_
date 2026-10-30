package com.serviceplus.metadata.aadhaarConfiguration.repository;

import com.serviceplus.metadata.aadhaarConfiguration.entity.AuaApiFieldMappingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAuaApiFieldMappingHistoryRepository extends JpaRepository<AuaApiFieldMappingHistory, Long> {

    List<AuaApiFieldMappingHistory> findByModificationId(Long modificationId);

    List<AuaApiFieldMappingHistory> findByServiceId(Integer serviceId);
}
