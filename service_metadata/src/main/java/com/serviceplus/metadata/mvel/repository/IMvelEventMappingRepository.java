package com.serviceplus.metadata.mvel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.mvel.entity.MvelEventMapping;

@Repository
public interface IMvelEventMappingRepository extends JpaRepository<MvelEventMapping, Integer> {
	public MvelEventMapping findByEventIdAndServiceId(Long eventId,Integer serviceId);
	public List<MvelEventMapping> findByServiceId(Integer serviceId);

	void deleteByServiceId(Integer serviceId);
}
