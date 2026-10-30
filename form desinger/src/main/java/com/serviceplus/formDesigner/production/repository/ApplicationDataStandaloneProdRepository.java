package com.serviceplus.formDesigner.production.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.ApplicationDataStandalone;

@Repository
public interface ApplicationDataStandaloneProdRepository extends MongoRepository<ApplicationDataStandalone, String> {
	void deleteByFormId(String formId);
}
