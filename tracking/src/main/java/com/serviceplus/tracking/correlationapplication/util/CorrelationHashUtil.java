package com.serviceplus.tracking.correlationapplication.util;

import java.util.List;
import java.util.stream.Collectors;

public final class CorrelationHashUtil {

	private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
	private static final long FNV_PRIME = 0x100000001b3L;

	private CorrelationHashUtil() {
	}

	public static long computeAttrHash(List<String> attrValues) {

		String canonical = attrValues.stream().map(v -> v == null ? "" : v.trim())
				.collect(Collectors.joining("|"));

		long hash = FNV_OFFSET_BASIS;

		for (int i = 0; i < canonical.length(); i++) {
			hash ^= canonical.charAt(i);
			hash *= FNV_PRIME;
		}

		return hash;
	}
}