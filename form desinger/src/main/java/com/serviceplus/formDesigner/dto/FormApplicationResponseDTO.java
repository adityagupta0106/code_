package com.serviceplus.formDesigner.dto;

import java.util.List;

public class FormApplicationResponseDTO {

    private List<Object> header;

    private List<Object> body;

    private PaginationDTO pagination;
    
    public static class PaginationDTO {

        private Integer page;

        private Integer size;

        private Long totalElements;

        private Integer totalPages;

        private Boolean first;

        private Boolean last;

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

		public Long getTotalElements() {
			return totalElements;
		}

		public void setTotalElements(Long totalElements) {
			this.totalElements = totalElements;
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

        // getters/setters
    }

	public List<Object> getHeader() {
		return header;
	}

	public void setHeader(List<Object> header) {
		this.header = header;
	}

	public List<Object> getBody() {
		return body;
	}

	public void setBody(List<Object> body) {
		this.body = body;
	}

	public PaginationDTO getPagination() {
		return pagination;
	}

	public void setPagination(PaginationDTO pagination) {
		this.pagination = pagination;
	}

}