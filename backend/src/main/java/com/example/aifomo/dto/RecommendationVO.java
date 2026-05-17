package com.example.aifomo.dto;

import lombok.Data;

@Data
public class RecommendationVO {
    private Long id;
    private Long userId;
    private String content;
    private String type;
}
