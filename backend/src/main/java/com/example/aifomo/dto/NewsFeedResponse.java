package com.example.aifomo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewsFeedResponse {
    private String category;
    private String keyword;
    private int total;
    private boolean mock;
    private LocalDateTime fetchedAt;
    private List<NewsArticleVO> articles;
}
