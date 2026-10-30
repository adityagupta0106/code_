package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceTemplateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceTemplateHistoryRepository extends JpaRepository<ServiceTemplateHistory, Long> {

    List<ServiceTemplateHistory> findByModificationId(Long modificationId);

    List<ServiceTemplateHistory> findByServiceId(Integer serviceId);
}
