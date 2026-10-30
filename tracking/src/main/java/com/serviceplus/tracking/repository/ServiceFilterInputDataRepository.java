package com.serviceplus.tracking.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.ServiceFilterInputData;

@Repository
public interface ServiceFilterInputDataRepository extends JpaRepository<ServiceFilterInputData, Long> {

	List<ServiceFilterInputData> findByApplicationIdAndServiceIdAndFilterId(String applicationId, Long serviceId ,Long filterId);

	Page<ServiceFilterInputData> findAll(Specification<ServiceFilterInputData> spec, Pageable pageable);

}