package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aifomo.dto.StatsResponse;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.mapper.FomoTestMapper;
import com.example.aifomo.service.StatsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatsServiceImpl implements StatsService {
    private final FomoTestMapper fomoTestMapper;

    public StatsServiceImpl(FomoTestMapper fomoTestMapper) {
        this.fomoTestMapper = fomoTestMapper;
    }

    @Override
    public StatsResponse getStats() {
        List<FomoTest> list = fomoTestMapper.selectList(new LambdaQueryWrapper<FomoTest>().orderByDesc(FomoTest::getId).last("limit 30"));
        StatsResponse response = new StatsResponse();
        response.setAnxietyDistribution(buildAnxiety(list));
        response.setLearningTimeTrend(buildTrend(list));
        response.setAiUsageFrequency(buildAiUsage(list));
        return response;
    }

    private List<StatsResponse.LabelValue> buildAnxiety(List<FomoTest> list) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("正常", 0);
        map.put("轻度焦虑", 0);
        map.put("中度焦虑", 0);
        map.put("高度焦虑", 0);
        for (FomoTest item : list) {
            map.put(item.getAnxietyLevel(), map.getOrDefault(item.getAnxietyLevel(), 0) + 1);
        }
        return map.entrySet().stream().map(e -> new StatsResponse.LabelValue(e.getKey(), e.getValue())).toList();
    }

    private List<StatsResponse.LabelValue> buildTrend(List<FomoTest> list) {
        List<StatsResponse.LabelValue> trend = new ArrayList<>();
        List<FomoTest> ordered = new ArrayList<>(list);
        Collections.reverse(ordered);
        for (FomoTest item : ordered) {
            int learningIndex = Math.max(0, 20 - item.getShortVideoTime());
            String label = item.getCreatedTime() == null ? "ID" + item.getId() : item.getCreatedTime().toLocalDate().toString();
            trend.add(new StatsResponse.LabelValue(label, learningIndex));
        }
        return trend;
    }

    private List<StatsResponse.LabelValue> buildAiUsage(List<FomoTest> list) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("低", 0);
        map.put("中", 0);
        map.put("高", 0);
        map.put("很高", 0);
        for (FomoTest item : list) {
            String label;
            int value = item.getAiUsage();
            if (value <= 5) label = "低";
            else if (value <= 10) label = "中";
            else if (value <= 15) label = "高";
            else label = "很高";
            map.put(label, map.get(label) + 1);
        }
        return map.entrySet().stream().map(e -> new StatsResponse.LabelValue(e.getKey(), e.getValue())).toList();
    }
}
