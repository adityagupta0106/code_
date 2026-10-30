package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ServiceSubmissionLocationAdoption;

@Repository
public interface IServiceSubmissionLocationAdoptionRepository extends JpaRepository<ServiceSubmissionLocationAdoption, Long> {

    Optional<ServiceSubmissionLocationAdoption> findByTenantIdAndServiceIdAndSduLocationIdAndSubmissionLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer sduLocationId, Integer submissionLocationId);

    Optional<ServiceSubmissionLocationAdoption> findByTenantIdAndServiceIdAndSubmissionLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer submissionLocationId);

    List<ServiceSubmissionLocationAdoption> findByTenantIdAndServiceIdAndSduLocationIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer sduLocationId);

    List<ServiceSubmissionLocationAdoption> findByTenantIdAndServiceIdAndDeletedFlagFalse(
            String tenantId, Integer serviceId);

    List<ServiceSubmissionLocationAdoption> findByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer sduLocationId, String adoptionStatus);

    long countByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer sduLocationId, String adoptionStatus);

    boolean existsByTenantIdAndServiceIdAndSduLocationIdAndAdoptionStatusAndDeletedFlagFalse(
            String tenantId, Integer serviceId, Integer sduLocationId, String adoptionStatus);

    @Query("""
            select a from ServiceSubmissionLocationAdoption a
            where a.tenantId = :tenantId
              and a.serviceId = :serviceId
              and a.submissionLocationId in :submissionLocationIds
              and a.deletedFlag = false
            """)
    List<ServiceSubmissionLocationAdoption> findByLocationIds(
            @Param("tenantId") String tenantId,
            @Param("serviceId") Integer serviceId,
            @Param("submissionLocationIds") List<Integer> submissionLocationIds);

    List<ServiceSubmissionLocationAdoption> findByServiceId(Integer serviceId);

    void deleteByServiceId(Integer serviceId);
}
