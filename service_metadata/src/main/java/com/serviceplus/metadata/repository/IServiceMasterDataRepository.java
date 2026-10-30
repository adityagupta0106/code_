package com.serviceplus.metadata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceplus.metadata.entity.ServiceMasterData;

public interface IServiceMasterDataRepository extends JpaRepository<ServiceMasterData, Object> {
	Optional<ServiceMasterData> findByName(String name);

}
