package com.serviceplus.metadata.formSubscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.formSubscription.entity.FormPublishLog;

@Repository
public interface IFormPublishLogRepository extends JpaRepository<FormPublishLog, Long> {

	public Optional<FormPublishLog> findByUserIdAndServiceIdAndTaskIdAndFormIdAndPublishFormIdAndMode(
			Long userId, Integer serviceId, String taskId, String formId, String publishFormId, String mode);

	public List<FormPublishLog> findByUserIdAndModeAndApply(Long userId, String mode, Boolean apply);

	@Query("""
			SELECT f
			FROM FormPublishLog f
			WHERE f.userId = :userId
			AND f.serviceId = :serviceId
			AND f.mode = :mode
			AND f.apply is null
			ORDER BY f.taskId ASC, f.publishVersion DESC
			""")
	List<FormPublishLog> findLogs(@Param("userId") Long userId, @Param("serviceId") Integer serviceId,
			@Param("mode") String mode);

}
