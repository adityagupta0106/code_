package com.serviceplus.tracking.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.ServiceFilterOutputData;

@Repository
public interface ServiceFilterOutputDataRepository extends JpaRepository<ServiceFilterOutputData, Long> {
	List<ServiceFilterOutputData> findByApplicationId(String applicationId);
    List<ServiceFilterOutputData> findAll(Specification<ServiceFilterOutputData> spec);

}