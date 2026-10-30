package com.serviceplus.tracking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.repository.InboxSentBoxFilterConfigRepository;

@Service
public class InboxSentBoxFilterConfigService {

	private final InboxSentBoxFilterConfigRepository repository;

	public InboxSentBoxFilterConfigService(InboxSentBoxFilterConfigRepository repository) {
		this.repository = repository;
	}

	public InboxSentBoxFilterConfig save(InboxSentBoxFilterConfig filterConfig) {

		return repository.save(filterConfig);
	}

	public List<InboxSentBoxFilterConfig> saveAll(List<InboxSentBoxFilterConfig> filterConfigs) {

		return repository.saveAll(filterConfigs);
	}

	public List<InboxSentBoxFilterConfig> findByServiceIdAndFilterId(Long serviceId, Long filterId) {

		return repository.findByServiceIdAndFilterId(serviceId, filterId);
	}
	
	public List<InboxSentBoxFilterConfig> findByServiceIdAndAttrType(Long serviceId,  Character attrType) {

		return repository.findByServiceIdAndAttrType(serviceId, attrType);
	}

}