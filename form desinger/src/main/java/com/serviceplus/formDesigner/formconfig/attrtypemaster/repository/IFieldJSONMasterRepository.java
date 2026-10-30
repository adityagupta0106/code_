package com.serviceplus.formDesigner.formconfig.attrtypemaster.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldJSONMaster;

@Repository
public interface IFieldJSONMasterRepository extends MongoRepository<FieldJSONMaster, String> {

	public FieldJSONMaster findByTypeIdAndTenantId(Integer typeId,String tenantId);
	public List<FieldJSONMaster> findByTenantId(String tenantId);
	public List<FieldJSONMaster> findByTypeId(Integer typeId);
}
