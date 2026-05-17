package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.dto.FomoTestRequest;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.User;
import com.example.aifomo.mapper.FomoTestMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.FomoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FomoServiceImpl extends ServiceImpl<FomoTestMapper, FomoTest> implements FomoService {
    private final UserMapper userMapper;

    public FomoServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public FomoTest submit(String username, FomoTestRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        FomoTest test = new FomoTest();
        test.setUserId(user.getId());
        test.setShortVideoTime(request.getShortVideoTime());
        test.setLearningSwitch(request.getLearningSwitch());
        test.setAnxietyLevel(calculateLevel(request.getShortVideoTime(), request.getLearningSwitch(), request.getAnxietyLevel(), request.getAiUsage()));
        test.setAiUsage(request.getAiUsage());
        test.setTotalScore(request.getShortVideoTime() + request.getLearningSwitch() + request.getAnxietyLevel() + request.getAiUsage());
        test.setCreatedTime(LocalDateTime.now());
        save(test);
        return test;
    }

    @Override
    public FomoTest latestByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return getOne(new LambdaQueryWrapper<FomoTest>().eq(FomoTest::getUserId, user.getId()).orderByDesc(FomoTest::getId).last("limit 1"));
    }

    public static String calculateLevel(int shortVideoTime, int learningSwitch, int anxietyLevel, int aiUsage) {
        int total = shortVideoTime + learningSwitch + anxietyLevel + aiUsage;
        if (total <= 30) return "正常";
        if (total <= 60) return "轻度焦虑";
        if (total <= 80) return "中度焦虑";
        return "高度焦虑";
    }
}
