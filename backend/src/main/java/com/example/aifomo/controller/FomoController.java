package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.FomoTestRequest;
import com.example.aifomo.dto.FomoResultVO;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.service.FomoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fomo")
public class FomoController {
    private final FomoService fomoService;

    public FomoController(FomoService fomoService) {
        this.fomoService = fomoService;
    }

    @PostMapping("/submit")
    public ApiResponse<FomoResultVO> submit(Authentication authentication, @RequestBody @Valid FomoTestRequest request) {
        fomoService.submit(authentication.getName(), request);
        return ApiResponse.success(fomoService.latestByUsername(authentication.getName()));
    }

    @GetMapping("/latest")
    public ApiResponse<FomoResultVO> latest(Authentication authentication) {
        return ApiResponse.success(fomoService.latestByUsername(authentication.getName()));
    }

    @GetMapping("/score")
    public ApiResponse<Map<String, Object>> score(Authentication authentication) {
        FomoTest latest = fomoService.latestEntityByUsername(authentication.getName());
        if (latest == null) {
            return ApiResponse.success(Map.of("totalScore", 0, "level", "暂无数据"));
        }
        return ApiResponse.success(Map.of("totalScore", latest.getTotalScore(), "level", latest.getAnxietyLevel()));
    }
}
