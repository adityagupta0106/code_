package com.serviceplus.formDesigner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.LanguagePacks;

@Repository
public interface LanguagePacksRepository extends MongoRepository<LanguagePacks, String> {

}
