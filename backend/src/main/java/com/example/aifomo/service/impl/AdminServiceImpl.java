package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aifomo.dto.AdminDashboardResponse;
import com.example.aifomo.entity.AiChat;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.Recommendation;
import com.example.aifomo.entity.User;
import com.example.aifomo.exception.BizException;
import com.example.aifomo.mapper.AiChatMapper;
import com.example.aifomo.mapper.FomoTestMapper;
import com.example.aifomo.mapper.RecommendationMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserMapper userMapper;
    private final FomoTestMapper fomoTestMapper;
    private final AiChatMapper aiChatMapper;
    private final RecommendationMapper recommendationMapper;

    public AdminServiceImpl(UserMapper userMapper,
                            FomoTestMapper fomoTestMapper,
                            AiChatMapper aiChatMapper,
                            RecommendationMapper recommendationMapper) {
        this.userMapper = userMapper;
        this.fomoTestMapper = fomoTestMapper;
        this.aiChatMapper = aiChatMapper;
        this.recommendationMapper = recommendationMapper;
    }

    private void checkAdmin(String username) {
        if (!"admin".equals(username)) {
            throw new BizException("无管理员权限");
        }
    }

    @Override
    public Page<User> users(long page, long size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().orderByDesc(User::getId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getMajor, keyword)
                    .or().like(User::getLearningGoal, keyword));
        }
        return userMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public Page<AdminDashboardResponse.RecentRecommendation> recommendations(long page, long size) {
        Page<Recommendation> rawPage = recommendationMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<Recommendation>().orderByDesc(Recommendation::getId));
        Map<Long, User> userMap = loadUserMap();
        return convertPage(rawPage, item -> toRecentRecommendation(item, userMap.get(item.getUserId())));
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

    @Override
    public AdminDashboardResponse dashboard() {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getId));
        List<FomoTest> tests = fomoTestMapper.selectList(new LambdaQueryWrapper<FomoTest>().orderByDesc(FomoTest::getId));
        List<AiChat> chats = aiChatMapper.selectList(new LambdaQueryWrapper<AiChat>().orderByDesc(AiChat::getId));
        List<Recommendation> recommendations = recommendationMapper.selectList(new LambdaQueryWrapper<Recommendation>().orderByDesc(Recommendation::getId));

        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a, LinkedHashMap::new));

        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setSummary(buildSummary(users, tests, chats, recommendations));
        response.setAnxietyDistribution(buildAnxietyDistribution(tests));
        response.setRegistrationTrend(buildRegistrationTrend(users));
        response.setAiUsageFrequency(buildAiUsageFrequency(tests));
        response.setMajorDistribution(buildMajorDistribution(users));
        response.setRecommendationTypeDistribution(buildRecommendationTypeDistribution(recommendations));
        response.setRecentUsers(users.stream().limit(8).map(this::toRecentUser).toList());
        response.setRecentFomoTests(tests.stream().limit(8).map(test -> toRecentFomoTest(test, userMap.get(test.getUserId()))).toList());
        response.setRecentChats(chats.stream().limit(8).map(chat -> toRecentChat(chat, userMap.get(chat.getUserId()))).toList());
        response.setRecentRecommendations(recommendations.stream().limit(8).map(rec -> toRecentRecommendation(rec, userMap.get(rec.getUserId()))).toList());
        return response;
    }

    @Override
    public Page<AdminDashboardResponse.RecentFomoTest> fomoTests(long page, long size) {
        Page<FomoTest> rawPage = fomoTestMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<FomoTest>().orderByDesc(FomoTest::getId));
        Map<Long, User> userMap = loadUserMap();
        return convertPage(rawPage, item -> toRecentFomoTest(item, userMap.get(item.getUserId())));
    }

    @Override
    public Page<AdminDashboardResponse.RecentChat> chats(long page, long size) {
        Page<AiChat> rawPage = aiChatMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<AiChat>().orderByDesc(AiChat::getId));
        Map<Long, User> userMap = loadUserMap();
        return convertPage(rawPage, item -> toRecentChat(item, userMap.get(item.getUserId())));
    }

    private Map<Long, User> loadUserMap() {
        return userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getId))
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a, LinkedHashMap::new));
    }

    private <T, R> Page<R> convertPage(Page<T> source, java.util.function.Function<T, R> mapper) {
        Page<R> page = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        page.setRecords(source.getRecords().stream().map(mapper).toList());
        return page;
    }

    private AdminDashboardResponse.Summary buildSummary(List<User> users, List<FomoTest> tests, List<AiChat> chats, List<Recommendation> recommendations) {
        AdminDashboardResponse.Summary summary = new AdminDashboardResponse.Summary();
        summary.setTotalUsers(users.size());
        summary.setTotalFomoTests(tests.size());
        summary.setTotalAiChats(chats.size());
        summary.setTotalRecommendations(recommendations.size());
        summary.setHighAnxietyCount(tests.stream().filter(item -> "高度焦虑".equals(item.getAnxietyLevel())).count());
        summary.setAverageScore(tests.stream().mapToInt(item -> item.getTotalScore() == null ? 0 : item.getTotalScore()).average().orElse(0));
        return summary;
    }

    private List<AdminDashboardResponse.LabelValue> buildAnxietyDistribution(List<FomoTest> tests) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("正常", 0);
        map.put("轻度焦虑", 0);
        map.put("中度焦虑", 0);
        map.put("高度焦虑", 0);
        for (FomoTest item : tests) {
            String label = item.getAnxietyLevel() == null ? "正常" : item.getAnxietyLevel();
            map.put(label, map.getOrDefault(label, 0) + 1);
        }
        return map.entrySet().stream().map(e -> new AdminDashboardResponse.LabelValue(e.getKey(), e.getValue())).toList();
    }

    private List<AdminDashboardResponse.LabelValue> buildRegistrationTrend(List<User> users) {
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        for (User user : users) {
            if (user.getCreatedTime() == null) {
                continue;
            }
            LocalDate date = user.getCreatedTime().toLocalDate();
            map.put(date, map.getOrDefault(date, 0) + 1);
        }
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new AdminDashboardResponse.LabelValue(e.getKey().toString(), e.getValue()))
                .toList();
    }

    private List<AdminDashboardResponse.LabelValue> buildAiUsageFrequency(List<FomoTest> tests) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("0-2次", 0);
        map.put("3-5次", 0);
        map.put("6-10次", 0);
        map.put("10次以上", 0);
        for (FomoTest item : tests) {
            int value = item.getAiUsageTimes() == null ? 0 : item.getAiUsageTimes();
            String label;
            if (value <= 2) label = "0-2次";
            else if (value <= 5) label = "3-5次";
            else if (value <= 10) label = "6-10次";
            else label = "10次以上";
            map.put(label, map.get(label) + 1);
        }
        return map.entrySet().stream().map(e -> new AdminDashboardResponse.LabelValue(e.getKey(), e.getValue())).toList();
    }

    private List<AdminDashboardResponse.LabelValue> buildMajorDistribution(List<User> users) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (User user : users) {
            String major = user.getMajor();
            if (major == null || major.isBlank()) {
                major = "未填写";
            }
            map.put(major, map.getOrDefault(major, 0) + 1);
        }
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(e -> new AdminDashboardResponse.LabelValue(e.getKey(), e.getValue()))
                .toList();
    }

    private List<AdminDashboardResponse.LabelValue> buildRecommendationTypeDistribution(List<Recommendation> recommendations) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Recommendation recommendation : recommendations) {
            String type = recommendation.getType();
            if (type == null || type.isBlank()) {
                type = "未分类";
            }
            map.put(type, map.getOrDefault(type, 0) + 1);
        }
        return map.entrySet().stream().map(e -> new AdminDashboardResponse.LabelValue(e.getKey(), e.getValue())).toList();
    }

    private AdminDashboardResponse.RecentUser toRecentUser(User user) {
        AdminDashboardResponse.RecentUser item = new AdminDashboardResponse.RecentUser();
        item.setId(user.getId());
        item.setUsername(user.getUsername());
        item.setMajor(user.getMajor());
        item.setLearningGoal(user.getLearningGoal());
        item.setCreatedTime(user.getCreatedTime());
        return item;
    }

    private AdminDashboardResponse.RecentFomoTest toRecentFomoTest(FomoTest test, User user) {
        AdminDashboardResponse.RecentFomoTest item = new AdminDashboardResponse.RecentFomoTest();
        item.setId(test.getId());
        item.setUserId(test.getUserId());
        item.setUsername(user == null ? "-" : user.getUsername());
        item.setTotalScore(test.getTotalScore());
        item.setAnxietyLevel(test.getAnxietyLevel());
        item.setAiUsageTimes(test.getAiUsageTimes());
        item.setCreatedTime(test.getCreatedTime());
        return item;
    }

    private AdminDashboardResponse.RecentChat toRecentChat(AiChat chat, User user) {
        AdminDashboardResponse.RecentChat item = new AdminDashboardResponse.RecentChat();
        item.setId(chat.getId());
        item.setUserId(chat.getUserId());
        item.setUsername(user == null ? "-" : user.getUsername());
        item.setQuestion(chat.getQuestion());
        item.setAnswer(chat.getAnswer());
        item.setCreatedTime(chat.getCreatedTime());
        return item;
    }

    private AdminDashboardResponse.RecentRecommendation toRecentRecommendation(Recommendation recommendation, User user) {
        AdminDashboardResponse.RecentRecommendation item = new AdminDashboardResponse.RecentRecommendation();
        item.setId(recommendation.getId());
        item.setUserId(recommendation.getUserId());
        item.setUsername(user == null ? "-" : user.getUsername());
        item.setContent(recommendation.getContent());
        item.setType(recommendation.getType());
        return item;
    }
}
