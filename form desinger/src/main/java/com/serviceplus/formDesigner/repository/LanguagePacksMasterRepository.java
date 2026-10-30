package com.serviceplus.formDesigner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.LanguagePacksMaster;

@Repository
public interface LanguagePacksMasterRepository extends MongoRepository<LanguagePacksMaster, String> {

}
