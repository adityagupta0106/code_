package com.serviceplus.metadata.departmentConfig.repository;

import com.serviceplus.metadata.departmentConfig.entity.BsaConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BsaConfigurationRepository extends JpaRepository<BsaConfiguration, Long> {

    
    List<BsaConfiguration> findByUserName(String userName);

    List<BsaConfiguration> findByActiveStatusTrue();

	Optional<BsaConfiguration> findByEntityIdAndActiveStatusTrueOrderByIdDesc(int intValue);
}
