package com.example.aifomo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_chat")
public class AiChat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private LocalDateTime createdTime;
}
