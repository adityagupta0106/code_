package com.serviceplus.tracking.correlationapplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrData;

@Repository
public interface ApplicationAttrDataRepository extends JpaRepository<ApplicationAttrData, Long> {

	List<ApplicationAttrData> findByApplicationIdAndSetIdAndOutputSetIdOrderByIdAsc(String applicationId, Long setId,
			String outputSetId);
}
