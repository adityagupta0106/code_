package com.serviceplus.metadata.pattern.dto;

public class PatternTemplateSummaryDTO {

    private Long id;
    private String patternName;
    private String rawPattern;

    public PatternTemplateSummaryDTO() {
    }

    public PatternTemplateSummaryDTO(Long id, String patternName, String rawPattern) {
        this.id = id;
        this.patternName = patternName;
        this.rawPattern = rawPattern;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public String getName() {
        return patternName;
    }

    public void setName(String name) {
        this.patternName = name;
    }

    public String getRawPattern() {
        return rawPattern;
    }

    public void setRawPattern(String rawPattern) {
        this.rawPattern = rawPattern;
    }
}
