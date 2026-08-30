package com.ieltsbeta.dto;

public record AchievementStatusResponse(
        String key,
        String title,
        String icon,
        boolean unlocked
) {}
