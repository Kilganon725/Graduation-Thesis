package com.example.aifomo.service;

import com.example.aifomo.dto.NewsFeedResponse;

public interface NewsService {
    NewsFeedResponse getNews(String category, String keyword, int pageSize);
}
