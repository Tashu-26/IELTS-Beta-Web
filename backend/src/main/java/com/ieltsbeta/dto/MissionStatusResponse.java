package com.ieltsbeta.dto;

public record MissionStatusResponse(
        String key,
        String title,
        int xpReward,
        boolean done
) {}
