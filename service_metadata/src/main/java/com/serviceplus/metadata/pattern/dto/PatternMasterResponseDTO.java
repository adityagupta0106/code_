package com.serviceplus.metadata.pattern.dto;

import java.util.List;

public class PatternMasterResponseDTO {

    private List<PlaceholderDTO> placeholders;
    private List<SeparatorDTO> separators;
    private List<SeriesTypeDTO> seriesTypes;
    private List<ResetAttributeDTO> resetAttributes;

    public PatternMasterResponseDTO() {
    }

    public PatternMasterResponseDTO(List<PlaceholderDTO> placeholders,
                                    List<SeparatorDTO> separators,
                                    List<SeriesTypeDTO> seriesTypes,
                                    List<ResetAttributeDTO> resetAttributes) {
        this.placeholders = placeholders;
        this.separators = separators;
        this.seriesTypes = seriesTypes;
        this.resetAttributes = resetAttributes;
    }

    public List<PlaceholderDTO> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<PlaceholderDTO> placeholders) {
        this.placeholders = placeholders;
    }

    public List<SeparatorDTO> getSeparators() {
        return separators;
    }

    public void setSeparators(List<SeparatorDTO> separators) {
        this.separators = separators;
    }

    public List<SeriesTypeDTO> getSeriesTypes() {
        return seriesTypes;
    }

    public void setSeriesTypes(List<SeriesTypeDTO> seriesTypes) {
        this.seriesTypes = seriesTypes;
    }

    public List<ResetAttributeDTO> getResetAttributes() {
        return resetAttributes;
    }

    public void setResetAttributes(List<ResetAttributeDTO> resetAttributes) {
        this.resetAttributes = resetAttributes;
    }

    public static class PlaceholderDTO {
        private String label;
        private String value;
        private String description;
        private String sample;

        public PlaceholderDTO() {
        }

        public PlaceholderDTO(String label, String value, String description, String sample) {
            this.label = label;
            this.value = value;
            this.description = description;
            this.sample = sample;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSample() {
            return sample;
        }

        public void setSample(String sample) {
            this.sample = sample;
        }
    }

    public static class SeparatorDTO {
        private String label;
        private String value;
        private String symbol;

        public SeparatorDTO() {
        }

        public SeparatorDTO(String label, String value, String symbol) {
            this.label = label;
            this.value = value;
            this.symbol = symbol;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }

    public static class SeriesTypeDTO {
        private String label;
        private String value;

        public SeriesTypeDTO() {
        }

        public SeriesTypeDTO(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ResetAttributeDTO {
        private String label;
        private String value;
        private String description;

        public ResetAttributeDTO() {
        }

        public ResetAttributeDTO(String label, String value, String description) {
            this.label = label;
            this.value = value;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
