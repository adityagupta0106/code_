package com.serviceplus.tracking.correlationapplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrFieldsetData;

@Repository
public interface ApplicationAttrFieldsetDataRepository extends JpaRepository<ApplicationAttrFieldsetData, Long> {

	void deleteByApplicationIdAndSetIdAndOutputSetId(String applicationId, Long setId, String outputSetId);

	List<ApplicationAttrFieldsetData> findByApplicationIdAndSetIdAndOutputSetIdOrderByRowNoAsc(String applicationId,
			Long setId, String outputSetId);

	List<ApplicationAttrFieldsetData> findByApplicationIdAndSetIdAndOutputSetId(String applicationId, Long setId,
			String outputSetId);
}