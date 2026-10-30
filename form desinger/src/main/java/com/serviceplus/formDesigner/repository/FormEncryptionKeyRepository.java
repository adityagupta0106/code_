package com.serviceplus.formDesigner.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.FormEncryptionKey;

@Repository
public interface FormEncryptionKeyRepository extends MongoRepository<FormEncryptionKey, String> {

	public Optional<FormEncryptionKey> findByFormIdAndServiceIdAndTaskId(String formId,Integer serviceId, String taskId);
}
