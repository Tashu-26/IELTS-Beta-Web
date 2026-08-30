package com.ieltsbeta.service;

import java.util.List;

/**
 * Fixed, hardcoded definitions for missions and achievements (per the Phase
 * 1/3 decision: these are small and static, so they live as constants here
 * rather than as database catalog tables -- only per-student PROGRESS
 * against them is persisted, in student_missions/student_achievements).
 *
 * Missions are tied to real actions elsewhere in the app (taking a test,
 * submitting work, updating a goal, checking progress) -- there is no
 * manual "mark complete" button anywhere.
 */
final class GamificationCatalog {

    private GamificationCatalog() {
    }

    record MissionDefinition(String key, String title, int xpReward, int coinsReward) {
    }

    record AchievementDefinition(String key, String title, String icon) {
    }

    static final List<MissionDefinition> MISSIONS = List.of(
            new MissionDefinition("take_test", "Complete a practice test today", 30, 6),
            new MissionDefinition("submit_work", "Submit Writing or Speaking work", 25, 5),
            new MissionDefinition("review_progress", "Check your progress page", 10, 2),
            new MissionDefinition("set_goal", "Set or update your goal", 15, 3)
    );

    static final List<AchievementDefinition> ACHIEVEMENTS = List.of(
            new AchievementDefinition("first_test", "Completed your first practice test", "🎯"),
            new AchievementDefinition("five_tests", "Completed 5 practice tests", "🏆"),
            new AchievementDefinition("first_submission", "Submitted your first Writing/Speaking task", "✍️"),
            new AchievementDefinition("streak_3", "3-day streak", "🔥"),
            new AchievementDefinition("streak_7", "7-day streak", "🔥🔥"),
            new AchievementDefinition("band_achiever", "Reached your target band", "🎓")
    );
}
