package com.ieltsbeta.service;

import com.ieltsbeta.dto.AchievementStatusResponse;
import com.ieltsbeta.dto.GamificationSummaryResponse;
import com.ieltsbeta.dto.MissionStatusResponse;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.StudentAchievement;
import com.ieltsbeta.entity.StudentGamification;
import com.ieltsbeta.entity.StudentMission;
import com.ieltsbeta.event.BandCheckEvent;
import com.ieltsbeta.event.MissionCompletedEvent;
import com.ieltsbeta.repository.StudentAchievementRepository;
import com.ieltsbeta.repository.StudentGamificationRepository;
import com.ieltsbeta.repository.StudentMissionRepository;
import com.ieltsbeta.repository.SubmissionRepository;
import com.ieltsbeta.repository.TestAttemptRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Awards XP/coins for real actions (never a manual "claim" button), tracks a
 * daily streak, and unlocks achievements based on real milestones. See
 * GamificationCatalog for the fixed mission/achievement definitions.
 *
 * OBSERVER PATTERN (concrete Observer): this service no longer needs to be
 * called directly by AuthService/TestAttemptService/SubmissionService.
 * Instead it listens for MissionCompletedEvent / BandCheckEvent (published
 * by those services, the Subjects) via @EventListener. This decouples "a
 * student did something" from "gamification reacts to it" -- new observers
 * (e.g. an analytics service) could be added later without touching the
 * publishers at all.
 */
@Service
public class GamificationService {

    private final StudentGamificationRepository gamificationRepository;
    private final StudentMissionRepository missionRepository;
    private final StudentAchievementRepository achievementRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final SubmissionRepository submissionRepository;
    private final CurrentUserService currentUserService;

    public GamificationService(StudentGamificationRepository gamificationRepository,
                                StudentMissionRepository missionRepository,
                                StudentAchievementRepository achievementRepository,
                                TestAttemptRepository testAttemptRepository,
                                SubmissionRepository submissionRepository,
                                CurrentUserService currentUserService) {
        this.gamificationRepository = gamificationRepository;
        this.missionRepository = missionRepository;
        this.achievementRepository = achievementRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.submissionRepository = submissionRepository;
        this.currentUserService = currentUserService;
    }

    /** Observer entry point for MissionCompletedEvent -- see class javadoc. */
    @EventListener
    @Transactional
    public void onMissionCompleted(MissionCompletedEvent event) {
        recordMissionCompletion(event.getStudentId(), event.getMissionKey());
    }

    /** Observer entry point for BandCheckEvent -- see class javadoc. */
    @EventListener
    @Transactional
    public void onBandCheck(BandCheckEvent event) {
        checkBandAchievement(event.getStudent());
    }

    /**
     * Records that a student completed a real action tied to a mission.
     * Idempotent per calendar day -- calling this twice for the same
     * student+mission+day only awards XP/coins once.
     */
    @Transactional
    public void recordMissionCompletion(Long studentId, String missionKey) {
        GamificationCatalog.MissionDefinition def = GamificationCatalog.MISSIONS.stream()
                .filter(m -> m.key().equals(missionKey))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown mission key: " + missionKey));

        LocalDate today = LocalDate.now();
        StudentMission mission = missionRepository
                .findByStudentIdAndMissionKeyAndMissionDate(studentId, missionKey, today)
                .orElse(null);

        if (mission != null && Boolean.TRUE.equals(mission.getDone())) {
            return; // already awarded today
        }

        if (mission == null) {
            mission = new StudentMission();
            mission.setStudentId(studentId);
            mission.setMissionKey(missionKey);
            mission.setMissionDate(today);
        }
        mission.setDone(true);
        mission.setCompletedAt(OffsetDateTime.now());
        missionRepository.save(mission);

        StudentGamification gamification = gamificationRepository.findById(studentId).orElseGet(() -> {
            StudentGamification g = new StudentGamification();
            g.setStudentId(studentId);
            return g;
        });
        gamification.setXp(gamification.getXp() + def.xpReward());
        gamification.setCoins(gamification.getCoins() + def.coinsReward());
        updateStreak(gamification, today);
        gamification.setUpdatedAt(OffsetDateTime.now());
        gamificationRepository.save(gamification);

        checkAchievements(studentId, gamification);
    }

    private void updateStreak(StudentGamification gamification, LocalDate today) {
        LocalDate last = gamification.getLastActiveDate();
        if (last != null && last.equals(today)) {
            // already active today -- streak unchanged
        } else if (last != null && last.equals(today.minusDays(1))) {
            gamification.setStreak(gamification.getStreak() + 1);
        } else {
            gamification.setStreak(1); // first ever activity, or a gap -- reset
        }
        gamification.setLastActiveDate(today);
    }

    private void checkAchievements(Long studentId, StudentGamification gamification) {
        long testsCompleted = testAttemptRepository.findByStudent_StudentIdOrderByStartTimeDesc(studentId)
                .stream().filter(a -> a.getSubmitTime() != null).count();
        long submissionsCount = submissionRepository
                .findByStudent_StudentIdOrderBySubmittedAtDesc(studentId).size();

        maybeUnlock(studentId, "first_test", testsCompleted >= 1);
        maybeUnlock(studentId, "five_tests", testsCompleted >= 5);
        maybeUnlock(studentId, "first_submission", submissionsCount >= 1);
        maybeUnlock(studentId, "streak_3", gamification.getStreak() >= 3);
        maybeUnlock(studentId, "streak_7", gamification.getStreak() >= 7);
    }

    /** Called separately (needs live Student.targetBand/currentBand, not just the gamification row). */
    @Transactional
    public void checkBandAchievement(Student student) {
        if (student.getTargetBand() != null && student.getCurrentBand() != null
                && student.getCurrentBand().compareTo(student.getTargetBand()) >= 0) {
            maybeUnlock(student.getStudentId(), "band_achiever", true);
        }
    }

    private void maybeUnlock(Long studentId, String achievementKey, boolean condition) {
        if (!condition) {
            return;
        }
        if (achievementRepository.existsByStudentIdAndAchievementKey(studentId, achievementKey)) {
            return;
        }
        StudentAchievement achievement = new StudentAchievement();
        achievement.setStudentId(studentId);
        achievement.setAchievementKey(achievementKey);
        achievementRepository.save(achievement);
    }

    @Transactional(readOnly = true)
    public GamificationSummaryResponse getSummary(Jwt jwt) {
        Student student = currentUserService.requireStudent(jwt);
        Long studentId = student.getStudentId();

        StudentGamification gamification = gamificationRepository.findById(studentId).orElse(null);
        int xp = gamification != null ? gamification.getXp() : 0;
        int coins = gamification != null ? gamification.getCoins() : 0;
        int streak = gamification != null ? gamification.getStreak() : 0;
        int level = xp / 100 + 1;

        LocalDate today = LocalDate.now();
        Map<String, StudentMission> todaysMissions = missionRepository
                .findByStudentIdAndMissionDate(studentId, today).stream()
                .collect(Collectors.toMap(StudentMission::getMissionKey, m -> m));

        List<MissionStatusResponse> missions = GamificationCatalog.MISSIONS.stream()
                .map(def -> new MissionStatusResponse(
                        def.key(),
                        def.title(),
                        def.xpReward(),
                        todaysMissions.containsKey(def.key())
                                && Boolean.TRUE.equals(todaysMissions.get(def.key()).getDone())
                ))
                .toList();

        Set<String> unlockedKeys = achievementRepository.findByStudentId(studentId).stream()
                .map(StudentAchievement::getAchievementKey)
                .collect(Collectors.toSet());

        List<AchievementStatusResponse> achievements = GamificationCatalog.ACHIEVEMENTS.stream()
                .map(def -> new AchievementStatusResponse(
                        def.key(), def.title(), def.icon(), unlockedKeys.contains(def.key())))
                .toList();

        return new GamificationSummaryResponse(xp, coins, streak, level, missions, achievements);
    }
}
