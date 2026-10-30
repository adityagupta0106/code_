package com.serviceplus.metadata.pattern.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.pattern.entity.PatternConfiguration;

@Repository
public interface PatternConfigurationRepository extends JpaRepository<PatternConfiguration, Long> {

	boolean existsByPatternNameAndCreatedBy(String patternName, Long createdBy);

	boolean existsByPatternNameAndCreatedByAndIdNot(String patternName, Long createdBy, Long id);

	List<PatternConfiguration> findByCreatedBy(Long createdBy);

	Page<PatternConfiguration> findByCreatedBy(Long createdBy, Pageable pageable);

	List<PatternConfiguration> findByEntityId(Integer entityId);
}
