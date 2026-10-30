package com.serviceplus.metadata.dto;

public class LevelWiseUnitsDTO {

	private Long entityUnitCode;
    private String entityUnitName;

    private Integer parentEntityUnitCode;
    private String parentEntityUnitName;
    
	public Long getEntityUnitCode() {
		return entityUnitCode;
	}
	public void setEntityUnitCode(Long entityUnitCode) {
		this.entityUnitCode = entityUnitCode;
	}
	public String getEntityUnitName() {
		return entityUnitName;
	}
	public void setEntityUnitName(String entityUnitName) {
		this.entityUnitName = entityUnitName;
	}
	public Integer getParentEntityUnitCode() {
		return parentEntityUnitCode;
	}
	public void setParentEntityUnitCode(Integer parentEntityUnitCode) {
		this.parentEntityUnitCode = parentEntityUnitCode;
	}
	public String getParentEntityUnitName() {
		return parentEntityUnitName;
	}
	public void setParentEntityUnitName(String parentEntityUnitName) {
		this.parentEntityUnitName = parentEntityUnitName;
	}
	public LevelWiseUnitsDTO(Long entityUnitCode, String entityUnitName, Integer parentEntityUnitCode,
			String parentEntityUnitName) {
		super();
		this.entityUnitCode = entityUnitCode;
		this.entityUnitName = entityUnitName;
		this.parentEntityUnitCode = parentEntityUnitCode;
		this.parentEntityUnitName = parentEntityUnitName;
	}
	 public LevelWiseUnitsDTO() {
	    }
	
    
    
}
