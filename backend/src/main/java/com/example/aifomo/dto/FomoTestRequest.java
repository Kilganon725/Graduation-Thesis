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
}
