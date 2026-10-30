package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ExternalDataCombinationRequestDTO {

    private Long id;

    @NotBlank(message = "combinationName can't be blank")
    private String combinationName;

    @NotNull(message = "searchOnDataFound can't be null")
    private Boolean searchOnDataFound;

    @NotNull(message = "sets can't be null")
    @NotEmpty(message = "sets can't be empty")
    @Valid
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
