package com.serviceplus.metadata.utility;

import java.util.List;

public class MvelSanitizer {

    private static final List<String> BLACKLIST = List.of(
            "System", "Runtime", "ProcessBuilder",
            "File", "FileInputStream", "FileOutputStream",
            "Class", "ClassLoader", "forName",
            "Thread", "sleep", "while(true)", "for(;;)",
            "exec", "getRuntime"
    );

    public static void validate(String functionBody) {

        if (functionBody == null || functionBody.trim().isEmpty()) {
            throw new RuntimeException("Function body cannot be empty");
        }

        String normalized = functionBody.replaceAll("\\s+", "").toLowerCase();

        for (String keyword : BLACKLIST) {
            if (normalized.contains(keyword.toLowerCase())) {
                throw new RuntimeException("Unsafe keyword detected: " + keyword);
            }
        }
    }
}
