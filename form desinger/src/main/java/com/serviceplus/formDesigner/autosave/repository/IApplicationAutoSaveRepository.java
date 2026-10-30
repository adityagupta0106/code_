package com.serviceplus.formDesigner.autosave.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.formDesigner.autosave.model.ApplicationAutoSave;

@Repository
public interface IApplicationAutoSaveRepository extends MongoRepository<ApplicationAutoSave, String> {
	public Optional<ApplicationAutoSave> findByUserIDAndServiceIdAndTaskIdAndFormIdAndApplicationId(Long userID, Integer serviceId,
			String formId, String taskId, String applicationId);
	public Optional<ApplicationAutoSave> findByUserIDAndServiceIdAndTaskIdAndFormId(Long userID, Integer serviceId, String formId, String taskId);

}
