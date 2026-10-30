package com.serviceplus.metadata.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.serviceplus.metadata.dto.DesignationDTO;
import com.serviceplus.metadata.dto.DesignationRequestDTO;
import com.serviceplus.metadata.dto.HierarchyDetailsDTO;
import com.serviceplus.metadata.dto.LevelEntityRequestDTO;
import com.serviceplus.metadata.dto.LevelWiseUnitsDTO;
import com.serviceplus.metadata.dto.LgdUnitDTO;
import com.serviceplus.metadata.dto.UnitRequestDTO;

@FeignClient(name = "ServiceplusLgd",path = "/splgd")
public interface LgdFeignClient {
	@GetMapping("/lgd/getOrgUnitsAtSpecificLevel")
	public ResponseEntity<?> getSubordinateOrgUnits(@RequestParam Integer orgUnitCode,@RequestParam Integer orgLocatedLevelCode);
	
	@GetMapping("/lgd/batch/findAllOfficeLocation")
	public ResponseEntity<?> findAllOfficeLocation(@RequestParam Integer lType, @RequestParam Integer stateID,
															@RequestParam Integer subOrgCode);
	@GetMapping("/lgd/getOrgUnitListByLevel")
	public ResponseEntity<?> getOrgUnitListByLevel(@RequestParam Integer levelId);
	
	@PostMapping("/spgd/levelWiseUnits")
    List<LevelWiseUnitsDTO> getLevelWiseUnits(@RequestBody LevelEntityRequestDTO request);
	
	@PostMapping("/spgd/designationName")
    ResponseEntity<DesignationDTO> getDesignationName(@RequestBody DesignationRequestDTO request);
	
	@GetMapping("/spgd/levelLowerHierarchy")
	ResponseEntity<List<HierarchyDetailsDTO>> getLevelLowerHierarchy(@RequestParam Integer levelCode,
			@RequestParam Integer entityCode,
			@RequestParam(required = false) Long unitCode);

	@GetMapping("/spgd/hierarchy/related-units")
	ResponseEntity<List<LgdUnitDTO>> getRelatedUnits(
			@RequestParam Integer sourceLevelCode,
			@RequestParam Long sourceUnitCode,
			@RequestParam Integer destLevelCode);

	@GetMapping("/spgd/unitsByLevel")
	ResponseEntity<List<LgdUnitDTO>> getUnitsByLevel(
			@RequestParam Integer levelCode,
			@RequestParam(required = false) Long entityCode);
	
	@PostMapping("/spgd/unitName")
	ResponseEntity<String> getUnitName(@RequestBody UnitRequestDTO unit);
}
