package com.serviceplus.formDesigner.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.ApplicationData;
import com.serviceplus.formDesigner.request.model.ApplicationDataStandalone;

@Repository
public interface ApplicationDataStandaloneRepository extends MongoRepository<ApplicationDataStandalone, String> {
	
	Page<ApplicationData> findByHolderId(String holderId, Pageable pageable);
	Page<ApplicationData> findByHolderIdAndFormId(String holderId, String formId, Pageable pageable);
	void deleteByFormId(String formId);
}
