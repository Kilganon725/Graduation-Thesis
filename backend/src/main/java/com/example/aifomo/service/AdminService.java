package com.example.aifomo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aifomo.dto.AdminDashboardResponse;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;
import com.example.aifomo.entity.AiChat;
import com.example.aifomo.entity.FomoTest;

public interface AdminService {
    AdminDashboardResponse dashboard();
    Page<User> users(long page, long size, String keyword);
    void deleteUser(Long id);
    Page<AdminDashboardResponse.RecentFomoTest> fomoTests(long page, long size);
    Page<AdminDashboardResponse.RecentChat> chats(long page, long size);
    Page<AdminDashboardResponse.RecentRecommendation> recommendations(long page, long size);
    Recommendation saveRecommendation(Recommendation recommendation);
    void deleteRecommendation(Long id);
}
