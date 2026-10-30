package com.serviceplus.metadata.departmentConfig.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.departmentConfig.entity.WebServiceDetails;

@Repository
public interface WebServiceRepository extends MongoRepository<WebServiceDetails, String> {

	List<WebServiceDetails> findByTenantId(String tenantId);

	List<WebServiceDetails> findByIdAndTenantId(String id, String tenantId);

}
