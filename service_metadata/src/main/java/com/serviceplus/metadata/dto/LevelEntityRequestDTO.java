package com.serviceplus.metadata.dto;

import java.util.List;

public class LevelEntityRequestDTO {

	private Integer levelCode;
	private Integer entityCode;
	private Long clc;
	
	private Long parentUnitCode;
	List<UnitRequestDTO> unitId;
	private Long locationCode;
	
	public Integer getLevelCode() {
		return levelCode;
	}
	public void setLevelCode(Integer levelCode) {
		this.levelCode = levelCode;
	}
	public Integer getEntityCode() {
		return entityCode;
	}
	public void setEntityCode(Integer entityCode) {
		this.entityCode = entityCode;
	}
	public Long getClc() {
		return clc;
	}
	public void setClc(Long clc) {
		this.clc = clc;
	}
	public Long getParentUnitCode() {
		return parentUnitCode;
	}
	public void setParentUnitCode(Long parentUnitCode) {
		this.parentUnitCode = parentUnitCode;
	}
	public List<UnitRequestDTO> getUnitId() {
		return unitId;
	}
	public void setUnitId(List<UnitRequestDTO> unitId) {
		this.unitId = unitId;
	}
	public Long getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(Long locationCode) {
		this.locationCode = locationCode;
	}
	
	
	
	
}

