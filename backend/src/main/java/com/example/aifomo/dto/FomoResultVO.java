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

    private Integer shortVideoTime;
    private Integer learningSwitch;
    private Integer anxietyLevelScore;
    private Integer aiUsage;
    private Integer totalScore;
    private String anxietyLevel;
    private LocalDateTime createdTime;
}
