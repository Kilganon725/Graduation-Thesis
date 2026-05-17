package com.example.aifomo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;
import com.example.aifomo.service.AdminService;
import com.example.aifomo.service.impl.AdminServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final AdminServiceImpl adminServiceImpl;

    public AdminController(AdminService adminService, AdminServiceImpl adminServiceImpl) {
        this.adminService = adminService;
        this.adminServiceImpl = adminServiceImpl;
    }

    private void ensureAdmin(Authentication authentication) {
        adminServiceImpl.ensureAdmin(authentication.getName());
    }

    @GetMapping("/users")
    public ApiResponse<Page<User>> users(Authentication authentication,
                                         @RequestParam(defaultValue = "1") long page,
                                         @RequestParam(defaultValue = "10") long size) {
        ensureAdmin(authentication);
        return ApiResponse.success(adminService.users(page, size));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(Authentication authentication, @PathVariable Long id) {
        ensureAdmin(authentication);
        adminService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/recommendations")
    public ApiResponse<Page<Recommendation>> recommendations(Authentication authentication,
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
