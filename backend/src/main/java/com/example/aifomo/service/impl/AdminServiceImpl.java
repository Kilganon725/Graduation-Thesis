package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;
import com.example.aifomo.exception.BizException;
import com.example.aifomo.mapper.RecommendationMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserMapper userMapper;
    private final RecommendationMapper recommendationMapper;

    public AdminServiceImpl(UserMapper userMapper, RecommendationMapper recommendationMapper) {
        this.userMapper = userMapper;
        this.recommendationMapper = recommendationMapper;
    }

    private void checkAdmin(String username) {
        if (!"admin".equals(username)) {
            throw new BizException("无管理员权限");
        }
    }

    @Override
    public Page<User> users(long page, long size) {
        return userMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<User>().orderByDesc(User::getId));
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public Page<Recommendation> recommendations(long page, long size) {
        return recommendationMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<Recommendation>().orderByDesc(Recommendation::getId));
    }

    @Override
    public Recommendation saveRecommendation(Recommendation recommendation) {
        if (recommendation.getId() == null) {
            recommendationMapper.insert(recommendation);
        } else {
            recommendationMapper.updateById(recommendation);
        }
        return recommendation;
    }

    @Override
    public void deleteRecommendation(Long id) {
        recommendationMapper.deleteById(id);
    }

    public void ensureAdmin(String username) {
        checkAdmin(username);
    }
}
