package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.LoginRequest;
import com.example.aifomo.dto.RegisterRequest;
import com.example.aifomo.dto.TokenResponse;
import com.example.aifomo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }
}
