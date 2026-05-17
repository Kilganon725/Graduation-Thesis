package com.example.aifomo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;

public interface AdminService {
    Page<User> users(long page, long size);
    void deleteUser(Long id);
    Page<Recommendation> recommendations(long page, long size);
    Recommendation saveRecommendation(Recommendation recommendation);
    void deleteRecommendation(Long id);
}
