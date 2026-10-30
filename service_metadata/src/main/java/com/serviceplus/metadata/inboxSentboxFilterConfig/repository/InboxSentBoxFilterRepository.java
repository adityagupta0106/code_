package com.serviceplus.metadata.inboxSentboxFilterConfig.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.serviceplus.metadata.inboxSentboxFilterConfig.entity.InboxSentBoxFilter;

public interface InboxSentBoxFilterRepository extends JpaRepository<InboxSentBoxFilter, Long>{

	public Optional<InboxSentBoxFilter> findByIdAndUserId(Long id, Long userId);
    public boolean existsByFilterName(String filterName);
    public boolean existsByFilterNameAndIdNot(String filterName, Long id);

	@Query(value = """
			SELECT EXISTS (
			    SELECT 1
			    FROM schm_sp.service_filter sf
			    CROSS JOIN LATERAL jsonb_array_elements(sf.tasks_json) AS task
			    WHERE sf.service_id = :serviceId
			      AND task->>'value' = :taskId
			)
			""", nativeQuery = true)
	boolean existsByServiceIdAndTaskId(@Param("serviceId") Long serviceId, @Param("taskId") String taskId);

	@Query(value = """
			SELECT EXISTS (
			    SELECT 1
			    FROM schm_sp.service_filter sf
			    CROSS JOIN LATERAL jsonb_array_elements(sf.tasks_json) AS task
			    WHERE sf.service_id = :serviceId
			      AND task->>'value' = :taskId
			      AND sf.id <> :id
			)
			""", nativeQuery = true)
	boolean existsByServiceIdAndTaskIdAndIdNot(@Param("serviceId") Long serviceId, @Param("taskId") String taskId, @Param("id") Long id);
	
	public Page<InboxSentBoxFilter> findByUserId(Long userId, Pageable pageable);
	public List<InboxSentBoxFilter> findByServiceIdAndStatus(Long serviceId, Integer status);
	public List<InboxSentBoxFilter> findByServiceId(Long serviceId);
	public void deleteByServiceId(Long serviceId);

}
