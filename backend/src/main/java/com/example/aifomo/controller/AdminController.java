package com.example.aifomo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.AdminDashboardResponse;
import com.example.aifomo.entity.AiChat;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;
import com.example.aifomo.service.AdminService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private void ensureAdmin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || !"admin".equals(authentication.getName())) {
            throw new com.example.aifomo.exception.BizException("无管理员权限");
        }
    }

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> dashboard(Authentication authentication) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.dashboard());
    }

    @GetMapping("/users")
    public ApiResponse<Page<User>> users(Authentication authentication,
                                         @RequestParam(defaultValue = "1") long page,
                                         @RequestParam(defaultValue = "10") long size,
                                         @RequestParam(defaultValue = "") String keyword) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.users(page, size, keyword));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(Authentication authentication, @PathVariable Long id) {
        ensureAdmin(authentication);
        adminService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/fomo-tests")
    public ApiResponse<Page<AdminDashboardResponse.RecentFomoTest>> fomoTests(Authentication authentication,
                                                                              @RequestParam(defaultValue = "1") long page,
                                                                              @RequestParam(defaultValue = "10") long size) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.fomoTests(page, size));
    }

    @GetMapping("/chats")
    public ApiResponse<Page<AdminDashboardResponse.RecentChat>> chats(Authentication authentication,
                                                                      @RequestParam(defaultValue = "1") long page,
                                                                      @RequestParam(defaultValue = "10") long size) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.chats(page, size));
    }

    @GetMapping("/recommendations")
    public ApiResponse<Page<AdminDashboardResponse.RecentRecommendation>> recommendations(Authentication authentication,
                                                                                          @RequestParam(defaultValue = "1") long page,
                                                                                          @RequestParam(defaultValue = "10") long size) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.recommendations(page, size));
    }

    @PostMapping("/recommendations")
    public ApiResponse<Recommendation> saveRecommendation(Authentication authentication, @RequestBody Recommendation recommendation) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.saveRecommendation(recommendation));
    }

    @DeleteMapping("/recommendations/{id}")
    public ApiResponse<Void> deleteRecommendation(Authentication authentication, @PathVariable Long id) {
        ensureAdmin(authentication);
        adminService.deleteRecommendation(id);
        return ApiResponse.success("删除成功", null);
    }
}
