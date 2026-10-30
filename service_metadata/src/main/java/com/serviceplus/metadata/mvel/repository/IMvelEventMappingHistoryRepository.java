package com.serviceplus.metadata.mvel.repository;

import com.serviceplus.metadata.mvel.entity.MvelEventMappingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMvelEventMappingHistoryRepository extends JpaRepository<MvelEventMappingHistory, Long> {

    List<MvelEventMappingHistory> findByModificationId(Long modificationId);

    List<MvelEventMappingHistory> findByServiceId(Integer serviceId);
}
