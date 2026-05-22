package com.example.aifomo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminDashboardResponse {
    private Summary summary;
    private List<LabelValue> anxietyDistribution;
    private List<LabelValue> registrationTrend;
    private List<LabelValue> aiUsageFrequency;
    private List<LabelValue> majorDistribution;
    private List<LabelValue> recommendationTypeDistribution;
    private List<RecentUser> recentUsers;
    private List<RecentFomoTest> recentFomoTests;
    private List<RecentChat> recentChats;
    private List<RecentRecommendation> recentRecommendations;

    @Data
    public static class Summary {
        private long totalUsers;
        private long totalFomoTests;
        private long totalAiChats;
        private long totalRecommendations;
        private long interventionTotalCount;
        private long interventionCompletedCount;
        private double interventionCompletionRate;
        private long highAnxietyCount;
        private double averageScore;
    }

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

    @Data
    public static class RecentUser {
        private Long id;
        private String username;
        private String major;
        private String learningGoal;
        private LocalDateTime createdTime;
    }

    @Data
    public static class RecentFomoTest {
        private Long id;
        private Long userId;
        private String username;
        private Integer totalScore;
        private String anxietyLevel;
        private Integer aiUsageTimes;
        private LocalDateTime createdTime;
    }

    @Data
    public static class RecentChat {
        private Long id;
        private Long userId;
        private String username;
        private String question;
        private String answer;
        private LocalDateTime createdTime;
    }

    @Data
    public static class RecentRecommendation {
        private Long id;
        private Long userId;
        private String username;
        private String content;
        private String type;
    }
}
