package com.serviceplus.formDesigner.formconfig.attrtypemaster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.RegexPatternMaster;

@Repository
public interface IRegexPatternMasterRepository extends MongoRepository<RegexPatternMaster, String> {
	
}
