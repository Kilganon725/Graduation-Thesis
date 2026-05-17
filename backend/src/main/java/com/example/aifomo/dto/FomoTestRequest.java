package com.example.aifomo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FomoTestRequest {
    @NotNull(message = "shortVideoTime不能为空")
    @Min(value = 0, message = "shortVideoTime不能小于0")
    @Max(value = 20, message = "shortVideoTime不能大于20")
    private Integer shortVideoTime;

    @NotNull(message = "learningSwitch不能为空")
    @Min(value = 0, message = "learningSwitch不能小于0")
    @Max(value = 20, message = "learningSwitch不能大于20")
    private Integer learningSwitch;

    @NotNull(message = "anxietyLevel不能为空")
    @Min(value = 0, message = "anxietyLevel不能小于0")
    @Max(value = 20, message = "anxietyLevel不能大于20")
    private Integer anxietyLevel;

    @NotNull(message = "aiUsage不能为空")
    @Min(value = 0, message = "aiUsage不能小于0")
    @Max(value = 20, message = "aiUsage不能大于20")
    private Integer aiUsage;
}
