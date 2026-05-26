package com.example.aifomo.dto;

import lombok.Data;

@Data
public class NewsArticleVO {
    private String title;
    private String description;
    private String content;
    private String url;
    private String urlToImage;
    private String sourceName;
    private String publishedAt;
    private String category;
}
