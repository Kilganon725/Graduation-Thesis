package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;
import com.example.aifomo.mapper.FomoTestMapper;
import com.example.aifomo.mapper.RecommendationMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationServiceImpl extends ServiceImpl<RecommendationMapper, Recommendation> implements RecommendationService {
    private final UserMapper userMapper;
    private final FomoTestMapper fomoTestMapper;

    public RecommendationServiceImpl(UserMapper userMapper, FomoTestMapper fomoTestMapper) {
        this.userMapper = userMapper;
        this.fomoTestMapper = fomoTestMapper;
    }

    @Override
    public List<Recommendation> generateForUser(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        FomoTest test = fomoTestMapper.selectOne(new LambdaQueryWrapper<FomoTest>().eq(FomoTest::getUserId, user.getId()).orderByDesc(FomoTest::getId).last("limit 1"));

        List<Recommendation> result = new ArrayList<>();
        if (test == null) {
            result.add(build(user.getId(), "先完成一次FOMO测评，再生成个性化学习路径。", "default"));
            return result;
        }

        String goal = user.getLearningGoal() == null ? "" : user.getLearningGoal();
        boolean aiGoal = goal.contains("AI") || goal.contains("人工智能") || goal.contains("Python") || goal.contains("数据");
        boolean highAnxiety = "中度焦虑".equals(test.getAnxietyLevel()) || "高度焦虑".equals(test.getAnxietyLevel());
        boolean lowStudy = test.getShortVideoTime() >= 12;
        boolean frequentSwitch = test.getLearningSwitch() >= 12;

        if (highAnxiety && lowStudy) {
            result.add(build(user.getId(), "基础学习路径：每天固定 30 分钟起步，先建立单一可执行计划，优先完成基础课程和练习。", "基础路径"));
        }
        if (aiGoal) {
            result.add(build(user.getId(), "AI方向路径：Python 基础 -> 数据分析基础 -> Pandas/NumPy -> 可视化 -> 小型项目实战。", "AI路线"));
        }
        if (frequentSwitch) {
            result.add(build(user.getId(), "方向稳定策略：先锁定一个主方向 4 周，禁止同时开启多个课程，集中积累成果。", "聚焦策略"));
        }
        if (result.isEmpty()) {
            result.add(build(user.getId(), "均衡学习路径：按周制定输入、练习、复盘三段式计划，每天保持稳定学习节奏。", "均衡路径"));
        }

        remove(new LambdaQueryWrapper<Recommendation>().eq(Recommendation::getUserId, user.getId()));
        saveBatch(result);
        return result;
    }

    @Override
    public List<Recommendation> myRecommendations(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return list(new LambdaQueryWrapper<Recommendation>().eq(Recommendation::getUserId, user.getId()).orderByDesc(Recommendation::getId));
    }

    private Recommendation build(Long userId, String content, String type) {
        Recommendation recommendation = new Recommendation();
        recommendation.setUserId(userId);
        recommendation.setContent(content);
        recommendation.setType(type);
        return recommendation;
    }
}
