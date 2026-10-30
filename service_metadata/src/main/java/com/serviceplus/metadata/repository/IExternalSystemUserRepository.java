package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ExternalSystemUser;

@Repository
public interface IExternalSystemUserRepository extends JpaRepository<ExternalSystemUser, Long> {

    List<ExternalSystemUser> findByExternalSystemRegId(Long externalSystemId);

    List<ExternalSystemUser> findByClientId(String clientId);

}
