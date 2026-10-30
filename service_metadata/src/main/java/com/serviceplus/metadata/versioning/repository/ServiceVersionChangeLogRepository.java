package com.serviceplus.metadata.versioning.repository;

import com.serviceplus.metadata.versioning.entity.ServiceVersionChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceVersionChangeLogRepository extends JpaRepository<ServiceVersionChangeLog, Long> {

    List<ServiceVersionChangeLog> findByServiceIdOrderByCreatedAtDesc(Integer serviceId);

    List<ServiceVersionChangeLog> findByVersionChangeIdOrderByCreatedAtDesc(Long versionChangeId);

    List<ServiceVersionChangeLog> findByModificationTokenOrderByCreatedAtDesc(String modificationToken);
}
