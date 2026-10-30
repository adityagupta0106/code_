package com.serviceplus.formDesigner.master.document;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITenantSetupMasterRepository extends MongoRepository<TenantSetupMaster, String> {
	public Optional<TenantSetupMaster> findByType(String type);

}
