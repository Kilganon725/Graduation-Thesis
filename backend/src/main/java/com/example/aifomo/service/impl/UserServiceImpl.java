package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.dto.LoginRequest;
import com.example.aifomo.dto.RegisterRequest;
import com.example.aifomo.dto.TokenResponse;
import com.example.aifomo.dto.UserProfileUpdateRequest;
import com.example.aifomo.entity.User;
import com.example.aifomo.exception.BizException;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.UserService;
import com.example.aifomo.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public TokenResponse register(RegisterRequest request) {
        User exists = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (exists != null) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMajor(request.getMajor());
        user.setLearningGoal(request.getLearningGoal());
        user.setCreatedTime(LocalDateTime.now());
        save(user);
        return new TokenResponse(jwtUtil.generateToken(user.getId(), user.getUsername()), user.getId(), user.getUsername());
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        return new TokenResponse(jwtUtil.generateToken(user.getId(), user.getUsername()), user.getId(), user.getUsername());
    }

    @Override
    public User currentUser(String username) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    @Override
    public User updateProfile(String username, UserProfileUpdateRequest request) {
        User user = currentUser(username);
        user.setMajor(request.getMajor());
        user.setLearningGoal(request.getLearningGoal());
        updateById(user);
        return user;
    }
}
