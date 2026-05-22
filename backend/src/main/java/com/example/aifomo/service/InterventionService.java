package com.example.aifomo.service;

import com.example.aifomo.entity.FomoIntervention;
import com.example.aifomo.entity.FomoTest;
import com.example.aifomo.entity.User;

public interface InterventionService {
    FomoIntervention createForTest(User user, FomoTest test);
    FomoIntervention latestByTestId(Long testId);
    FomoIntervention complete(Long id, String username);
    long totalCount();
    long completedCount();
}
