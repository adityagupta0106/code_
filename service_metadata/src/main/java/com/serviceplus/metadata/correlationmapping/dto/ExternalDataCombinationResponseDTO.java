package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

public class ExternalDataCombinationResponseDTO {

    private Long id;
    private String combinationName;
    private Boolean searchOnDataFound;
    private List<CombinationSetDTO> sets = new ArrayList<>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCombinationName() {
		return combinationName;
	}
	public void setCombinationName(String combinationName) {
		this.combinationName = combinationName;
	}
	public Boolean getSearchOnDataFound() {
		return searchOnDataFound;
	}
	public void setSearchOnDataFound(Boolean searchOnDataFound) {
		this.searchOnDataFound = searchOnDataFound;
	}
	public List<CombinationSetDTO> getSets() {
		return sets;
	}
	public void setSets(List<CombinationSetDTO> sets) {
		this.sets = sets;
	}

    
}
