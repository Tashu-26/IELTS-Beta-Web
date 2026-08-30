package com.ieltsbeta.service;

import com.ieltsbeta.entity.Student;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OBSERVER PATTERN under test: GamificationService is the concrete Observer.
 * These tests call the @EventListener entry points directly (as Spring
 * would when a MissionCompletedEvent/BandCheckEvent is published) to prove
 * the reaction logic works without needing a publisher in the loop at all --
 * exactly the decoupling the pattern is meant to buy.
 */
@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock private StudentGamificationRepository gamificationRepository;
    @Mock private StudentMissionRepository missionRepository;
    @Mock private StudentAchievementRepository achievementRepository;
    @Mock private TestAttemptRepository testAttemptRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private CurrentUserService currentUserService;

    private GamificationService gamificationService;

    @BeforeEach
    void setUp() {
        gamificationService = new GamificationService(gamificationRepository, missionRepository,
                achievementRepository, testAttemptRepository, submissionRepository, currentUserService);
    }

    @Test
    void onMissionCompleted_awardsXpAndCoins_forFirstCompletionToday() {
        Long studentId = 1L;
        when(missionRepository.findByStudentIdAndMissionKeyAndMissionDate(eq(studentId), eq("set_goal"), any()))
                .thenReturn(Optional.empty());
        when(gamificationRepository.findById(studentId)).thenReturn(Optional.empty());
        when(testAttemptRepository.findByStudent_StudentIdOrderByStartTimeDesc(studentId))
                .thenReturn(Collections.emptyList());
        when(submissionRepository.findByStudent_StudentIdOrderBySubmittedAtDesc(studentId))
                .thenReturn(Collections.emptyList());

        gamificationService.onMissionCompleted(new MissionCompletedEvent(studentId, "set_goal"));

        verify(missionRepository).save(any(StudentMission.class));
        verify(gamificationRepository).save(any(StudentGamification.class));
    }

    @Test
    void onMissionCompleted_doesNothing_whenAlreadyCompletedToday() {
        Long studentId = 1L;
        StudentMission alreadyDone = new StudentMission();
        alreadyDone.setDone(true);
        when(missionRepository.findByStudentIdAndMissionKeyAndMissionDate(eq(studentId), eq("set_goal"), any()))
                .thenReturn(Optional.of(alreadyDone));

        gamificationService.onMissionCompleted(new MissionCompletedEvent(studentId, "set_goal"));

        verify(missionRepository, never()).save(any());
        verify(gamificationRepository, never()).save(any());
    }

    @Test
    void onMissionCompleted_throws_forUnknownMissionKey() {
        assertThat(
                org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () ->
                        gamificationService.onMissionCompleted(new MissionCompletedEvent(1L, "not_a_real_mission")))
        ).hasMessageContaining("Unknown mission key");
    }

    @Test
    void onBandCheck_unlocksBandAchiever_whenCurrentMeetsTarget() {
        Student student = new Student();
        student.setStudentId(2L);
        student.setTargetBand(new BigDecimal("7.0"));
        student.setCurrentBand(new BigDecimal("7.5"));
        when(achievementRepository.existsByStudentIdAndAchievementKey(2L, "band_achiever")).thenReturn(false);

        gamificationService.onBandCheck(new BandCheckEvent(student));

        verify(achievementRepository).save(any());
    }

    @Test
    void onBandCheck_doesNothing_whenTargetNotYetReached() {
        Student student = new Student();
        student.setStudentId(2L);
        student.setTargetBand(new BigDecimal("8.0"));
        student.setCurrentBand(new BigDecimal("6.0"));

        gamificationService.onBandCheck(new BandCheckEvent(student));

        verify(achievementRepository, never()).save(any());
    }

    @Test
    void onBandCheck_doesNotDuplicateAchievement_whenAlreadyUnlocked() {
        Student student = new Student();
        student.setStudentId(2L);
        student.setTargetBand(new BigDecimal("7.0"));
        student.setCurrentBand(new BigDecimal("7.0"));
        when(achievementRepository.existsByStudentIdAndAchievementKey(2L, "band_achiever")).thenReturn(true);

        gamificationService.onBandCheck(new BandCheckEvent(student));

        verify(achievementRepository, never()).save(any());
    }

    @Test
    void getSummary_computesLevelFromXp() {
        Student student = new Student();
        student.setStudentId(3L);
        org.springframework.security.oauth2.jwt.Jwt jwt = org.mockito.Mockito.mock(org.springframework.security.oauth2.jwt.Jwt.class);
        when(currentUserService.requireStudent(jwt)).thenReturn(student);

        StudentGamification gamification = new StudentGamification();
        gamification.setXp(250);
        gamification.setCoins(10);
        gamification.setStreak(2);
        when(gamificationRepository.findById(3L)).thenReturn(Optional.of(gamification));
        when(missionRepository.findByStudentIdAndMissionDate(eq(3L), any())).thenReturn(Collections.emptyList());
        when(achievementRepository.findByStudentId(3L)).thenReturn(Collections.emptyList());

        var summary = gamificationService.getSummary(jwt);

        assertThat(summary.xp()).isEqualTo(250);
        assertThat(summary.level()).isEqualTo(3); // 250 / 100 + 1
    }
}
