package com.serviceplus.tracking.enums;

import org.springframework.http.HttpStatus;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;

public enum FilterOperator {
	AND("AND"), OR("OR");

	private final String symbol;

	FilterOperator(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public static FilterOperator fromSymbol(String symbol) {
		for (FilterOperator op : values()) {
			if (op.symbol.equalsIgnoreCase(symbol))
				return op;
		}
		throw new SPRuntimeError("Unsupported filter condition: " + symbol, HttpStatus.BAD_REQUEST);
	}
}