package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.entity.ModuleFileMetadata;
import com.serviceplus.metadata.entity.ModuleFileMetadata.ModuleFileMetadataId;

@Repository
public interface IModuleFileMetadataRepository extends JpaRepository<ModuleFileMetadata, ModuleFileMetadataId> {

	Optional<ModuleFileMetadata> findByIdModuleIdAndIdReferenceId(Long moduleId, String referenceId);

	List<ModuleFileMetadata> findByIdModuleId(Long moduleId);
}