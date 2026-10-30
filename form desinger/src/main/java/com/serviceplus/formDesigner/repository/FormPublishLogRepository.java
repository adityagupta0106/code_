package com.serviceplus.formDesigner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.FormPublishLog;

@Repository
public interface FormPublishLogRepository extends MongoRepository<FormPublishLog, String> {

}
