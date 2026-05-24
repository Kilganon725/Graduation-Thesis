package com.example.aifomo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FomoResultVO {
    private Long id;
    private Long userId;

    private Integer shortVideoMinutes;
    private Integer learningSwitchTimes;
    private Integer anxietyFrequency;
    private Integer aiUsageTimes;
    private Integer sleepHours;
    private Integer focusLevel;
    private Integer notificationFrequency;
    private Integer goalClarity;

    private Integer shortVideoTime;
    private Integer learningSwitch;
    private Integer anxietyLevelScore;
    private Integer aiUsage;
    private Integer totalScore;
    private String anxietyLevel;
    private LocalDateTime createdTime;

    private Long interventionId;
    private String interventionTitle;
    private String interventionContent;
    private String interventionStatus;
    private LocalDateTime interventionCreatedTime;
    private LocalDateTime interventionCompletedTime;

    private Integer previousTotalScore;
    private String previousAnxietyLevel;
    private LocalDateTime previousCreatedTime;
    private Integer scoreDelta;
    private String scoreTrend;
}
