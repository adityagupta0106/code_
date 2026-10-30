package com.serviceplus.metadata.dto;

public class HierarchyDetailsDTO {
	private Integer id;
    private Integer levelCode;
    private String levelSpecificName;
    private Integer entityCode;
    private Integer parentEntityCode;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getLevelCode() {
		return levelCode;
	}
	public void setLevelCode(Integer levelCode) {
		this.levelCode = levelCode;
	}
	public String getLevelSpecificName() {
		return levelSpecificName;
	}
	public void setLevelSpecificName(String levelSpecificName) {
		this.levelSpecificName = levelSpecificName;
	}
	public Integer getEntityCode() {
		return entityCode;
	}
	public void setEntityCode(Integer entityCode) {
		this.entityCode = entityCode;
	}
	public Integer getParentEntityCode() {
		return parentEntityCode;
	}
	public void setParentEntityCode(Integer parentEntityCode) {
		this.parentEntityCode = parentEntityCode;
	}
    

}
