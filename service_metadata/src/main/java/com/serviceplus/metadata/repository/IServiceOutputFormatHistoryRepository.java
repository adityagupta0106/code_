package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceOutputFormatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceOutputFormatHistoryRepository extends JpaRepository<ServiceOutputFormatHistory, Long> {

    List<ServiceOutputFormatHistory> findByModificationId(Long modificationId);

    List<ServiceOutputFormatHistory> findByServiceId(Integer serviceId);
}
