package com.serviceplus.tracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;

@Repository
public interface InboxSentBoxFilterConfigRepository extends JpaRepository<InboxSentBoxFilterConfig, Long> {
	List<InboxSentBoxFilterConfig> findByServiceIdAndFilterId(Long serviceId, Long filterId);
	List<InboxSentBoxFilterConfig> findByServiceIdAndAttrType(Long serviceId, Character attrType);
	List<InboxSentBoxFilterConfig> findByServiceIdAndTaskIdAndAttrTypeAndFilterTypeIn(Long serviceId,String taskId, Character attrType, List<Character> filterTypes);

	List<InboxSentBoxFilterConfig> findByServiceIdAndTaskIdAndAttrTypeAndFilterTypeInOrderByAttrOrder(Long serviceId,
			String taskId, Character attrType, List<Character> filterTypes);
}