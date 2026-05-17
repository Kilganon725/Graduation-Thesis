package com.example.aifomo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aifomo.dto.LoginRequest;
import com.example.aifomo.dto.RegisterRequest;
import com.example.aifomo.dto.TokenResponse;
import com.example.aifomo.dto.UserProfileUpdateRequest;
import com.example.aifomo.entity.User;

public interface UserService extends IService<User> {
    TokenResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    User currentUser(String username);
    User updateProfile(String username, UserProfileUpdateRequest request);
}
