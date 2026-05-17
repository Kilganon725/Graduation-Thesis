package com.example.aifomo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aifomo.entity.Recommendation;

import java.util.List;

public interface RecommendationService extends IService<Recommendation> {
    List<Recommendation> generateForUser(String username);
    List<Recommendation> myRecommendations(String username);
}
