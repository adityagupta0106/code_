package com.serviceplus.formDesigner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.LanguageMaster;
@Repository
public interface LanguageMasterRepository extends MongoRepository<LanguageMaster, String> {

}
