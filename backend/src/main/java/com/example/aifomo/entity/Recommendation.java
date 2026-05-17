package com.example.aifomo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("recommendation")
public class Recommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    private String type;
}
