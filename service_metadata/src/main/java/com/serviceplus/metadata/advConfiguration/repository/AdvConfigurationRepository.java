package com.serviceplus.metadata.advConfiguration.repository;

import com.serviceplus.metadata.advConfiguration.entity.AdvConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvConfigurationRepository extends JpaRepository<AdvConfiguration, Long> {

    List<AdvConfiguration> findByBaseServiceId(Integer baseServiceId);

    List<AdvConfiguration> findByServiceId(Integer serviceId);

    Optional<AdvConfiguration> findFirstByServiceId(Integer serviceId);

    Optional<AdvConfiguration> findFirstByBaseServiceId(Integer baseServiceId);

    Optional<AdvConfiguration> findByServiceIdAndBaseServiceId(Integer serviceId, Integer baseServiceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AdvConfiguration c WHERE c.serviceId = :serviceId AND c.cByUserId = :userId")
    int deleteByServiceIdAndUserId(@Param("serviceId") Integer serviceId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AdvConfiguration c WHERE c.serviceId = :serviceId")
    int deleteByServiceId(@Param("serviceId") Integer serviceId);
}
