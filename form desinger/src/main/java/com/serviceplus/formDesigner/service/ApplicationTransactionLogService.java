package com.serviceplus.formDesigner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.formDesigner.repository.ApplicationTransactionLogRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTransactionLog;

@Service
public class ApplicationTransactionLogService {

	@Autowired
	private ApplicationTransactionLogRepository applicationTransactionLogRepository;
	
	public ApplicationTransactionLog save(ApplicationTransactionLog applicationTransactionLog) {
		return applicationTransactionLogRepository.save(applicationTransactionLog);
	}
	
	public ApplicationTransactionLog findByTxnId(String txnId) {
		return applicationTransactionLogRepository.findByTxnId(txnId);
	}
	
	public ApplicationTransactionLog findByTxnIdAndFormId(String txnId, String formId) {
		return applicationTransactionLogRepository.findByTxnIdAndFormId(txnId,formId);
	}
	
	public ApplicationTransactionLog findByTxnIdAndFormIdAndDataId(String txnId, String formId,String dataId) {
		return applicationTransactionLogRepository.findByTxnIdAndFormIdAndDataId(txnId,formId,dataId);
	}
	
	
}
