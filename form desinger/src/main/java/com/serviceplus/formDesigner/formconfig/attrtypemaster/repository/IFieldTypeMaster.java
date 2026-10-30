package com.serviceplus.formDesigner.formconfig.attrtypemaster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldTypeMaster;

@Repository
public interface IFieldTypeMaster extends MongoRepository<FieldTypeMaster, String>{
	public FieldTypeMaster findByTenantId(String tenantId);
    boolean existsByTenantId(String tenantId);

}
