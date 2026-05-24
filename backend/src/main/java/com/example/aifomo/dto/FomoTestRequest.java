package com.example.aifomo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FomoTestRequest {
    @NotNull(message = "shortVideoMinutes不能为空")
    @Min(value = 0, message = "每天刷短视频时长不能小于0")
    @Max(value = 240, message = "每天刷短视频时长不能大于240")
    private Integer shortVideoMinutes;

    @NotNull(message = "learningSwitchTimes不能为空")
    @Min(value = 0, message = "每周切换学习方向次数不能小于0")
    @Max(value = 20, message = "每周切换学习方向次数不能大于20")
    private Integer learningSwitchTimes;

    @NotNull(message = "anxietyFrequency不能为空")
    @Min(value = 1, message = "AI信息焦虑频率不能小于1")
    @Max(value = 5, message = "AI信息焦虑频率不能大于5")
    private Integer anxietyFrequency;

    @NotNull(message = "aiUsageTimes不能为空")
    @Min(value = 0, message = "AI工具使用频率不能小于0")
    @Max(value = 50, message = "AI工具使用频率不能大于50")
    private Integer aiUsageTimes;

    @NotNull(message = "sleepHours不能为空")
    @Min(value = 0, message = "每日睡眠时长不能小于0")
    @Max(value = 16, message = "每日睡眠时长不能大于16")
    private Integer sleepHours;

    @NotNull(message = "focusLevel不能为空")
    @Min(value = 1, message = "专注程度不能小于1")
    @Max(value = 5, message = "专注程度不能大于5")
    private Integer focusLevel;

    @NotNull(message = "notificationFrequency不能为空")
    @Min(value = 1, message = "消息通知干扰频率不能小于1")
    @Max(value = 5, message = "消息通知干扰频率不能大于5")
    private Integer notificationFrequency;

    @NotNull(message = "goalClarity不能为空")
    @Min(value = 1, message = "目标清晰度不能小于1")
    @Max(value = 5, message = "目标清晰度不能大于5")
    private Integer goalClarity;
}
