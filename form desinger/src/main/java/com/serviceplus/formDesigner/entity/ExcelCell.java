package com.serviceplus.formDesigner.entity;

import java.util.List;

import com.serviceplus.formDesigner.dto.FormProcessCacheDTO.LabelValue;

public class ExcelCell {
    private Object value;
    private int cellType;
    private int inputType;
    private List<LabelValue> optionsVal;

    public ExcelCell(Object value, int cellType, Integer inputType) {
        this.value = value;
        this.cellType = cellType;
        this.inputType = inputType;
    }

    public Object getValue() {
        return value;
    }

    public int getCellType() {
        return cellType;
    }

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}

	public List<LabelValue> getOptionsVal() {
		return optionsVal;
	}

	public void setOptionsVal(List<LabelValue> optionsVal) {
		this.optionsVal = optionsVal;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setCellType(int cellType) {
		this.cellType = cellType;
	}
    
}
