package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.UserProfileUpdateRequest;
import com.example.aifomo.entity.User;
import com.example.aifomo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResponse<User> profile(Authentication authentication) {
        return ApiResponse.success(userService.currentUser(authentication.getName()));
    }

    @PutMapping("/profile")
    public ApiResponse<User> update(Authentication authentication, @RequestBody @Valid UserProfileUpdateRequest request) {
        return ApiResponse.success(userService.updateProfile(authentication.getName(), request));
    }
}
