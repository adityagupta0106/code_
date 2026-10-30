package com.serviceplus.metadata.pattern.dto;

import java.util.List;

public class PatternTemplatePageDTO {

	private List<PatternTemplateSummaryDTO> records;
	private Integer page;
	private Integer size;
	private Long totalRecords;
	private Integer totalPages;
	private Boolean first;
	private Boolean last;

	public PatternTemplatePageDTO() {
	}

	public PatternTemplatePageDTO(List<PatternTemplateSummaryDTO> records) {
	    this.records = records;
	}
	
	public PatternTemplatePageDTO(List<PatternTemplateSummaryDTO> records, Integer page, Integer size,
			Long totalRecords, Integer totalPages, Boolean first, Boolean last) {
		this.records = records;
		this.page = page;
		this.size = size;
		this.totalRecords = totalRecords;
		this.totalPages = totalPages;
		this.first = first;
		this.last = last;
	}

	public List<PatternTemplateSummaryDTO> getRecords() {
	    return records;
	}

	public void setRecords(List<PatternTemplateSummaryDTO> records) {
	    this.records = records;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Long getTotalElements() {
		return totalRecords;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Boolean getFirst() {
		return first;
	}

	public void setFirst(Boolean first) {
		this.first = first;
	}

	public Boolean getLast() {
		return last;
	}

	public void setLast(Boolean last) {
		this.last = last;
	}
}
