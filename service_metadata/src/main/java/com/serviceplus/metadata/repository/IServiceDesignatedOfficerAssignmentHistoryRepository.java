package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IServiceDesignatedOfficerAssignmentHistoryRepository extends JpaRepository<ServiceDesignatedOfficerAssignmentHistory, Long> {

    List<ServiceDesignatedOfficerAssignmentHistory> findByModificationId(Long modificationId);

    List<ServiceDesignatedOfficerAssignmentHistory> findByServiceId(Integer serviceId);
}
