package com.serviceplus.tracking.dto;

import java.time.LocalDate;
import java.util.List;

public class ApplicationTrackingFilter {

    private LocalDate fromDate;

    private LocalDate toDate;

    private List<String> applicationRefNo;

    private String searchKeyword;

    private String category;

    private Integer page = 0;

    private Integer size = 10;

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public List<String> getApplicationRefNo() {
        return applicationRefNo;
    }

    public void setApplicationRefNo(List<String> applicationRefNo) {
        this.applicationRefNo = applicationRefNo;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}