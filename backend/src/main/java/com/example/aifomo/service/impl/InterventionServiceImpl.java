package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.entity.FomoIntervention;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.User;
import com.example.aifomo.exception.BizException;
import com.example.aifomo.mapper.FomoInterventionMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.InterventionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InterventionServiceImpl extends ServiceImpl<FomoInterventionMapper, FomoIntervention> implements InterventionService {
    private final UserMapper userMapper;

    public InterventionServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public FomoIntervention createForTest(User user, FomoTest test) {
        FomoIntervention intervention = new FomoIntervention();
        intervention.setUserId(user.getId());
        intervention.setTestId(test.getId());
        intervention.setTitle(buildTitle(test));
        intervention.setContent(buildContent(test, user));
        intervention.setStatus("待执行");
        intervention.setCreatedTime(LocalDateTime.now());
        save(intervention);
        return intervention;
    }

    @Override
    public FomoIntervention latestByTestId(Long testId) {
        return getOne(new LambdaQueryWrapper<FomoIntervention>().eq(FomoIntervention::getTestId, testId).orderByDesc(FomoIntervention::getId).last("limit 1"));
    }

    @Override
    @Transactional
    public FomoIntervention complete(Long id, String username) {
        FomoIntervention intervention = getById(id);
        if (intervention == null) {
            throw new BizException("干预计划不存在");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null || !user.getId().equals(intervention.getUserId())) {
            throw new BizException("无权限操作该干预计划");
        }
        if (!"已完成".equals(intervention.getStatus())) {
            intervention.setStatus("已完成");
            intervention.setCompletedTime(LocalDateTime.now());
            updateById(intervention);
        }
        return intervention;
    }

    @Override
    public long totalCount() {
        return count();
    }

    @Override
    public long completedCount() {
        return count(new LambdaQueryWrapper<FomoIntervention>().eq(FomoIntervention::getStatus, "已完成"));
    }

    private String buildTitle(FomoTest test) {
        if ("高度焦虑".equals(test.getAnxietyLevel())) {
            return "高强度干预计划";
        }
        if ("中度焦虑".equals(test.getAnxietyLevel())) {
            return "中等强度干预计划";
        }
        if ("轻度焦虑".equals(test.getAnxietyLevel())) {
            return "轻量干预计划";
        }
        return "学习稳定计划";
    }

    private String buildContent(FomoTest test, User user) {
        List<String> items = new ArrayList<>();
        String goal = user.getLearningGoal() == null ? "你的学习目标" : user.getLearningGoal();
        if (test.getShortVideoTime() >= 15) {
            items.add("把短视频浏览控制在每天 20 分钟以内，固定在晚间一个时段集中查看。");
        }
        if (test.getLearningSwitch() >= 15) {
            items.add("本周只保留 1 个主学习方向，围绕 " + goal + " 设计连续 7 天任务。");
        }
        if (test.getAnxietyFrequency() != null && test.getAnxietyFrequency() >= 4) {
            items.add("遇到 AI 相关焦虑时，先写下问题，再集中在固定时段使用 AI 工具，不做碎片化搜索。");
        }
        if (test.getAiUsage() >= 15) {
            items.add("将 AI 工具使用限制为学习前 15 分钟、学习后 15 分钟两次，避免高频打断。");
        }
        if (items.isEmpty()) {
            items.add("保持当前习惯，继续按计划完成学习任务，并在 7 天后复测。");
        }
        items.add("在 7 天后重新进行 FOMO 测评，观察总分是否下降。");
        return String.join("\n", items);
    }
}
