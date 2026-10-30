package com.serviceplus.tracking.correlationapplication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrIdentifierData;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrIdentifierData.ApplicationAttrIdentifierDataId;

@Repository
public interface ApplicationAttrIdentifierDataRepository
		extends JpaRepository<ApplicationAttrIdentifierData, ApplicationAttrIdentifierDataId> {

	Optional<ApplicationAttrIdentifierData> findByApplicationIdAndSetId(String applicationId, Long setId);

	List<ApplicationAttrIdentifierData> findByBaseServiceIdAndSetIdAndAttrHash(Long baseServiceId, Long setId,
			Long attrHash);

}