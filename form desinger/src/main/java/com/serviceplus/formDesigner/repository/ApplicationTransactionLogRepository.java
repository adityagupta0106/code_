package com.serviceplus.formDesigner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.ApplicationTransactionLog;

@Repository
public interface ApplicationTransactionLogRepository extends MongoRepository<ApplicationTransactionLog, String> {

	public ApplicationTransactionLog findByTxnIdAndFormId(String txnId, String formId);

	public ApplicationTransactionLog findByTxnId(String txnId);

	public ApplicationTransactionLog findByTxnIdAndFormIdAndDataId(String txnId, String formId,
			String dataId);
}
