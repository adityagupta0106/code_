package com.serviceplus.metadata.inboxSentboxFilterConfig.repository;

import com.serviceplus.metadata.inboxSentboxFilterConfig.entity.ServiceFilterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IServiceFilterHistoryRepository extends JpaRepository<ServiceFilterHistory, Long> {

    List<ServiceFilterHistory> findByModificationId(Long modificationId);

    List<ServiceFilterHistory> findByServiceId(Long serviceId);
}
