package com.serviceplus.metadata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceplus.metadata.entity.SystemVariableMaster;

public interface ISystemVariableRepository extends JpaRepository<SystemVariableMaster, String> {
	
	List<SystemVariableMaster> findByModuleIdOrModuleIdIsNull(Long moduleId);

}
