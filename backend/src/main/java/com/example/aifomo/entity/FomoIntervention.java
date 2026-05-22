package com.example.aifomo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fomo_intervention")
public class FomoIntervention {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long testId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime completedTime;
}
