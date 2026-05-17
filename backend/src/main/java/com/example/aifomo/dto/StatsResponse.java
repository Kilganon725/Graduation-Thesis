package com.example.aifomo.dto;

import lombok.Data;

import java.util.List;

@Data
public class StatsResponse {
    private List<LabelValue> anxietyDistribution;
    private List<LabelValue> learningTimeTrend;
    private List<LabelValue> aiUsageFrequency;

    @Data
    public static class LabelValue {
        private String label;
        private Integer value;

        public LabelValue() {}

        public LabelValue(String label, Integer value) {
            this.label = label;
            this.value = value;
        }
    }
}
