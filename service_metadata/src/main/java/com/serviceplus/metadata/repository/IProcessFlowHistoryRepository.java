package com.serviceplus.metadata.repository;

import com.serviceplus.metadata.entity.ProcessFlowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProcessFlowHistoryRepository extends JpaRepository<ProcessFlowHistory, Long> {

    List<ProcessFlowHistory> findByModificationId(Long modificationId);

    List<ProcessFlowHistory> findByServiceId(Integer serviceId);
}
