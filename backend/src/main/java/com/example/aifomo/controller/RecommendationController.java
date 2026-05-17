package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.service.RecommendationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/generate")
    public ApiResponse<List<Recommendation>> generate(Authentication authentication) {
        return ApiResponse.success(recommendationService.generateForUser(authentication.getName()));
    }

    @GetMapping("/mine")
    public ApiResponse<List<Recommendation>> mine(Authentication authentication) {
        return ApiResponse.success(recommendationService.myRecommendations(authentication.getName()));
    }
}
