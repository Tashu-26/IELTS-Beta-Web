package com.ieltsbeta.dto;

import java.util.List;

public record GamificationSummaryResponse(
        int xp,
        int coins,
        int streak,
        int level,
        List<MissionStatusResponse> missions,
        List<AchievementStatusResponse> achievements
) {}
