package com.example.aifomo.controller;

import com.example.aifomo.common.ApiResponse;
import com.example.aifomo.entity.FomoIntervention;
import com.example.aifomo.service.InterventionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/intervention")
public class InterventionController {
    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<FomoIntervention> complete(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || authentication.getName() == null) {
            throw new com.example.aifomo.exception.BizException("未登录");
        }
        return ApiResponse.success(interventionService.complete(id, authentication.getName()));
    }
}
