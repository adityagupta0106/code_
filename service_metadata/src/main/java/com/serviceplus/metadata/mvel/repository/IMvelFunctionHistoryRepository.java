package com.serviceplus.metadata.mvel.repository;

import com.serviceplus.metadata.mvel.entity.MvelFunctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMvelFunctionHistoryRepository extends JpaRepository<MvelFunctionHistory, Long> {

    List<MvelFunctionHistory> findByModificationId(Long modificationId);

    List<MvelFunctionHistory> findByServiceId(Integer serviceId);
}
