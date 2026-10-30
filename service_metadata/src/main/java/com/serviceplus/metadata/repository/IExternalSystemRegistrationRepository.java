package com.serviceplus.metadata.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ExternalSystemRegistration;

@Repository
public interface IExternalSystemRegistrationRepository extends JpaRepository<ExternalSystemRegistration, Long> {

	public Optional<ExternalSystemRegistration> findByClientId(String clientId);
	public Optional<ExternalSystemRegistration> findByClientName(String clientName);
	public List<ExternalSystemRegistration> findByDepartmentIdAndLocationId(Integer departmentId, Integer locationId);
	public List<ExternalSystemRegistration> findByDepartmentIdAndStatusIn(Integer departmentId, Collection<String> status);
}
