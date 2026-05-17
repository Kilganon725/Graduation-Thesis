package com.example.aifomo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aifomo.entity.AiChat;
import com.example.aifomo.entity.User;
import com.example.aifomo.mapper.AiChatMapper;
import com.example.aifomo.mapper.UserMapper;
import com.example.aifomo.service.AiChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiChatServiceImpl extends ServiceImpl<AiChatMapper, AiChat> implements AiChatService {
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public AiChatServiceImpl(UserMapper userMapper,
                             RestTemplate restTemplate,
                             @Value("${ai.api-key:}") String apiKey,
                             @Value("${ai.base-url:https://api.openai.com}") String baseUrl,
                             @Value("${ai.model:gpt-4o-mini}") String model) {
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    @Override
    public AiChat chat(String username, String question) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        String answer = callAi(user, question);
        AiChat chat = new AiChat();
        chat.setUserId(user.getId());
        chat.setQuestion(question);
        chat.setAnswer(answer);
        chat.setCreatedTime(LocalDateTime.now());
        save(chat);
        return chat;
    }

    @Override
    public List<AiChat> history(String username, int limit) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return list(new LambdaQueryWrapper<AiChat>().eq(AiChat::getUserId, user.getId()).orderByDesc(AiChat::getId).last("limit " + limit));
    }

    private String callAi(User user, String question) {
        if (apiKey == null || apiKey.isBlank()) {
            return "当前未配置 AI 接口密钥。你可以围绕“学习目标、焦虑原因、时间管理、AI 学习路线”继续提问。";
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "你是一个大学生学习助手，回答要简洁、具体、可执行。"));
            if (user.getLearningGoal() != null) {
                messages.add(Map.of("role", "system", "content", "用户学习目标：" + user.getLearningGoal()));
            }
            List<AiChat> recent = new ArrayList<>(history(user.getUsername(), 4));
            Collections.reverse(recent);
            for (AiChat item : recent) {
                messages.add(Map.of("role", "user", "content", item.getQuestion()));
                messages.add(Map.of("role", "assistant", "content", item.getAnswer()));
            }
            messages.add(Map.of("role", "user", "content", question));
            payload.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            String url = baseUrl + "/v1/chat/completions";
            String response = restTemplate.postForObject(url, new HttpEntity<>(payload, headers), String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (!content.isMissingNode()) {
                return content.asText();
            }
        } catch (Exception ignored) {
        }
        return "AI 接口调用失败，当前返回本地建议：把问题拆成目标、约束、下一步三个部分，再逐项执行。";
    }
}
