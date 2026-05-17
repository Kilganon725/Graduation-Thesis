package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.dto.StatsResponse;
import com.example.aifomo.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public ApiResponse<StatsResponse> stats() {
        return ApiResponse.success(statsService.getStats());
    }
}
