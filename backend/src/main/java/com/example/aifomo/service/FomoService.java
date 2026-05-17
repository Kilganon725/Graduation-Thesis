package com.example.aifomo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aifomo.dto.FomoTestRequest;
import com.example.aifomo.entity.FomoTest;

public interface FomoService extends IService<FomoTest> {
    FomoTest submit(String username, FomoTestRequest request);
    FomoTest latestByUsername(String username);
}
