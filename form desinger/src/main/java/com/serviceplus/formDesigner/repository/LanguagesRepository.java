package com.serviceplus.formDesigner.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.Languages;

@Repository
public interface LanguagesRepository extends MongoRepository<Languages, String> {
	List<Languages> findByIsActive(Boolean isActive);

}
