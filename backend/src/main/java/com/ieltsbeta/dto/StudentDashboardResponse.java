package com.ieltsbeta.dto;

public record StudentDashboardResponse(
        MeResponse profile,
        ProgressResponse progress,
        GamificationSummaryResponse gamification
) {
}