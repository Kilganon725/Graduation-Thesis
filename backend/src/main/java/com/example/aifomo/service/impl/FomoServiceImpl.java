package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.dto.FomoTestRequest;
import com.example.aifomo.dto.FomoResultVO;
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
        test.setShortVideoMinutes(request.getShortVideoMinutes());
        test.setLearningSwitchTimes(request.getLearningSwitchTimes());
        test.setAnxietyFrequency(request.getAnxietyFrequency());
        test.setAiUsageTimes(request.getAiUsageTimes());

        int shortVideoScore = scoreShortVideoMinutes(request.getShortVideoMinutes());
        int switchScore = scoreLearningSwitchTimes(request.getLearningSwitchTimes());
        int anxietyScore = scoreAnxietyFrequency(request.getAnxietyFrequency());
        int aiUsageScore = scoreAiUsageTimes(request.getAiUsageTimes());

        test.setShortVideoTime(shortVideoScore);
        test.setLearningSwitch(switchScore);
        test.setAnxietyLevel(calculateLevel(shortVideoScore, switchScore, anxietyScore, aiUsageScore));
        test.setAiUsage(aiUsageScore);
        test.setTotalScore(shortVideoScore + switchScore + anxietyScore + aiUsageScore);
        test.setCreatedTime(LocalDateTime.now());
        save(test);
        return test;
    }

    @Override
    public FomoTest latestEntityByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return list(new LambdaQueryWrapper<FomoTest>().eq(FomoTest::getUserId, user.getId()).orderByDesc(FomoTest::getId))
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public FomoResultVO latestByUsername(String username) {
        FomoTest latest = latestEntityByUsername(username);
        return toVO(latest);
    }

    public static String calculateLevel(int shortVideoTime, int learningSwitch, int anxietyLevel, int aiUsage) {
        int total = shortVideoTime + learningSwitch + anxietyLevel + aiUsage;
        if (total <= 30) return "正常";
        if (total <= 60) return "轻度焦虑";
        if (total <= 80) return "中度焦虑";
        return "高度焦虑";
    }

    public static int scoreShortVideoMinutes(int minutes) {
        if (minutes <= 15) return 0;
        if (minutes <= 30) return 5;
        if (minutes <= 60) return 10;
        if (minutes <= 120) return 15;
        return 20;
    }

    public static int scoreLearningSwitchTimes(int times) {
        if (times <= 1) return 0;
        if (times <= 3) return 5;
        if (times <= 5) return 10;
        if (times <= 8) return 15;
        return 20;
    }

    public static int scoreAnxietyFrequency(int frequency) {
        if (frequency <= 1) return 0;
        if (frequency == 2) return 5;
        if (frequency == 3) return 10;
        if (frequency == 4) return 15;
        return 20;
    }

    public static int scoreAiUsageTimes(int times) {
        if (times == 0) return 0;
        if (times <= 2) return 5;
        if (times <= 5) return 10;
        if (times <= 10) return 15;
        return 20;
    }

    public static FomoResultVO toVO(FomoTest test) {
        if (test == null) {
            return null;
        }
        FomoResultVO vo = new FomoResultVO();
        vo.setId(test.getId());
        vo.setUserId(test.getUserId());
        vo.setShortVideoMinutes(test.getShortVideoMinutes());
        vo.setLearningSwitchTimes(test.getLearningSwitchTimes());
        vo.setAnxietyFrequency(test.getAnxietyFrequency());
        vo.setAiUsageTimes(test.getAiUsageTimes());
        vo.setShortVideoTime(test.getShortVideoTime());
        vo.setLearningSwitch(test.getLearningSwitch());
        vo.setAnxietyLevelScore(scoreAnxietyFrequency(test.getAnxietyFrequency()));
        vo.setAiUsage(test.getAiUsage());
        vo.setTotalScore(test.getTotalScore());
        vo.setAnxietyLevel(test.getAnxietyLevel());
        vo.setCreatedTime(test.getCreatedTime());
        return vo;
    }
}
