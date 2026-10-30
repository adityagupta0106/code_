package com.serviceplus.formDesigner.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.request.model.UserFormEncryptionKey;

@Repository
public interface UserFormEncryptionKeyRepository extends MongoRepository<UserFormEncryptionKey, String> {
	public Optional<UserFormEncryptionKey> findByUserIdAndServiceIdAndTaskId(Long userId,Integer serviceId, String taskId);
	public Optional<UserFormEncryptionKey> findByServiceIdAndTaskId(Integer serviceId, String taskId);

}
