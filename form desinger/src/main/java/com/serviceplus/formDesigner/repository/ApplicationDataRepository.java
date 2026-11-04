package com.serviceplus.formDesigner.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.serviceplus.formDesigner.request.model.ApplicationData;

@Repository
public interface ApplicationDataRepository extends MongoRepository<ApplicationData, String> {

	public List<ApplicationData> findByFormId(String formId);
	Page<ApplicationData> findByHolderIdAndServiceId(String holderId, Integer serviceId, Pageable pageable);
	Page<ApplicationData> findByHolderIdAndFormIdAndServiceId(String holderId, String formId, Integer serviceId, Pageable pageable);
	public List<ApplicationData> findByUserIdAndServiceId(Long userId, Integer serviceId);
	public void deleteByFormIdAndServiceId(String formId,Integer serviceId);
	
	public List<ApplicationData> findByFormIdAndTxnId(String formId,String txnId);
	
	public ApplicationData findByDataId(String dataId);
	public ApplicationData findByDataIdAndFormId(String dataId, String formId);
	public ApplicationData findByApplIdAndTaskIdAndHolderId(String applicationId, String taskId,String holderId);
	
	

@Query("{ 'tenantId': ?0, 'serviceId': ?1, 'applId': ?2, 'draft': { '$ne': true } }")
List<ApplicationData> findTrackingApplicationData(String tenantId,Integer serviceId,String applId);

@Query("{ 'tenantId': ?0, 'serviceId': ?1, 'taskId': 's_1', 'draft': { '$ne': true }, 'applId': { '$exists': true, '$nin': [null, ''] } }")
Page<ApplicationData> findSubmittedTrackingApplications(String tenantId,Integer serviceId,Pageable pageable
);

	
	
}
