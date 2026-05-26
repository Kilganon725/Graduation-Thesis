package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.NewsFeedResponse;
import com.example.aifomo.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ApiResponse<NewsFeedResponse> news(@RequestParam(defaultValue = "ai") String category,
                                              @RequestParam(defaultValue = "") String keyword,
                                              @RequestParam(defaultValue = "8") int pageSize) {
        return ApiResponse.success(newsService.getNews(category, keyword, pageSize));
    }
}
