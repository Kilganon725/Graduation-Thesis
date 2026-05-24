package com.example.aifomo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fomo_test")
public class FomoTest {
    @TableId(type = IdType.AUTO)
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
    private String anxietyLevel;
    private Integer aiUsage;
    private Integer totalScore;
    private LocalDateTime createdTime;
}
