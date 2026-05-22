package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.dto.FomoTestRequest;
import com.example.aifomo.dto.FomoResultVO;
import com.example.aifomo.entity.FomoIntervention;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.User;
import com.example.aifomo.mapper.FomoTestMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.InterventionService;
import com.example.aifomo.service.FomoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FomoServiceImpl extends ServiceImpl<FomoTestMapper, FomoTest> implements FomoService {
    private final UserMapper userMapper;
    private final InterventionService interventionService;

    public FomoServiceImpl(UserMapper userMapper, InterventionService interventionService) {
        this.userMapper = userMapper;
        this.interventionService = interventionService;
    }

    @Override
    @Transactional
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
        interventionService.createForTest(user, test);
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
        if (latest == null) {
            return null;
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        FomoTest previous = list(new LambdaQueryWrapper<FomoTest>().eq(FomoTest::getUserId, user.getId()).orderByDesc(FomoTest::getId).last("limit 2"))
                .stream()
                .skip(1)
                .findFirst()
                .orElse(null);
        FomoIntervention intervention = interventionService.latestByTestId(latest.getId());
        return toVO(latest, previous, intervention);
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
        return toVO(test, null, null);
    }

    public static FomoResultVO toVO(FomoTest test, FomoTest previous, FomoIntervention intervention) {
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
        if (intervention != null) {
            vo.setInterventionId(intervention.getId());
            vo.setInterventionTitle(intervention.getTitle());
            vo.setInterventionContent(intervention.getContent());
            vo.setInterventionStatus(intervention.getStatus());
            vo.setInterventionCreatedTime(intervention.getCreatedTime());
            vo.setInterventionCompletedTime(intervention.getCompletedTime());
        }
        if (previous != null) {
            vo.setPreviousTotalScore(previous.getTotalScore());
            vo.setPreviousAnxietyLevel(previous.getAnxietyLevel());
            vo.setPreviousCreatedTime(previous.getCreatedTime());
            int delta = (test.getTotalScore() == null ? 0 : test.getTotalScore()) - (previous.getTotalScore() == null ? 0 : previous.getTotalScore());
            vo.setScoreDelta(delta);
            vo.setScoreTrend(delta < 0 ? "下降" : delta > 0 ? "上升" : "持平");
        }
        return vo;
    }
}
