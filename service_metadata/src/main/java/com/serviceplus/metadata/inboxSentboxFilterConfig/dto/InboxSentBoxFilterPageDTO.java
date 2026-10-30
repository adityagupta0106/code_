package com.serviceplus.metadata.inboxSentboxFilterConfig.dto;

import java.util.List;

public class InboxSentBoxFilterPageDTO {

    private List<InboxSentBoxFilterListDTO> records;

    private Integer page;

    private Integer size;

    private Long totalRecords;

    private Integer totalPages;

    private Boolean first;

	private Boolean last;

	public List<InboxSentBoxFilterListDTO> getRecords() {
		return records;
	}

	public void setRecords(List<InboxSentBoxFilterListDTO> records) {
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
