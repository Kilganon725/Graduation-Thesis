package com.example.aifomo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aifomo.entity.AiChat;

import java.util.List;

public interface AiChatService extends IService<AiChat> {
    AiChat chat(String username, String question);
    List<AiChat> history(String username, int limit);
}
